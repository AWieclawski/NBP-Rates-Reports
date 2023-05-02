package edu.awieclawski.commons.dtos.enums;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

public enum NbpType {
	A("Tabela kursów średnich walut obcych", Constants.A),
	B("Tabela kursów średnich walut niewymienialnych", Constants.B),
	C("Tabela kursów kupna i sprzedaży", Constants.C),
	H("Tabela kursów jednostek rozliczeniowych", Constants.H);

	@Getter
	private String description;
	private String constant;

	@JsonValue
	public String getConstant() {
		return this.constant;
	}

	private NbpType(String description, String constant) {
		this.description = description;
		this.constant = constant;
	}

	public static class Constants {
		public static final String A = "A";
		public static final String B = "B";
		public static final String C = "C";
		public static final String H = "H";
	}

	public static NbpType getEnumByConst(String constant) {
		for (NbpType e : NbpType.values()) {
			if (Objects.equals(e.getConstant(), constant)) {
				return e;
			}
		}
		return null;
	}

}
