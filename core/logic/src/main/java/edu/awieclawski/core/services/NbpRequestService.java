package edu.awieclawski.core.services;

import edu.awieclawski.core.dtos.ConnResultDto;

public interface NbpRequestService {

	ConnResultDto getDateOfLastDataPackagesByTypeAndSave(ConnResultDto connDto);

	ConnResultDto connGetResults(ConnResultDto connDto);

	ConnResultDto operateConnResult(ConnResultDto connDto);

	ConnResultDto dayBackCount(ConnResultDto connDto);

	ConnResultDto dayBack(ConnResultDto connDto);
	
	boolean findBoundaryElements(ConnResultDto connDto);

}
