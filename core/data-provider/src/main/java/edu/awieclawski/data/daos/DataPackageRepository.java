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
	
	@Query("SELECT d FROM DataPackage d WHERE d.url LIKE %:endPoint%  "
			+ " AND d.url LIKE %:code% "
			+ " AND d.url LIKE %:date% ")
	List<DataPackage> findByUrlLikeDaySingle(
			@Param("endPoint") String endPoint,
			@Param("code") String code,
			@Param("date") String date);
	
	@Query("SELECT d FROM DataPackage d WHERE d.url LIKE %:endPoint%  "
			+ " AND d.url LIKE %:date% "
			+ " AND d.url LIKE %:code% "
			+ " AND d.url LIKE %:dateEnd% ")
	List<DataPackage> findByUrlLikeRangeSingle(
			@Param("endPoint") String endPoint,
			@Param("code") String code,
			@Param("date") String date,
			@Param("dateEnd") String dateEnd);

	@Query("SELECT d FROM DataPackage d WHERE d.url LIKE %:endPoint%  "
			+ " AND d.url LIKE %:date% ")
	List<DataPackage> findByUrlLikeDayTable(
			@Param("endPoint") String endPoint,
			@Param("date") String date);

	@Query("SELECT d FROM DataPackage d WHERE d.url LIKE %:endPoint%  "
			+ " AND d.url LIKE %:date% "
			+ " AND d.url LIKE %:dateEnd% ")
	List<DataPackage> findByUrlLikeRangeTable(
			@Param("endPoint") String endPoint,
			@Param("date") String date,
			@Param("dateEnd") String dateEnd);

	@Modifying
	@Query("UPDATE DataPackage d SET d.converted = TRUE WHERE d.id = :id")
	void updateConvertedFlag(@Param("id") Long id);

	@Modifying
	@Query("UPDATE DataPackage d SET d.processed = TRUE WHERE d.id = :id")
	void updateProcessedFlag(@Param("id") Long id);
}
