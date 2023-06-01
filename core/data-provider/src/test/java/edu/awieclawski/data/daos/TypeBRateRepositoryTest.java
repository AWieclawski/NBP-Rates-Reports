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

import edu.awieclawski.commons.dtos.enums.NbpType;
import edu.awieclawski.data.daos.implemented.RatesTypeBRepository;
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
@ActiveProfiles("testdaos")
@Sql(scripts = "classpath:/test-schema.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TypeBRateRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private RatesTypeBRepository ratesRepository;

	private final LocalDate DATE = LocalDate.now().minusDays(1);
	private final String CODE_AFN = "AFN";
	private final String CODE_MGA = "MGA";
	private Currency CURRENCY_AFN;
	private Currency CURRENCY_MGA;
	private Currency CURRENCY_USD;

	@BeforeEach
	void init() {
		CURRENCY_AFN = getCurrencyEntity(CODE_AFN);
		entityManager.persist(CURRENCY_AFN);
		CURRENCY_MGA = getCurrencyEntity(CODE_MGA);
		entityManager.persist(CURRENCY_MGA);
		CURRENCY_USD = getCurrencyEntity("USD");
		entityManager.persist(CURRENCY_USD);
	}

	@Test
	void findValidFromDateReturnPublishedNextBusinessDayExchangeRateTypeB() {
		// given
		LocalDate validFrom = DATE.minusDays(2); // must be after exchRateOne
		ExchangeRateTypeB exchRateOne = ExchangeRateTypeB.builder()
				.currency(CURRENCY_AFN)
				.nbpTable("011/B/NBP/2023")
				.rate(new BigDecimal("1.2345"))
				.published(DATE.minusDays(3))
				.build();
		// published at next business day after valid from date
		ExchangeRateTypeB exchRateTwo = ExchangeRateTypeB.builder()
				.currency(CURRENCY_AFN)
				.nbpTable("013/B/NBP/2023")
				.rate(new BigDecimal("1.3456"))
				.published(DATE.minusDays(1))
				.build();
		ExchangeRateTypeB exchRateThree = ExchangeRateTypeB.builder()
				.currency(CURRENCY_AFN)
				.nbpTable("014/B/NBP/2023")
				.rate(new BigDecimal("1.4567"))
				.published(DATE)
				.build();

		entityManager.persist(exchRateOne);
		entityManager.persist(exchRateTwo);
		entityManager.persist(exchRateThree);
		entityManager.persist(getExchangeRateTypeA(CURRENCY_USD, DATE.minusDays(9))); // disruption

		// when
		ExchangeRateTypeB foundB = ratesRepository.findValidFromDate(validFrom);

		// then
		Assertions.assertEquals(foundB, exchRateTwo);
		Assertions.assertEquals(NbpType.B, foundB.getNbpType());
		Assertions.assertTrue(ratesRepository.findIfExistsBeforeDate(validFrom));
		Assertions.assertTrue(ratesRepository.findIfExistsByCodeBeforeDate(CODE_AFN, validFrom));
		Assertions.assertFalse(ratesRepository.findIfExistsByCodeBeforeDate(CODE_MGA, validFrom));
		Assertions.assertFalse(ratesRepository.findIfExistsBeforeDate(DATE.minusDays(8)));
	}

	private ExchangeRateTypeA getExchangeRateTypeA(Currency currency, LocalDate date) {

		return ExchangeRateTypeA.builder()
				.currency(currency)
				.nbpTable("013/A/NBP/2023")
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
