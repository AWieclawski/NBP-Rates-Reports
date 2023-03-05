package edu.awieclawski.core.services.implemented;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.awieclawski.commons.beans.NbpEndPointElements;
import edu.awieclawski.converters.exceptions.ConverterException;
import edu.awieclawski.core.exceptions.NbpRequestException;
import edu.awieclawski.core.services.NbpDataPackageService;
import edu.awieclawski.core.services.DemoRequestService;
import edu.awieclawski.models.entities.DataPackage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class DemoRequestServiceImpl implements DemoRequestService {

	private final NbpDataPackageService dataService;
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
			List<Pair<Boolean, DataPackage>> pairsList = typeConnectionSwitch(date, endPoint, currencyCode, endDate);
			List<DataPackage> dataList = pairsList.stream()
					.filter(Objects::nonNull)
					.map(Pair::getRight)
					.toList();

			if (!dataList.isEmpty()) {
				opPackages.addAll(dataList);
			} else {
				log.info("Connection counter reset to {}", --counter);
				date = (dayBackDemo(date, counter, opPackages, endPoint, currencyCode, endDate));
			}

			if (CollectionUtils.isEmpty(opPackages)) {
				date = (dayBackDemo(date, counter, opPackages, endPoint, currencyCode, endDate));
				log.info("DataPackage list found empty. Operational date changed to: {}", date);
			}
		} catch (Throwable t) {
			log.warn("During finding Previous Business-Day for date: {} an error was thrown: {} | Error message: {}",
					date, t.getClass().getSimpleName(), t.getMessage());

			if (counter < maxCount) {
				date = (dayBackDemo(date, counter, opPackages, endPoint, currencyCode, endDate));
			} else {
				throw new NbpRequestException("FAILED after " + counter + " attempts! Last used date: " + date);
			}
		}

		return Pair.of(date, opPackages);
	}

	private LocalDate dayBackDemo(LocalDate date, int counter, List<DataPackage> opPackages, String endPoint,
			String currencyCode, LocalDate endDate) {
		date = date.plusDays(-1);

		if (endDate != null) {
			endDate = endDate.plusDays(-1);
		}

		counter++;

		return getPairDatePackagesBeforeLastPackageRecordAndSave(date, counter, opPackages, endPoint, currencyCode,
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

}
