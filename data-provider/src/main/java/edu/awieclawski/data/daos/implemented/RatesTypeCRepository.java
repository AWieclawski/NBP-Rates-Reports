package edu.awieclawski.data.daos.implemented;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.awieclawski.data.daos.base.BaseRatesRepository;
import edu.awieclawski.models.entities.ExchangeRateTypeC;

public interface RatesTypeCRepository extends BaseRatesRepository<ExchangeRateTypeC> {

	@Override
	List<ExchangeRateTypeC> findAll();

	@Override
	Optional<ExchangeRateTypeC> findById(Long id);

	@Override
	@Query(value = "SELECT er FROM ExchangeRateTypeC er LEFT JOIN er.currency cr "
	+ " WHERE cr.code IN :codes AND er.published BETWEEN :publishedStart AND :publishedEnd ")
	List<ExchangeRateTypeC> findByDatesRangeAndSymbolList(
	@Param("publishedStart") LocalDate publishedStart,
	@Param("publishedEnd") LocalDate publishedEnd,
	@Param("codes") List<String> codes);

	@Override
	@Query(value = "SELECT er FROM ExchangeRateTypeC er "
	+ " WHERE er.published BETWEEN :publishedStart AND :publishedEnd ")
	List<ExchangeRateTypeC> findByDatesRange(
	@Param("publishedStart") LocalDate publishedStart,
	@Param("publishedEnd") LocalDate publishedEnd);

	@Query(value = "SELECT CASE WHEN count(er)>0 THEN TRUE ELSE FALSE END FROM ExchangeRateTypeC er "
	+ " LEFT JOIN er.currency cr "
	+ " WHERE (cr.code = :code AND er.published = :published) ")
	boolean findIfExistsByCodeDate(
	@Param("code") String code,
	@Param("published") LocalDate published);

	@Query(value = "SELECT MIN(er) FROM ExchangeRateTypeC er WHERE er.published > :validFrom")
	ExchangeRateTypeC findValidFromDate(@Param("validFrom") LocalDate validFrom);
}
