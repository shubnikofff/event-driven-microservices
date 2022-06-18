package com.shubnikofff.ordersservice.core.events;


import com.shubnikofff.core.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
public class OrderApprovedEvent {
	private final String orderId;
	private final OrderStatus orderStatus = OrderStatus.APPROVED;
}
