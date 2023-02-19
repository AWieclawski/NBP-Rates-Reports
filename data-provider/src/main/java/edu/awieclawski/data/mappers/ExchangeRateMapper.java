package edu.awieclawski.data.mappers;

import java.util.Objects;

import javax.persistence.EntityNotFoundException;

import edu.awieclawski.commons.dtos.data.ExchangeRateTypeADto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeBDto;
import edu.awieclawski.commons.dtos.data.ExchangeRateTypeCDto;
import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import edu.awieclawski.models.entities.ExchangeRate;
import edu.awieclawski.models.entities.ExchangeRateTypeA;
import edu.awieclawski.models.entities.ExchangeRateTypeB;
import edu.awieclawski.models.entities.ExchangeRateTypeC;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateMapper {

	public static <T extends ExchangeRate> ExchangeRateDto toDto(T entity) {

		if (nullCheck(entity)) {
			return null;
		}

		if (entity instanceof ExchangeRateTypeA) {
			return ExchangeRateTypeAMapper.toDto((ExchangeRateTypeA) entity);
		} else if (entity instanceof ExchangeRateTypeB) {
			return ExchangeRateTypeBMapper.toDto((ExchangeRateTypeB) entity);
		} else if (entity instanceof ExchangeRateTypeC) {
			return ExchangeRateTypeCMapper.toDto((ExchangeRateTypeC) entity);
		}
		throw new EntityNotFoundException(getErrorMsg(entity.getClass()));
	}

	@SuppressWarnings("unchecked")
	public static <T extends ExchangeRate> T toEntity(ExchangeRateDto dto) {

		if (nullCheck(dto)) {
			return null;
		}

		if (dto instanceof ExchangeRateTypeADto) {
			return (T) ExchangeRateTypeAMapper.toEntity((ExchangeRateTypeADto) dto);
		} else if (dto instanceof ExchangeRateTypeBDto) {
			return (T) ExchangeRateTypeBMapper.toEntity((ExchangeRateTypeBDto) dto);
		} else if (dto instanceof ExchangeRateTypeCDto) {
			return (T) ExchangeRateTypeCMapper.toEntity((ExchangeRateTypeCDto) dto);
		}
		throw new EntityNotFoundException(getDtoErrorMsg(dto.getClass()));
	}

	@SuppressWarnings("unchecked")
	public static <T extends ExchangeRate> T toRawEntity(ExchangeRateDto dto) {

		if (nullCheck(dto)) {
			return null;
		}

		if (dto instanceof ExchangeRateTypeADto) {
			return (T) new ExchangeRateTypeA();
		} else if (dto instanceof ExchangeRateTypeBDto) {
			return (T) new ExchangeRateTypeB();
		} else if (dto instanceof ExchangeRateTypeCDto) {
			return (T) new ExchangeRateTypeC();
		}
		throw new EntityNotFoundException(getDtoErrorMsg(dto.getClass()));
	}

	private static boolean nullCheck(Object o) {
		return Objects.isNull(o);
	}

	private static <T extends ExchangeRateDto> String getDtoErrorMsg(Class<T> type) {
		return type != null ? String.format("Mapped DTO %s does not fit the method!", type.getSimpleName())
		: "is null and";
	}

	private static <T extends ExchangeRate> String getErrorMsg(Class<T> type) {
		return type != null ? String.format("Mapped DTO %s does not fit the method!", type.getSimpleName())
		: "is null and";
	}

}
