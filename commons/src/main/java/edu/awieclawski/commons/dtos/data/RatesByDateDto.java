package edu.awieclawski.commons.dtos.data;

import java.time.LocalDate;
import java.util.List;

import edu.awieclawski.commons.dtos.data.bases.ExchangeRateDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RatesByDateDto {
	protected LocalDate published;
	protected String nbpTable;
	protected List<ExchangeRateDto> rates;
}
