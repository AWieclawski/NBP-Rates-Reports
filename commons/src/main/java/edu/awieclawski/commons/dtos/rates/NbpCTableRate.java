package edu.awieclawski.commons.dtos.rates;

import java.math.BigDecimal;

import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.commons.dtos.rates.bases.NbpTableRate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class NbpCTableRate extends NbpTableRate {
	private BigDecimal bid;
	private BigDecimal ask;
	
	public NbpCTableRate(BigDecimal bid, BigDecimal ask) {
		super.type = NbpType.C;
		this.bid = bid;
		this.ask = ask;
	}
	
	
}
