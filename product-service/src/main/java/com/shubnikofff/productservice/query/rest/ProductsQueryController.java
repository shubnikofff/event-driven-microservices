package com.shubnikofff.productservice.query.rest;

import com.shubnikofff.productservice.query.FindProductsQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsQueryController {

	private final QueryGateway queryGateway;

	@GetMapping
	public List<ProductRestModel> getProducts() {
		final var query = new FindProductsQuery();

		final List<ProductRestModel> products = queryGateway
				.query(query, ResponseTypes.multipleInstancesOf(ProductRestModel.class))
				.join();

		return products;
	}

}
