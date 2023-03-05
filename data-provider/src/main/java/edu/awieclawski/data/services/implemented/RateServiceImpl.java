package edu.awieclawski.data.services.implemented;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.data.daos.ExchangeRateRepository;
import edu.awieclawski.data.daos.implemented.RatesTypeARepository;
import edu.awieclawski.data.daos.implemented.RatesTypeBRepository;
import edu.awieclawski.data.daos.implemented.RatesTypeCRepository;
import edu.awieclawski.data.exceptions.DataExistsException;
import edu.awieclawski.data.exceptions.DataNotFoundException;
import edu.awieclawski.data.exceptions.IllegalResultsException;
import edu.awieclawski.data.exceptions.RateNotFoundException;
import edu.awieclawski.data.mappers.CurrencyMapper;
import edu.awieclawski.data.mappers.ExchangeRateMapper;
import edu.awieclawski.data.services.CurrencyService;
import edu.awieclawski.data.services.RateService;
import edu.awieclawski.models.entities.Currency;
import edu.awieclawski.models.entities.ExchangeRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
class RateServiceImpl implements RateService {

	private final ExchangeRateRepository exchangeRateRepository;
	private final RatesTypeARepository ratesTypeARepository;
	private final RatesTypeBRepository ratesTypeBRepository;
	private final RatesTypeCRepository ratesTypeCRepository;

	protected final CurrencyService currencyService;

	public static final String EXIST_ERR_MSG = "ExchangeRate already exists! ";
	public static final String NOT_FOUND_ERR_MSG = "ExchangeRate not found! ";

	/**
	 * EntityExistsException should be caught in caller. Could roll back the
	 * Transaction
	 */
	@Override
	@Transactional
	public <T extends ExchangeRateDto> ExchangeRate save(T rateDto) {

		if (rateDto.getId() != null) {
			Optional<? extends ExchangeRate> optEntity = exchangeRateRepository.findById(rateDto.getId());

			if (optEntity.isPresent()) {
				log.info("{} already exists!", optEntity.get().getInfo());
				throw new DataExistsException(EXIST_ERR_MSG, null);
			}
		}

		if (isRateDuplicated(rateDto)) {
			log.warn("{} not to be saved - the same entity exists!", rateDto.getInfo());
			throw new EntityExistsException(EXIST_ERR_MSG);
		}

		ExchangeRate entity = ExchangeRateMapper.toEntity(rateDto);
		assignCurrency(entity);
		entity = exchangeRateRepository.save(entity);
		log.info("{} saved", entity.getInfo());

		return entity;
	}

