package edu.awieclawski.commons.dtos.utils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import edu.awieclawski.commons.dtos.exceptions.DeserializeDateException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalDateDeserializer extends StdDeserializer<LocalDate> {

	/**
	 * acc. to
	 * https://kodejava.org/how-to-format-localdate-object-using-jackson/
	 */
	private static final long serialVersionUID = -6341381489889984058L;

	protected LocalDateDeserializer() {
		super(LocalDate.class);
	}

	@Override
	public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		LocalDate deserialized;

		try {
			deserialized = LocalDate.parse(parser.readValueAs(String.class));
			return deserialized;
		} catch (DateTimeParseException e) {
			log.error("LocalDate deserialization error: {} for String value: [{}]", e.getMessage(), e.getParsedString());
		}

		throw new DeserializeDateException("LocalDateDeserializer error ");
	}

}
