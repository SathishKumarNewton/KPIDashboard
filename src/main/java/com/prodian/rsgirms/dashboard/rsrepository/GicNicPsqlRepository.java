package com.prodian.rsgirms.dashboard.rsrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.dashboard.modelfunction.GwpNwpPsqlFunction;
import com.prodian.rsgirms.dashboard.modelfunction.UserRole;

import java.io.Serializable;

@Repository("GicNicPsqlRepository")
public interface GicNicPsqlRepository extends JpaRepository<UserRole, Serializable> {

    // @Query("select * from calc_new_r12_gic_nic_now(' and (BUSINESS_TYPE) in (''Renewal'') and (CHANNEL) in (''OEM'') and (SUB_CHANNEL) in (''Honda Assure'') and (MAKE) in (''Honda Motors Ltd.'') and (MODELGROUP) in (''WRV'') and upper(coalesce(FUELTYPE,''N'')) in (''DIESEL'') and (STATEGROUPING) in (''Rest of Tamilnadu'')  and (ncb_flag) in (''N'')','201904')")
    // GicNicPsqlFunction callR12GicNic();
	
	
	@Query(value="select * from calc_new_r12_gwp_nwp_now(' and (BUSINESS_TYPE) in (''Renewal'') and (CHANNEL) in (''OEM'') and (SUB_CHANNEL) in (''Honda Assure'') and (MAKE) in (''Honda Motors Ltd.'') and (MODELGROUP) in (''WRV'') and upper(coalesce(FUELTYPE,''N'')) in (''DIESEL'') and (STATEGROUPING) in (''Rest of Tamilnadu'')  and (ncb_flag) in (''N'')','201904')")
	GwpNwpPsqlFunction callR12GwpNwp();
	
	
	
	
}