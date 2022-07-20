//
//package com.prodian.rsgirms.scheduler.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import com.prodian.rsgirms.scheduler.model.BroucherAgentMapping;
//
///**
// * @author CSS
// *
// */
//
//@Repository
//public interface BroucherAgentMappingRepository extends JpaRepository<BroucherAgentMapping, Integer> {
//
//	@Query("select s from BroucherAgentMapping s where isDynamicHtmlCreated='N'")
//	List<BroucherAgentMapping> getDynamicHtmlCreatedList();
//
//	@Query("select s from BroucherAgentMapping s where isDynamicHtmlCreated='Y' and isHtmlToPdfConverted='N'")
//	List<BroucherAgentMapping> getDynamicHtmlCreatedAndIsHtmlToPdfConvertedList();
//
//
//}
