package com.shubniukofff.ordersservice.core.events;

import com.shubniukofff.ordersservice.core.enums.OrderStatus;
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
