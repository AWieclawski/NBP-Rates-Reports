package edu.awieclawski.data.daos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import edu.awieclawski.data.daos.implemented.RatesTypeCRepository;
import edu.awieclawski.models.entities.Currency;
import edu.awieclawski.models.entities.ExchangeRate;
import edu.awieclawski.models.entities.ExchangeRateTypeB;
import edu.awieclawski.models.entities.ExchangeRateTypeC;

/**
 * https://programmingtechie.com/2020/10/21/spring-boot-testing-tutorial-database-testing-with-test-containers/
 * 
 * @author awieclawski
 *
 */
@DataJpaTest
@ActiveProfiles("test")
@Sql( scripts = "classpath:/test-schema.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TypeCRateRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private CurrencyRepository currencyRepository;

	@Autowired
	private RatesTypeCRepository ratesCRepository;

	private final LocalDate DATE = LocalDate.now().minusDays(1);
	private final String TABLE_B = "XXX/B/NBP/YYYY";
	private final String TABLE_C = "XXX/C/NBP/YYYY";
	private final String CODE_EUR = "EUR";
	private final String CODE_USD = "USD";
	private Currency CURRENCY_EUR;
	private Currency CURRENCY_USD;

	@BeforeEach
	void init() {
		CURRENCY_EUR = getCurrencyEntity(CODE_EUR);
		entityManager.persist(CURRENCY_EUR);
		CURRENCY_USD = getCurrencyEntity(CODE_USD);
		entityManager.persist(CURRENCY_USD);
	}

	@Test
	void findByDatesRangeAndSymbolListReturnValidExchangeRateTypeCPersistedWithCurrency() {
		// given
		BigDecimal ask1 = new BigDecimal("2.2378");
		BigDecimal bid1 = new BigDecimal("2.3498");
		BigDecimal ask2 = new BigDecimal("2.2332");
		BigDecimal bid2 = new BigDecimal("2.3454");

		ExchangeRate exchRateCEur1 = getExchangeRateTypeC(CURRENCY_EUR, ask1, bid1);
		LocalDate dateOne = DATE.minusDays(1); // range end
		exchRateCEur1.setPublished(dateOne);

		entityManager.persist(exchRateCEur1);
		ExchangeRate exchRateCUsd2 = getExchangeRateTypeC(CURRENCY_USD, ask2, bid2);
		LocalDate dateTwo = DATE.minusDays(3); // range start
		exchRateCUsd2.setPublished(dateTwo);
		entityManager.persist(exchRateCUsd2);

		BigDecimal rateEx = new BigDecimal("2.3465");
		ExchangeRate exchRateBEur = getExchangeRateTypeB(CURRENCY_EUR, rateEx);
		LocalDate dateDis = DATE.minusDays(2);
		exchRateBEur.setPublished(dateDis);
		entityManager.persist(exchRateBEur); // disruption in the range

		// when
		List<Currency> currencies = currencyRepository.findAll();

		// then
		Assertions.assertEquals(2, currencies.size());
		Assertions.assertTrue(currencies.containsAll(List.of(CURRENCY_EUR, CURRENCY_USD)));

		// when
		List<ExchangeRateTypeC> ratesC = ratesCRepository.findByDatesRangeAndSymbolList(dateTwo, dateOne,
				List.of(CODE_EUR, CODE_USD));

		// then
		Assertions.assertEquals(2, ratesC.size());
		Assertions.assertTrue(
				ratesC.stream().map(ExchangeRate::getCurrency).anyMatch(curr -> curr.getCode().equals(CODE_EUR)));
		Assertions.assertTrue(
				ratesC.stream().map(rate -> (ExchangeRateTypeC) rate).anyMatch(rate -> rate.getAsk().equals(ask1)));
		Assertions.assertTrue(
				ratesC.stream().map(rate -> (ExchangeRateTypeC) rate).anyMatch(rate -> rate.getBid().equals(bid1)));
		Assertions.assertTrue(
				ratesC.stream().map(ExchangeRate::getCurrency).anyMatch(curr -> curr.getCode().equals(CODE_USD)));
		Assertions.assertTrue(
				ratesC.stream().map(rate -> (ExchangeRateTypeC) rate).anyMatch(rate -> rate.getAsk().equals(ask2)));
		Assertions.assertTrue(
				ratesC.stream().map(rate -> (ExchangeRateTypeC) rate).anyMatch(rate -> rate.getBid().equals(bid2)));
		Assertions.assertTrue(
				ratesC.stream().map(rate -> (ExchangeRateTypeC) rate).noneMatch(rate -> rate.getBid().equals(rateEx)));
		Assertions.assertTrue(
				ratesC.stream().map(rate -> (ExchangeRateTypeC) rate).noneMatch(rate -> rate.getAsk().equals(rateEx)));

	}

	@Test
	void findValidFromDateReturnPublishedNextBusinessDayExchangeRateTypeA() {
		// given
		LocalDate validFrom = DATE.minusDays(2); // must be after exchRateOne
		ExchangeRateTypeC exchRateOne = ExchangeRateTypeC.builder()
				.currency(CURRENCY_EUR)
				.nbpTable("011/C/NBP/2023")
				.ask(new BigDecimal("1.3456"))
				.bid(new BigDecimal("1.3467"))
				.published(DATE.minusDays(3))
				.build();
		// published at next business day after valid from date
		ExchangeRateTypeC exchRateTwo = ExchangeRateTypeC.builder()
				.currency(CURRENCY_EUR)
				.nbpTable("013/C/NBP/2023")
				.ask(new BigDecimal("1.3478"))
				.bid(new BigDecimal("1.3489"))
				.published(DATE.minusDays(1))
				.build();
		ExchangeRateTypeC exchRateThree = ExchangeRateTypeC.builder()
				.currency(CURRENCY_EUR)
				.nbpTable("014/C/NBP/2023")
				.ask(new BigDecimal("1.3490"))
				.bid(new BigDecimal("1.3491"))
				.published(DATE)
				.build();

		entityManager.persist(exchRateOne);
		entityManager.persist(exchRateTwo);
		entityManager.persist(exchRateThree);
		entityManager.persist(getExchangeRateTypeB(CURRENCY_EUR, DATE.minusDays(9))); // disruption

		// when
		ExchangeRateTypeC foundC = ratesCRepository.findValidFromDate(validFrom);

		// then
		Assertions.assertEquals(foundC, exchRateTwo);
		Assertions.assertTrue(ratesCRepository.findIfExistsBeforeDate(validFrom));
		Assertions.assertTrue(ratesCRepository.findIfExistsByCodeBeforeDate(CODE_EUR, validFrom));
		Assertions.assertFalse(ratesCRepository.findIfExistsByCodeBeforeDate(CODE_USD, validFrom));
		Assertions.assertFalse(ratesCRepository.findIfExistsBeforeDate(DATE.minusDays(8)));
	}

	private ExchangeRateTypeB getExchangeRateTypeB(Currency currency, LocalDate date) {

		return ExchangeRateTypeB.builder()
				.currency(currency)
				.nbpTable("013/B/NBP/2023")
				.rate(new BigDecimal("1.4587"))
				.published(date)
				.build();
	}

	private ExchangeRateTypeB getExchangeRateTypeB(Currency currency, BigDecimal rate) {

		return ExchangeRateTypeB.builder()
				.currency(currency)
				.nbpTable(TABLE_B)
				.rate(rate)
				.published(DATE)
				.build();
	}

	private ExchangeRateTypeC getExchangeRateTypeC(Currency currency, BigDecimal ask, BigDecimal bid) {

		return ExchangeRateTypeC.builder()
				.currency(currency)
				.nbpTable(TABLE_C)
				.ask(ask)
				.bid(bid)
				.published(DATE)
				.build();
	}

	private Currency getCurrencyEntity(String code) {

		return Currency.builder()
				.code(code)
				.description(code + " description")
				.build();
	}

}
