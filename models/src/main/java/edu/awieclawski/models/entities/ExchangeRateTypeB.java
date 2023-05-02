package edu.awieclawski.models.entities;

import java.math.BigDecimal;

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
@DiscriminatorValue(ExchangeRateTypeB.DISC)
@EqualsAndHashCode(callSuper = true)
public class ExchangeRateTypeB extends ExchangeRate {

	public static final String DISC = NbpType.Constants.B;

	@Column(name = "rate", updatable = false)
	private BigDecimal rate;

	@Override
	public String toString() {
		String currToString = currency != null ? currency.toString() + "]" : "]";
		return "ExchangeRateTypeB [id=" + id + ", rate=" + rate
		+ ", published=" + published + ", nbpTable=" + nbpTable + ", currency=" + currToString;
	}

	@Override
	public String getInfo() {
		String currInfo = currency != null ? currency.getInfo() + "]" : "]";
		return "ExchangeRateTypeB [id=" + id + ", nbpTable=" + nbpTable + ", currency=" + currInfo;
	}

	@Override
	public NbpType getNbpType() {
		return NbpType.getEnumByConst(DISC);
	}

}
