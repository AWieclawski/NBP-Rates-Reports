package edu.awieclawski.schedulers.validators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ch.qos.logback.classic.Level;
import edu.awieclawski.commons.tools.MemmoryAppender;
import edu.awieclawski.htmltopdf.dtos.ReportDto;
import edu.awieclawski.schedulers.exceptions.ReportValidationException;

@SpringBootTest
class ReportDtoValidatorTest extends MemmoryAppender<ReportDtoValidator> {

	@Autowired
	private ReportDtoValidator validator;

	@Test
	void testReportDtoBothDatesNullThrowException() {
		// given
		ReportDto dto = ReportDto.builder().currencies(Arrays.asList("AAA", "BBB"))
			.validToStart(null)
			.validToEnd(null)
			.build();
		// then
		Throwable t = Assertions.assertThrows(ReportValidationException.class, () -> validator.validate(dto));
		Assertions.assertTrue(
			t.getMessage().toLowerCase().contains("null") && t.getMessage().toLowerCase().contains("both"));
	}

	@Test
	void testReportDtoCurrenciesWithSymbolLongerThanThreeReturnWarningOnlyAndRemoweWrongSymbol() {
		initMemoryAppender(Level.WARN);
		final String WRONG = "ABCD";
		// given
		ReportDto dto = ReportDto.builder()
			.currencies(new ArrayList<>(Arrays.asList("AAA", "BBB", WRONG)))
			.validToStart(LocalDate.now().minusDays(10))
			.validToEnd(LocalDate.now().minusDays(1))
			.build();
		// then
		Assertions.assertDoesNotThrow(() -> validator.validate(dto));
		Assertions.assertTrue(countEventsForLogger() > 0);
		Assertions.assertFalse(dto.getCurrencies().contains(WRONG));
		cleanUpMemoryAppender();
	}

	@Test
	void testReportDtoCurrenciesWithSymbolConsistNotOnlyLettersReturnWarningOnlyAndRemoweWrongSymbol() {
		initMemoryAppender(Level.WARN);
		final String WRONG = "1B/CD";
		// given
		ReportDto dto = ReportDto.builder()
			.currencies(new ArrayList<>(Arrays.asList("AAA", "BBB", WRONG)))
			.validToStart(LocalDate.now().minusDays(10))
			.validToEnd(LocalDate.now().minusDays(1))
			.build();
		// then
		Assertions.assertDoesNotThrow(() -> validator.validate(dto));
		Assertions.assertTrue(countEventsForLogger() > 0);
		Assertions.assertFalse(dto.getCurrencies().contains(WRONG));
		cleanUpMemoryAppender();
	}

	@Test
	void testReportDtoDateStartNullReturnWarningOnlyAndEqualToDateEnd() {
		initMemoryAppender(Level.WARN);
		// given
		ReportDto dto = ReportDto.builder().currencies(Arrays.asList("AAA", "BBB"))
			.validToStart(null)
			.validToEnd(LocalDate.now().minusDays(1))
			.build();
		// then
		Assertions.assertDoesNotThrow(() -> validator.validate(dto));
		Assertions.assertEquals(dto.getValidToEnd(), dto.getValidToStart());
		Assertions.assertTrue(countEventsForLogger() > 0);
		cleanUpMemoryAppender();
	}

	@Test
	void testReportDtoDateEndNullReturnWarningOnlyAndEqualToDateStart() {
		initMemoryAppender(Level.WARN);
		// given
		ReportDto dto = ReportDto.builder().currencies(Arrays.asList("AAA", "BBB"))
			.validToStart(LocalDate.now().minusDays(1))
			.validToEnd(null)
			.build();
		// then
		Assertions.assertDoesNotThrow(() -> validator.validate(dto));
		Assertions.assertEquals(dto.getValidToEnd(), dto.getValidToStart());
		Assertions.assertTrue(countEventsForLogger() > 0);
		cleanUpMemoryAppender();
	}

	@Test
	void testReportDtoDateEndBeforeDateStartReturnWarningOnlyAndReplaceDates() {
		initMemoryAppender(Level.WARN);
		// given
		ReportDto dto = ReportDto.builder().currencies(Arrays.asList("AAA", "BBB"))
			.validToStart(LocalDate.now().minusDays(1))
			.validToEnd(LocalDate.now().minusDays(2))
			.build();
		// then
		Assertions.assertDoesNotThrow(() -> validator.validate(dto));
		Assertions.assertTrue(dto.getValidToEnd().isAfter(dto.getValidToStart()));
		Assertions.assertTrue(countEventsForLogger() > 0);
		cleanUpMemoryAppender();
	}

	@Test
	void testReportDtoCurrenciesNullReturnWarningOnly() {
		initMemoryAppender(Level.WARN);
		// given
		ReportDto dto = ReportDto.builder().currencies(null)
			.validToStart(LocalDate.now().minusDays(2))
			.validToEnd(LocalDate.now().minusDays(1))
			.build();
		// then
		Assertions.assertDoesNotThrow(() -> validator.validate(dto));
		Assertions.assertTrue(dto.getValidToEnd().isAfter(dto.getValidToStart()));
		Assertions.assertTrue(countEventsForLogger() > 0);
		cleanUpMemoryAppender();
	}

	@Test
	void testReportDtoFutureEndDateReturnWarningOnlyAndChangeDateToYesterday() {
		initMemoryAppender(Level.WARN);
		// given
		ReportDto dto = ReportDto.builder()
			.currencies(null)
			.validToStart(LocalDate.now().plusDays(2))
			.validToEnd(LocalDate.now().minusDays(3))
			.build();
		// then
		Assertions.assertDoesNotThrow(() -> validator.validate(dto));
		Assertions.assertTrue(dto.getValidToEnd().isAfter(dto.getValidToStart()));
		Assertions.assertTrue(dto.getValidToStart().isBefore(LocalDate.now()));
		Assertions.assertTrue(countEventsForLogger() > 0);
		cleanUpMemoryAppender();
	}

}
