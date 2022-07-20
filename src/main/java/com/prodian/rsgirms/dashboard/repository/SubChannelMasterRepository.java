package com.prodian.rsgirms.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.IntermediaryMaster;
import com.prodian.rsgirms.dashboard.model.SubChannelMaster;

@Repository
public interface SubChannelMasterRepository extends JpaRepository<SubChannelMaster, Integer> {

	@Query("select distinct channelName from SubChannelMaster")
	List<String> getUniqueChannels();

	@Query("select distinct subChannel from SubChannelMaster")
	List<String> getUniqueSubChannels();

	@Query( "select o from SubChannelMaster o where channelName in :ids" )
	List<SubChannelMaster> findByChannelNameIn(@Param("ids") List<String> arrayParam);

}
