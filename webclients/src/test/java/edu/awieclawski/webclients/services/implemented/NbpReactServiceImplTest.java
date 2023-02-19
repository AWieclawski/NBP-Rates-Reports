package edu.awieclawski.webclients.services.implemented;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import edu.awieclawski.webclients.exceptions.NbpIntegrationException;
import edu.awieclawski.webclients.services.ConnectionsRepeater;
import edu.awieclawski.webclients.services.NbpReactService;

@SpringBootTest
@ActiveProfiles("test")
class NbpReactServiceImplTest implements ConnectionsRepeater {

	@Autowired
	private NbpReactService nbpIntegrationService;

	@Test
	void testGetATypeRateByValidDateAndValidSymbolDoesNotThrow() {
		LocalDate date = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchChannelException(() -> nbpIntegrationService.getATypeRateByDateAndSymbol(date, "eur"), 0));
	}

	@Test
	void testGetATypeRatesTableByValidDateAndValidSymbolDoesNotThrow() {
		LocalDate date = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchChannelException(() -> nbpIntegrationService.getATypeRatesTableByDate(date), 0));
	}

	@Test
	void testGetATypeRateByNotValidDateAndValidSymbolThrowsException() {
		LocalDate date = LocalDate.of(2022, 5, 29); // non business day

		Assertions.assertThrows(NbpIntegrationException.class,
				() -> tryCatchChannelException(() -> nbpIntegrationService.getATypeRateByDateAndSymbol(date, "eur"), 0));
	}

	@Test
	void getATypeRateByDatesRangeAndSymbollForValidDateAndValidSymbolDoesNotThrow() {
		LocalDate startDate = LocalDate.of(2022, 5, 23); // business day
		LocalDate endDate = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchChannelException(
						() -> nbpIntegrationService.getATypeRatesByDatesRangeAndSymbol(startDate, endDate, "eur"), 0));
	}

	@Test
	void getATypeRatesTableByDatesRangeAndSymbollForValidDateAndValidSymbolDoesNotThrow() {
		LocalDate startDate = LocalDate.of(2022, 5, 23); // business day
		LocalDate endDate = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchChannelException(
						() -> nbpIntegrationService.getATypeRatesTableByDatesRange(startDate, endDate), 0));
	}

	@Test
	void getATypeRateByDatesRangeAndSymbollForNotValidSymbolThrowsException() {
		LocalDate startDate = LocalDate.of(2022, 5, 23); // business day
		LocalDate endDate = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertThrows(RuntimeException.class,
				() -> tryCatchChannelException(
						() -> nbpIntegrationService.getATypeRatesByDatesRangeAndSymbol(startDate, endDate, "xyz"), 0));
	}

}
