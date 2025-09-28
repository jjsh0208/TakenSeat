package com.taken_seat.payment_service.application.tossclient.dto;

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

}