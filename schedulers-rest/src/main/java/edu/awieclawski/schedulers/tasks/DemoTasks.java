package edu.awieclawski.schedulers.tasks;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.awieclawski.core.services.demo.CoreDemo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemoTasks {

	@Value("${nbp-api.demo}")
	private Boolean RUN_DEMO;

	private final CoreDemo coreDemo;

	private boolean isBuild = false;

	@PostConstruct
	void changeFlag() {
		isBuild = true;

		if (Objects.isNull(RUN_DEMO)) {
			RUN_DEMO = false;
			log.info("Demo scheduler is OFF");
		} else if (RUN_DEMO) {
			log.info("Demo scheduler is ON");
		} else {
			log.info("Demo scheduler is OFF");
		}
	}

	@Scheduled(cron = "0 5,25 * * * ?") // AT 5 MINUTE of each hour
	public void demoCoreRunRateSingle() throws InterruptedException {
		String msg = "demo run for rate single date";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(false, false);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 10,30 * * * ?") // AT 10 MINUTE of each hour
	public void demoCoreRunRateRange() throws InterruptedException {
		String msg = "demo run for rate with dates range";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(false, true);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 15,35 * * * ?") // AT 15 MINUTE of each hour
	public void demoCoreRunForTableSingle() throws InterruptedException {
		String msg = "demo run for table with single date";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, false);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 20,40 * * * ?") // AT 20 MINUTE of each hour
	public void demoCoreRunTableRange() throws InterruptedException {
		String msg = "demo run for table rates with dates range";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, true);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 45,55 * * * ?") // AT 45 MINUTE of each hour
	public void demoCoreRunRateSingleBis() throws InterruptedException {
		String msg = "demo run for rate single date";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(false, false);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 50 * * * ?") // AT 50 MINUTE of each hour
	public void demoCoreRunForTableSingleBis() throws InterruptedException {
		String msg = "demo run for table with single date";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, false);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 00 * * * ?") // AT 00 MINUTE of each hour
	public void demoCoreRunTableRangeBis() throws InterruptedException {
		String msg = "demo run for table rates with dates range";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, true);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

}
