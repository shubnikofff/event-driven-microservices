package com.shubnikofff.ordersservice.command;

import com.shubnikofff.ordersservice.core.enums.OrderStatus;
import com.shubnikofff.ordersservice.core.events.OrderCreatedEvent;
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
	public void on(OrderCreatedEvent orderCreatedEvent) {
		orderId = orderCreatedEvent.getOrderId();
		productId = orderCreatedEvent.getProductId();
		userId = orderCreatedEvent.getUserId();
		quantity = orderCreatedEvent.getQuantity();
		addressId = orderCreatedEvent.getAddressId();
		orderStatus = orderCreatedEvent.getOrderStatus();
	}
}
