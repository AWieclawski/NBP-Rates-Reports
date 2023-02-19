package edu.awieclawski.commons.dtos.rates.bases;

import edu.awieclawski.commons.dtos.enums.NbpType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public abstract class NbpTableRate {
	private String currency;
	private String code;
	protected NbpType type;
}
