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
public class NbpARate extends NbpRate {
	private BigDecimal mid;

	public NbpARate(BigDecimal mid) {
		super.type = NbpType.A;
		this.mid = mid;
	}

}
