package edu.awieclawski.data.mappers;

import edu.awieclawski.commons.dtos.data.ExchangeRateTypeBDto;
import edu.awieclawski.models.entities.ExchangeRateTypeB;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExchangeRateTypeBMapper {

	static ExchangeRateTypeBDto toDto(ExchangeRateTypeB entity) {
		ExchangeRateTypeBDto dto = null;

		if (entity != null) {
			dto = new ExchangeRateTypeBDto();
			dto.setCurrency(CurrencyMapper.toDto(entity.getCurrency()));
			dto.setNbpTable(entity.getNbpTable());
			dto.setPublished(entity.getPublished());
			dto.setValidTo(dto.getPublished() != null ? dto.getPublished().plusDays(1) : dto.getPublished());
			dto.setRate(entity.getRate());			
			dto.setId(entity.getId());
		}
		return dto;
	}

	static ExchangeRateTypeB toEntity(ExchangeRateTypeBDto dto) {
		ExchangeRateTypeB entity = null;

		if (dto != null) {
			entity = new ExchangeRateTypeB();
			entity.setCurrency(CurrencyMapper.toEntity(dto.getCurrency()));
			entity.setNbpTable(dto.getNbpTable());
			entity.setPublished(dto.getPublished());
			entity.setRate(dto.getRate());
		}
		return entity;
	}

}
