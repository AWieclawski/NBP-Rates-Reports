package edu.awieclawski.webclients.connectors;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

/**
 * https://docs.spring.io/spring-framework/docs/5.1.1.RELEASE/spring-framework-reference/web-reactive.html#webflux-client-builder-reactor
 * 
 * @author awieclawski
 *
 */
@Slf4j
public class CustomHttpConnector {
	// must be final to enable using in lambdas
	private final SslContext[] sslContextArray = new SslContext[1];

	public ClientHttpConnector clientConnector(int timeout, int read_timeout, int write_timeout, boolean secured) {

		HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
				.doOnConnected(conn -> conn.addHandlerFirst(new ReadTimeoutHandler(read_timeout, TimeUnit.SECONDS))
						.addHandlerFirst(new WriteTimeoutHandler(write_timeout)));
		;

		if (secured) {
			sslContextArray[0] = getSslContext()[0];
			httpClient.secure(con -> con.sslContext(sslContextArray[0]));
		}

		return new ReactorClientHttpConnector(httpClient);
	}

	private SslContext[] getSslContext() {

		try {
			sslContextArray[0] = (SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build());
		} catch (SSLException e) {
			log.error("SSLException thrown: {}", e.getMessage());
		}

		return sslContextArray;
	}

}
