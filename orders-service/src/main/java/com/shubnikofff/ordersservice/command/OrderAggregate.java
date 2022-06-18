package com.shubnikofff.ordersservice.command;

import com.shubnikofff.ordersservice.core.events.OrderApprovedEvent;
import com.shubnikofff.ordersservice.command.commands.ApproveOrderCommand;
import com.shubnikofff.ordersservice.command.commands.CreateOrderCommand;
import com.shubnikofff.core.model.OrderStatus;
import com.shubnikofff.ordersservice.command.commands.RejectOrderCommand;
import com.shubnikofff.ordersservice.core.events.OrderCreatedEvent;
import com.shubnikofff.ordersservice.core.events.OrderRejectedEvent;
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

	@CommandHandler
	public void handle(ApproveOrderCommand approveOrderCommand) {
		final var orderApprovedEvent = new OrderApprovedEvent(approveOrderCommand.getOrderId());
		AggregateLifecycle.apply(orderApprovedEvent);
	}

	@EventSourcingHandler
	protected void on(OrderApprovedEvent orderApprovedEvent) {
		orderStatus = orderApprovedEvent.getOrderStatus();
	}

	@CommandHandler
	public void handle(RejectOrderCommand rejectOrderCommand) {
		final var orderRejectedEvent = new OrderRejectedEvent(rejectOrderCommand.getOrderId(), rejectOrderCommand.getReason());
		AggregateLifecycle.apply(orderRejectedEvent);
	}

	@EventSourcingHandler
	public void on(OrderRejectedEvent orderRejectedEvent) {
		orderStatus = orderRejectedEvent.getOrderStatus();
	}
}
