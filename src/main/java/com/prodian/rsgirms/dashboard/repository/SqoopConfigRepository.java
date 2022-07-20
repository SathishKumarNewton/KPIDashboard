package com.prodian.rsgirms.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.SqoopConfiguation;

@Repository
public interface SqoopConfigRepository extends JpaRepository<SqoopConfiguation, Integer> {

	List<SqoopConfiguation> findBySourceSchemaUserName(String userName);

}
