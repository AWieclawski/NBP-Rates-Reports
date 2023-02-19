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
public class NbpBRate extends NbpRate {
	private BigDecimal mid;

	public NbpBRate(BigDecimal mid) {
		super.type = NbpType.B;
		this.mid = mid;
	}

}
