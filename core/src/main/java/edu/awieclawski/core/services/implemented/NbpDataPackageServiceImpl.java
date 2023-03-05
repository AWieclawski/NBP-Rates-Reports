package edu.awieclawski.core.services.implemented;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.awieclawski.core.bases.ConnectorsFactory;
import edu.awieclawski.core.services.NbpDataPackageService;
import edu.awieclawski.data.exceptions.DataExistsException;
import edu.awieclawski.data.services.DataPackageService;
import edu.awieclawski.models.entities.DataPackage;
import edu.awieclawski.webclients.dtos.DataResponseDto;
import edu.awieclawski.webclients.services.NbpIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class NbpDataPackageServiceImpl extends ConnectorsFactory implements NbpDataPackageService {

	private final DataPackageService dataService;
	private final NbpIntegrationService reactService;

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getATypeRateByDateAndSymbolAndSave(LocalDate date, String currSymb, int count) {
		DataResponseDto dataDto = doConnecATypeImpl(reactService, date, currSymb, count);

		return handleDataDto(dataDto, "ATypeRate By Date " + date + " And Symbol " + currSymb);
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getATypeRatesByDateRangeAndSymbolAndSave(LocalDate startDate, LocalDate endDate,
			String currSymb, int count) {
		DataResponseDto dataDto = doConnectATypeImpl(reactService, startDate, endDate, currSymb, count);

		return handleDataDto(dataDto,
				"ATypeRate By Date Range " + startDate + " - " + endDate + " And Symbol " + currSymb);
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getCTypeRateByDateAndSymbolAndSave(LocalDate date, String currSymb, int count) {
		DataResponseDto dataDto = doConnecCTypeImpl(reactService, date, currSymb, count);

		return handleDataDto(dataDto, "CTypeRate By Date " + date + " And Symbol " + currSymb);
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getCTypeRatesByDateRangeAndSymbolAndSave(LocalDate startDate, LocalDate endDate,
			String currSymb, int count) {
		DataResponseDto dataDto = doConnectCTypeImpl(reactService, startDate, endDate, currSymb, count);

		return handleDataDto(dataDto,
				"CTypeRate By Date Range " + startDate + " - " + endDate + " And Symbol " + currSymb);
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getATypeTableByDateAndSave(LocalDate date, int count) {
		DataResponseDto dataDto = doConnectATypeImpl(reactService, date, count);

		return handleDataDto(dataDto, "ATypeRates Table By Date " + date);
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getATypeTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate,
			int count) {
		DataResponseDto dataDto = doConnectATypeImpl(reactService, startDate, endDate, count);

		return handleDataDto(dataDto, "ATypeRates Table By Date Range " + startDate + " - " + endDate);
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getBTypeTableByDateAndSave(LocalDate date, int count) {
		DataResponseDto dataDto = doConnectBTypeImpl(reactService, date, count);

		return handleDataDto(dataDto, "BTypeRates Table By Date " + date);
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getBTypeTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate,
			int count) {
		DataResponseDto dataDto = doConnectBTypeImpl(reactService, startDate, endDate, count);

		return handleDataDto(dataDto, "BTypeRates Table By Date Range " + startDate + " - " + endDate);
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getCTypeTableByDateAndSave(LocalDate date, int count) {
		DataResponseDto dataDto = doConnectCTypeImpl(reactService, date, count);

		return handleDataDto(dataDto, "CTypeRates Table By Date " + date);
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackage> getCTypeTableByDateRangeAndSave(LocalDate startDate, LocalDate endDate,
			int count) {
		DataResponseDto dataDto = doConnectCTypeImpl(reactService, startDate, endDate, count);

		return handleDataDto(dataDto, "CTypeRates Table By Date Range " + startDate + " - " + endDate);
	}

	/**
	 * Catch DataExistsException from new Transaction established in
	 * dataService.save
	 * 
	 * @param dataDto
	 * @param msg
	 * @return
	 */
	private Pair<Boolean, DataPackage> handleDataDto(DataResponseDto dataDto, String msg) {
		DataPackage dataEntity = null;
		Boolean dtoExists = false;

		if (dataDto != null) {
			dtoExists = true;
			log.debug("Found {} to be saved.", msg);
			try {
				dataEntity = saveDataEntity(dataDto);
			} catch (DataExistsException d) {
				log.info("Persist process aborted! {}", d.getMessage(), msg);
			}
		} else {
			log.info("Not found {}. Nothing to be saved!", msg);
		}

		return Pair.of(dtoExists, dataEntity);
	}

	private DataPackage saveDataEntity(DataResponseDto dataDto) {
		DataPackage dataEntity = DataPackage.builder()
				.endPoint(dataDto.getEndPoint())
				.jsonData(dataDto.getJsonData())
				.url(dataDto.getUrl())
				.build();

		return dataService.save(dataEntity);
	}

	@Override
	public List<DataPackage> getByUrlLikeDaySingle(String endPoint, String code, LocalDate date) {
		String sDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		return dataService.findByUrlLikeDaySingle(endPoint, code, sDate);
	}

	@Override
	public List<DataPackage> getByUrlLikeRangeSingle(String endPoint, String code, LocalDate date, LocalDate dateEnd) {
		String sDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String sDateEnd = dateEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		return dataService.findByUrlLikeRangeSingle(endPoint, code, sDate, sDateEnd);
	}

	@Override
	public List<DataPackage> getByUrlLikeDayTable(String endPoint, LocalDate date) {
		String sDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		return dataService.findByUrlLikeDayTable(endPoint, sDate);
	}

	@Override
	public List<DataPackage> getByUrlLikeRangeTable(String endPoint, LocalDate date, LocalDate dateEnd) {
		String sDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String sDateEnd = dateEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		return dataService.findByUrlLikeRangeTable(endPoint, sDate, sDateEnd);
	}
}
