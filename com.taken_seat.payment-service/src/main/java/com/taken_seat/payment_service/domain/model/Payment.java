package com.taken_seat.payment_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.payment_service.application.dto.service.PaymentDto;
import com.taken_seat.payment_service.application.tossclient.dto.TossConfirmResponse;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_payment")
public class Payment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false)
	private UUID bookingId;

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private Integer amount;

	@Column(nullable = false)
	private String orderName;

	@Column(unique = true)
	private String paymentKey;

	@Column(unique = true)
	private String idempotencyKey;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	private LocalDateTime approvedAt;

	private Integer refundAmount;

	private LocalDateTime refundRequestedAt;

	public static Payment register(PaymentDto dto) {
		Payment payment = Payment.builder()
			.bookingId(dto.getBookingId())
			.userId(dto.getUserId())
			.amount(dto.getAmount())
			.orderName(dto.getOrderName())
			.paymentStatus(PaymentStatus.PENDING)
			.build();

		payment.prePersist(dto.getUserId());

		return payment;
	}

	public static Payment register(PaymentMessage message) {
		Payment payment = Payment.builder()
			.bookingId(message.getBookingId())
			.userId(message.getUserId())
			.amount(message.getAmount())
			.orderName(message.getOrderName())
			.paymentStatus(PaymentStatus.PENDING)
			.build();

		payment.prePersist(message.getUserId());

		return payment;
	}

	public void update(PaymentDto dto) {
		this.amount = dto.getAmount();
		this.paymentStatus = dto.getPaymentStatus();
		this.preUpdate(dto.getUserId());
	}

	@Override
	public void delete(UUID deleteBy) {
		super.delete(deleteBy);
		this.paymentStatus = PaymentStatus.DELETED;
	}

	public void verifyRefundable() {
		if (this.paymentStatus != PaymentStatus.COMPLETED) {
			throw new PaymentException(ResponseCode.CANNOT_REFUND, "환불이 불가능한 결제 상태입니다.");
		}
	}

	/**
	 * 환불 처리를 완료하고 상태를 변경합니다.
	 */
	public void completeRefund(PaymentRefundMessage message) {
		this.paymentStatus = PaymentStatus.REFUNDED;
		this.refundAmount = message.getAmount(); // 메시지로부터 환불 금액을 받음
		this.refundRequestedAt = LocalDateTime.now();
		this.preUpdate(message.getUserId()); // 변경자 정보 업데이트
	}

	public void updateSuccessInfo(TossConfirmResponse response) {

		this.paymentStatus = PaymentStatus.COMPLETED;
		this.paymentKey = response.paymentKey();
		this.amount = response.totalAmount();
		this.approvedAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.idempotencyKey = response.idempotencyKey();
	}
}
