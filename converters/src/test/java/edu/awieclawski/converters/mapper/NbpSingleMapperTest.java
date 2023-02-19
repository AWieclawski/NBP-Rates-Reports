package edu.awieclawski.converters.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import edu.awieclawski.commons.dtos.rates.NbpARate;
import edu.awieclawski.commons.dtos.responses.NbpSingle;
import edu.awieclawski.converters.mappers.NbpRatesMapper;

class NbpSingleMapperTest {

	private List<NbpSingle<NbpARate>> dtoList;

	@BeforeEach
	public void setup() {
		dtoList = new ArrayList<>();
	}

	@Test
	void testNbpGenericMapperHavingNbpSingleWithNbpARateForValidDateReturnOk() {
		// given
		String json = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"102/A/NBP/2022\",\"effectiveDate\":\"2022-05-27\",\"mid\":4.6102}]}";
		NbpRatesMapper<NbpSingle<NbpARate>> mapper = new NbpRatesMapper<>(new TypeReference<NbpSingle<NbpARate>>() {
		});
		// then
		Assertions.assertDoesNotThrow(() -> dtoList.add(mapper.map(json)));

		if (dtoList.get(0) != null && dtoList.get(0).getRates() != null) {
			NbpARate rate = dtoList.get(0).getRates().get(0);
			Assertions.assertEquals("102/A/NBP/2022", (rate.getNo()));
			Assertions.assertEquals(LocalDate.parse("2022-05-27"), (rate.getEffectiveDate()));
			Assertions.assertEquals(new BigDecimal("4.6102"), (rate.getMid()));
		}
	}

	@Test
	void testNbpGenericMapperHavingNbpSingleWithNbpARateForValidDateRangeReturnOk() {
		// given
		String json = "{\"table\":\"A\",\"currency\":\"dolar amerykański\",\"code\":\"USD\",\"rates\":[{\"no\":\"206/A/NBP/2022\",\"effectiveDate\":\"2022-10-24\",\"mid\":4.8669},{\"no\":\"207/A/NBP/2022\",\"effectiveDate\":\"2022-10-25\",\"mid\":4.8499}]}";
		NbpRatesMapper<NbpSingle<NbpARate>> mapper = new NbpRatesMapper<>(new TypeReference<NbpSingle<NbpARate>>() {
		});

		// then NbpSingle<NbpARate>
		Assertions.assertDoesNotThrow(() -> dtoList.add(mapper.map(json)));

		if (dtoList.get(0) != null && dtoList.get(0).getRates() != null) {
			NbpARate rate = dtoList.get(0).getRates().get(0);
			Assertions.assertEquals("206/A/NBP/2022", (rate.getNo()));
			Assertions.assertEquals(LocalDate.parse("2022-10-24"), (rate.getEffectiveDate()));
			Assertions.assertEquals(new BigDecimal("4.8669"), (rate.getMid()));

			rate = dtoList.get(0).getRates().get(1);
			Assertions.assertEquals("207/A/NBP/2022", (rate.getNo()));
			Assertions.assertEquals(LocalDate.parse("2022-10-25"), (rate.getEffectiveDate()));
			Assertions.assertEquals(new BigDecimal("4.8499"), (rate.getMid()));
		}
	}

}
