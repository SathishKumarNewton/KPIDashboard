package com.prodian.rsgirms.usermatrix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.usermatrix.model.Channel;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String>{

}
