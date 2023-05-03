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

import edu.awieclawski.models.entities.Currency;
import edu.awieclawski.models.entities.ExchangeRate;
import edu.awieclawski.models.entities.ExchangeRateTypeA;
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
class ExchangeRateRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private CurrencyRepository currencyRepository;

	@Autowired
	private ExchangeRateRepository exchangeRateRepository;

	private final LocalDate DATE = LocalDate.now().minusDays(1);
	private final String TABLE_A = "XXX/A/NBP/YYYY";
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
	void findByCodePublishedTableReturnValidExchangeRateTypeAPersistedWithCurrency() {
		// given
		BigDecimal rateEur = new BigDecimal("2.2345");
		BigDecimal rateUsd = new BigDecimal("2.3456");

		ExchangeRate exchRateEur = getExchangeRateTypeA(CURRENCY_EUR, rateEur);
		entityManager.persist(exchRateEur);

		ExchangeRate exchRateUsd = getExchangeRateTypeA(CURRENCY_USD, rateUsd);
		entityManager.persist(exchRateUsd);

		ExchangeRate exchRateBUsd = getExchangeRateTypeB(CURRENCY_USD, rateUsd);
		entityManager.persist(exchRateBUsd); // disruption

		// when
		List<Currency> currencies = currencyRepository.findAll();

		// then
		Assertions.assertEquals(2, currencies.size());
		Assertions.assertTrue(currencies.containsAll(List.of(CURRENCY_EUR, CURRENCY_USD)));

		// when
		List<ExchangeRate> rates = exchangeRateRepository.findByCodePublishedTable(CODE_EUR, DATE, TABLE_A);

		// then
		Assertions.assertEquals(1, rates.size());
		Assertions.assertTrue(
				rates.stream().map(ExchangeRate::getCurrency).allMatch(curr -> curr.getCode().equals(CODE_EUR)));
		Assertions.assertTrue(
				rates.stream().map(rate -> (ExchangeRateTypeA) rate).allMatch(rate -> rate.getRate().equals(rateEur)));
		Assertions
				.assertTrue(exchangeRateRepository.existsByCurrencyCodeAndNbpTable(CODE_EUR, TABLE_A));

		// when
		rates = exchangeRateRepository.findByCodePublishedTable(CODE_USD, DATE, TABLE_A);

		// then
		Assertions.assertEquals(1, rates.size());
		Assertions.assertTrue(
				rates.stream().map(ExchangeRate::getCurrency).allMatch(curr -> curr.getCode().equals(CODE_USD)));
		Assertions.assertTrue(
				rates.stream().map(rate -> (ExchangeRateTypeA) rate).allMatch(rate -> rate.getRate().equals(rateUsd)));
		Assertions
				.assertTrue(exchangeRateRepository.existsByCurrencyCodeAndNbpTable(CODE_USD, TABLE_A));
	}

	@Test
	void findByCodePublishedTableReturnValidExchangeRateTypeBPersistedWithCurrency() {
		// given
		BigDecimal rateEur = new BigDecimal("2.2354");
		BigDecimal rateUsd = new BigDecimal("2.3465");

		ExchangeRate exchRateEur = getExchangeRateTypeB(CURRENCY_EUR, rateEur);
		entityManager.persist(exchRateEur);

		ExchangeRate exchRateUsd = getExchangeRateTypeB(CURRENCY_USD, rateUsd);
		entityManager.persist(exchRateUsd);

		ExchangeRate exchRateAUsd = getExchangeRateTypeA(CURRENCY_USD, rateUsd);
		entityManager.persist(exchRateAUsd); // disruption

		// when
		List<Currency> currencies = currencyRepository.findAll();

		// then
		Assertions.assertEquals(2, currencies.size());
		Assertions.assertTrue(currencies.containsAll(List.of(CURRENCY_EUR, CURRENCY_USD)));

		// when
		List<ExchangeRate> rates = exchangeRateRepository.findByCodePublishedTable(CODE_EUR, DATE, TABLE_B);

		// then
		Assertions.assertEquals(1, rates.size());
		Assertions.assertTrue(
				rates.stream().map(ExchangeRate::getCurrency).allMatch(curr -> curr.getCode().equals(CODE_EUR)));
		Assertions.assertTrue(
				rates.stream().map(rate -> (ExchangeRateTypeB) rate).allMatch(rate -> rate.getRate().equals(rateEur)));
		Assertions
				.assertTrue(exchangeRateRepository.existsByCurrencyCodeAndNbpTable(CODE_EUR, TABLE_B));

		// when
		rates = exchangeRateRepository.findByCodePublishedTable(CODE_USD, DATE, TABLE_B);

		// then
		Assertions.assertEquals(1, rates.size());
		Assertions.assertTrue(
				rates.stream().map(ExchangeRate::getCurrency).allMatch(curr -> curr.getCode().equals(CODE_USD)));
		Assertions.assertTrue(
				rates.stream().map(rate -> (ExchangeRateTypeB) rate).allMatch(rate -> rate.getRate().equals(rateUsd)));
		Assertions
				.assertTrue(exchangeRateRepository.existsByCurrencyCodeAndNbpTable(CODE_USD, TABLE_B));
	}

	@Test
	void findByCodePublishedTableReturnValidExchangeRateTypeCPersistedWithCurrency() {
		// given
		BigDecimal ask = new BigDecimal("2.2378");
		BigDecimal bid = new BigDecimal("2.3498");

		ExchangeRate exchRateCEur = getExchangeRateTypeC(CURRENCY_EUR, ask, bid);
		entityManager.persist(exchRateCEur);

		ExchangeRate exchRateBEur = getExchangeRateTypeB(CURRENCY_EUR, new BigDecimal("2.3465"));
		entityManager.persist(exchRateBEur); // disruption

		// when
		List<Currency> currencies = currencyRepository.findAll();

		// then
		Assertions.assertEquals(2, currencies.size());
		Assertions.assertTrue(currencies.containsAll(List.of(CURRENCY_EUR, CURRENCY_USD)));

		// when
		List<ExchangeRate> rates = exchangeRateRepository.findByCodePublishedTable(CODE_EUR, DATE, TABLE_C);

		// then
		Assertions.assertEquals(1, rates.size());
		Assertions.assertTrue(
				rates.stream().map(ExchangeRate::getCurrency).allMatch(curr -> curr.getCode().equals(CODE_EUR)));
		Assertions.assertTrue(
				rates.stream().map(rate -> (ExchangeRateTypeC) rate).allMatch(rate -> rate.getAsk().equals(ask)));
		Assertions.assertTrue(
				rates.stream().map(rate -> (ExchangeRateTypeC) rate).allMatch(rate -> rate.getBid().equals(bid)));
		Assertions
				.assertTrue(exchangeRateRepository.existsByCurrencyCodeAndNbpTable(CODE_EUR, TABLE_C));

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

		// then
		Assertions
				.assertTrue(exchangeRateRepository.existsByCurrencyCodeAndNbpTable(CODE_EUR, TABLE_C));
		Assertions
				.assertTrue(exchangeRateRepository.existsByCurrencyCodeAndNbpTable(CODE_USD, TABLE_C));
	}

	private ExchangeRateTypeA getExchangeRateTypeA(Currency currency, BigDecimal rate) {

		return ExchangeRateTypeA.builder()
				.currency(currency)
				.nbpTable(TABLE_A)
				.rate(rate)
				.published(DATE)
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
