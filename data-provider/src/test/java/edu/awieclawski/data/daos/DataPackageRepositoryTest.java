package edu.awieclawski.data.daos;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import edu.awieclawski.models.entities.DataPackage;

/**
 * https://programmingtechie.com/2020/10/21/spring-boot-testing-tutorial-database-testing-with-test-containers/
 * 
 * @author awieclawski
 *
 */
@DataJpaTest
@ActiveProfiles("test")
@Sql({"/test-schema.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DataPackageRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private DataPackageRepository repository;

	@Test
	void testFindByUrl() {
		final String url = "https://api.nbp.pl/api/exchangerates/rates/a/eur/2022-05-23/2022-05-27?format=json";
		final String endPoint = "exchangerates/rates/a";
		entityManager.persist(getDataPackageStub(url, endPoint));
		List<DataPackage> packages = repository.findByUrl(url);
		Assertions.assertEquals(1, packages.size());
		Assertions.assertEquals(url, packages.get(0).getUrl());
	}

	private static DataPackage getDataPackageStub(String url, String endPoint) {
		new DataPackage();
		DataPackage data = DataPackage.builder()
				.jsonData(
						"{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"098/A/NBP/2022\",\"effectiveDate\":\"2022-05-23\",\"mid\":4.6171},{\"no\":\"099/A/NBP/2022\",\"effectiveDate\":\"2022-05-24\",\"mid\":4.6107},{\"no\":\"100/A/NBP/2022\",\"effectiveDate\":\"2022-05-25\",\"mid\":4.5955},{\"no\":\"101/A/NBP/2022\",\"effectiveDate\":\"2022-05-26\",\"mid\":4.6135},{\"no\":\"102/A/NBP/2022\",\"effectiveDate\":\"2022-05-27\",\"mid\":4.6102}]}")
				.url(url)
				.endPoint(endPoint)
				.build();

		return data;
	}

}
