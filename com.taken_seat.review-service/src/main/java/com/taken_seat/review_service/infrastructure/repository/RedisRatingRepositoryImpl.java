package com.taken_seat.review_service.infrastructure.repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

import com.taken_seat.review_service.application.service.ReviewChangeMaker;
import com.taken_seat.review_service.domain.repository.RedisRatingRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;
import com.taken_seat.review_service.domain.repository.projection.ReviewStatProjection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RedisRatingRepositoryImpl implements RedisRatingRepository {

	private final ReviewRepository reviewRepository;
	private final ReviewChangeMaker reviewChangeMaker;
	private final RedisTemplate<String, Object> redisTemplate;

	private final String AVG_RATING_KEY = "avgRating:";
	private static final String FIELD_AVG_RATING = "avgRating";
	private static final String FIELD_REVIEW_COUNT = "reviewCount";

	@Override
	public Double getAvgRating(UUID performanceId) {
		log.info("[Review] 평균 평점 조회 시작, performanceId={}", performanceId);
		String avgRatingKey = AVG_RATING_KEY + performanceId;

		//1 redis에서 가져옴
		Map<Object, Object> ratingData = redisTemplate.opsForHash().entries(avgRatingKey);

		// 2. 팩토리 메서드를 통해 안전하게 캐스팅된 객체 획득
		RedisReviewStat stat = RedisReviewStat.fromRedisMap(ratingData);

		// 3. 비즈니스 정책: 리뷰가 50개 미만이면 무조건 DB 다시 조회
		if (stat.avgRating() == 0.0 || stat.reviewCount() < 50) {
			log.info("[Review] 평점이 없거나 리뷰 수가 적음, DB에서 조회 시작, performanceId={}", performanceId);

			// Map이 아닌 Projection 객체로 깔끔하게 받아옴
			ReviewStatProjection dbStat = reviewRepository.fetchAvgRatingAndReviewCountByPerformanceId(performanceId);

			// Projection의 Getter를 이용해 값을 바로 저장
			saveRatingToRedisWithTTL(performanceId, dbStat.getAvgRating(), dbStat.getReviewCount());

			log.info("[Review] DB에서 평균 평점 및 리뷰 수 조회 완료, avgRating={}, reviewCount={}", dbStat.getAvgRating(),
				dbStat.getReviewCount());

			// 객체의 Getter를 바로 반환
			return dbStat.getAvgRating();
		}

		// 캐시된 평점 반환
		return stat.avgRating();
	}

	@Override
	public void setAvgRatingForChangedPerformances() {

		List<UUID> performanceIds = reviewChangeMaker.getChangedPerformanceIds();

		if (performanceIds.isEmpty()) {
			log.info("[Review] 변경된 공연 없음, 리뷰 평점 갱신 생략");
			return;
		}

		List<ReviewStatProjection> avgRatingStats =
			reviewRepository.fetchAvgRatingAndReviewCountByPerformanceIds(performanceIds);

		if (avgRatingStats.isEmpty()) {
			log.info("[Review] 공연 ID에 대한 리뷰 통계 없음. 리뷰 평점 갱신 생략");
		}

		int batchSize = 1000;
		int totalRecords = avgRatingStats.size();

		for (int i = 0; i < totalRecords; i += batchSize) {
			int start = i;
			int end = Math.min(start + batchSize, totalRecords);

			List<ReviewStatProjection> batchList = avgRatingStats.subList(start, end);
			log.info("[Review] Redis Pipeline 처리 시작 (start = {}, end = {})", start, end);

			redisTemplate.executePipelined(new SessionCallback<Object>() {

				@Override
				public @Nullable <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {

					RedisOperations<String, Object> stringOps = (RedisOperations<String, Object>)operations;

					for (ReviewStatProjection stat : batchList) {

						String avgRatingKey = AVG_RATING_KEY + stat.getPerformanceId().toString();

						// String.valueOf()를 통해 String으로 변환하여 저장
						Map<String, String> redisMap = new HashMap<>();
						redisMap.put(FIELD_AVG_RATING, String.valueOf(stat.getAvgRating()));
						redisMap.put(FIELD_REVIEW_COUNT, String.valueOf(stat.getReviewCount()));

						// 명령어를 큐에 쌓음
						stringOps.opsForHash().putAll(avgRatingKey, redisMap);
						stringOps.expire(avgRatingKey, Duration.ofHours(2));
					}
					// 파이프라인 모드에서는 여기서의 반환값이 의미가 없으므로 null 반환
					return null;
				}
			});

		}
		reviewChangeMaker.clearChangedPerformanceIds();
		log.info("[Review] Redis Pipeline 처리 완료");
	}

	private void saveRatingToRedisWithTTL(UUID performanceId, double avgRating, long reviewCount) {
		String avgRatingKey = AVG_RATING_KEY + performanceId;

		Map<String, Object> ratingInfo = new HashMap<>();
		ratingInfo.put(FIELD_AVG_RATING, String.valueOf(avgRating));
		ratingInfo.put(FIELD_REVIEW_COUNT, String.valueOf(reviewCount));

		// 해시로 저장
		redisTemplate.opsForHash().putAll(avgRatingKey, ratingInfo);
		// TTL 설정: 1시간 30분
		redisTemplate.expire(avgRatingKey, Duration.ofHours(2));
		log.info("[Review] 평점 및 리뷰 수 Redis에 저장 완료, performanceId={}, avgRating={}, reviewCount={}", performanceId,
			avgRating, reviewCount);
	}

	public record RedisReviewStat(double avgRating, long reviewCount) {
		public static RedisReviewStat fromRedisMap(Map<Object, Object> map) {
			if (map == null || map.isEmpty()) {
				return new RedisReviewStat(0.0, 0L);
			}

			double rating = parseDouble(map.get("avgRating"));
			long count = parseLong(map.get("reviewCount"));

			return new RedisReviewStat(rating, count);
		}

		private static double parseDouble(Object obj) {
			return obj != null ? Double.parseDouble(String.valueOf(obj)) : 0.0;
		}

		private static long parseLong(Object obj) {
			return obj != null ? Long.parseLong(String.valueOf(obj)) : 0L;
		}
	}
}