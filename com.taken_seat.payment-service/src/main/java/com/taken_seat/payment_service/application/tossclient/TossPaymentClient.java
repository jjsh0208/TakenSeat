package com.taken_seat.payment_service.application.tossclient;

import com.taken_seat.payment_service.application.tossclient.dto.TossConfirmResponse;
import com.taken_seat.payment_service.application.tossclient.dto.TossPaymentRequest;

public interface TossPaymentClient {
	TossConfirmResponse confirmPayment(TossPaymentRequest request, String idempotencyKey);

	void cancelPayment(String paymentKey, String cancelReason, int cancelAmount, String idempotencyKey);
}
