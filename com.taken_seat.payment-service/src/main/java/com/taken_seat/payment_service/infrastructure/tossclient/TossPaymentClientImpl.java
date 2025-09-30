package com.taken_seat.payment_service.infrastructure.tossclient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.payment_service.application.tossclient.TossPaymentClient;
import com.taken_seat.payment_service.application.tossclient.dto.TossApiResponse;
import com.taken_seat.payment_service.application.tossclient.dto.TossCancelRequest;
import com.taken_seat.payment_service.application.tossclient.dto.TossConfirmResponse;
import com.taken_seat.payment_service.application.tossclient.dto.TossPaymentRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TossPaymentClientImpl implements TossPaymentClient {

	private static final String BASIC_AUTH_PREFIX = "Basic ";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String EMPTY_SECRET_KEY_SUFFIX = ":";

	private final RestClient restClient;

	public TossPaymentClientImpl(@Value("${toss.secret-key}") String secretKey) {
		String authHeader = createAuthHeader(secretKey);

		this.restClient = RestClient.builder()
			.baseUrl("https://api.tosspayments.com/v1")
			.defaultHeader(AUTHORIZATION_HEADER, authHeader)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@Override
	public TossConfirmResponse confirmPayment(TossPaymentRequest request, String idempotencyKey) {
		try {
			ResponseEntity<TossApiResponse> responseEntity = restClient.post()
				.uri("/payments/confirm")
				.header("Idempotency-Key", idempotencyKey)
				.body(request)
				.retrieve()
				.toEntity(TossApiResponse.class);

			TossApiResponse tossRes = responseEntity.getBody();

			return new TossConfirmResponse(
				tossRes.orderId(),
				tossRes.paymentKey(),
				tossRes.status(),
				tossRes.approvedAt(),
				tossRes.method(),
				tossRes.totalAmount(),
				tossRes.card(),
				idempotencyKey
			);
		} catch (HttpClientErrorException ex) {
			log.error("Toss 결제 클라이언트 오류: {}", ex.getResponseBodyAsString(), ex);
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT, "결제 승인 중 클라이언트 오류가 발생했습니다.");
		} catch (RestClientException ex) {
			log.error("Toss 결제 서버 통신 오류", ex);
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT, "결제 승인 요청 중 오류가 발생했습니다.");
		}
	}

	@Override
	public void cancelPayment(String paymentKey, String cancelReason, int cancelAmount, String idempotencyKey) {

		TossCancelRequest request = new TossCancelRequest(cancelAmount, cancelReason);

		try {
			restClient.post()
				.uri("/payments/{paymentKey}/cancel", paymentKey)
				.header("Idempotency-Key", idempotencyKey)
				.body(request)
				.retrieve()
				.toBodilessEntity();
			log.info("[Toss] 환불 성공 - paymentKey={}, amount={}", paymentKey, cancelAmount);

		} catch (HttpClientErrorException ex) {
			log.error("Toss 결제 클라이언트 오류: {}", ex.getResponseBodyAsString(), ex);
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT, "결제 승인 중 클라이언트 오류가 발생했습니다.");
		} catch (RestClientException ex) {
			log.error("Toss 결제 서버 통신 오류", ex);
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT, "결제 승인 요청 중 오류가 발생했습니다.");
		}
	}

	private String createAuthHeader(String secretKey) {
		String raw = secretKey + EMPTY_SECRET_KEY_SUFFIX; // secretKey 뒤에 ':'를 붙임
		String encodedKey = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
		return BASIC_AUTH_PREFIX + encodedKey;
	}
}


