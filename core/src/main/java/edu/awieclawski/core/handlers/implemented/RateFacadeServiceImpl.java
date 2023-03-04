package edu.awieclawski.core.handlers.implemented;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.core.facades.BaseFacade;
import edu.awieclawski.core.facades.RateFacade;
import edu.awieclawski.data.services.RateService;
import edu.awieclawski.models.entities.Currency;
import edu.awieclawski.models.entities.ExchangeRate;
import edu.awieclawski.models.entities.ExchangeRateTypeA;
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
	public ExchangeRate findIfExistByCodeDateTable(String code, LocalDate published, String table) {
		return rateService.findByCodePublishedTable(Objects.requireNonNull(code),
				Objects.requireNonNull(published),
				Objects.requireNonNull(table));
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T extends ExchangeRate> List<T> findAllByType(NbpType nbpType) {
		return (List<T>) rateService.findAllByType(nbpType);
	}

	@Override
	public <T extends ExchangeRate> List<T> makeDistinctByCurrency(List<T> rates) {
		return rates.stream().filter(distinctByKey(ExchangeRate::getCurrency)).collect(Collectors.toList());
	}

	@Override
	public <T extends ExchangeRate> List<T> makeDistinctById(List<T> rates) {
		return rates.stream().filter(Objects::nonNull)
				.filter(distinctByKey(ExchangeRate::getId)).collect(Collectors.toList());
	}

	@Override
	public <T extends ExchangeRate> List<T> makeDistinctByTable(List<T> rates) {
		return rates.stream().filter(Objects::nonNull)
				.filter(distinctByKey(ExchangeRate::getNbpTable)).collect(Collectors.toList());
	}

	@Override
	public <T extends ExchangeRate> List<Currency> getDistinctCurrencies(List<T> rates) {
		return rates.stream().filter(Objects::nonNull)
				.map(ExchangeRate::getCurrency)
				.filter(distinctByKey(Currency::getCode))
				.collect(Collectors.toList());
	}

	/**
	 * EntityExistsException should be caught in caller. Requires New Transaction
	 * Propagation prevents to roll back Transaction
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T extends ExchangeRateDto> ExchangeRate save(T rate) {
		return rateService.save(rate);
	}

	/**
	 * EntityExistsException should be caught in caller. Requires New Transaction
	 * Propagation prevents to roll back Transaction
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T extends ExchangeRateDto> ExchangeRate save(T rate, Currency currency) {
		return rateService.saveWithCurrency(rate, currency);
	}

	@Override
	public void delete(Long id) {
		rateService.delete(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExchangeRate> List<T> getDemo() {
		// Use NBP type A table as parameter
		List<ExchangeRateTypeA> opRates = rateService.findAllByType(NbpType.A);
		List<ExchangeRateTypeA> rates = null;

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

	@Override
	public <T extends ExchangeRate> List<T> getByDatesRangeAndSymbolListAndType(LocalDate publishedStart,
			LocalDate publishedEnd, List<String> codes, NbpType nbpType) {

		if (publishedStart != null && CollectionUtils.isEmpty(codes)) {
			return rateService.getByDatesRangeAndType(publishedStart, publishedEnd, nbpType);
		}

		return rateService.getByDatesRangeAndSymbolListAndType(publishedStart, publishedEnd, codes, nbpType);
	}

	@Override
	public Boolean findIfExistByCodeAndDateAndType(String code, LocalDate published, NbpType nbpType) {
		return rateService.findIfExistsByCodeAndDateAndType(code, published, nbpType);
	}

}
