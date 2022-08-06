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
	
	@Query(value="select * from calc_new_r12_acq_loss_now(' and (BUSINESS_TYPE) in (''Renewal'') and (CHANNEL) in (''OEM'') and (SUB_CHANNEL) in (''Honda Assure'') and (MAKE) in (''Honda Motors Ltd.'') and (MODELGROUP) in (''WRV'') and upper(coalesce(FUELTYPE,''N'')) in (''DIESEL'') and (STATEGROUPING) in (''Rest of Tamilnadu'')  and (ncb_flag) in (''N'')','201904,2015')") 
	AcqPsqlFunction callR12AcqFunction();

	@Query(value="select * from calc_new_r12_gic_nic_now(?1,?2)", nativeQuery = true)
	GicNicPsqlFunction callR12GicNic(String sql, String date);

	@Query(value="select * from calc_new_r12_gwp_nwp_now(' and (BUSINESS_TYPE) in (''Renewal'') and (CHANNEL) in (''OEM'') and (SUB_CHANNEL) in (''Honda Assure'') and (MAKE) in (''Honda Motors Ltd.'') and (MODELGROUP) in (''WRV'') and upper(coalesce(FUELTYPE,''N'')) in (''DIESEL'') and (STATEGROUPING) in (''Rest of Tamilnadu'')  and (ncb_flag) in (''N'')','201904')")
	GwpNwpPsqlFunction callR12GwpNwp();
	
	@Query(value="select * from calc_new_r12_gep_nep_now(' and (BUSINESS_TYPE) in (''Renewal'') and (CHANNEL) in (''OEM'') and (SUB_CHANNEL) in (''Honda Assure'') and (MAKE) in (''Honda Motors Ltd.'') and (MODELGROUP) in (''WRV'') and upper(coalesce(FUELTYPE,''N'')) in (''DIESEL'') and (STATEGROUPING) in (''Rest of Tamilnadu'')  and (ncb_flag) in (''N'')','201904')")
	GepNepPsqlFunctions callR12GepNep();
	
	
}
