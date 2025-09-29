package com.taken_seat.payment_service.infrastructure.tossclient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

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
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TossPaymentClientImpl implements TossPaymentClient {

	private static final String BASIC_AUTH_PREFIX = "Basic ";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String EMPTY_SECRET_KEY_SUFFIX = ":";

	private final RestClient restClient;
	private final PaymentRepository paymentRepository;

	public TossPaymentClientImpl(@Value("${toss.secret-key}") String secretKey, PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
		String authHeader = createAuthHeader(secretKey);

		this.restClient = RestClient.builder()
			.baseUrl("https://api.tosspayments.com/v1")
			.defaultHeader(AUTHORIZATION_HEADER, authHeader)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@Override
	public TossConfirmResponse confirmPayment(TossPaymentRequest request) {

		// 여기서 사용하는 orderId는 내 Payment에서 사용되는 bookingId이다.
		Optional<Payment> paymentOpt = paymentRepository.findByBookingIdAndDeletedAtIsNull(
			UUID.fromString(request.orderId()));

		// 1. filter와 map을 이용해 "이미 처리된 결제 응답" Optional을 생성
		Optional<TossConfirmResponse> existingResponseOpt = paymentOpt
			.filter(payment -> payment.getIdempotencyKey() != null) // idempotencyKey가 null이 아닌 경우만 통과
			.map(TossConfirmResponse::from); // 통과한 Payment를 TossConfirmResponse로 변환

		// 2. "이미 처리된 결제 응답"이 존재하면 바로 반환합니다.
		if (existingResponseOpt.isPresent()) {
			log.info("이미 처리된 결제입니다. orderId={}", request.orderId());
			return existingResponseOpt.get();
		}

		// 3. 존재하지 않으면, 새로운 결제 로직을 진행
		// orderId 와 랜덤 UUID를 생성해 멱등성키를 만들어준다.
		String idempotencyKey = request.orderId() + "_" + UUID.randomUUID();

		try {
			ResponseEntity<TossApiResponse> responseEntity = restClient.post()
				.uri("/payments/confirm")
				.body(request)
				.header("Idempotency-Key", idempotencyKey)
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
	public void refund(String paymentKey, Integer cancelAmount, String cancelReason) {

		TossCancelRequest request = new TossCancelRequest(cancelAmount, cancelReason);

		try {
			restClient.post()
				.uri("/payments/{paymentKey}/cancel", paymentKey)
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


