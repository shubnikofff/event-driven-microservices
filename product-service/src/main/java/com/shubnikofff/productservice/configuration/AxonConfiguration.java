package com.shubnikofff.productservice.configuration;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfiguration {

	@Bean
	public XStream xStream() {
		final var xStream = new XStream();
		xStream.allowTypesByWildcard(new String[]{"com.shubnikofff.**"});
		return xStream;
	}
}
