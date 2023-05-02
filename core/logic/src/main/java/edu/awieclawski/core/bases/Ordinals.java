package edu.awieclawski.core.bases;

import java.util.Map;

public interface Ordinals {
	Map<Integer, String> ordinalsMap = Map.of(
			0, "Zeroth",
			1, "First",
			2, "Second",
			3, "Third",
			4, "Fourth",
			5, "Fifth",
			6, "Sixth",
			7, "Seventh",
			8, "Eighth",
			9, "Ninth");

	default String getOrdinalbyNumber(int no) {
		return ordinalsMap.get(no) != null ? ordinalsMap.get(no) : String.valueOf(no);
	}
}
