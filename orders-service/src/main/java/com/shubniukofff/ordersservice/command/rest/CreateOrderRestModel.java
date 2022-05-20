package com.shubniukofff.ordersservice.command.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class CreateOrderRestModel {
//	@NotBlank
	private final UUID productId;
	@Min(1)
	private final int quantity;
//	@NotBlank
	private final UUID addressId;
}
