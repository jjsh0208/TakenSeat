package com.taken_seat.payment_service.application.tossclient.dto;

import java.time.format.DateTimeFormatter;

import com.taken_seat.payment_service.domain.model.Payment;

public record TossConfirmResponse(
	String orderId,
	String paymentKey,
	String status,
	String approvedAt,
	String method,
	int totalAmount,
	CardInfo card,
	String idempotencyKey // 멱등키 필드 추가
) {

	public static TossConfirmResponse from(Payment payment) {
		// Payment 엔티티의 필드를 사용하여 TossConfirmResponse 레코드를 생성
		return new TossConfirmResponse(
			payment.getBookingId().toString(), // UUID -> String
			payment.getPaymentKey(),
			payment.getPaymentStatus().name(), // Enum -> String
			payment.getApprovedAt() != null ? payment.getApprovedAt().format(DateTimeFormatter.ISO_DATE_TIME) : null,
			null,
			payment.getAmount(),
			null,
			payment.getIdempotencyKey()
		);
	}
}