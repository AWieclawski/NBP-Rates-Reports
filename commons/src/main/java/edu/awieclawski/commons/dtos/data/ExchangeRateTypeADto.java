package edu.awieclawski.commons.dtos.data;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@EqualsAndHashCode(exclude = { "rateInverted" }, callSuper = true)
public class ExchangeRateTypeADto extends ExchangeRateDto {
	private BigDecimal rate;
	private BigDecimal rateInverted;

	public ExchangeRateTypeADto(Long id, BigDecimal rate, BigDecimal rateInverted, LocalDate published, String nbpTable,
	CurrencyDto currency) {
		super(id, published, nbpTable, currency);
		this.rate = rate;
		this.rateInverted = rateInverted;
	}

	@Override
	public String getInfo() {
		String currInfo = currency != null ? currency.getInfo() + "]" : "]";
		return "ExchangeRateTypeADto [nbpTable=" + nbpTable + ", currency=" + currInfo;
	}

	@Override
	public String toString() {
		String currToString = currency != null ? currency.toString() + "]" : "]";
		return "ExchangeRateTypeADto [rate=" + rate + ", published=" + published + ", nbpTable=" + nbpTable
		+ ", currency=" + currToString;
	}

}
