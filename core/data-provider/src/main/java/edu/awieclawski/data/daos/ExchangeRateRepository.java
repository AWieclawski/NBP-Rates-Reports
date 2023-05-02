package edu.awieclawski.data.daos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import edu.awieclawski.models.entities.ExchangeRate;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, Long> {

	@Query(value = "SELECT er FROM ExchangeRate er LEFT JOIN er.currency cr "
	+ " WHERE cr.code = :code AND er.published = :published AND er.nbpTable = :table ")
	List<ExchangeRate> findByCodePublishedTable(
	@Param("code") String code,
	@Param("published") LocalDate published,
	@Param("table") String table);

	@Query(value = "SELECT CASE WHEN count(er)>0 THEN TRUE ELSE FALSE END FROM ExchangeRate er LEFT JOIN er.currency cr "
	+ " WHERE lower(cr.code) LIKE lower(:code) AND lower(er.nbpTable) LIKE lower(:nbpTable) ")
	boolean existsByCurrencyCodeAndNbpTable(
	@Param("code") String code,
	@Param("nbpTable") String nbpTable);

	@Override
	default Iterable<ExchangeRate> findAll() {
		throw new RuntimeException("Not allowed! Mothod may return diferent entities");
	}

}