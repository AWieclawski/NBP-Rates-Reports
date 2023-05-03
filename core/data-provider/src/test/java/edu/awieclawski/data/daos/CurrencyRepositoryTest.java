package edu.awieclawski.data.daos;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import edu.awieclawski.models.entities.Currency;

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
class CurrencyRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private CurrencyRepository repository;

	@Test
	void testFindByCode() {
		final String code = "ABC";
		final String description = "Description of the currency ABC";
		entityManager.persist(getCurrency(code, description));
		List<Currency> packages = repository.findByCode(code);
		Assertions.assertEquals(1, packages.size());
		Assertions.assertEquals(code, packages.get(0).getCode());
		Assertions.assertEquals(LocalDateTime.now().toLocalDate(), packages.get(0).getCreatedAt().toLocalDate());
	}

	private static Currency getCurrency(String code, String description) {
		new Currency();
		Currency entity = Currency.builder()
				.code(code)
				.description(description)
				.build();

		return entity;
	}

}
