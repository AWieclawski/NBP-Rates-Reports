package edu.awieclawski.data.services.implemented;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;

import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public <T extends ExchangeRateDto> T save(T rateDto) {

		if (rateDto.getId() != null) {
			Optional<? extends ExchangeRate> optEntity = exchangeRateRepository.findById(rateDto.getId());

			if (optEntity.isPresent()) {
				log.warn("{} already exists!", optEntity.get().getInfo());
				throw new DataExistsException(EXIST_ERR_MSG, null);
			}
		}

		if (isRateDuplicated(rateDto)) {
			log.warn("{} not to be saved - the same entity exists!", rateDto.getInfo());
			throw new EntityExistsException(EXIST_ERR_MSG);
		}

		ExchangeRate entity = ExchangeRateMapper.toEntity(rateDto);
		entity = assignCurrency(entity);
		entity = exchangeRateRepository.save(entity);
		log.debug("{} saved", entity.getInfo());

		return (T) ExchangeRateMapper.toDto(entity);
	}

	/**
	 * Optimized ExchangeRate persistence to avoid Currency existence.
	 * EntityExistsException should be caught in caller. Could roll back the
	 * Transaction
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public <T extends ExchangeRateDto> T saveWithCurrency(T rateDto, CurrencyDto currency) {

		if (rateDto.getId() != null) {
			Optional<? extends ExchangeRate> optEntity = exchangeRateRepository.findById(rateDto.getId());

			if (optEntity.isPresent()) {
				log.warn("{} already exists!", optEntity.get().getInfo());
				throw new DataExistsException(EXIST_ERR_MSG, null);
			}
		}

		if (isRateDuplicated(rateDto)) {
			log.warn("{} already exists!", rateDto.getInfo());
			throw new EntityExistsException(EXIST_ERR_MSG);
		}

		ExchangeRate entity = ExchangeRateMapper.toEntity(rateDto);
		Currency currencyEntity = currencyService.findByCode(currency.getCode());
		entity.setCurrency(currencyEntity);
		entity = exchangeRateRepository.save(entity);
		log.debug("{} saved", entity.getInfo());

		return (T) ExchangeRateMapper.toDto(entity);
	}

	private <T extends ExchangeRate> T assignCurrency(T entity) {
		CurrencyDto currencyDto = CurrencyMapper.toDto(entity.getCurrency());
		Currency currencyEntity = null;

		if (currencyDto != null) {

			try {
				currencyEntity = currencyService.trySaveCurrencyDto(currencyDto);
			} catch (EntityExistsException ignore) {
			}

			if (currencyEntity == null) {
				currencyEntity = currencyService.findByCode(currencyDto.getCode());
			}

			entity.setCurrency(currencyEntity);
		}

		return entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T extends ExchangeRateDto> List<T> findAllByType(NbpType nbpType) {
		List<? extends ExchangeRate> exchangeRates;
		nbpType = typeDefault(nbpType);

		if (NbpType.A.equals(nbpType)) {
			exchangeRates = ratesTypeARepository.findAll();
		} else if (NbpType.B.equals(nbpType)) {
			exchangeRates = ratesTypeARepository.findAll();
		} else if (NbpType.C.equals(nbpType)) {
			exchangeRates = ratesTypeARepository.findAll();
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		if (!exchangeRates.isEmpty()) {
			log.debug("Found {} Exchange Rates", exchangeRates.size());
		} else {
			log.info("Any {}", NOT_FOUND_ERR_MSG);
		}

		return (List<T>) exchangeRates.stream()
				.filter(Objects::nonNull)
				.map(ExchangeRateMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public ExchangeRateDto findById(Long id) {
		return ExchangeRateMapper.toDto(exchangeRateRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException(NOT_FOUND_ERR_MSG, null)));
	}

	@Override
	@Transactional(readOnly = true)
	public ExchangeRateDto findByCodePublishedTable(String code, LocalDate published, String table) {
		List<ExchangeRate> exchangeRates = exchangeRateRepository.findByCodePublishedTable(code, published, table);

		if (exchangeRates.size() == 1) {
			return ExchangeRateMapper.toDto(exchangeRates.get(0));
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
		log.debug("{} deleted", foundOptional.get().getInfo());
	}

	@Override
	public <T extends ExchangeRateDto> boolean isRateDuplicated(T dto) {
		return exchangeRateRepository.existsByCurrencyCodeAndNbpTable(dto.getCurrency().getCode(), dto.getNbpTable());
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * RateNotFoundException could roll back Transaction
	 * 
	 * @param exchangeRate
	 * @return
	 */
	@Transactional(readOnly = true)
	public <T extends ExchangeRateDto> T findDuplicate(T dto) {
		ExchangeRateDto entity = null;

		try {
			entity = findByCodePublishedTable(dto.getCurrency().getCode(), dto.getPublished(), dto.getNbpTable());
		} catch (RateNotFoundException ignore) {
		}

		return (T) entity;
	}

	@Transactional(readOnly = true)
	public boolean findIfExistsByCodeBeforeDateAndType(String code, LocalDate published, NbpType nbpType) {
		Boolean ifExists = false;
		nbpType = typeDefault(nbpType);

		if (NbpType.A.equals(nbpType)) {
			ifExists = ratesTypeARepository.findIfExistsByCodeBeforeDate(code, published);
		} else if (NbpType.B.equals(nbpType)) {
			ifExists = ratesTypeBRepository.findIfExistsByCodeBeforeDate(code, published);
		} else if (NbpType.C.equals(nbpType)) {
			ifExists = ratesTypeCRepository.findIfExistsByCodeBeforeDate(code, published);
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		String partMsg = "Not found any result";
		if (ifExists) {
			partMsg = "Found results";
		}
		log.info("{} for params: [currency code={}], [published={}], [nbp table type={}]", partMsg, code, published,
				nbpType);
		return ifExists;
	}

	@Transactional(readOnly = true)
	public boolean findIfExistsBeforeDateAndType(LocalDate published, NbpType nbpType) {
		Boolean ifExists = false;
		nbpType = typeDefault(nbpType);

		if (NbpType.A.equals(nbpType)) {
			ifExists = ratesTypeARepository.findIfExistsBeforeDate(published);
		} else if (NbpType.B.equals(nbpType)) {
			ifExists = ratesTypeBRepository.findIfExistsBeforeDate(published);
		} else if (NbpType.C.equals(nbpType)) {
			ifExists = ratesTypeCRepository.findIfExistsBeforeDate(published);
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		String partMsg = "Not found any result";
		if (ifExists) {
			partMsg = "Found results";
		}
		log.info("{} for params: [published={}], [nbp table type={}]", partMsg, published, nbpType);
		return ifExists;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T extends ExchangeRateDto> List<T> getByDatesRangeAndSymbolListAndType(LocalDate publishedStart,
			LocalDate publishedEnd, List<String> codes, NbpType nbpType) {
		List<? extends ExchangeRate> ratesList;
		nbpType = typeDefault(nbpType);

		if (NbpType.A.equals(nbpType)) {
			ratesList = ratesTypeARepository.findByDatesRangeAndSymbolList(publishedStart, publishedEnd,
					codes);
		} else if (NbpType.B.equals(nbpType)) {
			ratesList = ratesTypeBRepository.findByDatesRangeAndSymbolList(publishedStart, publishedEnd,
					codes);
		} else if (NbpType.C.equals(nbpType)) {
			ratesList = ratesTypeCRepository.findByDatesRangeAndSymbolList(publishedStart, publishedEnd,
					codes);
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		String partMsg = "Not found any";
		if (!CollectionUtils.isEmpty(ratesList)) {
			partMsg = "Found " + ratesList.size();
		}
		log.info(
				"{} rates for params: [start date publ.={}], [end date publ.={}], [currency codes={}], [nbp table type={}]",
				partMsg, publishedStart, publishedEnd, codes, nbpType);

		return (List<T>) ratesList.stream()
				.filter(Objects::nonNull)
				.map(ExchangeRateMapper::toDto)
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T extends ExchangeRateDto> List<T> getByDatesRangeAndType(LocalDate publishedStart, LocalDate publishedEnd,
			NbpType nbpType) {
		List<? extends ExchangeRate> ratesList;
		nbpType = typeDefault(nbpType);

		if (NbpType.A.equals(nbpType)) {
			ratesList = ratesTypeARepository.findByDatesRange(publishedStart, publishedEnd);
		} else if (NbpType.B.equals(nbpType)) {
			ratesList = ratesTypeBRepository.findByDatesRange(publishedStart, publishedEnd);
		} else if (NbpType.C.equals(nbpType)) {
			ratesList = ratesTypeCRepository.findByDatesRange(publishedStart, publishedEnd);
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		String partMsg = "Not found any";
		if (!CollectionUtils.isEmpty(ratesList)) {
			partMsg = "Found " + ratesList.size();
		}
		log.info("{} rates for params: [start date publ.={}], [end date publ.={}], [nbp table type={}]",
				partMsg, publishedStart, publishedEnd, nbpType);

		return (List<T>) ratesList.stream()
				.filter(Objects::nonNull)
				.map(ExchangeRateMapper::toDto)
				.collect(Collectors.toList());
	}

	private NbpType typeDefault(NbpType nbpType) {
		if (Objects.isNull(nbpType)) {
			nbpType = NbpType.A;
			log.warn("No NBP table type submitted. Established default type {}.", nbpType.getConstant());
		}
		return nbpType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExchangeRateDto> List<T> getByCodeAndDatesRangeAndType(String code, LocalDate publishedStart,
			LocalDate publishedEnd, NbpType nbpType) {
		List<? extends ExchangeRate> ratesList;
		nbpType = typeDefault(nbpType);

		if (NbpType.A.equals(nbpType)) {
			ratesList = ratesTypeARepository.findByCodeAndDatesRange(code, publishedStart, publishedEnd);
		} else if (NbpType.B.equals(nbpType)) {
			ratesList = ratesTypeBRepository.findByCodeAndDatesRange(code, publishedStart, publishedEnd);
		} else if (NbpType.C.equals(nbpType)) {
			ratesList = ratesTypeCRepository.findByCodeAndDatesRange(code, publishedStart, publishedEnd);
		} else {
			throw new RateNotFoundException("Ralated NBP type table not supported: " + nbpType);
		}

		String partMsg = "Not found any";
		if (!CollectionUtils.isEmpty(ratesList)) {
			partMsg = "Found " + ratesList.size();
		}
		log.info("{} rates for params: [start date publ.={}], [end date publ.={}], [nbp table type={}]",
				partMsg, publishedStart, publishedEnd, nbpType);

		return (List<T>) ratesList.stream()
				.filter(Objects::nonNull)
				.map(ExchangeRateMapper::toDto)
				.collect(Collectors.toList());
	}

}
