package com.prodian.rsgirms.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.FuelTypeMasterNow;

@Repository
public interface FuelTypeMasterNowRepository  extends JpaRepository<FuelTypeMasterNow, String>{

	
	@Query("select UPPER(fuleType) FROM FuelTypeMasterNow")
	List<String> getFuelTypes();
}
