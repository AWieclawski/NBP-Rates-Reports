package edu.awieclawski.core.services.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import edu.awieclawski.commons.beans.NbpEndpointBeans;
import edu.awieclawski.core.exceptions.DemoRunException;
import edu.awieclawski.core.facades.CurrencyFacade;
import edu.awieclawski.core.facades.RateFacade;
import edu.awieclawski.core.services.ConversionService;
import edu.awieclawski.core.services.NbpRequestService;
import edu.awieclawski.data.services.DataPackageService;
import edu.awieclawski.models.entities.Currency;
import edu.awieclawski.models.entities.DataPackage;
import edu.awieclawski.models.entities.ExchangeRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CoreDemo {

	private final NbpRequestService requestService;
	private final DataPackageService dataService;
	private final ConversionService ratesConverterService;
	private final RateFacade rateHandler;
	private final CurrencyFacade currencyHandler;
	private final NbpEndpointBeans endPoints;

	// standard messages
	private static final String ERR_DEMO_END = "Error thrown. Failed Demo - END <<<";
	private static final String RESULTS_EMPTY_DEMO_END = "Result list is empty. Failed Demo - END <<<";
	private static final String RESULTS_NOT_ONE_DEMO_END = "Results list size is not one. Failed Demo - END <<<";
	private static final String NO_EARLIEST_DEMO_END = "Eearliest data package not found! Failed Demo - END <<<";
	private static final String UNEXPECTED_ERROR = "Unexpected error {} message: {} \n";

	// operational variables
	private Boolean isTable;
	private Boolean isRange;
	private String currencyCode;
	private LocalDate endDate;
	private LocalDate startDate;
	private String endPoint;

	private List<DataPackage> prePackages;
	private List<Currency> preCurrencies;
	private List<DataPackage> opPackages;
	private List<Currency> opCurrencies;
	private List<ExchangeRate> opRates;

	private void init(Boolean forTable, Boolean forRange) {
		opPackages = new ArrayList<>();
		opCurrencies = new ArrayList<>();
		opRates = new ArrayList<>();
		isTable = forTable;
		isRange = forRange;
		setContextVariables(LocalDate.now());

		if (forTable == null) {
			isTable = false;
		}

		if (forRange == null) {
			isRange = false;
		}
	}

	public void run(Boolean forTable, Boolean forRange) {
		init(forTable, forRange);
		stageOne();
		stageTwo();
		stageThree();
		stageFour();
	}

	public void stageOne() {
		log.info("Demo - START >>> ");
		log.info("Demo - Find existing DataPackages");
		List<LocalDate> dates = new ArrayList<>();
		prePackages = dataService.findAll();
		preCurrencies = currencyHandler.findAll();

		if (CollectionUtils.isEmpty(prePackages)) {

			try {
				dates.add(
				operatePair(requestService.getPairDatePackagesBeforeLastPackageRecordAndSave(startDate, 0, opPackages,
				endPoint, currencyCode, endDate)));
			} catch (Exception e) {
				throw new DemoRunException("Error: " + ERR_DEMO_END);
			}

		} else {
			prePackages.sort(Comparator.comparing(DataPackage::getCreatedAt));
			Optional<DataPackage> earliest = prePackages.stream().findFirst();
			earliest.ifPresentOrElse(
			value -> dates.add(
			operatePair(requestService.getPairDatePackagesBeforeLastPackageRecordAndSave(
			value.getCreatedAt().toLocalDate().minusDays(1), // get day before last saved date
			0, opPackages, endPoint, currencyCode, endDate))),
			() -> {
				log.error(NO_EARLIEST_DEMO_END);
				return;
			});
		}

		if (dates.isEmpty()) {
			throw new DemoRunException("Error: " + RESULTS_EMPTY_DEMO_END);
		}

		if (dates.size() > 1) {
			throw new DemoRunException("Error: " + RESULTS_NOT_ONE_DEMO_END);
		}

		log.info("Demo - Conversion ONLY just imported DataPackages to ExchangeRates (stage).");

		try {
			opRates.addAll(convertData(prePackages));
		} catch (Exception e) {
			throw new DemoRunException("Error: " + ERR_DEMO_END + e.getMessage() + " cause:" + e.getCause());
		}

	}

	private LocalDate operatePair(Pair<LocalDate, List<DataPackage>> pair) {
		opPackages = pair.getSecond(); // avoid remove records existed before demo
		return pair.getFirst();
	}

	public void stageTwo() {
		log.info("Demo - Erase ONLY just imported ExchangeRates (stage).");
		List<ExchangeRate> list = Collections.synchronizedList(opRates);

		synchronized (list) {
			try {
				list.stream()
				.filter(Objects::nonNull)
				.forEach(this::deleteImportedRate);
			} catch (Exception e) {
				throw new DemoRunException("Error: " + ERR_DEMO_END + e.getMessage() + " cause:" + e.getCause());
			}
		}
	}

	public void stageThree() {
		log.info("Demo - Erase ONLY just imported Currencies (stage).");
		List<Currency> usedCurrencies = rateHandler.getDistinctCurrencies(opRates);
		opCurrencies.addAll(usedCurrencies);
		// remove entities existing before demo start
		opCurrencies.removeAll(preCurrencies);
		List<Currency> currencies = Collections.synchronizedList(opCurrencies);

		synchronized (currencies) {
			try {
				currencies.stream()
				.filter(Objects::nonNull)
				.forEach(this::deleteImportedCurrency);
			} catch (Exception e) {
				throw new DemoRunException("Error: " + ERR_DEMO_END + e.getMessage() + " cause:" + e.getCause());
			}
		}
	}

	public void stageFour() {
		log.info("Demo - Erase ONLY just imported DataPackages (stage).");
		List<DataPackage> list = Collections.synchronizedList(opPackages);

		synchronized (list) {
			try {
				list.stream()
				.filter(Objects::nonNull)
				.forEach(this::deleteImportedPackage);
			} catch (Exception e) {
				throw new DemoRunException("Error: " + ERR_DEMO_END + e.getMessage() + " cause:" + e.getCause());
			}
		}

		log.info("All OK. Demo - END <<< ");
	}

	private void deleteImportedCurrency(Currency currency) {
		try {
			currencyHandler.delete(currency.getId());
		} catch (Exception e) {
			log.error(UNEXPECTED_ERROR, e.getClass().getSimpleName(), e.getMessage(), e);
			throw new DemoRunException("Error: " + e.getMessage() + " cause:" + e.getCause());
		}
	}

	private void deleteImportedRate(ExchangeRate rate) {
		try {
			rateHandler.delete(rate.getId());
		} catch (Exception e) {
			log.error(UNEXPECTED_ERROR, e.getClass().getSimpleName(), e.getMessage(), e);
			throw new DemoRunException("Error: " + e.getMessage() + " cause:" + e.getCause());
		}
	}

	private void deleteImportedPackage(DataPackage data) {
		try {
			dataService.delete(data);
		} catch (Exception e) {
			log.error(UNEXPECTED_ERROR, e.getClass().getSimpleName(), e.getMessage(), e);
			throw new DemoRunException("Error: " + e.getMessage() + " cause:" + e.getCause());
		}
	}

	private List<ExchangeRate> convertData(List<DataPackage> prePackages) {
		List<ExchangeRate> list = new ArrayList<>();

		try {
			list = ratesConverterService.convertEceptOmittedDataPackagesAndSave(prePackages);
		} catch (Exception e) {
			log.error(UNEXPECTED_ERROR, e.getClass().getSimpleName(), e.getMessage(), e);
			throw new DemoRunException("Error: " + e.getMessage() + " cause:" + e.getCause());
		}

		return list;
	}

	private void setContextVariables(LocalDate date) {
		if (isTable && !isRange) {
			endPoint = endPoints.aTableRate;
			startDate = date;
			endDate = null;
			currencyCode = null;
		} else if (isTable && isRange) {
			endPoint = endPoints.aTableRate;
			startDate = date.minusDays(7);
			endDate = date;
			currencyCode = null;
		} else if (!isTable && !isRange) {
			endPoint = endPoints.ratesA;
			startDate = date;
			endDate = null;
			currencyCode = "eur";
		} else {
			endPoint = endPoints.ratesA;
			startDate = date.minusDays(7);
			endDate = date;
			currencyCode = "eur";
		}
	}

}
