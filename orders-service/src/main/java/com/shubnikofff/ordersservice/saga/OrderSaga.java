package com.shubnikofff.ordersservice.saga;

import com.shubnikofff.core.commands.ReserveProductCommand;
import com.shubnikofff.core.events.ProductReservedEvent;
import com.shubnikofff.ordersservice.core.events.OrderCreatedEvent;
import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
//@RequiredArgsConstructor
@Log4j2
public class OrderSaga {

	@Autowired
	private transient CommandGateway commandGateway;

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
	}
}
