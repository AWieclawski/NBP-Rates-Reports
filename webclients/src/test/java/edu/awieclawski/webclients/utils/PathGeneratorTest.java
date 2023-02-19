package edu.awieclawski.webclients.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@ActiveProfiles("test")
class PathGeneratorTest {

	@Autowired
	private PathGenerator pathGenerator;

	private MultiValueMap<String, String> params;
	private final URI[] uri = new URI[1];

	@BeforeEach
	void init() {
		uri[0] = null;
		params = null;
	}

	@Test
	void buildFinalUriReturnOk() throws URISyntaxException {
		// given
		params = pathGenerator.setParamsMap("format", "json");
		URI fullUri = new URI("http://api.nbp.pl/api/exchangerates/rates");
		// then
		Assertions.assertDoesNotThrow(() -> uri[0] = pathGenerator.buildFinalUri(params, fullUri));
		Assertions.assertTrue(uri[0].toString().equals("http://api.nbp.pl/api/exchangerates/rates?format=json"));
	}

	@Test
	void completeUriWithSingleDateReturnOk() throws URISyntaxException {
		// given
		String endPoint = "exchangerates/rates/a";
		String currencySymbol = "PLN";
		LocalDate publicationDate = LocalDate.of(2022, 5, 27);
		// then
		Assertions.assertDoesNotThrow(
				() -> uri[0] = pathGenerator.completeUri(endPoint, currencySymbol, publicationDate));
		Assertions.assertTrue(uri[0].toString().equals("exchangerates/rates/a/pln/2022-05-27"));
	}

	@Test
	void completeTableUriWithSingleDateReturnOk() throws URISyntaxException {
		// given
		String endPoint = "exchangerates/tables/a";
		LocalDate publicationDate = LocalDate.of(2022, 5, 27);
		// then
		Assertions.assertDoesNotThrow(
				() -> uri[0] = pathGenerator.completeUri(endPoint, publicationDate));
		Assertions.assertTrue(uri[0].toString().equals("exchangerates/tables/a/2022-05-27"));
	}

	@Test
	@Disabled
	void completeUriWithSingleDateNullThrowRuntimeException() throws URISyntaxException {
		// given
		String endPoint = "exchangerates/rates/a";
		String currencySymbol = "PLN";
		LocalDate publicationDate = null;
		// then
		RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
				() -> pathGenerator.completeUri(endPoint, currencySymbol, publicationDate));
		Assertions.assertTrue(exception.getMessage().contains(PathGenerator.ERR_MSG));
	}

	@Test
	void completeUriWithDateRangeReturnOk() throws URISyntaxException {
		// given
		String endPoint = "exchangerates/rates/a";
		String currencySymbol = "EUR";
		LocalDate startDate = LocalDate.of(2022, 5, 23);
		LocalDate endDate = LocalDate.of(2022, 5, 27);
		// then
		Assertions.assertDoesNotThrow(
				() -> uri[0] = pathGenerator.completeUri(endPoint, currencySymbol, startDate, endDate));
		Assertions.assertTrue(uri[0].toString().equals("exchangerates/rates/a/eur/2022-05-23/2022-05-27"));
	}

	@Test
	@Disabled
	void completeUriWithDateRangeNullThrowRuntimeException() throws URISyntaxException {
		// given
		String endPoint = "exchangerates/rates/a";
		String currencySymbol = "EUR";
		LocalDate startDate = null;
		LocalDate endDate = LocalDate.of(2022, 5, 27);
		// then
		RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
				() -> pathGenerator.completeUri(endPoint, currencySymbol, startDate, endDate));
		Assertions.assertTrue(exception.getMessage().contains(PathGenerator.ERR_MSG));
	}

}
