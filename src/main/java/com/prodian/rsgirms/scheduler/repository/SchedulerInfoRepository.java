
package com.prodian.rsgirms.scheduler.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.scheduler.model.SchedulerInfo;

/**
 * @author CSS
 *
 */

@Repository
public interface SchedulerInfoRepository extends JpaRepository<SchedulerInfo, Integer> {

	@Query("select s from SchedulerInfo s where s.schedulerStartDate > ?1 AND s.schedulerEndDate <?2 AND s.processName = ?3 ")
	List<SchedulerInfo> getSchdeulerTransactionDetails(Timestamp timestampStartDate, Timestamp timestampEndDate, String processName);


}
