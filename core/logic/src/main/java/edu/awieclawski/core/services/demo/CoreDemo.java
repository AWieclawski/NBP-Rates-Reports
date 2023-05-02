package edu.awieclawski.core.services.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import edu.awieclawski.commons.beans.NbpConnectionElements;
import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.core.dtos.ConnResultDto;
import edu.awieclawski.core.exceptions.DemoRunException;
import edu.awieclawski.core.facades.CurrencyFacade;
import edu.awieclawski.core.facades.RateFacade;
import edu.awieclawski.core.services.ConversionService;
import edu.awieclawski.core.services.DemoRequestService;
import edu.awieclawski.core.services.NbpDataPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CoreDemo {

	private final DemoRequestService requestService;
	private final NbpDataPackageService dataService;
	private final ConversionService ratesConverterService;
	private final RateFacade rateHandler;
	private final CurrencyFacade currencyHandler;
	private final NbpConnectionElements endPoints;

	// standard messages
	private static final String ERR_DEMO_END = "Error thrown. Failed Demo - END <<<";
	private static final String RESULTS_EMPTY_DEMO_END = "Result list is empty. Failed Demo - END <<<";
	private static final String RESULTS_NOT_ONE_DEMO_END = "Results list size is not one. Failed Demo - END <<<";
	private static final String NO_EARLIEST_DEMO_END = "Eearliest data package not found! Failed Demo - END <<<";
	private static final String UNEXPECTED_ERROR = "Unexpected error {} message: {} \n";

	// operational variables
	private Boolean isTable;
	private Boolean isRange;
	private NbpType tableType;
	private String currencyCode;
	private LocalDate endDate;
	private LocalDate startDate;
	private String endPoint;

	private List<DataPackageDto> prePackages;
	private List<CurrencyDto> preCurrencies;
	private List<DataPackageDto> opPackages;
	private List<CurrencyDto> opCurrencies;
	private List<ExchangeRateDto> opRates;

	private void init(Boolean forTable, Boolean forRange, NbpType type) {
		opPackages = new ArrayList<>();
		opCurrencies = new ArrayList<>();
		opRates = new ArrayList<>();
		isTable = forTable;
		isRange = forRange;
		tableType = type;
		setContextVariables(LocalDate.now());

		if (forTable == null) {
			isTable = false;
		}

		if (forRange == null) {
			isRange = false;
		}
	}

	public void run(Boolean forTable, Boolean forRange, NbpType type) {
		init(forTable, forRange, type);
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
						operateDto(
								requestService.getPackageBeforeLastRecordAndSave(
										ConnResultDto.builder()
												.startDate(startDate)
												.packages(opPackages)
												.endPoint(endPoint)
												.currSymb(currencyCode)
												.counter(0)
												.endDate(endDate)
												.build())));
			} catch (Exception e) {
				throw new DemoRunException("Operating date set-up: " + ERR_DEMO_END);
			}

		} else {
			prePackages.sort(Comparator.comparing(DataPackageDto::getCreatedAt));
			Optional<DataPackageDto> earliest = prePackages.stream().findFirst();
			earliest.ifPresentOrElse(
					value -> dates.add(
							operateDto(
									requestService.getPackageBeforeLastRecordAndSave(
											ConnResultDto.builder()
													.startDate(value.getCreatedAt().minusDays(1))
													.packages(opPackages)
													.endPoint(endPoint)
													.currSymb(currencyCode)
													.counter(0)
													.endDate(endDate)
													.build()))),
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

	private LocalDate operateDto(ConnResultDto dto) {
		return dto.getStartDate();
	}

	public void stageTwo() {
		log.info("Demo - Erase ONLY just imported ExchangeRates (stage).");
		List<ExchangeRateDto> list = Collections.synchronizedList(opRates);

		synchronized (list) {
			try {
				list.stream()
						.filter(Objects::nonNull)
						.forEach(this::deleteImportedRate);
			} catch (Exception e) {
				throw new DemoRunException("Error: " + ERR_DEMO_END + e.getMessage() + " cause:" + e.getCause());
			}
			log.info("Deleted {} rates", list.size());
		}
	}

	public void stageThree() {
		log.info("Demo - Erase ONLY just imported Currencies (stage).");
		List<CurrencyDto> usedCurrencies = rateHandler.getDistinctCurrencies(opRates);
		opCurrencies.addAll(usedCurrencies);
		// remove entities existing before demo start
		opCurrencies.removeAll(preCurrencies);
		List<CurrencyDto> currencies = Collections.synchronizedList(opCurrencies);

		synchronized (currencies) {
			try {
				currencies.stream()
						.filter(Objects::nonNull)
						.forEach(this::deleteImportedCurrency);
			} catch (Exception e) {
				throw new DemoRunException("Error: " + ERR_DEMO_END + e.getMessage() + " cause:" + e.getCause());
			}
			log.info("Deleted {} currencies", currencies.size());
		}
	}

	public void stageFour() {
		log.info("Demo - Erase ONLY just imported DataPackages (stage).");
		List<DataPackageDto> list = Collections.synchronizedList(dataService.getDistinctPackages(opPackages));
		synchronized (list) {
			try {
				list.stream()
						.filter(Objects::nonNull)
						.forEach(this::deleteImportedPackage);
			} catch (Exception e) {
				throw new DemoRunException("Error: " + ERR_DEMO_END + e.getMessage() + " cause:" + e.getCause());
			}
			log.info("Deleted {} packages", list.size());
		}

		log.info("All OK. Demo - END <<< ");
	}

	private void deleteImportedCurrency(CurrencyDto currency) {
		try {
			currencyHandler.delete(currency.getId());
		} catch (Exception e) {
			log.error(UNEXPECTED_ERROR, e.getClass().getSimpleName(), e.getMessage(), e);
			throw new DemoRunException("Error: " + e.getMessage() + " cause:" + e.getCause());
		}
	}

	private void deleteImportedRate(ExchangeRateDto rate) {
		try {
			rateHandler.delete(rate.getId());
		} catch (Exception e) {
			log.error(UNEXPECTED_ERROR, e.getClass().getSimpleName(), e.getMessage(), e);
			throw new DemoRunException("Error: " + e.getMessage() + " cause:" + e.getCause());
		}
	}

	private void deleteImportedPackage(DataPackageDto data) {
		try {
			dataService.delete(data);
		} catch (Exception e) {
			log.error(UNEXPECTED_ERROR, e.getClass().getSimpleName(), e.getMessage(), e);
			throw new DemoRunException("Error: " + e.getMessage() + " cause:" + e.getCause());
		}
	}

	private List<ExchangeRateDto> convertData(List<DataPackageDto> prePackages) {
		List<ExchangeRateDto> list = new ArrayList<>();

		try {
			list = ratesConverterService.convertEceptOmittedDataPackagesAndSave(prePackages);
		} catch (Exception e) {
			log.error(UNEXPECTED_ERROR, e.getClass().getSimpleName(), e.getMessage(), e);
			throw new DemoRunException("Error: " + e.getMessage() + " cause:" + e.getCause());
		}

		return list;
	}

	private void setContextVariables(LocalDate date) {
		if (NbpType.A.equals(tableType)) {
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

		if (NbpType.C.equals(tableType)) {
			if (isTable && !isRange) {
				endPoint = endPoints.cTableRate;
				startDate = date;
				endDate = null;
				currencyCode = null;
			} else if (isTable && isRange) {
				endPoint = endPoints.cTableRate;
				startDate = date.minusDays(7);
				endDate = date;
				currencyCode = null;
			} else if (!isTable && !isRange) {
				endPoint = endPoints.ratesC;
				startDate = date;
				endDate = null;
				currencyCode = "eur";
			} else {
				endPoint = endPoints.ratesC;
				startDate = date.minusDays(7);
				endDate = date;
				currencyCode = "eur";
			}
		}

		if (NbpType.B.equals(tableType)) {
			if (isTable && !isRange) {
				endPoint = endPoints.cTableRate;
				startDate = date;
				endDate = null;
				currencyCode = null;
			} else if (isTable && isRange) {
				endPoint = endPoints.cTableRate;
				startDate = date.minusDays(7);
				endDate = date;
				currencyCode = null;
			}
		}

	}

}
