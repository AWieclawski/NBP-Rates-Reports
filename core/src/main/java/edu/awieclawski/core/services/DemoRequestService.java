package edu.awieclawski.core.services;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import edu.awieclawski.models.entities.DataPackage;

public interface DemoRequestService {

	Pair<LocalDate, List<DataPackage>> getPairDatePackagesBeforeLastPackageRecordAndSave(LocalDate date,
			int counter, List<DataPackage> opPackages, String endPoint, String currencyCode, LocalDate endDate);

}
