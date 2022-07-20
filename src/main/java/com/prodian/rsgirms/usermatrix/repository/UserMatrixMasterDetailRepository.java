package com.prodian.rsgirms.usermatrix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterDetail;

@Repository
public interface UserMatrixMasterDetailRepository extends JpaRepository<UserMatrixMasterDetail, Integer> {

	List<UserMatrixMasterDetail> findAllByUserId(Integer userId);

	List<UserMatrixMasterDetail> findByUserIdAndDashboardId(Integer userId, Integer dashboardId);

}
