package com.taken_seat.payment_service.application.tossclient;

import com.taken_seat.payment_service.application.tossclient.dto.TossConfirmResponse;
import com.taken_seat.payment_service.application.tossclient.dto.TossPaymentRequest;
import com.taken_seat.payment_service.domain.model.Payment;

public interface TossPaymentClient {
	TossConfirmResponse confirmPayment(TossPaymentRequest request, String idempotencyKey);

	void refund(Payment payment, String cancelReason);
}