	/**
	 * Optimized ExchangeRate persistence to avoid Currency existence.
	 * EntityExistsException should be caught in caller. Could roll back the
	 * Transaction
	 */
	@Override
	@Transactional
	public <T extends ExchangeRateDto> ExchangeRate saveWithCurrency(T rateDto, Currency currency) {

		if (rateDto.getId() != null) {
			Optional<? extends ExchangeRate> optEntity = exchangeRateRepository.findById(rateDto.getId());

			if (optEntity.isPresent()) {
				log.info("{} already exists!", optEntity.get().getInfo());
				throw new DataExistsException(EXIST_ERR_MSG, null);
			}
		}

		if (isRateDuplicated(rateDto)) {
			log.warn("{} already exists!", rateDto.getInfo());
			throw new EntityExistsException(EXIST_ERR_MSG);
		}

		ExchangeRate entity = ExchangeRateMapper.toEntity(rateDto);
		entity.setCurrency(currency);
		entity = exchangeRateRepository.save(entity);
		log.info("{} saved", entity.getInfo());

		return entity;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public <T extends ExchangeRate> T assignCurrency(T entity) {
		CurrencyDto currencyDto = CurrencyMapper.toDto(entity.getCurrency());
		Currency currency = null;

		if (currencyDto != null) {

			try {
				currency = currencyService.trySaveCurrencyDto(currencyDto);
			} catch (EntityExistsException ignore) {
			}

			if (currency == null) {
				currency = currencyService.findByCode(currencyDto.getCode());
			}

			entity.setCurrency(currency);
		}

		return entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T extends ExchangeRate> List<T> findAllByType(NbpType nbpType) {
		List<T> exchangeRates;

		if (Objects.isNull(nbpType) || NbpType.A.equals(nbpType)) {
			exchangeRates = (List<T>) ratesTypeARepository.findAll();
		} else if (NbpType.B.equals(nbpType)) {
			exchangeRates = (List<T>) ratesTypeARepository.findAll();
		} else if (NbpType.C.equals(nbpType)) {
			exchangeRates = (List<T>) ratesTypeARepository.findAll();
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		if (!exchangeRates.isEmpty()) {
			log.debug("Found {} Exchange Rates", exchangeRates.size());
		} else {
			log.info("Any {}", NOT_FOUND_ERR_MSG);
		}

		return exchangeRates;
	}

	@Override
	@Transactional(readOnly = true)
	public ExchangeRate findById(Long id) {
		return exchangeRateRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException(NOT_FOUND_ERR_MSG, null));
	}

	@Override
	@Transactional(readOnly = true)
	public ExchangeRate findByCodePublishedTable(String code, LocalDate published, String table) {
		List<ExchangeRate> exchangeRates = exchangeRateRepository.findByCodePublishedTable(code, published, table);

		if (exchangeRates.size() == 1) {
			return exchangeRates.get(0);
		} else if (exchangeRates.size() > 1) {
			log.warn("Found {} too many Exchange Rates by code {} and published {}", exchangeRates.size(), code,
					published);
			throw new IllegalResultsException("Too many entities found!", null);
		} else {
			log.warn("Any {} by code {} and validFrom {}", NOT_FOUND_ERR_MSG, code, published);
			throw new RateNotFoundException(NOT_FOUND_ERR_MSG);
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Optional<ExchangeRate> foundOptional = exchangeRateRepository.findById(id);

		if (foundOptional.isEmpty()) {
			log.warn("ExchangeRate [id={}] not exists!", id);
			throw new DataNotFoundException(NOT_FOUND_ERR_MSG, null);
		}

		exchangeRateRepository.delete(foundOptional.get());
		log.info("{} deleted", foundOptional.get().getInfo());
	}

	@Override
	public <T extends ExchangeRateDto> boolean isRateDuplicated(T dto) {
		return exchangeRateRepository.existsByCurrencyCodeAndNbpTable(dto.getCurrency().getCode(), dto.getNbpTable());
	}

	@Override
	/**
	 * RateNotFoundException could roll back Transaction
	 * 
	 * @param exchangeRate
	 * @return
	 */
	@Transactional(readOnly = true)
	public <T extends ExchangeRateDto> ExchangeRate findDuplicate(T dto) {
		ExchangeRate entity = ExchangeRateMapper.toRawEntity(dto);

		try {
			entity = findByCodePublishedTable(dto.getCurrency().getCode(), dto.getPublished(), dto.getNbpTable());
		} catch (RateNotFoundException ignore) {
		}

		return entity;
	}

	@Transactional(readOnly = true)
	public boolean findIfExistsByCodeAndDateAndType(String code, LocalDate published, NbpType nbpType) {
		Boolean ifExists = false;

		if (Objects.isNull(nbpType)) {
			nbpType = NbpType.A;
			log.warn("No NBP table type submitted. Established {}.", nbpType.getClass());
		}

		if (NbpType.A.equals(nbpType)) {
			ifExists = ratesTypeARepository.findIfExistsByCodeDate(code, published);
		} else if (NbpType.B.equals(nbpType)) {
			ifExists = ratesTypeBRepository.findIfExistsByCodeDate(code, published);
		} else if (NbpType.C.equals(nbpType)) {
			ifExists = ratesTypeCRepository.findIfExistsByCodeDate(code, published);
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		log.info("Found any result: {} for params: [currency code={}], [published={}], [nbp table type={}]", ifExists,
				code, published, nbpType);
		return ifExists;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T extends ExchangeRate> List<T> getByDatesRangeAndSymbolListAndType(LocalDate publishedStart,
			LocalDate publishedEnd, List<String> codes, NbpType nbpType) {
		List<T> ratesList;

		if (Objects.isNull(nbpType)) {
			nbpType = NbpType.A;
			log.warn("No NBP table type submitted. Established {}.", nbpType.getClass());
		}

		if (NbpType.A.equals(nbpType)) {
			ratesList = (List<T>) ratesTypeARepository.findByDatesRangeAndSymbolList(publishedStart, publishedEnd,
					codes);
		} else if (NbpType.B.equals(nbpType)) {
			ratesList = (List<T>) ratesTypeBRepository.findByDatesRangeAndSymbolList(publishedStart, publishedEnd,
					codes);
		} else if (NbpType.C.equals(nbpType)) {
			ratesList = (List<T>) ratesTypeCRepository.findByDatesRangeAndSymbolList(publishedStart, publishedEnd,
					codes);
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		log.info(
				"Found {} rates for params: [start date publ.={}], [end date publ.={}], [currency codes={}], [nbp table type={}]",
				ratesList.size(), publishedStart, publishedEnd, codes, nbpType);
		return ratesList;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T extends ExchangeRate> List<T> getByDatesRangeAndType(LocalDate publishedStart, LocalDate publishedEnd,
			NbpType nbpType) {
		List<T> ratesList;

		if (Objects.isNull(nbpType) || NbpType.A.equals(nbpType)) {
			ratesList = (List<T>) ratesTypeARepository.findByDatesRange(publishedStart, publishedEnd);
		} else if (NbpType.B.equals(nbpType)) {
			ratesList = (List<T>) ratesTypeBRepository.findByDatesRange(publishedStart, publishedEnd);
		} else if (NbpType.C.equals(nbpType)) {
			ratesList = (List<T>) ratesTypeCRepository.findByDatesRange(publishedStart, publishedEnd);
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		log.info("Found {} rates for params: [start date publ.={}], [end date publ.={}], [nbp table type={}]",
				ratesList.size(), publishedStart, publishedEnd, nbpType);

		return ratesList;
	}

}
