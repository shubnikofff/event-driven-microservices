package com.shubnikofff.ordersservice.query;

import lombok.Value;

@Value
public class FindOrderQuery {

	private final String orderId;
}
