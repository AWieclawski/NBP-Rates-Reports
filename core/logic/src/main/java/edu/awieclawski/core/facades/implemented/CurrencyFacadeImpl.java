package edu.awieclawski.core.facades.implemented;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.core.facades.BaseFacade;
import edu.awieclawski.core.facades.CurrencyFacade;
import edu.awieclawski.data.mappers.CurrencyMapper;
import edu.awieclawski.data.services.CurrencyService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class CurrencyFacadeImpl extends BaseFacade implements CurrencyFacade {

	protected final CurrencyService currencyService;

	/**
	 * Pass CurrencyNotFoundException thrown in CurrencyService. Could be handled in
	 * caller methods of another services.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public CurrencyDto findIfExist(String code, String description) {
		return CurrencyMapper.toDto(currencyService.findByCode(Objects.requireNonNull(code)));
	}

	@Override
	@Transactional
	public CurrencyDto buildNewCurrencyAndSave(String code, String description) {
		CurrencyDto currency = CurrencyDto.builder()
				.code(code)
				.description(description)
				.build();

		return CurrencyMapper.toDto(currencyService.save(currency));
	}

	@Override
	@Transactional(readOnly = true)
	public List<CurrencyDto> findAll() {
		return currencyService.findAll().stream()
				.filter(Objects::nonNull)
				.map(CurrencyMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<CurrencyDto> makeDistinctByCode(List<CurrencyDto> currencies) {
		return currencies.stream().filter(distinctByKey(CurrencyDto::getCode)).collect(Collectors.toList());
	}

	@Override
	public void delete(Long id) {
		currencyService.delete(id);
	}

}
