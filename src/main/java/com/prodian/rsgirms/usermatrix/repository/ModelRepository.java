package com.prodian.rsgirms.usermatrix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.usermatrix.model.Model;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {

	List<Model> findByMake(String make);

}
