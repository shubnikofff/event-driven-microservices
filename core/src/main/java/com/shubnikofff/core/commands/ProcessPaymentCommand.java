package com.shubnikofff.core.commands;

import com.shubnikofff.core.model.PaymentDetails;
import lombok.Builder;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Getter
public class ProcessPaymentCommand {

	@TargetAggregateIdentifier
	private final String paymentId;
	private final String orderId;
	private final PaymentDetails paymentDetails;
}
