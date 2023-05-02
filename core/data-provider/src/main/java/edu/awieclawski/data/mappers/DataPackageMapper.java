package edu.awieclawski.data.mappers;

import java.util.Objects;

import edu.awieclawski.commons.dtos.data.DataPackageDto;
import edu.awieclawski.models.entities.DataPackage;

public class DataPackageMapper {

	public static DataPackageDto toDto(DataPackage entity) {

		if (nullCheck(entity)) {
			return null;
		}

		return DataPackageDto.builder()
				.endPoint(entity.getEndPoint())
				.jsonData(entity.getJsonData())
				.url(entity.getUrl())
				.id(entity.getId())
				.converted(entity.getConverted())
				.processed(entity.getProcessed())
				.createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toLocalDate() : null)
				.build();
	}

	public static DataPackage toEntity(DataPackageDto dto) {

		if (nullCheck(dto)) {
			return null;
		}

		return DataPackage.builder()
				.endPoint(dto.getEndPoint())
				.jsonData(dto.getJsonData())
				.url(dto.getUrl())
				.build();
	}

	private static boolean nullCheck(Object o) {
		return Objects.isNull(o);
	}

}
