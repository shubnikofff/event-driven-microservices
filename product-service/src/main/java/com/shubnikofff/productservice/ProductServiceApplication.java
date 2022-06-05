package com.shubnikofff.productservice;

import com.shubnikofff.productservice.command.interceptors.CreateProductCommandInterceptor;
import com.shubnikofff.productservice.configuration.AxonConfiguration;
import com.shubnikofff.productservice.core.errorhandling.ProductsServiceEventErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@EnableDiscoveryClient
@SpringBootApplication
@Import({AxonConfiguration.class})
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

	@Autowired
	public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus commandBus) {
		commandBus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));
	}

	@Autowired
	public void configure(EventProcessingConfigurer configurer) {
		configurer.registerListenerInvocationErrorHandler(
				"product-group",
				configuration -> new ProductsServiceEventErrorHandler()
		);

//		configurer.registerListenerInvocationErrorHandler(
//				"product-group",
//				configuration -> PropagatingErrorHandler.instance()
//		);
	}
}
