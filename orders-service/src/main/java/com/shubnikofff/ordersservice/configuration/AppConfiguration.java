package com.shubnikofff.ordersservice.configuration;

import com.thoughtworks.xstream.XStream;
import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class AppConfiguration {

	@Bean
	public XStream xStream() {
		final var xStream = new XStream();
		xStream.allowTypesByWildcard(new String[]{"com.shubnikofff.**"});
		return xStream;
	}

	@Bean
	public DeadlineManager deadlineManager(Configuration configuration, SpringTransactionManager transactionManager) {
		return SimpleDeadlineManager.builder()
				.scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
				.transactionManager(transactionManager)
				.build();
	}
}
