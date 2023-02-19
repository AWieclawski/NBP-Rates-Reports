package edu.awieclawski.data.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.awieclawski.models.entities.DataPackage;

public interface DataPackageRepository extends JpaRepository<DataPackage, Long> {

	List<DataPackage> findByUrl(String url);

	List<DataPackage> findByConvertedFalse();

	@Modifying
	@Query("UPDATE DataPackage d SET d.converted = TRUE WHERE d.id = :id")
	void updateConvertedFlag(@Param("id") Long id);

	@Modifying
	@Query("UPDATE DataPackage d SET d.processed = TRUE WHERE d.id = :id")
	void updateProcessedFlag(@Param("id") Long id);
}
