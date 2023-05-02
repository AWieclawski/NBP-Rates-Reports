package edu.awieclawski.data.services.implemented;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.data.daos.CurrencyRepository;
import edu.awieclawski.data.exceptions.CurrencyNotFoundException;
import edu.awieclawski.data.exceptions.DataExistsException;
import edu.awieclawski.data.exceptions.DataNotFoundException;
import edu.awieclawski.data.exceptions.IllegalResultsException;
import edu.awieclawski.data.mappers.CurrencyMapper;
import edu.awieclawski.data.services.CurrencyService;
import edu.awieclawski.models.entities.Currency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
class CurrencyServiceImpl implements CurrencyService {

	private final CurrencyRepository currencyRepository;

	public final static String EXIST_ERR_MSG = "Currency already exists! ";
	public final static String NOT_FOUND_ERR_MSG = "Currency not found! ";

	@Override
	@Transactional
	public Currency save(CurrencyDto currencyDto) {

		if (currencyDto.getId() != null) {
			Optional<Currency> foundEntity = currencyRepository.findById(currencyDto.getId());

			if (foundEntity.isPresent()) {
				log.warn("{} already exists!", foundEntity.get().getInfo());
				throw new DataExistsException(EXIST_ERR_MSG);
			}
		}

		if (isCurrencyDuplicatedByCode(currencyDto.getCode())) {
			log.warn("{} already exists!", currencyDto.getInfo());
			throw new EntityExistsException(EXIST_ERR_MSG);
		}

		Currency entity = CurrencyMapper.toEntity(currencyDto);
		entity = currencyRepository.save(entity);
		log.debug("{} saved", entity.getInfo());

		return entity;
	}

	/**
	 * EntityExistsException should be caught in caller. Requires New Transaction
	 * Propagation prevents to roll back Transaction
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Currency trySaveCurrencyDto(CurrencyDto currency) {
		return save(currency);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Currency> findAll() {
		List<Currency> currencies = currencyRepository.findAll();

		if (currencies.size() > 0) {
			log.debug("Found {} currencies", currencies.size());
		} else {
			log.info("Any {}", NOT_FOUND_ERR_MSG);
		}

		return currencies;
	}

	@Override
	@Transactional(readOnly = true)
	public Currency findById(Long id) {
		return currencyRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException(NOT_FOUND_ERR_MSG));
	}

	@Override
	@Transactional(readOnly = true)
	public Currency findByCode(String code) {
		List<Currency> currencies = currencyRepository.findByCode(code);

		if (currencies.size() == 1) {
			return currencies.get(0);
		} else if (currencies.size() > 1) {
			log.warn("Found {} - too many Currencies by code {} ", currencies.size(), code);
			throw new IllegalResultsException("Too many entities found!");
		} else {
			log.debug("Any {} By code {} ", NOT_FOUND_ERR_MSG, code);
			throw new CurrencyNotFoundException(NOT_FOUND_ERR_MSG);
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Optional<Currency> foundOptional = currencyRepository.findById(id);

		if (foundOptional.isEmpty()) {
			log.warn("Currency [id={}] not exists!", id);
			throw new DataNotFoundException(NOT_FOUND_ERR_MSG);
		}

		currencyRepository.delete(foundOptional.get());
		log.debug("{} deleted", foundOptional.get().getInfo());
	}

	public boolean isCurrencyDuplicatedByCode(String code) {
		return currencyRepository.existsCurrencyByCode(code);
	}

	/**
	 * CurrencyNotFoundException could roll back Transaction
	 * 
	 * @param currencyDto
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public Currency findDuplicate(CurrencyDto currencyDto) {
		Currency entity = null;

		try {
			entity = findByCode(currencyDto.getCode());
		} catch (CurrencyNotFoundException ignore) {
		}

		return entity;
	}

}
