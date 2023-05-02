package edu.awieclawski.data.mappers;

import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.models.entities.ExchangeRateTypeA;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExchangeRateTypeAMapper {

	static ExchangeRateTypeADto toDto(ExchangeRateTypeA entity) {
		ExchangeRateTypeADto dto = null;

		if (entity != null) {
			dto = new ExchangeRateTypeADto();
			dto.setCurrency(CurrencyMapper.toDto(entity.getCurrency()));
			dto.setNbpTable(entity.getNbpTable());
			dto.setPublished(entity.getPublished());
			dto.setValidTo(dto.getPublished() != null ? dto.getPublished().plusDays(1) : dto.getPublished());
			dto.setRate(entity.getRate());
			dto.setId(entity.getId());
		}
		return dto;
	}

	static ExchangeRateTypeA toEntity(ExchangeRateTypeADto dto) {
		ExchangeRateTypeA entity = null;

		if (dto != null) {
			entity = new ExchangeRateTypeA();
			entity.setCurrency(CurrencyMapper.toEntity(dto.getCurrency()));
			entity.setNbpTable(dto.getNbpTable());
			entity.setPublished(dto.getPublished());
			entity.setRate(dto.getRate());
		}
		return entity;
	}

}
