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
public class ExchangeRateTypeCDto extends ExchangeRateDto {
	private BigDecimal bid;
	private BigDecimal ask;
	private LocalDate tradingDate;

	public ExchangeRateTypeCDto(Long id, BigDecimal bid, BigDecimal ask, LocalDate published, LocalDate tradingDate,
	String nbpTable, CurrencyDto currency) {
		super(id, published, nbpTable, currency);
		this.bid = bid;
		this.ask = ask;
		this.tradingDate = tradingDate;
	}

	@Override
	public String getInfo() {
		String currInfo = currency != null ? currency.getInfo() + "]" : "]";
		return "ExchangeRateTypeCDto [nbpTable=" + nbpTable + ", currency=" + currInfo;
	}

	@Override
	public String toString() {
		String currToString = currency != null ? currency.toString() + "]" : "]";
		return "ExchangeRateTypeCDto [bid=" + bid + ", ask=" + ask + ", tradingDate=" + tradingDate + ", published="
		+ published + ", nbpTable=" + nbpTable + ", currency=" + currToString;
	}

}
