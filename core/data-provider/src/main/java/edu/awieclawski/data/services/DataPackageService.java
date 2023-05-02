package edu.awieclawski.data.services;

import java.time.LocalDate;
import java.util.List;

import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.models.entities.DataPackage;

public interface DataPackageService {

	String MSG_DATA_URL_NOT_EXIST = "DataPackage [url={}] not exists!";
	String MSG_DATA_URL_EXIST = "DataPackage [url={}] already exists!";

	DataPackage save(DataPackage data);

	void delete(DataPackageDto data);

	List<DataPackageDto> findAll();

	List<DataPackage> findByUrl(String url);

	List<DataPackage> findByUrlLikeDaySingle(String endPoint, String code, String date);

	List<DataPackage> findByUrlLikeRangeSingle(String endPoint, String code, String date, String dateEnd);

	List<DataPackage> findByUrlLikeDayTable(String endPoint, String date);

	List<DataPackage> findByUrlLikeRangeTable(String endPoint, String date, String dateEnd);

	List<DataPackage> findByCreatedAt(LocalDate date);

	List<DataPackage> findByConverted(Boolean isConverted);

	List<DataPackage> findByEndPoint(String endPoint);

	List<DataPackageDto> findAllNotConverted();

	void updateConverted(DataPackageDto data);

	void updateProcessed(DataPackageDto data);

	boolean flushTransaction();

}
