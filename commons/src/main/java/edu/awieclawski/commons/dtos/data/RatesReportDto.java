package edu.awieclawski.commons.dtos.data;

import java.time.LocalDate;
import java.util.List;

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
public class RatesReportDto {
	protected LocalDate reportDate;
	protected String nbpTablesSeparated;
	protected List<RatesByDateDto> ratesByDate;
}
