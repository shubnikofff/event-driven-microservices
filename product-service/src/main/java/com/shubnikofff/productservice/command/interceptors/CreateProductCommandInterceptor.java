package com.shubnikofff.productservice.command.interceptors;

import com.shubnikofff.productservice.command.CreateProductCommand;
import com.shubnikofff.productservice.core.data.ProductLookupRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

	private final ProductLookupRepository productLookupRepository;

	@Override
	public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> list) {
		return ((index, command) -> {

			LOGGER.info("Intercepted command: " + command.getPayloadType());

			if (CreateProductCommand.class.equals(command.getPayloadType())) {
				final var createProductCommand = (CreateProductCommand) command.getPayload();

				final var productLookupEntity = productLookupRepository.findByProductIdOrTitle(
						createProductCommand.getProductId(),
						createProductCommand.getTitle()
				);

				if (productLookupEntity != null) {
					throw new IllegalStateException(String.format(
							"Product with productId %s ot title %s already exists",
							createProductCommand.getProductId(),
							createProductCommand.getTitle()
					));
				}
			}

			return command;
		});
	}
}
