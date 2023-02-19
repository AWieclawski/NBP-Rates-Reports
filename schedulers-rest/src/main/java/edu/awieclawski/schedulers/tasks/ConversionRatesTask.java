package edu.awieclawski.schedulers.tasks;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.awieclawski.core.services.ConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionRatesTask {

	private final ConversionService conversionRatesService;

	private boolean isBuild = false;

	@PostConstruct
	void changeFlag() {
		isBuild = true;
	}

	/**
	 * fired at 1:15 AM each day
	 * 
	 * @throws InterruptedException
	 */
	@Scheduled(cron = "0 15 1 * * ?")
	public void dailyNewestDataPackagesConversion() throws InterruptedException {
		if (isBuild) {
			log.info(" >>> START daily newest data packages conversion");
			try {
				conversionRatesService.convertNewDataPackagesAndSave();
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {}", t.getClass(), t.getMessage());
			}
			log.info(" <<< STOP daily newest data packages conversion");
		} else {
			log.warn(" Not ready yet - daily newest data packages conversion");
		}
	}

}
