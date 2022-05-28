package com.shubniukofff.ordersservice.command.rest;

import com.shubniukofff.ordersservice.command.CreateOrderCommand;
import com.shubniukofff.ordersservice.core.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

	private final CommandGateway commandGateway;

	@PostMapping
	public String createOrder(@Valid @RequestBody CreateOrderRestModel requestBody) {
		final var createOrderCommand = CreateOrderCommand.builder()
				.orderId(UUID.randomUUID().toString())
				.userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
				.productId(requestBody.getProductId())
				.quantity(requestBody.getQuantity())
				.addressId(requestBody.getAddressId())
				.orderStatus(OrderStatus.CREATED)
				.build();

		return commandGateway.sendAndWait(createOrderCommand);
	}
}
