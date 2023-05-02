package edu.awieclawski.core.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;

public interface ConversionService {

	List<ExchangeRateDto> convertToExchangeRates(List<DataPackageDto> packages);

	List<ExchangeRateDto> convertNewDataPackagesAndSave();

	List<ExchangeRateDto> convertEceptOmittedDataPackagesAndSave(List<DataPackageDto> ommitedPackages);

	<T extends ExchangeRateDto> List<T> getExchangeRatesList(DataPackageDto data);

	<T extends ExchangeRateDto> T trySaveRate(T rate);

	<T extends ExchangeRateDto> T trySaveRateWithCurrency(T rate, CurrencyDto currency);

	// default Collection utils

	default Optional<CurrencyDto> getCurrencyByCode(Set<CurrencyDto> currencies, String code) {
		return currencies.stream().filter(c -> Objects.equals(c.getCode(), code)).findFirst();
	}

}
