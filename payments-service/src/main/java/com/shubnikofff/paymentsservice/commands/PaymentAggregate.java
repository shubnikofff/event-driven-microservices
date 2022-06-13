package com.shubnikofff.paymentsservice.commands;

import com.shubnikofff.core.commands.ProcessPaymentCommand;
import com.shubnikofff.core.events.PaymentProcessedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Aggregate
public class PaymentAggregate {

	@AggregateIdentifier
	private String paymentId;
	private String orderId;

	public PaymentAggregate() {
	}

	@CommandHandler
	public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
		if (processPaymentCommand.getPaymentDetails().getValidUntilYear() < LocalDate.now().getYear()) {
			throw new IllegalArgumentException("Credit Card expired");
		}

		final var paymentProcessedEvent = PaymentProcessedEvent.builder()
				.paymentId(processPaymentCommand.getPaymentId())
				.orderId(processPaymentCommand.getOrderId())
				.build();

		AggregateLifecycle.apply(paymentProcessedEvent);
	}

	@EventSourcingHandler
	public void on(PaymentProcessedEvent paymentProcessedEvent) {
		paymentId = paymentProcessedEvent.getPaymentId();
		orderId = paymentProcessedEvent.getOrderId();
	}
}
