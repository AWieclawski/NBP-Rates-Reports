package edu.awieclawski.webclients.services.implemented;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import edu.awieclawski.webclients.exceptions.NbpIntegrationException;
import edu.awieclawski.webclients.services.ConnectionsRepeater;
import edu.awieclawski.webclients.services.NbpIntegrationService;

@SpringBootTest
@ActiveProfiles("test")
class NbpReactServiceImplTest implements ConnectionsRepeater {

	@Autowired
	private NbpIntegrationService nbpIntegrationService;

	@Test
	void testGetATypeRateByValidDateAndValidSymbolDoesNotThrow() {
		LocalDate date = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(() -> nbpIntegrationService.getATypeRateByDateAndSymbol(date, "eur"), 0));
	}

	@Test
	void testGetCTypeRateByValidDateAndValidSymbolDoesNotThrow() {
		LocalDate date = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(() -> nbpIntegrationService.getCTypeRateByDateAndSymbol(date, "eur"), 0));
	}

	@Test
	void testGetATypeTableByValidDateAndValidSymbolDoesNotThrow() {
		LocalDate date = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(() -> nbpIntegrationService.getATypeTableByDate(date), 0));
	}

	@Test
	void testGetBTypeTableByValidDateAndValidSymbolDoesNotThrow() {
		LocalDate date = LocalDate.of(2022, 5, 25); // once week Wednesday.

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(() -> nbpIntegrationService.getBTypeTableByDate(date), 0));
	}

	@Test
	void testGetCTypeTableByValidDateAndValidSymbolDoesNotThrow() {
		LocalDate date = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(() -> nbpIntegrationService.getCTypeTableByDate(date), 0));
	}

	@Test
	void testGetATypeRateByNotValidDateAndValidSymbolThrowsException() {
		LocalDate date = LocalDate.of(2022, 5, 29); // non business day

		Assertions.assertThrows(NbpIntegrationException.class,
				() -> tryCatchException(() -> nbpIntegrationService.getATypeRateByDateAndSymbol(date, "eur"), 0));
	}

	@Test
	void getATypeRateByDatesRangeAndSymbollForValidDateAndValidSymbolDoesNotThrow() {
		LocalDate startDate = LocalDate.of(2022, 5, 23); // business day
		LocalDate endDate = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(
						() -> nbpIntegrationService.getATypeRatesByDatesRangeAndSymbol(startDate, endDate, "eur"), 0));
	}

	@Test
	void getCTypeRateByDatesRangeAndSymbollForValidDateAndValidSymbolDoesNotThrow() {
		LocalDate startDate = LocalDate.of(2022, 5, 23); // business day
		LocalDate endDate = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(
						() -> nbpIntegrationService.getCTypeRatesByDatesRangeAndSymbol(startDate, endDate, "eur"), 0));
	}

	@Test
	void getATypeTablesByDatesRangeAndSymbollForValidDateAndValidSymbolDoesNotThrow() {
		LocalDate startDate = LocalDate.of(2022, 5, 23); // business day
		LocalDate endDate = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(
						() -> nbpIntegrationService.getATypeTableByDatesRange(startDate, endDate), 0));
	}

	@Test
	void getBTypeTablesByDatesRangeAndSymbollForValidDateAndValidSymbolDoesNotThrow() {
		LocalDate startDate = LocalDate.of(2022, 5, 23); // business day
		LocalDate endDate = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(
						() -> nbpIntegrationService.getBTypeTableByDatesRange(startDate, endDate), 0));
	}

	@Test
	void getCTypeTablesByDatesRangeAndSymbollForValidDateAndValidSymbolDoesNotThrow() {
		LocalDate startDate = LocalDate.of(2022, 5, 23); // business day
		LocalDate endDate = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertDoesNotThrow(
				() -> tryCatchException(
						() -> nbpIntegrationService.getCTypeTableByDatesRange(startDate, endDate), 0));
	}

	@Test
	void getATypeRateByDatesRangeAndSymbollForNotValidSymbolThrowsException() {
		LocalDate startDate = LocalDate.of(2022, 5, 23); // business day
		LocalDate endDate = LocalDate.of(2022, 5, 27); // business day

		Assertions.assertThrows(RuntimeException.class,
				() -> tryCatchException(
						() -> nbpIntegrationService.getATypeRatesByDatesRangeAndSymbol(startDate, endDate, "xyz"), 0));
	}

}
