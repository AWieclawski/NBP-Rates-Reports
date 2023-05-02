package edu.awieclawski.core.services.implemented;

import java.util.Objects;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import edu.awieclawski.commons.beans.NbpConnectionElements;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.converters.exceptions.ConverterException;
import edu.awieclawski.core.dtos.ConnResultDto;
import edu.awieclawski.core.services.DemoRequestService;
import edu.awieclawski.core.services.NbpRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@DependsOn("nbpConnectionElements")
@Slf4j
class DemoRequestServiceImpl implements DemoRequestService {

	private final NbpRequestService nbpReq;
	private final NbpConnectionElements endPoints;

	@Override
	public ConnResultDto getPackageBeforeLastRecordAndSave(ConnResultDto connDto) {

		if (connDto.getCodes().isEmpty() || connDto.getCodes().size() > 1) {
			if (Objects.isNull(connDto.getNbpType()) || NbpType.A.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.aTableRate);
				return shiftDateByResult(connDto);
			} else if (NbpType.B.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.bTableRate);
				return shiftDateByResult(connDto);
			} else if (NbpType.C.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.cTableRate);
				return shiftDateByResult(connDto);
			} else {
				throw new ConverterException("Ralated NBP type table not supported: " + connDto.getNbpType());
			}
		} else { // regards codes with only one element
			String code = connDto.getCodes().get(0);
			if (Objects.isNull(connDto.getNbpType()) || NbpType.A.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.ratesA);
				return shiftDateByResult(connDto);
			} else if (NbpType.C.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.ratesC);
				return shiftDateByResult(connDto);
			} else {
				throw new ConverterException(
						"Ralated NBP type rate [" + connDto.getNbpType()
								+ "] for single currency [" + code + "] not supported!");
			}
		}
	}

	@Override
	public ConnResultDto shiftDateByResult(ConnResultDto connDto) {
		boolean boundaryElementsFound = true;

		while (boundaryElementsFound) {
			boundaryElementsFound = nbpReq.findBoundaryElements(connDto);

			if (boundaryElementsFound) {
				connDto = nbpReq.dayBack(connDto);
				log.info("Boundary date shifted to {} {}", connDto.getStartDate(),
						connDto.getEndDate() != null ? " / " + connDto.getEndDate() : "");
				continue;
			}
		}

		return nbpReq.operateConnResult(connDto);
	}

}
