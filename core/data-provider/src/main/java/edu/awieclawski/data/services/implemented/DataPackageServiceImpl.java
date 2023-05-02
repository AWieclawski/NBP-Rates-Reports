package edu.awieclawski.data.services.implemented;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.data.daos.DataPackageRepository;
import edu.awieclawski.data.exceptions.DataExistsException;
import edu.awieclawski.data.exceptions.DataNotFoundException;
import edu.awieclawski.data.mappers.DataPackageMapper;
import edu.awieclawski.data.services.DataPackageService;
import edu.awieclawski.models.entities.DataPackage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
class DataPackageServiceImpl implements DataPackageService {

	private final DataPackageRepository dataPackageRepository;

	public static final String EXIST_ERR_MSG = "DataPackage already exists! ";
	public static final String NOT_FOUND_ERR_MSG = "DataPackage not found! ";
	public static final String NOT_IMPLEMENTED = "Not implemented yet!";

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public DataPackage save(DataPackage data) throws DataExistsException {
		List<DataPackage> list = dataPackageRepository.findByUrl(data.getUrl());

		if (!list.isEmpty()) {
			log.warn(MSG_DATA_URL_EXIST, data.getUrl());
			throw new DataExistsException(EXIST_ERR_MSG);
		}

		data = dataPackageRepository.save(data);
		log.debug("{} saved", data.getInfo());

		return data;
	}

	@Override
	@Transactional
	public void delete(DataPackageDto data) throws DataNotFoundException {
		List<DataPackage> list = dataPackageRepository.findByUrl(data.getUrl());

		if (list.isEmpty()) {
			log.warn(MSG_DATA_URL_NOT_EXIST, data.getUrl());
			throw new DataNotFoundException(NOT_FOUND_ERR_MSG);
		}

		dataPackageRepository.deleteById(data.getId());
		log.info("{} deleted", data.getInfo());
	}

	@Override
	@Transactional(readOnly = true)
	public List<DataPackageDto> findAll() {
		List<DataPackage> dataPackges = dataPackageRepository.findAll();

		if (!dataPackges.isEmpty()) {
			log.debug("Found {} data packages", dataPackges.size());
		} else {
			log.info("Any {}", NOT_FOUND_ERR_MSG);
		}

		return dataPackges.stream()
				.filter(Objects::nonNull)
				.map(DataPackageMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<DataPackage> findByCreatedAt(LocalDate date) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public List<DataPackage> findByConverted(Boolean isConverted) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public List<DataPackage> findByEndPoint(String endPoint) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public List<DataPackage> findByUrl(String url) {

		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public List<DataPackageDto> findAllNotConverted() {
		return dataPackageRepository.findByConvertedFalse().stream()
				.filter(Objects::nonNull)
				.map(DataPackageMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updateConverted(DataPackageDto data) {
		List<DataPackage> list = dataPackageRepository.findByUrl(data.getUrl());

		if (list.isEmpty()) {
			log.warn(MSG_DATA_URL_NOT_EXIST, data.getUrl());
			throw new DataNotFoundException(NOT_FOUND_ERR_MSG);
		}

		dataPackageRepository.updateConvertedFlag(data.getId());
		log.debug("{} updated as CONVERTED", data.getInfo());
	}

	@Override
	@Transactional
	public void updateProcessed(DataPackageDto data) {
		List<DataPackage> list = dataPackageRepository.findByUrl(data.getUrl());

		if (list.isEmpty()) {
			log.warn(MSG_DATA_URL_NOT_EXIST, data.getUrl());
			throw new DataNotFoundException(NOT_FOUND_ERR_MSG);
		}

		dataPackageRepository.updateProcessedFlag(data.getId());
		log.debug("{} updated as PROCESSED", data.getInfo());
	}

	@Override
	public boolean flushTransaction() {
		logTransactionStatus();
		return TransactionSynchronizationManager.isActualTransactionActive();
	}

	private void logTransactionStatus() {
		TransactionStatus status = null;

		try {
			status = TransactionAspectSupport.currentTransactionStatus();
		} catch (Exception e) {
			log.info("Transaction info error {}", e.getMessage());
		}

		if (status != null && !status.isCompleted()) {
			log.info("Transaction is to be flushed");
			status.flush();
		}
	}

	@Override
	public List<DataPackage> findByUrlLikeDaySingle(String endPoint, String date, String code) {
		return dataPackageRepository.findByUrlLikeDaySingle(endPoint, code, date);
	}

	@Override
	public List<DataPackage> findByUrlLikeRangeSingle(String endPoint, String code, String date, String dateEnd) {
		return dataPackageRepository.findByUrlLikeRangeSingle(endPoint, code, date, dateEnd);
	}

	@Override
	public List<DataPackage> findByUrlLikeDayTable(String endPoint, String date) {
		return dataPackageRepository.findByUrlLikeDayTable(endPoint, date);
	}

	@Override
	public List<DataPackage> findByUrlLikeRangeTable(String endPoint, String date, String dateEnd) {
		return dataPackageRepository.findByUrlLikeRangeTable(endPoint, date, dateEnd);
	}

}
