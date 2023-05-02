package edu.awieclawski.data.daos.base;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import edu.awieclawski.models.entities.ExchangeRate;

/**
 * Since Spring Data JPA release 1.4 the usage of restricted SpEL template
 * expressions in manually defined queries (in via @Query) is supported
 * 
 * https://docs.spring.io/spring-data/data-jpa/docs/1.7.x/reference/html/#jpa.query.spel-expressions
 * 
 * @author awieclawski
 *
 * @param <T>
 */
@NoRepositoryBean
public interface BaseRatesRepository<T extends ExchangeRate> extends JpaRepository<T, Long> {

	String FIND_BY_DATES_RANGE_AND_SYMBOL_LIST = "SELECT er FROM #{#entityName} er LEFT JOIN er.currency cr "
			+ " WHERE cr.code IN :codes AND er.published BETWEEN :publishedStart AND :publishedEnd";

	String FIND_VALID_FROM_DATA = "SELECT MIN(er) FROM #{#entityName} er WHERE er.published > :validFrom";

	String FIND_BY_DATES_RANGE = "SELECT er FROM #{#entityName} er "
			+ " WHERE er.published BETWEEN :publishedStart AND :publishedEnd";

	String FIND_BY_CODE_AND_DATES_RANGE = "SELECT er FROM #{#entityName} er "
			+ " LEFT JOIN er.currency cr "
			+ " WHERE cr.code = :code AND er.published BETWEEN :publishedStart AND :publishedEnd ";

	String FIND_IF_EXISTS_BY_CODE_BEFORE_DATE = "SELECT CASE WHEN count(er)>0 THEN TRUE ELSE FALSE END FROM #{#entityName} er "
			+ " LEFT JOIN er.currency cr "
			+ " WHERE (cr.code = :code AND er.published <= :published) ";

	String FIND_IF_EXISTS_BEFORE_DATE = "SELECT CASE WHEN count(er)>0 THEN TRUE ELSE FALSE END FROM #{#entityName} er "
			+ " WHERE er.published <= :published ";

	List<T> findAll();

	Optional<T> findById(Long id);

	@Query(value = FIND_BY_DATES_RANGE_AND_SYMBOL_LIST)
	List<T> findByDatesRangeAndSymbolList(
			@Param("publishedStart") LocalDate publishedStart,
			@Param("publishedEnd") LocalDate publishedEnd,
			@Param("codes") List<String> codes);

	@Query(value = FIND_BY_DATES_RANGE)
	List<T> findByDatesRange(
			@Param("publishedStart") LocalDate publishedStart,
			@Param("publishedEnd") LocalDate publishedEnd);

	@Query(value = FIND_BY_CODE_AND_DATES_RANGE)
	List<T> findByCodeAndDatesRange(
			@Param("code") String code,
			@Param("publishedStart") LocalDate publishedStart,
			@Param("publishedEnd") LocalDate publishedEnd);

	@Query(value = FIND_VALID_FROM_DATA)
	T findValidFromDate(
			@Param("validFrom") LocalDate validFrom);

	@Query(value = FIND_IF_EXISTS_BEFORE_DATE)
	boolean findIfExistsBeforeDate(
			@Param("published") LocalDate published);

	@Query(value = FIND_IF_EXISTS_BY_CODE_BEFORE_DATE)
	boolean findIfExistsByCodeBeforeDate(
			@Param("code") String code,
			@Param("published") LocalDate published);

}
