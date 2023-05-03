package edu.awieclawski.data.daos;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import edu.awieclawski.data.daos.implemented.RatesTypeARepository;
import edu.awieclawski.models.entities.Currency;
import edu.awieclawski.models.entities.ExchangeRateTypeA;
import edu.awieclawski.models.entities.ExchangeRateTypeB;

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
class TypeBRateRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private RatesTypeARepository ratesARepository;

	private final LocalDate DATE = LocalDate.now().minusDays(1);
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
	void findValidFromDateReturnPublishedNextBusinessDayExchangeRateTypeA() {
		// given
		LocalDate validFrom = DATE.minusDays(2); // must be after exchRateOne
		ExchangeRateTypeA exchRateOne = ExchangeRateTypeA.builder()
				.currency(CURRENCY_EUR)
				.nbpTable("011/A/NBP/2023")
				.rate(new BigDecimal("1.2345"))
				.published(DATE.minusDays(3))
				.build();
		// published at next business day after valid from date
		ExchangeRateTypeA exchRateTwo = ExchangeRateTypeA.builder()
				.currency(CURRENCY_EUR)
				.nbpTable("013/A/NBP/2023")
				.rate(new BigDecimal("1.3456"))
				.published(DATE.minusDays(1))
				.build();
		ExchangeRateTypeA exchRateThree = ExchangeRateTypeA.builder()
				.currency(CURRENCY_EUR)
				.nbpTable("014/A/NBP/2023")
				.rate(new BigDecimal("1.4567"))
				.published(DATE)
				.build();

		entityManager.persist(exchRateOne);
		entityManager.persist(exchRateTwo);
		entityManager.persist(exchRateThree);
		entityManager.persist(getExchangeRateTypeB(CURRENCY_EUR, DATE.minusDays(9))); // disruption
		
		//when
		ExchangeRateTypeA foundA = ratesARepository.findValidFromDate(validFrom);

		// then
		Assertions.assertEquals(foundA, exchRateTwo);
		Assertions.assertTrue(ratesARepository.findIfExistsBeforeDate(validFrom));
		Assertions.assertTrue(ratesARepository.findIfExistsByCodeBeforeDate(CODE_EUR, validFrom));
		Assertions.assertFalse(ratesARepository.findIfExistsByCodeBeforeDate(CODE_USD, validFrom));
		Assertions.assertFalse(ratesARepository.findIfExistsBeforeDate(DATE.minusDays(8)));
	}

	private ExchangeRateTypeB getExchangeRateTypeB(Currency currency, LocalDate date) {

		return ExchangeRateTypeB.builder()
				.currency(currency)
				.nbpTable("013/B/NBP/2023")
				.rate(new BigDecimal("1.4587"))
				.published(date)
				.build();
	}

	private Currency getCurrencyEntity(String code) {

		return Currency.builder()
				.code(code)
				.description(code + " description")
				.build();
	}

}
