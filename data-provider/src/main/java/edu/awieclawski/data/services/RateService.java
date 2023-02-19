package edu.awieclawski.data.services;

import java.time.LocalDate;
import java.util.List;

import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.models.entities.Currency;
import edu.awieclawski.models.entities.ExchangeRate;

public interface RateService {

	<T extends ExchangeRateDto> ExchangeRate save(T exchangeRate);

	<T extends ExchangeRate> List<T> findAllByType(NbpType nbpType);

	ExchangeRate findById(Long id);

	ExchangeRate findByCodePublishedTable(String code, LocalDate published, String table);

	boolean findIfExistsByCodeAndDateAndType(String code, LocalDate published, NbpType nbpType);

	void delete(Long id);

	<T extends ExchangeRateDto> boolean isRateDuplicated(T dto);

	<T extends ExchangeRateDto> ExchangeRate findDuplicate(T dto);

	<T extends ExchangeRate> ExchangeRate assignCurrency(T entity);

	<T extends ExchangeRateDto> ExchangeRate saveWithCurrency(T rateDto, Currency currency);

	<T extends ExchangeRate> List<T> getByDatesRangeAndSymbolListAndType(LocalDate publishedStart,
	LocalDate publishedEnd, List<String> codes, NbpType nbpType);

	<T extends ExchangeRate> List<T> getByDatesRangeAndType(LocalDate publishedStart, LocalDate publishedEnd,
	NbpType nbpType);

}
