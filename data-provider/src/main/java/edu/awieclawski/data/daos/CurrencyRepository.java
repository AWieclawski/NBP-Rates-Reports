package edu.awieclawski.data.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.awieclawski.models.entities.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

	boolean existsCurrencyByCode(String code);

	List<Currency> findByCode(String code);

}
