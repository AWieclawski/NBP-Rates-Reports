package edu.awieclawski.models.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrePersist;

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
@DiscriminatorValue(ExchangeRateTypeA.DISC)
@EqualsAndHashCode(callSuper = true)
public class ExchangeRateTypeA extends ExchangeRate {

	public static final String DISC = NbpType.Constants.A;
	public static final BigDecimal ZERO = BigDecimal.ZERO;
	public static final BigDecimal ONE = BigDecimal.ONE;
	public static final int DECIMALS = 4;

	@Column(name = "rate", updatable = false)
	private BigDecimal rate;

	@Column(name = "rate_inv", updatable = false)
	private BigDecimal rateInverted;

	@PrePersist
	private void initIfNull() {
		if (getRateInverted() == null) {
			setRateInverted(reciprocal(this.rate));
		}
	}

	public static BigDecimal reciprocal(BigDecimal rate) {

		if (rate == null || rate.equals(ZERO)) {
			return ZERO;
		}

		BigDecimal inverted = ONE.divide(rate, DECIMALS * 4, RoundingMode.HALF_UP);
		inverted = inverted.setScale(DECIMALS, RoundingMode.HALF_UP);

		return inverted;
	}

	@Override
	public String toString() {
		String currToString = currency != null ? currency.toString() + "]" : "]";
		return "ExchangeRateTypeA [id=" + id + ", rate=" + rate + ", rateInverted=" + rateInverted
				+ ", published=" + published + ", nbpTable=" + nbpTable + ", currency=" + currToString;
	}

	@Override
	public String getInfo() {
		String currInfo = currency != null ? currency.getInfo() + "]" : "]";
		return "ExchangeRateTypeA [id=" + id + ", nbpTable=" + nbpTable + ", currency=" + currInfo;
	}

	@Override
	public NbpType getNbpType() {
		return NbpType.getEnumByConst(DISC);
	}
}
