package edu.awieclawski.core.facades;

import java.util.List;

import edu.awieclawski.commons.dtos.data.CurrencyDto;

public interface CurrencyFacade {

	CurrencyDto findIfExist(String code, String description);

	CurrencyDto buildNewCurrencyAndSave(String code, String description);

	List<CurrencyDto> findAll();

	List<CurrencyDto> makeDistinctByCode(List<CurrencyDto> currencies);

	void delete(Long id);

}
