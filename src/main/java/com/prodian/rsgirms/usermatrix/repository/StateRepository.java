package com.prodian.rsgirms.usermatrix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.usermatrix.model.State;

@Repository
public interface StateRepository extends JpaRepository<State, String> {

	List<State> getStateByClusterName(String clusterName);

}
