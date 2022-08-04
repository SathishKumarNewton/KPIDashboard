package com.prodian.rsgirms.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.gicnicmodel;

import java.io.Serializable;

@Repository("gicnicRepository")
public interface gicnicRepository extends JpaRepository<gicnicmodel, Serializable> {
}
