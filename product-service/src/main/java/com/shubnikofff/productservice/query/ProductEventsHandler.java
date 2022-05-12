package com.shubnikofff.productservice.query;

import com.shubnikofff.productservice.core.data.ProductEntity;
import com.shubnikofff.productservice.core.data.ProductsRepository;
import com.shubnikofff.productservice.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("product-group")
public class ProductEventsHandler {

	private final ProductsRepository productsRepository;

	@ExceptionHandler(resultType = Exception.class)
	public void handle(Exception exception) throws Exception {
		throw exception;
	}

	@ExceptionHandler(resultType = IllegalArgumentException.class)
	public void handle(IllegalArgumentException exception) {

	}

	@EventHandler
	public void on(ProductCreatedEvent event) throws Exception {
		final var productEntity = new ProductEntity();
		BeanUtils.copyProperties(event, productEntity);

		try {
			productsRepository.save(productEntity);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		if(true) throw new Exception("Forcing exception in the Event Handler class");
	}
}
