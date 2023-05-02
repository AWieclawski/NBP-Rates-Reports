package edu.awieclawski.core.facades.implemented;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.core.facades.BaseFacade;
import edu.awieclawski.core.facades.RateFacade;
import edu.awieclawski.data.services.RateService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class RateFacadeServiceImpl extends BaseFacade implements RateFacade {

	protected final RateService rateService;

	/**
	 * Pass RateNotFoundException thrown in CurrencyService. Could be handled in
	 * caller methods of another services.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public ExchangeRateDto findIfExistByCodeDateTable(String code, LocalDate published, String table) {
		return rateService.findByCodePublishedTable(Objects.requireNonNull(code),
				Objects.requireNonNull(published),
				Objects.requireNonNull(table));
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T extends ExchangeRateDto> List<T> findAllByType(NbpType nbpType) {
		return (List<T>) rateService.findAllByType(nbpType).stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public <T extends ExchangeRateDto> List<T> makeDistinctByCurrency(List<T> rates) {
		return rates.stream()
				.filter(distinctByKey(ExchangeRateDto::getCurrency))
				.collect(Collectors.toList());
	}

	@Override
	public <T extends ExchangeRateDto> List<T> makeDistinctById(List<T> rates) {
		return rates.stream().filter(Objects::nonNull)
				.filter(distinctByKey(ExchangeRateDto::getId))
				.collect(Collectors.toList());
	}

	@Override
	public <T extends ExchangeRateDto> List<T> makeDistinctByTable(List<T> rates) {
		return rates.stream().filter(Objects::nonNull)
				.filter(distinctByKey(ExchangeRateDto::getNbpTable))
				.collect(Collectors.toList());
	}

	@Override
	public <T extends ExchangeRateDto> List<CurrencyDto> getDistinctCurrencies(List<T> rates) {
		return rates.stream().filter(Objects::nonNull)
				.map(ExchangeRateDto::getCurrency)
				.filter(distinctByKey(CurrencyDto::getCode))
				.collect(Collectors.toList());
	}

	/**
	 * EntityExistsException should be caught in caller. Requires New Transaction
	 * Propagation prevents to roll back Transaction
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T extends ExchangeRateDto> T save(T rate) {
		return rateService.save(rate);
	}

	/**
	 * EntityExistsException should be caught in caller. Requires New Transaction
	 * Propagation prevents to roll back Transaction
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T extends ExchangeRateDto> T save(T rate, CurrencyDto currency) {
		return rateService.saveWithCurrency(rate, currency);
	}

	@Override
	public void delete(Long id) {
		rateService.delete(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExchangeRateDto> List<T> getDemo() {
		// Use NBP type A table as parameter
		List<ExchangeRateTypeADto> opRates = rateService.findAllByType(NbpType.A);
		List<ExchangeRateTypeADto> rates = null;

		if (opRates != null) {
			LocalDate maxDate = opRates.stream()
					.filter(Objects::nonNull)
					.map(u -> u.getPublished())
					.max(LocalDate::compareTo).orElse(null);

			if (maxDate != null) {
				rates = opRates.stream()
						.filter(Objects::nonNull)
						.filter(r -> r.getPublished().isAfter(maxDate.minusDays(1)))
						.collect(Collectors.toList());
			}
		}

		return (List<T>) rates;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExchangeRateDto> List<T> getByDatesRangeAndSymbolListAndType(LocalDate publishedStart,
			LocalDate publishedEnd, List<String> codes, NbpType nbpType) {

		if (publishedStart != null && CollectionUtils.isEmpty(codes)) {
			return (List<T>) rateService.getByDatesRangeAndType(publishedStart, publishedEnd, nbpType);
		}

		return (List<T>) rateService.getByDatesRangeAndSymbolListAndType(publishedStart, publishedEnd, codes, nbpType);
	}

	// getByCodeAndDatesRangeAndType

	@Override
	public Boolean findIfExistByCodeAndDateAndType(String code, LocalDate published, NbpType nbpType) {
		return rateService.findIfExistsByCodeBeforeDateAndType(code, published, nbpType);
	}

	@Override
	public Boolean findIfExistsBeforeDateAndType(LocalDate published, NbpType nbpType) {
		return rateService.findIfExistsBeforeDateAndType(published, nbpType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExchangeRateDto> List<T> getByCodeAndDatesRangeAndType(String code, LocalDate publishedStart,
			LocalDate publishedEnd, NbpType nbpType) {
		return (List<T>) rateService.getByCodeAndDatesRangeAndType(code, publishedStart, publishedEnd, nbpType);
	}

}
