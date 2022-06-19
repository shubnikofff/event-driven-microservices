package com.shubnikofff.ordersservice.query;

import com.shubnikofff.ordersservice.core.data.OrdersRepository;
import com.shubnikofff.ordersservice.core.model.OrderSummary;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderQueriesHandler {

	private final OrdersRepository ordersRepository;

	@QueryHandler
	public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
		final var orderEntity = ordersRepository.findByOrderId(findOrderQuery.getOrderId());
		return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
	}
}
