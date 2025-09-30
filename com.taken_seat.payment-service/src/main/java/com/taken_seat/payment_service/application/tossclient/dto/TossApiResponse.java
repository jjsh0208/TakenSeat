package com.taken_seat.payment_service.application.tossclient.dto;

public record TossApiResponse(
	String orderId,
	String paymentKey,
	String status,
	String approvedAt,
	String method,
	int totalAmount,
	CardInfo card
) {

}
