package edu.awieclawski.data.mappers;

import edu.awieclawski.commons.dtos.data.ExchangeRateTypeCDto;
import edu.awieclawski.models.entities.ExchangeRateTypeC;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExchangeRateTypeCMapper {

	static ExchangeRateTypeCDto toDto(ExchangeRateTypeC entity) {
		ExchangeRateTypeCDto dto = null;

		if (entity != null) {
			dto = new ExchangeRateTypeCDto();
			dto.setCurrency(CurrencyMapper.toDto(entity.getCurrency()));
			dto.setNbpTable(entity.getNbpTable());
			dto.setPublished(entity.getPublished());
			dto.setTradingDate(entity.getTradingDate());
			dto.setBid(entity.getBid());
			dto.setAsk(entity.getAsk());
			dto.setId(entity.getId());
		}
		return dto;
	}

	static ExchangeRateTypeC toEntity(ExchangeRateTypeCDto dto) {
		ExchangeRateTypeC entity = null;

		if (dto != null) {
			entity = new ExchangeRateTypeC();
			entity.setCurrency(CurrencyMapper.toEntity(dto.getCurrency()));
			entity.setNbpTable(dto.getNbpTable());
			entity.setPublished(dto.getPublished());
			entity.setTradingDate(dto.getTradingDate());
			entity.setAsk(dto.getAsk());
			entity.setBid(dto.getBid());
		}
		return entity;
	}

}
