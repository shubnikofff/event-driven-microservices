package com.shubnikofff.productservice.command;

import com.shubnikofff.core.commands.CancelProductReservationCommand;
import com.shubnikofff.core.commands.ReserveProductCommand;
import com.shubnikofff.core.events.ProductReservationCancelledEvent;
import com.shubnikofff.core.events.ProductReservedEvent;
import com.shubnikofff.productservice.core.events.ProductCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate
public class ProductAggregate {

	@AggregateIdentifier
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;

	public ProductAggregate() {
	}

	@CommandHandler
	public ProductAggregate(CreateProductCommand createProductCommand) {
		if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Price cannot be less or equal than zero");
		}

		if (createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()) {
			throw new IllegalArgumentException("Title cannot be empty");
		}

		final var productCreatedEvent = new ProductCreatedEvent();

		BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

		AggregateLifecycle.apply(productCreatedEvent);
	}

	@CommandHandler
	public void handle(ReserveProductCommand reserveProductCommand) {
		if (quantity < reserveProductCommand.getQuantity()) {
			throw new IllegalArgumentException("Insufficient number of items in stock.");
		}

		final var productReservedEvent = ProductReservedEvent.builder()
				.orderId(reserveProductCommand.getOrderId())
				.productId(reserveProductCommand.getProductId())
				.quantity(reserveProductCommand.getQuantity())
				.userId(reserveProductCommand.getUserId())
				.build();

		AggregateLifecycle.apply(productReservedEvent);
	}

	@CommandHandler
	public void handle(CancelProductReservationCommand cancelProductReservationCommand) {
		final var productReservationCancelledEvent = ProductReservationCancelledEvent.builder()
				.orderId(cancelProductReservationCommand.getOrderId())
				.productId(cancelProductReservationCommand.getProductId())
				.userId(cancelProductReservationCommand.getUserId())
				.quantity(cancelProductReservationCommand.getQuantity())
				.reason(cancelProductReservationCommand.getReason())
				.build();

		AggregateLifecycle.apply(productReservationCancelledEvent);
	}

	@EventSourcingHandler
	public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
		quantity += productReservationCancelledEvent.getQuantity();
	}

	@EventSourcingHandler
	public void  on(ProductCreatedEvent productCreatedEvent) {
		productId = productCreatedEvent.getProductId();
		title = productCreatedEvent.getTitle();
		price = productCreatedEvent.getPrice();
		quantity = productCreatedEvent.getQuantity();
	}

	@EventSourcingHandler
	public void on(ProductReservedEvent productReservedEvent) {
		this.quantity -= productReservedEvent.getQuantity();
	}
}
