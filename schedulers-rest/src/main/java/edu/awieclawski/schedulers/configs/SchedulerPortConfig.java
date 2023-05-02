package edu.awieclawski.schedulers.configs;

import javax.annotation.PostConstruct;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import edu.awieclawski.commons.beans.NbpConnectionElements;
import lombok.RequiredArgsConstructor;

@Component
@Configuration
@RequiredArgsConstructor
@DependsOn("nbpConnectionElements")
public class SchedulerPortConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
	private final NbpConnectionElements endPoints;

	private Integer portNumber;

	@PostConstruct
	private void init() {
		portNumber = endPoints.portNumber;
	}

	@Override
	public void customize(ConfigurableWebServerFactory factory) {
		factory.setPort(portNumber);
	}
}
