package edu.awieclawski.core.services;

import java.time.LocalDate;

import edu.awieclawski.models.entities.DataPackage;

public interface NbpDataPackageService {

	DataPackage getATypeRateByDateAndSymbolAndSave(LocalDate date, String currSymb, int count);

	DataPackage getATypeRatesByDateRangeAndSymbolAndSave(LocalDate startDate, LocalDate endDate, String currSymb,
	int count);

	DataPackage getATypeRatesTableByDateAndSave(LocalDate date, int count);

	DataPackage getATypeRatesTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate, int count);
}
