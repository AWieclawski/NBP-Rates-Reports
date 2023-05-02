package edu.awieclawski.commons.dtos.responses;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.awieclawski.commons.dtos.utils.LocalDateDeserializer;
import edu.awieclawski.commons.dtos.utils.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class NbpATable<T> {
	private String table;
	private String no;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate effectiveDate;
	private List<T> rates;
}
