package edu.awieclawski.core.services.implemented;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.awieclawski.commons.beans.NbpEndPointElements;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.converters.exceptions.ConverterException;
import edu.awieclawski.core.exceptions.NbpRequestException;
import edu.awieclawski.core.facades.RateFacade;
import edu.awieclawski.core.services.NbpDataPackageService;
import edu.awieclawski.core.services.NbpRequestService;
import edu.awieclawski.models.entities.DataPackage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@DependsOn("endPointElements")
class NbpRequestServiceImpl implements NbpRequestService {

	private final NbpDataPackageService dataService;
	private final RateFacade rateFacade;
	private final NbpEndPointElements endPoints;

	private int maxCount;

	@PostConstruct
	private void init() {
		maxCount = endPoints.maxCount;
	}

	@Override
	@Transactional
	public Pair<LocalDate, List<DataPackage>> getPairDatePackagesIfLastPackageRecordAndSave(LocalDate date,
			int counter, List<DataPackage> opPackages, String endPoint, String currencyCode, LocalDate endDate) {
		Boolean connFailed = true;

		if (opPackages == null) {
			opPackages = new ArrayList<>();
		}

		try {
			List<Pair<Boolean, DataPackage>> pairsList = typeConnectionSwitch(date, endPoint, currencyCode, endDate);
			List<DataPackage> dataList = pairsList.stream()
					.filter(Objects::nonNull)
					.map(Pair::getRight)
					.toList();
			connFailed = pairsList.stream()
					.filter(Objects::nonNull)
					.map(Pair::getLeft)
					.anyMatch(b -> b.equals(false));

			if (!dataList.isEmpty()) {
				opPackages.addAll(dataList);
			} else if (connFailed) {
				log.info("Connection counter reset to {}", --counter);
				date = (dayBack(date, counter, opPackages, endPoint, currencyCode, endDate));
			} else {

				log.info("Connection was ok?", !connFailed);

				if (endDate == null) {

					if (endPoint.contains("tables")) {
						dataList = dataService.getByUrlLikeDayTable(endPoint, date);
					} else {
						dataList = dataService.getByUrlLikeDaySingle(endPoint, currencyCode, date);
					}

				} else {

					if (endPoint.contains("tables")) {
						dataList = dataService.getByUrlLikeRangeTable(endPoint, date, endDate);
					} else {
						dataList = dataService.getByUrlLikeRangeSingle(endPoint, currencyCode, date, endDate);
					}
				}

				log.info("DataPackages found {}.", dataList);
			}

		} catch (Throwable t) {
			log.warn(
					"During finding Data Packages for date: {} and endPoint {} an error was thrown: {} | Error message: {}",
					date, endPoint, t.getClass().getSimpleName(), t.getMessage());

			if (counter < maxCount && connFailed) {
				date = (dayBack(date, counter, opPackages, endPoint, currencyCode, endDate));
			} else {
				throw new NbpRequestException("FAILED after " + counter + " attempts! Last used date: " + date);
			}
		}

		return Pair.of(date, opPackages);
	}

	@Override
	@Transactional
	public LocalDate getPackagesAndSaveIfDateBeforeLastExchangeRateDate(LocalDate date, int counter,
			List<DataPackage> opPackages, String endPoint, NbpType nbpType, List<String> codes, LocalDate endDate) {
		boolean boundaryElementsFound = false;

		for (String code : codes) {
			boundaryElementsFound = boundaryElementsFound
					&& rateFacade.findIfExistByCodeAndDateAndType(code, date, nbpType)
					&& rateFacade.findIfExistByCodeAndDateAndType(code, endDate, nbpType);
		}

		if (boundaryElementsFound) {
			return date;
		}

		String currencyCode = codes.size() > 0 ? codes.get(0) : null;
		return getPairDatePackagesIfLastPackageRecordAndSave(date, counter, opPackages, endPoint, currencyCode,
				endDate).getLeft();
	}

	private LocalDate dayBack(LocalDate date, int counter, List<DataPackage> opPackages, String endPoint,
			String currencyCode, LocalDate endDate) {
		date = date.plusDays(-1);

		if (endDate != null) {
			endDate = endDate.plusDays(-1);
		}

		counter++;

		return getPairDatePackagesIfLastPackageRecordAndSave(date, counter, opPackages, endPoint, currencyCode,
				endDate).getLeft();
	}

