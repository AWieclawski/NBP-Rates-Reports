package edu.awieclawski.core.services;

import java.time.LocalDate;

import edu.awieclawski.models.entities.DataPackage;

public interface NbpDataPackageService {
	
	DataPackage getATypeRateByDateAndSymbolAndSave(LocalDate date, String currSymb, int count);
	
	DataPackage getATypeRatesByDateRangeAndSymbolAndSave(LocalDate startDate, LocalDate endDate, String currSymb,
			int count);

	DataPackage getCTypeRateByDateAndSymbolAndSave(LocalDate date, String currSymb, int count);

	DataPackage getCTypeRatesByDateRangeAndSymbolAndSave(LocalDate startDate, LocalDate endDate, String currSymb,
			int count);
	
	DataPackage getATypeTableByDateAndSave(LocalDate date, int count);
	
	DataPackage getATypeTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate, int count);

	DataPackage getCTypeTableByDateAndSave(LocalDate date, int count);

	DataPackage getCTypeTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate, int count);
}
