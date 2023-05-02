package edu.awieclawski.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.awieclawski.commons.dtos.enums.NbpType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue(ExchangeRateTypeC.DISC)
@EqualsAndHashCode(callSuper = true)
public class ExchangeRateTypeC extends ExchangeRate {

	public static final String DISC = NbpType.Constants.C;

	@Column(name = "bid", updatable = false)
	private BigDecimal bid;

	@Column(name = "ask", updatable = false)
	private BigDecimal ask;

	@Column(name = "trading_date", updatable = false)
	private LocalDate tradingDate;

	@Override
	public String toString() {
		String currToString = currency != null ? currency.toString() + "]" : "]";
		return "ExchangeRateTypeC [id=" + id + ", bid=" + bid + ", ask=" + ask + ", tradingDate=" + tradingDate
		+ ", published=" + published + ", nbpTable=" + nbpTable + ", currency=" + currToString;
	}

	@Override
	public String getInfo() {
		String currInfo = currency != null ? currency.getInfo() + "]" : "]";
		return "ExchangeRateTypeC [id=" + id + ", nbpTable=" + nbpTable + ", currency=" + currInfo;
	}

	@Override
	public NbpType getNbpType() {
		return NbpType.getEnumByConst(DISC);
	}

}
