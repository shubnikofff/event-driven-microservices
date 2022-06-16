package com.shubnikofff.ordersservice.command.commands;

import com.shubnikofff.core.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Builder
public class CreateOrderCommand {

	@TargetAggregateIdentifier
	public final String orderId;
	private final String userId;
	private final String productId;
	private final int quantity;
	private final String addressId;
	private final OrderStatus orderStatus;
}
