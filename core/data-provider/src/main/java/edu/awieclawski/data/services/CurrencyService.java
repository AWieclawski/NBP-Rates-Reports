package edu.awieclawski.data.services;

import java.util.List;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.models.entities.Currency;

public interface CurrencyService {

	Currency save(CurrencyDto curency);

	List<Currency> findAll();

	Currency findById(Long id);

	Currency findByCode(String code);

	void delete(Long id);
	
	Currency findDuplicate(CurrencyDto currencyDto);

	Currency trySaveCurrencyDto(CurrencyDto currency);

}
