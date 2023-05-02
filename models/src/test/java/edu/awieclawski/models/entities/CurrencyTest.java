package edu.awieclawski.models.entities;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CurrencyTest {

	@Test
	void testRemoveCurrenciesFromListReturnOnlyDifferentByCode() {
		// given
		Currency currOne = Currency.builder().code("ABC").build(); // Doubled
		Currency currTwo = Currency.builder().code("BCD").build();
		Currency currThree = Currency.builder().code("CDE").build(); // Doubled
		Currency currFour = Currency.builder().code("DRF").build();
		List<Currency> currenciesA = new LinkedList<>(Arrays.asList(currOne, currTwo, currThree, currFour));

		Currency currFive = Currency.builder().code("ABC").build();
		Currency currSix = Currency.builder().code("CDE").build();
		List<Currency> currenciesB = new LinkedList<>(Arrays.asList(currFive, currSix));

		// when
		currenciesA.removeAll(currenciesB);

		// then
		Assertions.assertEquals(currenciesA.size(), 2);
		Assertions.assertTrue(currenciesA.contains(currFour));
		Assertions.assertTrue(currenciesA.contains(currTwo));
		Assertions.assertFalse(currenciesA.contains(currOne));
		Assertions.assertFalse(currenciesA.contains(currThree));
	}

}
