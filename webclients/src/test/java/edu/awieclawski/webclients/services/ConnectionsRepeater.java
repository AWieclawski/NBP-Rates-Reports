package edu.awieclawski.webclients.services;

import java.util.concurrent.TimeUnit;

import org.springframework.web.reactive.function.client.WebClientRequestException;

import edu.awieclawski.webclients.exceptions.NbpIntegrationException;
import io.netty.channel.ChannelException;
import lombok.extern.slf4j.Slf4j;

public interface ConnectionsRepeater {

	int MAX_TRY = 3;
	int SECONDS_DELAY = 15;

	@Slf4j
	final class LogHolder {
	}

	/**
	 * Practical ignore objective network issues during tests in. target reject
	 * 
	 * @param action
	 */
	public default void tryCatchException(Runnable action, int count) {
		try {
			action.run();
		} catch (ChannelException | WebClientRequestException e) {
			LogHolder.log.error("Network error {} caused {} : {}", e.getClass(), e.getCause(), e.getMessage());
			try {

				if (count < MAX_TRY) {
					LogHolder.log.error("Wait to next attempt in {} seconds.", SECONDS_DELAY);
					TimeUnit.SECONDS.sleep(SECONDS_DELAY);
					count++;

					LogHolder.log.error("Action emergency attempt {}", count);
					tryCatchException(action, count);
				} else {
					throw new NbpIntegrationException("NBP API error after " + count + " attempts ");
				}

			} catch (InterruptedException i) {
				LogHolder.log.error("InterruptedException {}", i.getMessage());
			}
		}
	}

}
