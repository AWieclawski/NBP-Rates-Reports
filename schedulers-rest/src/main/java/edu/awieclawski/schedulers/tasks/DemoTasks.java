package edu.awieclawski.schedulers.tasks;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.awieclawski.commons.dtos.enums.NbpType;
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

	// A

	@Scheduled(cron = "0 5 * * * ?") // AT 5 MINUTE of each hour
	public void demoCoreRunARateSingle() throws InterruptedException {
		String msg = "demo run for rate single date";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(false, false, NbpType.A);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 10 * * * ?") // AT 10 MINUTE of each hour
	public void demoCoreRunARateRange() throws InterruptedException {
		String msg = "demo run for rate with dates range";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(false, true, NbpType.A);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 15 * * * ?") // AT 15 MINUTE of each hour
	public void demoCoreRunForATableSingle() throws InterruptedException {
		String msg = "demo run for table with single date";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, false, NbpType.A);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 20 * * * ?") // AT 20 MINUTE of each hour
	public void demoCoreRunATableRange() throws InterruptedException {
		String msg = "demo run for table rates with dates range";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, true, NbpType.A);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	// C

	@Scheduled(cron = "0 25 * * * ?") // AT 25 MINUTE of each hour
	public void demoCoreRunCRateSingle() throws InterruptedException {
		String msg = "demo run for rate single date";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(false, false, NbpType.C);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 30 * * * ?") // AT 30 MINUTE of each hour
	public void demoCoreRunForCRateRange() throws InterruptedException {
		String msg = "demo run for rate with dates range";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(false, true, NbpType.C);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 35 * * * ?") // AT 35 MINUTE of each hour
	public void demoCoreRunCTableSingle() throws InterruptedException {
		String msg = "demo run for table with single date";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, false, NbpType.C);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 40 * * * ?") // AT 40 MINUTE of each hour
	public void demoCoreRunCTableRange() throws InterruptedException {
		String msg = "demo run for table rates with dates range";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, true, NbpType.C);
			} catch (Throwable t) {
				log.warn("Thrown exception: {}. Error message: {} \n {}",
						t.getClass().getSimpleName(), t.getMessage(), t);
			}
			log.info(" <<< STOP " + msg);
		} else if (!isBuild) {
			log.warn(" Not ready yet - " + msg);
		}
	}

	@Scheduled(cron = "0 45 * * * ?") // AT 45 MINUTE of each hour
	public void demoCoreRunBTableSingle() throws InterruptedException {
		String msg = "demo run for table rates with dates range";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, false, NbpType.B);
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
	public void demoCoreRunBTableRange() throws InterruptedException {
		String msg = "demo run for table rates with dates range";

		if (isBuild && RUN_DEMO) {
			log.info(" >>> START " + msg);
			try {
				coreDemo.run(true, true, NbpType.B);
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
