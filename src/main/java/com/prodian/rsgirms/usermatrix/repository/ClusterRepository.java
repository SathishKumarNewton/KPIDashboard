package com.prodian.rsgirms.usermatrix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.usermatrix.model.Cluster;

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, String> {

	List<Cluster> getClusterByZoneName(String zoneNames);

}
