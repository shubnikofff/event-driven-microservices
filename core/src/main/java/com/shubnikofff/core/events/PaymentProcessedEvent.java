package com.shubnikofff.core.events;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentProcessedEvent {
	private final String orderId;
	private final String paymentId;
}
