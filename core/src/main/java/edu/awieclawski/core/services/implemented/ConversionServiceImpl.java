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

import edu.awieclawski.commons.beans.NbpEndpointBeans;
import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.commons.dtos.rates.NbpARate;
import edu.awieclawski.commons.dtos.rates.NbpATableRate;
import edu.awieclawski.commons.dtos.responses.NbpATable;
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

	private final RateFacade rateHandler;
	private final DataPackageService dataService;
	private final NbpEndpointBeans endPoints;

	// static TypeReference mappers
	public final static NbpRatesMapper<NbpSingle<NbpARate>> mapperNbpSingleNbpARate =
			new NbpRatesMapper<>(new TypeReference<NbpSingle<NbpARate>>() {
			});
	public final static NbpRatesMapper<List<NbpATable<NbpATableRate>>> mapperListNbpATableNbpATableRate =
			new NbpRatesMapper<>(new TypeReference<List<NbpATable<NbpATableRate>>>() {
			});

	@Override
	public List<ExchangeRate> convertToExchangeRates(List<DataPackage> packages) {
		List<ExchangeRate> entities = new ArrayList<>();
		List<ExchangeRateTypeADto> dtoList = new ArrayList<>();
		Set<Currency> currencies = new HashSet<>();

		for (DataPackage data : packages) {

			try {
				dtoList.addAll(getExchangeRatesList(data));
			} catch (Exception e) {
				log.warn("ExchangeRate conversion error! Message: {}, cause: {}", e.getMessage(), e.getCause());
				continue;
			}

			if (CollectionUtils.isNotEmpty(dtoList)) {

				for (ExchangeRateTypeADto rate : dtoList) {
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

	@Override
	@Transactional
	public List<ExchangeRateTypeADto> getExchangeRatesList(DataPackage data) {
		List<ExchangeRateTypeADto> rates = new ArrayList<>();
		String endPoint = data.getEndPoint();

		if (Objects.equals(endPoints.ratesA, endPoint)) {
			rates = getNbpSingleWithNbpARate(data);
		} else if (Objects.equals(endPoints.aTableRate, endPoint)) {
			rates = getNbpSingleWithNbpATableRate(data);
		} else {
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
		CurrencyDto currency = rateHandler.getCurrencyDto(dto.getCode(), dto.getCurrency());

		if (dto.getRates() != null) {

			for (NbpARate subDto : dto.getRates()) {
				ExchangeRateTypeADto rate = rateHandler.getExchangeRateFromNbpARate(subDto, currency);
				rates.add(rate);
			}
		}

		return rates;
	}

	private List<ExchangeRateTypeADto> getNbpSingleWithNbpATableRate(DataPackage data) {
		List<ExchangeRateTypeADto> rates = new ArrayList<>();
		List<NbpATable<NbpATableRate>> dtoList = mapperListNbpATableNbpATableRate.map(data.getJsonData());

		for (NbpATable<NbpATableRate> dto : dtoList) {

			if (dto.getRates() != null) {

				for (NbpATableRate subDto : dto.getRates()) {
					CurrencyDto currency = rateHandler.getCurrencyDto(subDto.getCode(), subDto.getCurrency());
					ExchangeRateTypeADto rate = rateHandler.getExchangeRateFromNbpATableRate(subDto, currency, dto.getNo(),
							dto.getEffectiveDate());
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
	public ExchangeRate trySaveRate(ExchangeRateTypeADto rate) {

		try {
			return rateHandler.save(rate);
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
	public ExchangeRate trySaveRateWithCurrency(ExchangeRateTypeADto rate, Currency currency) {

		try {
			return rateHandler.save(rate, currency);
		} catch (EntityExistsException ignore) {
		}

		return null;
	}

}
