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
public class NbpBTableRate extends NbpTableRate {
	private BigDecimal mid;

	public NbpBTableRate(BigDecimal mid) {
		super.type = NbpType.B;
		this.mid = mid;
	}
	
}
