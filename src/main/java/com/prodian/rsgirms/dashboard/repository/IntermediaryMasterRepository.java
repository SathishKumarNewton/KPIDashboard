package com.prodian.rsgirms.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.IntermediaryMaster;

@Repository
public interface IntermediaryMasterRepository extends JpaRepository<IntermediaryMaster, String> {

	List<IntermediaryMaster> findByIntermediaryCode(String code);

}
