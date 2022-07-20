package com.prodian.rsgirms.usermatrix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.usermatrix.model.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

	List<Branch> getBranchesByStateNew(String state);

	Branch getBranchesByBranchCode(String branchCodes);

}
