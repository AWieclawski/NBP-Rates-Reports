package edu.awieclawski.core.facades;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeBDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeCDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.commons.dtos.rates.NbpARate;
import edu.awieclawski.commons.dtos.rates.NbpATableRate;
import edu.awieclawski.commons.dtos.rates.NbpBRate;
import edu.awieclawski.commons.dtos.rates.NbpBTableRate;
import edu.awieclawski.commons.dtos.rates.NbpCRate;
import edu.awieclawski.commons.dtos.rates.NbpCTableRate;

public interface RateFacade {

	ExchangeRateDto findIfExistByCodeDateTable(String code, LocalDate published, String table);

	Boolean findIfExistByCodeAndDateAndType(String code, LocalDate published, NbpType nbpType);

	Boolean findIfExistsBeforeDateAndType(LocalDate published, NbpType nbpType);

	<T extends ExchangeRateDto> T save(T rate);

	<T extends ExchangeRateDto> List<T> findAllByType(NbpType nbpType);

	<T extends ExchangeRateDto> List<T> makeDistinctById(List<T> rates);

	<T extends ExchangeRateDto> List<T> makeDistinctByTable(List<T> rates);

	<T extends ExchangeRateDto> List<T> makeDistinctByCurrency(List<T> rates);

	<T extends ExchangeRateDto> List<CurrencyDto> getDistinctCurrencies(List<T> rates);

	void delete(Long id);

	<T extends ExchangeRateDto> T save(T rate, CurrencyDto currency);

	<T extends ExchangeRateDto> List<T> getDemo();

	<T extends ExchangeRateDto> List<T> getByDatesRangeAndSymbolListAndType(LocalDate publishedStart,
			LocalDate publishedEnd, List<String> codes, NbpType nbpType);

	<T extends ExchangeRateDto> List<T> getByCodeAndDatesRangeAndType(String code, LocalDate publishedStart,
			LocalDate publishedEnd, NbpType nbpType);

	// default basic DTO builders

	default CurrencyDto getCurrencyDto(String code, String desc) {
		return CurrencyDto.builder()
				.code(code)
				.description(desc)
				.build();
	}

	default ExchangeRateTypeADto getExchangeRateFromNbpARate(NbpARate subDto, CurrencyDto currency) {
		return ExchangeRateTypeADto.builder()
				.currency(Objects.requireNonNull(currency))
				.nbpTable(Objects.requireNonNull(subDto.getNo()))
				.rate(Objects.requireNonNull(subDto.getMid()))
				.published(Objects.requireNonNull(subDto.getEffectiveDate()))
				.build();
	}

	default ExchangeRateTypeADto getExchangeRateFromNbpATableRate(NbpATableRate subDto, CurrencyDto currency,
			String tableNo, LocalDate date) {
		return ExchangeRateTypeADto.builder()
				.currency(Objects.requireNonNull(currency))
				.nbpTable(Objects.requireNonNull(tableNo))
				.rate(Objects.requireNonNull(subDto.getMid()))
				.published(Objects.requireNonNull(date))
				.build();
	}

	default ExchangeRateTypeBDto getExchangeRateFromNbpBRate(NbpBRate subDto, CurrencyDto currency) {
		return ExchangeRateTypeBDto.builder()
				.currency(Objects.requireNonNull(currency))
				.nbpTable(Objects.requireNonNull(subDto.getNo()))
				.rate(Objects.requireNonNull(subDto.getMid()))
				.published(Objects.requireNonNull(subDto.getEffectiveDate()))
				.build();
	}

	default ExchangeRateTypeCDto getExchangeRateFromNbpCRate(NbpCRate subDto, CurrencyDto currency) {
		return ExchangeRateTypeCDto.builder()
				.currency(Objects.requireNonNull(currency))
				.nbpTable(Objects.requireNonNull(subDto.getNo()))
				.ask(Objects.requireNonNull(subDto.getAsk()))
				.bid(Objects.requireNonNull(subDto.getBid()))
				.published(Objects.requireNonNull(subDto.getEffectiveDate()))
				.build();
	}

	default ExchangeRateTypeBDto getExchangeRateFromNbpBTableRate(NbpBTableRate subDto, CurrencyDto currency,
			String tableNo, LocalDate date) {
		return ExchangeRateTypeBDto.builder()
				.currency(Objects.requireNonNull(currency))
				.nbpTable(Objects.requireNonNull(tableNo))
				.rate(Objects.requireNonNull(subDto.getMid()))
				.published(Objects.requireNonNull(date))
				.build();
	}

	default ExchangeRateTypeCDto getExchangeRateFromNbpCTableRate(NbpCTableRate subDto, CurrencyDto currency,
			String tableNo, LocalDate date, LocalDate tradingDate) {
		return ExchangeRateTypeCDto.builder()
				.currency(Objects.requireNonNull(currency))
				.nbpTable(Objects.requireNonNull(tableNo))
				.ask(Objects.requireNonNull(subDto.getAsk()))
				.bid(Objects.requireNonNull(subDto.getBid()))
				.tradingDate(Objects.requireNonNull(tradingDate))
				.published(Objects.requireNonNull(date))
				.build();
	}

}
