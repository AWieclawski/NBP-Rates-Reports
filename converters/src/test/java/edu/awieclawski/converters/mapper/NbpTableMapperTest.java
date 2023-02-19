package edu.awieclawski.converters.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import edu.awieclawski.commons.dtos.rates.NbpATableRate;
import edu.awieclawski.commons.dtos.responses.NbpATable;
import edu.awieclawski.converters.mappers.NbpRatesMapper;

class NbpTableMapperTest {
	private List<NbpATable<NbpATableRate>> dtoResponse;

	@BeforeEach
	public void setup() {
		dtoResponse = new ArrayList<>();
	}

	@Test
	public void testNbpATableWithNbpATableRateForValidDateReturnOk() {
		// given
		String json =
				"[{\"table\":\"A\",\"no\":\"062/A/NBP/2022\",\"effectiveDate\":\"2022-03-30\",\"rates\":[{\"currency\":\"bat (Tajlandia)\",\"code\":\"THB\",\"mid\":0.1252},{\"currency\":\"dolar amerykański\",\"code\":\"USD\",\"mid\":4.1688}]},"
						+ "{\"table\":\"A\",\"no\":\"063/A/NBP/2022\",\"effectiveDate\":\"2022-03-31\",\"rates\":[{\"currency\":\"bat (Tajlandia)\",\"code\":\"THB\",\"mid\":0.1253},{\"currency\":\"dolar amerykański\",\"code\":\"USD\",\"mid\":4.1689}]}]";
		NbpRatesMapper<List<NbpATable<NbpATableRate>>> mapper =
				new NbpRatesMapper<>(new TypeReference<List<NbpATable<NbpATableRate>>>() {
				});

		// then
		Assertions.assertDoesNotThrow(() -> dtoResponse.addAll(mapper.map(json)));

		if (dtoResponse != null && dtoResponse.get(0).getRates() != null) {
			Assertions.assertEquals("062/A/NBP/2022",(dtoResponse.get(0).getNo()));
			Assertions.assertEquals(LocalDate.parse("2022-03-30"),(dtoResponse.get(0).getEffectiveDate()));

			NbpATableRate rate = dtoResponse.get(0).getRates().get(0);
			Assertions.assertEquals("THB",(rate.getCode()));
			Assertions.assertEquals(new BigDecimal("0.1252"),(rate.getMid()));

			rate = dtoResponse.get(0).getRates().get(1);
			Assertions.assertEquals("USD",(rate.getCode()));
			Assertions.assertEquals(new BigDecimal("4.1688"),(rate.getMid()));
			Assertions.assertEquals(dtoResponse.size(),2);
		}
	}

}
