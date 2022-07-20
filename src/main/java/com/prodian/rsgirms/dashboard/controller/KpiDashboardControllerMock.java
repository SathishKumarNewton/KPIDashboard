
package com.prodian.rsgirms.dashboard.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.kylin.jdbc.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.prodian.rsgirms.constants.RMSConstants;
import com.prodian.rsgirms.dashboard.response.CubeAKpiResponseMock;
import com.prodian.rsgirms.dashboard.response.CubeBKpiResponseMock;
import com.prodian.rsgirms.dashboard.response.CubeCKpiResponseMock;
import com.prodian.rsgirms.dashboard.response.CubeDKpiResponseMock;
import com.prodian.rsgirms.dashboard.response.GwpResponse;
import com.prodian.rsgirms.dashboard.service.KpiDashboardService;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;

@Controller
public class KpiDashboardControllerMock {


	@Autowired
	private UserService userService;
	
	@Autowired
	private KpiDashboardService kpiDashboardService;

	//private Connection connection = null;
	
	
//	@GetMapping("/kpiDashboard")
//	public String SchedulerInfoDetails(Model model) {
//		return "kpiDashboard";
//	}
	
	@GetMapping("/kpiDashboardMock")
	public String SchedulerInfoDetails(Model model) {
		return "kpiDashboardMock";
	}
	
//	@RequestMapping(value = "/getKpiDataList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public @ResponseBody
//	List<GeneralKpiResponse> getKpiDataList(HttpServletRequest req) throws SQLException {
//		List<GeneralKpiResponse> generalKpiResponseList=new ArrayList<GeneralKpiResponse>();
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User user = userService.findUserByUserName(auth.getName());
//		int userId = user.getId();
//		 Connection connection = null;
//		
//		String maps= "{}";
//		long startTime = System.currentTimeMillis();
//       // JSONArray jsArray = new JSONArray();
//		System.out.println("Started query execution");
//		try {
//			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
//			
//		      Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
//		      Properties info = new Properties();
//		      info.put("user", "ADMIN");
//		      info.put("password", "KYLIN");
//		        connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_BASE_IP_AND_PORT+"/learn_kylin", info);
//		        System.out.println("Connection status -------------------------->"+connection);
//		        Statement stmt = connection.createStatement();
//		        
//		        String queryStr =  "SELECT "  + 
//       				 "SUM(FACT_KPI_GENERAL.GWP) as FACT_KPI_GENERAL_GWP "  + 
//       				 ",SUM(FACT_KPI_GENERAL.GEP) as FACT_KPI_GENERAL_GEP "  + 
//       				 ",SUM(FACT_KPI_GENERAL.NEP) as FACT_KPI_GENERAL_NEP "  + 
//       				 ",SUM(FACT_KPI_GENERAL.AVERAGE_GWP) as FACT_KPI_GENERAL_AVERAGE_GWP "  + 
//       				 ",SUM(FACT_KPI_GENERAL.AVERAGE_NEP) as FACT_KPI_GENERAL_AVERAGE_NEP "  + 
//       				 ",SUM(FACT_KPI_GENERAL.AVERAGE_GEP) as FACT_KPI_GENERAL_AVERAGE_GEP "  + 
//       				 ",SUM(FACT_KPI_GENERAL.GEP_TP) as FACT_KPI_GENERAL_GEP_TP "  + 
//       				 ",SUM(FACT_KPI_GENERAL.NEP_TP) as FACT_KPI_GENERAL_NEP_TP "  + 
//       				 ",SUM(FACT_KPI_GENERAL.SEVERITY_PERC) as FACT_KPI_GENERAL_SEVERITY_PERC "  + 
//       				 ",SUM(FACT_KPI_GENERAL.REPUDIATED_CLAIM_PERC) as FACT_KPI_GENERAL_REPUDIATED_CLAIM_PERC "  + 
//       				 ",SUM(FACT_KPI_GENERAL.REGISTERED_CLAIM) as FACT_KPI_GENERAL_REGISTERED_CLAIM "  + 
//       				 ",SUM(FACT_KPI_GENERAL.REDPUDIATED_CLAIM) as FACT_KPI_GENERAL_REDPUDIATED_CLAIM "  + 
//       				 ",SUM(FACT_KPI_GENERAL.NIC) as FACT_KPI_GENERAL_NIC "  + 
//       				 ",SUM(FACT_KPI_GENERAL.NIC_NEP_RATIO) as FACT_KPI_GENERAL_NIC_NEP_RATIO "  + 
//       				 ",SUM(FACT_KPI_GENERAL.CLAIM_FREQUENCY) as FACT_KPI_GENERAL_CLAIM_FREQUENCY "  + 
//       				 ",SUM(FACT_KPI_GENERAL.WRITTEN_POLICIES) as FACT_KPI_GENERAL_WRITTEN_POLICIES "  + 
//       				 ",SUM(FACT_KPI_GENERAL.NET_ACQUISITION_COST) as FACT_KPI_GENERAL_NET_ACQUISITION_COST "  + 
//       				 ",SUM(FACT_KPI_GENERAL.EXPENSE_RATIO) as FACT_KPI_GENERAL_EXPENSE_RATIO "  + 
//       				 ",SUM(FACT_KPI_GENERAL.ACQUISITION_COST_RATIO) as FACT_KPI_GENERAL_ACQUISITION_COST_RATIO "  + 
//       				 ",SUM(FACT_KPI_GENERAL.CLAIMS_RATION) as FACT_KPI_GENERAL_CLAIMS_RATION "  + 
//       				 ",SUM(FACT_KPI_GENERAL.COR) as FACT_KPI_GENERAL_COR "  + 
//       				 ",SUM(FACT_KPI_GENERAL.XOL_COST) as FACT_KPI_GENERAL_XOL_COST "  + 
//       				 ",SUM(FACT_KPI_GENERAL.EXPENSE) as FACT_KPI_GENERAL_EXPENSE "  + 
//       				 ",SUM(FACT_KPI_GENERAL.EARNED_POLICIES) as FACT_KPI_GENERAL_EARNED_POLICIES "  + 
//       				 ",SUM(FACT_KPI_GENERAL.NWP) as FACT_KPI_GENERAL_NWP "  + 
//       				 " FROM RSDB.FACT_KPI_GENERAL as FACT_KPI_GENERAL "  + 
//       				 "INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW "  + 
//       				 "ON FACT_KPI_GENERAL.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME "  + 
//       				 "INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW "  + 
//       				 "ON FACT_KPI_GENERAL.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND FACT_KPI_GENERAL.SUBCHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL "  + 
//       				 "INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW "  + 
//       				 "ON FACT_KPI_GENERAL.REGION = DS_MASTER_ZONE_NOW.ZONE_NAME "  + 
//       				 "INNER JOIN RSDB.DS_MASTER_CLUSTER_NOW as DS_MASTER_CLUSTER_NOW "  + 
//       				 "ON FACT_KPI_GENERAL.CLUSTER_NAME = DS_MASTER_CLUSTER_NOW.CLUSTER_NAME AND FACT_KPI_GENERAL.REGION = DS_MASTER_CLUSTER_NOW.ZONE_NAME "  + 
//       				 "INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW "  + 
//       				 "ON FACT_KPI_GENERAL.CLUSTER_NAME = DS_MASTER_STATE_NOW.CLUSTER_NAME AND FACT_KPI_GENERAL.STATE = DS_MASTER_STATE_NOW.STATE "  + 
//       				 "INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW "  + 
//       				 "ON FACT_KPI_GENERAL.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE "  + 
//       				 "INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW "  + 
//       				 "ON FACT_KPI_GENERAL.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE "  + 
//       				 /*"WHERE entry_date BETWEEN  '"+fromDate+"' and  '"+toDate+"' ";*/
//       				"WHERE entry_month  BETWEEN '"+fromDate+"' and  '"+toDate+"' ";
//       				
//								 
//		        //String queryStr = "select  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,  SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,  SUM(PREV_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PREV_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PREV_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PREV_YTD_POLICY_COUNT) LASTYR_YTD_POLICY  from (  SELECT  NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME as CLUSTER_NAME  ,NEW_POLICY_FACT.STATE as STATE  ,NEW_MASTER_STATE.STATE_CODE AS STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT as PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC  ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB  ,NEW_POLICY_FACT.BUSINESS_TYPE as BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL as CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL  ,NEW_POLICY_FACT.MAKE as MAKE  ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME as MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_YTD_POLICY_COUNT  FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT  INNER JOIN RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE  ON NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_ZONE.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_CLUSTER as NEW_MASTER_CLUSTER  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_CLUSTER.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_STATE as NEW_MASTER_STATE  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_STATE.STATE  INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH  ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE  INNER JOIN RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT  ON NEW_POLICY_FACT.PRODUCT_CODE = NEW_MASTER_PRODUCT.PRODUCT_CODE  INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as NEW_MASTER_BUSINESS_TYPE  ON NEW_POLICY_FACT.BUSINESS_TYPE = NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE  INNER JOIN RSDB.NEW_MASTER_CHANNEL as NEW_MASTER_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_CHANNEL.CHANNEL_NAME  INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as NEW_MASTER_SUB_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL = NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL  INNER JOIN RSDB.NEW_MASTER_MAKE as NEW_MASTER_MAKE  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME  INNER JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE = NEW_MASTER_MODEL.MODEL_CODE  INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as NEW_MASTER_FIN_YEAR  ON NEW_POLICY_FACT.FIN_YEAR = NEW_MASTER_FIN_YEAR.FIN_YEAR  INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as NEW_MASTER_FIN_MONTH  ON NEW_POLICY_FACT.MONTH_FLAG = NEW_MASTER_FIN_MONTH.ENTRY_MONTH  WHERE 1=1  and NEW_MASTER_PRODUCT.SEGMENT_NEW in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by  NEW_POLICY_FACT.ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME  ,NEW_POLICY_FACT.STATE  ,NEW_MASTER_STATE.STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION  ,NEW_MASTER_PRODUCT.SEGMENT_NEW  ,NEW_POLICY_FACT.BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL  ,NEW_POLICY_FACT.MAKE  ,NEW_POLICY_FACT.MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG  ) CUR_ACT_CUBE  GROUP BY  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE ";
//		        System.out.println("queryStr------------------------------ "+queryStr);
//		        ResultSet rs = stmt.executeQuery(queryStr);
//		        System.out.println("START------------------------------ ");
//		        ResultSetMetaData rsmd = rs.getMetaData();
//		        System.out.println("START------------------------------ ");
//		        
//		        //jsArray = convertToJSON(rs);
//		        
//		        
//		        
//		        while(rs.next()) {
//		        	GeneralKpiResponse generalKpiResponseObj = new GeneralKpiResponse();
//		        	generalKpiResponseObj.setGwp(rs.getDouble(1));
//		        	generalKpiResponseObj.setNwp(rs.getDouble(24));
//		        	generalKpiResponseObj.setGep(rs.getDouble(2));
//		        	generalKpiResponseObj.setNep(rs.getDouble(3));
//		        	generalKpiResponseObj.setAvgGwp(rs.getDouble(4));
//		        	generalKpiResponseObj.setAvgNep(rs.getDouble(5));
//		        	generalKpiResponseObj.setAvgGep(rs.getDouble(6));
//		        	generalKpiResponseObj.setGepTp(rs.getDouble(7));
//		        	generalKpiResponseObj.setNepTp(rs.getDouble(8));
//		        	generalKpiResponseObj.setSeverityPerc(rs.getDouble(9));
//		        	generalKpiResponseObj.setRepudiatedClaimPerc(rs.getDouble(10));
//		        	generalKpiResponseObj.setRegisteredClaim(rs.getDouble(11));
//		        	generalKpiResponseObj.setNic(rs.getDouble(12));
//		        	generalKpiResponseObj.setNicNepRatio(rs.getDouble(13));
//		        	generalKpiResponseObj.setClaimFrequency(rs.getDouble(14));
//		        	generalKpiResponseObj.setWp(rs.getDouble(15));
//		        	generalKpiResponseObj.setNetAcquisitionCost(rs.getDouble(16));
//		        	generalKpiResponseObj.setExpenseRatio(rs.getDouble(17));
//		        	generalKpiResponseObj.setAcquisitionCostRatio(rs.getDouble(18));
//		        	generalKpiResponseObj.setClaimsRatio(rs.getDouble(19));
//		        	generalKpiResponseObj.setCor(rs.getDouble(20));
//		        	generalKpiResponseObj.setXolCost(rs.getDouble(21));
//		        	generalKpiResponseObj.setExpense(rs.getDouble(22));
//		        	generalKpiResponseObj.setEarnedPolicies(rs.getDouble(23));
//		        	
//		        	generalKpiResponseList.add(generalKpiResponseObj);
//		        	
//		        }
//		        
//		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
//		        //System.out.println(jsArray.toString());
//		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//		    } catch (Exception e) {
//		    	System.out.println("kylinDataSource initialize error, ex: " +  e);
//		    	System.out.println();
//		    	System.out.println();
//		    	System.out.println();
//		    	e.printStackTrace();
//		    }finally {
//				connection.close();
//			}
//	        
//				System.out.println("CALLED THE METHOD");
////		return jsArray.toJSONString();
//				return generalKpiResponseList;
//	}
		
