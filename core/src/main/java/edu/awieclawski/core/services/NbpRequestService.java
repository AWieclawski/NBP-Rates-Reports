package edu.awieclawski.core.services;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.models.entities.DataPackage;

public interface NbpRequestService {

	LocalDate getPackagesAndSaveIfDateBeforeLastExchangeRateDate(LocalDate date, int counter,
			List<DataPackage> opPackages, String endPoint, NbpType nbpType, List<String> codes, LocalDate endDate);

	LocalDate getDateOfLastDataPackagesByTypeAndSave(LocalDate date, int counter, List<DataPackage> opPackages,
			NbpType nbpType, List<String> codes, LocalDate endDate);

	Pair<LocalDate, List<DataPackage>> getPairDatePackagesIfLastPackageRecordAndSave(LocalDate date, int counter,
			List<DataPackage> opPackages, String endPoint, String currencyCode, LocalDate endDate);
}
