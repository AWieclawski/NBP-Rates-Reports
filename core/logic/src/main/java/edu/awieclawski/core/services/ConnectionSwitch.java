package edu.awieclawski.core.services;

import edu.awieclawski.core.dtos.ConnResultDto;

public interface ConnectionSwitch {

	ConnResultDto getByEndPoint(ConnResultDto connDto);

}
