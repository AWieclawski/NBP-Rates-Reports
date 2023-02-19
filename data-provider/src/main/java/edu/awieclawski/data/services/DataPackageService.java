package edu.awieclawski.data.services;

import java.time.LocalDate;
import java.util.List;

import edu.awieclawski.models.entities.DataPackage;

public interface DataPackageService {
	
	String MSG_DATA_URL_NOT_EXIST = "DataPackage [url={}] not exists!";
	String MSG_DATA_URL_EXIST  = "DataPackage [url={}] already exists!";

	DataPackage save(DataPackage data);

	void delete(DataPackage data);

	List<DataPackage> findAll();

	List<DataPackage> findByUrl(String url);

	List<DataPackage> findByCreatedAt(LocalDate date);

	List<DataPackage> findByConverted(Boolean isConverted);

	List<DataPackage> findByEndPoint(String endPoint);

	List<DataPackage> findAllNotConverted();

	void updateConverted(DataPackage data);

	void updateProcessed(DataPackage data);
	
	boolean flushTransaction();

}
