package com.prodian.rsgirms.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.ModelGroupMasterNow;

@Repository
public interface ModelGroupMasterNowRepository  extends JpaRepository<ModelGroupMasterNow, String>{

	@Query("select UPPER(modelGroup) FROM ModelGroupMasterNow")
	List<String> getModelGroups();
}
