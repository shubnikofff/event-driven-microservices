package com.shubnikofff.ordersservice.command.rest;

import com.shubnikofff.core.model.OrderStatus;
import com.shubnikofff.ordersservice.command.commands.CreateOrderCommand;
import com.shubnikofff.ordersservice.core.model.OrderSummary;
import com.shubnikofff.ordersservice.query.FindOrderQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersCommandController {

	private final CommandGateway commandGateway;

	private final QueryGateway queryGateway;

	@PostMapping
	public OrderSummary createOrder(@Valid @RequestBody CreateOrderRestModel requestBody) {
		final var orderId = UUID.randomUUID().toString();

		final var createOrderCommand = CreateOrderCommand.builder()
				.orderId(orderId)
				.userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
				.productId(requestBody.getProductId())
				.quantity(requestBody.getQuantity())
				.addressId(requestBody.getAddressId())
				.orderStatus(OrderStatus.CREATED)
				.build();

		final var queryResult = queryGateway.subscriptionQuery(
				new FindOrderQuery(orderId),
				ResponseTypes.instanceOf(OrderSummary.class),
				ResponseTypes.instanceOf(OrderSummary.class)
		);

		try {
			commandGateway.sendAndWait(createOrderCommand);
			return queryResult.updates().blockFirst();
		} finally {
			queryResult.close();
		}
	}
}
