package com.prodian.rsgirms.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.EngineCapacityMaster;

@Repository
public interface EngineCapacityMasterRepository  extends JpaRepository<EngineCapacityMaster, String>{
    
}
