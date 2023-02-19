package edu.awieclawski.webclients.filters;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author awieclawski
 * 
 *         according to https://www.baeldung.com/spring-log-webclient-calls
 *
 */
@Slf4j
public class CustomWebClientFilter {

	@Getter
	private URI requestUrl;

	public ExchangeFilterFunction loggRequest() {

		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			HttpHeaders headers = null;

			if (clientRequest != null) {
				requestUrl = clientRequest.url();
				log.info("Request method={}, target url={}", clientRequest.method(), requestUrl);
				headers = clientRequest.headers();
			}

			if (headers != null) {
				headers.forEach((name, values) -> values
						.forEach(value -> log.info("Request header name={}, value={}", name, value)));
			}

			return Mono.just(clientRequest);
		});
	}

	public ExchangeFilterFunction loggResponse() {

		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {

			if (clientResponse != null) {
				log.info("Response status Code={}", clientResponse.statusCode());
				log.info("Response content-length={}", clientResponse.headers().header("content-length"));
			}

			return Mono.just(clientResponse);
		});
	}

}
