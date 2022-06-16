package com.shubnikofff.ordersservice.command.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@RequiredArgsConstructor
@Getter
public class ApproveOrderCommand {

	@TargetAggregateIdentifier
	private final String orderId;
}
