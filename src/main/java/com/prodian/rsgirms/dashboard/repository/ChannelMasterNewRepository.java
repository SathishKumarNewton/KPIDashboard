package com.prodian.rsgirms.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.ChannelMasterNew;

@Repository
public interface ChannelMasterNewRepository  extends JpaRepository<ChannelMasterNew, String>{
    
}
