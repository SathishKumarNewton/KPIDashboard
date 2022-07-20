package com.prodian.rsgirms.usermatrix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.usermatrix.model.UserMatrixMaster;

@Repository
public interface UserMatrixMasterRepository extends JpaRepository<UserMatrixMaster, Integer>{

	List<UserMatrixMaster> getUserMatrixByUserIdAndUserMatrixRole(Integer userId, String role);

	UserMatrixMaster findByUserId(Integer userId);

	List<UserMatrixMaster> findByDashboardId(Integer dashboardId);

	List<UserMatrixMaster> getUserMatrixByUserIdAndUserMatrixRoleAndDashboardId(Integer userId, String role,
			Integer dashboardId);

	UserMatrixMaster findByUserIdAndDashboardId(Integer userId, Integer dashboardId);

}
