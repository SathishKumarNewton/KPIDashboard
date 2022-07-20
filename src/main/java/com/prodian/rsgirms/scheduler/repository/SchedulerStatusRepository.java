
package com.prodian.rsgirms.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.scheduler.model.CubeStatus;

/**
 * @author CSS
 *
 */

@Repository
public interface SchedulerStatusRepository extends JpaRepository<CubeStatus, Integer> {

	CubeStatus findByProcessDateAndCubeName(String processDate, String cubeName);

	List<CubeStatus> findByProcessDateAndIsBuildStarted(String createSpecifiedDateFormat, String active);

	@Query("select s from CubeStatus s order by processDate desc")
	List<CubeStatus> getCubeJobStatus();

	@Query("select s from CubeStatus s where  processDate=?1 order by processDate desc")
	List<CubeStatus> getCubeJobStatusByBuildDate(String buildDate);


}
