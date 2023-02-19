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
public class NbpATableRate extends NbpTableRate {
	private BigDecimal mid;

	public NbpATableRate(BigDecimal mid) {
		super.type = NbpType.A;
		this.mid = mid;
	}
	
}
