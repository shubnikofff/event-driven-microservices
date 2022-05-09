package com.shubnikofff.productservice.command.rest;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class CreateProductRestModel {

//	@NotBlank(message = "Product title is required field")
	private final String title;

	@Min(value = 1, message = "Price cannot be lower than 1")
	private final BigDecimal price;

	@Min(value = 1, message = "Quantity cannot be lower than 1")
	@Max(value = 5, message = "Quantity cannot be larger than 5")
	private final Integer quantity;
}
