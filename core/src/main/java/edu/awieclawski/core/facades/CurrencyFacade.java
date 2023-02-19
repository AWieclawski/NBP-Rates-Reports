package edu.awieclawski.core.facades;

import java.util.List;

import edu.awieclawski.models.entities.Currency;

public interface CurrencyFacade {

	Currency findIfExist(String code, String description);

	Currency buildNewCurrencyAndSave(String code, String description);

	List<Currency> findAll();
	
	List<Currency> makeDistinctByCode(List<Currency> currencies);

	void delete(Long id);

}
