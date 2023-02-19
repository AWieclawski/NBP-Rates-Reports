package edu.awieclawski.core.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.models.entities.Currency;
import edu.awieclawski.models.entities.DataPackage;
import edu.awieclawski.models.entities.ExchangeRate;

public interface ConversionService {

	List<ExchangeRate> convertToExchangeRates(List<DataPackage> packages);

	List<ExchangeRate> convertNewDataPackagesAndSave();

	List<ExchangeRate> convertEceptOmittedDataPackagesAndSave(List<DataPackage> ommitedPackages);

	List<ExchangeRateTypeADto> getExchangeRatesList(DataPackage data);

	ExchangeRate trySaveRate(ExchangeRateTypeADto rate);

	ExchangeRate trySaveRateWithCurrency(ExchangeRateTypeADto rate, Currency currency);

	// default Collection utils

	default Optional<Currency> getCurrencyByCode(Set<Currency> currencies, String code) {
		return currencies.stream().filter(c -> Objects.equals(c.getCode(), code)).findFirst();
	}

}
