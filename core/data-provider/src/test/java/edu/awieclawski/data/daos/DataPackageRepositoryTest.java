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
@ActiveProfiles("testdaos")
@Sql( scripts = "classpath:/test-schema.sql")
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

	@Test
	void findByUrlLikeDayTable() {
		String date1 = "2022-05-23";
		String date2 = "2022-06-24";
		final String endPoint = "exchangerates/tables/b";
		final String url1 = "https://api.nbp.pl/api/" + endPoint + "/" + date1 + "?format=json";
		final String url2 = "https://api.nbp.pl/api/" + endPoint + "/" + date2 + "?format=json";
		entityManager.persist(getDataPackageStub(url1, endPoint));
		entityManager.persist(getDataPackageStub(url2, endPoint));
		List<DataPackage> packages = repository.findByUrlLikeDayTable(endPoint, date1);
		Assertions.assertEquals(1, packages.size());
		Assertions.assertEquals(url1, packages.get(0).getUrl());
		packages = repository.findByUrlLikeDayTable(endPoint, date2);
		Assertions.assertEquals(1, packages.size());
		Assertions.assertEquals(url2, packages.get(0).getUrl());
	}

	@Test
	void findByUrlLikeRangeTable() {
		String date1 = "2022-05-23";
		String date2 = "2022-06-24";
		final String endPoint = "exchangerates/tables/b";
		final String url1 = "https://api.nbp.pl/api/" + endPoint + "/" + date1 + "/" + date2
				+ "?format=json";
		final String url2 = "https://api.nbp.pl/api/" + endPoint + "?format=json";
		entityManager.persist(getDataPackageStub(url1, endPoint));
		entityManager.persist(getDataPackageStub(url2, endPoint));
		List<DataPackage> packages = repository.findByUrlLikeRangeTable(endPoint, date1, date2);
		Assertions.assertEquals(1, packages.size());
		Assertions.assertEquals(url1, packages.get(0).getUrl());
		Assertions.assertNotEquals(url2, packages.get(0).getUrl());
	}

	@Test
	void findByUrlLikeDaySingle() {
		String date1 = "2022-05-23";
		String date2 = "2022-06-24";
		String code = "eur";
		final String endPoint = "exchangerates/rates/a";
		final String url1 = "https://api.nbp.pl/api/" + endPoint + "/" + code + "/" + date1 + "?format=json";
		final String url2 = "https://api.nbp.pl/api/" + endPoint + "/" + code + "/" + date2 + "?format=json";
		entityManager.persist(getDataPackageStub(url1, endPoint));
		entityManager.persist(getDataPackageStub(url2, endPoint));
		List<DataPackage> packages = repository.findByUrlLikeDaySingle(endPoint, code, date1);
		Assertions.assertEquals(1, packages.size());
		Assertions.assertEquals(url1, packages.get(0).getUrl());
		packages = repository.findByUrlLikeDaySingle(endPoint, code, date2);
		Assertions.assertEquals(1, packages.size());
		Assertions.assertEquals(url2, packages.get(0).getUrl());
	}

	@Test
	void findByUrlLikeRangeSingle() {
		String date1 = "2022-05-23";
		String date2 = "2022-06-24";
		String code = "eur";
		final String endPoint = "exchangerates/rates/a";
		final String url1 = "https://api.nbp.pl/api/" + endPoint + "/" + code + "/" + date1 + "/" + date2
				+ "?format=json";
		final String url2 = "https://api.nbp.pl/api/" + endPoint + "/" + code + "?format=json";
		entityManager.persist(getDataPackageStub(url1, endPoint));
		entityManager.persist(getDataPackageStub(url2, endPoint));
		List<DataPackage> packages = repository.findByUrlLikeRangeSingle(endPoint, code, date1, date2);
		Assertions.assertEquals(1, packages.size());
		Assertions.assertEquals(url1, packages.get(0).getUrl());
		Assertions.assertNotEquals(url2, packages.get(0).getUrl());
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
