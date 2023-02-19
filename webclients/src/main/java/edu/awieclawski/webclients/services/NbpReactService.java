package edu.awieclawski.webclients.services;

import java.util.function.Function;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import edu.awieclawski.webclients.connectors.CustomHttpConnector;
import edu.awieclawski.webclients.filters.CustomWebClientFilter;
import edu.awieclawski.webclients.handlers.CustomErrorHandler;
import reactor.core.publisher.Mono;

public interface NbpReactService extends NbpIntegrationService {
	String ERROR_MSG = "NBP integration REACT service problem!";

	default ClientHttpConnector customHttpConnector() {

		return new CustomHttpConnector().clientConnector(20000, 20, 20, false);
	}

	default Function<ClientResponse, Mono<? extends Throwable>> errorHandler() {

		return new CustomErrorHandler().handleErrorResponse(ERROR_MSG);
	}

	default ExchangeFilterFunction requestExchangeFilterFunction() {

		return new CustomWebClientFilter().loggRequest();
	}

	default ExchangeFilterFunction responseExchangeFilterFunction() {

		return new CustomWebClientFilter().loggResponse();
	}

}
