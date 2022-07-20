package com.prodian.rsgirms.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prodian.rsgirms.scheduler.model.SqoopJobResponse;

public interface SqoopJobResponseRepository extends JpaRepository<SqoopJobResponse, Integer> {

}
