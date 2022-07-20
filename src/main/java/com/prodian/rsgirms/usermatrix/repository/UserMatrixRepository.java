package com.prodian.rsgirms.usermatrix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.usermatrix.model.UserMatrix;

@Repository
public interface UserMatrixRepository extends JpaRepository<UserMatrix, Integer> {

	List<UserMatrix> getUserMatrixByUserIdAndUserMatrixRole(Integer userId, String userMatrixRole);

}
