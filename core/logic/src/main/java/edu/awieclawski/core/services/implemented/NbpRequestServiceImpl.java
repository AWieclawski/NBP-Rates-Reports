package edu.awieclawski.core.services.implemented;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import edu.awieclawski.commons.beans.NbpConnectionElements;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.converters.exceptions.ConverterException;
import edu.awieclawski.core.dtos.ConnResultDto;
import edu.awieclawski.core.exceptions.NbpRequestException;
import edu.awieclawski.core.facades.RateFacade;
import edu.awieclawski.core.services.ConnectionSwitch;
import edu.awieclawski.core.services.NbpDataPackageService;
import edu.awieclawski.core.services.NbpRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@DependsOn("nbpConnectionElements")
class NbpRequestServiceImpl implements NbpRequestService {

	private final NbpDataPackageService dataService;
	private final RateFacade rateFacade;
	private final NbpConnectionElements endPoints;
	private final ConnectionSwitch connSwitch;

	private int maxCount;

	@PostConstruct
	private void init() {
		maxCount = endPoints.maxCount;
	}

	@Override
	@Transactional
	public ConnResultDto getDateOfLastDataPackagesByTypeAndSave(ConnResultDto connDto)
			throws ConverterException {

		if (connDto.getCodes().isEmpty() || connDto.getCodes().size() > 1) {
			if (Objects.isNull(connDto.getNbpType()) || NbpType.A.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.aTableRate);
				return connGetResults(connDto);
			} else if (NbpType.B.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.bTableRate);
				return connGetResults(connDto);
			} else if (NbpType.C.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.cTableRate);
				return connGetResults(connDto);
			} else {
				throw new ConverterException("Ralated NBP type table not supported: " + connDto.getNbpType());
			}
		} else { // regards codes with only one element
			String code = connDto.getCodes().get(0);
			if (Objects.isNull(connDto.getNbpType()) || NbpType.A.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.ratesA);
				return connGetResults(connDto);
			} else if (NbpType.C.equals(connDto.getNbpType())) {
				connDto.setEndPoint(endPoints.ratesC);
				return connGetResults(connDto);
			} else {
				throw new ConverterException(
						"Ralated NBP type rate [" + connDto.getNbpType()
								+ "] for single currency [" + code + "] not supported!");
			}
		}
	}

	@Override
	public ConnResultDto connGetResults(ConnResultDto connDto) {
		boolean boundaryElementsFound = findBoundaryElements(connDto);

		if (boundaryElementsFound) {
			return connDto;
		}

		return operateConnResult(connDto);
	}

	@Override
	public ConnResultDto operateConnResult(ConnResultDto connDto) {

		if (connDto.getCurrSymb() == null) {
			connDto.setCurrSymb(connDto.getCodes().size() == 1 ? connDto.getCodes().get(0) : null);
		}

		boolean isFinished = false;

		while (!isFinished) {

			try {
				connDto = connSwitch.getByEndPoint(connDto);
			} catch (Exception e) {
				log.warn(
						"During finding Data Packages for date: {} and endPoint {} an error was thrown: {} | Error message: {}",
						connDto.getStartDate(), connDto.getEndPoint(), e.getClass().getSimpleName(), e.getMessage());

				if (connDto.getCounter() < maxCount && !connDto.getSucceed()) {
					connDto = dayBackCount(connDto);
					continue;
				} else {
					throw new NbpRequestException("FAILED after " + connDto.getCounter() + " attempts! Last used date: "
							+ connDto.getStartDate());
				}
			}

			if (!connDto.getSucceed()) {
				connDto = dayBackCount(connDto);
				connDto.setCounter(connDto.getCounter() - 1);
				log.info("Connection counter reset to {}", connDto.getCounter());
				continue;
			} else {
				isFinished = true;

				if (connDto.getEndDate() == null) {

					if (connDto.getEndPoint().contains("tables")) {
						connDto.addPackages(
								dataService.getByUrlLikeDayTable(connDto.getEndPoint(), connDto.getStartDate()));
					} else {
						connDto.addPackages(
								dataService.getByUrlLikeDaySingle(connDto.getEndPoint(), connDto.getCurrSymb(),
										connDto.getStartDate()));
					}

				} else {

					if (connDto.getEndPoint().contains("tables")) {
						connDto.addPackages(
								dataService.getByUrlLikeRangeTable(connDto.getEndPoint(), connDto.getStartDate(),
										connDto.getEndDate()));
					} else {
						connDto.addPackages(
								dataService.getByUrlLikeRangeSingle(connDto.getEndPoint(), connDto.getCurrSymb(),
										connDto.getStartDate(), connDto.getEndDate()));
					}
				}
				log.info("Data Package succesfully found at NBP API with date: {}.", connDto.getStartDate());
			}
		}

		return connDto;
	}

	@Override
	public ConnResultDto dayBackCount(ConnResultDto connDto) {
		connDto = dayBack(connDto);
		connDto.setCounter(connDto.getCounter() + 1);
		return connDto;
	}

	@Override
	public ConnResultDto dayBack(ConnResultDto connDto) {
		connDto.setStartDate(connDto.getStartDate().plusDays(-1));

		if (connDto.getEndDate() != null) {
			connDto.setEndDate(connDto.getEndDate().plusDays(-1));
		}

		return connDto;
	}

	@Override
	public boolean findBoundaryElements(ConnResultDto connDto) {
		boolean boundaryElementsFound = true;

		for (String code : connDto.getCodes()) {
			if (connDto.getEndDate() == null) {
				boundaryElementsFound = boundaryElementsFound
						&& rateFacade.findIfExistByCodeAndDateAndType(code, connDto.getStartDate(),
								connDto.getNbpType());
			} else {
				boundaryElementsFound = boundaryElementsFound
						&& rateFacade.findIfExistByCodeAndDateAndType(code, connDto.getEndDate(),
								connDto.getNbpType());
			}
		}

		if (CollectionUtils.isEmpty(connDto.getCodes())) {
			if (connDto.getEndDate() == null) {
				boundaryElementsFound = boundaryElementsFound
						&& rateFacade.findIfExistsBeforeDateAndType(connDto.getStartDate(), connDto.getNbpType());
			} else {
				boundaryElementsFound = boundaryElementsFound
						&& rateFacade.findIfExistsBeforeDateAndType(connDto.getEndDate(), connDto.getNbpType());
			}
		}

		return boundaryElementsFound;
	}

}
