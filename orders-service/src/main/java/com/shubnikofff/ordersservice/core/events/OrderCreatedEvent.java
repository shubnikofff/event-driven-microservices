package com.shubnikofff.ordersservice.core.events;

import com.shubnikofff.core.model.OrderStatus;
import lombok.Data;

@Data
public class OrderCreatedEvent {
	private String orderId;
	private String productId;
	private String userId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;
}
