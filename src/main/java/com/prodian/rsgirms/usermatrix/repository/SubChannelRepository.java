package com.prodian.rsgirms.usermatrix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.usermatrix.model.SubChannel;

@Repository
public interface SubChannelRepository extends JpaRepository<SubChannel, String> {

	List<SubChannel> findByChannelName(String channelName);

}
