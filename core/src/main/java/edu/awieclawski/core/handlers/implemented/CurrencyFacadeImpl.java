package edu.awieclawski.core.handlers.implemented;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.core.facades.BaseFacade;
import edu.awieclawski.core.facades.CurrencyFacade;
import edu.awieclawski.data.services.CurrencyService;
import edu.awieclawski.models.entities.Currency;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class CurrencyFacadeImpl extends BaseFacade implements CurrencyFacade {

	protected final CurrencyService currencyService;

	/**
	 * Pass CurrencyNotFoundException thrown in CurrencyService. Could be
	 * handled in caller methods of another services.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public Currency findIfExist(String code, String description) {
		return currencyService.findByCode(Objects.requireNonNull(code));
	}

	@Override
	@Transactional
	public Currency buildNewCurrencyAndSave(String code, String description) {
		CurrencyDto currency = CurrencyDto.builder()
				.code(code)
				.description(description)
				.build();

		return currencyService.save(currency);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Currency> findAll() {
		return currencyService.findAll();
	}

	@Override
	public List<Currency> makeDistinctByCode(List<Currency> currencies) {
		return currencies.stream().filter(distinctByKey(Currency::getCode)).collect(Collectors.toList());
	}
	
	@Override
	public void delete(Long id) {
		currencyService.delete(id);
	}

}
