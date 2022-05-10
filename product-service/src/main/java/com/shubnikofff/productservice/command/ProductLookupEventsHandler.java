package com.shubnikofff.productservice.command;

import com.shubnikofff.productservice.core.data.ProductLookupEntity;
import com.shubnikofff.productservice.core.data.ProductLookupRepository;
import com.shubnikofff.productservice.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {

	private final ProductLookupRepository productLookupRepository;

	@EventHandler
	public void on(ProductCreatedEvent event) {
		final var productLookupEntity = new ProductLookupEntity(event.getProductId(), event.getTitle());
		productLookupRepository.save(productLookupEntity);
	}
}
