package com.shubnikofff.ordersservice;

import com.shubnikofff.ordersservice.configuration.AppConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@Import({AppConfiguration.class})
public class OrdersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersServiceApplication.class, args);
	}

}
