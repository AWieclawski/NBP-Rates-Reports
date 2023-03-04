package edu.awieclawski.core.services.implemented;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityExistsException;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;

import edu.awieclawski.commons.beans.NbpEndPointElements;
import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeCDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.rates.NbpARate;
import edu.awieclawski.commons.dtos.rates.NbpATableRate;
import edu.awieclawski.commons.dtos.rates.NbpCRate;
import edu.awieclawski.commons.dtos.rates.NbpCTableRate;
import edu.awieclawski.commons.dtos.responses.NbpATable;
import edu.awieclawski.commons.dtos.responses.NbpCTable;
import edu.awieclawski.commons.dtos.responses.NbpSingle;
import edu.awieclawski.converters.exceptions.ConverterException;
import edu.awieclawski.converters.mappers.NbpRatesMapper;
import edu.awieclawski.core.facades.RateFacade;
import edu.awieclawski.core.services.ConversionService;
import edu.awieclawski.data.services.DataPackageService;
import edu.awieclawski.models.entities.Currency;
import edu.awieclawski.models.entities.DataPackage;
import edu.awieclawski.models.entities.ExchangeRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class ConversionServiceImpl implements ConversionService {

	private final RateFacade rateFacade;
	private final DataPackageService dataService;
	private final NbpEndPointElements endPoints;

	// static TypeReference mappers
	public final static NbpRatesMapper<NbpSingle<NbpARate>> mapperNbpSingleNbpARate = new NbpRatesMapper<>(
			new TypeReference<NbpSingle<NbpARate>>() {
			});
	public final static NbpRatesMapper<List<NbpATable<NbpATableRate>>> mapperListNbpATableNbpATableRate = new NbpRatesMapper<>(
			new TypeReference<List<NbpATable<NbpATableRate>>>() {
			});
	public final static NbpRatesMapper<NbpSingle<NbpCRate>> mapperNbpSingleNbpCRate = new NbpRatesMapper<>(
			new TypeReference<NbpSingle<NbpCRate>>() {
			});
	public final static NbpRatesMapper<List<NbpCTable<NbpCTableRate>>> mapperListNbpCTableNbpCTableRate = new NbpRatesMapper<>(
			new TypeReference<List<NbpCTable<NbpCTableRate>>>() {
			});

	@Override
	public List<ExchangeRate> convertToExchangeRates(List<DataPackage> packages) {
		List<ExchangeRate> entities = new ArrayList<>();
		List<? extends ExchangeRateDto> dtoList = new ArrayList<>();
		Set<Currency> currencies = new HashSet<>();

		for (DataPackage data : packages) {

			try {
				dtoList.addAll(getExchangeRatesList(data));
			} catch (Exception e) {
				log.warn("ExchangeRate conversion error! Message: {}, cause: {}", e.getMessage(), e.getCause());
				continue;
			}

			if (CollectionUtils.isNotEmpty(dtoList)) {

				for (ExchangeRateDto rate : dtoList) {
					ExchangeRate entity = null;

					try {
						String code = rate.getCurrency().getCode();
						Optional<Currency> optCurrency = getCurrencyByCode(currencies, code);

						if (optCurrency.isEmpty()) {
							entity = trySaveRate(rate);
						} else {
							entity = trySaveRateWithCurrency(rate, optCurrency.orElseThrow());
						}

						if (entity != null) {
							currencies.add(entity.getCurrency());
							entities.add(entity);
						} else {
							continue;
						}

					} catch (Exception e) {
						log.debug("ExchangeRate save error! Message: {}, cause: {}. May be ignored.", e.getMessage(),
								e.getCause());
						continue;
					}
				}
			}
			dataService.updateConverted(data);
		}

		return entities;
	}

	@Override
	@Transactional
	public List<ExchangeRate> convertNewDataPackagesAndSave() {
		List<DataPackage> foundEntities = new ArrayList<>();
		List<ExchangeRate> operatedEntities = new ArrayList<>();
		foundEntities.addAll(dataService.findAllNotConverted());

		if (foundEntities.size() > 0) {
			log.info("Found {} not converted Data package/s.", foundEntities.size());
			operatedEntities.addAll(convertToExchangeRates(foundEntities));
		} else {
			log.info("Not found any new Data package for conversion. ");
		}

		processedFlagsHandler(foundEntities);

		return operatedEntities;
	}

	private void processedFlagsHandler(List<DataPackage> processedEntities) {
		for (DataPackage data : processedEntities) {
			dataService.updateProcessed(data);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public <T extends ExchangeRateDto> List<T> getExchangeRatesList(DataPackage data) {
		List<T> rates = new ArrayList<>();
		String endPoint = data.getEndPoint();

		if (Objects.equals(endPoints.ratesA, endPoint)) {
			rates = (List<T>) getNbpSingleWithNbpARate(data);
		} else if (Objects.equals(endPoints.aTableRate, endPoint)) {
			rates = (List<T>) getNbpATableWithNbpATableRate(data);
		} else if (Objects.equals(endPoints.ratesC, endPoint)) {
			rates = (List<T>) getNbpSingleWithNbpCRate(data);
		} else if (Objects.equals(endPoints.cTableRate, endPoint)) {
			rates = (List<T>) getNbpCTableWithNbpCRate(data);
		}

		else {
			throw new ConverterException("Ralated NBP end-point not supported: " + endPoint);
		}

		return rates;
	}

	@Override
	@Transactional
	public List<ExchangeRate> convertEceptOmittedDataPackagesAndSave(List<DataPackage> ommittedPackages) {
		List<DataPackage> dataEntities = Collections.synchronizedList(new ArrayList<>());
		List<ExchangeRate> exchangeEntities = Collections.synchronizedList(new ArrayList<>());
		dataEntities = dataService.findAllNotConverted();
		dataEntities.removeAll(ommittedPackages);

		if (dataEntities.size() > 0) {
			log.info("Found {} not converted Data package/s.", dataEntities.size());
			exchangeEntities = convertToExchangeRates(dataEntities);
		} else {
			log.info("Not found any new Data package for conversion. ");
		}

		processedFlagsHandler(dataEntities);

		return exchangeEntities;
	}

	private List<ExchangeRateTypeADto> getNbpSingleWithNbpARate(DataPackage data) {
		List<ExchangeRateTypeADto> rates = new ArrayList<>();
		NbpSingle<NbpARate> dto = mapperNbpSingleNbpARate.map(data.getJsonData());
		CurrencyDto currency = rateFacade.getCurrencyDto(dto.getCode(), dto.getCurrency());

		if (dto.getRates() != null) {

			for (NbpARate subDto : dto.getRates()) {
				ExchangeRateTypeADto rate = rateFacade.getExchangeRateFromNbpARate(subDto, currency);
				rates.add(rate);
			}
		}

		return rates;
	}

	private List<ExchangeRateTypeCDto> getNbpSingleWithNbpCRate(DataPackage data) {
		List<ExchangeRateTypeCDto> rates = new ArrayList<>();
		NbpSingle<NbpCRate> dto = mapperNbpSingleNbpCRate.map(data.getJsonData());
		CurrencyDto currency = rateFacade.getCurrencyDto(dto.getCode(), dto.getCurrency());

		if (dto.getRates() != null) {
			for (NbpCRate subDto : dto.getRates()) {
				ExchangeRateTypeCDto rate = rateFacade.getExchangeRateFromNbpCRate(subDto, currency);
				rates.add(rate);
			}
		}

		return rates;
	}

	private List<ExchangeRateTypeADto> getNbpATableWithNbpATableRate(DataPackage data) {
		List<ExchangeRateTypeADto> rates = new ArrayList<>();
		List<NbpATable<NbpATableRate>> dtoList = mapperListNbpATableNbpATableRate.map(data.getJsonData());

		for (NbpATable<NbpATableRate> dto : dtoList) {

			if (dto.getRates() != null) {

				for (NbpATableRate subDto : dto.getRates()) {
					CurrencyDto currency = rateFacade.getCurrencyDto(subDto.getCode(), subDto.getCurrency());
					ExchangeRateTypeADto rate = rateFacade.getExchangeRateFromNbpATableRate(subDto, currency,
							dto.getNo(),
							dto.getEffectiveDate());
					rates.add(rate);
				}
			}
		}

		return rates;
	}

	private List<ExchangeRateTypeCDto> getNbpCTableWithNbpCRate(DataPackage data) {
		List<ExchangeRateTypeCDto> rates = new ArrayList<>();
		List<NbpCTable<NbpCTableRate>> dtoList = mapperListNbpCTableNbpCTableRate.map(data.getJsonData());

		for (NbpCTable<NbpCTableRate> dto : dtoList) {

			if (dto.getRates() != null) {

				for (NbpCTableRate subDto : dto.getRates()) {
					CurrencyDto currency = rateFacade.getCurrencyDto(subDto.getCode(), subDto.getCurrency());
					ExchangeRateTypeCDto rate = rateFacade.getExchangeRateFromNbpCTableRate(subDto, currency,
							dto.getNo(),
							dto.getEffectiveDate(),
							dto.getTradingDate());
					rates.add(rate);
				}
			}
		}

		return rates;
	}

	/**
	 * Could control EntityExistsException passed by RateHandleService from
	 * RateService
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public <T extends ExchangeRateDto> ExchangeRate trySaveRate(T rate) {

		try {
			return rateFacade.save(rate);
		} catch (EntityExistsException ignore) {
		}

		return null;
	}

	/**
	 * Could control EntityExistsException passed by RateHandleService from
	 * RateService
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public <T extends ExchangeRateDto> ExchangeRate trySaveRateWithCurrency(T rate, Currency currency) {

		try {
			return rateFacade.save(rate, currency);
		} catch (EntityExistsException ignore) {
		}

		return null;
	}

}
