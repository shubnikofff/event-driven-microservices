package com.shubnikofff.ordersservice.saga;

import com.shubnikofff.core.commands.CancelProductReservationCommand;
import com.shubnikofff.core.commands.ProcessPaymentCommand;
import com.shubnikofff.core.commands.ReserveProductCommand;
import com.shubnikofff.ordersservice.core.events.OrderApprovedEvent;
import com.shubnikofff.core.events.PaymentProcessedEvent;
import com.shubnikofff.core.events.ProductReservationCancelledEvent;
import com.shubnikofff.core.events.ProductReservedEvent;
import com.shubnikofff.core.model.User;
import com.shubnikofff.core.query.FetchUserPaymentDetailsQuery;
import com.shubnikofff.ordersservice.command.commands.ApproveOrderCommand;
import com.shubnikofff.ordersservice.command.commands.RejectOrderCommand;
import com.shubnikofff.ordersservice.core.events.OrderCreatedEvent;
import com.shubnikofff.ordersservice.core.events.OrderRejectedEvent;
import com.shubnikofff.ordersservice.core.model.OrderSummary;
import com.shubnikofff.ordersservice.query.FindOrderQuery;
import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Saga
//@RequiredArgsConstructor
@Log4j2
public class OrderSaga {

	public static final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";

	private String scheduleId;

	@Autowired
	private transient CommandGateway commandGateway;

	@Autowired
	private transient QueryGateway queryGateway;

	@Autowired
	private transient DeadlineManager deadlineManager;

	@Autowired
	private transient QueryUpdateEmitter queryUpdateEmitter;

	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent orderCreatedEvent) {
		final var reserveProductCommand = ReserveProductCommand.builder()
				.orderId(orderCreatedEvent.getOrderId())
				.productId(orderCreatedEvent.getProductId())
				.quantity(orderCreatedEvent.getQuantity())
				.userId(orderCreatedEvent.getUserId())
				.build();

		log.info("OrderCreatedEvent handled for orderId {} and productId {}", reserveProductCommand.getOrderId(), reserveProductCommand.getProductId());

		commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage, CommandResultMessage<?> commandResultMessage) {
				if (commandResultMessage.isExceptional()) {
					final var rejectOrderCommand = new RejectOrderCommand(orderCreatedEvent.getOrderId(), commandResultMessage.exceptionResult().getMessage());
					commandGateway.send(rejectOrderCommand);
				}
			}
		});
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservedEvent productReservedEvent) {
		// process user payment
		log.info("ProductReservedEvent is called for productId {} and orderId {}", productReservedEvent.getProductId(), productReservedEvent.getOrderId());

		final var query = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

		User userPaymentDetails = null;
		try {
			userPaymentDetails = queryGateway.query(query, ResponseTypes.instanceOf(User.class)).join();
		} catch (Exception e) {
			log.error(e);
			cancelProductReservation(productReservedEvent, e.getMessage());
			return;
		}

		if (userPaymentDetails == null) {
			cancelProductReservation(productReservedEvent, "Could not fetch user payment details");
			return;
		}

		log.info("Successfully fetched user payment details for user {}", userPaymentDetails.getFirstName());

		scheduleId = deadlineManager.schedule(
				Duration.of(120, ChronoUnit.SECONDS),
				PAYMENT_PROCESSING_TIMEOUT_DEADLINE,
				productReservedEvent
		);

		final var processPaymentCommand = ProcessPaymentCommand.builder()
				.orderId(productReservedEvent.getOrderId())
				.paymentId(UUID.randomUUID().toString())
				.paymentDetails(userPaymentDetails.getPaymentDetails())
				.build();

		String result = null;
		try {
			result = commandGateway.sendAndWait(processPaymentCommand);
		} catch (Exception e) {
			log.error(e.getMessage());
			cancelProductReservation(productReservedEvent, e.getMessage());
			return;
		}

		if (result == null) {
			log.info("The ProcessPaymentCommand resulted in NULL. Initiating compensating transaction...");
			cancelProductReservation(productReservedEvent, "Could not process user payment with provided payment details");
		}
	}

	private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
		cancelPaymentProcessingDeadline();

		final var cancelProductReservationCommand = CancelProductReservationCommand.builder()
				.orderId(productReservedEvent.getOrderId())
				.userId(productReservedEvent.getUserId())
				.productId(productReservedEvent.getProductId())
				.quantity(productReservedEvent.getQuantity())
				.reason(reason)
				.build();

		commandGateway.send(cancelProductReservationCommand);
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessedEvent event) {
		cancelPaymentProcessingDeadline();
		final var approveOrderCommand = new ApproveOrderCommand(event.getOrderId());
		commandGateway.send(approveOrderCommand);
	}

	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent orderApprovedEvent) {
		log.info("Order is approved. Order Saga complete for orderId: {}", orderApprovedEvent.getOrderId());
//		SagaLifecycle.end();
		queryUpdateEmitter.emit(
				FindOrderQuery.class,
				query -> true,
				new OrderSummary(orderApprovedEvent.getOrderId(), orderApprovedEvent.getOrderStatus(), "")
		);
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
		final var rejectOrderCommand = new RejectOrderCommand(productReservationCancelledEvent.getOrderId(), productReservationCancelledEvent.getReason());
		commandGateway.send(rejectOrderCommand);
	}

	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderRejectedEvent orderRejectedEvent) {
		log.info("Successfully rejected order with id {}", orderRejectedEvent.getOrderId());

		queryUpdateEmitter.emit(
				FindOrderQuery.class,
				query -> true,
				new OrderSummary(orderRejectedEvent.getOrderId(), orderRejectedEvent.getOrderStatus(), orderRejectedEvent.getReason())
		);
	}

	@DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
	public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
		log.info("Payment processing deadline took place. Sending compensating command to cancel to cancel product reservation.");
		cancelProductReservation(productReservedEvent, "Payment timeout");
	}

	private void cancelPaymentProcessingDeadline() {
		if (scheduleId != null) {
			deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
			scheduleId = null;
		}
	}
}
