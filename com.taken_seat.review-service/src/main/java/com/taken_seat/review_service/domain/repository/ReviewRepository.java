package com.taken_seat.review_service.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.projection.ReviewStatProjection;

@Repository
public interface ReviewRepository {

	Review save(Review review);

	Boolean existsByAuthorIdAndPerformanceIdAndDeletedAtIsNull(UUID authorId, UUID performanceId);

	Optional<Review> findByIdAndDeletedAtIsNull(UUID id);

	<S extends Review> List<S> saveAllAndFlush(Iterable<S> entities);

	ReviewStatProjection fetchAvgRatingAndReviewCountByPerformanceId(UUID performanceId);

	List<ReviewStatProjection> fetchAvgRatingAndReviewCountByPerformanceIds(List<UUID> performanceIds);
}
