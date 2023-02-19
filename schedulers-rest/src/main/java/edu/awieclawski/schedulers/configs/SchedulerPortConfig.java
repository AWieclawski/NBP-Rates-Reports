package edu.awieclawski.schedulers.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class SchedulerPortConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

	@Value("${rest.api.port}")
	private Integer portNumber;

	@Override
	public void customize(ConfigurableWebServerFactory factory) {
		factory.setPort(portNumber);
	}
}
