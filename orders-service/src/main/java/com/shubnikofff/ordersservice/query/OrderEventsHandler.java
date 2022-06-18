package com.shubnikofff.ordersservice.query;

import com.shubnikofff.ordersservice.core.events.OrderApprovedEvent;
import com.shubnikofff.ordersservice.core.data.OrderEntity;
import com.shubnikofff.ordersservice.core.data.OrdersRepository;
import com.shubnikofff.ordersservice.core.events.OrderCreatedEvent;
import com.shubnikofff.ordersservice.core.events.OrderRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("order-group")
public class OrderEventsHandler {

	private final OrdersRepository ordersRepository;

	@EventHandler
	public void on(OrderCreatedEvent orderCreatedEvent) {
		final var orderEntity = new OrderEntity();
		BeanUtils.copyProperties(orderCreatedEvent, orderEntity);
		ordersRepository.save(orderEntity);
	}

	@EventHandler
	public void on(OrderApprovedEvent orderApprovedEvent) {
		final var orderEntity = ordersRepository.findByOrderId(orderApprovedEvent.getOrderId());

		if(orderEntity == null) {
			// todo: Do something about it
			return;
		}

		orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
		ordersRepository.save(orderEntity);
	}

	@EventHandler
	public void on(OrderRejectedEvent orderRejectedEvent) {
		final var orderEntity = ordersRepository.findByOrderId(orderRejectedEvent.getOrderId());
		orderEntity.setOrderStatus(orderRejectedEvent.getOrderStatus());
		ordersRepository.save(orderEntity);
	}
}
