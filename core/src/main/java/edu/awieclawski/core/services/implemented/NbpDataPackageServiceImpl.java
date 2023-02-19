package edu.awieclawski.core.services.implemented;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.awieclawski.core.bases.ConnectorsFactory;
import edu.awieclawski.core.services.NbpDataPackageService;
import edu.awieclawski.data.exceptions.DataExistsException;
import edu.awieclawski.data.services.DataPackageService;
import edu.awieclawski.models.entities.DataPackage;
import edu.awieclawski.webclients.dtos.DataResponseDto;
import edu.awieclawski.webclients.services.NbpReactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class NbpDataPackageServiceImpl extends ConnectorsFactory implements NbpDataPackageService {

	private final DataPackageService dataService;
	private final NbpReactService reactService;

	@Override
	@Transactional
	public DataPackage getATypeRateByDateAndSymbolAndSave(LocalDate date, String currSymb, int count) {
		DataResponseDto dataDto = doConnecATypeImpl(reactService, date, currSymb, count);

		return handleDataDto(dataDto, "ATypeRate By Date " + date + " And Symbol " + currSymb);
	}

	@Override
	@Transactional
	public DataPackage getATypeRatesByDateRangeAndSymbolAndSave(LocalDate startDate, LocalDate endDate,
	String currSymb, int count) {
		DataResponseDto dataDto = doConnectATypeImpl(reactService, startDate, endDate, currSymb, count);

		return handleDataDto(dataDto,
		"ATypeRate By Date Range " + startDate + " - " + endDate + " And Symbol " + currSymb);
	}

	@Override
	@Transactional
	public DataPackage getATypeRatesTableByDateAndSave(LocalDate date, int count) {
		DataResponseDto dataDto = doConnectATypeImpl(reactService, date, count);

		return handleDataDto(dataDto, "ATypeRates Table By Date " + date);
	}

	@Override
	@Transactional
	public DataPackage getATypeRatesTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate, int count) {
		DataResponseDto dataDto = doConnectATypeImpl(reactService, startDate, endDate, count);

		return handleDataDto(dataDto, "ATypeRates Table By Date Range " + startDate + " - " + endDate);
	}

	/**
	 * Catch DataExistsException from new Transaction established in
	 * dataService.save
	 * 
	 * @param dataDto
	 * @param msg
	 * @return
	 */
	private DataPackage handleDataDto(DataResponseDto dataDto, String msg) {
		DataPackage dataEntity = null;

		if (dataDto != null) {
			log.debug("Found {} to be saved.", msg);
			try {
				dataEntity = saveDataEntity(dataDto);
			} catch (DataExistsException d) {
				log.info("Persist process aborted! {}", d.getMessage(), msg);
			}
		} else {
			log.info("Not found {}. Nothing to be saved!", msg);
		}

		return dataEntity;
	}

	private DataPackage saveDataEntity(DataResponseDto dataDto) {
		DataPackage dataEntity = DataPackage.builder()
		.endPoint(dataDto.getEndPoint())
		.jsonData(dataDto.getJsonData())
		.url(dataDto.getUrl())
		.build();

		return dataService.save(dataEntity);
	}
}
