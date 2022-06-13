package com.shubnikofff.ordersservice.saga;

import com.shubnikofff.core.commands.ProcessPaymentCommand;
import com.shubnikofff.core.commands.ReserveProductCommand;
import com.shubnikofff.core.events.PaymentProcessedEvent;
import com.shubnikofff.core.events.ProductReservedEvent;
import com.shubnikofff.core.model.User;
import com.shubnikofff.core.query.FetchUserPaymentDetailsQuery;
import com.shubnikofff.ordersservice.core.events.OrderCreatedEvent;
import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
//@RequiredArgsConstructor
@Log4j2
public class OrderSaga {

	@Autowired
	private transient CommandGateway commandGateway;

	@Autowired
	private transient QueryGateway queryGateway;

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
					log.error(commandResultMessage.optionalExceptionResult().get());
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
			// todo: start compensating transaction
			return;
		}

		if (userPaymentDetails == null) {
			// todo: start compensating transaction
			return;
		}

		log.info("Successfully fetched user payment details for user {}", userPaymentDetails.getFirstName());

		final var processPaymentCommand = ProcessPaymentCommand.builder()
				.orderId(productReservedEvent.getOrderId())
				.paymentId(UUID.randomUUID().toString())
				.build();

		String result = null;
		try {
			result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error(e.getMessage());
			// todo: start compensating transaction
		}

		if(result == null) {
			log.info("The ProcessPaymentCommand resulted in NULL. Initiating compensating transaction...");
			// todo: start compensating transaction
		}
	}

    @SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessedEvent event) {
		// todo: Send an ApproveOrderCommand
	}
}
