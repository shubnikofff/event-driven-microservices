package com.shubnikofff.productservice.query;

import com.shubnikofff.productservice.core.data.ProductEntity;
import com.shubnikofff.productservice.core.data.ProductsRepository;
import com.shubnikofff.productservice.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventsHandler {

	private final ProductsRepository productsRepository;

	@EventHandler
	public void on(ProductCreatedEvent event) {
		final var productEntity = new ProductEntity();
		BeanUtils.copyProperties(event, productEntity);

		productsRepository.save(productEntity);
	}
}
