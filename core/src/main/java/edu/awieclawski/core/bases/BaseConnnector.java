package edu.awieclawski.core.bases;

import java.util.concurrent.TimeUnit;

import org.springframework.web.reactive.function.client.WebClientException;

import edu.awieclawski.webclients.dtos.DataResponseDto;
import edu.awieclawski.webclients.exceptions.NbpIntegrationException;
import io.netty.channel.ChannelException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class BaseConnnector implements Ordinals {

	protected int attempsQty = 3;
	protected int delaySeconds = 15;
	protected DataResponseDto responseDto;

	protected void run(int count) {
		init();
		String conn = getOrdinalbyNumber(count + 1) + " connection";

		try {
			responseDto = doExecute(count);
			log.info("{} succeced.", conn);
		} catch (ChannelException | WebClientException e) {
			log.error("{} failed. Network error: {}, message: {} | cause: {}.",
					conn, e.getClass().getSimpleName(), e.getMessage(), e.getCause());
			count = emergencyLogic(count, conn);
		}

		if (responseDto == null) {
			conn = getOrdinalbyNumber(count + 1) + " connection in emergency threat mode.";
			log.warn("{} Recurrent attempt no: {}", conn, count);
			run(count);
		}
	}

	protected abstract DataResponseDto execute();

	private void init() {
		responseDto = null;
	}

	public DataResponseDto getResponseDto() {
		return this.responseDto;
	}

	private DataResponseDto doExecute(int count) {
		return execute();
	}

	private int emergencyLogic(int count, String conn) {

		if (count <= attempsQty) {
			log.warn("Next connection attempt will be executed in {} seconds", delaySeconds);

			try {
				TimeUnit.SECONDS.sleep(delaySeconds);
			} catch (InterruptedException i) {
				log.error("InterruptedException {}", i.getMessage());
			}

			count++;
		} else {
			throw new NbpIntegrationException("Persistent error. Quit after try " + conn);
		}

		return count;
	}

}
