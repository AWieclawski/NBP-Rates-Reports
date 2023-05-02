package edu.awieclawski.converters.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.awieclawski.converters.exceptions.ConverterException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NbpRatesMapper<T> {
	private String errMsg;
	private ObjectMapper objectMapper;
	private TypeReference<T> type;

	public T map(String json) {
		T dto;

		try {
			dto = objectMapper.readValue(json, type);
		} catch (Exception e) {
			log.error("Json {} to dto {} error!", json, type.getType(), e);
			throw new ConverterException(errMsg + e.getClass() + e.getMessage());
		}

		return dto;
	}

	public NbpRatesMapper(TypeReference<T> type) {
		this.errMsg = "Json to DTO Mapper throws ";
		this.objectMapper = new ObjectMapper();
		this.type = type;
	}
}
