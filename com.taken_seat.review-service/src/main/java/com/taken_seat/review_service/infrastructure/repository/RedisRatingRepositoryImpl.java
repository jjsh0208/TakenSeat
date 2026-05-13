package com.taken_seat.review_service.infrastructure.repository;

import java.math.BigDecimal;
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
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
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
	private final RedisSerializer<String> serializer = new StringRedisSerializer();

	private final String AVG_RATING_KEY = "avgRating:";
	private static final String FIELD_AVG_RATING = "avgRating";
	private static final String FIELD_REVIEW_COUNT = "reviewCount";

	@Override
	public Double getAvgRating(UUID performanceId) {
		log.info("[Review] 평균 평점 조회 시작, performanceId={}", performanceId);
		String avgRatingKey = AVG_RATING_KEY + performanceId;

		Map<Object, Object> ratingData = redisTemplate.opsForHash().entries(avgRatingKey);

		double avgRating = getOrDefaultRating(ratingData, FIELD_AVG_RATING);
		long reviewCount = getOrDefaultReviewCountFromObjectKey(ratingData, FIELD_REVIEW_COUNT);

		// 리뷰가 50개 미만이면 캐시(Redis)를 믿지 않고 무조건 DB를 다시 조회한다"라는 명확한 비즈니스 정책
		if (avgRating == 0.0 || reviewCount < 50) {
			log.info("[Review] 평점이 없거나 리뷰 수가 적음, DB에서 평점 및 리뷰 수 조회 시작, performanceId={}", performanceId);
			Map<String, Object> avgRatingAndCount = reviewRepository.fetchAvgRatingAndReviewCountByPerformanceId(
				performanceId);

			saveRating(performanceId, avgRatingAndCount);

			avgRating = bigDecimalToDouble(avgRatingAndCount.get(FIELD_AVG_RATING));
			log.info("[Review] DB에서 평균 평점 및 리뷰 수 조회 완료, avgRating={}, reviewCount={}", avgRating, reviewCount);
		}

		return avgRating;
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

						String avgRatingKey = AVG_RATING_KEY + stat.getPerformanceId();

						// String.valueOf()를 통해 String으로 변환하여 저장
						Map<String, String> redisMap = new HashMap<>();
						redisMap.put(avgRatingKey, String.valueOf(stat.getAvgRating()));
						redisMap.put(avgRatingKey, String.valueOf(stat.getReviewCount()));

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

	private double getOrDefaultRating(Map<Object, Object> ratingData, String field) {
		Object ratingObj = ratingData.get(field);
		return (ratingObj != null) ? (double)ratingObj : 0.0;
	}

	private long getOrDefaultReviewCountFromObjectKey(Map<Object, Object> ratingData, String field) {
		Object reviewCountObj = ratingData.get(field);

		if (reviewCountObj instanceof Long) {
			return (Long)reviewCountObj;
		} else if (reviewCountObj instanceof Integer) {
			return ((Integer)reviewCountObj).longValue();
		} else if (reviewCountObj instanceof String) {
			try {
				return Long.parseLong((String)reviewCountObj);
			} catch (NumberFormatException e) {
				log.warn("[Review] 문자열 리뷰 수 파싱 실패: {}", reviewCountObj);
			}
		}
		return 0L;
	}

	private double bigDecimalToDouble(Object value) {
		if (value instanceof BigDecimal) {
			return ((BigDecimal)value).doubleValue();
		}
		log.error("[Review] 잘못된 값 형식, value={}", value);
		throw new ReviewException(ResponseCode.ILLEGAL_ARGUMENT);
	}

	private void saveRating(UUID performanceId, Map<String, Object> avgRatingAndCount) {
		double avgRating = bigDecimalToDouble(avgRatingAndCount.get(FIELD_AVG_RATING));
		long reviewCount = (long)avgRatingAndCount.get(FIELD_REVIEW_COUNT);

		saveRatingToRedisWithTTL(performanceId, avgRating, reviewCount);
		log.info("[Review] Redis에 평점 및 리뷰 수 저장, performanceId={}, avgRating={}, reviewCount={}", performanceId,
			avgRating, reviewCount);
	}

	private void saveRatingToRedisWithTTL(UUID performanceId, double avgRating, long reviewCount) {
		String avgRatingKey = AVG_RATING_KEY + performanceId;

		Map<String, Object> ratingInfo = new HashMap<>();
		ratingInfo.put(FIELD_AVG_RATING, avgRating);
		ratingInfo.put(FIELD_REVIEW_COUNT, reviewCount);

		// 해시로 저장
		redisTemplate.opsForHash().putAll(avgRatingKey, ratingInfo);

		// TTL 설정: 1시간 30분
		redisTemplate.expire(avgRatingKey, Duration.ofHours(2));
		log.info("[Review] 평점 및 리뷰 수 Redis에 저장 완료, performanceId={}, avgRating={}, reviewCount={}", performanceId,
			avgRating, reviewCount);
	}
}
