package com.prodian.rsgirms.scheduler.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class DenormalizeSchedulerService {

	/*@Autowired
	 public DataSource dataSource;*/

	Logger logger = LoggerFactory.getLogger(DenormalizeSchedulerService.class);

	//@Scheduled(cron = "0 0/1 * ? * *")
	public boolean policyDenormalize() throws SQLException{
		System.out.println("policyDenormalize called---------->");
		Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "rsbiadmin", "Prodian123");
		
		String sql = " select * from rsdb.rsa_dwh_product_master ";
	    System.out.println("Running: " + sql);
	    
		
		Statement stmt = con.createStatement();
	    ResultSet res = stmt.executeQuery(sql);
		
	    //int counter = 0;
	    while (res.next()) {
	    	System.out.println("product-->"+res.getString(1));
	    }
		
		return false;
	}
	
	
	
	//@Scheduled(cron = "0 0/1 * ? * *")
	public boolean dailyPolicyDenormalizeJob() throws SQLException{
		System.out.println("policyDenormalize called---------->");
		Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "rsbiadmin", "Prodian123");
		
		String sql = " insert into table RSA_KPI_FACT_POLICY_DENORMALIZE_TEST select p.policy_no,p.endorsement_no,p.client_code,p.agent_code,p.inception_date,p.expirty_date,p.efffective_date,p.endorsement_code,p.product_code,p.branch_code,p.domicle,p.business_type_code,p.installment_mode,p.oa_code,p.licensed_or_non_licensed,p.last_modified_date,p.created_date,cast(p.policy_si as decimal(21,6)),p.payment_mode,p.plan_,p.source_,p.nominee_code,p.migration_flag,p.portability_flag,p.floter_flag,p.previous_policy_no,p.campain_code,p.policy_status,p.is_vip,p.channel,p.sub_channel,p.master_policy_no,p.financial_year,p.entry_year,p.business_band,p.policy_days,p.eff_fin_year_month,p.family_size,p.hr_code,p.tenure,p.receipt_no,p.invoice_date,p.invoice_no,p.rs_gstinnum,p.hsc_code,p.financial_non_financial,p.product_group,p.external_or_internal,p.stp_nstp,cast(p.previous_si as decimal(21,6)),p.endt_reas_code,p.uw_year,p.policy_source_type,p.entry_date,P.gwp_date,cast(p.client_premium as decimal(21,6)),p.livescovered,p.maxage,p.subline,p.business_type,p.product_type,p.posted_flag,(case when size(medical_dCode)>1 then 'Y' else 'N' end) disease_code,totalnumberofyearswithrs,numberofyearswithrsinmigrationpolicy,(CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.modelcode WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.modelcode WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.make ELSE '' END) MODELCODE ,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.make WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.make WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.make ELSE '' END) MAKE , "+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.model WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.model WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.model ELSE '' END) model,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.modelgroup WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.modelgroup WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.modelgroup ELSE '' END) modelgroup,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.classofvehicle WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN '' WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.classofvehicle ELSE '' END) classofvehicle,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.vehicleage WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.vehicleage WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.vehicleage ELSE 0 END) vehicleage,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.seatingcapacity WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.seatingcapacity WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.seatingcapacity ELSE 0 END) seatingcapacity,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.fueltype WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.fueltype WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.fueltype ELSE '' END) fueltype,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.registrationnumber WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.registrationno WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.registrationnumber ELSE '' END) registrationnumber,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.vir_number WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.vir_number WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.vir_number ELSE '' END) vir_number,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.registrationstate WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.registrationregion WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.registrationstate ELSE '' END) regState,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.registrationzone WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.registrationzone WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.registrationzone ELSE '' END) regZone,"+
						" (CASE WHEN p.product_code in ('VOC','VFA','VGC','VAR','VRR','VRT') THEN ccc.registeringlocation WHEN p.product_code in ('VMC','VMB','VMBL','VML') THEN mmm.registeringlocationorcity WHEN p.product_code in ('VPC','VPL','VPB','VPBL') THEN ppc.registeringlocation ELSE '' END) regLocation,concat(cast(FINANCIAL_YEAR as int),'-',lpad(EFF_FIN_YEAR_MONTH,2,0),'-','01') fin_date,(CASE WHEN ENDORSEMENT_CODE IN ('00','11','12') THEN 1 WHEN ENDORSEMENT_CODE IN ('02','08') THEN -1 ELSE 0 END) policy_count,COALESCE((CASE WHEN MIGRATION_FLAG='Yes' then 'Migration' else 'Normal' end),'NONE') POLICY_CATEGORY,b.POLICY_NO,b.ENDT_NO,b.IRDA_COMM,b.ORC_COMM,b.FOS_COMM,b.OTHER_COMM,b.ACQ_COST,b.FLAG,b.CREATED_DATE,b.LAST_MODIFIED_DATE"+ 
						" from (select * from rsa_dwh_policy_latest WHERE (substr(created_date,1,10)='2020-08-17')) p"+
						" left join rsa_dwh_acq_cost_2020_2021 b on p.policy_no=b.policy_no and p.endorsement_no=b.endt_no"+
						" Left join (select POLICY_NO medical_policyno,ENDT_NO medical_endtno,COLLECT_LIST((case when DISEASE_CODE in ('Null','NA','NIL','Nil','Nill','NULL','NONE') then null else DISEASE_CODE end)) as medical_dCode from rsa_dwh_medical m group by POLICY_NO,ENDT_NO) mm on"+ 
						" P.POLICY_NO=mm.medical_policyno AND P.ENDORSEMENT_NO=mm.medical_endtno"+
						" LEFT JOIN (select * from (select I.*,ROW_NUMBER() over(partition by POLICY_NO,ENDT_NO ORDER BY 1) as rownumber from rsa_dwh_insured I)ii where rownumber=1) iii"+
						" on P.POLICY_NO=iii.POLICY_NO and P.ENDORSEMENT_NO = iii.ENDT_NO"+
						" LEFT JOIN (select * from (select c.*,ROW_NUMBER() over(partition by POLICYCODE ORDER BY 1) as rownumber from rsa_dwh_commercialvehicle_with_vehicleno c)cc where rownumber=1 )ccc ON p.policy_no=ccc.policycode"+
						" LEFT JOIN (select * from (select m.*,ROW_NUMBER() over(partition by POLICYCODE ORDER BY 1) as rownumber from rsa_dwh_motorcycle m)mc where rownumber=1 )mmm ON p.policy_no=mmm.policycode  "+
						" LEFT JOIN (select * from (select pc.*,ROW_NUMBER() over(partition by POLICYCODE ORDER BY 1) as rownumber from rsa_dwh_privatepassengercar_with_vehicleno pc)pp where rownumber=1 )ppc ON p.policy_no=ppc.policycode" ; 
	    System.out.println("Running: " + sql);
	    
		
		Statement stmt = con.createStatement();
	    ResultSet res = stmt.executeQuery(sql);
		
	    /*commented to check server start issue in live*/
	   // DumpHiveMessages(stmt);
	    
	    if(stmt!=null){
	    	stmt.close();
	    }
	    
	    //int counter = 0;
	    while (res.next()) {
	    	System.out.println("product-->"+res.getString(1));
	    }
		
		return false;
	}
	
	
	
	 /*private static void DumpHiveMessages (java.sql.Statement stmtGeneric)
	  { org.apache.hive.jdbc.HiveStatement stmtExtended ;
	    try
	    { stmtExtended =(org.apache.hive.jdbc.HiveStatement)stmtGeneric ;
	      for (String sLogMessage : stmtExtended.getQueryLog())
	      { System.out.println("HIVE SAYS>" +sLogMessage) ;    } 
	      if (stmtExtended.hasMoreLogs())
	      { System.out.println("WARNING>(...log stream still open...") ; }
	    }
	    catch (Exception duh)
	    { System.out.println("WARNING>Error while accessing Hive log stream");
	    }
	  }*/

}
