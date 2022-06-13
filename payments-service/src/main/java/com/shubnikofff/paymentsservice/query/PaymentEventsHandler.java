package com.shubnikofff.paymentsservice.query;

import com.shubnikofff.core.events.PaymentProcessedEvent;
import com.shubnikofff.paymentsservice.core.data.PaymentEntity;
import com.shubnikofff.paymentsservice.core.data.PaymentsRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventsHandler {

	private final PaymentsRepository paymentsRepository;

	@EventHandler
	public void on(PaymentProcessedEvent event) {
		final var paymentEntity = new PaymentEntity(event.getPaymentId(), event.getOrderId());
		paymentsRepository.save(paymentEntity);
	}
}
