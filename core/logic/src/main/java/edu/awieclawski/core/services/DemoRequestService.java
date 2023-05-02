package edu.awieclawski.core.services;

import edu.awieclawski.core.dtos.ConnResultDto;

public interface DemoRequestService {

	ConnResultDto getPackageBeforeLastRecordAndSave(ConnResultDto connDto);

	ConnResultDto shiftDateByResult(ConnResultDto connDto);

}
