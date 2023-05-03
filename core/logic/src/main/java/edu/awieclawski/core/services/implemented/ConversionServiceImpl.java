package edu.awieclawski.core.services.implemented;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityExistsException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import edu.awieclawski.commons.beans.NbpConnectionElements;
import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeBDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeCDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.rates.NbpARate;
import edu.awieclawski.commons.dtos.rates.NbpATableRate;
import edu.awieclawski.commons.dtos.rates.NbpBTableRate;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class ConversionServiceImpl implements ConversionService {

	private final RateFacade rateFacade;
	private final DataPackageService dataService;
	private final NbpConnectionElements endPoints;

	// static TypeReference mappers
	public static final NbpRatesMapper<NbpSingle<NbpARate>> mapperNbpSingleNbpARate = new NbpRatesMapper<>(
			new TypeReference<NbpSingle<NbpARate>>() {
			});
	public static final NbpRatesMapper<List<NbpATable<NbpATableRate>>> mapperListNbpATableNbpATableRate = new NbpRatesMapper<>(
			new TypeReference<List<NbpATable<NbpATableRate>>>() {
			});
	public static final NbpRatesMapper<NbpSingle<NbpCRate>> mapperNbpSingleNbpCRate = new NbpRatesMapper<>(
			new TypeReference<NbpSingle<NbpCRate>>() {
			});
	public static final NbpRatesMapper<List<NbpCTable<NbpCTableRate>>> mapperListNbpCTableNbpCTableRate = new NbpRatesMapper<>(
			new TypeReference<List<NbpCTable<NbpCTableRate>>>() {
			});
	public static final NbpRatesMapper<List<NbpATable<NbpBTableRate>>> mapperListNbpATableNbpBTableRate = new NbpRatesMapper<>(
			new TypeReference<List<NbpATable<NbpBTableRate>>>() {
			});

	@Override
	public List<ExchangeRateDto> convertToExchangeRates(List<DataPackageDto> packages) {
		List<ExchangeRateDto> rates = new ArrayList<>();
		List<? extends ExchangeRateDto> dtoList = new ArrayList<>();
		Set<CurrencyDto> currencies = new HashSet<>();
		int counter = 0;

		for (DataPackageDto data : packages) {

			try {
				dtoList.addAll(getExchangeRatesList(data));
			} catch (Exception e) {
				log.warn("ExchangeRate conversion error! Message: {}, cause: {}", e.getMessage(), e.getCause());
				continue;
			}

			if (!CollectionUtils.isEmpty(dtoList)) {

				for (ExchangeRateDto dto : dtoList) {
					ExchangeRateDto rate = null;

					try {
						String code = dto.getCurrency().getCode();
						Optional<CurrencyDto> optCurrency = getCurrencyByCode(currencies, code);

						if (optCurrency.isEmpty()) {
							rate = trySaveRate(dto);
							if (rate != null) {
								counter++;
							}
						} else {
							rate = trySaveRateWithCurrency(dto, optCurrency.orElseThrow());
							if (rate != null) {
								counter++;
							}
						}

						if (rate != null) {
							currencies.add(rate.getCurrency());
							rates.add(rate);
						}
					} catch (Exception e) {
						log.debug("ExchangeRate save error! Message: {}, cause: {}. May be ignored.", e.getMessage(),
								e.getCause());
					}
				}
				log.info("Saved {} rates", counter);
			}
			dataService.updateConverted(data);
		}

		return rates;
	}

	@Override
	@Transactional
	public List<ExchangeRateDto> convertNewDataPackagesAndSave() {
		List<DataPackageDto> foundPackages = new ArrayList<>();
		List<ExchangeRateDto> rates = new ArrayList<>();
		foundPackages.addAll(dataService.findAllNotConverted());

		if (!foundPackages.isEmpty()) {
			log.info("Found {} not converted Data package/s.", foundPackages.size());
			rates.addAll(convertToExchangeRates(foundPackages));
		} else {
			log.info("Not found any new Data package for conversion. ");
		}

		processedFlagsHandler(foundPackages);

		return rates;
	}

	private void processedFlagsHandler(List<DataPackageDto> processedEntities) {
		for (DataPackageDto data : processedEntities) {
			dataService.updateProcessed(data);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public <T extends ExchangeRateDto> List<T> getExchangeRatesList(DataPackageDto data) {
		List<T> rates;
		String endPoint = data.getEndPoint();

		if (Objects.equals(endPoints.ratesA, endPoint)) {
			rates = (List<T>) getNbpSingleWithNbpARate(data);
		} else if (Objects.equals(endPoints.aTableRate, endPoint)) {
			rates = (List<T>) getNbpATableWithNbpATableRate(data);
		} else if (Objects.equals(endPoints.bTableRate, endPoint)) {
			rates = (List<T>) getNbpATableWithNbpBTableRate(data);
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
	public List<ExchangeRateDto> convertEceptOmittedDataPackagesAndSave(List<DataPackageDto> ommittedPackages) {
		List<DataPackageDto> packages;
		List<ExchangeRateDto> rates = Collections.synchronizedList(new ArrayList<>());
		packages = dataService.findAllNotConverted();
		packages.removeAll(ommittedPackages);

		if (!packages.isEmpty()) {
			log.info("Found {} not converted Data package/s.", packages.size());
			rates = convertToExchangeRates(packages);
		} else {
			log.info("Not found any new Data package for conversion. ");
		}

		processedFlagsHandler(packages);

		return rates;
	}

	private List<ExchangeRateTypeADto> getNbpSingleWithNbpARate(DataPackageDto data) {
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

	private List<ExchangeRateTypeCDto> getNbpSingleWithNbpCRate(DataPackageDto data) {
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

	private List<ExchangeRateTypeADto> getNbpATableWithNbpATableRate(DataPackageDto data) {
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

	private List<ExchangeRateTypeBDto> getNbpATableWithNbpBTableRate(DataPackageDto data) {
		List<ExchangeRateTypeBDto> rates = new ArrayList<>();
		List<NbpATable<NbpBTableRate>> dtoList = mapperListNbpATableNbpBTableRate.map(data.getJsonData());

		for (NbpATable<NbpBTableRate> dto : dtoList) {

			if (dto.getRates() != null) {

				for (NbpBTableRate subDto : dto.getRates()) {
					CurrencyDto currency = rateFacade.getCurrencyDto(subDto.getCode(), subDto.getCurrency());
					ExchangeRateTypeBDto rate = rateFacade.getExchangeRateFromNbpBTableRate(subDto, currency,
							dto.getNo(),
							dto.getEffectiveDate());
					rates.add(rate);
				}
			}
		}

		return rates;
	}

	private List<ExchangeRateTypeCDto> getNbpCTableWithNbpCRate(DataPackageDto data) {
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
	public <T extends ExchangeRateDto> T trySaveRate(T rate) {

		try {
			return (T) rateFacade.save(rate);
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
	public <T extends ExchangeRateDto> T trySaveRateWithCurrency(T rate, CurrencyDto currency) {

		try {
			return (T) rateFacade.save(rate, currency);
		} catch (EntityExistsException ignore) {
		}

		return null;
	}

}
