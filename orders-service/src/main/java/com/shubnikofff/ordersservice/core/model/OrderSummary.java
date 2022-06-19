package com.shubnikofff.ordersservice.core.model;

import com.shubnikofff.core.model.OrderStatus;
import lombok.Value;

@Value
public class OrderSummary {

	private final String orderId;
	private final OrderStatus orderStatus;
	private final String message;
}
