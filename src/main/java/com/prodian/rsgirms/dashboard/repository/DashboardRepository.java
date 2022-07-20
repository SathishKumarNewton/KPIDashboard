package com.prodian.rsgirms.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.Dashboard;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Integer> {

	Dashboard findDashboardById(Integer id);

}
