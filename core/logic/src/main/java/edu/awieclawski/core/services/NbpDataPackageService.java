package edu.awieclawski.core.services;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.core.dtos.ConnResultDto;

public interface NbpDataPackageService {

	Pair<Boolean, DataPackageDto> getATypeRateByDateAndSymbolAndSave(ConnResultDto result);

	Pair<Boolean, DataPackageDto> getATypeRatesByDateRangeAndSymbolAndSave(ConnResultDto result);

	Pair<Boolean, DataPackageDto> getCTypeRateByDateAndSymbolAndSave(ConnResultDto result);

	Pair<Boolean, DataPackageDto> getCTypeRatesByDateRangeAndSymbolAndSave(ConnResultDto result);

	Pair<Boolean, DataPackageDto> getATypeTableByDateAndSave(ConnResultDto result);

	Pair<Boolean, DataPackageDto> getATypeTableByDateRangeAndSave(ConnResultDto result);

	Pair<Boolean, DataPackageDto> getBTypeTableByDateAndSave(ConnResultDto result);

	Pair<Boolean, DataPackageDto> getBTypeTableByDateRangeAndSave(ConnResultDto result);

	Pair<Boolean, DataPackageDto> getCTypeTableByDateAndSave(ConnResultDto result);

	Pair<Boolean, DataPackageDto> getCTypeTableByDateRangeAndSave(ConnResultDto result);

	List<DataPackageDto> getByUrlLikeDaySingle(String endPoint, String code, LocalDate date);

	List<DataPackageDto> getByUrlLikeRangeSingle(String endPoint, String code, LocalDate date, LocalDate dateEnd);

	List<DataPackageDto> getByUrlLikeDayTable(String endPoint, LocalDate date);

	List<DataPackageDto> getByUrlLikeRangeTable(String endPoint, LocalDate date, LocalDate dateEnd);

	Pair<Boolean, DataPackageDto> handleDataDto(DataPackageDto dataDto, String msg);

	List<DataPackageDto> getDistinctPackages(List<DataPackageDto> packages);
	
	void delete(DataPackageDto data);

	List<DataPackageDto> findAll();

}
