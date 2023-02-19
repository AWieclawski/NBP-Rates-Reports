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
@EqualsAndHashCode(callSuper = true)
public class ExchangeRateTypeBDto extends ExchangeRateDto {
	private BigDecimal rate;

	public ExchangeRateTypeBDto(Long id, BigDecimal rate, LocalDate published, String nbpTable, CurrencyDto currency) {
		super(id, published, nbpTable, currency);
		this.rate = rate;
	}

	@Override
	public String getInfo() {
		String currInfo = currency != null ? currency.getInfo() + "]" : "]";
		return "ExchangeRateTypeBDto [nbpTable=" + nbpTable + ", currency=" + currInfo;
	}

	@Override
	public String toString() {
		String currToString = currency != null ? currency.toString() + "]" : "]";
		return "ExchangeRateTypeBDto [rate=" + rate + ", published=" + published + ", nbpTable=" + nbpTable
		+ ", currency=" + currToString;
	}

}
