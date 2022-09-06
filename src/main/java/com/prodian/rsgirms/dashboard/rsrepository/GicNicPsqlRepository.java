package com.prodian.rsgirms.dashboard.rsrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.modelfunction.AcqPsqlFunction;
import com.prodian.rsgirms.dashboard.modelfunction.GwpNwpPsqlFunction;
import com.prodian.rsgirms.dashboard.modelfunction.GepNepPsqlFunctions;
import com.prodian.rsgirms.dashboard.modelfunction.UserRole;
import com.prodian.rsgirms.dashboard.modelfunction.GicNicPsqlFunction;

import java.io.Serializable;
import java.util.List;

@Repository("GicNicPsqlRepository")
public interface GicNicPsqlRepository extends JpaRepository<UserRole, Serializable> {
	
	@Query(value="select * from calc_new_r12_acq_loss_include(?1, ?2)", nativeQuery = true) 
	AcqPsqlFunction calc_new_r12_acq_loss_include(String sql, String date);

	@Query(value="select * from calc_new_r12_gic_nic_include(?1, ?2)", nativeQuery = true)
	GicNicPsqlFunction calc_new_r12_gic_nic_include(String sql, String date);

	@Query(value="select * from calc_new_r12_gwp_nwp_include(?1, ?2)", nativeQuery = true)
	GwpNwpPsqlFunction calc_new_r12_gwp_nwp_include(String sql, String date);
	
	@Query(value="select * from calc_new_r12_gep_nep_include(?1, ?2)", nativeQuery = true)
	GepNepPsqlFunctions calc_new_r12_gep_nep_include(String sql, String date);

	@Query(value="select * from calc_new_r12_acq_loss_exclude(?1, ?2)", nativeQuery = true) 
	AcqPsqlFunction calc_new_r12_acq_loss_exclude(String sql, String date);

	@Query(value="select * from calc_new_r12_gic_nic_exclude(?1, ?2)", nativeQuery = true)
	GicNicPsqlFunction calc_new_r12_gic_nic_exclude(String sql, String date);

	@Query(value="select * from calc_new_r12_gwp_nwp_exclude(?1, ?2)", nativeQuery = true)
	GwpNwpPsqlFunction calc_new_r12_gwp_nwp_exclude(String sql, String date);
	
	@Query(value="select * from calc_new_r12_gep_nep_exclude(?1, ?2)", nativeQuery = true)
	GepNepPsqlFunctions calc_new_r12_gep_nep_exclude(String sql, String date);

	@Query(value="select * from calc_new_r12_acq_loss_only_addon(?1, ?2)", nativeQuery = true) 
	AcqPsqlFunction calc_new_r12_acq_loss_only_addon(String sql, String date);

	@Query(value="select * from calc_new_r12_gic_nic_only_addon(?1, ?2)", nativeQuery = true)
	GicNicPsqlFunction calc_new_r12_gic_nic_only_addon(String sql, String date);

	@Query(value="select * from calc_new_r12_gwp_nwp_only_addon(?1, ?2)", nativeQuery = true)
	GwpNwpPsqlFunction calc_new_r12_gwp_nwp_only_addon(String sql, String date);
	
	@Query(value="select * from calc_new_r12_gep_nep_only_addon(?1, ?2)", nativeQuery = true)
	GepNepPsqlFunctions calc_new_r12_gep_nep_only_addon(String sql, String date);
	
	
}
