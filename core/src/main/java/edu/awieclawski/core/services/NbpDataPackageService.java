package edu.awieclawski.core.services;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import edu.awieclawski.models.entities.DataPackage;

public interface NbpDataPackageService {
	
	Pair<Boolean, DataPackage> getATypeRateByDateAndSymbolAndSave(LocalDate date, String currSymb, int count);
	
	Pair<Boolean, DataPackage> getATypeRatesByDateRangeAndSymbolAndSave(LocalDate startDate, LocalDate endDate,
			String currSymb, int count);

	Pair<Boolean, DataPackage> getCTypeRateByDateAndSymbolAndSave(LocalDate date, String currSymb, int count);

	Pair<Boolean, DataPackage> getCTypeRatesByDateRangeAndSymbolAndSave(LocalDate startDate, LocalDate endDate,
			String currSymb, int count);
	
	Pair<Boolean, DataPackage> getATypeTableByDateAndSave(LocalDate date, int count);
	
	Pair<Boolean, DataPackage> getATypeTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate, int count);

	Pair<Boolean, DataPackage> getBTypeTableByDateAndSave(LocalDate date, int count);

	Pair<Boolean, DataPackage> getBTypeTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate, int count);

	Pair<Boolean, DataPackage> getCTypeTableByDateAndSave(LocalDate date, int count);

	Pair<Boolean, DataPackage> getCTypeTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate, int count);
	
	List<DataPackage> getByUrlLikeDaySingle(String endPoint, String code, LocalDate date);
	
	List<DataPackage> getByUrlLikeRangeSingle(String endPoint, String code, LocalDate date, LocalDate dateEnd);

	List<DataPackage> getByUrlLikeDayTable(String endPoint, LocalDate date);

	List<DataPackage> getByUrlLikeRangeTable(String endPoint, LocalDate date, LocalDate dateEnd);

}
