package edu.awieclawski.core.services.implemented;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
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
class NbpRequestServiceImpl implements NbpRequestService {

	private final NbpDataPackageService dataService;
	private final RateFacade rateFacade;
	private final NbpEndPointElements endPoints;

	@Value("${nbp-api.attempts}")
	private static int maxCount;

	@Override
	@Transactional
	public Pair<LocalDate, List<DataPackage>> getPairDatePackagesBeforeLastPackageRecordAndSave(LocalDate date,
			int counter, List<DataPackage> opPackages, String endPoint, String currencyCode, LocalDate endDate) {

		if (opPackages == null) {
			opPackages = new ArrayList<>();
		}

		try {
			List<DataPackage> dataList = typeConnectionSwitch(date, endPoint, currencyCode, endDate);

			if (!dataList.isEmpty()) {
				opPackages.addAll(dataList);
			} else {
				log.info("Connection counter reset to {}", --counter);
				date = (dayBack(date, counter, opPackages, endPoint, currencyCode, endDate));
			}

			if (CollectionUtils.isEmpty(opPackages)) {
				date = (dayBack(date, counter, opPackages, endPoint, currencyCode, endDate));
				log.info("DataPackage list found empty. Operational date changed to: {}", date);
			}
		} catch (Throwable t) {
			log.warn("During finding Previous Business-Day for date: {} an error was thrown: {} | Error message: {}",
					date, t.getClass().getSimpleName(), t.getMessage());

			if (counter < maxCount) {
				date = (dayBack(date, counter, opPackages, endPoint, currencyCode, endDate));
			} else {
				throw new NbpRequestException("FAILED after " + counter + " attempts! Last used date: " + date);
			}
		}

		return Pair.of(date, opPackages);
	}

	/**
	 * Used by Demo with NbpType.A NBP table type
	 */
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
		return getPairDatePackagesBeforeLastPackageRecordAndSave(date, counter, opPackages, endPoint, currencyCode,
				endDate).getFirst();
	}

	private LocalDate dayBack(LocalDate date, int counter, List<DataPackage> opPackages, String endPoint,
			String currencyCode, LocalDate endDate) {
		date = date.plusDays(-1);

		if (endDate != null) {
			endDate = endDate.plusDays(-1);
		}

		counter++;

		return getPairDatePackagesBeforeLastPackageRecordAndSave(date, counter, opPackages, endPoint, currencyCode,
				endDate).getFirst();
	}

	private List<DataPackage> typeConnectionSwitch(LocalDate date, String endPoint, String currencyCode,
			LocalDate endDate)
			throws ConverterException {
		List<DataPackage> dataList = new ArrayList<>();
		DataPackage entity = null;

		if (Objects.equals(endPoints.aTableRate, endPoint) && endDate == null) {
			entity = dataService.getATypeTableByDateAndSave(date, 0);
		} else if (Objects.equals(endPoints.ratesA, endPoint) && endDate == null) {
			entity = dataService.getATypeRateByDateAndSymbolAndSave(date, currencyCode, 0);
		} else if (Objects.equals(endPoints.aTableRate, endPoint) && endDate != null) {
			entity = dataService.getATypeTableByDateRangeAndSave(date, endDate, 0);
		} else if (Objects.equals(endPoints.ratesA, endPoint) && endDate != null) {
			entity = dataService.getATypeRatesByDateRangeAndSymbolAndSave(date, endDate, currencyCode, 0);
		} else if (Objects.equals(endPoints.cTableRate, endPoint) && endDate == null) {
			entity = dataService.getCTypeTableByDateAndSave(date, 0);
		} else if (Objects.equals(endPoints.ratesC, endPoint) && endDate == null) {
			entity = dataService.getCTypeRateByDateAndSymbolAndSave(date, currencyCode, 0);
		} else if (Objects.equals(endPoints.cTableRate, endPoint) && endDate != null) {
			entity = dataService.getCTypeTableByDateRangeAndSave(date, endDate, 0);
		} else if (Objects.equals(endPoints.ratesC, endPoint) && endDate != null) {
			entity = dataService.getCTypeRatesByDateRangeAndSymbolAndSave(date, endDate, currencyCode, 0);
		}

		else {
			throw new ConverterException("Ralated NBP end-point not supported: " + endPoint);
		}

		if (entity != null) {
			dataList.add(entity);
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
