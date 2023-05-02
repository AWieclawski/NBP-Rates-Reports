package edu.awieclawski.commons.dtos.data;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode(of = { "id", "code" })
public class CurrencyDto {
	private Long id;
	private String code;
	private String description;

	public CurrencyDto(Long id, String code, String description) {
		this.id = id;
		this.code = code;
		this.description = description;
	}

	public String getInfo() {
		return "Currency [code=" + code + "]";
	}

	@Override
	public String toString() {
		return "CurrencyDto [code=" + code + ", description=" + description + "]";
	}

}
