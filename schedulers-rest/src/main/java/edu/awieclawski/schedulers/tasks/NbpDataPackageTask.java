package edu.awieclawski.schedulers.tasks;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.awieclawski.core.dtos.ConnResultDto;
import edu.awieclawski.core.services.NbpDataPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NbpDataPackageTask {

	private final NbpDataPackageService npDataPackageService;

	private boolean isBuild = false;

	@PostConstruct
	void changeFlag() {
		isBuild = true;
	}

	/**
	 * fired at 1:05 AM each day
	 * 
	 * @throws InterruptedException
	 */
	@Scheduled(cron = "0 5 1 * * ?")
	public void dailyNbpDataPackageDayBefore() throws InterruptedException {
		if (isBuild) {
			log.info(" >>> START daily NBP data download day before");
			try {
				npDataPackageService.getATypeTableByDateAndSave(
						ConnResultDto.builder().startDate(LocalDate.now().minusDays(1)).counter(0).build());
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {}", t.getClass(), t.getMessage());
			}
			log.info(" <<< STOP daily NBP data download day before");
		} else {
			log.warn(" Not ready yet - daily NBP data download day before");
		}
	}

}
