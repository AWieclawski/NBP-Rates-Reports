package edu.awieclawski.commons.dtos.rates;

import java.math.BigDecimal;

import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.commons.dtos.rates.bases.NbpRate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class NbpCRate extends NbpRate {
	private BigDecimal bid;
	private BigDecimal ask;

	public NbpCRate(BigDecimal bid, BigDecimal ask) {
		super.type = NbpType.C;
		this.bid = bid;
		this.ask = ask;
	}

}
