package edu.awieclawski.data.services;

import java.time.LocalDate;
import java.util.List;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.enums.NbpType;

public interface RateService {

	<T extends ExchangeRateDto> T save(T exchangeRate);

	<T extends ExchangeRateDto> List<T> findAllByType(NbpType nbpType);

	ExchangeRateDto findById(Long id);

	ExchangeRateDto findByCodePublishedTable(String code, LocalDate published, String table);

	boolean findIfExistsByCodeBeforeDateAndType(String code, LocalDate published, NbpType nbpType);
	
	boolean findIfExistsBeforeDateAndType(LocalDate published, NbpType nbpType);

	void delete(Long id);

	<T extends ExchangeRateDto> boolean isRateDuplicated(T dto);

	<T extends ExchangeRateDto> T findDuplicate(T dto);

	<T extends ExchangeRateDto> T saveWithCurrency(T rateDto, CurrencyDto currency);

	<T extends ExchangeRateDto> List<T> getByDatesRangeAndSymbolListAndType(LocalDate publishedStart,
			LocalDate publishedEnd, List<String> codes, NbpType nbpType);

	<T extends ExchangeRateDto> List<T> getByDatesRangeAndType(LocalDate publishedStart, LocalDate publishedEnd,
			NbpType nbpType);
	
	<T extends ExchangeRateDto> List<T> getByCodeAndDatesRangeAndType(String code, LocalDate publishedStart, LocalDate publishedEnd, NbpType nbpType);

}
