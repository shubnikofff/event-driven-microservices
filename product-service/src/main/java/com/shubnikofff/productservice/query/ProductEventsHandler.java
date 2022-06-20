package com.shubnikofff.productservice.query;

import com.shubnikofff.core.events.ProductReservationCancelledEvent;
import com.shubnikofff.core.events.ProductReservedEvent;
import com.shubnikofff.productservice.core.data.ProductEntity;
import com.shubnikofff.productservice.core.data.ProductsRepository;
import com.shubnikofff.productservice.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Log4j2
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
	public void on(ProductCreatedEvent event) {
		final var productEntity = new ProductEntity();
		BeanUtils.copyProperties(event, productEntity);

		try {
			productsRepository.save(productEntity);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void on(ProductReservedEvent productReservedEvent) {
		final var productEntity = productsRepository.findByProductId(productReservedEvent.getProductId());

		log.debug("ProductReservedEvent: Current product quantity: {}", productEntity.getQuantity());

		productEntity.setQuantity(productEntity.getQuantity() - productReservedEvent.getQuantity());
		productsRepository.save(productEntity);

		log.debug("ProductReservedEvent: New product quantity: {}", productEntity.getQuantity());

		log.info("ProductReservedEvent is called for productId {} and orderId {}", productReservedEvent.getProductId(), productReservedEvent.getOrderId());
	}

	@EventHandler
	public void  on(ProductReservationCancelledEvent productReservationCancelledEvent) {
		final var productEntity = productsRepository.findByProductId(productReservationCancelledEvent.getProductId());

		log.debug("ProductReservationCancelledEvent: Current product quantity: {}", productEntity.getQuantity());

		final var newQuantity = productEntity.getQuantity() + productReservationCancelledEvent.getQuantity();
		productEntity.setQuantity(newQuantity);
		productsRepository.save(productEntity);

		log.debug("ProductReservationCancelledEvent: New product quantity: {}", productEntity.getQuantity());
	}

	@ResetHandler
	public void reset() {
		productsRepository.deleteAll();
	}
}
