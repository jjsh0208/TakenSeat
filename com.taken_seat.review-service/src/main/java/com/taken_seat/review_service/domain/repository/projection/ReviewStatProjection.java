package com.taken_seat.review_service.domain.repository.projection;

import java.util.UUID;

public interface ReviewStatProjection {
	UUID getPerformanceId();

	Double getAvgRating();

	Long getReviewCount();
}
