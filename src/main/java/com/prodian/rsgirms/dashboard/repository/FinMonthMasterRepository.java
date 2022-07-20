package com.prodian.rsgirms.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.FinMonthMaster;

@Repository
public interface FinMonthMasterRepository extends JpaRepository<FinMonthMaster, Integer>{

}
