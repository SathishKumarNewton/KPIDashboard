//
//package com.prodian.rsgirms.scheduler.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import com.prodian.rsgirms.scheduler.model.BroucherMaster;
//
///**
// * @author CSS
// *
// */
//
//@Repository
//public interface BroucherMasterRepository extends JpaRepository<BroucherMaster, Integer> {
//
//	@Query("select s from BroucherMaster s where isActive='Y'")
//	List<BroucherMaster> getActiveBrouchers();
//
//	BroucherMaster findByBroucherName(String broucherName);
//
//	BroucherMaster findByBorucherId(Integer valueOf);
//
//}
