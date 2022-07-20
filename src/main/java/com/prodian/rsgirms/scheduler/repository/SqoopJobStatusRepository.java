package com.prodian.rsgirms.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prodian.rsgirms.scheduler.model.SqoopJobResponse;
import com.prodian.rsgirms.scheduler.model.SqoopJobStatusResponse;

public interface SqoopJobStatusRepository extends JpaRepository<SqoopJobStatusResponse, Integer> {

	List<SqoopJobStatusResponse> findBySqoopDate(String createSpecifiedDateFormat);

}