	private List<Pair<Boolean, DataPackage>> typeConnectionSwitch(LocalDate date, String endPoint, String currencyCode,
			LocalDate endDate)
			throws ConverterException {
		List<Pair<Boolean, DataPackage>> dataList = new ArrayList<>();
		Pair<Boolean, DataPackage> pair = null;

		if (Objects.equals(endPoints.aTableRate, endPoint) && endDate == null) {
			pair = dataService.getATypeTableByDateAndSave(date, 0);
		} else if (Objects.equals(endPoints.ratesA, endPoint) && endDate == null) {
			pair = dataService.getATypeRateByDateAndSymbolAndSave(date, currencyCode, 0);
		} else if (Objects.equals(endPoints.aTableRate, endPoint) && endDate != null) {
			pair = dataService.getATypeTableByDateRangeAndSave(date, endDate, 0);
		} else if (Objects.equals(endPoints.ratesA, endPoint) && endDate != null) {
			pair = dataService.getATypeRatesByDateRangeAndSymbolAndSave(date, endDate, currencyCode, 0);
		} else if (Objects.equals(endPoints.bTableRate, endPoint) && endDate == null) {
			pair = dataService.getBTypeTableByDateAndSave(date, 0);
		} else if (Objects.equals(endPoints.bTableRate, endPoint) && endDate != null) {
			pair = dataService.getBTypeTableByDateRangeAndSave(date, endDate, 0);
		} else if (Objects.equals(endPoints.cTableRate, endPoint) && endDate == null) {
			pair = dataService.getCTypeTableByDateAndSave(date, 0);
		} else if (Objects.equals(endPoints.ratesC, endPoint) && endDate == null) {
			pair = dataService.getCTypeRateByDateAndSymbolAndSave(date, currencyCode, 0);
		} else if (Objects.equals(endPoints.cTableRate, endPoint) && endDate != null) {
			pair = dataService.getCTypeTableByDateRangeAndSave(date, endDate, 0);
		} else if (Objects.equals(endPoints.ratesC, endPoint) && endDate != null) {
			pair = dataService.getCTypeRatesByDateRangeAndSymbolAndSave(date, endDate, currencyCode, 0);
		}

		else {
			throw new ConverterException("Ralated NBP end-point not supported: " + endPoint);
		}

		if (pair != null) {
			dataList.add(pair);
		}

		return dataList;
	}

	@Override
	@Transactional
	public LocalDate getDateOfLastDataPackagesByTypeAndSave(LocalDate date, int counter, List<DataPackage> opPackages,
			NbpType nbpType, List<String> codes, LocalDate endDate)
			throws ConverterException {

		if (codes.isEmpty() || codes.size() > 1) {
			if (Objects.isNull(nbpType) || NbpType.A.equals(nbpType)) {
				return getPackagesAndSaveIfDateBeforeLastExchangeRateDate(date, counter, opPackages,
						endPoints.aTableRate, nbpType, codes, endDate);
			} else if (NbpType.B.equals(nbpType)) {
				return getPackagesAndSaveIfDateBeforeLastExchangeRateDate(date, counter, opPackages,
						endPoints.bTableRate, nbpType, codes, endDate);
			} else if (NbpType.C.equals(nbpType)) {
				return getPackagesAndSaveIfDateBeforeLastExchangeRateDate(date, counter, opPackages,
						endPoints.cTableRate, nbpType, codes, endDate);
			} else {
				throw new ConverterException("Ralated NBP type table not supported: " + nbpType);
			}
		} else { // regards codes with only one element
			String code = codes.get(0);
			if (Objects.isNull(nbpType) || NbpType.A.equals(nbpType)) {
				return getPackagesAndSaveIfDateBeforeLastExchangeRateDate(date, counter, opPackages, endPoints.ratesA,
						nbpType, codes, endDate);
			} else if (NbpType.C.equals(nbpType)) {
				return getPackagesAndSaveIfDateBeforeLastExchangeRateDate(date, counter, opPackages, endPoints.ratesC,
						nbpType, codes, endDate);
			} else {
				throw new ConverterException(
						"Ralated NBP type rate [" + nbpType + "] for single currency [" + code + "] not supported!");
			}
		}
	}

}
