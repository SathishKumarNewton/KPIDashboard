
package com.prodian.rsgirms.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.model.UserDashboard;

@Repository
public interface UserDashboardRepository extends JpaRepository<UserDashboard, Integer> {

	List<UserDashboard> findByUserId(Integer userId);

	List<UserDashboard> findByDashboardId(Integer dashboardId);

	UserDashboard findByUserDashboardId(Integer userDashboardId);

	UserDashboard findByUserIdAndDashboardName(int userId, String dashboardName);

}