	@RequestMapping(value = "/getKpiFiltersMock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody GwpResponse getFiltersForDropdown() {
//		GwpResponse gwpResponse = service.getFiltersForDropdown();
		GwpResponse gwpResponse = new GwpResponse();
		try {
			gwpResponse = kpiDashboardService.getFiltersForDropdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gwpResponse;
	}
	
	
	
//	@RequestMapping(value = "/getKpiCubeAData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public @ResponseBody
//	List<CubeAKpiResponse> getKpicubeAData(HttpServletRequest req) throws SQLException {
//		List<CubeAKpiResponse> generalKpiResponseList=new ArrayList<CubeAKpiResponse>();
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User user = userService.findUserByUserName(auth.getName());
//		int userId = user.getId();
//		 Connection connection = null;
//		
//		String maps= "{}";
//		long startTime = System.currentTimeMillis();
//       // JSONArray jsArray = new JSONArray();
//		System.out.println("Started query execution");
//		try {
//			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
//			
//		      Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
//		      Properties info = new Properties();
//		      info.put("user", "ADMIN");
//		      info.put("password", "KYLIN");
//		        connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
//		        System.out.println("Connection status -------------------------->"+connection);
//		        Statement stmt = connection.createStatement();
//		        
//		        String fromMonth = fromDate.split("/")[0];
//		        String fromYear = fromDate.split("/")[1];
//		        String toMonth = toDate.split("/")[0];
//		        String toYear = toDate.split("/")[1];
//		        
//		        String queryStr =  "SELECT " +
//		        		"SUM(FACT_KPI_A.GWP) as FACT_KPI_A_GWP, " +
//		        		"SUM(FACT_KPI_A.NWP) as FACT_KPI_A_NWP, " +
//		        		"SUM(FACT_KPI_A.LIVESCOVERED) as FACT_KPI_A_LIVESCOVERED " +
//		        		"FROM RSDB.FACT_KPI_A as FACT_KPI_A " +
//		        		"INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW " +
//		        		"ON FACT_KPI_A.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME " +
//		        		"INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW " +
//		        		"ON FACT_KPI_A.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND FACT_KPI_A.SUBCHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL " +
//		        		"INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW " +
//		        		"ON FACT_KPI_A.REGION = DS_MASTER_ZONE_NOW.ZONE_NAME " +
//		        		"INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW " +
//		        		"ON FACT_KPI_A.STATE = DS_MASTER_STATE_NOW.STATE " +
//		        		"INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW " +
//		        		"ON FACT_KPI_A.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE " +
//		        		"INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW " +
//		        		"ON FACT_KPI_A.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE " +
//		        		" WHERE (KPI_MONTH BETWEEN "+fromMonth+" AND "+toMonth+") AND KPI_YEAR BETWEEN "+fromYear+" AND "+toYear+"";
//       				
//								 
//		        //String queryStr = "select  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,  SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,  SUM(PREV_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PREV_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PREV_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PREV_YTD_POLICY_COUNT) LASTYR_YTD_POLICY  from (  SELECT  NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME as CLUSTER_NAME  ,NEW_POLICY_FACT.STATE as STATE  ,NEW_MASTER_STATE.STATE_CODE AS STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT as PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC  ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB  ,NEW_POLICY_FACT.BUSINESS_TYPE as BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL as CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL  ,NEW_POLICY_FACT.MAKE as MAKE  ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME as MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_YTD_POLICY_COUNT  FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT  INNER JOIN RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE  ON NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_ZONE.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_CLUSTER as NEW_MASTER_CLUSTER  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_CLUSTER.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_STATE as NEW_MASTER_STATE  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_STATE.STATE  INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH  ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE  INNER JOIN RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT  ON NEW_POLICY_FACT.PRODUCT_CODE = NEW_MASTER_PRODUCT.PRODUCT_CODE  INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as NEW_MASTER_BUSINESS_TYPE  ON NEW_POLICY_FACT.BUSINESS_TYPE = NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE  INNER JOIN RSDB.NEW_MASTER_CHANNEL as NEW_MASTER_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_CHANNEL.CHANNEL_NAME  INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as NEW_MASTER_SUB_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL = NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL  INNER JOIN RSDB.NEW_MASTER_MAKE as NEW_MASTER_MAKE  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME  INNER JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE = NEW_MASTER_MODEL.MODEL_CODE  INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as NEW_MASTER_FIN_YEAR  ON NEW_POLICY_FACT.FIN_YEAR = NEW_MASTER_FIN_YEAR.FIN_YEAR  INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as NEW_MASTER_FIN_MONTH  ON NEW_POLICY_FACT.MONTH_FLAG = NEW_MASTER_FIN_MONTH.ENTRY_MONTH  WHERE 1=1  and NEW_MASTER_PRODUCT.SEGMENT_NEW in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by  NEW_POLICY_FACT.ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME  ,NEW_POLICY_FACT.STATE  ,NEW_MASTER_STATE.STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION  ,NEW_MASTER_PRODUCT.SEGMENT_NEW  ,NEW_POLICY_FACT.BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL  ,NEW_POLICY_FACT.MAKE  ,NEW_POLICY_FACT.MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG  ) CUR_ACT_CUBE  GROUP BY  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE ";
//		        System.out.println("queryStr------------------------------ "+queryStr);
//		        ResultSet rs = stmt.executeQuery(queryStr);
//		        System.out.println("START------------------------------ ");
//		        
//		        //jsArray = convertToJSON(rs);
//		        
//		        
//		        
//		        while(rs.next()) {
//		        	CubeAKpiResponse cubeAKpiResponse = new CubeAKpiResponse();
//		        	cubeAKpiResponse.setGwp(rs.getDouble(1));
//		        	cubeAKpiResponse.setNwp(rs.getDouble(2));
//		        	cubeAKpiResponse.setLivesCovered(rs.getDouble(3));
//		        	
//		        	generalKpiResponseList.add(cubeAKpiResponse);
//		        	
//		        }
//		        
//		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
//		        //System.out.println(jsArray.toString());
//		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//		    } catch (Exception e) {
//		    	System.out.println("kylinDataSource initialize error, ex: " +  e);
//		    	System.out.println();
//		    	System.out.println();
//		    	System.out.println();
//		    	e.printStackTrace();
//		    }finally {
//				connection.close();
//			}
//	        
//				System.out.println("CALLED THE METHOD");
////		return jsArray.toJSONString();
//				return generalKpiResponseList;
//	}
	
	@RequestMapping(value = "/getKpiCubeADataMock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<CubeAKpiResponseMock> getKpicubeAData(HttpServletRequest req) throws SQLException {
		List<CubeAKpiResponseMock> generalKpiResponseList=new ArrayList<CubeAKpiResponseMock>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		 Connection connection = null;
		
		String maps= "{}";
		long startTime = System.currentTimeMillis();
       // JSONArray jsArray = new JSONArray();
		System.out.println("Started query execution");
		try {
			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
			
		      Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		      Properties info = new Properties();
		      info.put("user", "ADMIN");
		      info.put("password", "KYLIN");
		        connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
		        System.out.println("Connection status -------------------------->"+connection);
		        Statement stmt = connection.createStatement();
		        
		        String fromMonth = fromDate.split("/")[0];
		        String fromYear = fromDate.split("/")[1];
		        String toMonth = toDate.split("/")[0];
		        String toYear = toDate.split("/")[1];
		        
		        String queryStr =  "SELECT " +
		        		"SUM(FACT_KPI_A.GWP) as FACT_KPI_A_GWP, " +
		        		"SUM(FACT_KPI_A.NWP) as FACT_KPI_A_NWP, " +
		        		"SUM(FACT_KPI_A.LIVESCOVERED) as FACT_KPI_A_LIVESCOVERED " +
		        		"FROM RSDB.FACT_KPI_A as FACT_KPI_A " +
		        		"INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW " +
		        		"ON FACT_KPI_A.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME " +
		        		"INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW " +
		        		"ON FACT_KPI_A.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND FACT_KPI_A.SUBCHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL " +
		        		"INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW " +
		        		"ON FACT_KPI_A.REGION = DS_MASTER_ZONE_NOW.ZONE_NAME " +
		        		"INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW " +
		        		"ON FACT_KPI_A.STATE = DS_MASTER_STATE_NOW.STATE " +
		        		"INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW " +
		        		"ON FACT_KPI_A.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE " +
		        		"INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW " +
		        		"ON FACT_KPI_A.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE " +
		        		" WHERE (KPI_MONTH BETWEEN "+fromMonth+" AND "+toMonth+") AND KPI_YEAR BETWEEN "+fromYear+" AND "+toYear+"";
       				
								 
		        //String queryStr = "select  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,  SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,  SUM(PREV_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PREV_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PREV_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PREV_YTD_POLICY_COUNT) LASTYR_YTD_POLICY  from (  SELECT  NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME as CLUSTER_NAME  ,NEW_POLICY_FACT.STATE as STATE  ,NEW_MASTER_STATE.STATE_CODE AS STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT as PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC  ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB  ,NEW_POLICY_FACT.BUSINESS_TYPE as BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL as CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL  ,NEW_POLICY_FACT.MAKE as MAKE  ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME as MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_YTD_POLICY_COUNT  FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT  INNER JOIN RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE  ON NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_ZONE.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_CLUSTER as NEW_MASTER_CLUSTER  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_CLUSTER.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_STATE as NEW_MASTER_STATE  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_STATE.STATE  INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH  ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE  INNER JOIN RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT  ON NEW_POLICY_FACT.PRODUCT_CODE = NEW_MASTER_PRODUCT.PRODUCT_CODE  INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as NEW_MASTER_BUSINESS_TYPE  ON NEW_POLICY_FACT.BUSINESS_TYPE = NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE  INNER JOIN RSDB.NEW_MASTER_CHANNEL as NEW_MASTER_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_CHANNEL.CHANNEL_NAME  INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as NEW_MASTER_SUB_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL = NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL  INNER JOIN RSDB.NEW_MASTER_MAKE as NEW_MASTER_MAKE  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME  INNER JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE = NEW_MASTER_MODEL.MODEL_CODE  INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as NEW_MASTER_FIN_YEAR  ON NEW_POLICY_FACT.FIN_YEAR = NEW_MASTER_FIN_YEAR.FIN_YEAR  INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as NEW_MASTER_FIN_MONTH  ON NEW_POLICY_FACT.MONTH_FLAG = NEW_MASTER_FIN_MONTH.ENTRY_MONTH  WHERE 1=1  and NEW_MASTER_PRODUCT.SEGMENT_NEW in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by  NEW_POLICY_FACT.ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME  ,NEW_POLICY_FACT.STATE  ,NEW_MASTER_STATE.STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION  ,NEW_MASTER_PRODUCT.SEGMENT_NEW  ,NEW_POLICY_FACT.BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL  ,NEW_POLICY_FACT.MAKE  ,NEW_POLICY_FACT.MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG  ) CUR_ACT_CUBE  GROUP BY  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE ";
		        System.out.println("queryStr------------------------------ "+queryStr);
		        ResultSet rs = stmt.executeQuery(queryStr);
		        System.out.println("START------------------------------ ");
		        
		        //jsArray = convertToJSON(rs);
		        
		        
		        
		        while(rs.next()) {
		        	CubeAKpiResponseMock cubeAKpiResponse = new CubeAKpiResponseMock();
		        	cubeAKpiResponse.setGwp(rs.getDouble(1));
		        	cubeAKpiResponse.setNwp(rs.getDouble(2));
		        	cubeAKpiResponse.setLivesCovered(rs.getDouble(3));
		        	
		        	generalKpiResponseList.add(cubeAKpiResponse);
		        	
		        }
		        
		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
		        //System.out.println(jsArray.toString());
		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		    } catch (Exception e) {
		    	System.out.println("kylinDataSource initialize error, ex: " +  e);
		    	System.out.println();
		    	System.out.println();
		    	System.out.println();
		    	e.printStackTrace();
		    }finally {
				connection.close();
			}
	        
				System.out.println("CALLED THE METHOD");
//		return jsArray.toJSONString();
				return generalKpiResponseList;
	}
	
	
//	@RequestMapping(value = "/getKpiCubeBData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public @ResponseBody
//	List<CubeBKpiResponse> getKpicubeBData(HttpServletRequest req) throws SQLException {
//		List<CubeBKpiResponse> generalKpiResponseList=new ArrayList<CubeBKpiResponse>();
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User user = userService.findUserByUserName(auth.getName());
//		int userId = user.getId();
//		 Connection connection = null;
//		
//		String maps= "{}";
//		long startTime = System.currentTimeMillis();
//       // JSONArray jsArray = new JSONArray();
//		System.out.println("Started query execution");
//		try {
//			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
//			
//		      Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
//		      Properties info = new Properties();
//		      info.put("user", "ADMIN");
//		      info.put("password", "KYLIN");
//		        connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
//		        System.out.println("Connection status -------------------------->"+connection);
//		        Statement stmt = connection.createStatement();
//		        
//		        String fromMonth = fromDate.split("/")[0];
//		        String fromYear = fromDate.split("/")[1];
//		        String toMonth = toDate.split("/")[0];
//		        String toYear = toDate.split("/")[1];
//		        
//		        String queryStr =  "SELECT " +
//		        		"SUM(FACT_KPI_B.GWP_OD) as FACT_KPI_B_GWP_OD ,"+
//		        		"SUM(FACT_KPI_B.GWP_TP) as FACT_KPI_B_GWP_TP ,"+
//		        		"SUM(FACT_KPI_B.NWP_OD) as FACT_KPI_B_NWP_OD ,"+
//		        		"SUM(FACT_KPI_B.NWP_TP) as FACT_KPI_B_NWP_TP ,"+
//		        		"SUM(FACT_KPI_B.DISCOUNT_GWP_OD) as FACT_KPI_B_DISCOUNT_GWP_OD ,"+
//		        		"SUM(FACT_KPI_B.DISCOUNT_NWP_OD) as FACT_KPI_B_DISCOUNT_NWP_OD "+
//		        		"FROM RSDB.FACT_KPI_B as FACT_KPI_B " +
//		        		"INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW " +
//		        		"ON FACT_KPI_B.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME " +
//		        		"INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW " +
//		        		"ON FACT_KPI_B.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND FACT_KPI_B.SUBCHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL " +
//		        		"INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW " +
//		        		"ON FACT_KPI_B.REGION = DS_MASTER_ZONE_NOW.ZONE_NAME " +
//		        		"INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW " +
//		        		"ON FACT_KPI_B.STATE = DS_MASTER_STATE_NOW.STATE " +
//		        		"INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW " +
//		        		"ON FACT_KPI_B.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE " +
//		        		"INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW " +
//		        		"ON FACT_KPI_B.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE " +
//		        		"WHERE (KPI_MONTH BETWEEN "+fromMonth+" AND "+toMonth+") AND KPI_YEAR BETWEEN "+fromYear+" AND "+toYear+"";
//       				
//								 
//		        //String queryStr = "select  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,  SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,  SUM(PREV_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PREV_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PREV_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PREV_YTD_POLICY_COUNT) LASTYR_YTD_POLICY  from (  SELECT  NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME as CLUSTER_NAME  ,NEW_POLICY_FACT.STATE as STATE  ,NEW_MASTER_STATE.STATE_CODE AS STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT as PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC  ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB  ,NEW_POLICY_FACT.BUSINESS_TYPE as BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL as CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL  ,NEW_POLICY_FACT.MAKE as MAKE  ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME as MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_YTD_POLICY_COUNT  FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT  INNER JOIN RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE  ON NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_ZONE.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_CLUSTER as NEW_MASTER_CLUSTER  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_CLUSTER.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_STATE as NEW_MASTER_STATE  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_STATE.STATE  INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH  ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE  INNER JOIN RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT  ON NEW_POLICY_FACT.PRODUCT_CODE = NEW_MASTER_PRODUCT.PRODUCT_CODE  INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as NEW_MASTER_BUSINESS_TYPE  ON NEW_POLICY_FACT.BUSINESS_TYPE = NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE  INNER JOIN RSDB.NEW_MASTER_CHANNEL as NEW_MASTER_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_CHANNEL.CHANNEL_NAME  INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as NEW_MASTER_SUB_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL = NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL  INNER JOIN RSDB.NEW_MASTER_MAKE as NEW_MASTER_MAKE  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME  INNER JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE = NEW_MASTER_MODEL.MODEL_CODE  INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as NEW_MASTER_FIN_YEAR  ON NEW_POLICY_FACT.FIN_YEAR = NEW_MASTER_FIN_YEAR.FIN_YEAR  INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as NEW_MASTER_FIN_MONTH  ON NEW_POLICY_FACT.MONTH_FLAG = NEW_MASTER_FIN_MONTH.ENTRY_MONTH  WHERE 1=1  and NEW_MASTER_PRODUCT.SEGMENT_NEW in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by  NEW_POLICY_FACT.ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME  ,NEW_POLICY_FACT.STATE  ,NEW_MASTER_STATE.STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION  ,NEW_MASTER_PRODUCT.SEGMENT_NEW  ,NEW_POLICY_FACT.BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL  ,NEW_POLICY_FACT.MAKE  ,NEW_POLICY_FACT.MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG  ) CUR_ACT_CUBE  GROUP BY  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE ";
//		        System.out.println("queryStr------------------------------ "+queryStr);
//		        ResultSet rs = stmt.executeQuery(queryStr);
//		        System.out.println("START------------------------------ ");
//		        
//		        //jsArray = convertToJSON(rs);
//		        
//		        
//		        
//		        while(rs.next()) {
//		        	CubeBKpiResponse cubeAKpiResponse = new CubeBKpiResponse();
//		        	cubeAKpiResponse.setGwpOd(rs.getDouble(1));
//		        	cubeAKpiResponse.setGwpTp(rs.getDouble(2));
//		        	cubeAKpiResponse.setNwpOd(rs.getDouble(3));
//		        	cubeAKpiResponse.setNwpTp(rs.getDouble(4));
//		        	cubeAKpiResponse.setDiscountGwpOd(rs.getDouble(5));
//		        	cubeAKpiResponse.setDiscountNwpOd(rs.getDouble(6));
//		        	generalKpiResponseList.add(cubeAKpiResponse);
//		        	
//		        }
//		        
//		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
//		        //System.out.println(jsArray.toString());
//		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//		    } catch (Exception e) {
//		    	System.out.println("kylinDataSource initialize error, ex: " +  e);
//		    	System.out.println();
//		    	System.out.println();
//		    	System.out.println();
//		    	e.printStackTrace();
//		    }finally {
//				connection.close();
//			}
//	        
//				System.out.println("CALLED THE METHOD");
////		return jsArray.toJSONString();
//				return generalKpiResponseList;
//	}
	
	@RequestMapping(value = "/getKpiCubeBDataMock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<CubeBKpiResponseMock> getKpicubeBData(HttpServletRequest req) throws SQLException {
		List<CubeBKpiResponseMock> generalKpiResponseList=new ArrayList<CubeBKpiResponseMock>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		 Connection connection = null;
		
		String maps= "{}";
		long startTime = System.currentTimeMillis();
       // JSONArray jsArray = new JSONArray();
		System.out.println("Started query execution");
		try {
			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
			
		      Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		      Properties info = new Properties();
		      info.put("user", "ADMIN");
		      info.put("password", "KYLIN");
		        connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
		        System.out.println("Connection status -------------------------->"+connection);
		        Statement stmt = connection.createStatement();
		        
		        String fromMonth = fromDate.split("/")[0];
		        String fromYear = fromDate.split("/")[1];
		        String toMonth = toDate.split("/")[0];
		        String toYear = toDate.split("/")[1];
		        
		        String queryStr =  "SELECT " +
		        		"SUM(FACT_KPI_B.GWP_OD) as FACT_KPI_B_GWP_OD ,"+
		        		"SUM(FACT_KPI_B.GWP_TP) as FACT_KPI_B_GWP_TP ,"+
		        		"SUM(FACT_KPI_B.NWP_OD) as FACT_KPI_B_NWP_OD ,"+
		        		"SUM(FACT_KPI_B.NWP_TP) as FACT_KPI_B_NWP_TP ,"+
		        		"SUM(FACT_KPI_B.DISCOUNT_GWP_OD) as FACT_KPI_B_DISCOUNT_GWP_OD ,"+
		        		"SUM(FACT_KPI_B.DISCOUNT_NWP_OD) as FACT_KPI_B_DISCOUNT_NWP_OD "+
		        		"FROM RSDB.FACT_KPI_B as FACT_KPI_B " +
		        		"INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW " +
		        		"ON FACT_KPI_B.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME " +
		        		"INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW " +
		        		"ON FACT_KPI_B.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND FACT_KPI_B.SUBCHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL " +
		        		"INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW " +
		        		"ON FACT_KPI_B.REGION = DS_MASTER_ZONE_NOW.ZONE_NAME " +
		        		"INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW " +
		        		"ON FACT_KPI_B.STATE = DS_MASTER_STATE_NOW.STATE " +
		        		"INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW " +
		        		"ON FACT_KPI_B.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE " +
		        		"INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW " +
		        		"ON FACT_KPI_B.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE " +
		        		"WHERE (KPI_MONTH BETWEEN "+fromMonth+" AND "+toMonth+") AND KPI_YEAR BETWEEN "+fromYear+" AND "+toYear+"";
       				
								 
		        //String queryStr = "select  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,  SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,  SUM(PREV_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PREV_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PREV_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PREV_YTD_POLICY_COUNT) LASTYR_YTD_POLICY  from (  SELECT  NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME as CLUSTER_NAME  ,NEW_POLICY_FACT.STATE as STATE  ,NEW_MASTER_STATE.STATE_CODE AS STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT as PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC  ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB  ,NEW_POLICY_FACT.BUSINESS_TYPE as BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL as CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL  ,NEW_POLICY_FACT.MAKE as MAKE  ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME as MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_YTD_POLICY_COUNT  FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT  INNER JOIN RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE  ON NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_ZONE.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_CLUSTER as NEW_MASTER_CLUSTER  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_CLUSTER.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_STATE as NEW_MASTER_STATE  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_STATE.STATE  INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH  ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE  INNER JOIN RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT  ON NEW_POLICY_FACT.PRODUCT_CODE = NEW_MASTER_PRODUCT.PRODUCT_CODE  INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as NEW_MASTER_BUSINESS_TYPE  ON NEW_POLICY_FACT.BUSINESS_TYPE = NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE  INNER JOIN RSDB.NEW_MASTER_CHANNEL as NEW_MASTER_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_CHANNEL.CHANNEL_NAME  INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as NEW_MASTER_SUB_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL = NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL  INNER JOIN RSDB.NEW_MASTER_MAKE as NEW_MASTER_MAKE  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME  INNER JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE = NEW_MASTER_MODEL.MODEL_CODE  INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as NEW_MASTER_FIN_YEAR  ON NEW_POLICY_FACT.FIN_YEAR = NEW_MASTER_FIN_YEAR.FIN_YEAR  INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as NEW_MASTER_FIN_MONTH  ON NEW_POLICY_FACT.MONTH_FLAG = NEW_MASTER_FIN_MONTH.ENTRY_MONTH  WHERE 1=1  and NEW_MASTER_PRODUCT.SEGMENT_NEW in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by  NEW_POLICY_FACT.ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME  ,NEW_POLICY_FACT.STATE  ,NEW_MASTER_STATE.STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION  ,NEW_MASTER_PRODUCT.SEGMENT_NEW  ,NEW_POLICY_FACT.BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL  ,NEW_POLICY_FACT.MAKE  ,NEW_POLICY_FACT.MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG  ) CUR_ACT_CUBE  GROUP BY  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE ";
		        System.out.println("queryStr------------------------------ "+queryStr);
		        ResultSet rs = stmt.executeQuery(queryStr);
		        System.out.println("START------------------------------ ");
		        
		        //jsArray = convertToJSON(rs);
		        
		        
		        
		        while(rs.next()) {
		        	CubeBKpiResponseMock cubeAKpiResponse = new CubeBKpiResponseMock();
		        	cubeAKpiResponse.setGwpOd(rs.getDouble(1));
		        	cubeAKpiResponse.setGwpTp(rs.getDouble(2));
		        	cubeAKpiResponse.setNwpOd(rs.getDouble(3));
		        	cubeAKpiResponse.setNwpTp(rs.getDouble(4));
		        	cubeAKpiResponse.setDiscountGwpOd(rs.getDouble(5));
		        	cubeAKpiResponse.setDiscountNwpOd(rs.getDouble(6));
		        	generalKpiResponseList.add(cubeAKpiResponse);
		        	
		        }
		        
		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
		        //System.out.println(jsArray.toString());
		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		    } catch (Exception e) {
		    	System.out.println("kylinDataSource initialize error, ex: " +  e);
		    	System.out.println();
		    	System.out.println();
		    	System.out.println();
		    	e.printStackTrace();
		    }finally {
				connection.close();
			}
	        
				System.out.println("CALLED THE METHOD");
//		return jsArray.toJSONString();
				return generalKpiResponseList;
	}
	
	
//	@RequestMapping(value = "/getKpiCubeCData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public @ResponseBody
//	List<CubeCKpiResponse> getKpiCubeCData(HttpServletRequest req) throws SQLException {
//		List<CubeCKpiResponse> generalKpiResponseList=new ArrayList<CubeCKpiResponse>();
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User user = userService.findUserByUserName(auth.getName());
//		int userId = user.getId();
//		 Connection connection = null;
//		
//		String maps= "{}";
//		long startTime = System.currentTimeMillis();
//       // JSONArray jsArray = new JSONArray();
//		System.out.println("Started query execution");
//		try {
//			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
//			
//		      Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
//		      Properties info = new Properties();
//		      info.put("user", "ADMIN");
//		      info.put("password", "KYLIN");
//		        connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
//		        System.out.println("Connection status -------------------------->"+connection);
//		        Statement stmt = connection.createStatement();
//		        
//		        String fromMonth = fromDate.split("/")[0];
//		        String fromYear = fromDate.split("/")[1];
//		        String toMonth = toDate.split("/")[0];
//		        String toYear = toDate.split("/")[1];
//		        
//		        
//		        
//		        String queryStr =  "SELECT " +
//		        		" SUM(FACT_KPI_C.GEP) as FACT_KPI_C_GEP, "+
//		        		" SUM(FACT_KPI_C.NEP) as FACT_KPI_C_NEP, "+
//				        " SUM(FACT_KPI_C.GEP_OD) as FACT_KPI_C_GEP_OD, "+
//				        " SUM(FACT_KPI_C.NEP_OD) as FACT_KPI_C_NEP_OD, "+
//				        " SUM(FACT_KPI_C.GEP_TP) as FACT_KPI_C_GEP_TP, "+
//				        " SUM(FACT_KPI_C.NEP_TP) as FACT_KPI_C_NEP_TP, "+
//				        " SUM(FACT_KPI_C.DISCOUNT_GEP_OD) as FACT_KPI_C_DISCOUNT_GEP_OD, "+
//				        " SUM(FACT_KPI_C.DISCOUNT_NEP_OD) as FACT_KPI_C_DISCOUNT_NEP_OD, "+
//				        " SUM(FACT_KPI_C.IBNR_GIC_OD) as FACT_KPI_C_IBNR_GIC_OD, "+
//				        " SUM(FACT_KPI_C.NBNR_GIC_OD) as FACT_KPI_C_NBNR_GIC_OD, "+
//				        " SUM(FACT_KPI_C.IBNR_NIC_OD) as FACT_KPI_C_IBNR_NIC_OD, "+
//				        " SUM(FACT_KPI_C.NBNR_NIC_OD) as FACT_KPI_C_NBNR_NIC_OD, "+
//				        " SUM(FACT_KPI_C.IBNR_GIC_HEALTH) as FACT_KPI_C_IBNR_GIC_HEALTH, "+
//				        " SUM(FACT_KPI_C.NBNR_GIC_HEALTH) as FACT_KPI_C_NBNR_GIC_HEALTH "+
//		        		"FROM RSDB.FACT_KPI_C as FACT_KPI_C " +
//		        		"INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW " +
//		        		"ON FACT_KPI_C.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME " +
//		        		"INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW " +
//		        		"ON FACT_KPI_C.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND FACT_KPI_C.SUBCHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL " +
//		        		"INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW " +
//		        		"ON FACT_KPI_C.REGION = DS_MASTER_ZONE_NOW.ZONE_NAME " +
//		        		"INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW " +
//		        		"ON FACT_KPI_C.STATE = DS_MASTER_STATE_NOW.STATE " +
//		        		"INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW " +
//		        		"ON FACT_KPI_C.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE " +
//		        		"INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW " +
//		        		"ON FACT_KPI_C.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE " +
//		        		"WHERE (KPI_MONTH BETWEEN "+fromMonth+" AND "+toMonth+") AND KPI_YEAR BETWEEN "+fromYear+" AND "+toYear+"";
//       				
//								 
//		        //String queryStr = "select  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,  SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,  SUM(PREV_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PREV_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PREV_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PREV_YTD_POLICY_COUNT) LASTYR_YTD_POLICY  from (  SELECT  NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME as CLUSTER_NAME  ,NEW_POLICY_FACT.STATE as STATE  ,NEW_MASTER_STATE.STATE_CODE AS STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT as PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC  ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB  ,NEW_POLICY_FACT.BUSINESS_TYPE as BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL as CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL  ,NEW_POLICY_FACT.MAKE as MAKE  ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME as MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_YTD_POLICY_COUNT  FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT  INNER JOIN RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE  ON NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_ZONE.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_CLUSTER as NEW_MASTER_CLUSTER  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_CLUSTER.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_STATE as NEW_MASTER_STATE  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_STATE.STATE  INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH  ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE  INNER JOIN RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT  ON NEW_POLICY_FACT.PRODUCT_CODE = NEW_MASTER_PRODUCT.PRODUCT_CODE  INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as NEW_MASTER_BUSINESS_TYPE  ON NEW_POLICY_FACT.BUSINESS_TYPE = NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE  INNER JOIN RSDB.NEW_MASTER_CHANNEL as NEW_MASTER_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_CHANNEL.CHANNEL_NAME  INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as NEW_MASTER_SUB_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL = NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL  INNER JOIN RSDB.NEW_MASTER_MAKE as NEW_MASTER_MAKE  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME  INNER JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE = NEW_MASTER_MODEL.MODEL_CODE  INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as NEW_MASTER_FIN_YEAR  ON NEW_POLICY_FACT.FIN_YEAR = NEW_MASTER_FIN_YEAR.FIN_YEAR  INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as NEW_MASTER_FIN_MONTH  ON NEW_POLICY_FACT.MONTH_FLAG = NEW_MASTER_FIN_MONTH.ENTRY_MONTH  WHERE 1=1  and NEW_MASTER_PRODUCT.SEGMENT_NEW in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by  NEW_POLICY_FACT.ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME  ,NEW_POLICY_FACT.STATE  ,NEW_MASTER_STATE.STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION  ,NEW_MASTER_PRODUCT.SEGMENT_NEW  ,NEW_POLICY_FACT.BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL  ,NEW_POLICY_FACT.MAKE  ,NEW_POLICY_FACT.MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG  ) CUR_ACT_CUBE  GROUP BY  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE ";
//		        System.out.println("queryStr------------------------------ "+queryStr);
//		        ResultSet rs = stmt.executeQuery(queryStr);
//		        System.out.println("START------------------------------ ");
//		        
//		        //jsArray = convertToJSON(rs);
//		        
//		        
//		        
//		        while(rs.next()) {
//		        	
//		        	CubeCKpiResponse cubeCKpiResponse = new CubeCKpiResponse();
//		        	cubeCKpiResponse.setGep(rs.getDouble(1));
//		        	cubeCKpiResponse.setNep(rs.getDouble(2));
//		        	cubeCKpiResponse.setGepOd(rs.getDouble(3));
//		        	cubeCKpiResponse.setNepOd(rs.getDouble(4));
//		        	cubeCKpiResponse.setGepTp(rs.getDouble(5));
//		        	cubeCKpiResponse.setNepTp(rs.getDouble(6));
//		        	cubeCKpiResponse.setDiscountGepOd(rs.getDouble(7));
//		        	cubeCKpiResponse.setDiscountNepOd(rs.getDouble(8));
//		        	cubeCKpiResponse.setIbnrGicOd(rs.getDouble(9));
//		        	cubeCKpiResponse.setNbnrGicOd(rs.getDouble(10));
//		        	cubeCKpiResponse.setIbnrNicOd(rs.getDouble(11));
//		        	cubeCKpiResponse.setNbnrNicOd(rs.getDouble(12));
//		        	cubeCKpiResponse.setIbnrGicHealth(rs.getDouble(13));
//		        	cubeCKpiResponse.setNbnrGicHealth(rs.getDouble(14));
//		        	generalKpiResponseList.add(cubeCKpiResponse);
//		        	
//		        }
//		        
//		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
//		        //System.out.println(jsArray.toString());
//		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//		    } catch (Exception e) {
//		    	System.out.println("kylinDataSource initialize error, ex: " +  e);
//		    	System.out.println();
//		    	System.out.println();
//		    	System.out.println();
//		    	e.printStackTrace();
//		    }finally {
//				connection.close();
//			}
//	        
//				System.out.println("CALLED THE METHOD");
////		return jsArray.toJSONString();
//				return generalKpiResponseList;
//	}
	
	@RequestMapping(value = "/getKpiCubeCDataMock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<CubeCKpiResponseMock> getKpiCubeCData(HttpServletRequest req) throws SQLException {
		List<CubeCKpiResponseMock> generalKpiResponseList=new ArrayList<CubeCKpiResponseMock>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		 Connection connection = null;
		
		String maps= "{}";
		long startTime = System.currentTimeMillis();
       // JSONArray jsArray = new JSONArray();
		System.out.println("Started query execution");
		try {
			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
			
		      Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		      Properties info = new Properties();
		      info.put("user", "ADMIN");
		      info.put("password", "KYLIN");
		        connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
		        System.out.println("Connection status -------------------------->"+connection);
		        Statement stmt = connection.createStatement();
		        
		        String fromMonth = fromDate.split("/")[0];
		        String fromYear = fromDate.split("/")[1];
		        String toMonth = toDate.split("/")[0];
		        String toYear = toDate.split("/")[1];
		        
		        
		        
		        String queryStr =  "SELECT " +
		        		" SUM(FACT_KPI_C.GEP) as FACT_KPI_C_GEP, "+
		        		" SUM(FACT_KPI_C.NEP) as FACT_KPI_C_NEP, "+
				        " SUM(FACT_KPI_C.GEP_OD) as FACT_KPI_C_GEP_OD, "+
				        " SUM(FACT_KPI_C.NEP_OD) as FACT_KPI_C_NEP_OD, "+
				        " SUM(FACT_KPI_C.GEP_TP) as FACT_KPI_C_GEP_TP, "+
				        " SUM(FACT_KPI_C.NEP_TP) as FACT_KPI_C_NEP_TP, "+
				        " SUM(FACT_KPI_C.DISCOUNT_GEP_OD) as FACT_KPI_C_DISCOUNT_GEP_OD, "+
				        " SUM(FACT_KPI_C.DISCOUNT_NEP_OD) as FACT_KPI_C_DISCOUNT_NEP_OD, "+
				        " SUM(FACT_KPI_C.IBNR_GIC_OD) as FACT_KPI_C_IBNR_GIC_OD, "+
				        " SUM(FACT_KPI_C.NBNR_GIC_OD) as FACT_KPI_C_NBNR_GIC_OD, "+
				        " SUM(FACT_KPI_C.IBNR_NIC_OD) as FACT_KPI_C_IBNR_NIC_OD, "+
				        " SUM(FACT_KPI_C.NBNR_NIC_OD) as FACT_KPI_C_NBNR_NIC_OD, "+
				        " SUM(FACT_KPI_C.IBNR_GIC_HEALTH) as FACT_KPI_C_IBNR_GIC_HEALTH, "+
				        " SUM(FACT_KPI_C.NBNR_GIC_HEALTH) as FACT_KPI_C_NBNR_GIC_HEALTH "+
		        		"FROM RSDB.FACT_KPI_C as FACT_KPI_C " +
		        		"INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW " +
		        		"ON FACT_KPI_C.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME " +
		        		"INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW " +
		        		"ON FACT_KPI_C.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND FACT_KPI_C.SUBCHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL " +
		        		"INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW " +
		        		"ON FACT_KPI_C.REGION = DS_MASTER_ZONE_NOW.ZONE_NAME " +
		        		"INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW " +
		        		"ON FACT_KPI_C.STATE = DS_MASTER_STATE_NOW.STATE " +
		        		"INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW " +
		        		"ON FACT_KPI_C.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE " +
		        		"INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW " +
		        		"ON FACT_KPI_C.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE " +
		        		"WHERE (KPI_MONTH BETWEEN "+fromMonth+" AND "+toMonth+") AND KPI_YEAR BETWEEN "+fromYear+" AND "+toYear+"";
       				
								 
		        //String queryStr = "select  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,  SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,  SUM(PREV_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PREV_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PREV_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PREV_YTD_POLICY_COUNT) LASTYR_YTD_POLICY  from (  SELECT  NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME as CLUSTER_NAME  ,NEW_POLICY_FACT.STATE as STATE  ,NEW_MASTER_STATE.STATE_CODE AS STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT as PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC  ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB  ,NEW_POLICY_FACT.BUSINESS_TYPE as BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL as CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL  ,NEW_POLICY_FACT.MAKE as MAKE  ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME as MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_YTD_POLICY_COUNT  FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT  INNER JOIN RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE  ON NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_ZONE.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_CLUSTER as NEW_MASTER_CLUSTER  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_CLUSTER.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_STATE as NEW_MASTER_STATE  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_STATE.STATE  INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH  ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE  INNER JOIN RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT  ON NEW_POLICY_FACT.PRODUCT_CODE = NEW_MASTER_PRODUCT.PRODUCT_CODE  INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as NEW_MASTER_BUSINESS_TYPE  ON NEW_POLICY_FACT.BUSINESS_TYPE = NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE  INNER JOIN RSDB.NEW_MASTER_CHANNEL as NEW_MASTER_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_CHANNEL.CHANNEL_NAME  INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as NEW_MASTER_SUB_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL = NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL  INNER JOIN RSDB.NEW_MASTER_MAKE as NEW_MASTER_MAKE  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME  INNER JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE = NEW_MASTER_MODEL.MODEL_CODE  INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as NEW_MASTER_FIN_YEAR  ON NEW_POLICY_FACT.FIN_YEAR = NEW_MASTER_FIN_YEAR.FIN_YEAR  INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as NEW_MASTER_FIN_MONTH  ON NEW_POLICY_FACT.MONTH_FLAG = NEW_MASTER_FIN_MONTH.ENTRY_MONTH  WHERE 1=1  and NEW_MASTER_PRODUCT.SEGMENT_NEW in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by  NEW_POLICY_FACT.ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME  ,NEW_POLICY_FACT.STATE  ,NEW_MASTER_STATE.STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION  ,NEW_MASTER_PRODUCT.SEGMENT_NEW  ,NEW_POLICY_FACT.BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL  ,NEW_POLICY_FACT.MAKE  ,NEW_POLICY_FACT.MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG  ) CUR_ACT_CUBE  GROUP BY  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE ";
		        System.out.println("queryStr------------------------------ "+queryStr);
		        ResultSet rs = stmt.executeQuery(queryStr);
		        System.out.println("START------------------------------ ");
		        
		        //jsArray = convertToJSON(rs);
		        
		        
		        
		        while(rs.next()) {
		        	
		        	CubeCKpiResponseMock cubeCKpiResponse = new CubeCKpiResponseMock();
		        	cubeCKpiResponse.setGep(rs.getDouble(1));
		        	cubeCKpiResponse.setNep(rs.getDouble(2));
		        	cubeCKpiResponse.setGepOd(rs.getDouble(3));
		        	cubeCKpiResponse.setNepOd(rs.getDouble(4));
		        	cubeCKpiResponse.setGepTp(rs.getDouble(5));
		        	cubeCKpiResponse.setNepTp(rs.getDouble(6));
		        	cubeCKpiResponse.setDiscountGepOd(rs.getDouble(7));
		        	cubeCKpiResponse.setDiscountNepOd(rs.getDouble(8));
		        	cubeCKpiResponse.setIbnrGicOd(rs.getDouble(9));
		        	cubeCKpiResponse.setNbnrGicOd(rs.getDouble(10));
		        	cubeCKpiResponse.setIbnrNicOd(rs.getDouble(11));
		        	cubeCKpiResponse.setNbnrNicOd(rs.getDouble(12));
		        	cubeCKpiResponse.setIbnrGicHealth(rs.getDouble(13));
		        	cubeCKpiResponse.setNbnrGicHealth(rs.getDouble(14));
		        	generalKpiResponseList.add(cubeCKpiResponse);
		        	
		        }
		        
		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
		        //System.out.println(jsArray.toString());
		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		    } catch (Exception e) {
		    	System.out.println("kylinDataSource initialize error, ex: " +  e);
		    	System.out.println();
		    	System.out.println();
		    	System.out.println();
		    	e.printStackTrace();
		    }finally {
				connection.close();
			}
	        
				System.out.println("CALLED THE METHOD");
//		return jsArray.toJSONString();
				return generalKpiResponseList;
	}
	
	
//	@RequestMapping(value = "/getKpiCubeDData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public @ResponseBody
//	List<CubeDKpiResponseMock> getKpiCubeDData(HttpServletRequest req) throws SQLException {
//		List<CubeDKpiResponseMock> generalKpiResponseList=new ArrayList<CubeDKpiResponseMock>();
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User user = userService.findUserByUserName(auth.getName());
//		int userId = user.getId();
//		 Connection connection = null;
//		
//		String maps= "{}";
//		long startTime = System.currentTimeMillis();
//       // JSONArray jsArray = new JSONArray();
//		System.out.println("Started query execution");
//		try {
//			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
//			
//		      Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
//		      Properties info = new Properties();
//		      info.put("user", "ADMIN");
//		      info.put("password", "KYLIN");
//		        connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
//		        System.out.println("Connection status -------------------------->"+connection);
//		        Statement stmt = connection.createStatement();
//		        
//		        String fromMonth = fromDate.split("/")[0];
//		        String fromYear = fromDate.split("/")[1];
//		        String toMonth = toDate.split("/")[0];
//		        String toYear = toDate.split("/")[1];
//		        
//		        
//		        
//		        String queryStr =  "SELECT "+
//		        		" SUM(FACT_KPI_D.REPUDIATEDCLAIMS_PERC) as FACT_KPI_D_REPUDIATEDCLAIMS_PERC, "
//						+ " SUM(FACT_KPI_D.SEVERITY_PERC) as FACT_KPI_D_SEVERITY_PERC, "
//						+ " SUM(FACT_KPI_D.NIC) as FACT_KPI_D_NIC, "
//						+ " SUM(FACT_KPI_D.CLAIM_RATIO) as FACT_KPI_D_CLAIM_RATIO, "
//						+ " SUM(FACT_KPI_D.EXPENSE_RATIO) as FACT_KPI_D_EXPENSE_RATIO, "
//						+ " SUM(FACT_KPI_D.ACTUAL_GIC_OD) as FACT_KPI_D_ACTUAL_GIC_OD, "
//						+ " SUM(FACT_KPI_D.ACTUAL_GIC_TP) as FACT_KPI_D_ACTUAL_GIC_TP, "
//						+ " SUM(FACT_KPI_D.ACTUAL_NIC_OD) as FACT_KPI_D_ACTUAL_NIC_OD, "
//						+ " SUM(FACT_KPI_D.ACTUAL_NIC_TP) as FACT_KPI_D_ACTUAL_NIC_TP, "
//						+ " SUM(FACT_KPI_D.ACQUISTION_COST_RATIO) as FACT_KPI_D_ACQUISTION_COST_RATIO, "
//						+ " SUM(FACT_KPI_D.REPUDIATEDCLAIMS) as FACT_KPI_D_REPUDIATEDCLAIMS, "
//						+ " SUM(FACT_KPI_D.COR) as FACT_KPI_D_COR, "
//						+ " SUM(FACT_KPI_D.REGISTERED_CLAIM) as FACT_KPI_D_REGISTERED_CLAIM "+
//		        		"FROM RSDB.FACT_KPI_D as FACT_KPI_D " +
//		        		"INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW " +
//		        		"ON FACT_KPI_D.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME " +
//		        		"INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW " +
//		        		"ON FACT_KPI_D.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND FACT_KPI_D.SUBCHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL " +
//		        		"INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW " +
//		        		"ON FACT_KPI_D.REGION = DS_MASTER_ZONE_NOW.ZONE_NAME " +
//		        		"INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW " +
//		        		"ON FACT_KPI_D.STATE = DS_MASTER_STATE_NOW.STATE " +
//		        		"INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW " +
//		        		"ON FACT_KPI_D.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE " +
//		        		"INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW " +
//		        		"ON FACT_KPI_D.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE " +
//		        		"WHERE (KPI_MONTH BETWEEN "+fromMonth+" AND "+toMonth+") AND KPI_YEAR BETWEEN "+fromYear+" AND "+toYear+"";
//       				
//								 
//		        //String queryStr = "select  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,  SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,  SUM(PREV_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PREV_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PREV_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PREV_YTD_POLICY_COUNT) LASTYR_YTD_POLICY  from (  SELECT  NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME as CLUSTER_NAME  ,NEW_POLICY_FACT.STATE as STATE  ,NEW_MASTER_STATE.STATE_CODE AS STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT as PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC  ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB  ,NEW_POLICY_FACT.BUSINESS_TYPE as BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL as CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL  ,NEW_POLICY_FACT.MAKE as MAKE  ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME as MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_YTD_POLICY_COUNT  FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT  INNER JOIN RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE  ON NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_ZONE.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_CLUSTER as NEW_MASTER_CLUSTER  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_CLUSTER.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_STATE as NEW_MASTER_STATE  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_STATE.STATE  INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH  ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE  INNER JOIN RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT  ON NEW_POLICY_FACT.PRODUCT_CODE = NEW_MASTER_PRODUCT.PRODUCT_CODE  INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as NEW_MASTER_BUSINESS_TYPE  ON NEW_POLICY_FACT.BUSINESS_TYPE = NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE  INNER JOIN RSDB.NEW_MASTER_CHANNEL as NEW_MASTER_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_CHANNEL.CHANNEL_NAME  INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as NEW_MASTER_SUB_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL = NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL  INNER JOIN RSDB.NEW_MASTER_MAKE as NEW_MASTER_MAKE  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME  INNER JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE = NEW_MASTER_MODEL.MODEL_CODE  INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as NEW_MASTER_FIN_YEAR  ON NEW_POLICY_FACT.FIN_YEAR = NEW_MASTER_FIN_YEAR.FIN_YEAR  INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as NEW_MASTER_FIN_MONTH  ON NEW_POLICY_FACT.MONTH_FLAG = NEW_MASTER_FIN_MONTH.ENTRY_MONTH  WHERE 1=1  and NEW_MASTER_PRODUCT.SEGMENT_NEW in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by  NEW_POLICY_FACT.ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME  ,NEW_POLICY_FACT.STATE  ,NEW_MASTER_STATE.STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION  ,NEW_MASTER_PRODUCT.SEGMENT_NEW  ,NEW_POLICY_FACT.BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL  ,NEW_POLICY_FACT.MAKE  ,NEW_POLICY_FACT.MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG  ) CUR_ACT_CUBE  GROUP BY  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE ";
//		        System.out.println("queryStr------------------------------ "+queryStr);
//		        ResultSet rs = stmt.executeQuery(queryStr);
//		        System.out.println("START------------------------------ ");
//		        
//		        //jsArray = convertToJSON(rs);
//		        
//		        
//		        
//		        while(rs.next()) {
//		        	
//		        	CubeDKpiResponseMock cubeDKpiResponse = new CubeDKpiResponseMock();
//		        	cubeDKpiResponse.setRepdiatedClaimsPerc(rs.getDouble(1));
//		        	cubeDKpiResponse.setSeverityPerc(rs.getDouble(2));
//		        	cubeDKpiResponse.setNic(rs.getDouble(3));
//		        	cubeDKpiResponse.setClaimRatio(rs.getDouble(4));
//		        	cubeDKpiResponse.setExpenseRatio(rs.getDouble(5));
//		        	cubeDKpiResponse.setActualGicOd(rs.getDouble(6));
//		        	cubeDKpiResponse.setActualGicTp(rs.getDouble(7));
//		        	cubeDKpiResponse.setActualNicOd(rs.getDouble(8));
//		        	cubeDKpiResponse.setActualNicTp(rs.getDouble(9));
//		        	cubeDKpiResponse.setAcr(rs.getDouble(10));
//		        	cubeDKpiResponse.setRepudiatedClaims(rs.getDouble(11));
//		        	cubeDKpiResponse.setCor(rs.getDouble(12));
//		        	cubeDKpiResponse.setRegisteredClaims(rs.getDouble(13));
//		        	generalKpiResponseList.add(cubeDKpiResponse);
//		        	
//		        }
//		        
//		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
//		        //System.out.println(jsArray.toString());
//		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//		    } catch (Exception e) {
//		    	System.out.println("kylinDataSource initialize error, ex: " +  e);
//		    	System.out.println();
//		    	System.out.println();
//		    	System.out.println();
//		    	e.printStackTrace();
//		    }finally {
//				connection.close();
//			}
//	        
//				System.out.println("CALLED THE METHOD");
////		return jsArray.toJSONString();
//				return generalKpiResponseList;
//	}
	
	@RequestMapping(value = "/getKpiCubeDDataMock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<CubeDKpiResponseMock> getKpiCubeDData(HttpServletRequest req) throws SQLException {
		List<CubeDKpiResponseMock> generalKpiResponseList=new ArrayList<CubeDKpiResponseMock>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		 Connection connection = null;
		
		String maps= "{}";
		long startTime = System.currentTimeMillis();
       // JSONArray jsArray = new JSONArray();
		System.out.println("Started query execution");
		try {
			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
			
		      Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		      Properties info = new Properties();
		      info.put("user", "ADMIN");
		      info.put("password", "KYLIN");
		        connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
		        System.out.println("Connection status -------------------------->"+connection);
		        Statement stmt = connection.createStatement();
		        
		        String fromMonth = fromDate.split("/")[0];
		        String fromYear = fromDate.split("/")[1];
		        String toMonth = toDate.split("/")[0];
		        String toYear = toDate.split("/")[1];
		        
		        
		        
		        String queryStr =  "SELECT "+
		        		" SUM(FACT_KPI_D.REPUDIATEDCLAIMS_PERC) as FACT_KPI_D_REPUDIATEDCLAIMS_PERC, "
						+ " SUM(FACT_KPI_D.SEVERITY_PERC) as FACT_KPI_D_SEVERITY_PERC, "
						+ " SUM(FACT_KPI_D.NIC) as FACT_KPI_D_NIC, "
						+ " SUM(FACT_KPI_D.CLAIM_RATIO) as FACT_KPI_D_CLAIM_RATIO, "
						+ " SUM(FACT_KPI_D.EXPENSE_RATIO) as FACT_KPI_D_EXPENSE_RATIO, "
						+ " SUM(FACT_KPI_D.ACTUAL_GIC_OD) as FACT_KPI_D_ACTUAL_GIC_OD, "
						+ " SUM(FACT_KPI_D.ACTUAL_GIC_TP) as FACT_KPI_D_ACTUAL_GIC_TP, "
						+ " SUM(FACT_KPI_D.ACTUAL_NIC_OD) as FACT_KPI_D_ACTUAL_NIC_OD, "
						+ " SUM(FACT_KPI_D.ACTUAL_NIC_TP) as FACT_KPI_D_ACTUAL_NIC_TP, "
						+ " SUM(FACT_KPI_D.ACQUISTION_COST_RATIO) as FACT_KPI_D_ACQUISTION_COST_RATIO, "
						+ " SUM(FACT_KPI_D.REPUDIATEDCLAIMS) as FACT_KPI_D_REPUDIATEDCLAIMS, "
						+ " SUM(FACT_KPI_D.COR) as FACT_KPI_D_COR, "
						+ " SUM(FACT_KPI_D.REGISTERED_CLAIM) as FACT_KPI_D_REGISTERED_CLAIM "+
		        		"FROM RSDB.FACT_KPI_D as FACT_KPI_D " +
		        		"INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW " +
		        		"ON FACT_KPI_D.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME " +
		        		"INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW " +
		        		"ON FACT_KPI_D.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND FACT_KPI_D.SUBCHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL " +
		        		"INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW " +
		        		"ON FACT_KPI_D.REGION = DS_MASTER_ZONE_NOW.ZONE_NAME " +
		        		"INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW " +
		        		"ON FACT_KPI_D.STATE = DS_MASTER_STATE_NOW.STATE " +
		        		"INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW " +
		        		"ON FACT_KPI_D.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE " +
		        		"INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW " +
		        		"ON FACT_KPI_D.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE " +
		        		"WHERE (KPI_MONTH BETWEEN "+fromMonth+" AND "+toMonth+") AND KPI_YEAR BETWEEN "+fromYear+" AND "+toYear+"";
       				
								 
		        //String queryStr = "select  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,  SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,  SUM(PREV_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PREV_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PREV_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PREV_YTD_POLICY_COUNT) LASTYR_YTD_POLICY  from (  SELECT  NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME as CLUSTER_NAME  ,NEW_POLICY_FACT.STATE as STATE  ,NEW_MASTER_STATE.STATE_CODE AS STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT as PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC  ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB  ,NEW_POLICY_FACT.BUSINESS_TYPE as BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL as CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL  ,NEW_POLICY_FACT.MAKE as MAKE  ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME as MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR= '2019-2020') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,  SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_MTD_GWP_OUR_SHARE  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end)  as  PREV_YTD_GWP_OUR_SHARE  , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_MTD_POLICY_COUNT  , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019' then  NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) PREV_YTD_POLICY_COUNT  FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT  INNER JOIN RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE  ON NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_ZONE.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_CLUSTER as NEW_MASTER_CLUSTER  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_CLUSTER.ZONE_NAME  INNER JOIN RSDB.NEW_MASTER_STATE as NEW_MASTER_STATE  ON NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_STATE.STATE  INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH  ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE  INNER JOIN RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT  ON NEW_POLICY_FACT.PRODUCT_CODE = NEW_MASTER_PRODUCT.PRODUCT_CODE  INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as NEW_MASTER_BUSINESS_TYPE  ON NEW_POLICY_FACT.BUSINESS_TYPE = NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE  INNER JOIN RSDB.NEW_MASTER_CHANNEL as NEW_MASTER_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_CHANNEL.CHANNEL_NAME  INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as NEW_MASTER_SUB_CHANNEL  ON NEW_POLICY_FACT.CHANNEL = NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL = NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL  INNER JOIN RSDB.NEW_MASTER_MAKE as NEW_MASTER_MAKE  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME  INNER JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL  ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE = NEW_MASTER_MODEL.MODEL_CODE  INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as NEW_MASTER_FIN_YEAR  ON NEW_POLICY_FACT.FIN_YEAR = NEW_MASTER_FIN_YEAR.FIN_YEAR  INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as NEW_MASTER_FIN_MONTH  ON NEW_POLICY_FACT.MONTH_FLAG = NEW_MASTER_FIN_MONTH.ENTRY_MONTH  WHERE 1=1  and NEW_MASTER_PRODUCT.SEGMENT_NEW in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by  NEW_POLICY_FACT.ZONE_NAME  ,NEW_POLICY_FACT.CLUSTER_NAME  ,NEW_POLICY_FACT.STATE  ,NEW_MASTER_STATE.STATE_CODE  ,NEW_POLICY_FACT.BRANCH_CODE  ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME  ,NEW_POLICY_FACT.PRODUCT_CODE  ,NEW_MASTER_PRODUCT.PRODUCT  ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION  ,NEW_MASTER_PRODUCT.SEGMENT_NEW  ,NEW_POLICY_FACT.BUSINESS_TYPE  ,NEW_POLICY_FACT.CHANNEL  ,NEW_POLICY_FACT.SUB_CHANNEL  ,NEW_POLICY_FACT.MAKE  ,NEW_POLICY_FACT.MODEL_CODE  ,NEW_MASTER_MODEL.MODEL_NAME  ,NEW_POLICY_FACT.FIN_YEAR  ,NEW_POLICY_FACT.MONTH_FLAG  ) CUR_ACT_CUBE  GROUP BY  CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE ";
		        System.out.println("queryStr------------------------------ "+queryStr);
		        ResultSet rs = stmt.executeQuery(queryStr);
		        System.out.println("START------------------------------ ");
		        
		        //jsArray = convertToJSON(rs);
		        
		        
		        
		        while(rs.next()) {
		        	
		        	CubeDKpiResponseMock cubeDKpiResponse = new CubeDKpiResponseMock();
		        	cubeDKpiResponse.setRepdiatedClaimsPerc(rs.getDouble(1));
		        	cubeDKpiResponse.setSeverityPerc(rs.getDouble(2));
		        	cubeDKpiResponse.setNic(rs.getDouble(3));
		        	cubeDKpiResponse.setClaimRatio(rs.getDouble(4));
		        	cubeDKpiResponse.setExpenseRatio(rs.getDouble(5));
		        	cubeDKpiResponse.setActualGicOd(rs.getDouble(6));
		        	cubeDKpiResponse.setActualGicTp(rs.getDouble(7));
		        	cubeDKpiResponse.setActualNicOd(rs.getDouble(8));
		        	cubeDKpiResponse.setActualNicTp(rs.getDouble(9));
		        	cubeDKpiResponse.setAcr(rs.getDouble(10));
		        	cubeDKpiResponse.setRepudiatedClaims(rs.getDouble(11));
		        	cubeDKpiResponse.setCor(rs.getDouble(12));
		        	cubeDKpiResponse.setRegisteredClaims(rs.getDouble(13));
		        	generalKpiResponseList.add(cubeDKpiResponse);
		        	
		        }
		        
		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
		        //System.out.println(jsArray.toString());
		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		    } catch (Exception e) {
		    	System.out.println("kylinDataSource initialize error, ex: " +  e);
		    	System.out.println();
		    	System.out.println();
		    	System.out.println();
		    	e.printStackTrace();
		    }finally {
				connection.close();
			}
	        
				System.out.println("CALLED THE METHOD");
//		return jsArray.toJSONString();
				return generalKpiResponseList;
	}
	
}
