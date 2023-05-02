package edu.awieclawski.htmltopdf.dtos;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.commons.dtos.utils.LocalDateDeserializer;
import edu.awieclawski.commons.dtos.utils.LocalDateSerializer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class ReportDto {
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate validToStart; // valid-to defined as date published + 1 day

	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate validToEnd; // valid-to defined as date published + 1 day

	private List<String> currencies;

	private NbpType nbpType;

	public ReportDto(LocalDate validToStart, LocalDate validToEnd, List<String> currencies) {
		this.validToStart = validToStart;
		this.validToEnd = validToEnd;
		this.currencies = currencies;
	}

	public ReportDto(LocalDate validToStart, LocalDate validToEnd, List<String> currencies, NbpType nbpType) {
		this(validToStart, validToEnd, currencies);
		this.nbpType = nbpType;
	}

}
