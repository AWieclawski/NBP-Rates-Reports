package edu.awieclawski.webclients.handlers;

import java.util.function.Function;

import org.springframework.web.reactive.function.client.ClientResponse;

import edu.awieclawski.webclients.exceptions.NbpIntegrationException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomErrorHandler {

	public Function<ClientResponse, Mono<? extends Throwable>> handleErrorResponse(String msg) {

		return clientResponse -> {

			if (clientResponse != null) {
				String raw = clientResponse.bodyToMono(String.class).toString();
				log.error("Web client response issue: {} | {} ", msg, raw);
			}

			throw new NbpIntegrationException(msg);
		};
	}

}
