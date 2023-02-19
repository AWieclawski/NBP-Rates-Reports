package edu.awieclawski.commons.dtos.rates.bases;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.commons.dtos.utils.LocalDateDeserializer;
import edu.awieclawski.commons.dtos.utils.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public abstract class NbpRate {
	private String no;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate effectiveDate;
	protected NbpType type;
}
