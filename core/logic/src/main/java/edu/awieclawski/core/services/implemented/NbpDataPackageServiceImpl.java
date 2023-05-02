package edu.awieclawski.core.services.implemented;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.core.bases.ConnectorsFactory;
import edu.awieclawski.core.dtos.ConnResultDto;
import edu.awieclawski.core.services.NbpDataPackageService;
import edu.awieclawski.data.exceptions.DataExistsException;
import edu.awieclawski.data.mappers.DataPackageMapper;
import edu.awieclawski.data.services.DataPackageService;
import edu.awieclawski.models.entities.DataPackage;
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
	public Pair<Boolean, DataPackageDto> getATypeRateByDateAndSymbolAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnecATypeImpl(reactService, result.getStartDate(), result.getCurrSymb(),
				result.getCounter());

		return handleDataDto(dataDto,
				"ATypeRate By Date " + result.getStartDate() + " And Symbol " + result.getCurrSymb());
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackageDto> getATypeRatesByDateRangeAndSymbolAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnectATypeImpl(reactService, result.getStartDate(), result.getEndDate(),
				result.getCurrSymb(), result.getCounter());

		return handleDataDto(dataDto,
				"ATypeRate By Date Range " + result.getStartDate() + " - " + result.getEndDate() + " And Symbol "
						+ result.getCurrSymb());
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackageDto> getCTypeRateByDateAndSymbolAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnecCTypeImpl(reactService, result.getStartDate(), result.getCurrSymb(),
				result.getCounter());

		return handleDataDto(dataDto,
				"CTypeRate By Date " + result.getStartDate() + " And Symbol " + result.getCurrSymb());
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackageDto> getCTypeRatesByDateRangeAndSymbolAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnectCTypeImpl(reactService, result.getStartDate(), result.getEndDate(),
				result.getCurrSymb(), result.getCounter());

		return handleDataDto(dataDto,
				"CTypeRate By Date Range " + result.getStartDate() + " - " + result.getEndDate() + " And Symbol "
						+ result.getCurrSymb());
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackageDto> getATypeTableByDateAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnectATypeImpl(reactService, result.getStartDate(), result.getCounter());

		return handleDataDto(dataDto, "ATypeRates Table By Date " + result.getStartDate());
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackageDto> getATypeTableByDateRangeAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnectATypeImpl(reactService, result.getStartDate(), result.getEndDate(),
				result.getCounter());

		return handleDataDto(dataDto,
				"ATypeRates Table By Date Range " + result.getStartDate() + " - " + result.getEndDate());
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackageDto> getBTypeTableByDateAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnectBTypeImpl(reactService, result.getStartDate(), result.getCounter());

		return handleDataDto(dataDto, "BTypeRates Table By Date " + result.getStartDate());
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackageDto> getBTypeTableByDateRangeAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnectBTypeImpl(reactService, result.getStartDate(), result.getEndDate(),
				result.getCounter());

		return handleDataDto(dataDto,
				"BTypeRates Table By Date Range " + result.getStartDate() + " - " + result.getEndDate());
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackageDto> getCTypeTableByDateAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnectCTypeImpl(reactService, result.getStartDate(), result.getCounter());

		return handleDataDto(dataDto, "CTypeRates Table By Date " + result.getStartDate());
	}

	@Override
	@Transactional
	public Pair<Boolean, DataPackageDto> getCTypeTableByDateRangeAndSave(ConnResultDto result) {
		DataPackageDto dataDto = doConnectCTypeImpl(reactService, result.getStartDate(), result.getEndDate(),
				result.getCounter());

		return handleDataDto(dataDto,
				"CTypeRates Table By Date Range " + result.getStartDate() + " - " + result.getEndDate());
	}

	/**
	 * Catch DataExistsException from new Transaction established in
	 * dataService.save
	 * 
	 * @param dataDto
	 * @param msg
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Pair<Boolean, DataPackageDto> handleDataDto(DataPackageDto dataDto, String msg) {
		DataPackageDto dto = null;
		Boolean dtoExists = false;

		if (dataDto != null) {
			dtoExists = true;
			log.debug("Found {} to be saved.", msg);
			try {
				dto = DataPackageMapper.toDto(saveDataEntity(dataDto));
			} catch (DataExistsException d) {
				log.info("Persist process aborted! {}", d.getMessage(), msg);
			}
		} else {
			log.info("Not found {}. Nothing to be saved!", msg);
		}

		return Pair.of(dtoExists, dto);
	}

	private DataPackage saveDataEntity(DataPackageDto dataDto) {
		DataPackage dataEntity = DataPackage.builder()
				.endPoint(dataDto.getEndPoint())
				.jsonData(dataDto.getJsonData())
				.url(dataDto.getUrl())
				.build();

		return dataService.save(dataEntity);
	}

	@Override
	public List<DataPackageDto> getByUrlLikeDaySingle(String endPoint, String code, LocalDate date) {
		String sDate = date.format(reactService.getDateFormat());
		return dataService.findByUrlLikeDaySingle(endPoint, code, sDate).stream()
				.filter(Objects::nonNull)
				.map(DataPackageMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<DataPackageDto> getByUrlLikeRangeSingle(String endPoint, String code, LocalDate date,
			LocalDate dateEnd) {
		String sDate = date.format(reactService.getDateFormat());
		String sDateEnd = dateEnd.format(reactService.getDateFormat());
		return dataService.findByUrlLikeRangeSingle(endPoint, code, sDate, sDateEnd).stream()
				.filter(Objects::nonNull)
				.map(DataPackageMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<DataPackageDto> getByUrlLikeDayTable(String endPoint, LocalDate date) {
		String sDate = date.format(reactService.getDateFormat());
		return dataService.findByUrlLikeDayTable(endPoint, sDate).stream()
				.filter(Objects::nonNull)
				.map(DataPackageMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<DataPackageDto> getByUrlLikeRangeTable(String endPoint, LocalDate date, LocalDate dateEnd) {
		String sDate = date.format(reactService.getDateFormat());
		String sDateEnd = dateEnd.format(reactService.getDateFormat());
		return dataService.findByUrlLikeRangeTable(endPoint, sDate, sDateEnd).stream()
				.filter(Objects::nonNull)
				.map(DataPackageMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<DataPackageDto> getDistinctPackages(List<DataPackageDto> packages) {
		return packages.stream().filter(Objects::nonNull)
				.filter(distinctByKey(DataPackageDto::getUrl))
				.collect(Collectors.toList());
	}

	@Override
	public void delete(DataPackageDto data) {
		dataService.delete(data);
	}

	@Override
	public List<DataPackageDto> findAll() {
		return dataService.findAll();
	}

}
