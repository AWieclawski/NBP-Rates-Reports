package edu.awieclawski.commons.dtos.responses;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class NbpSingle<T> {
	private String table;
	private String currency;
	private String code;
	private List<T> rates;
}
