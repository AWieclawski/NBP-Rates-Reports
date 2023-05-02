package edu.awieclawski.commons.dtos.data.bases;

import java.time.LocalDate;

import edu.awieclawski.commons.dtos.data.CurrencyDto;
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
public class ExchangeRateDto {
	protected Long id;
	protected LocalDate published;
	protected String nbpTable;
	protected CurrencyDto currency;
	protected LocalDate validTo;

	protected ExchangeRateDto(Long id, LocalDate published, String nbpTable, CurrencyDto currency) {
		this.id = id;
		this.published = published;
		this.nbpTable = nbpTable;
		this.currency = currency;
		this.validTo = published != null ? published.plusDays(1) : published;
	}

	public String getInfo() {
		String currInfo = currency != null ? currency.getInfo() + "]" : "]";
		return "ExchangeRateDto [nbpTable=" + nbpTable + ", currency=" + currInfo;
	}

	@Override
	public String toString() {
		String currToString = currency != null ? currency.toString() + "]" : "]";
		return "ExchangeRateDto [published=" + published + ", nbpTable=" + nbpTable + ", validTo=" + validTo
				+ ", currency=" + currToString;
	}

}
