package edu.awieclawski.commons.dtos.data;

import java.util.Objects;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class CurrencyDto {
	private Long id;
	private String code;
	private String description;

	@Override
	public int hashCode() {
		return Objects.hash(code, description, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurrencyDto other = (CurrencyDto) obj;
		return Objects.equals(code, other.code) && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id);
	}

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
