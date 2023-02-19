package edu.awieclawski.data.daos.base;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import edu.awieclawski.models.entities.ExchangeRate;

@NoRepositoryBean
public interface BaseRatesRepository<T extends ExchangeRate> extends JpaRepository<T, Long> {

	List<T> findAll();

	Optional<T> findById(Long id);

	List<T> findByDatesRangeAndSymbolList(LocalDate publishedStart, LocalDate publishedEnd, List<String> codes);

	List<T> findByDatesRange(LocalDate publishedStart, LocalDate publishedEnd);

}
