package com.shubniukofff.ordersservice.command;

import com.shubniukofff.ordersservice.core.enums.OrderStatus;
import com.shubniukofff.ordersservice.core.events.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {
	@AggregateIdentifier
	private String orderId;
	private String productId;
	private String userId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;

	public OrderAggregate() {}

	@CommandHandler
	public OrderAggregate(CreateOrderCommand createOrderCommand) {
		if(createOrderCommand.getQuantity() < 1) {
			throw new IllegalArgumentException("Quantity should be more than zero");
		}

		final var orderCreatedEvent = new OrderCreatedEvent();
		BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
		AggregateLifecycle.apply(orderCreatedEvent);
	}

	@EventSourcingHandler
	public void on(CreateOrderCommand createOrderCommand) {
		orderId = createOrderCommand.getOrderId();
		productId = createOrderCommand.getProductId();
		userId = createOrderCommand.getUserId();
		quantity = createOrderCommand.getQuantity();
		addressId = createOrderCommand.getAddressId();
		orderStatus = createOrderCommand.getOrderStatus();
	}
}
