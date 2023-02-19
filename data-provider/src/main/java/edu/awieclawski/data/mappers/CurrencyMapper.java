package edu.awieclawski.data.mappers;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
import edu.awieclawski.models.entities.Currency;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyMapper {

	public static CurrencyDto toDto(Currency entity) {
		CurrencyDto dto = null;

		if (entity != null) {
			dto = new CurrencyDto();
			dto.setCode(entity.getCode());
			dto.setDescription(entity.getDescription());
			dto.setId(entity.getId());
		}
		return dto;
	}

	public static Currency toEntity(CurrencyDto dto) {
		Currency entity = null;

		if (dto != null) {
			entity = new Currency();
			entity.setCode(dto.getCode());
			entity.setDescription(dto.getDescription());
		}
		return entity;
	}

}
