
package com.prodian.rsgirms.dashboard.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.kylin.jdbc.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.prodian.rsgirms.constants.RMSConstants;
import com.prodian.rsgirms.dashboard.model.BranchMaster;
import com.prodian.rsgirms.dashboard.model.IntermediaryMaster;
import com.prodian.rsgirms.dashboard.model.ModelMaster;
import com.prodian.rsgirms.dashboard.model.MonthlyDashboardDetails;
import com.prodian.rsgirms.dashboard.model.ProductMaster;
import com.prodian.rsgirms.dashboard.model.SubChannelMaster;
import com.prodian.rsgirms.dashboard.model.UserDashboard;
import com.prodian.rsgirms.dashboard.repository.IntermediaryMasterRepository;
import com.prodian.rsgirms.dashboard.repository.MonthlyDashboardDetailsRepository;
import com.prodian.rsgirms.dashboard.repository.ProductMasterRepository;
import com.prodian.rsgirms.dashboard.repository.SubChannelMasterRepository;
import com.prodian.rsgirms.dashboard.repository.UserDashboardRepository;
import com.prodian.rsgirms.dashboard.response.BudgetCubeResponse;
import com.prodian.rsgirms.dashboard.response.ClaimsCubeResponse;
import com.prodian.rsgirms.dashboard.response.ClaimsCubeResponseNew;
import com.prodian.rsgirms.dashboard.response.ClaimsSingleLineCubeResponse;
import com.prodian.rsgirms.dashboard.response.CubeAKpiResponse;
import com.prodian.rsgirms.dashboard.response.GepCubeResponse;
import com.prodian.rsgirms.dashboard.response.GepCubeResponseFinal;
import com.prodian.rsgirms.dashboard.response.GepCubeResponseNew;
import com.prodian.rsgirms.dashboard.response.GwpkpiResponse;
import com.prodian.rsgirms.dashboard.response.InsCubeResponseNew;
import com.prodian.rsgirms.dashboard.response.KpiFiltersResponse;
import com.prodian.rsgirms.dashboard.response.PolicyCubeResponseNew;
import com.prodian.rsgirms.dashboard.response.ReserverSingleLineCubeResponseNew;
import com.prodian.rsgirms.dashboard.response.SingleLineCubeResponseNew;
import com.prodian.rsgirms.dashboard.service.KpiDashboardService;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterRequest;
import com.prodian.rsgirms.usermatrix.service.UserMatrixService;

import com.prodian.rsgirms.dashboard.modelfunction.GicNicPsqlFunction;
import com.prodian.rsgirms.dashboard.rsrepository.GicNicPsqlRepository;
import com.prodian.rsgirms.dashboard.modelfunction.AcqPsqlFunction;
import com.prodian.rsgirms.dashboard.modelfunction.GwpNwpPsqlFunction;
import com.prodian.rsgirms.dashboard.modelfunction.GepNepPsqlFunctions;

@Controller
public class KpiUpdatedDataController {

	@Autowired
	private UserService userService;

	@Autowired
	private KpiDashboardService kpiDashboardService;

	@Autowired
	private UserMatrixService userMatrixService;

	@Autowired
	private UserDashboardRepository userDashboardRepository;

	@Autowired
	private ProductMasterRepository productMasterRepository;
	
	@Autowired
	private SubChannelMasterRepository subChannelMasterRepository;
	
	@Autowired
	private MonthlyDashboardDetailsRepository monthlyDashboardDetailsRepository;

	@Autowired
	private IntermediaryMasterRepository intermediaryMasterRepository;

	@Autowired
	private GicNicPsqlRepository gicNicPsqlRepository;

	@GetMapping("/motorKpiDataUpdatedNew")
public ModelAndView getMockMotorKpiDashBoard() {
		ModelAndView model = new ModelAndView("motorKpiDataUpdatedNew");
		try{
			/*commneted for local*/
		KpiFiltersResponse res = kpiDashboardService.getKpiFilters();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		//User user = userService.findUserByUserName("shyam");
		int userId = user.getId();

		List<Integer> dashboardIds = userDashboardRepository.findByUserId(userId).stream()
				.map(UserDashboard::getDashboardId).collect(Collectors.toList());
		UserMatrixMasterRequest req = new UserMatrixMasterRequest();
		req.setDashboardId(dashboardIds);
		/*commneted for local*/
		UserMatrixMasterRequest filterRequest = userMatrixService.getUserMatrixChildByUserId(userId, req);
		model.addObject("filters", filterRequest);
		model.addObject("channels", res.getChannelMasters());
		System.out.println("channels1-------------->"+res.getChannelMasters().size());
		model.addObject("businessTypes", res.getBusinessTypeMasters());
		model.addObject("campaigns", res.getCampaignMasters());
		//model.addObject("subChannels", res.getSubChannelMasters());
		model.addObject("subChannels",new SubChannelMaster());
		//System.out.println("subChannels1-------------->"+res.getSubChannelMasters().size());
		model.addObject("finMonths", res.getFinMonthMasters());
		model.addObject("finYears", res.getFinYearMasters());
		// model.addObject("makes",res.getMakeMasters());
		List<ModelMaster> models = res.getModelMasters();
		model.addObject("models", (models == null || models.isEmpty()) ? ""
				: models.stream().map(ModelMaster::getModel).collect(Collectors.toSet()));
		model.addObject("makes", (models == null || models.isEmpty()) ? ""
				: models.stream().map(ModelMaster::getMake).collect(Collectors.toSet()));
		model.addObject("modelGroups", (models == null || models.isEmpty()) ? ""
				: models.stream().map(ModelMaster::getModelGroup).collect(Collectors.toSet()));
		model.addObject("modelClassifications", (models == null || models.isEmpty()) ? ""
				: models.stream().map(ModelMaster::getModelClassification).collect(Collectors.toSet()));
		model.addObject("intermediaries", res.getIntermediaryMasters());
		//model.addObject("intermediaries", null);
		model.addObject("oas", res.getOaMasters());
		model.addObject("policyCategories", res.getPolicyCategoryMasters());
		model.addObject("policyTypes", res.getPolicyTypeMasters());

		List<ProductMaster> products = res.getProductMasters();
		model.addObject("products", products);


		List<ProductMaster> motorProducts = (products == null || products.isEmpty()) ? new ArrayList<ProductMaster>()
				: products.stream()
						.filter(p -> (p.getProductType() != null && p.getProductType().toLowerCase().contains("motor")))
						.collect(Collectors.toList());
		List<ProductMaster> healthProducts = (products == null || products.isEmpty()) ? new ArrayList<ProductMaster>()
				: products.stream().filter(
						p -> (p.getProductType() != null && p.getProductType().toLowerCase().contains("health")))
						.collect(Collectors.toList());

		model.addObject("motorProducts", motorProducts);
		model.addObject("healthProducts", healthProducts);
		model.addObject("subLines", res.getSublineMasters());
		model.addObject("branches", res.getBranchMasters());
		model.addObject("cities", (res.getBranchMasters() == null || res.getBranchMasters().isEmpty())
				? new ArrayList<BranchMaster>()
				: res.getBranchMasters().stream().map(BranchMaster::getRaDescription).collect(Collectors.toSet()));
		model.addObject("regions",
				(res.getBranchMasters() == null || res.getBranchMasters().isEmpty()) ? new ArrayList<BranchMaster>()
						: res.getBranchMasters().stream().map(BranchMaster::getRegion).collect(Collectors.toSet()));
		model.addObject("states",
				(res.getBranchMasters() == null || res.getBranchMasters().isEmpty()) ? new ArrayList<BranchMaster>()
						: res.getBranchMasters().stream().map(BranchMaster::getStateNew).collect(Collectors.toSet()));
		model.addObject("clusters", (res.getBranchMasters() == null || res.getBranchMasters().isEmpty())
				? new ArrayList<BranchMaster>()
				: res.getBranchMasters().stream().map(BranchMaster::getClusterName).collect(Collectors.toSet()));
		model.addObject("subClusters",
				(res.getBranchMasters() == null || res.getBranchMasters().isEmpty()) ? new ArrayList<BranchMaster>()
						: res.getBranchMasters().stream().map(BranchMaster::getSubCluster).collect(Collectors.toSet()));
		model.addObject("zones",
				(res.getBranchMasters() == null || res.getBranchMasters().isEmpty()) ? new ArrayList<BranchMaster>()
						: res.getBranchMasters().stream().map(BranchMaster::getZone).collect(Collectors.toSet()));
		model.addObject("userName", user.getName() + " " + user.getLastName());
		
		List<MonthlyDashboardDetails> monthlyDetails = monthlyDashboardDetailsRepository.findAll();
		Set<String> fuelTypes = monthlyDetails.stream().map(MonthlyDashboardDetails::getFuelType)
				.collect(Collectors.toSet());
		Set<String> ncbFlags = monthlyDetails.stream().map(MonthlyDashboardDetails::getNcbFlag)
				.collect(Collectors.toSet());
		model.addObject("fuelTypes", fuelTypes);
		model.addObject("ncbFlags", ncbFlags);
		
		model.addObject("channelsNow", res.getChannelMastersNow());
		model.addObject("subChannelsNow", res.getSubChannelMastersNow());
		model.addObject("makesNow", res.getMakeMastersNow());
		model.addObject("modelGroupsNow", res.getModelGroupMastersNow());
		model.addObject("businessTypesNow", res.getBusinessTypeMastersNow());
		model.addObject("fuelTypesNow", res.getFuelTypeMastersNow());
		model.addObject("stateGroupsNow", res.getStateGroupMastersNow());
		
		/*uncommneted for local*/
		/*model.addObject("filters", null);
		model.addObject("channels", null);
		model.addObject("businessTypes", null);
		model.addObject("campaigns", null);
		model.addObject("subChannels", null);
		model.addObject("finMonths", null);
		model.addObject("finYears",null);
		model.addObject("models", null);
		model.addObject("makes", null);
		model.addObject("modelGroups", null);
		model.addObject("modelClassifications", null);
		model.addObject("intermediaries", null);
		model.addObject("oas", null);
		model.addObject("policyCategories", null);
		model.addObject("policyTypes", null);
		model.addObject("products", null);
		model.addObject("motorProducts", null);
		model.addObject("healthProducts", null);
		model.addObject("subLines", null);
		model.addObject("branches", null);
		model.addObject("cities", null);
		model.addObject("regions",
				null);
		model.addObject("states",
				null);
		model.addObject("clusters", null);
		model.addObject("subClusters",
				null);
		model.addObject("zones",
				null);
		model.addObject("userName", user.getName() + " " + user.getLastName());
		
		model.addObject("fuelTypes", null);
		model.addObject("ncbFlags", null);*/
		}catch(Exception e){
			e.printStackTrace();
		}
		return model;
	}

	
	
	/*@RequestMapping(value="/getIntermediaries", method=RequestMethod.GET)
	public String getIntermediaries(ModelMap map) {
	    // TODO: retrieve the new value here so you can add it to model map
	    map.addAttribute("intermediaries", intermediaryMasterRepository.findAll());
	    System.out.println("list size-->"+intermediaryMasterRepository.findAll().size());

	    // change "myview" to the name of your view 
	    return "motorKpiNew :: #iCode";
	}*/
	
	
	
	@RequestMapping(value="/getOEMwiseGwpUpdated", method=RequestMethod.GET)
	@ResponseBody
public List<GwpkpiResponse> getOEMwiseGwp() throws SQLException {
	    List<GwpkpiResponse> list = new ArrayList<>();
	    Connection connection = null;
	    String queryStr="";
	    try{
	    Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password", "KYLIN");
		
		connection = driverManager 
				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		Statement stmt = connection.createStatement();
		/*queryStr = " select case when channel='OEM' then 'OEM' ELSE 'NON_OEM' end as oem_non_oem, coalesce(c.fueltypE,a.fueltypE) as fueltype ,STATE_GROUPING,sum(ins_gwp) "+
					" from "+
					" RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL a  "+
					" left join RSA_DWH_CITY_GROUPING_MASTER_FINAL b on a.regLocation=b.citycode  "+
					" left join RSA_DWH_MODEL_MASTER c on a.make=c.make and a.modelcode=c.model_code "+
					" where financial_year='2018' group by case when channel='OEM' then 'OEM' ELSE 'NON_OEM' end,coalesce(c.fueltypE,a.fueltypE),STATE_GROUPING" ;	 */
		
		
		/*queryStr = " SELECT oem_non_oem,upper(fueltype),STATE_GROUPING,SUM(CASE WHEN substring(inception_date,1,10) >= DATE '2021-10-01' and  "+
				" substring(inception_date,1,10) <= DATE '2021-10-31' THEN (GWP) ELSE 0.0 END) CM_GWP,  "+
				" SUM(CASE WHEN substring(inception_date,1,10) >= DATE '2021-09-01' and   "+
				" substring(inception_date,1,10) <= DATE '2021-09-30' THEN (GWP) ELSE 0.0 END) PM_GWP  "+
				" FROM(  "+
				" SELECT inception_date,case when channel='OEM' then 'OEM' ELSE 'NON_OEM' end as oem_non_oem,coalesce(c.fueltypE,a.fueltypE) as fueltype ,STATE_GROUPING,  "+
				" SUM(INS_GWP) GWP FROM RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL a  "+
				" left join RSA_DWH_CITY_GROUPING_MASTER_FINAL b on a.regLocation=b.citycode   "+
				" left join RSA_DWH_MODEL_MASTER c on a.make=c.make and a.modelcode=c.model_code  "+
				" GROUP BY inception_date,case when channel='OEM' then 'OEM' ELSE 'NON_OEM' end,coalesce(c.fueltypE,a.fueltypE) ,STATE_GROUPING) X  "+
				" group by oem_non_oem,upper(fueltype),STATE_GROUPING ";*/
		
		queryStr = " SELECT oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END) HIGHEND,upper(fueltype),coalesce(NCB_FLAG,'N') NCB_FL,coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS'),SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(true,false)+"' and   substring(inception_date,1,10) <= DATE '"+getCustomLastDate(true,false)+"' THEN (GWP) ELSE 0.0 END) CM_GWP,"
				+ "   SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(false,true)+"' and    substring(inception_date,1,10) <= DATE '"+getCustomLastDate(false,true)+"' THEN (GWP) ELSE 0.0 END) PM_GWP "
						+ "  FROM(   SELECT inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end as oem_non_oem,HIGH_END,coalesce(c.fueltypE,a.fueltypE) as fueltype ,NCB_FLAG,b.cityname,statename,  "
						+ " SUM(INS_GWP) GWP FROM RSDB.RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_CURRENT a LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW ON a.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  left join RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL b on RSA_DWH_CITY_MASTER_NOW.CITYCODE=b.citycode left join RSDB.RSA_DWH_MODEL_MASTER_CURRENT c on  a.modelcode=c.model_code  GROUP BY inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end,HIGH_END,coalesce(c.fueltypE,a.fueltypE),NCB_FLAG ,b.cityname,statename) X   group by oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END),upper(fueltype),coalesce(NCB_FLAG,'N'),coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS') ";
		
		/*queryStr = " SELECT oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END) HIGHEND,upper(fueltype),coalesce(NCB_FLAG,'N') NCB_FL,coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS'),SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(true,false)+"' and   substring(inception_date,1,10) <= DATE '"+getCustomLastDate(true,false)+"' THEN (GWP) ELSE 0.0 END) CM_GWP,   SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(false,true)+"' and    substring(inception_date,1,10) <= DATE '"+getCustomLastDate(false,true)+"' THEN (GWP) ELSE 0.0 END) PM_GWP   FROM( "+
				   " SELECT inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end as oem_non_oem, MM_MODELCLASSIFICATION as HIGH_END ,coalesce(MM_FUELTYPE,PPC_FUELTYPE) as fueltype ,NCB_FLAG,cityname,statename,   SUM(COVERAGE_PREIMUM) GWP FROM RSA_DWH_OEM_CLASIFICATION_FACT  GROUP BY inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end, MM_MODELCLASSIFICATION,coalesce(MM_FUELTYPE,PPC_FUELTYPE),NCB_FLAG ,cityname,statename "+ 
				   " ) X   group by oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END),upper(fueltype),coalesce(NCB_FLAG,'N'),coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS') ";*/ 
		
//		String query="";
		
		
		System.out.println("queryStr------------------------------ " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");

	
		while (rs.next()) {
			GwpkpiResponse response = new GwpkpiResponse();
			response.setOemType(rs.getString(1));
			response.setHighEnd(rs.getString(2));
			response.setFuelType(rs.getString(3));
			response.setNcb(rs.getString(4));
			response.setCity(rs.getString(5));
			response.setState(rs.getString(6));
			response.setCmGwp(rs.getDouble(7));
			response.setPmGwp(rs.getDouble(8));
			list.add(response);
			
		}	
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}
	    // change "myview" to the name of your view 
	    return list;
	}
	
	
	@GetMapping("/getPolicyCubeDataUpdatedNew/{claimType}")
	@ResponseBody
public List<PolicyCubeResponseNew> getPolicyCubeDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest,@PathVariable(value="claimType") String claimType)
			throws SQLException {
		Connection connection = null;
		List<PolicyCubeResponseNew> kpiResponseList = new ArrayList<PolicyCubeResponseNew>();
		long startTime = System.currentTimeMillis();
		try {
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			/*String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";*/

			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];

			String queryStr = "";
			System.out.println("Query filterRequest: " + (filterRequest.getAddOnNew().equals( "Include")));
			if(filterRequest.getAddOnNew().equals("Include")){

			 queryStr += "select "  
					+ "SUM(POLICY_COUNT) as POLICY_COUNT, " 
					+ "SUM(case when x.CATEGORY='Comprehensive' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_OD, " 
					+ "SUM(case when x.CATEGORY='TP' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_TP, " 
					+ "SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_others, " 
					+ "SUM(ACQ_COST) as ACQ_COST, " 
					+ "SUM(case when x.CATEGORY='Comprehensive' THEN ACQ_COST ELSE 0 END) as ACQ_COST_OD, " 
					+ "SUM(case when x.CATEGORY='TP' THEN ACQ_COST ELSE 0 END) as ACQ_COST_TP,"  
					 + "SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN ACQ_COST ELSE 0 END) as ACQ_COST_others, " 
					 + "SUM(LIVESCOVERED) AS LIVESCOVERED "
					+ "from(  SELECT  SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.LIVESCOVERED) as LIVESCOVERED  ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.ACQ_COST) as ACQ_COST  ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_COUNT ) as POLICY_COUNT  ,ADDON_TYPE,CATEGORY " 
					+ "FROM RSDB.RSA_KPI_FACT_POLICY_CURRENT as RSA_KPI_FACT_POLICY_FINAL_NOW " 
					+ "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL " 
					 + "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE " 
					 + "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE " 
					 + "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE " 
					 + "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE " 
					+ "LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY " 
					+ "LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  "
					+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  "
					+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE " 
					+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  "
					+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_POLICY_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  "
					+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";   
			} else if(filterRequest.getAddOnNew().equals( "Exclude")){
				 
				 queryStr += "SELECT (POLICY_COUNT - ADDON_POLICY_COUNT_OD - ADDON_POLICY_COUNT_TP - ADDON_POLICY_COUNT_others) POLICY_COUNT,"
				 		+ "(POLICY_COUNT_OD - ADDON_POLICY_COUNT_OD ) POLICY_COUNT_OD,"
				 		+ "(POLICY_COUNT_TP - ADDON_POLICY_COUNT_TP ) POLICY_COUNT_TP,"
				 		+ "(POLICY_COUNT_others - ADDON_POLICY_COUNT_others ) POLICY_COUNT_others,"
				 		+ "(ACQ_COST - ADDON_ACQ_COST_OD - ADDON_ACQ_COST_TP - ADDON_ACQ_COST_others) ACQ_COST,"
				 		+ "(ACQ_COST_OD - ADDON_ACQ_COST_OD ) ACQ_COST_OD,"
				 		+ "(ACQ_COST_TP - ADDON_ACQ_COST_TP ) ACQ_COST_TP,"
				 		+ "(ACQ_COST_others - ADDON_ACQ_COST_others ) ACQ_COST_others,"
				 		+ "(LIVESCOVERED) LIVESCOVERED "
				 		+ "FROM ("
				 		+ "select  "
				 		+ "SUM(POLICY_COUNT) as POLICY_COUNT,  "
				 		+ "SUM(case when x.CATEGORY='Comprehensive' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_OD,  "
				 		+ "SUM(case when x.CATEGORY='TP' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_TP,  "
				 		+ "SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_others,  "
				 		+ "SUM(ACQ_COST) as ACQ_COST,  "
				 		+ "SUM(case when x.CATEGORY='Comprehensive' THEN ACQ_COST ELSE 0 END) as ACQ_COST_OD,  "
				 		+ "SUM(case when x.CATEGORY='TP' THEN ACQ_COST ELSE 0 END) as ACQ_COST_TP,  "
				 		+ "SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN ACQ_COST ELSE 0 END) as ACQ_COST_others,  "
				 		+ "SUM(LIVESCOVERED) AS LIVESCOVERED,"
				 		+ "SUM(case when x.CATEGORY='Comprehensive' AND ADDON_TYPE='YES' THEN POLICY_COUNT ELSE 0 END) as ADDON_POLICY_COUNT_OD,  "
				 		+ "SUM(case when x.CATEGORY='TP' AND ADDON_TYPE='YES' THEN POLICY_COUNT ELSE 0 END) as ADDON_POLICY_COUNT_TP, "
				 		+ "SUM(case when coalesce(x.CATEGORY,'Others')='Others' AND ADDON_TYPE='YES' THEN POLICY_COUNT ELSE 0 END) as ADDON_POLICY_COUNT_others, "
				 		+ "SUM(case when x.CATEGORY='Comprehensive' AND ADDON_TYPE='YES' THEN ACQ_COST ELSE 0 END) as ADDON_ACQ_COST_OD,  "
				 		+ "SUM(case when x.CATEGORY='TP' AND ADDON_TYPE='YES' THEN ACQ_COST ELSE 0 END) as ADDON_ACQ_COST_TP,  "
				 		+ "SUM(case when coalesce(x.CATEGORY,'Others')='Others' AND ADDON_TYPE='YES' THEN ACQ_COST ELSE 0 END) as ADDON_ACQ_COST_others "
				 		+ "from(  SELECT  SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.LIVESCOVERED) as LIVESCOVERED  ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.ACQ_COST) as ACQ_COST  ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_COUNT ) as POLICY_COUNT  ,ADDON_TYPE,CATEGORY  FROM RSDB.RSA_KPI_FACT_POLICY_CURRENT as RSA_KPI_FACT_POLICY_FINAL_NOW  "
				 		+ "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "
				 		+ "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  "
				 		+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  "
				 		+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "
				 		+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  "
				 		+ "LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  "
				 		+ "LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  "
				 		+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  "
				 		+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  "
				 		+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  "
				 		+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_POLICY_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  "
				 		+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";
					
						 		 
				}else if(filterRequest.getAddOnNew().equals("Only Addon")) {
				
				queryStr += "SELECT (ADDON_POLICY_COUNT_OD + ADDON_POLICY_COUNT_TP + ADDON_POLICY_COUNT_others) POLICY_COUNT, "
                      + "(ADDON_POLICY_COUNT_OD ) POLICY_COUNT_OD, "
                      + "(ADDON_POLICY_COUNT_TP ) POLICY_COUNT_TP, "
                      + "(ADDON_POLICY_COUNT_others ) POLICY_COUNT_others, "
                      + "(ADDON_ACQ_COST_OD + ADDON_ACQ_COST_TP + ADDON_ACQ_COST_others) ACQ_COST, "
                      + "(ADDON_ACQ_COST_OD ) ACQ_COST_OD, "
                      + "(ADDON_ACQ_COST_TP ) ACQ_COST_TP, "
                      + "(ADDON_ACQ_COST_others ) ACQ_COST_others, "
                      + "(LIVESCOVERED) LIVESCOVERED "
                      + "FROM ("
                      + "select  "
                      + "SUM(POLICY_COUNT) as POLICY_COUNT,  "
                      + "SUM(case when x.CATEGORY='Comprehensive' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_OD,  "
                      + "SUM(case when x.CATEGORY='TP' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_TP,  "
                      + "SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_others,  "
                      + "SUM(ACQ_COST) as ACQ_COST,  "
                      + "SUM(case when x.CATEGORY='Comprehensive' THEN ACQ_COST ELSE 0 END) as ACQ_COST_OD,  "
                      + "SUM(case when x.CATEGORY='TP' THEN ACQ_COST ELSE 0 END) as ACQ_COST_TP,  "
                      + "SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN ACQ_COST ELSE 0 END) as ACQ_COST_others,  "
                      + "SUM(LIVESCOVERED) AS LIVESCOVERED,"
                      + "SUM(case when x.CATEGORY='Comprehensive' AND ADDON_TYPE='YES' THEN POLICY_COUNT ELSE 0 END) as ADDON_POLICY_COUNT_OD,  "
                      + "SUM(case when x.CATEGORY='TP' AND ADDON_TYPE='YES' THEN POLICY_COUNT ELSE 0 END) as ADDON_POLICY_COUNT_TP,  "
                      + "SUM(case when coalesce(x.CATEGORY,'Others')='Others' AND ADDON_TYPE='YES' THEN POLICY_COUNT ELSE 0 END) as ADDON_POLICY_COUNT_others, "
                      + "SUM(case when x.CATEGORY='Comprehensive' AND ADDON_TYPE='YES' THEN ACQ_COST ELSE 0 END) as ADDON_ACQ_COST_OD,  "
                      + "SUM(case when x.CATEGORY='TP' AND ADDON_TYPE='YES' THEN ACQ_COST ELSE 0 END) as ADDON_ACQ_COST_TP,  "
                      + "SUM(case when coalesce(x.CATEGORY,'Others')='Others' AND ADDON_TYPE='YES' THEN ACQ_COST ELSE 0 END) as ADDON_ACQ_COST_others "
                      + "from(  SELECT  SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.LIVESCOVERED) as LIVESCOVERED  ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.ACQ_COST) as ACQ_COST  ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_COUNT ) as POLICY_COUNT  ,ADDON_TYPE,CATEGORY  FROM RSDB.RSA_KPI_FACT_POLICY_CURRENT as RSA_KPI_FACT_POLICY_FINAL_NOW  "
                      + "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  "
                      + "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  "
                      + "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  "
                      + "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  "
                      + "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  "
                      + "LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  "
                      + "LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  "
                      + "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  "
                      + "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  "
                      + "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  "
                      + "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_POLICY_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  "
                      + "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";
				
			}
			
			
			String finstartDate = fromYear + "-" + fromMonth + "-01";
			String finEndDate = toYear + "-" + toMonth + "-31";
			
			
			if(claimType.equalsIgnoreCase("R")){
				queryStr += " WHERE ";
			queryStr += getFinCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));

			}else if(claimType.equalsIgnoreCase("U")){
				
				queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";
			}
			
			if (filterRequest != null && filterRequest.getPolicyTypes() != null
					&& !filterRequest.getPolicyTypes().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
					vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
					if (i != filterRequest.getPolicyTypes().size() - 1) {
						vals += ",";
					}
				}
				
				queryStr += " and TRIM(RSA_DWH_COVERCODE_MASTER.CATEGORY) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getChannelNow() != null
					&& !filterRequest.getChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
					vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getSubChannelNow() != null
					&& !filterRequest.getSubChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
					vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getSubChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getMakeNow() != null
					&& !filterRequest.getMakeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
					vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
					if (i != filterRequest.getMakeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MAKE) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getModelGroupNow() != null
					&& !filterRequest.getModelGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
					vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getModelGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELGROUP) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getFuelTypeNow() != null
					&& !filterRequest.getFuelTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
					vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getFuelTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and coalesce(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE,'N') in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getStateGroupNow() != null
					&& !filterRequest.getStateGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
					vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getStateGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
			}
			/*if (filterRequest != null && filterRequest.getNcbNow() != null
					&& !filterRequest.getNcbNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
					vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
					if (i != filterRequest.getNcbNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.getNcbNow) in (" + vals + ")";
			}*/
			
			/*latest*/
			/*if (filterRequest != null && filterRequest.getNcbNow() != null
					&& !filterRequest.getNcbNow().isEmpty()) {
				
				
				String Nvals = "and (RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_PREM)= '0'";
				String Yvals = "and (RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_PREM)<> '0'";
				int cvalcounter = 0,cvalNcounter = 0;
				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
					
					 if(filterRequest.getNcbNow().get(i).trim().contains("N")){
						 if(cvalcounter==0)
						queryStr += Nvals;
						 cvalcounter++;
					 }else if(filterRequest.getNcbNow().get(i).trim().contains("Y")){
						if(cvalNcounter==0)
						queryStr += Yvals;
						cvalNcounter++;
					 }
				
					System.out.println("NCB NEW POLICY query------------------------------ " + queryStr);
					
				}
				
				
				
			}*/
			
			
			/*String vals = "";
			for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
				vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
				if (i != filterRequest.getMotorNcbFlag().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
			
			
			queryStr += "  ";*/			
			
			if (filterRequest != null && filterRequest.getMotorChannel() != null
					&& !filterRequest.getMotorChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
					vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
					if (i != filterRequest.getMotorChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL) in (" + vals + ")";
			}


			if (filterRequest != null && filterRequest.getMotorSubChannel() != null
					&& !filterRequest.getMotorSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
					vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getMotorSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorZone() != null
					&& !filterRequest.getMotorZone().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
					vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
					if (i != filterRequest.getMotorZone().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCluster() != null
					&& !filterRequest.getMotorCluster().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
					vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
					if (i != filterRequest.getMotorCluster().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorState() != null
					&& !filterRequest.getMotorState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
					vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
					if (i != filterRequest.getMotorState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCity() != null
					&& !filterRequest.getMotorCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
					vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
					if (i != filterRequest.getMotorCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}
		

			if (filterRequest != null && filterRequest.getMotorBranch() != null
					&& !filterRequest.getMotorBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
					vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
					if (i != filterRequest.getMotorBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
					&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorFuelType() != null
					&& !filterRequest.getMotorFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
					vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
					if (i != filterRequest.getMotorFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.FUELTYPE) in (" + vals + ")";
			}
			
			/*if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
					&& !filterRequest.getMotorNcbFlag().isEmpty()) {
				
				
				String Nvals = "and (RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_PREM)= '0'";
				String Yvals = "and (RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_PREM)<> '0'";
				int cvalcounter = 0,cvalNcounter = 0;
				for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
					
					 if(filterRequest.getMotorNcbFlag().get(i).trim().contains("N")){
						 if(cvalcounter==0)
						queryStr += Nvals;
						 cvalcounter++;
					 }else if(filterRequest.getMotorNcbFlag().get(i).trim().contains("Y")){
						if(cvalNcounter==0)
						queryStr += Yvals;
						cvalNcounter++;
					 }
				
					System.out.println("NCB NEW POLICY query------------------------------ " + queryStr);
					
				}
				
			}*/
			
			/*String vals = "";
			for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
				vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
				if (i != filterRequest.getMotorNcbFlag().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
			
			
			queryStr += "  ";*/
			
			if (filterRequest != null && filterRequest.getMotorCarType() != null
					&& !filterRequest.getMotorCarType().isEmpty()) {
				String vals = "'HIGHEND','High End'";
				String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
				int cvalcounter = 0,cvalNHEcounter = 0;
				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
					
					 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
						 if(cvalcounter==0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
						 cvalcounter++;
					 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
						if(cvalNHEcounter==0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
						cvalNHEcounter++;
					 }
				
					System.out.println("HE query------------------------------ " + queryStr);
					
				}
				
			}
			
			
	
			if(filterRequest.getAddOnNew().equals("Include")){
				queryStr += " group by ADDON_TYPE,category ) x";
			}else if(filterRequest.getAddOnNew().equals("Exclude")){
				queryStr += " group by ADDON_TYPE,category ) x ) mm";
			}else if(filterRequest.getAddOnNew().equals("Only Addon")){
				queryStr += " group by ADDON_TYPE,category ) x) mm";
			}

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				PolicyCubeResponseNew res = new PolicyCubeResponseNew();
				res.setWrittenPolicies(rs.getDouble(1));
				res.setWrittenPoliciesComprehensive(rs.getDouble(2));
				res.setWrittenPoliciesTp(rs.getDouble(3));
				res.setWrittenPoliciesOthers(rs.getDouble(4));
				res.setAcqCost(rs.getDouble(5));
				res.setAcqCostComprehensive(rs.getDouble(6));
				res.setAcqCostTp(rs.getDouble(7));
				res.setAcqCostOthers(rs.getDouble(8));
				res.setLivesCovered(rs.getDouble(9));
				kpiResponseList.add(res);
			}

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}
		return kpiResponseList;
	}
	
	
	@GetMapping("/getInsCubeDataUpdatedNew/{claimType}")
	@ResponseBody
public List<InsCubeResponseNew> getInsCubeDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest, @PathVariable(value="claimType") String claimType)
			throws SQLException {
		Connection connection = null;
		List<InsCubeResponseNew> kpiResponseList = new ArrayList<InsCubeResponseNew>();
		long startTime = System.currentTimeMillis();
		try {
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			/*String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";*/

			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];

			String queryStr= "";
			System.out.println("Query filterRequest: " + (filterRequest.getAddOnNew().equals( "Include")));
			if(filterRequest.getAddOnNew().equals("Include")) {
				queryStr += "SELECT "
						+ "SUM(INS_GWP) as INS_GWP_POLICY_COMP,"
						+ "0 as InsGwpPolicyTp,"
						+ "0 as InsGwpPolicyOthers,"
						+ "SUM(INS_GWP_OD) as INS_GWP_OD_POLICY_COMP,"
						+ "0 as INS_GWP_OD_POLICY_TP,"
						+ "0 as INS_GWP_OD_POLICY_others,"
						+ "SUM(INS_GWP_TP) as INS_GWP_TP_POLICY_COMP,"
						+ "0 as INS_GWP_TP_POLICY_TP,"
						+ "0 as INS_GWP_TP_POLICY_others,"
						+ "SUM(INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD_POLICY_COMP,"
						+ "0 as INS_GWP_DISCOUNT_OD_POLICY_TP,"
						+ "0 as INS_GWP_DISCOUNT_OD_POLICY_others,"
						+ "SUM((INS_GWP *(1-od_quota_share-od_obligatory))+(INS_GWP*od_quota_share*od_RI_COMMISSION)) as INS_NWP_POLICY_COMP,"
						+ "0 as INS_NWP_POLICY_TP,"
						+ "0 as INS_NWP_POLICY_others,"
						+ "SUM((INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION)) as INS_NWP_OD_POLICY_COMP,"
						+ "0 as INS_NWP_OD_POLICY_TP,"
						+ "0 as INS_NWP_OD_POLICY_others,"
						+ "SUM((INS_GWP_TP *(1-od_quota_share-od_obligatory))+(INS_GWP_TP*od_quota_share*od_RI_COMMISSION)) as INS_NWP_TP_POLICY_COMP,"
						+ "0 as INS_NWP_OD_POLICY_TP,"
						+ "0 as INS_NWP_OD_POLICY_others,"
						+ "SUM((INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION)) as INS_NWP_DISCOUNT_OD_POLICY,"
						+ "0 as INS_NWP_DISCOUNT_OD_POLICY_TP,"
						+ "0 as INS_NWP_DISCOUNT_OD_POLICY_others,"
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_COMP,"
						+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_TP,"
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_others,"
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_COMP,"
						+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_TP,"
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_others,"
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_COMP,"
						+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_TP,"
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_others,"
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_COMP,"
						+ "SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_TP,"
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_others,"
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_NCB *(1-od_quota_share-od_obligatory))+(INS_GWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_COMP,"
						+ "SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_TP,"
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_others,"
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_COMP,  SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_TP,  SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_others "
						+ "FROM ( SELECT  SUM(INS_GWP) as INS_GWP  ,SUM(INS_GWP_OD) as INS_GWP_OD  ,SUM(INS_GWP_TP) as INS_GWP_TP  ,SUM(INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD  ,SUM(INS_NWP) as INS_NWP  ,SUM(INS_NWP_OD) as INS_NWP_OD  ,SUM(INS_NWP_TP) as INS_NWP_TP  ,SUM(INS_NWP_DISCOUNT_OD) as INS_NWP_DISCOUNT_OD  ,SUM(INS_GWP_DEP) as INS_GWP_DEP  ,SUM(INS_GWP_NCB) as INS_GWP_NCB  ,SUM(INS_GWP_OTHERADDON) as INS_GWP_OTHERADDON  ,SUM(INS_NWP_DEP) as INS_NWP_DEP  ,SUM(INS_NWP_NCB) as INS_NWP_NCB  ,SUM(INS_NWP_OTHERADDON) as INS_NWP_OTHERADDON  , CATEGORY, uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code  FROM RSDB.RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_CURRENT as RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE ";
			}
			
			else if(filterRequest.getAddOnNew().equals("Exclude")) {
				System.out.println("Query filterRequest: " + (filterRequest.getAddOnNew().equals( "Exclude")));
				queryStr += "SELECT "
						+ "(INS_GWP_POLICY - INS_GWP_DEP_POLICY_COMP - INS_GWP_DEP_POLICY_TP - INS_GWP_DEP_POLICY_others - INS_GWP_NCB_POLICY_COMP - INS_GWP_NCB_POLICY_TP - INS_GWP_NCB_POLICY_others - INS_GWP_OTHERADDON_POLICY_COMP - INS_GWP_OTHERADDON_POLICY_TP - INS_GWP_OTHERADDON_POLICY_others) AS INS_GWP_POLICY_COMP, "
						+ "0 as INS_GWP_POLICY_TP, "
						+ "0 as INS_GWP_POLICY_others, "
						+ "(INS_GWP_OD_POLICY - INS_GWP_DEP_POLICY_COMP - INS_GWP_NCB_POLICY_COMP - INS_GWP_OTHERADDON_POLICY_COMP) AS INS_GWP_OD_POLICY_COMP, "
						+ "0 as INS_GWP_OD_POLICY_TP, "
						+ "0 as INS_GWP_OD_POLICY_others, "
						+ "(INS_GWP_TP_POLICY - INS_GWP_DEP_POLICY_TP - INS_GWP_NCB_POLICY_TP - INS_GWP_OTHERADDON_POLICY_TP) AS INS_GWP_TP_POLICY_COMP, "
						+ "0 as INS_GWP_TP_POLICY_TP, "
						+ "0 as INS_GWP_TP_POLICY_others, "
						+ "(INS_GWP_DISCOUNT_OD_POLICY) as INS_GWP_DISCOUNT_OD_POLICY_COMP, "
						+ "0 as INS_GWP_DISCOUNT_OD_POLICY_TP, "
						+ "0 as INS_GWP_DISCOUNT_OD_POLICY_others, "
						+ "(INS_NWP_POLICY - INS_NWP_DEP_POLICY_COMP - INS_NWP_DEP_POLICY_TP - INS_NWP_DEP_POLICY_others - INS_NWP_NCB_POLICY_COMP - INS_NWP_NCB_POLICY_TP - INS_NWP_NCB_POLICY_others - INS_NWP_OTHERADDON_POLICY_COMP - INS_NWP_OTHERADDON_POLICY_TP - INS_NWP_OTHERADDON_POLICY_others) AS INS_NWP_POLICY_COMP, "
						+ "0 as INS_NWP_POLICY_TP, "
						+ "0 as INS_NWP_POLICY_others, "
						+ "(INS_NWP_OD_POLICY - INS_NWP_DEP_POLICY_COMP - INS_NWP_NCB_POLICY_COMP - INS_NWP_OTHERADDON_POLICY_COMP) AS INS_NWP_OD_POLICY_COMP, "
						+ "0 as INS_NWP_OD_POLICY_TP, "
						+ "0 as INS_NWP_OD_POLICY_others, "
						+ "(INS_NWP_TP_POLICY - INS_NWP_DEP_POLICY_TP - INS_NWP_NCB_POLICY_TP - INS_NWP_OTHERADDON_POLICY_TP) AS INS_NWP_TP_POLICY_COMP, "
						+ "0 as INS_NWP_TP_POLICY_TP, "
						+ "0 as INS_NWP_TP_POLICY_others, "
						+ "(INS_NWP_DISCOUNT_OD_POLICY) as INS_NWP_DISCOUNT_OD_POLICY_COMP, "
						+ "0 as INS_NWP_DISCOUNT_OD_POLICY_TP, "
						+ "0 as INS_NWP_DISCOUNT_OD_POLICY_others, "
						+ "0 as INS_GWP_DEP_POLICY_COMP, "
						+ "0 as INS_GWP_DEP_POLICY_TP, "
						+ "0 as INS_GWP_DEP_POLICY_others, "
						+ "0 as INS_GWP_NCB_POLICY_COMP, "
						+ "0 as INS_GWP_NCB_POLICY_TP, "
						+ "0 as INS_GWP_NCB_POLICY_others, "
						+ "0 as INS_GWP_OTHERADDON_POLICY_COMP, "
						+ "0 as INS_GWP_OTHERADDON_POLICY_TP, "
						+ "0 as INS_GWP_OTHERADDON_POLICY_others, "
						+ "0 as INS_NWP_DEP_POLICY_COMP, "
						+ "0 as INS_NWP_DEP_POLICY_TP, "
						+ "0 as INS_NWP_DEP_POLICY_others, "
						+ "0 as INS_NWP_NCB_POLICY_COMP, "
						+ "0 as INS_NWP_NCB_POLICY_TP, "
						+ "0 as INS_NWP_NCB_POLICY_others, "
						+ "0 as INS_NWP_OTHERADDON_POLICY_COMP, "
						+ "0 as INS_NWP_OTHERADDON_POLICY_TP, "
						+ "0 as INS_NWP_OTHERADDON_POLICY_others "
						+ "FROM ( SELECT SUM(INS_GWP) as INS_GWP_POLICY, "
						+ "SUM(INS_GWP_OD) as INS_GWP_OD_POLICY, "
						+ "SUM(INS_GWP_TP) as INS_GWP_TP_POLICY, "
						+ "SUM(INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD_POLICY, "
						+ "SUM((INS_GWP *(1-od_quota_share-od_obligatory))+(INS_GWP*od_quota_share*od_RI_COMMISSION)) as INS_NWP_POLICY, "
						+ "SUM((INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION)) as INS_NWP_OD_POLICY, "
						+ "SUM((INS_GWP_TP *(1-od_quota_share-od_obligatory))+(INS_GWP_TP*od_quota_share*od_RI_COMMISSION)) as INS_NWP_TP_POLICY, "
						+ "SUM((INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION)) as INS_NWP_DISCOUNT_OD_POLICY, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_NCB *(1-od_quota_share-od_obligatory))+(INS_GWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_COMP,  SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_TP,  SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_others "
						+ "FROM ( SELECT  SUM(INS_GWP) as INS_GWP  ,SUM(INS_GWP_OD) as INS_GWP_OD  ,SUM(INS_GWP_TP) as INS_GWP_TP  ,SUM(INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD  ,SUM(INS_NWP) as INS_NWP  ,SUM(INS_NWP_OD) as INS_NWP_OD  ,SUM(INS_NWP_TP) as INS_NWP_TP  ,SUM(INS_NWP_DISCOUNT_OD) as INS_NWP_DISCOUNT_OD  ,SUM(INS_GWP_DEP) as INS_GWP_DEP  ,SUM(INS_GWP_NCB) as INS_GWP_NCB  ,SUM(INS_GWP_OTHERADDON) as INS_GWP_OTHERADDON  ,SUM(INS_NWP_DEP) as INS_NWP_DEP  ,SUM(INS_NWP_NCB) as INS_NWP_NCB  ,SUM(INS_NWP_OTHERADDON) as INS_NWP_OTHERADDON  , CATEGORY, uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code  FROM RSDB.RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_CURRENT as RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE ";
				
			}
			else if(filterRequest.getAddOnNew().equals("Only Addon")) {
				System.out.println("Query filterRequest: " + (filterRequest.getAddOnNew().equals( "Only Addon")));
				queryStr += "SELECT "
						+ "(INS_GWP_DEP_POLICY_COMP + INS_GWP_DEP_POLICY_TP + INS_GWP_DEP_POLICY_others + INS_GWP_NCB_POLICY_COMP + INS_GWP_NCB_POLICY_TP + INS_GWP_NCB_POLICY_others + INS_GWP_OTHERADDON_POLICY_COMP + INS_GWP_OTHERADDON_POLICY_TP + INS_GWP_OTHERADDON_POLICY_others) AS INS_GWP_POLICY_COMP, "
						+ "0 as INS_GWP_POLICY_TP, "
						+ "0 as INS_GWP_POLICY_others, "
						+ "(INS_GWP_DEP_POLICY_COMP + INS_GWP_NCB_POLICY_COMP + INS_GWP_OTHERADDON_POLICY_COMP) AS INS_GWP_OD_POLICY_COMP, "
						+ "0 as INS_GWP_OD_POLICY_TP, "
						+ "0 as INS_GWP_OD_POLICY_others, "
						+ "(INS_GWP_DEP_POLICY_TP + INS_GWP_NCB_POLICY_TP + INS_GWP_OTHERADDON_POLICY_TP) AS INS_GWP_TP_POLICY_COMP, "
						+ "0 as INS_GWP_TP_POLICY_TP, "
						+ "0 as INS_GWP_TP_POLICY_others, "
						+ "(INS_GWP_DISCOUNT_OD_POLICY) as INS_GWP_DISCOUNT_OD_POLICY_COMP, "
						+ "0 as INS_GWP_DISCOUNT_OD_POLICY_TP, "
						+ "0 as INS_GWP_DISCOUNT_OD_POLICY_others, "
						+ "(INS_NWP_DEP_POLICY_COMP + INS_NWP_DEP_POLICY_TP + INS_NWP_DEP_POLICY_others + INS_NWP_NCB_POLICY_COMP + INS_NWP_NCB_POLICY_TP + INS_NWP_NCB_POLICY_others + INS_NWP_OTHERADDON_POLICY_COMP + INS_NWP_OTHERADDON_POLICY_TP + INS_NWP_OTHERADDON_POLICY_others) AS INS_NWP_POLICY_COMP, "
						+ "0 as INS_NWP_POLICY_TP, "
						+ "0 as INS_NWP_POLICY_others, "
						+ "(INS_NWP_DEP_POLICY_COMP + INS_NWP_NCB_POLICY_COMP + INS_NWP_OTHERADDON_POLICY_COMP) AS INS_NWP_OD_POLICY_COMP, "
						+ "0 as INS_NWP_OD_POLICY_TP, "
						+ "0 as INS_NWP_OD_POLICY_others, "
						+ "(INS_NWP_DEP_POLICY_TP + INS_NWP_NCB_POLICY_TP + INS_NWP_OTHERADDON_POLICY_TP) AS INS_NWP_TP_POLICY_COMP, "
						+ "0 as INS_NWP_TP_POLICY_TP, "
						+ "0 as INS_NWP_TP_POLICY_others, "
						+ "(INS_NWP_DISCOUNT_OD_POLICY) as INS_NWP_DISCOUNT_OD_POLICY_COMP, "
						+ "0 as INS_NWP_DISCOUNT_OD_POLICY_TP, "
						+ "0 as INS_NWP_DISCOUNT_OD_POLICY_others, "
						+ "0 as INS_GWP_DEP_POLICY_COMP, "
						+ "0 as INS_GWP_DEP_POLICY_TP, "
						+ "0 as INS_GWP_DEP_POLICY_others, "
						+ "0 as INS_GWP_NCB_POLICY_COMP, "
						+ "0 as INS_GWP_NCB_POLICY_TP, "
						+ "0 as INS_GWP_NCB_POLICY_others, "
						+ "0 as INS_GWP_OTHERADDON_POLICY_COMP, "
						+ "0 as INS_GWP_OTHERADDON_POLICY_TP, "
						+ "0 as INS_GWP_OTHERADDON_POLICY_others, "
						+ "0 as INS_NWP_DEP_POLICY_COMP, "
						+ "0 as INS_NWP_DEP_POLICY_TP, "
						+ "0 as INS_NWP_DEP_POLICY_others, "
						+ "0 as INS_NWP_NCB_POLICY_COMP, "
						+ "0 as INS_NWP_NCB_POLICY_TP, "
						+ "0 as INS_NWP_NCB_POLICY_others, "
						+ "0 as INS_NWP_OTHERADDON_POLICY_COMP, "
						+ "0 as INS_NWP_OTHERADDON_POLICY_TP, "
						+ "0 as INS_NWP_OTHERADDON_POLICY_others "
						+ "FROM ( SELECT SUM(INS_GWP) as INS_GWP_POLICY, "
						+ "SUM(INS_GWP_OD) as INS_GWP_OD_POLICY, "
						+ "SUM(INS_GWP_TP) as INS_GWP_TP_POLICY, "
						+ "SUM(INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD_POLICY, "
						+ "SUM((INS_GWP *(1-od_quota_share-od_obligatory))+(INS_GWP*od_quota_share*od_RI_COMMISSION)) as INS_NWP_POLICY, "
						+ "SUM((INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION)) as INS_NWP_OD_POLICY, "
						+ "SUM((INS_GWP_TP *(1-od_quota_share-od_obligatory))+(INS_GWP_TP*od_quota_share*od_RI_COMMISSION)) as INS_NWP_TP_POLICY, "
						+ "SUM((INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION)) as INS_NWP_DISCOUNT_OD_POLICY, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_NCB *(1-od_quota_share-od_obligatory))+(INS_GWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_COMP, "
						+ "SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_TP, "
						+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_others, "
						+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_COMP,  SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_TP,  SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_others "
						+ "FROM ( SELECT  SUM(INS_GWP) as INS_GWP  ,SUM(INS_GWP_OD) as INS_GWP_OD  ,SUM(INS_GWP_TP) as INS_GWP_TP  ,SUM(INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD  ,SUM(INS_NWP) as INS_NWP  ,SUM(INS_NWP_OD) as INS_NWP_OD  ,SUM(INS_NWP_TP) as INS_NWP_TP  ,SUM(INS_NWP_DISCOUNT_OD) as INS_NWP_DISCOUNT_OD  ,SUM(INS_GWP_DEP) as INS_GWP_DEP  ,SUM(INS_GWP_NCB) as INS_GWP_NCB  ,SUM(INS_GWP_OTHERADDON) as INS_GWP_OTHERADDON  ,SUM(INS_NWP_DEP) as INS_NWP_DEP  ,SUM(INS_NWP_NCB) as INS_NWP_NCB  ,SUM(INS_NWP_OTHERADDON) as INS_NWP_OTHERADDON  , CATEGORY, uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code  FROM RSDB.RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_CURRENT as RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE ";
			}
			
			
			String finstartDate = fromYear + "-" + fromMonth + "-01";
			String finEndDate = toYear + "-" + toMonth + "-31";
			
			
			if(claimType.equalsIgnoreCase("R")){
				queryStr += " WHERE  ";
				queryStr += getFinCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));

			}else if(claimType.equalsIgnoreCase("U")){
				
				queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";
			}
			
			if (filterRequest != null && filterRequest.getPolicyTypes() != null
					&& !filterRequest.getPolicyTypes().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
					vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
					if (i != filterRequest.getPolicyTypes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_COVERCODE_MASTER.CATEGORY) in (" + vals + ")";
			}
				
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getChannelNow() != null
					&& !filterRequest.getChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
					vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getSubChannelNow() != null
					&& !filterRequest.getSubChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
					vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getSubChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getMakeNow() != null
					&& !filterRequest.getMakeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
					vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
					if (i != filterRequest.getMakeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MAKE) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getModelGroupNow() != null
					&& !filterRequest.getModelGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
					vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getModelGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELGROUP) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getFuelTypeNow() != null
					&& !filterRequest.getFuelTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
					vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getFuelTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and coalesce(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE,'N') in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getStateGroupNow() != null
					&& !filterRequest.getStateGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
					vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getStateGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getNcbNow() != null
					&& !filterRequest.getNcbNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
					vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
					if (i != filterRequest.getNcbNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
			}

			
			if (filterRequest != null && filterRequest.getMotorChannel() != null
					&& !filterRequest.getMotorChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
					vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
					if (i != filterRequest.getMotorChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorSubChannel() != null
					&& !filterRequest.getMotorSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
					vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getMotorSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
			}

			/*if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}*/
			
			if (filterRequest != null && filterRequest.getMotorZone() != null
					&& !filterRequest.getMotorZone().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
					vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
					if (i != filterRequest.getMotorZone().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCluster() != null
					&& !filterRequest.getMotorCluster().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
					vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
					if (i != filterRequest.getMotorCluster().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorState() != null
					&& !filterRequest.getMotorState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
					vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
					if (i != filterRequest.getMotorState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCity() != null
					&& !filterRequest.getMotorCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
					vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
					if (i != filterRequest.getMotorCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBranch() != null
					&& !filterRequest.getMotorBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
					vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
					if (i != filterRequest.getMotorBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
					&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorFuelType() != null
					&& !filterRequest.getMotorFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
					vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
					if (i != filterRequest.getMotorFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
					&& !filterRequest.getMotorNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
					vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
					if (i != filterRequest.getMotorNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
			}
			
			/*if (filterRequest != null && filterRequest.getMotorCarType() != null
					&& !filterRequest.getMotorCarType().isEmpty()) {
				String vals = "'HIGHEND','High End'";
				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
					
					if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
						if (i != filterRequest.getMotorNcbFlag().size() - 1) {
							vals += ",";
						}
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
					}else{
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) not in (" + vals + ")";
					}
				
					System.out.println("HE query------------------------------ " + queryStr);
					
				}
				
			}*/
			
			if (filterRequest != null && filterRequest.getMotorCarType() != null
					&& !filterRequest.getMotorCarType().isEmpty()) {
				String vals = "'HIGHEND','High End'";
				String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
				int cvalcounter = 0,cvalNHEcounter = 0;
				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
					
					 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
						 if(cvalcounter==0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
						 cvalcounter++;
					 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
						if(cvalNHEcounter==0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
						cvalNHEcounter++;
					 }
				
					System.out.println("HE query------------------------------ " + queryStr);
					
				}
				
			}
			

			/*queryStr += " group by category ) x";*/
			if(filterRequest.getAddOnNew().equals("Include")){
				queryStr += " group by category,uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code ) aa, (select r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE,sum(r.OD_OBLIGATORY) OD_OBLIGATORY,sum(r.OD_QUOTA_SHARE) OD_QUOTA_SHARE,sum(OD_RI_COMMISSION) OD_RI_COMMISSION,sum(r.TP_OBLIGATORY) TP_OBLIGATORY,sum(r.TP_QUOTA_SHARE) TP_QUOTA_SHARE,sum(TP_RI_COMMISSION) TP_RI_COMMISSION   from rsa_dwh_ri_obligatory_master1_new r group by r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE) bb where aa.uw_year=bb.underwriting_year and aa.product_Code=bb.xgen_productcode";
			}else if(filterRequest.getAddOnNew().equals("Exclude")){
				queryStr += " group by category,uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code ) aa, (select r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE,sum(r.OD_OBLIGATORY) OD_OBLIGATORY,sum(r.OD_QUOTA_SHARE) OD_QUOTA_SHARE,sum(OD_RI_COMMISSION) OD_RI_COMMISSION,sum(r.TP_OBLIGATORY) TP_OBLIGATORY,sum(r.TP_QUOTA_SHARE) TP_QUOTA_SHARE,sum(TP_RI_COMMISSION) TP_RI_COMMISSION   from rsa_dwh_ri_obligatory_master1_new r group by r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE) bb where aa.uw_year=bb.underwriting_year and aa.product_Code=bb.xgen_productcode)mm";
			}else if(filterRequest.getAddOnNew().equals("Only Addon")){
				queryStr += " group by category,uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code ) aa, (select r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE,sum(r.OD_OBLIGATORY) OD_OBLIGATORY,sum(r.OD_QUOTA_SHARE) OD_QUOTA_SHARE,sum(OD_RI_COMMISSION) OD_RI_COMMISSION,sum(r.TP_OBLIGATORY) TP_OBLIGATORY,sum(r.TP_QUOTA_SHARE) TP_QUOTA_SHARE,sum(TP_RI_COMMISSION) TP_RI_COMMISSION   from rsa_dwh_ri_obligatory_master1_new r group by r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE) bb where aa.uw_year=bb.underwriting_year and aa.product_Code=bb.xgen_productcode)mm";
			}
			
//			
//			queryStr += "group by category,uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code ) aa, (select r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE,sum(r.OD_OBLIGATORY) OD_OBLIGATORY,sum(r.OD_QUOTA_SHARE) OD_QUOTA_SHARE,sum(OD_RI_COMMISSION) OD_RI_COMMISSION,sum(r.TP_OBLIGATORY) TP_OBLIGATORY,sum(r.TP_QUOTA_SHARE) TP_QUOTA_SHARE,sum(TP_RI_COMMISSION) TP_RI_COMMISSION   from rsa_dwh_ri_obligatory_master1_new r group by r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE) bb "
//			+"where aa.uw_year = bb.underwriting_year "
//			+"and aa.product_Code = bb.xgen_productcode";
//			
			
			
			/*queryStr += " group by category,uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code ) aa,"
					+ " (select r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE,sum(r.OD_OBLIGATORY) OD_OBLIGATORY,sum(r.OD_QUOTA_SHARE) OD_QUOTA_SHARE,sum(OD_RI_COMMISSION) OD_RI_COMMISSION,sum(r.TP_OBLIGATORY) TP_OBLIGATORY,sum(r.TP_QUOTA_SHARE) TP_QUOTA_SHARE,sum(TP_RI_COMMISSION) TP_RI_COMMISSION  "
					+ " from rsa_dwh_ri_obligatory_master1_new r "
					+ "group by r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE) bb "
					+ "where aa.uw_year=bb.underwriting_year and aa.product_Code=bb.xgen_productcode";*/

			System.out.println("queryStr------------------------------ NWP " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				InsCubeResponseNew res = new InsCubeResponseNew();
				
				res.setInsGwpPolicyComprehensive(rs.getDouble(1));
				res.setInsGwpPolicyTp(rs.getDouble(2));
				res.setInsGwpPolicyOthers(rs.getDouble(3));
				res.setInsGwpOdPolicyComprehensive(rs.getDouble(4));
				res.setInsGwpOdPolicyTp(rs.getDouble(5));
				res.setInsGwpOdPolicyOthers(rs.getDouble(6));
				res.setInsGwpTpPolicyComprehensive(rs.getDouble(7));
				res.setInsGwpTpPolicyTp(rs.getDouble(8));
				res.setInsGwpTpPolicyOthers(rs.getDouble(9));
				res.setInsGwpDiscountPolicyComprehensive(rs.getDouble(10));
				res.setInsGwpDiscountPolicyTp(rs.getDouble(11));
				res.setInsGwpDiscountPolicyOthers(rs.getDouble(12));
				
				res.setInsNwpPolicyComprehensive(rs.getDouble(13));
				res.setInsNwpPolicyTp(rs.getDouble(14));
				res.setInsNwpPolicyOthers(rs.getDouble(5));
				res.setInsNwpOdPolicyComprehensive(rs.getDouble(16));
				res.setInsNwpOdPolicyTp(rs.getDouble(17));
				res.setInsNwpOdPolicyOthers(rs.getDouble(18));
				res.setInsNwpTpPolicyComprehensive(rs.getDouble(19));
				res.setInsNwpTpPolicyTp(rs.getDouble(20));
				res.setInsNwpTpPolicyOthers(rs.getDouble(21));
				res.setInsNwpDiscountPolicyComprehensive(rs.getDouble(21));
				res.setInsNwpDiscountPolicyTp(rs.getDouble(23));
				res.setInsNwpDiscountPolicyOthers(rs.getDouble(24));
				
				res.setInsGwpDepPolicyComprehensive(rs.getDouble(25));
				res.setInsGwpDepPolicyTp(rs.getDouble(26));
				res.setInsGwpDepPolicyOthers(rs.getDouble(27));
				res.setInsGwpNcbPolicyComprehensive(rs.getDouble(28));
				res.setInsGwpNcbPolicyTp(rs.getDouble(29));
				res.setInsGwpNcbPolicyOthers(rs.getDouble(30));
				res.setInsGwpOtherAddonPolicyComprehensive(rs.getDouble(31));
				res.setInsGwpOtherAddonPolicyTp(rs.getDouble(32));
				res.setInsGwpOtherAddonPolicyOthers(rs.getDouble(33));
				
				res.setInsNwpDepPolicyComprehensive(rs.getDouble(34));
				res.setInsNwpDepPolicyTp(rs.getDouble(35));
				res.setInsNwpDepPolicyOthers(rs.getDouble(36));
				res.setInsNwpNcbPolicyComprehensive(rs.getDouble(37));
				res.setInsNwpNcbPolicyTp(rs.getDouble(38));
				res.setInsNwpNcbPolicyOthers(rs.getDouble(39));
				res.setInsNwpOtherAddonPolicyComprehensive(rs.getDouble(40));
				res.setInsNwpOtherAddonPolicyTp(rs.getDouble(41));
				res.setInsNwpOtherAddonPolicyOthers(rs.getDouble(42));
				
				kpiResponseList.add(res);
			}

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}
		return kpiResponseList;
	}

	
	/**
	 * @author Aravindharaj P
	 * @created dec 01, 2020 12:11:00 PM
	 * @filename KpiUpdatedDataController.java
	 * @package com.prodian.rsgirms.dashboard.controller
	 */
	
	
@GetMapping("/getInsNewCubeDataForUW")
@ResponseBody
public List<InsCubeResponseNew> getInsCubeDataForUW(HttpServletRequest req, UserMatrixMasterRequest filterRequest) throws SQLException {
		
		Connection connection = null;
		List<InsCubeResponseNew> kpiResponseList = new ArrayList<InsCubeResponseNew>();
		long startTime = System.currentTimeMillis();
		
		try {
			
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();
			
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];
			
			String queryStr = 
					"SELECT "
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP) as INS_GWP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_OD) as INS_GWP_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_TP) as INS_GWP_TP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_DISCOUNT_OD) as INS_NWP_DISCOUNT_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_DEP) as INS_GWP_DEP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_DEP_OD) as INS_GWP_DEP_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_DEP_TP) as INS_GWP_DEP_TP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_NCB) as INS_GWP_NCB,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_NCB_OD) as INS_GWP_NCB_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_NCB_TP) as INS_GWP_NCB_TP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_OTHER_ADDON) as INS_GWP_OTHER_ADDON,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_OTHER_ADDON_OD) as INS_GWP_OTHER_ADDON_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_OTHER_ADDON_TP) as INS_GWP_OTHER_ADDON_TP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP) as INS_NWP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_OD) as INS_NWP_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_TP) as INS_NWP_TP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_DEP) as INS_NWP_DEP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_DEP_OD) as INS_NWP_DEP_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_DEP_TP) as INS_NWP_DEP_TP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_NCB) as INS_NWP_NCB,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_NCB_OD) as INS_NWP_NCB_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_NCB_TP) as INS_NWP_NCB_TP,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_OTHER_ADDON) as INS_NWP_OTHER_ADDON,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_OTHER_ADDON_OD) as INS_NWP_OTHER_ADDON_OD,"
					+ "SUM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_OTHER_ADDON_TP) as INS_NWP_OTHER_ADDON_TP "
					+ "FROM RSDB.RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE as RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE "
					+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
					+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
					+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  "
					+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "
					+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
					+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
					+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "
					+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "
					+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE ";
					
			String finstartDate = fromYear + "-" + fromMonth + "-01";
			String finEndDate = toYear + "-" + toMonth + "-31";
			
			System.out.println(finstartDate + finEndDate);
			
			
			queryStr+="WHERE (SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"')";
			
			if (filterRequest != null && filterRequest.getChannelNow() != null
					&& !filterRequest.getChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
					vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.CHANNEL) in ("+ vals +")";
			}
			
			if(filterRequest != null && filterRequest.getChannelNew() != null && !filterRequest.getChannelNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNew().size(); i++) {
					vals += "'" + filterRequest.getChannelNew().get(i).trim() + "'";
					if (i != filterRequest.getChannelNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.CHANNEL_NEW) in (" + vals + ")";

			}
			
			if (filterRequest != null && filterRequest.getSubChannelNow() != null
					&& !filterRequest.getSubChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
					vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getSubChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.CHANNEL_NEW) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.AGENT_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorFuelType() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and coalesce(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.FUELTYPE,'N') in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getNcbNow() != null
					&& !filterRequest.getNcbNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
					vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
					if (i != filterRequest.getNcbNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.NCB_FLAG) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorBranch() != null
					&& !filterRequest.getMotorBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
					vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
					if (i != filterRequest.getMotorBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.BRANCH_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.BUSINESS_TYPE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMakeNow() != null
					&& !filterRequest.getMakeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
					vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
					if (i != filterRequest.getMakeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.MAKE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getModelGroupNow() != null
					&& !filterRequest.getModelGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
					vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getModelGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.MODELGROUP) in (" + vals + ")";
			}
			
//			if (filterRequest != null && filterRequest.getPolicyTypes() != null
//					&& !filterRequest.getPolicyTypes().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//					vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//					if (i != filterRequest.getPolicyTypes().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.POLICY_TYPE) in (" + vals + ")";
//			}
			
			if(filterRequest != null && filterRequest.getPolicyTypeNew() != null && !filterRequest.getPolicyTypeNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getPolicyTypeNew().size(); i++) {
					vals += "'" + filterRequest.getPolicyTypeNew().get(i).trim() + "'";
					if (i != filterRequest.getPolicyTypeNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.POLICY_TYPE_NEW) in (" + vals + ")";
			}
			
			if(filterRequest != null && filterRequest.getCategorisation() != null && !filterRequest.getCategorisation().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getCategorisation().size(); i++) {
					vals += "'" + filterRequest.getCategorisation().get(i).trim() + "'";
					if (i != filterRequest.getCategorisation().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.CATEGORISATION) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getVehicleAge() != null
					&& !filterRequest.getVehicleAge().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getVehicleAge().size(); i++) {
					vals += "'" + filterRequest.getVehicleAge().get(i).trim() + "'";
					if (i != filterRequest.getVehicleAge().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.VEHICLEAGE) in (" + vals + ")";
			}
			
			if(filterRequest != null && filterRequest.getEngineCapacity() != null && !filterRequest.getEngineCapacity().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getEngineCapacity().size(); i++) {
					vals += "'" + filterRequest.getEngineCapacity().get(i).trim() + "'";
					if (i != filterRequest.getEngineCapacity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.ENGINECAPACITY) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getIntermediaryNames() != null
					&& !filterRequest.getIntermediaryNames().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getIntermediaryNames().size(); i++) {
					vals += "'" + filterRequest.getIntermediaryNames().get(i).trim() + "'";
					if (i != filterRequest.getIntermediaryNames().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCarType() != null
					&& !filterRequest.getMotorCarType().isEmpty()) {
				String vals = "'HIGHEND','High End'";
				String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
				int cvalcounter = 0,cvalNHEcounter = 0;
				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
					
					 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
						 if(cvalcounter==0)
						queryStr += "and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
						 cvalcounter++;
					 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
						if(cvalNHEcounter==0)
						queryStr += "and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
						cvalNHEcounter++;
					 }
				
					System.out.println("Hign End Query Inside InsCube ------------------------------ " + queryStr);
					
				}
				
			}
			
			if (filterRequest != null && filterRequest.getMotorZone() != null
					&& !filterRequest.getMotorZone().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
					vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
					if (i != filterRequest.getMotorZone().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCluster() != null
					&& !filterRequest.getMotorCluster().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
					vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
					if (i != filterRequest.getMotorCluster().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorState() != null
					&& !filterRequest.getMotorState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
					vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
					if (i != filterRequest.getMotorState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCity() != null
					&& !filterRequest.getMotorCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
					vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
					if (i != filterRequest.getMotorCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getStateGroupNow() != null
					&& !filterRequest.getStateGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
					vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getStateGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
			}
			
			System.out.println("queryStr------------------------------ GWP&NWP Inside UW" + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				InsCubeResponseNew res = new InsCubeResponseNew();
				
				res.setInsGwpPolicy(rs.getDouble(1));
				res.setInsGwpPolicyComprehensive(rs.getDouble(2));
				res.setInsGwpPolicyTp(rs.getDouble(3));
				
				res.setInsGwpDiscountPolicyComprehensive(rs.getDouble(4));
				res.setInsNwpDiscountPolicyComprehensive(rs.getDouble(5));
				
				res.setInsGwpDepPolicy(rs.getDouble(6));
				res.setInsGwpDepPolicyComprehensive(rs.getDouble(7));
				res.setInsGwpDepPolicyTp(rs.getDouble(8));
				
				res.setInsGwpNcbPolicy(rs.getDouble(9));
				res.setInsGwpNcbPolicyComprehensive(rs.getDouble(10));
				res.setInsGwpNcbPolicyTp(rs.getDouble(11));
				
				res.setInsGwpOtherAddonPolicy(rs.getDouble(12));
				res.setInsGwpOtherAddonPolicyComprehensive(rs.getDouble(13));
				res.setInsGwpOtherAddonPolicyTp(rs.getDouble(14));
				
				res.setInsNwpPolicy(rs.getDouble(15));
				res.setInsNwpPolicyComprehensive(rs.getDouble(16));
				res.setInsNwpPolicyTp(rs.getDouble(17));
				
				res.setInsNwpDepPolicy(rs.getDouble(18));
				res.setInsNwpDepPolicyComprehensive(rs.getDouble(19));
				res.setInsNwpDepPolicyTp(rs.getDouble(20));
				
				res.setInsNwpNcbPolicy(rs.getDouble(21));
				res.setInsNwpNcbPolicyComprehensive(rs.getDouble(22));
				res.setInsNwpNcbPolicyTp(rs.getDouble(23));
				
				res.setInsNwpOtherAddonPolicy(rs.getDouble(24));
				res.setInsNwpOtherAddonPolicyComprehensive(rs.getDouble(25));
				res.setInsNwpOtherAddonPolicyTp(rs.getDouble(26));
				
				kpiResponseList.add(res);
			}

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

		}catch(Exception e) {
			
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println(e.getMessage());
			
		}finally {
			connection.close();
		}

		return kpiResponseList;
	}
	
@GetMapping("/getInsNewCubeDataForFIN")
@ResponseBody
public List<InsCubeResponseNew> getInsCubeDataForFIN(HttpServletRequest req, UserMatrixMasterRequest filterRequest) throws SQLException {
		
		Connection connection = null;
		List<InsCubeResponseNew> kpiResponseList = new ArrayList<InsCubeResponseNew>();
		long startTime = System.currentTimeMillis();
		
		try {
			
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();
			
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];
			
			String queryStr =
					"SELECT "
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP) as INS_GWP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_OD) as INS_GWP_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_TP) as INS_GWP_TP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_DISCOUNT_OD) as INS_NWP_DISCOUNT_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_DEP) as INS_GWP_DEP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_DEP_OD) as INS_GWP_DEP_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_DEP_TP) as INS_GWP_DEP_TP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_NCB) as INS_GWP_NCB,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_NCB_OD) as INS_GWP_NCB_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_NCB_TP) as INS_GWP_NCB_TP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_OTHER_ADDON) as INS_GWP_OTHER_ADDON,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_OTHER_ADDON_OD) as INS_GWP_OTHER_ADDON_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_GWP_OTHER_ADDON_TP) as INS_GWP_OTHER_ADDON_TP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP) as INS_NWP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_OD) as INS_NWP_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_TP) as INS_NWP_TP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_DEP) as INS_NWP_DEP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_DEP_OD) as INS_NWP_DEP_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_DEP_TP) as INS_NWP_DEP_TP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_NCB) as INS_NWP_NCB,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_NCB_OD) as INS_NWP_NCB_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_NCB_TP) as INS_NWP_NCB_TP,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_OTHER_ADDON) as INS_NWP_OTHER_ADDON,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_OTHER_ADDON_OD) as INS_NWP_OTHER_ADDON_OD,"
					+ "SUM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.INS_NWP_OTHER_ADDON_TP) as INS_NWP_OTHER_ADDON_TP "
					+ "FROM RSDB.RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE as RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE "
					+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
					+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
					+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "
					+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "
					+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
					+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
					+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "
					+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "
					+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "
					+ "WHERE ( (financial_year='"+fromYear+"' and eff_fin_year_month in ('04','05','06','07','08','09','10','11','12')) or (financial_year='"+toYear+"' and eff_fin_year_month in ('01','02','03')))";
			
			if (filterRequest != null && filterRequest.getChannelNow() != null
					&& !filterRequest.getChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
					vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.CHANNEL) in ("+ vals +")";
			}
			
			if(filterRequest != null && filterRequest.getChannelNew() != null && !filterRequest.getChannelNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNew().size(); i++) {
					vals += "'" + filterRequest.getChannelNew().get(i).trim() + "'";
					if (i != filterRequest.getChannelNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.CHANNEL_NEW) in ("+ vals +")";
			}
			
			if (filterRequest != null && filterRequest.getSubChannelNow() != null
					&& !filterRequest.getSubChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
					vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getSubChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.SUB_CHANNEL) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.AGENT_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorFuelType() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and coalesce(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.FUELTYPE,'N') in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getNcbNow() != null
					&& !filterRequest.getNcbNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
					vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
					if (i != filterRequest.getNcbNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.NCB_FLAG) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorBranch() != null
					&& !filterRequest.getMotorBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
					vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
					if (i != filterRequest.getMotorBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.BRANCH_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.BUSINESS_TYPE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMakeNow() != null
					&& !filterRequest.getMakeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
					vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
					if (i != filterRequest.getMakeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.MAKE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getModelGroupNow() != null
					&& !filterRequest.getModelGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
					vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getModelGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.MODELGROUP) in (" + vals + ")";
			}
			
//			if (filterRequest != null && filterRequest.getPolicyTypes() != null
//					&& !filterRequest.getPolicyTypes().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//					vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//					if (i != filterRequest.getPolicyTypes().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.POLICY_TYPE) in (" + vals + ")";
//			}
			
			if(filterRequest != null && filterRequest.getPolicyTypeNew() != null && !filterRequest.getPolicyTypeNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getPolicyTypeNew().size(); i++) {
					vals += "'" + filterRequest.getPolicyTypeNew().get(i).trim() + "'";
					if (i != filterRequest.getPolicyTypeNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.POLICY_TYPE_NEW) in (" + vals + ")";
			}
			
			if(filterRequest != null && filterRequest.getCategorisation() != null && !filterRequest.getCategorisation().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getCategorisation().size(); i++) {
					vals += "'" + filterRequest.getCategorisation().get(i).trim() + "'";
					if (i != filterRequest.getCategorisation().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.CATEGORISATION) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getVehicleAge() != null
					&& !filterRequest.getVehicleAge().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getVehicleAge().size(); i++) {
					vals += "'" + filterRequest.getVehicleAge().get(i).trim() + "'";
					if (i != filterRequest.getVehicleAge().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_INS_COVERAGE_NEW_CURRENT_TABLE.VEHICLEAGE) in (" + vals + ")";
			}
			
			if(filterRequest != null && filterRequest.getEngineCapacity() != null && !filterRequest.getEngineCapacity().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getEngineCapacity().size(); i++) {
					vals += "'" + filterRequest.getEngineCapacity().get(i).trim() + "'";
					if (i != filterRequest.getEngineCapacity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_INS_COVERAGE_NEW_CURRENT_TABLE.ENGINECAPACITY) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getIntermediaryNames() != null
					&& !filterRequest.getIntermediaryNames().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getIntermediaryNames().size(); i++) {
					vals += "'" + filterRequest.getIntermediaryNames().get(i).trim() + "'";
					if (i != filterRequest.getIntermediaryNames().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCarType() != null
					&& !filterRequest.getMotorCarType().isEmpty()) {
				String vals = "'HIGHEND','High End'";
				String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
				int cvalcounter = 0,cvalNHEcounter = 0;
				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
					
					 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
						 if(cvalcounter==0)
						queryStr += "and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
						 cvalcounter++;
					 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
						if(cvalNHEcounter==0)
						queryStr += "and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
						cvalNHEcounter++;
					 }
				
					System.out.println("Hign End Query Inside InsCube ------------------------------ " + queryStr);
					
				}
				
			}
			
			if (filterRequest != null && filterRequest.getMotorZone() != null
					&& !filterRequest.getMotorZone().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
					vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
					if (i != filterRequest.getMotorZone().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCluster() != null
					&& !filterRequest.getMotorCluster().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
					vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
					if (i != filterRequest.getMotorCluster().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorState() != null
					&& !filterRequest.getMotorState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
					vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
					if (i != filterRequest.getMotorState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCity() != null
					&& !filterRequest.getMotorCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
					vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
					if (i != filterRequest.getMotorCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getStateGroupNow() != null
					&& !filterRequest.getStateGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
					vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getStateGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
			}
			
			System.out.println("queryStr------------------------------ GWP&NWP Inside FIN " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				InsCubeResponseNew res = new InsCubeResponseNew();
				
				res.setInsGwpPolicy(rs.getDouble(1));
				res.setInsGwpPolicyComprehensive(rs.getDouble(2));
				res.setInsGwpPolicyTp(rs.getDouble(3));
				
				res.setInsGwpDiscountPolicyComprehensive(rs.getDouble(4));
				res.setInsNwpDiscountPolicyComprehensive(rs.getDouble(5));
				
				res.setInsGwpDepPolicy(rs.getDouble(6));
				res.setInsGwpDepPolicyComprehensive(rs.getDouble(7));
				res.setInsGwpDepPolicyTp(rs.getDouble(8));
				
				res.setInsGwpNcbPolicy(rs.getDouble(9));
				res.setInsGwpNcbPolicyComprehensive(rs.getDouble(10));
				res.setInsGwpNcbPolicyTp(rs.getDouble(11));
				
				res.setInsGwpOtherAddonPolicy(rs.getDouble(12));
				res.setInsGwpOtherAddonPolicyComprehensive(rs.getDouble(13));
				res.setInsGwpOtherAddonPolicyTp(rs.getDouble(14));
				
				res.setInsNwpPolicy(rs.getDouble(15));
				res.setInsNwpPolicyComprehensive(rs.getDouble(16));
				res.setInsNwpPolicyTp(rs.getDouble(17));
				
				res.setInsNwpDepPolicy(rs.getDouble(18));
				res.setInsNwpDepPolicyComprehensive(rs.getDouble(19));
				res.setInsNwpDepPolicyTp(rs.getDouble(20));
				
				res.setInsNwpNcbPolicy(rs.getDouble(21));
				res.setInsNwpNcbPolicyComprehensive(rs.getDouble(22));
				res.setInsNwpNcbPolicyTp(rs.getDouble(23));
				
				res.setInsNwpOtherAddonPolicy(rs.getDouble(24));
				res.setInsNwpOtherAddonPolicyComprehensive(rs.getDouble(25));
				res.setInsNwpOtherAddonPolicyTp(rs.getDouble(26));
				
				kpiResponseList.add(res);
			}

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

		}catch(Exception e) {
			
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println(e.getMessage());
			
		}finally {
			connection.close();
		}

		return kpiResponseList;
	}
	
@GetMapping("/getGepNewCubeMonthAscolumnDataUW")
@ResponseBody
public List<GepCubeResponseFinal> getGepNewCubeMonthAscolumnDataUW(HttpServletRequest req,
		UserMatrixMasterRequest filterRequest) throws SQLException {
	// System.out.println("----------------Called getGepNewCubeMonthAscolumnDataUW
	// api -----------------");
	Connection connection = null;
	List<GepCubeResponseFinal> kpiResponseList = new ArrayList<GepCubeResponseFinal>();
	long startTime = System.currentTimeMillis();
	try {
		// String fromDate = req.getParameter("fromDate") == null ? "" :
		// req.getParameter("fromDate");
		// String toDate = req.getParameter("toDate") == null ? "" :
		// req.getParameter("toDate");
		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

		List<ProductMaster> productMasters = productMasterRepository.findAll();

		String motorProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		String healthProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password", "KYLIN");
		connection = driverManager
				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		Statement stmt = connection.createStatement();

		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];
		List<String> measureList = null;

		String queryStr = "SELECT SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEPCOVERAGE) as GEP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEPCOVERAGE) as NEP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_OD) as GEP_OD, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_TP) as GEP_TP  , "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_OD) as NEP_OD, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_TP) as NEP_TP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.DISCOUNT_GEP_OD) as DISCOUNT_GEP_OD, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.DISCOUNT_NEP_OD) as DISCOUNT_NEP_OD, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_DEP) as GEP_DEP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_DEP_OD) as GEP_DEP_OD  , "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_DEP_TP) as GEP_DEP_TP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_NCB) as GEP_NCB, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_NCB_OD) as GEP_NCB_OD, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_NCB_TP) as GEP_NCB_TP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_OTHER_ADDON) as GEP_OTHER_ADDON, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_OTHER_ADDON_OD) as GEP_OTHER_ADDON_OD  , "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_OTHER_ADDON_TP) as GEP_OTHER_ADDON_TP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_DEP) as NEP_DEP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_DEP_OD) as NEP_DEP_OD  , "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_DEP_TP) as NEP_DEP_TP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_NCB) as NEP_NCB, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_NCB_OD) as NEP_NCB_OD, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_NCB_TP) as NEP_NCB_TP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_OTHER_ADDON) as NEP_OTHER_ADDON, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_OTHER_ADDON_OD) as NEP_OTHER_ADDON_OD  , "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_OTHER_ADDON_TP) as NEP_OTHER_ADDON_TP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.EARNED_POLICIES) as EARNED_POLICIES, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.EARNED_POLICIES_OD) as EARNED_POLICIES_OD, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.EARNED_POLICIES_TP) as EARNED_POLICIES_TP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.ADDON_EARNED_POLICIES) as ADDON_EARNED_POLICIES  , "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.ADDON_EARNED_POLICIES_OD) as ADDON_EARNED_POLICIES_OD, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.ADDON_EARNED_POLICIES_TP) as ADDON_EARNED_POLICIES_TP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GIC_TPULR) as GIC_TPULR, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GIC_TPULR_DEP) as GIC_TPULR_DEP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GIC_TPULR_NCB) as GIC_TPULR_NCB, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GIC_TPULR_OTHER_ADDON) as GIC_TPULR_OTHER_ADDON  , "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NIC_TPULR) as NIC_TPULR, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NIC_TPULR_DEP) as NIC_TPULR_DEP, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NIC_TPULR_NCB) as NIC_TPULR_NCB, "
				+ "SUM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NIC_TPULR_OTHER_ADDON) as NIC_TPULR_OTHER_ADDON "
				+ "FROM RSDB.RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE as RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE "
				+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "
				+ "ON RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
				+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "
				+ "ON RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "
				+ "ON RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "
				+ "ON RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT "
				+ "ON RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME " // --------care
																																														// here---------
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";
	
		measureList = getgepBaseMeasures();
		System.out.println("AddOn: " + filterRequest.getAddOnNew());
		

		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
		queryStr += " WHERE (SUBSTRING(inception_date,1,10) >='" + finstartDate
				+ "' and SUBSTRING(inception_date,1,10) <='" + finEndDate + "')";

		/*
		 * if(claimType.equalsIgnoreCase("R")){ queryStr += " WHERE"; queryStr +=
		 * getFinGepCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),
		 * Integer.valueOf(fromYear),Integer.valueOf(toYear));
		 * 
		 * }else if(claimType.equalsIgnoreCase("U")){
		 * 
		 * queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"
		 * +finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' "; }
		 */

	
		/*
		 * if (filterRequest != null && filterRequest.getPolicyTypes() != null &&
		 * !filterRequest.getPolicyTypes().isEmpty()) { String vals = ""; for (int i =
		 * 0; i < filterRequest.getPolicyTypes().size(); i++) { vals += "'" +
		 * filterRequest.getPolicyTypes().get(i).trim() + "'"; if (i !=
		 * filterRequest.getPolicyTypes().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.POLICY_TYPE) in ("
		 * +vals+")"; }
		 */

		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.BUSINESS_TYPE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.CHANNEL) in (" + vals + ")";
		}


		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.SUB_CHANNEL) in 	(" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMakeNow() != null && !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.MAKE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.MODELGROUP) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and coalesce(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.FUELTYPE,'N') in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getNcbNow() != null && !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NCB_FLAG) in (" + vals + ")";
		}

		/*
		 * if (filterRequest != null && filterRequest.getMotorChannel() != null &&
		 * !filterRequest.getMotorChannel().isEmpty()) { String vals = ""; for (int i =
		 * 0; i < filterRequest.getMotorChannel().size(); i++) { vals += "'" +
		 * filterRequest.getMotorChannel().get(i).trim() + "'"; if (i !=
		 * filterRequest.getMotorChannel().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.CHANNEL) in (" + vals + ")";
		 * }
		 * 
		 * if (filterRequest != null && filterRequest.getMotorSubChannel() != null &&
		 * !filterRequest.getMotorSubChannel().isEmpty()) { String vals = ""; for (int i
		 * = 0; i < filterRequest.getMotorSubChannel().size(); i++) { vals += "'" +
		 * filterRequest.getMotorSubChannel().get(i).trim() + "'"; if (i !=
		 * filterRequest.getMotorSubChannel().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.SUB_CHANNEL) in (" + vals +
		 * ")"; }
		 */

		if (filterRequest != null && filterRequest.getMotorZone() != null
				&& !filterRequest.getMotorZone().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
				if (i != filterRequest.getMotorZone().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCluster() != null
				&& !filterRequest.getMotorCluster().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
				if (i != filterRequest.getMotorCluster().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorState() != null
				&& !filterRequest.getMotorState().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
				if (i != filterRequest.getMotorState().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCity() != null
				&& !filterRequest.getMotorCity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
				if (i != filterRequest.getMotorCity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorBranch() != null
				&& !filterRequest.getMotorBranch().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
				if (i != filterRequest.getMotorBranch().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.BRANCH_CODE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.AGENT_CODE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
		}

		/*
		 * if (filterRequest != null && filterRequest.getMotorFuelType() != null &&
		 * !filterRequest.getMotorFuelType().isEmpty()) { String vals = ""; for (int i =
		 * 0; i < filterRequest.getMotorFuelType().size(); i++) { vals += "'" +
		 * filterRequest.getMotorFuelType().get(i).trim() + "'"; if (i !=
		 * filterRequest.getMotorFuelType().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.FUEL_TYPE) in (" + vals +
		 * ")"; }
		 * 
		 * if (filterRequest != null && filterRequest.getMotorNcbFlag() != null &&
		 * !filterRequest.getMotorNcbFlag().isEmpty()) { String vals = ""; for (int i =
		 * 0; i < filterRequest.getMotorNcbFlag().size(); i++) { vals += "'" +
		 * filterRequest.getMotorNcbFlag().get(i).trim() + "'"; if (i !=
		 * filterRequest.getMotorNcbFlag().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NCB) in (" + vals + ")"; }
		 */
		
		//Have to uncomment----------
		/*
		 * if(filterRequest != null && filterRequest.getPolicyTypeNew() != null &&
		 * !filterRequest.getPolicyTypeNew().isEmpty()){
		 * 
		 * String vals = ""; for (int i = 0; i <
		 * filterRequest.getPolicyTypeNew().size(); i++) { vals += "'" +
		 * filterRequest.getPolicyTypeNew().get(i).trim() + "'"; if (i !=
		 * filterRequest.getPolicyTypeNew().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.POLICY_TYPE_NEW) in ("
		 * + vals + ")";
		 * 
		 * } if (filterRequest != null && filterRequest.getChannelNew() != null &&
		 * !filterRequest.getChannelNew().isEmpty()) { String vals = ""; for (int i = 0;
		 * i < filterRequest.getChannelNew().size(); i++) { vals += "'" +
		 * filterRequest.getChannelNew().get(i).trim() + "'"; if (i !=
		 * filterRequest.getChannelNew().size() - 1) vals += ","; } queryStr +=
		 * " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.CHANNEL_NEW) in (" +
		 * vals + ")"; }
		 * 
		 * if(filterRequest != null && filterRequest.getCategorisation() != null &&
		 * !filterRequest.getCategorisation().isEmpty()){
		 * 
		 * String vals = ""; for (int i = 0; i <
		 * filterRequest.getCategorisation().size(); i++) { vals += "'" +
		 * filterRequest.getCategorisation().get(i).trim() + "'"; if (i !=
		 * filterRequest.getCategorisation().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.CATEGORISATION) in ("
		 * + vals + ")";
		 * 
		 * }
		 * 
		 * if(filterRequest != null && filterRequest.getEngineCapacity() != null &&
		 * !filterRequest.getEngineCapacity().isEmpty()){
		 * 
		 * String vals = ""; for (int i = 0; i <
		 * filterRequest.getEngineCapacity().size(); i++) { vals += "'" +
		 * filterRequest.getEngineCapacity().get(i).trim() + "'"; if (i !=
		 * filterRequest.getEngineCapacity().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.ENGINECAPACITY) in ("
		 * + vals + ")";
		 * 
		 * } if (filterRequest != null && filterRequest.getVehicleAge() != null &&
		 * !filterRequest.getVehicleAge().isEmpty()) { String vals = ""; for (int i = 0;
		 * i < filterRequest.getVehicleAge().size(); i++) { vals += "'" +
		 * filterRequest.getVehicleAge().get(i).trim() + "'"; if (i !=
		 * filterRequest.getVehicleAge().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_UW_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.VEHICLEAGE) in (" +
		 * vals + ")"; }
		 */
		

		if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
			int cvalcounter = 0, cvalNHEcounter = 0;
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {

				if (filterRequest.getMotorCarType().get(i).trim().equals("HE")) {
					if (cvalcounter == 0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
					cvalcounter++;
				} else if (filterRequest.getMotorCarType().get(i).trim().equals("NHE")) {
					if (cvalNHEcounter == 0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals
								+ ")";
					cvalNHEcounter++;
				}

				System.out.println("HE query------------------------------ " + queryStr);

			}

		}

		System.out.println("queryStr------------------------------ " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");

		while (rs.next()) {

			GepCubeResponseFinal gepCubeResponse = new GepCubeResponseFinal();
			
			gepCubeResponse.setGep(rs.getDouble(1));
			gepCubeResponse.setNep(rs.getDouble(2));
			gepCubeResponse.setGepOd(rs.getDouble(3));
			gepCubeResponse.setGepTp(rs.getDouble(4));
			gepCubeResponse.setNepOd(rs.getDouble(5));
			gepCubeResponse.setNepTp(rs.getDouble(6));
			
			gepCubeResponse.setDiscountGepOd(rs.getDouble(7));
			gepCubeResponse.setDiscountGepTp(rs.getDouble(8));
			
			gepCubeResponse.setGepDep(rs.getDouble(9));
			gepCubeResponse.setGepDepOd(rs.getDouble(10));
			gepCubeResponse.setGepDepTp(rs.getDouble(11));
			
			gepCubeResponse.setGepNcb(rs.getDouble(12));
			gepCubeResponse.setGepNcbOd(rs.getDouble(13));
			gepCubeResponse.setGepNcbTp(rs.getDouble(14));
			
			gepCubeResponse.setGepOtherAddon(rs.getDouble(15));
			gepCubeResponse.setGepOtherAddonOd(rs.getDouble(16));
			gepCubeResponse.setGepOtherAddonTp(rs.getDouble(17));
			
			gepCubeResponse.setNepDep(rs.getDouble(18));
			gepCubeResponse.setNepDepOd(rs.getDouble(19));
			gepCubeResponse.setNepDepTp(rs.getDouble(20));
			
			gepCubeResponse.setNepNcb(rs.getDouble(21));
			gepCubeResponse.setNepNcbOd(rs.getDouble(22));
			gepCubeResponse.setNepNcbTp(rs.getDouble(23));
			
			gepCubeResponse.setNepOtherAddon(rs.getDouble(24));
			gepCubeResponse.setNepOtherAddonOd(rs.getDouble(25));
			gepCubeResponse.setNepOtherAddonTp(rs.getDouble(26));
			
			gepCubeResponse.setEarnedPolicies(rs.getDouble(27));
			gepCubeResponse.setEarnedPoliciesOd(rs.getDouble(28));
			gepCubeResponse.setEarnedPoliciesTp(rs.getDouble(29));
			
			gepCubeResponse.setAddonEarnedPolicies(rs.getDouble(30));
			gepCubeResponse.setAddonEarnedPoliciesOd(rs.getDouble(31));
			gepCubeResponse.setAddonEarnedPoliciesTp(rs.getDouble(32));
			
			gepCubeResponse.setGicTpulr(rs.getDouble(33));
			gepCubeResponse.setGicTpulrDep(rs.getDouble(34));
			gepCubeResponse.setGicTpulrNcb(rs.getDouble(35));
			gepCubeResponse.setGicTpulrOtherAddon(rs.getDouble(36));
			
			gepCubeResponse.setNicTpulr(rs.getDouble(37));
			gepCubeResponse.setNicTpulrDep(rs.getDouble(38));
			gepCubeResponse.setNicTpulrNcb(rs.getDouble(39));
			gepCubeResponse.setNicTpulrOtherAddon(rs.getDouble(40));
			
			/*
			 * for(int i=1; i<=40; i++) {
			 * System.out.println("rs.getDouble("+i+") = "+rs.getDouble(i)); }
			 */

			kpiResponseList.add(gepCubeResponse);

		}

		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	} finally {
		connection.close();
	}
	return kpiResponseList;
}

@GetMapping("/getGepNewCubeMonthAscolumnDataFIN")
@ResponseBody
public List<GepCubeResponseFinal> getGepCubeNewMonthAscolumnDataFIN(HttpServletRequest req,
		UserMatrixMasterRequest filterRequest) throws SQLException {
	System.out.print("filterRequest = " + filterRequest);

	Connection connection = null;
	List<GepCubeResponseFinal> kpiResponseList = new ArrayList<GepCubeResponseFinal>();
	long startTime = System.currentTimeMillis();
	try {
		// String fromDate = req.getParameter("fromDate") == null ? "" :
		// req.getParameter("fromDate");
		// String toDate = req.getParameter("toDate") == null ? "" :
		// req.getParameter("toDate");
		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

		List<ProductMaster> productMasters = productMasterRepository.findAll();

		String motorProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		String healthProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password", "KYLIN");
		connection = driverManager
				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		Statement stmt = connection.createStatement();

		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];
		List<String> measureList = null;

		String queryStr = "SELECT SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEPCOVERAGE) as GEP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEPCOVERAGE) as NEP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_OD) as GEP_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_TP) as GEP_TP  , "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_OD) as NEP_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_TP) as NEP_TP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.DISCOUNT_GEP_OD) as DISCOUNT_GEP_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.DISCOUNT_NEP_OD) as DISCOUNT_NEP_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_DEP) as GEP_DEP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_DEP_OD) as GEP_DEP_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_DEP_TP) as GEP_DEP_TP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_NCB) as GEP_NCB, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_NCB_OD) as GEP_NCB_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_NCB_TP) as GEP_NCB_TP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_OTHER_ADDON) as GEP_OTHER_ADDON, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_OTHER_ADDON_OD) as GEP_OTHER_ADDON_OD  , "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GEP_OTHER_ADDON_TP) as GEP_OTHER_ADDON_TP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_DEP) as NEP_DEP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_DEP_OD) as NEP_DEP_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_DEP_TP) as NEP_DEP_TP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_NCB) as NEP_NCB, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_NCB_OD) as NEP_NCB_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_NCB_TP) as NEP_NCB_TP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_OTHER_ADDON) as NEP_OTHER_ADDON, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_OTHER_ADDON_OD) as NEP_OTHER_ADDON_OD  , "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NEP_OTHER_ADDON_TP) as NEP_OTHER_ADDON_TP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.EARNED_POLICIES) as EARNED_POLICIES, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.EARNED_POLICIES_OD) as EARNED_POLICIES_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.EARNED_POLICIES_TP) as EARNED_POLICIES_TP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.ADDON_EARNED_POLICIES) as ADDON_EARNED_POLICIES  , "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.ADDON_EARNED_POLICIES_OD) as ADDON_EARNED_POLICIES_OD, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.ADDON_EARNED_POLICIES_TP) as ADDON_EARNED_POLICIES_TP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GIC_TPULR) as GIC_TPULR, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GIC_TPULR_DEP) as GIC_TPULR_DEP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GIC_TPULR_NCB) as GIC_TPULR_NCB, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.GIC_TPULR_OTHER_ADDON) as GIC_TPULR_OTHER_ADDON  , "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NIC_TPULR) as NIC_TPULR, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NIC_TPULR_DEP) as NIC_TPULR_DEP, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NIC_TPULR_NCB) as NIC_TPULR_NCB, "
				+ "SUM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NIC_TPULR_OTHER_ADDON) as NIC_TPULR_OTHER_ADDON "
				+ "FROM RSDB.RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE as RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE "
				+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "
				+ "ON RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
				+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "
				+ "ON RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "
				+ "ON RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "
				+ "ON RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT "
				+ "ON RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE ";
		// if(claimType.equalsIgnoreCase("R")){
		measureList = getgepBaseMeasures();
		System.out.println("AddOn: " + filterRequest.getAddOnNew());

		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";

		queryStr += "WHERE ((gep_year='" + fromYear
				+ "' and gep_MONTH in ('04','05','06','07','08','09','10','11','12')) or (gep_year='" + toYear
				+ "' and gep_MONTH in ('01','02','03')))";

		/*
		 * if (filterRequest != null && filterRequest.getPolicyTypes() != null &&
		 * !filterRequest.getPolicyTypes().isEmpty()) { String vals = ""; for (int i =
		 * 0; i < filterRequest.getPolicyTypes().size(); i++) { vals += "'" +
		 * filterRequest.getPolicyTypes().get(i).trim() + "'"; if (i !=
		 * filterRequest.getPolicyTypes().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.POLICY_TYPE) in (" +
		 * vals + ")"; }
		 */

		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.BUSINESS_TYPE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.CHANNEL_NEW) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMakeNow() != null && !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.MAKE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.MODELGROUP) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and coalesce(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.FUELTYPE,'N') in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getNcbNow() != null && !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.NCB_FLAG) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorZone() != null
				&& !filterRequest.getMotorZone().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
				if (i != filterRequest.getMotorZone().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCluster() != null
				&& !filterRequest.getMotorCluster().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
				if (i != filterRequest.getMotorCluster().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorState() != null
				&& !filterRequest.getMotorState().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
				if (i != filterRequest.getMotorState().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCity() != null
				&& !filterRequest.getMotorCity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
				if (i != filterRequest.getMotorCity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorBranch() != null
				&& !filterRequest.getMotorBranch().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
				if (i != filterRequest.getMotorBranch().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.BRANCH_CODE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.AGENT_CODE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
		}

		/*
		 * if (filterRequest != null && filterRequest.getMotorFuelType() != null &&
		 * !filterRequest.getMotorFuelType().isEmpty()) { String vals = ""; for (int i =
		 * 0; i < filterRequest.getMotorFuelType().size(); i++) { vals += "'" +
		 * filterRequest.getMotorFuelType().get(i).trim() + "'"; if (i !=
		 * filterRequest.getMotorFuelType().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.FUEL_TYPE) in (" + vals +
		 * ")"; }
		 * 
		 * if (filterRequest != null && filterRequest.getMotorNcbFlag() != null &&
		 * !filterRequest.getMotorNcbFlag().isEmpty()) { String vals = ""; for (int i =
		 * 0; i < filterRequest.getMotorNcbFlag().size(); i++) { vals += "'" +
		 * filterRequest.getMotorNcbFlag().get(i).trim() + "'"; if (i !=
		 * filterRequest.getMotorNcbFlag().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NCB) in (" + vals + ")"; }
		 */

		// Have to uncomment---------
		/*
		 * if(filterRequest != null && filterRequest.getPolicyTypeNew() != null &&
		 * !filterRequest.getPolicyTypeNew().isEmpty()){
		 * 
		 * String vals = ""; for (int i = 0; i <
		 * filterRequest.getPolicyTypeNew().size(); i++) { vals += "'" +
		 * filterRequest.getPolicyTypeNew().get(i).trim() + "'"; if (i !=
		 * filterRequest.getPolicyTypeNew().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.POLICY_TYPE_NEW) in (" +
		 * vals + ")";
		 * 
		 * } if (filterRequest != null && filterRequest.getChannelNew() != null &&
		 * !filterRequest.getChannelNew().isEmpty()) { String vals = ""; for (int i = 0;
		 * i < filterRequest.getChannelNew().size(); i++) { vals += "'" +
		 * filterRequest.getChannelNew().get(i).trim() + "'"; if (i !=
		 * filterRequest.getChannelNew().size() - 1) vals += ","; } queryStr +=
		 * " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.CHANNEL_NEW) in (" +
		 * vals + ")"; }
		 * 
		 * if(filterRequest != null && filterRequest.getCategorisation() != null &&
		 * !filterRequest.getCategorisation().isEmpty()){
		 * 
		 * String vals = ""; for (int i = 0; i <
		 * filterRequest.getCategorisation().size(); i++) { vals += "'" +
		 * filterRequest.getCategorisation().get(i).trim() + "'"; if (i !=
		 * filterRequest.getCategorisation().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.CATEGORISATION) in (" +
		 * vals + ")";
		 * 
		 * }
		 * 
		 * if(filterRequest != null && filterRequest.getEngineCapacity() != null &&
		 * !filterRequest.getEngineCapacity().isEmpty()){
		 * 
		 * String vals = ""; for (int i = 0; i <
		 * filterRequest.getEngineCapacity().size(); i++) { vals += "'" +
		 * filterRequest.getEngineCapacity().get(i).trim() + "'"; if (i !=
		 * filterRequest.getEngineCapacity().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.ENGINECAPACITY) in (" +
		 * vals + ")";
		 * 
		 * } if (filterRequest != null && filterRequest.getVehicleAge() != null &&
		 * !filterRequest.getVehicleAge().isEmpty()) { String vals = ""; for (int i = 0;
		 * i < filterRequest.getVehicleAge().size(); i++) { vals += "'" +
		 * filterRequest.getVehicleAge().get(i).trim() + "'"; if (i !=
		 * filterRequest.getVehicleAge().size() - 1) { vals += ","; } } queryStr +=
		 * " and TRIM(RSA_KPI_GEP_POLICY_MONTH_LEVEL_FACT_TABLE.VEHICLEAGE) in (" + vals
		 * + ")"; }
		 */

		if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
			int cvalcounter = 0, cvalNHEcounter = 0;
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {

				if (filterRequest.getMotorCarType().get(i).trim().equals("HE")) {
					if (cvalcounter == 0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
					cvalcounter++;
				} else if (filterRequest.getMotorCarType().get(i).trim().equals("NHE")) {
					if (cvalNHEcounter == 0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals
								+ ")";
					cvalNHEcounter++;
				}

				// System.out.println("HE query------------------------------ " + queryStr);

			}

		}

		System.out.println("queryStr------------------------------ " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");

		while (rs.next()) {

			GepCubeResponseFinal gepCubeResponse = new GepCubeResponseFinal();
			// System.out.println(rs.getDouble(1));
			gepCubeResponse.setGep(rs.getDouble(1));
			gepCubeResponse.setNep(rs.getDouble(2));
			gepCubeResponse.setGepOd(rs.getDouble(3));
			gepCubeResponse.setGepTp(rs.getDouble(4));
			gepCubeResponse.setNepOd(rs.getDouble(5));
			gepCubeResponse.setNepTp(rs.getDouble(6));

			gepCubeResponse.setDiscountGepOd(rs.getDouble(7));
			gepCubeResponse.setDiscountGepTp(rs.getDouble(8));

			gepCubeResponse.setGepDep(rs.getDouble(9));
			gepCubeResponse.setGepDepOd(rs.getDouble(10));
			gepCubeResponse.setGepDepTp(rs.getDouble(11));

			gepCubeResponse.setGepNcb(rs.getDouble(12));
			gepCubeResponse.setGepNcbOd(rs.getDouble(13));
			gepCubeResponse.setGepNcbTp(rs.getDouble(14));

			gepCubeResponse.setGepOtherAddon(rs.getDouble(15));
			gepCubeResponse.setGepOtherAddonOd(rs.getDouble(16));
			gepCubeResponse.setGepOtherAddonTp(rs.getDouble(17));

			gepCubeResponse.setNepDep(rs.getDouble(18));
			gepCubeResponse.setNepDepOd(rs.getDouble(19));
			gepCubeResponse.setNepDepTp(rs.getDouble(20));

			gepCubeResponse.setNepNcb(rs.getDouble(21));
			gepCubeResponse.setNepNcbOd(rs.getDouble(22));
			gepCubeResponse.setNepNcbTp(rs.getDouble(23));

			gepCubeResponse.setNepOtherAddon(rs.getDouble(24));
			gepCubeResponse.setNepOtherAddonOd(rs.getDouble(25));
			gepCubeResponse.setNepOtherAddonTp(rs.getDouble(26));

			gepCubeResponse.setEarnedPolicies(rs.getDouble(27));
			gepCubeResponse.setEarnedPoliciesOd(rs.getDouble(28));
			gepCubeResponse.setEarnedPoliciesTp(rs.getDouble(29));

			gepCubeResponse.setAddonEarnedPolicies(rs.getDouble(30));
			gepCubeResponse.setAddonEarnedPoliciesOd(rs.getDouble(31));
			gepCubeResponse.setAddonEarnedPoliciesTp(rs.getDouble(32));

			gepCubeResponse.setGicTpulr(rs.getDouble(33));
			gepCubeResponse.setGicTpulrDep(rs.getDouble(34));
			gepCubeResponse.setGicTpulrNcb(rs.getDouble(35));
			gepCubeResponse.setGicTpulrOtherAddon(rs.getDouble(36));

			gepCubeResponse.setNicTpulr(rs.getDouble(37));
			gepCubeResponse.setNicTpulrDep(rs.getDouble(38));
			gepCubeResponse.setNicTpulrNcb(rs.getDouble(39));
			gepCubeResponse.setNicTpulrOtherAddon(rs.getDouble(40));

			for (int i = 1; i <= 40; i++)
				System.out.println("rs.getDouble(" + i + ") = " + rs.getDouble(i));

			kpiResponseList.add(gepCubeResponse);

		}

		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	} finally {
		connection.close();
	}
	return kpiResponseList;
}
	
@GetMapping("/getReserveSingleLineNewCubeGicDataForUW")
@ResponseBody
public List<ReserverSingleLineCubeResponseNew> getReserveSingleLineCubeGicDataNewForUR(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
			throws SQLException {
	     
	  
	    Connection connection = null;
	    List<ReserverSingleLineCubeResponseNew> kpiResponseList = new ArrayList<ReserverSingleLineCubeResponseNew>();
	    long startTime = System.currentTimeMillis();
	    
	    try {
	    	String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
	    	String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
	    	
	    	List<ProductMaster> productMasters = productMasterRepository.findAll();
	    	
	    	String motorProductVals = "'" + productMasters.stream()
			            .filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
			            .collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";
	    	
	    	String healthProductVals = "'" + productMasters.stream()
			            .filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
			            .collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

	    	Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();
			
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];
			String queryStr = "";
         
			
			queryStr = "SELECT "
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_GIC) as RSL_GIC,  "
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_CAT_GIC) as RSL_CAT_GIC,"
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_THEFT_GIC) as RSL_THEFT_GIC, "
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_OTHER_GIC) as RSL_OTHER_GIC,"
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_TP_GIC) as RSL_TP_GIC, "
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_NIC) as RSL_NIC, "
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_CAT_NIC) as RSL_CAT_NIC, "
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_THEFT_NIC) as RSL_THEFT_NIC, "
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_OTHER_NIC) as RSL_OTHER_NIC, "
						+ "SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_TP_NIC) as RSL_TP_NIC "
						+ "FROM RSDB.RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL as RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL "
						+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
						+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
						+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  "
						+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "
						+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "
						+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
						+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  "
						+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "
						+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE " ;
						 // + "LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE" ;
				
			
			
			
			String finstartDate = fromYear + "-" + fromMonth + "-01";
			String finEndDate = toYear + "-" + toMonth + "-31";
			queryStr += " WHERE (SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' )";
			
			

			
			//1 channel 
			
			if (filterRequest != null && filterRequest.getChannelNow() != null
					&& !filterRequest.getChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
					vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CHANNEL) in (" + vals + ")";
			}
			
			
		//2 channel New
			                                                                                                                  
		if(filterRequest != null && filterRequest.getChannelNew() != null && !filterRequest.getChannelNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNew().size(); i++) {
					vals += "'" + filterRequest.getChannelNew().get(i).trim() + "'";
					if (i != filterRequest.getChannelNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CHANNEL_NEW) in  (" + vals + ")";

			}
		
			
			
			// 3 subchannel

			if (filterRequest != null && filterRequest.getSubChannelNow() != null
					&& !filterRequest.getSubChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
					vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getSubChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.SUB_CHANNEL) in (" + vals + ")";
			}
			
			// 4 agentcode
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE) in (" + vals + ")";
			}
			
			// 5 fueltype N 
			if (filterRequest != null && filterRequest.getFuelTypeNow() != null
					&& !filterRequest.getFuelTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
					vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getFuelTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and coalesce(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.FUELTYPE,'N') in (" + vals + ")";
			}
			
			
			// 6 ncb flag
			if (filterRequest != null && filterRequest.getNcbNow() != null
					&& !filterRequest.getNcbNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
					vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
					if (i != filterRequest.getNcbNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.NCB_FLAG) in (" + vals + ")";
			}
			
			
			// 7 branch code
			
			if (filterRequest != null && filterRequest.getMotorBranch() != null
					&& !filterRequest.getMotorBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
					vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
					if (i != filterRequest.getMotorBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE) in (" + vals + ")";
			}
			
			
			// 8 bussiness type
			
			
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.BUSINESS_TYPE) in (" + vals + ")";
			}
			
			// 9 make 
			
			if (filterRequest != null && filterRequest.getMakeNow() != null
					&& !filterRequest.getMakeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
					vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
					if (i != filterRequest.getMakeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.MAKE) in (" + vals + ")";
			}
			
			
			// 10 model group
			
			if (filterRequest != null && filterRequest.getModelGroupNow() != null
					&& !filterRequest.getModelGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
					vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getModelGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.MODELGROUP) in (" + vals + ")";
			}
			
			
			// 11 policy types
			                                                                                                            
//			if (filterRequest != null && filterRequest.getPolicyTypes() != null
//					&& !filterRequest.getPolicyTypes().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//					vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//					if (i != filterRequest.getPolicyTypes().size() - 1) {
//						vals += ",";
//					}
//				}
//				
//				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.POLICY_TYPE) in(" + vals + ")";
//			}
//			
			
		
			// 12  policytype new                                                                                
			
			if(filterRequest != null && filterRequest.getPolicyTypeNew() != null && !filterRequest.getPolicyTypeNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getPolicyTypeNew().size(); i++) {
					vals += "'" + filterRequest.getPolicyTypeNew().get(i).trim() + "'";
					if (i != filterRequest.getPolicyTypeNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.POLICY_TYPE_NEW) in(" + vals + ")";

			}
			
			
			
			
		  //  13 categerisation                                                                        
			
			
			if(filterRequest != null && filterRequest.getCategorisation() != null && !filterRequest.getCategorisation().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getCategorisation().size(); i++) {
					vals += "'" + filterRequest.getCategorisation().get(i).trim() + "'";
					if (i != filterRequest.getCategorisation().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CATEGORISATION) in  (" + vals + ")";

			}
			
			
			
		//	14 vechicle age                                                                              
			
			if (filterRequest != null && filterRequest.getVehicleAge() != null
					&& !filterRequest.getVehicleAge().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getVehicleAge().size(); i++) {
					vals += "'" + filterRequest.getVehicleAge().get(i).trim() + "'";
					if (i != filterRequest.getVehicleAge().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.VEHICLEAGE) in  (" + vals + ")";
			}
			
			
			
   //    15 engine capacity                                                                   
			
			if(filterRequest != null && filterRequest.getEngineCapacity() != null && !filterRequest.getEngineCapacity().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getEngineCapacity().size(); i++) {
					vals += "'" + filterRequest.getEngineCapacity().get(i).trim() + "'";
					if (i != filterRequest.getEngineCapacity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.ENGINECAPACITY) in(" + vals + ")";

			}
			
			
			
			
		   //16  intermediatery name
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
					&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
			}
			
			
			//17 model classification
			
			if (filterRequest != null && filterRequest.getMotorCarType() != null
					&& !filterRequest.getMotorCarType().isEmpty()) {
				String vals = "'HIGHEND','High End'";
				String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
				int cvalcounter = 0,cvalNHEcounter = 0;
				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
					
					 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
						 if(cvalcounter==0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
						              
						 cvalcounter++;
					 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
						if(cvalNHEcounter==0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
						cvalNHEcounter++;
					 }
				
					System.out.println("HE query------------------------------ " + queryStr);
					
				}
				
			}
			
			
			
			
			//18  zone
			
			if (filterRequest != null && filterRequest.getMotorZone() != null
					&& !filterRequest.getMotorZone().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
					vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
					if (i != filterRequest.getMotorZone().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
			}
			
			
			//19 cluster name
			
			if (filterRequest != null && filterRequest.getMotorCluster() != null
					&& !filterRequest.getMotorCluster().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
					vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
					if (i != filterRequest.getMotorCluster().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
			}
			
			//20 state new 
			
			if (filterRequest != null && filterRequest.getMotorState() != null
					&& !filterRequest.getMotorState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
					vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
					if (i != filterRequest.getMotorState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}
			
			//21 description
			
			if (filterRequest != null && filterRequest.getMotorCity() != null
					&& !filterRequest.getMotorCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
					vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
					if (i != filterRequest.getMotorCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}
			
			//22 grouping 
			
			if (filterRequest != null && filterRequest.getStateGroupNow() != null
					&& !filterRequest.getStateGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
					vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getStateGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
			}
			
			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");
			//System.out.println("Query -------------> " +queryStr);
			System.out.println(rs);
			
			
				
	 while (rs.next()) {

				ReserverSingleLineCubeResponseNew res = new ReserverSingleLineCubeResponseNew();
				
				res.setRslGic(rs.getDouble(1));
				res.setRslCatGic(rs.getDouble(2));
				res.setRslTheftGic(rs.getDouble(3));
				res.setRslOtherGic(rs.getDouble(4));
				res.setRslTpGic(rs.getDouble(5));
				
				
				res.setRslNic(rs.getDouble(6));
				res.setRslCatNic(rs.getDouble(7));
				res.setRslTheftNic(rs.getDouble(8));
				res.setRslOtherNic(rs.getDouble(9));
				res.setRslTpNic(rs.getDouble(10));
				


		kpiResponseList.add(res);
	}
		
			
		}catch (Exception e) {
			System.out.println("Error occure when UR query running");
			System.out.println("kylinDataSource initialize error, ex: " + e);
		
			
		}finally {
			connection.close();
		}
	   
				return kpiResponseList;
	   
}

@GetMapping("/getReserveSingleLineNewCubeGicDataForFIN")
@ResponseBody
public List<ReserverSingleLineCubeResponseNew> getReserveSingleLineNewCubeGicDataForFIN(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
			throws SQLException {
	  
	    Connection connection = null;
	    List<ReserverSingleLineCubeResponseNew> kpiResponseList = new ArrayList<ReserverSingleLineCubeResponseNew>();
	    long startTime = System.currentTimeMillis();
	    
	    try {
	    	
	    	String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
	    	String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
	    	
	    	List<ProductMaster> productMasters = productMasterRepository.findAll();
	    	
	    	String motorProductVals = "'" + productMasters.stream()
			            .filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
			            .collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";
	    	
	    	String healthProductVals = "'" + productMasters.stream()
			            .filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
			            .collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

	    	Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();
			
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];
			String queryStr = "";
			
			
				queryStr = "SELECT "
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_GIC) as RSL_GIC, "
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_CAT_GIC) as RSL_CAT_GIC, "
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_THEFT_GIC) as RSL_THEFT_GIC,  "
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_OTHER_GIC) as RSL_OTHER_GIC,"
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_TP_GIC) as RSL_TP_GIC,  "
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_NIC) as RSL_NIC, "
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_CAT_NIC) as RSL_CAT_NIC, "
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_THEFT_NIC) as RSL_THEFT_NIC, "
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_OTHER_NIC) as RSL_OTHER_NIC, "
						+ "SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_TP_NIC) as RSL_TP_NIC "
						+ "FROM RSDB.RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL as RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL "
						+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
						+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
						+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "
						+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "
						+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  "
						+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
						+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
						+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  "
						+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  "
						+ "LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE ";
			
				
			
			queryStr += "WHERE ( CSL_MVMT_MONTH between " + fromYear +fromMonth+ " and " + toYear +toMonth+ " )";
			
       // 1 channel 
			
			if (filterRequest != null && filterRequest.getChannelNow() != null
					&& !filterRequest.getChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
					vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CHANNEL) in (" + vals + ")";
				
			}
			
//         2 channel new 
			 
			 if(filterRequest != null && filterRequest.getChannelNew() != null && !filterRequest.getChannelNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNew().size(); i++) {
					vals += "'" + filterRequest.getChannelNew().get(i).trim() + "'";
					if (i != filterRequest.getChannelNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CHANNEL_NEW) in  (" + vals + ")";

			}
			
			
			
		    // 3 subchannel

			if (filterRequest != null && filterRequest.getSubChannelNow() != null
					&& !filterRequest.getSubChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
					vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getSubChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.SUB_CHANNEL) in (" + vals + ")";
			}
			
			// 4 agentcode
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE) in (" + vals + ")";
			}
			
			
			//5 fueltype N 
			if (filterRequest != null && filterRequest.getFuelTypeNow() != null
					&& !filterRequest.getFuelTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
					vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getFuelTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and coalesce(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.FUELTYPE,'N') in (" + vals + ")";
			}
			
			
			//6 ncb flag
			if (filterRequest != null && filterRequest.getNcbNow() != null
					&& !filterRequest.getNcbNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
					vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
					if (i != filterRequest.getNcbNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.NCB_FLAG) in (" + vals + ")";
			}
			
			
			//7 branch code
			
			if (filterRequest != null && filterRequest.getMotorBranch() != null
					&& !filterRequest.getMotorBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
					vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
					if (i != filterRequest.getMotorBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE) in (" + vals + ")";
			}
			
			
			// 8 bussiness type
			
			
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BUSINESS_TYPE) in (" + vals + ")";
			}
			
			
			//9 make 
			
			if (filterRequest != null && filterRequest.getMakeNow() != null
					&& !filterRequest.getMakeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
					vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
					if (i != filterRequest.getMakeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MAKE) in (" + vals + ")";
			}
			
			
			//10 model group
			
			if (filterRequest != null && filterRequest.getModelGroupNow() != null
					&& !filterRequest.getModelGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
					vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getModelGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MODELGROUP) in (" + vals + ")";
			}
			
			//11 policy type                                                         // changes
			
			
//			if (filterRequest != null && filterRequest.getPolicyTypes() != null
//					&& !filterRequest.getPolicyTypes().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//					vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//					if (i != filterRequest.getPolicyTypes().size() - 1) {
//						vals += ",";
//					}
//				}
//				
//				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.POLICY_TYPE) in (" + vals + ")";
//			}
			
			
			
		    //12  policytype new                                                               
			
			if(filterRequest != null && filterRequest.getPolicyTypeNew() != null && !filterRequest.getPolicyTypeNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getPolicyTypeNew().size(); i++) {
					vals += "'" + filterRequest.getPolicyTypeNew().get(i).trim() + "'";
					if (i != filterRequest.getPolicyTypeNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.POLICY_TYPE_NEW) in (" + vals + ")";

			}
			
			
			
			
			
       // 13 categerisation                                                                                     
			
			
			if(filterRequest != null && filterRequest.getCategorisation() != null && !filterRequest.getCategorisation().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getCategorisation().size(); i++) {
					vals += "'" + filterRequest.getCategorisation().get(i).trim() + "'";
					if (i != filterRequest.getCategorisation().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CATEGORISATION) in(" + vals + ")";

			}
			
			
	      //14 vechicle age                                                                                         
			
			
			if (filterRequest != null && filterRequest.getVehicleAge() != null
					&& !filterRequest.getVehicleAge().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getVehicleAge().size(); i++) {
					vals += "'" + filterRequest.getVehicleAge().get(i).trim() + "'";
					if (i != filterRequest.getVehicleAge().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.VEHICLEAGE) in (" + vals + ")";
			}
			
			
			
			
			// 15 engine capacity                                                                           
			
			if(filterRequest != null && filterRequest.getEngineCapacity() != null && !filterRequest.getEngineCapacity().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getEngineCapacity().size(); i++) {
					vals += "'" + filterRequest.getEngineCapacity().get(i).trim() + "'";
					if (i != filterRequest.getEngineCapacity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.ENGINECAPACITY) in (" + vals + ")";

			}
			
			
			
			
			//16  intermediatery name
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
					&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
			}
			
			
			//17 model classification
			
			if (filterRequest != null && filterRequest.getMotorCarType() != null
					&& !filterRequest.getMotorCarType().isEmpty()) {
				String vals = "'HIGHEND','High End'";
				String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
				int cvalcounter = 0,cvalNHEcounter = 0;
				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
					
					 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
						 if(cvalcounter==0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
						              
						 cvalcounter++;
					 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
						if(cvalNHEcounter==0)
						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
						cvalNHEcounter++;
					 }
				
					System.out.println("HE query------------------------------ " + queryStr);
					
				}
				
			}
			
			
			
			//18  zone
			
			 if (filterRequest != null && filterRequest.getMotorZone() != null
					&& !filterRequest.getMotorZone().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
					vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
					if (i != filterRequest.getMotorZone().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
			}
			
			
			// 19 cluster name
			
			if (filterRequest != null && filterRequest.getMotorCluster() != null
					&& !filterRequest.getMotorCluster().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
					vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
					if (i != filterRequest.getMotorCluster().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
			}
			
			
			//20 state new 
			
			if (filterRequest != null && filterRequest.getMotorState() != null
					&& !filterRequest.getMotorState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
					vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
					if (i != filterRequest.getMotorState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}
			
			//21 description
			
			if (filterRequest != null && filterRequest.getMotorCity() != null
					&& !filterRequest.getMotorCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
					vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
					if (i != filterRequest.getMotorCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}
			
			
			//22 grouping 
			
			if (filterRequest != null && filterRequest.getStateGroupNow() != null
					&& !filterRequest.getStateGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
					vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getStateGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
			}
			
			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");
			System.out.println(rs);
			
			
   while (rs.next()) {

 	  ReserverSingleLineCubeResponseNew res = new ReserverSingleLineCubeResponseNew();

 	    res.setRslGic(rs.getDouble(1));
			res.setRslCatGic(rs.getDouble(2));
			res.setRslTheftGic(rs.getDouble(3));
			res.setRslOtherGic(rs.getDouble(4));
			res.setRslTpGic(rs.getDouble(5));
			
			
			res.setRslNic(rs.getDouble(6));
			res.setRslCatNic(rs.getDouble(7));
			res.setRslTheftNic(rs.getDouble(8));
			res.setRslOtherNic(rs.getDouble(9));
			res.setRslTpNic(rs.getDouble(10));
			
//				if(claimParamType.equals("GIC")){
//				res.setCatGicOdOthersOtherAddon(rs.getDouble(11));
//				res.setCatGicOdOthersNoAddon(rs.getDouble(12));
//				res.setTheftGicOdComprehensiveDep(rs.getDouble(13));
//				res.setTheftGicOdComprehensiveNcb(rs.getDouble(14));
//				res.setTheftGicOdComprehensiveOtherAddon(rs.getDouble(15));
//				res.setTheftGicOdComprehensiveNoAddon(rs.getDouble(16));
//				res.setTheftGicOdTpDep(rs.getDouble(17));
//				res.setTheftGicOdTpNcb(rs.getDouble(18));
//				res.setTheftGicOdTpOtherAddon(rs.getDouble(19));
//				res.setTheftGicOdTpNoAddon(rs.getDouble(20));
//				res.setTheftGicOdOthersDep(rs.getDouble(21));
//				res.setTheftGicOdOthersNcb(rs.getDouble(22));
//				res.setTheftGicOdOthersOtherAddon(rs.getDouble(23));
//				res.setTheftGicOdOthersNoAddon(rs.getDouble(24));
//				res.setOthersGicOdComprehensiveDep(rs.getDouble(25));
//				res.setOthersGicOdComprehensiveNcb(rs.getDouble(26));
//				res.setOthersGicOdComprehensiveOtherAddon(rs.getDouble(27));
//				res.setOthersGicOdComprehensiveNoAddon(rs.getDouble(28));
//				res.setOthersGicOdTpDep(rs.getDouble(29));
//				res.setOthersGicOdTpNcb(rs.getDouble(30));
//				res.setOthersGicOdTpOtherAddon(rs.getDouble(31));
//				res.setOthersGicOdTpNoAddon(rs.getDouble(32));
//				res.setOthersGicOdOthersDep(rs.getDouble(33));
//				res.setOthersGicOdOthersNcb(rs.getDouble(34));
//				res.setOthersGicOdOthersOtherAddon(rs.getDouble(35));
//				res.setOthersGicOdOthersNoAddon(rs.getDouble(36));
//				res.setCatGicTpComprehensiveDep(rs.getDouble(37));
//				res.setCatGicTpComprehensiveNcb(rs.getDouble(38));
//				res.setCatGicTpComprehensiveOtherAddon(rs.getDouble(39));
//				res.setCatGicTpComprehensiveNoAddon(rs.getDouble(40));
//				res.setCatGicTpTpDep(rs.getDouble(41));
//				res.setCatGicTpTpNcb(rs.getDouble(42));
//				res.setCatGicTpTpOtherAddon(rs.getDouble(43));
//				res.setCatGicTpTpNoAddon(rs.getDouble(44));
//				res.setCatGicTpOthersDep(rs.getDouble(45));
//				res.setCatGicTpOthersNcb(rs.getDouble(46));
//				res.setCatGicTpOthersOtherAddon(rs.getDouble(47));
//				res.setCatGicTpOthersNoAddon(rs.getDouble(48));
//				res.setTheftGicTpComprehensiveDep(rs.getDouble(49));
//				res.setTheftGicTpComprehensiveNcb(rs.getDouble(50));
//				res.setTheftGicTpComprehensiveOtherAddon(rs.getDouble(51));
//				res.setTheftGicTpComprehensiveNoAddon(rs.getDouble(52));
//				res.setTheftGicTpTpDep(rs.getDouble(53));
//				res.setTheftGicTpTpNcb(rs.getDouble(54));
//				res.setTheftGicTpTpOtherAddon(rs.getDouble(55));
//				res.setTheftGicTpTpNoAddon(rs.getDouble(56));
//				res.setTheftGicTpOthersDep(rs.getDouble(57));
//				res.setTheftGicTpOthersNcb(rs.getDouble(58));
//				res.setTheftGicTpOthersOtherAddon(rs.getDouble(59));
//				res.setTheftGicTpOthersNoAddon(rs.getDouble(60));
//				res.setOthersGicTpComprehensiveDep(rs.getDouble(61));
//				res.setOthersGicTpComprehensiveNcb(rs.getDouble(62));
//				res.setOthersGicTpComprehensiveOtherAddon(rs.getDouble(63));
//				res.setOthersGicTpComprehensiveNoAddon(rs.getDouble(64));
//				res.setOthersGicTpTpDep(rs.getDouble(65));
//				res.setOthersGicTpTpNcb(rs.getDouble(66));
//				res.setOthersGicTpTpOtherAddon(rs.getDouble(67));
//				res.setOthersGicTpTpNoAddon(rs.getDouble(68));
//				res.setOthersGicTpOthersDep(rs.getDouble(69));
//				res.setOthersGicTpOthersNcb(rs.getDouble(70));
//				res.setOthersGicTpOthersOtherAddon(rs.getDouble(71));
//				res.setOthersGicTpOthersNoAddon(rs.getDouble(72));
//				}else if(claimParamType.equals("NIC")){
//					res.setNicComprehensiveDep(rs.getDouble(1));
//					res.setNicComprehensiveNcb(rs.getDouble(2));
//					res.setNicComprehensiveOtherAddon(rs.getDouble(3));
//					res.setNicComprehensiveNoAddon(rs.getDouble(4));
//					res.setNicTpDep(rs.getDouble(5));
//					res.setNicTpNcb(rs.getDouble(6));
//					res.setNicTpOtherAddon(rs.getDouble(7));
//					res.setNicTpNoAddon(rs.getDouble(8));
//					res.setNicOthersDep(rs.getDouble(9));
//					res.setNicOthersNcb(rs.getDouble(10));
//					res.setNicOthersOtherAddon(rs.getDouble(11));
//					res.setNicOthersNoAddon(rs.getDouble(12));
//					res.setNicTpComprehensiveDep(rs.getDouble(13));
//					res.setNicTpComprehensiveNcb(rs.getDouble(14));
//					res.setNicTpComprehensiveOtherAddon(rs.getDouble(15));
//					res.setNicTpComprehensiveNoAddon(rs.getDouble(16));
//					res.setNicTpTpDep(rs.getDouble(17));
//					res.setNicTpTpNcb(rs.getDouble(18));
//					res.setNicTpTpOtherAddon(rs.getDouble(19));
//					res.setNicTpTpNoAddon(rs.getDouble(20));
//					res.setNicTpOthersDep(rs.getDouble(21));
//					res.setNicTpOthersNcb(rs.getDouble(22));
//					res.setNicTpOthersOtherAddon(rs.getDouble(23));
//					res.setNicTpOthersNoAddon(rs.getDouble(24));
//					res.setNicOdComprehensiveDep(rs.getDouble(25));
//					res.setNicOdComprehensiveNcb(rs.getDouble(26));
//					res.setNicOdComprehensiveOtherAddon(rs.getDouble(27));
//					res.setNicOdComprehensiveNoAddon(rs.getDouble(28));
//					res.setNicOdTpDep(rs.getDouble(29));
//					res.setNicOdTpNcb(rs.getDouble(30));
//					res.setNicOdTpOtherAddon(rs.getDouble(31));
//					res.setNicOdTpNoAddon(rs.getDouble(32));
//					res.setNicOdOthersDep(rs.getDouble(33));
//					res.setNicOdOthersNcb(rs.getDouble(34));
//					res.setNicOdOthersOtherAddon(rs.getDouble(35));
//					res.setNicOdOthersNoAddon(rs.getDouble(36));
//				}
//				
				
//			}
//			
//			
				 kpiResponseList.add(res);
 }
	    }catch(Exception e) {
	    	System.out.println("Error occured when query running");
             System.out.println(e.getMessage());	    	
	    }
	    
		return kpiResponseList;
}
	
@GetMapping("/getClaimsNewCubeDataForFIN")
@ResponseBody
public List<ClaimsCubeResponseNew> getClaimsNewCubeData(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
		throws SQLException {
	Connection connection = null;
	List<ClaimsCubeResponseNew> kpiResponseList = new ArrayList<ClaimsCubeResponseNew>();
	long startTime = System.currentTimeMillis();
	try {
//		String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//		String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
		
		List<ProductMaster> productMasters = productMasterRepository.findAll();

		String motorProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";
		
		System.out.println(motorProductVals);

		String healthProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password", "KYLIN");
		connection = driverManager
				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		Statement stmt = connection.createStatement();
		System.out.println("Statement: "+stmt);
		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];
		String claimMovementStartDate = fromYear + "-" + fromMonth + "-01";
		String claimMovementEndDate = toYear + "-" + toMonth + "-31";
		String queryStr = "";
		queryStr += "SELECT "
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.CLAIM_COUNT) as CLAIM_COUNT,"
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.CAT_CLAIM_COUNT) as CAT_CLAIM_COUNT, "
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.THEFT_CLAIM_COUNT) as THEFT_CLAIM_COUNT, "
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.OTHER_CLAIM_COUNT) as OTHER_CLAIM_COUNT, "
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.CLAIM_COUNT_TP) as CLAIM_COUNT_TP, "
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.ADDON_CLAIM_COUNT) as ADDON_CLAIM_COUNT, "
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.ADDON_CAT_CLAIM_COUNT) as ADDON_CAT_CLAIM_COUNT, "
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.ADDON_THEFT_CLAIM_COUNT) as ADDON_THEFT_CLAIM_COUNT, "
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.ADDON_OTHER_CLAIM_COUNT) as ADDON_OTHER_CLAIM_COUNT, "
             + "SUM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.ADDON_CLAIM_COUNT_TP) as ADDON_CLAIM_COUNT_TP "
             + "FROM RSDB.RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE as RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE "
             + "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
             + "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
             + "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "
             + "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "
             + "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
             + "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
             + "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "
             + "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "
             + "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE"
             + " WHERE CLM_MOVEMENT_DATE>='" + claimMovementStartDate + "' AND CLM_MOVEMENT_DATE<='"+ claimMovementEndDate + "'";
		
		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
		
		
//		if(claimType.equalsIgnoreCase("R")){
//			queryStr += " WHERE CLM_MOVEMENT_DATE>='" + claimMovementStartDate + "' AND CLM_MOVEMENT_DATE<='"+ claimMovementEndDate + "'";
//
//		}else if(claimType.equalsIgnoreCase("U")){
//			
//			queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";
//		}
		
		
//		if (filterRequest != null && filterRequest.getPolicyTypes() != null
//				&& !filterRequest.getPolicyTypes().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//				vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//				if (i != filterRequest.getPolicyTypes().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.POLICY_TYPE) in (" + vals + ")";
//		}
		
		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr +=" and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.BUSINESS_TYPE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.CHANNEL_NEW) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.SUB_CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.MAKE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.MODELGROUP) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and coalesce(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.FUELTYPE,'N') in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.NCB_FLAG) in (" + vals + ")";
		}

		
		
//		if (filterRequest != null && filterRequest.getMotorChannel() != null
//				&& !filterRequest.getMotorChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
//		}

		
//		if (filterRequest != null && filterRequest.getMotorChannel() != null
//				&& !filterRequest.getMotorChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
//		}

//		if (filterRequest != null && filterRequest.getMotorSubChannel() != null
//				&& !filterRequest.getMotorSubChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorSubChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
//		}

		/*if (filterRequest != null && filterRequest.getMotorRegion() != null
				&& !filterRequest.getMotorRegion().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
				vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
				if (i != filterRequest.getMotorRegion().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
		}*/
		
		if (filterRequest != null && filterRequest.getMotorZone() != null
				&& !filterRequest.getMotorZone().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
				if (i != filterRequest.getMotorZone().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorCluster() != null
				&& !filterRequest.getMotorCluster().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
				if (i != filterRequest.getMotorCluster().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorState() != null
				&& !filterRequest.getMotorState().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
				if (i != filterRequest.getMotorState().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCity() != null
				&& !filterRequest.getMotorCity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
				if (i != filterRequest.getMotorCity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorBranch() != null
				&& !filterRequest.getMotorBranch().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
				if (i != filterRequest.getMotorBranch().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.BRANCH_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.AGENT_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.POLICY_TYPE_NEW) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.CATEGORISATION) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.VEHICLEAGE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_NEW_CURRENT_TABLE.ENGINECAPACITY) in (" + vals + ")";
		}
		

//		if (filterRequest != null && filterRequest.getMotorFuelType() != null
//				&& !filterRequest.getMotorFuelType().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
//				vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
//				if (i != filterRequest.getMotorFuelType().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE) in (" + vals + ")";
//		}
		
//		if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
//				&& !filterRequest.getMotorNcbFlag().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
//				vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
//				if (i != filterRequest.getMotorNcbFlag().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
//		}
		
		/*if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					if (i != filterRequest.getMotorNcbFlag().size() - 1) {
						vals += ",";
					}
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
				}else{
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) not in (" + vals + ")";
				}
			
				System.out.println("HE query------------------------------ " + queryStr);
				
			}
			
		}*/
		
		
		if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
			int cvalcounter = 0,cvalNHEcounter = 0;
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					 if(cvalcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
					 cvalcounter++;
				 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
					if(cvalNHEcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
					cvalNHEcounter++;
				 }
			
				System.out.println("HE query------------------------------ " + queryStr);
				
			}
			
		}
		

//		if(filterRequest.getAddOnNew().equals("Include")){
//			queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,category) x";
//		}else if(filterRequest.getAddOnNew().equals("Exclude")){
//			queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,ADDON_TYPE,category) x)mm";
//		}else if(filterRequest.getAddOnNew().equals("Only Addon")){
//			queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,ADDON_TYPE,category) x)mm";
//		}

		System.out.println("queryStr------------------------------ " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ "+rs);

		// jsArray = convertToJSON(rs);

		while (rs.next()) {

			ClaimsCubeResponseNew res = new ClaimsCubeResponseNew();
			res.setClaimCount(rs.getDouble(1));
			res.setCatClaimCount(rs.getDouble(2));
			res.setTheftClaimCount(rs.getDouble(3));
			res.setOtherClaimCount(rs.getDouble(4));
			res.setClaimCountTp(rs.getDouble(5));
			res.setAddOnClaimCount(rs.getDouble(6));
			res.setAddOnCatClaimCount(rs.getDouble(7));
			res.setAddOnTheftClaimCount(rs.getDouble(8));
			res.setAddOnOtherClaimCount(rs.getDouble(9));
			res.setAddOnClaimCountTp(rs.getDouble(10));
			kpiResponseList.add(res);
			
			System.out.println("KPI Response List: -------> "+kpiResponseList);
//			res.setCatClaimCountPoliciesComprehensive(rs.getDouble(1));
//			res.setCatClaimCountPoliciesTp(rs.getDouble(2));
//			res.setCatClaimCountPoliciesOthers(rs.getDouble(3));
//			res.setTheftClaimCountPoliciesComprehensive(rs.getDouble(4));
//			res.setTheftClaimCountPoliciesTp(rs.getDouble(5));
//			res.setTheftClaimCountPoliciesOthers(rs.getDouble(6));
//			res.setOthersClaimCountPoliciesComprehensive(rs.getDouble(7));
//			res.setOthersClaimCountPoliciesTp(rs.getDouble(8));
//			res.setOthersClaimCountPoliciesOthers(rs.getDouble(9));
//			kpiResponseList.add(res);
		}

		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	} finally {
		connection.close();
	}
	return kpiResponseList;
}

@GetMapping("/getClaimsNewCubeDataForUW")
@ResponseBody
public List<ClaimsCubeResponseNew> getClaimsNewUWCubeData(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
		throws SQLException {
	Connection connection = null;
	List<ClaimsCubeResponseNew> kpiResponseList = new ArrayList<ClaimsCubeResponseNew>();
	long startTime = System.currentTimeMillis();
	try {
//		String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//		String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
		
		List<ProductMaster> productMasters = productMasterRepository.findAll();

		String motorProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";
		
		System.out.println(motorProductVals);

		String healthProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password", "KYLIN");
		connection = driverManager
				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		Statement stmt = connection.createStatement();
		System.out.println("Statement: "+stmt);
		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];
		String claimMovementStartDate = fromYear + "-" + fromMonth + "-01";
		String claimMovementEndDate = toYear + "-" + toMonth + "-31";
		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
		String queryStr = "";
		queryStr += "SELECT "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.CLAIM_COUNT) as CLAIM_COUNT, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.CAT_CLAIM_COUNT) as CAT_CLAIM_COUNT, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.THEFT_CLAIM_COUNT) as THEFT_CLAIM_COUNT, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.OTHER_CLAIM_COUNT) as OTHER_CLAIM_COUNT, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.CLAIM_COUNT_TP) as CLAIM_COUNT_TP, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.ADDON_CLAIM_COUNT) as ADDON_CLAIM_COUNT, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.ADDON_CAT_CLAIM_COUNT) as ADDON_CAT_CLAIM_COUNT, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.ADDON_THEFT_CLAIM_COUNT) as ADDON_THEFT_CLAIM_COUNT, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.ADDON_OTHER_CLAIM_COUNT) as ADDON_OTHER_CLAIM_COUNT, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.ADDON_CLAIM_COUNT_TP) as ADDON_CLAIM_COUNT_TP "
				+ "FROM RSDB.RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE as RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE "
				+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
				+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
				+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "
				+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "
				+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "					
				+ "WHERE (SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"')";		
		
		
		
//		if(claimType.equalsIgnoreCase("R")){
//			queryStr += " WHERE CLM_MOVEMENT_DATE>='" + claimMovementStartDate + "' AND CLM_MOVEMENT_DATE<='"+ claimMovementEndDate + "'";
//
//		}else if(claimType.equalsIgnoreCase("U")){
//			
//			queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";
//		}
		
		
//		if (filterRequest != null && filterRequest.getPolicyTypes() != null
//				&& !filterRequest.getPolicyTypes().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//				vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//				if (i != filterRequest.getPolicyTypes().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.POLICY_TYPE) in (" + vals + ")";
//		}
//		
		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.BUSINESS_TYPE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.CHANNEL_NEW) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.SUB_CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.MAKE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.MODELGROUP) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and coalesce(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.FUELTYPE,'N') in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.NCB_FLAG) in (" + vals + ")";
		}

		
		
//		if (filterRequest != null && filterRequest.getMotorChannel() != null
//				&& !filterRequest.getMotorChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
//		}

		
//		if (filterRequest != null && filterRequest.getMotorChannel() != null
//				&& !filterRequest.getMotorChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
//		}

//		if (filterRequest != null && filterRequest.getMotorSubChannel() != null
//				&& !filterRequest.getMotorSubChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorSubChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
//		}

		/*if (filterRequest != null && filterRequest.getMotorRegion() != null
				&& !filterRequest.getMotorRegion().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
				vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
				if (i != filterRequest.getMotorRegion().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
		}*/
		
		if (filterRequest != null && filterRequest.getMotorZone() != null
				&& !filterRequest.getMotorZone().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
				if (i != filterRequest.getMotorZone().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorCluster() != null
				&& !filterRequest.getMotorCluster().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
				if (i != filterRequest.getMotorCluster().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorState() != null
				&& !filterRequest.getMotorState().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
				if (i != filterRequest.getMotorState().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCity() != null
				&& !filterRequest.getMotorCity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
				if (i != filterRequest.getMotorCity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorBranch() != null
				&& !filterRequest.getMotorBranch().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
				if (i != filterRequest.getMotorBranch().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.BRANCH_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.AGENT_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.POLICY_TYPE_NEW) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.CATEGORISATION) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.VEHICLEAGE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_UW_CLAIMS_NEW_CURRENT_TABLE.ENGINECAPACITY) in (" + vals + ")";
		}
		

//		if (filterRequest != null && filterRequest.getMotorFuelType() != null
//				&& !filterRequest.getMotorFuelType().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
//				vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
//				if (i != filterRequest.getMotorFuelType().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE) in (" + vals + ")";
//		}
		
//		if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
//				&& !filterRequest.getMotorNcbFlag().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
//				vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
//				if (i != filterRequest.getMotorNcbFlag().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
//		}
		
		/*if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					if (i != filterRequest.getMotorNcbFlag().size() - 1) {
						vals += ",";
					}
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
				}else{
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) not in (" + vals + ")";
				}
			
				System.out.println("HE query------------------------------ " + queryStr);
				
			}
			
		}*/
		
		
		if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
			int cvalcounter = 0,cvalNHEcounter = 0;
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					 if(cvalcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
					 cvalcounter++;
				 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
					if(cvalNHEcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
					cvalNHEcounter++;
				 }
			
				System.out.println("HE query------------------------------ " + queryStr);
				
			}
			
		}
		

//		if(filterRequest.getAddOnNew().equals("Include")){
//			queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,category) x";
//		}else if(filterRequest.getAddOnNew().equals("Exclude")){
//			queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,ADDON_TYPE,category) x)mm";
//		}else if(filterRequest.getAddOnNew().equals("Only Addon")){
//			queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,ADDON_TYPE,category) x)mm";
//		}

		System.out.println("queryStr------------------------------ " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ Claims UW"+rs);

		// jsArray = convertToJSON(rs);

		while (rs.next()) {

			ClaimsCubeResponseNew res = new ClaimsCubeResponseNew();
			res.setClaimCount(rs.getDouble(1));
			res.setCatClaimCount(rs.getDouble(2));
			res.setTheftClaimCount(rs.getDouble(3));
			res.setOtherClaimCount(rs.getDouble(4));
			res.setClaimCountTp(rs.getDouble(5));
			res.setAddOnClaimCount(rs.getDouble(6));
			res.setAddOnCatClaimCount(rs.getDouble(7));
			res.setAddOnTheftClaimCount(rs.getDouble(8));
			res.setAddOnOtherClaimCount(rs.getDouble(9));
			res.setAddOnClaimCountTp(rs.getDouble(10));
			kpiResponseList.add(res);
			
			System.out.println(kpiResponseList);
			
//			res.setCatClaimCountPoliciesComprehensive(rs.getDouble(1));
//			res.setCatClaimCountPoliciesTp(rs.getDouble(2));
//			res.setCatClaimCountPoliciesOthers(rs.getDouble(3));
//			res.setTheftClaimCountPoliciesComprehensive(rs.getDouble(4));
//			res.setTheftClaimCountPoliciesTp(rs.getDouble(5));
//			res.setTheftClaimCountPoliciesOthers(rs.getDouble(6));
//			res.setOthersClaimCountPoliciesComprehensive(rs.getDouble(7));
//			res.setOthersClaimCountPoliciesTp(rs.getDouble(8));
//			res.setOthersClaimCountPoliciesOthers(rs.getDouble(9));
//			kpiResponseList.add(res);
		}

		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	} finally {
		connection.close();
	}
	return kpiResponseList;
}	
	
@GetMapping("/getPolicyNewCubeDataForFIN")
@ResponseBody
public List<PolicyCubeResponseNew> getPolicyNewCubeDataForFIN(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
		throws SQLException {
	Connection connection = null;
	List<PolicyCubeResponseNew> kpiResponseList = new ArrayList<PolicyCubeResponseNew>();
	long startTime = System.currentTimeMillis();
	try {
		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

		List<ProductMaster> productMasters = productMasterRepository.findAll();

		/*String motorProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		String healthProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";*/

		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password", "KYLIN");
		connection = driverManager
				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		Statement stmt = connection.createStatement();

		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];

		System.out.println("PolicyApiPeriodBase-----------------------------------PolicyApi");
		

		String queryStr = "select "  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.POLICY_COUNT) as POLICY_COUNT,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.POLICY_COUNT_OD) as POLICY_COUNT_OD,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.POLICY_COUNT_TP) as POLICY_COUNT_TP,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.POLICY_COUNT_OTHERS) as POLICY_COUNT_OTHERS,"
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ADDON_POLICY_COUNT) as ADDON_POLICY_COUNT,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ADDON_POLICY_COUNT_OD) as ADDON_POLICY_COUNT_OD,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ADDON_POLICY_COUNT_TP) as ADDON_POLICY_COUNT_TP,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ADDON_POLICY_COUNT_OTHERS) as ADDON_POLICY_COUNT_OTHERS,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ACQ_COST) as ACQ_COST,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ACQ_COST_OD) as ACQ_COST_OD,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ACQ_COST_TP) as ACQ_COST_TP,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ACQ_COST_OTHERS) as ACQ_COST_OTHERS,"
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ADDON_ACQ_COST) as ADDON_ACQ_COST,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ADDON_ACQ_COST_OD) as ADDON_ACQ_COST_OD,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ADDON_ACQ_COST_TP) as ADDON_ACQ_COST_TP,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ADDON_ACQ_COST_OTHERS) as ADDON_ACQ_COST_OTHERS,"  
		+ " SUM(RSA_KPI_FACT_POLICY_CURRENT_NEW.LIVESCOVERED) AS LIVESCOVERED"
		+ " FROM RSDB.RSA_KPI_FACT_POLICY_CURRENT_NEW as RSA_KPI_FACT_POLICY_CURRENT_NEW"  
		+ " LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_POLICY_CURRENT_NEW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE " 
		+ " LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_POLICY_CURRENT_NEW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE"  
		+ " LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_POLICY_CURRENT_NEW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE"  
		+ " LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_POLICY_CURRENT_NEW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE"  
		+ " LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_POLICY_CURRENT_NEW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE"  
		+ " LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_POLICY_CURRENT_NEW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE " 
		+ " LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_POLICY_CURRENT_NEW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME"  
		+ " LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "
		+ " LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_POLICY_CURRENT_NEW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE" ;
					
		queryStr += " WHERE ";
		queryStr += getFinCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));

			if (filterRequest != null && filterRequest.getChannelNow() != null
					&& !filterRequest.getChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
					vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.CHANNEL) in ("+ vals +")";
			}
			
			if(filterRequest != null && filterRequest.getChannelNew() != null && !filterRequest.getChannelNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNew().size(); i++) {
					vals += "'" + filterRequest.getChannelNew().get(i).trim() + "'";
					if (i != filterRequest.getChannelNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.CHANNEL_NEW) in (" + vals + ")";

			}
			
			if (filterRequest != null && filterRequest.getSubChannelNow() != null
					&& !filterRequest.getSubChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
					vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getSubChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.SUB_CHANNEL) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.AGENT_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorFuelType() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and coalesce(RSA_KPI_FACT_POLICY_CURRENT_NEW.FUELTYPE,'N') in  (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getNcbNow() != null
					&& !filterRequest.getNcbNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
					vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
					if (i != filterRequest.getNcbNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.NCB_FLAG) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorBranch() != null
					&& !filterRequest.getMotorBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
					vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
					if (i != filterRequest.getMotorBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.BRANCH_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.BUSINESS_TYPE) in  (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMakeNow() != null
					&& !filterRequest.getMakeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
					vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
					if (i != filterRequest.getMakeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.MAKE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getModelGroupNow() != null
					&& !filterRequest.getModelGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
					vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getModelGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.MODELGROUP) in  (" + vals + ")";
			}
			
//			if (filterRequest != null && filterRequest.getPolicyTypes() != null
//					&& !filterRequest.getPolicyTypes().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//					vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//					if (i != filterRequest.getPolicyTypes().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.POLICY_TYPE) in (" + vals + ")";
//			}
			
			if(filterRequest != null && filterRequest.getPolicyTypeNew() != null && !filterRequest.getPolicyTypeNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getPolicyTypeNew().size(); i++) {
					vals += "'" + filterRequest.getPolicyTypeNew().get(i).trim() + "'";
					if (i != filterRequest.getPolicyTypeNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.POLICY_TYPE_NEW) in(" + vals + ")";
			}
			
			if(filterRequest != null && filterRequest.getCategorisation() != null && !filterRequest.getCategorisation().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getCategorisation().size(); i++) {
					vals += "'" + filterRequest.getCategorisation().get(i).trim() + "'";
					if (i != filterRequest.getCategorisation().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.CATEGORISATION) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getVehicleAge() != null
					&& !filterRequest.getVehicleAge().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getVehicleAge().size(); i++) {
					vals += "'" + filterRequest.getVehicleAge().get(i).trim() + "'";
					if (i != filterRequest.getVehicleAge().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.VEHICLEAGE) in (" + vals + ")";
			}
			
			if(filterRequest != null && filterRequest.getEngineCapacity() != null && !filterRequest.getEngineCapacity().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getEngineCapacity().size(); i++) {
					vals += "'" + filterRequest.getEngineCapacity().get(i).trim() + "'";
					if (i != filterRequest.getEngineCapacity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_POLICY_CURRENT_NEW.ENGINECAPACITY) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getIntermediaryNames() != null
					&& !filterRequest.getIntermediaryNames().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getIntermediaryNames().size(); i++) {
					vals += "'" + filterRequest.getIntermediaryNames().get(i).trim() + "'";
					if (i != filterRequest.getIntermediaryNames().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCarType() != null
					&& !filterRequest.getMotorCarType().isEmpty()) {
				String vals = "'HIGHEND','High End'";
				String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
				int cvalcounter = 0,cvalNHEcounter = 0;
				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
					
					 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
						 if(cvalcounter==0)
						queryStr += "and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
						 cvalcounter++;
					 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
						if(cvalNHEcounter==0)
						queryStr += "and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in  (" + nheVals + ")";
						cvalNHEcounter++;
					 }
					System.out.println("Hign End Query Inside InsCube ------------------------------ " + queryStr);					
				}				
			}
			
			if (filterRequest != null && filterRequest.getMotorZone() != null
					&& !filterRequest.getMotorZone().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
					vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
					if (i != filterRequest.getMotorZone().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCluster() != null
					&& !filterRequest.getMotorCluster().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
					vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
					if (i != filterRequest.getMotorCluster().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorState() != null
					&& !filterRequest.getMotorState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
					vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
					if (i != filterRequest.getMotorState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCity() != null
					&& !filterRequest.getMotorCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
					vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
					if (i != filterRequest.getMotorCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getStateGroupNow() != null
					&& !filterRequest.getStateGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
					vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getStateGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
			}
			
			System.out.println("policyqueryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

		while (rs.next()) {

			PolicyCubeResponseNew res = new PolicyCubeResponseNew();
			res.setWrittenPolicies(rs.getDouble(1));
			res.setWrittenPoliciesComprehensive(rs.getDouble(2));
			res.setWrittenPoliciesTp(rs.getDouble(3));
			res.setWrittenPoliciesOthers(rs.getDouble(4));
			res.setAddonWrittenPolicies(rs.getDouble(5));
			res.setAddonWrittenPoliciesComprehensive(rs.getDouble(6));
			res.setAddonWrittenPoliciesTp(rs.getDouble(7));
			res.setAddonWrittenPoliciesOthers(rs.getDouble(8));
			res.setAcqCost(rs.getDouble(9));
			res.setAcqCostComprehensive(rs.getDouble(10));
			res.setAcqCostTp(rs.getDouble(11));
			res.setAcqCostOthers(rs.getDouble(12));
			res.setAddonAcqCost(rs.getDouble(13));
			res.setAddonAcqCostComprehensive(rs.getDouble(14));
			res.setAddonAcqCostTp(rs.getDouble(15));
			res.setAddonAcqCostOthers(rs.getDouble(16));
			res.setLivesCovered(rs.getDouble(17));
			kpiResponseList.add(res);
		}

		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	} finally {
		connection.close();
	}
	return kpiResponseList;
}

@GetMapping("/getPolicyNewCubeDataForUW")
@ResponseBody
public List<PolicyCubeResponseNew> getPolicyNewCubeDataForUW(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
		throws SQLException {
	Connection connection = null;
	List<PolicyCubeResponseNew> kpiResponseList = new ArrayList<PolicyCubeResponseNew>();
	long startTime = System.currentTimeMillis();
	try {
		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

		List<ProductMaster> productMasters = productMasterRepository.findAll();

		/*String motorProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		String healthProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";*/

		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password", "KYLIN");
		connection = driverManager
				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		Statement stmt = connection.createStatement();

		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];

		System.out.println("PolicyApiPeriodBase-----------------------------------PolicyApi");
		

		String queryStr = "select"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.POLICY_COUNT) as POLICY_COUNT,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.POLICY_COUNT_OD) as POLICY_COUNT_OD,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.POLICY_COUNT_TP) as POLICY_COUNT_TP,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.POLICY_COUNT_OTHERS) as POLICY_COUNT_OTHERS,"
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ADDON_POLICY_COUNT) as ADDON_POLICY_COUNT,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ADDON_POLICY_COUNT_OD) as ADDON_POLICY_COUNT_OD,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ADDON_POLICY_COUNT_TP) as ADDON_POLICY_COUNT_TP,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ADDON_POLICY_COUNT_OTHERS) as ADDON_POLICY_COUNT_OTHERS,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ACQ_COST) as ACQ_COST, " 
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ACQ_COST_OD) as ACQ_COST_OD,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ACQ_COST_TP) as ACQ_COST_TP,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ACQ_COST_OTHERS) as ACQ_COST_OTHERS,"
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ADDON_ACQ_COST) as ADDON_ACQ_COST,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ADDON_ACQ_COST_OD) as ADDON_ACQ_COST_OD,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ADDON_ACQ_COST_TP) as ADDON_ACQ_COST_TP,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ADDON_ACQ_COST_OTHERS) as ADDON_ACQ_COST_OTHERS,"  
		+ " SUM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.LIVESCOVERED) AS LIVESCOVERED"
		+ " FROM RSDB.RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE as RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE " 
		+ " LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE " 
		+ " LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE"  
		+ " LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE"  
		+ " LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.OA_CODE = KPI_OA_MASTER_NW.OA_CODE"  
		+ " LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE"  
		+ " LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE"  
		+ " LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME"  
		+ " LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE" 
		+ " LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE";  
		
		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
			
			queryStr+=" WHERE (SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"')";
			
			if (filterRequest != null && filterRequest.getChannelNow() != null
					&& !filterRequest.getChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
					vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.CHANNEL) in ("+ vals +")";
			}
			
			if(filterRequest != null && filterRequest.getChannelNew() != null && !filterRequest.getChannelNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getChannelNew().size(); i++) {
					vals += "'" + filterRequest.getChannelNew().get(i).trim() + "'";
					if (i != filterRequest.getChannelNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.CHANNEL_NEW) in (" + vals + ")";

			}
			
			if (filterRequest != null && filterRequest.getSubChannelNow() != null
					&& !filterRequest.getSubChannelNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
					vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
					if (i != filterRequest.getSubChannelNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.SUB_CHANNEL) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.AGENT_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorFuelType() != null
					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and coalesce(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.FUELTYPE,'N') in  (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getNcbNow() != null
					&& !filterRequest.getNcbNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
					vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
					if (i != filterRequest.getNcbNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.NCB_FLAG) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorBranch() != null
					&& !filterRequest.getMotorBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
					vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
					if (i != filterRequest.getMotorBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.BRANCH_CODE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.BUSINESS_TYPE) in   (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMakeNow() != null
					&& !filterRequest.getMakeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
					vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
					if (i != filterRequest.getMakeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.MAKE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getModelGroupNow() != null
					&& !filterRequest.getModelGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
					vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getModelGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.MODELGROUP) in  (" + vals + ")";
			}
			
//			if (filterRequest != null && filterRequest.getPolicyTypes() != null
//					&& !filterRequest.getPolicyTypes().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//					vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//					if (i != filterRequest.getPolicyTypes().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.POLICY_TYPE) in  (" + vals + ")";
//			}
			
			if(filterRequest != null && filterRequest.getPolicyTypeNew() != null && !filterRequest.getPolicyTypeNew().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getPolicyTypeNew().size(); i++) {
					vals += "'" + filterRequest.getPolicyTypeNew().get(i).trim() + "'";
					if (i != filterRequest.getPolicyTypeNew().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.POLICY_TYPE_NEW) in (" + vals + ")";
			}
			
			if(filterRequest != null && filterRequest.getCategorisation() != null && !filterRequest.getCategorisation().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getCategorisation().size(); i++) {
					vals += "'" + filterRequest.getCategorisation().get(i).trim() + "'";
					if (i != filterRequest.getCategorisation().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.CATEGORISATION) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getVehicleAge() != null
					&& !filterRequest.getVehicleAge().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getVehicleAge().size(); i++) {
					vals += "'" + filterRequest.getVehicleAge().get(i).trim() + "'";
					if (i != filterRequest.getVehicleAge().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.VEHICLEAGE) in (" + vals + ")";
			}
			
			if(filterRequest != null && filterRequest.getEngineCapacity() != null && !filterRequest.getEngineCapacity().isEmpty()){
				
				String vals = "";
				for (int i = 0; i < filterRequest.getEngineCapacity().size(); i++) {
					vals += "'" + filterRequest.getEngineCapacity().get(i).trim() + "'";
					if (i != filterRequest.getEngineCapacity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_KPI_FACT_UW_POLICY_NEW_CURRENT_TABLE.ENGINECAPACITY) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getIntermediaryNames() != null
					&& !filterRequest.getIntermediaryNames().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getIntermediaryNames().size(); i++) {
					vals += "'" + filterRequest.getIntermediaryNames().get(i).trim() + "'";
					if (i != filterRequest.getIntermediaryNames().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCarType() != null
					&& !filterRequest.getMotorCarType().isEmpty()) {
				String vals = "'HIGHEND','High End'";
				String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
				int cvalcounter = 0,cvalNHEcounter = 0;
				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
					
					 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
						 if(cvalcounter==0)
						queryStr += "and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
						 cvalcounter++;
					 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
						if(cvalNHEcounter==0)
						queryStr += "and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in  (" + nheVals + ")";
						cvalNHEcounter++;
					 }
					System.out.println("Hign End Query Inside InsCube ------------------------------ " + queryStr);					
				}				
			}
			
			if (filterRequest != null && filterRequest.getMotorZone() != null
					&& !filterRequest.getMotorZone().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
					vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
					if (i != filterRequest.getMotorZone().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCluster() != null
					&& !filterRequest.getMotorCluster().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
					vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
					if (i != filterRequest.getMotorCluster().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorState() != null
					&& !filterRequest.getMotorState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
					vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
					if (i != filterRequest.getMotorState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getMotorCity() != null
					&& !filterRequest.getMotorCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
					vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
					if (i != filterRequest.getMotorCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}
			
			if (filterRequest != null && filterRequest.getStateGroupNow() != null
					&& !filterRequest.getStateGroupNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
					vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
					if (i != filterRequest.getStateGroupNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += "and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
			}
			
			System.out.println("policyqueryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

		while (rs.next()) {

			PolicyCubeResponseNew res = new PolicyCubeResponseNew();
			res.setWrittenPolicies(rs.getDouble(1));
			res.setWrittenPoliciesComprehensive(rs.getDouble(2));
			res.setWrittenPoliciesTp(rs.getDouble(3));
			res.setWrittenPoliciesOthers(rs.getDouble(4));
			res.setAddonWrittenPolicies(rs.getDouble(5));
			res.setAddonWrittenPoliciesComprehensive(rs.getDouble(6));
			res.setAddonWrittenPoliciesTp(rs.getDouble(7));
			res.setAddonWrittenPoliciesOthers(rs.getDouble(8));
			res.setAcqCost(rs.getDouble(9));
			res.setAcqCostComprehensive(rs.getDouble(10));
			res.setAcqCostTp(rs.getDouble(11));
			res.setAcqCostOthers(rs.getDouble(12));
			res.setAddonAcqCost(rs.getDouble(13));
			res.setAddonAcqCostComprehensive(rs.getDouble(14));
			res.setAddonAcqCostTp(rs.getDouble(15));
			res.setAddonAcqCostOthers(rs.getDouble(16));
			res.setLivesCovered(rs.getDouble(17));
			kpiResponseList.add(res);
		}

		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	} finally {
		connection.close();
	}
	return kpiResponseList;
}

@GetMapping("/getSingleLineCubeGicDataNewFIN")
@ResponseBody
public List<SingleLineCubeResponseNew> getSingleLineCubeGicDataNewFinYear (HttpServletRequest req, UserMatrixMasterRequest filterRequest
		){
	Connection connection = null;
	List<SingleLineCubeResponseNew> kpiResponseNew = new ArrayList<SingleLineCubeResponseNew>();
	double nicTp = 0;
	long startTime = System.currentTimeMillis();
	try {
		String fromDate = filterRequest.getFromDate() == null ? "": filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
		
		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance(); 
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password","KYLIN");
		
		connection = driverManager.connect("jdbc:kylin://"+ RMSConstants.KYLIN_BASE_IP_AND_PORT +"/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		
		Statement stmt = connection.createStatement();
		
		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];
		
		
		String queryStr = "";
		queryStr += "SELECT SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_GIC) as CSL_GIC, "
				+ "SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_CAT_GIC) as CSL_CAT_GIC, "
				+ "SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_THEFT_GIC) as CSL_THEFT_GIC, "
				+ "SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_OTHER_GIC) as CSL_OTHER_GIC, "
				+ "SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_TP_GIC) as CSL_TP_GIC, "
				+ "SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_NIC) as CSL_NIC, "
				+ "SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_CAT_NIC) as CSL_CAT_NIC, "
				+ "SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_THEFT_NIC) as CSL_THEFT_NIC, "
				+ "SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_OTHER_NIC) as CSL_OTHER_NIC, "
				+ "SUM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_TP_NIC) as CSL_TP_NIC "
				+ "FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE "
				+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
				+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
				+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "
				+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "
				+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "
				+ "LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE ";
		
		queryStr += "WHERE ( CSL_MVMT_MONTH between " + fromYear + fromMonth + " and " + toYear + toMonth+ " )";
		
		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
		

		

	if (filterRequest != null && filterRequest.getPolicyTypes() != null
			&& !filterRequest.getPolicyTypes().isEmpty()) {
		String vals = "";
		for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
			vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
			if (i != filterRequest.getPolicyTypes().size() - 1) {
				vals += ",";
			}
		}
		
		queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.POLICY_TYPE) in (" + vals + ")";
	}
	
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.BUSINESS_TYPE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CHANNEL_NEW) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.SUB_CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.MAKE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.MODELGROUP) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and coalesce(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.FUELTYPE,'N') in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.NCB_FLAG) in (" + vals + ")";
		}
		
	
		
//		if (filterRequest != null && filterRequest.getMotorChannel() != null
//				&& !filterRequest.getMotorChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
//		}

		
//		if (filterRequest != null && filterRequest.getMotorChannel() != null
//				&& !filterRequest.getMotorChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
//		}

//		if (filterRequest != null && filterRequest.getMotorSubChannel() != null
//				&& !filterRequest.getMotorSubChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorSubChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
//		}

		/*if (filterRequest != null && filterRequest.getMotorRegion() != null
				&& !filterRequest.getMotorRegion().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
				vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
				if (i != filterRequest.getMotorRegion().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
		}*/
		
		if (filterRequest != null && filterRequest.getMotorZone() != null
				&& !filterRequest.getMotorZone().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
				if (i != filterRequest.getMotorZone().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorCluster() != null
				&& !filterRequest.getMotorCluster().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
				if (i != filterRequest.getMotorCluster().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorState() != null
				&& !filterRequest.getMotorState().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
				if (i != filterRequest.getMotorState().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCity() != null
				&& !filterRequest.getMotorCity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
				if (i != filterRequest.getMotorCity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorBranch() != null
				&& !filterRequest.getMotorBranch().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
				if (i != filterRequest.getMotorBranch().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.BRANCH_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.AGENT_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getPolicyTypeNew() != null
				&& !filterRequest.getPolicyTypeNew().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getPolicyTypeNew().size(); i++) {
				vals += "'" + filterRequest.getPolicyTypeNew().get(i).trim() + "'";
				if (i != filterRequest.getPolicyTypeNew().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.POLICY_TYPE_NEW) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getCategorisation() != null
				&& !filterRequest.getCategorisation().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getCategorisation().size(); i++) {
				vals += "'" + filterRequest.getCategorisation().get(i).trim() + "'";
				if (i != filterRequest.getCategorisation().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CATEGORISATION) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getVehicleAge() != null
				&& !filterRequest.getVehicleAge().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getVehicleAge().size(); i++) {
				vals += "'" + filterRequest.getVehicleAge().get(i).trim() + "'";
				if (i != filterRequest.getVehicleAge().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.VEHICLEAGE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getEngineCapacity() != null
				&& !filterRequest.getEngineCapacity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getEngineCapacity().size(); i++) {
				vals += "'" + filterRequest.getEngineCapacity().get(i).trim() + "'";
				if (i != filterRequest.getEngineCapacity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.ENGINECAPACITY) in (" + vals + ")";
		}
		

//		if (filterRequest != null && filterRequest.getMotorFuelType() != null
//				&& !filterRequest.getMotorFuelType().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
//				vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
//				if (i != filterRequest.getMotorFuelType().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE) in (" + vals + ")";
//		}
		
//		if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
//				&& !filterRequest.getMotorNcbFlag().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
//				vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
//				if (i != filterRequest.getMotorNcbFlag().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
//		}
		
		/*if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					if (i != filterRequest.getMotorNcbFlag().size() - 1) {
						vals += ",";
					}
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
				}else{
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) not in (" + vals + ")";
				}
			
				System.out.println("HE query------------------------------ " + queryStr);
				
			}
			
		}*/
		
		if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
			int cvalcounter = 0,cvalNHEcounter = 0;
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					 if(cvalcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
					 cvalcounter++;
				 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
					if(cvalNHEcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
					cvalNHEcounter++;
				 }
			
				System.out.println("THE query------------------------------ " + queryStr);
				
			}
			
		}

//if(claimParamType.equals("GIC")){
//	queryStr += " group by RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHECODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.csl_claim_type ,CATASTROPHIC_MASTER.CAT_TYPE ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_NATURE_OF_CLAIM,CSL_CLAIM_NO,category ) x";
//}
//else if(claimParamType.equals("NIC")){
//	/*queryStr +=" GROUP by   "+
//			" uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH ) A , "+  
//			" (select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 "+  
//			" group by underwriting_year,XGEN_PRODUCTCODE,band) B   "+
//			" where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band "+   
//			" ) ";*/
//	/*queryStr +=" GROUP by   "+
//			" uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH,category ) A , "+  
//			" (select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 "+  
//			" group by underwriting_year,XGEN_PRODUCTCODE,band) B   "+
//			" where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band "+   
//			" ) ";*/
//	queryStr +=" group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,  "
//	+ " (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OD_OBLIGATORY) OD_OBLIGATORY,SUM(OD_QUOTA_SHARE) OD_QUOTA_SHARE,SUM(TP_OBLIGATORY) TP_OBLIGATORY,SUM(TP_QUOTA_SHARE) TP_QUOTA_SHARE from "
//	+ " RSA_DWH_RI_OBLIGATORY_MASTER1_NEW group by underwriting_year,XGEN_PRODUCTCODE,band) B  "
//	+ " where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";	
//}

		

	

	System.out.println("queryStr------------------------------ " + queryStr);
	ResultSet rs = stmt.executeQuery(queryStr);
	System.out.println("START------------------------------ ");
		
	// jsArray = convertToJSON(rs);
		int count =0 ;
	while (rs.next()) {

		SingleLineCubeResponseNew res = new SingleLineCubeResponseNew();
//		if(claimParamType.equals("GIC")){
	/*	res.setCatGicOdComprehensive(rs.getDouble(1));
		res.setCatGicOdTp(rs.getDouble(2));
		res.setCatGicOdOthers(rs.getDouble(3));
		res.setTheftGicOdComprehensive(rs.getDouble(4));
		res.setTheftGicOdTp(rs.getDouble(5));
		res.setTheftGicOdOthers(rs.getDouble(6));
		res.setOthersGicOdComprehensive(rs.getDouble(7));
		res.setOthersGicOdTp(rs.getDouble(8));
		res.setOthersGicOdOthers(rs.getDouble(9));
		
		res.setCatGicTpComprehensive(rs.getDouble(10));
		res.setCatGicTpTp(rs.getDouble(11));
		res.setCatGicTpOthers(rs.getDouble(12));
		res.setTheftGicTpComprehensive(rs.getDouble(13));
		res.setTheftGicTpTp(rs.getDouble(14));
		res.setTheftGicTpOthers(rs.getDouble(15));
		res.setOthersGicTpComprehensive(rs.getDouble(16));
		res.setOthersGicTpTp(rs.getDouble(17));
		res.setOthersGicTpOthers(rs.getDouble(18)); */
		
		res.setCslGic(rs.getDouble(1));
		res.setCslCatGic(rs.getDouble(2));
		res.setCslTheftGic(rs.getDouble(3));
		res.setCslOtherGic(rs.getDouble(4));
		res.setCslTpGic(rs.getDouble(5));
		res.setCslNic(rs.getDouble(6));
		res.setCslCatNic(rs.getDouble(7));
		res.setCslTheftNic(rs.getDouble(8));
		res.setCslOtherNic(rs.getDouble(9));	
		res.setCslTpNic(rs.getDouble(10));
//		count ++;
		
		
//		}else if(claimParamType.equals("NIC")){
			/*if(count==0){*/
//				res.setNicComprehensive(rs.getDouble(1));
//				res.setNicTp(rs.getDouble(2));
//				res.setNicOthers(rs.getDouble(3));
//				res.setNicTpComprehensive(nicTp);
				/*below code has to  be uncommented after category implementation*/
				/*res.setNicTpComprehensive(rs.getDouble(4));
				res.setNicTpTp(rs.getDouble(5));
				res.setNicTpOthers(rs.getDouble(6));*/
			/*}if(count==1){*/
//				res.setNicOdComprehensive(rs.getDouble(7));
//				res.setNicOdTp(rs.getDouble(8));
//				res.setNicOdOthers(rs.getDouble(9)); 
			
//		}
		
		kpiResponseNew.add(res);
	}

	System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		 

		
		
		
	}catch(Exception e){
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	}
	
	
	return kpiResponseNew;
	
}

@GetMapping("/getSingleLineCubeGicDataNewUW")
@ResponseBody
public List<SingleLineCubeResponseNew> getSingleLineCubeGicDataNewUw (HttpServletRequest req, UserMatrixMasterRequest filterRequest
		){
	Connection connection = null;
	List<SingleLineCubeResponseNew> kpiResponseNew = new ArrayList<SingleLineCubeResponseNew>();
	double nicTp = 0;
	long startTime = System.currentTimeMillis();
	try {
		String fromDate = filterRequest.getFromDate() == null ? "": filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
		
		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance(); 
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password","KYLIN");
		
		connection = driverManager.connect("jdbc:kylin://"+ RMSConstants.KYLIN_BASE_IP_AND_PORT +"/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		
		Statement stmt = connection.createStatement();
		
		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];
		
		
		String queryStr = "";
		queryStr += "SELECT SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_GIC) as CSL_GIC, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_CAT_GIC) as CSL_CAT_GIC, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_THEFT_GIC) as CSL_THEFT_GIC, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_OTHER_GIC) as CSL_OTHER_GIC, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_TP_GIC) as CSL_TP_GIC, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_NIC) as CSL_NIC, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_CAT_NIC) as CSL_CAT_NIC, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_THEFT_NIC) as CSL_THEFT_NIC, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_OTHER_NIC) as CSL_OTHER_NIC, "
				+ "SUM(RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CSL_TP_NIC) as CSL_TP_NIC "
				+ "FROM RSDB.RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE as RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE "
				+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
				+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
				+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "
				+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "
				+ "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_UW_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "
				+ "LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE ";
		
		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
		
		queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+ finstartDate +"' and SUBSTRING(inception_date,1,10) <='"+ finEndDate +"' ";
		
		
		
		

		

//	if (filterRequest != null && filterRequest.getPolicyTypes() != null
//			&& !filterRequest.getPolicyTypes().isEmpty()) {
//		String vals = "";
//		for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//			vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//			if (i != filterRequest.getPolicyTypes().size() - 1) {
//				vals += ",";
//			}
//		}
//		
//		queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.POLICY_TYPE) in (" + vals + ")";
//	}
	
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.BUSINESS_TYPE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CHANNEL_NEW) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.SUB_CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.MAKE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.MODELGROUP) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and coalesce(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.FUELTYPE,'N') in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.NCB_FLAG) in (" + vals + ")";
		}
		
	
		

		if (filterRequest != null && filterRequest.getMotorZone() != null
				&& !filterRequest.getMotorZone().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
				if (i != filterRequest.getMotorZone().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorCluster() != null
				&& !filterRequest.getMotorCluster().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
				if (i != filterRequest.getMotorCluster().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorState() != null
				&& !filterRequest.getMotorState().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
				if (i != filterRequest.getMotorState().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCity() != null
				&& !filterRequest.getMotorCity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
				if (i != filterRequest.getMotorCity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorBranch() != null
				&& !filterRequest.getMotorBranch().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
				if (i != filterRequest.getMotorBranch().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.BRANCH_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.AGENT_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getPolicyTypeNew() != null
				&& !filterRequest.getPolicyTypeNew().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getPolicyTypeNew().size(); i++) {
				vals += "'" + filterRequest.getPolicyTypeNew().get(i).trim() + "'";
				if (i != filterRequest.getPolicyTypeNew().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.POLICY_TYPE_NEW) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getCategorisation() != null
				&& !filterRequest.getCategorisation().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getCategorisation().size(); i++) {
				vals += "'" + filterRequest.getCategorisation().get(i).trim() + "'";
				if (i != filterRequest.getCategorisation().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.CATEGORISATION) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getVehicleAge() != null
				&& !filterRequest.getVehicleAge().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getVehicleAge().size(); i++) {
				vals += "'" + filterRequest.getVehicleAge().get(i).trim() + "'";
				if (i != filterRequest.getVehicleAge().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.VEHICLEAGE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getEngineCapacity() != null
				&& !filterRequest.getEngineCapacity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getEngineCapacity().size(); i++) {
				vals += "'" + filterRequest.getEngineCapacity().get(i).trim() + "'";
				if (i != filterRequest.getEngineCapacity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.ENGINECAPACITY) in (" + vals + ")";
		}
		

		
		if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
			int cvalcounter = 0,cvalNHEcounter = 0;
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					 if(cvalcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
					 cvalcounter++;
				 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
					if(cvalNHEcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
					cvalNHEcounter++;
				 }
			
				System.out.println("THE query------------------------------ " + queryStr);
				
			}
			
		}



		

	

	System.out.println("queryStr------------------------------ " + queryStr);
	ResultSet rs = stmt.executeQuery(queryStr);
	System.out.println("START------------------------------ ");
		
	// jsArray = convertToJSON(rs);
	
	while (rs.next()) {

		SingleLineCubeResponseNew res = new SingleLineCubeResponseNew();

		
		res.setCslGic(rs.getDouble(1));
		res.setCslCatGic(rs.getDouble(2));
		res.setCslTheftGic(rs.getDouble(3));
		res.setCslOtherGic(rs.getDouble(4));
		res.setCslTpGic(rs.getDouble(5));
		res.setCslNic(rs.getDouble(6));
		res.setCslCatNic(rs.getDouble(7));
		res.setCslTheftNic(rs.getDouble(8));
		res.setCslOtherNic(rs.getDouble(9));	
		res.setCslTpNic(rs.getDouble(10));
		
		kpiResponseNew.add(res);
	}

	System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		 

}catch(Exception e){
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	}
	
	return kpiResponseNew;
	
}










public String getFinCondQuery(int fromMonth,int toMonth,int fromYear,int toYear){
		
		//String txt = "("; 
		
		
		 String txt = ""; 
		 if( (fromYear==toYear && fromMonth==toMonth) ||  (fromYear==toYear && 
				 ((toMonth <4 && fromMonth <4) || (toMonth >=4 && fromMonth >=4) ) )){
			
			txt ="";
		}else{
			txt = "(";
		}
		
		int endMonth = 12;  
		if(fromYear==toYear){
		endMonth=toMonth;
		}

		if(fromMonth>0 && fromMonth<=3 ){
		 txt +="(financial_year='"+(fromYear-1)+"' and EFF_FIN_YEAR_MONTH in ( " ;
		 
		for (int i=fromMonth; i<=3; i++){
			if(i<10 && i>0){
				txt +="'0"+i+"'";
			}else{
				txt +="'"+i+"'";
			}
		if(i!=3){
		txt +=",";
		}else{
		    txt+=") ) or ";
		}

		}
		fromMonth=4;
		}
		txt +=" (financial_year='"+fromYear+"' and EFF_FIN_YEAR_MONTH in (" ;

		for (int i=fromMonth; i<=endMonth; i++){
			if(i<10 && i>0){
				txt +="'0"+i+"'";
			}else{
				txt +="'"+i+"'";
			}
		if(i!=endMonth){
		txt +=",";
		}else{
		    txt+="))";
		}
		    }
		
		/*if(fromYear == toYear && fromMonth==toMonth){
			return txt;
		}*/
		if(fromYear == toYear){
			return txt;
		}
		

		if(toMonth>0){
		 if(toMonth>3){
		    endMonth = 3;
		 }else{
		    endMonth = toMonth;
		 }

		for (int i=1; i<=endMonth; i++){
		if(i==1){
		txt +=" or (financial_year='"+(toYear-1)+"' and EFF_FIN_YEAR_MONTH in (" ;
		}
		if(i<10 && i>0){
			txt +="'0"+i+"'";
		}else{
			txt +="'"+i+"'";
		}
		if(i!=endMonth){
		txt +=",";
		}else{
		    txt+="))";
		}
		    }

		 endMonth = toMonth;

		    for (int i=4; i<=endMonth; i++){
		if(i==4){
		txt +=" or (financial_year='"+toYear+"' and EFF_FIN_YEAR_MONTH in (" ;
		}
		
		if(i<10 && i>0){
			txt +="'0"+i+"'";
		}else{
			txt +="'"+i+"'";
		}
		
		if(i!=endMonth){
		txt +=",";
		}else{
		    txt+="))";
		}
		    }

		}
		
		txt +=")";

		return txt;
		
	}

public String getFinGepCondQuery(int fromMonth,int toMonth,int fromYear,int toYear){

		
		String txt = "("; 

		int endMonth = 12;  
		if(fromYear==toYear){
		    endMonth=toMonth;
		}
		txt +="(gep_year='"+(fromYear)+"' and gep_MONTH in (" ;
		for (int i=fromMonth; i<=endMonth; i++){
			if(i<10 && i>0){
				txt += "'0"+i+"'";
			}else{
				txt += "'"+i+"'";
			}
			if(i!=endMonth){
				txt +=",";
			}else{
                txt+="))";
            }
			
		}

		if(fromYear!=toYear){
			txt +=" or (gep_year='"+(toYear)+"' and gep_MONTH in (" ;
			for (int i=1; i<=toMonth; i++){
				if(i<10 && i>0){
					txt += "'0"+i+"'";
				}else{
					txt += "'"+i+"'";
				}
				if(i!=toMonth){
					txt +=",";
				}else{
                    txt+="))";
                }
				
			}
		}
		
		txt +=")";


		return txt;
		
    }

public String getFinCondQueryForPsql(int fromMonth, int toMonth, int fromYear, int toYear){

		String txt = ""; 

		int endMonth = 12;  
		if(fromYear==toYear){
			endMonth=toMonth;
		}
		
		for (int i=fromMonth; i<=endMonth; i++){
			if(i<10 && i>0){
				txt += fromYear+"0"+i+"";
			}else{
				txt += fromYear+""+i+"";
			}
			if(i!=endMonth){
				txt +=",";
			}
			
		}

		if(fromYear!=toYear){
			txt +=",";
			for (int i=1; i<=toMonth; i++){
				if(i<10 && i>0){
					txt += toYear+"0"+i+"";
				}else{
					txt += toYear+""+i+"";
				}
				if(i!=toMonth){
					txt +=",";
				}
				
			}
		}
		return txt;
		
	}
	
	
public static String getCustomLastDate(boolean isLastDateOfCurrentMonth,boolean isLastDateOfPreviousMonth){
		
		Date today = new Date();  

        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(today);  
        if(isLastDateOfCurrentMonth){
        	calendar.add(Calendar.MONTH, 1); 
        }
        else if(isLastDateOfPreviousMonth){
        	calendar.add(Calendar.MONTH, 0); 
        }else{
        	calendar.add(Calendar.MONTH, 1); 
        }
         
        calendar.set(Calendar.DAY_OF_MONTH, 1);  
        calendar.add(Calendar.DATE, -1);  

        Date lastDayOfMonth = calendar.getTime();  

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        //System.out.println("Today            : " + sdf.format(today));  
        System.out.println("Last Day of Month: " + sdf.format(lastDayOfMonth));  
        
        return sdf.format(lastDayOfMonth);
	}

public static String getCustomFirstDate(boolean isFirstDateOfCurrentMonth,boolean isFirstDateOfPrevousMonth){
	
	Date today = new Date();  

    Calendar calendar = Calendar.getInstance();  
    calendar.setTime(today);  
    if(isFirstDateOfCurrentMonth){
    	calendar.add(Calendar.MONTH, 0); 
    }
    else if(isFirstDateOfPrevousMonth){
    	calendar.add(Calendar.MONTH, -1); 
    }else{
    	calendar.add(Calendar.MONTH, 0); 
    }
    calendar.set(Calendar.DAY_OF_MONTH, 1);  

    Date lastDayOfMonth = calendar.getTime();  

    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
    
    return sdf.format(lastDayOfMonth);
}






public String frameMesaureQuery(Integer fromMonth, Integer toMonth,Integer fromYear , Integer toYear,List<String> measureList, String endQuery){
	String query= "select ",monthPrefix="",year=""; int counter = 0, measureCount = 0;
	for(String measure : measureList){
		counter = 0;
		List<String> prefixArr = getColumnPrefixWithYear(fromMonth, toMonth, fromYear , toYear);
		if(measureCount>0){
			query += ",";
		}
		 query +=" sum(";
		for(String prefix : prefixArr){
		if(counter>0)
				query += "+";
		monthPrefix = prefix.split("@@")[0];
		year = prefix.split("@@")[1];
		query += "(case when gep_year='"+year+"' then "+monthPrefix+measure+" else 0 end)";
		counter++;
		}
		query+=")";
		measureCount++;
	}
	query+=endQuery;
	return query;
}

public static List<String> getColumnPrefixWithYear(int fromMonth, int toMonth,int fromYear , int toYear){
	String[] monthArr = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
	//String prefixArr="";
	List<String> prefixArr = new  ArrayList<>();
	if(fromYear==toYear){
		for(int i=fromMonth-1 ; i <=toMonth-1; i++){
			//prefixArr += monthArr[i]+"_"+"@@"+toYear;
			prefixArr.add(monthArr[i]+"_"+"@@"+toYear);
		}
	}else{
		for(int i=fromMonth-1 ; i <=12-1; i++){
			//prefixArr += monthArr[i]+"_"+"@@"+fromYear;
			prefixArr.add(monthArr[i]+"_"+"@@"+fromYear);
		}
		for(int i=0 ; i <=toMonth-1; i++){
			//prefixArr += monthArr[i]+"_"+"@@"+toYear;
			prefixArr.add(monthArr[i]+"_"+"@@"+toYear);
		}
	}
	
	
	return prefixArr;
}

public List<String> getgepBaseMeasures(){
	
	List<String> list = new ArrayList<>();
	list.add("GEPCOVERAGE");
	list.add("NEPCOVERAGE");
	list.add("GEP_OD");
	list.add("GEP_TP");
	list.add("NEP_OD");
	list.add("NEP_TP");
	//list.add("DISCOUNT_GEP_OD");
	//list.add("DISCOUNT_NEP_OD");
	list.add("GEP_NILDEP");
	list.add("GEP_NCB");
	list.add("GEP_OTHER_ADDON");
	//list.add("GEP_EARNED_DAYS");
	//list.add("GEP_POLICY_COUNT");
	list.add("OD_EARNED_POLICIES");
	/*list.add("DEP_EARNED_POLICIES");
	list.add("NCB_EARNED_POLICIES");
	list.add("OTHER_ADDON_EARNED_POLICIES");*/
	list.add("NEP_NILDEP");
	list.add("NEP_NCB");
	list.add("NEP_OTHER_ADDON");
	return list;
}

public List<String> getgepR12FreqMeasures(){

List<String> list = new ArrayList<>();
list.add("FREQ_CAT_OD_R12");
list.add("FREQ_THEFT_OD_R12");
list.add("FREQ_OTHERS_OD_R12");
/*list.add("FREQ_CAT_DEP_R12");
list.add("FREQ_THEFT_DEP_R12");
list.add("FREQ_OTHERS_DEP_R12");
list.add("FREQ_CAT_NCB_R12");
list.add("FREQ_THEFT_NCB_R12");
list.add("FREQ_OTHERS_NCB_R12");
list.add("FREQ_CAT_OTHERADDON_R12");
list.add("FREQ_THEFT_OTHERADDON_R12");
list.add("FREQ_OTHERS_OTHERADDON_R12");*/

return list;
}

public List<String> getgepR12SevGicMeasures(){

List<String> list = new ArrayList<>();
list.add("SEV_CAT_R12");
list.add("SEV_THEFT_R12");
list.add("SEV_OTHERS_R12");
list.add("GIC_CAT_OD_R12");
list.add("GIC_THEFT_OD_R12");
list.add("GIC_OTHERS_OD_R12");
/*list.add("GIC_CAT_DEP_R12");
list.add("GIC_THEFT_DEP_R12");
list.add("GIC_OTHERS_DEP_R12");
list.add("GIC_CAT_NCB_R12");
list.add("GIC_THEFT_NCB_R12");
list.add("GIC_OTHERS_NCB_R12");
list.add("GIC_CAT_OTHERADDON_R12");
list.add("GIC_THEFT_OTHERADDON_R12");
list.add("GIC_OTHERS_OTHERADDON_R12");*/

return list;
}


public List<String> getgepUWR12FreqMeasures(){

List<String> list = new ArrayList<>();
list.add("FREQ_CAT_OD_UW_R12");
list.add("FREQ_THEFT_UWOD_R12");
list.add("FREQ_OTHERS_UWOD_R12");
/*list.add("FREQ_CAT_DEP_UW_R12");
list.add("FREQ_THEFT_DEP_UW_R12");
list.add("FREQ_OTHERS_DEP_UW_R12");
list.add("FREQ_CAT_NCB_UW_R12");
list.add("FREQ_THEFT_NCB_UW_R12");
list.add("FREQ_OTHERS_NCB_UW_R12");
list.add("FREQ_CAT_OTHERADDON_UW_R12");
list.add("FREQ_THEFT_OTHERADDON_UW_R12");
list.add("FREQ_OTHERS_OTHERADDON_UW_R12");*/

return list;
}



public double getNicTp(Integer fromMonth, Integer toMonth,Integer fromYear , Integer toYear,UserMatrixMasterRequest filterRequest, String claimType) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{

	Connection connection = null;
	double nicTp=0;
	try{	
	
	Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
	Properties info = new Properties();
	info.put("user", "ADMIN");
	info.put("password", "KYLIN");
	connection = driverManager 
			.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
	System.out.println("Connection status -------------------------->" + connection);
	Statement stmt = connection.createStatement();
	
	
	 //"select sum(gic_tp*(1-TP_QUOTA_SHARE-TP_OBLIGATORY)) from (select  SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEPCOVERAGE*0.95) as gic_tp,uw_year,GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.PRODUCT_CODE,'NONE' band",
		
	String queryStr= "SELECT (NIC_TP_ULR_POLICY) as nic_Tp from ("
			+ "SELECT SUM(gic_tp*(1-TP_QUOTA_SHARE-TP_OBLIGATORY)) as NIC_TP_ULR_POLICY,"
			+ "SUM(case when GEP_NILDEP<>0 AND NEP_NILDEP<>0 THEN GIC_TP ELSE 0 END) as NIC_NILDEP_ULR_POLICY,"
			+ "SUM(case when GEP_NCB<>0 AND NEP_NCB<>0 THEN GIC_TP ELSE 0 END) as NIC_NCB_ULR_POLICY,"
			+ "SUM(case when GEP_OTHER_ADDON<>0 AND NEP_OTHER_ADDON<>0 THEN GIC_TP ELSE 0 END) as NIC_OTHER_ADDON_ULR_POLICY "
			+ "from (select  SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GIC_TP) as gic_tp,SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_NILDEP) as GEP_NILDEP,SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_NCB) as GEP_NCB,SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_OTHER_ADDON) as GEP_OTHER_ADDON,SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_NILDEP) as NEP_NILDEP,SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_NCB) as NEP_NCB,SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_OTHER_ADDON) as NEP_OTHER_ADDON,uw_year,GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.PRODUCT_CODE,'NONE' band,CATEGORY ";
			
//			monthPrefix="",year="",measure="gic_tp"; 
			int counter = 0, measureCount = 0;
	
	
	
	//for(String measure : measureList){
		// counter = 0;
		// List<String> prefixArr = getColumnPrefixWithYear(fromMonth, toMonth, fromYear , toYear);
		// if(measureCount>0){
		// 	queryStr += ",";
		// }
		// queryStr +=" sum(";
		// for(String prefix : prefixArr){
		// if(counter>0)
		// 	queryStr += "+";
		// monthPrefix = prefix.split("@@")[0];
		// year = prefix.split("@@")[1];
		// queryStr += "(case when gep_year='"+year+"' then "+monthPrefix+measure+" else 0 end)";
		// counter++;
		// }
		// queryStr+=") gic_tp,uw_year,GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.PRODUCT_CODE,'NONE' band";
		// measureCount++;
	//}
		System.out.println("nic tp select------------------------------ " + queryStr);
		
				queryStr += "FROM RSDB.GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL as GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE ";  
		// if (fromYear.equals(toYear)) {
		// 	if (fromMonth.equals(toMonth)) {
		// 		queryStr += " where ( gep_year= " + fromYear + " )";
		// 	} else {
		// 		queryStr += " WHERE (( gep_year=" + fromYear + " ))";
		// 	}
		// } else {
		// 	queryStr += " WHERE (( gep_year=" + fromYear + " ) or ( gep_year="
		// 			+ toYear + " ))";
		// }
		
		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
			
		if(claimType.equalsIgnoreCase("R")){
			queryStr += " WHERE";
			queryStr += getFinGepCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));
		}else if(claimType.equalsIgnoreCase("U")){
			queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";
		}
		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.BUSINESS_TYPE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getPolicyTypes() != null
				&& !filterRequest.getPolicyTypes().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
				vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
				if (i != filterRequest.getPolicyTypes().size() - 1) {
					vals += ",";
				}
			}
			
			queryStr += " and TRIM(RSA_DWH_COVERCODE_MASTER.CATEGORY) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.SUB_CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.MAKE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.MODELGROUP) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.FUEL_TYPE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.STATE_GROUPING) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NCB) in (" + vals + ")";
		}
		
		
		

		if (filterRequest != null && filterRequest.getMotorChannel() != null
				&& !filterRequest.getMotorChannel().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
				if (i != filterRequest.getMotorChannel().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorSubChannel() != null
				&& !filterRequest.getMotorSubChannel().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
				vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
				if (i != filterRequest.getMotorSubChannel().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.SUB_CHANNEL) in (" + vals + ")";
		}

		/*if (filterRequest != null && filterRequest.getMotorRegion() != null
				&& !filterRequest.getMotorRegion().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
				vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
				if (i != filterRequest.getMotorRegion().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
		}*/
		
		if (filterRequest != null && filterRequest.getMotorZone() != null
				&& !filterRequest.getMotorZone().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
				if (i != filterRequest.getMotorZone().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorCluster() != null
				&& !filterRequest.getMotorCluster().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
				if (i != filterRequest.getMotorCluster().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorState() != null
				&& !filterRequest.getMotorState().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
				if (i != filterRequest.getMotorState().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCity() != null
				&& !filterRequest.getMotorCity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
				if (i != filterRequest.getMotorCity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorBranch() != null
				&& !filterRequest.getMotorBranch().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
				if (i != filterRequest.getMotorBranch().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.BRANCH_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.AGENT_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			//queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorFuelType() != null
				&& !filterRequest.getMotorFuelType().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
				vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
				if (i != filterRequest.getMotorFuelType().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.FUEL_TYPE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
				&& !filterRequest.getMotorNcbFlag().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
				vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
				if (i != filterRequest.getMotorNcbFlag().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NCB) in (" + vals + ")";
		}
		
		queryStr += " group by uw_year,PRODUCT_CODE,CATEGORY) A ,(select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OD_OBLIGATORY) OD_OBLIGATORY,SUM(OD_QUOTA_SHARE) OD_QUOTA_SHARE,SUM(TP_OBLIGATORY) TP_OBLIGATORY,SUM(TP_QUOTA_SHARE) TP_QUOTA_SHARE from  RSA_DWH_RI_OBLIGATORY_MASTER1_NEW group by underwriting_year,XGEN_PRODUCTCODE,band) B   where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band)mm; ";
		
		
		
		System.out.println("nic tp queryStr------------------------------ " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");
		
		while (rs.next()) {
			nicTp = rs.getDouble(1);
		}
		
} catch (Exception e) {
	System.out.println("kylinDataSource initialize error, ex: " + e);
	System.out.println();
	e.printStackTrace();
} finally {
	connection.close();
}
	return nicTp;
}


@GetMapping("/getSubChannelByChannel")
@ResponseBody
public List<SubChannelMaster> getSubChannelByChannel(@RequestParam(value = "channelArr[]") List<String> arrayParam,ModelMap map)
		 {
	System.out.println("called getSubChannelByChannel()::arrayParam size-->"+arrayParam.size());
	for(String s : arrayParam){
		System.out.println("param -->"+s);
	}
	List<SubChannelMaster> list = subChannelMasterRepository.findByChannelNameIn(arrayParam);
	
	return list;
	/*System.out.println("list size-->"+list.size());
	 map.addAttribute("subChannels", list);

	    // change "myview" to the name of your view 
	    return "motorKpiDataUpdatedNew :: #subChannelId";*/
}




@GetMapping("/getUWPolicyCubeDataUpdatedNew")
@ResponseBody
public List<PolicyCubeResponseNew> getUWPolicyCubeDataUpdatedNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
		throws SQLException {
	Connection connection = null;
	List<PolicyCubeResponseNew> kpiResponseList = new ArrayList<PolicyCubeResponseNew>();
	long startTime = System.currentTimeMillis();
	try {
//		String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//		String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

		List<ProductMaster> productMasters = productMasterRepository.findAll();

		/*String motorProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		String healthProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";*/

		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password", "KYLIN");
		connection = driverManager
				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		Statement stmt = connection.createStatement();

		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];
		
		
		String queryStr = "SELECT SUM(POLICY_COUNT) as POLICY_COUNT,  SUM(case when x.CATEGORY='Comprehensive' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_OD,  SUM(case when x.CATEGORY='TP' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_TP,  SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_others,  SUM(ACQ_COST) as ACQ_COST,  SUM(case when x.CATEGORY='Comprehensive' THEN ACQ_COST ELSE 0 END) as ACQ_COST_OD,  SUM(case when x.CATEGORY='TP' THEN ACQ_COST ELSE 0 END) as ACQ_COST_TP,  SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN ACQ_COST ELSE 0 END) as ACQ_COST_others,  SUM(LIVESCOVERED)  from(  SELECT  SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.LIVESCOVERED) as LIVESCOVERED  ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.ACQ_COST) as ACQ_COST  ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_COUNT ) as POLICY_COUNT  ,CATEGORY  FROM RSDB.RSA_KPI_FACT_POLICY_CURRENT as RSA_KPI_FACT_POLICY_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_POLICY_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_POLICY_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_POLICY_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";
		
		
		

	/*	String queryStr = "select "+
							" SUM(POLICY_COUNT) as POLICY_COUNT, "+
							" SUM(case when x.CATEGORY='Comprehensive' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_OD, "+
							" SUM(case when x.CATEGORY='TP' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_TP, "+
							" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN POLICY_COUNT ELSE 0 END) as POLICY_COUNT_others, "+
							" SUM(ACQ_COST) as ACQ_COST, "+
							" SUM(case when x.CATEGORY='Comprehensive' THEN ACQ_COST ELSE 0 END) as ACQ_COST_OD, "+
							" SUM(case when x.CATEGORY='TP' THEN ACQ_COST ELSE 0 END) as ACQ_COST_TP, "+
							" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN ACQ_COST ELSE 0 END) as ACQ_COST_others, "+
							" SUM(LIVESCOVERED) "+
							" from( "+
							" SELECT "+
							" SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.LIVESCOVERED) as LIVESCOVERED "+
							" ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.ACQ_COST) as ACQ_COST "+
							" ,SUM(RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_COUNT ) as POLICY_COUNT "+
							" ,CATEGORY "+ 
							" FROM RSDB.RSA_KPI_FACT_POLICY_CURRENT as RSA_KPI_FACT_POLICY_FINAL_NOW "+  
							" LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "+
							" LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "+
							" LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "+
							" LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "+
							" LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "+
							" LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "+
							" LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY "+
							" LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE "+
							" LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "+
							" LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "+
							" LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "+
							" LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT "+
							" ON RSA_KPI_FACT_POLICY_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "+
							" LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL "+
							" ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "; */

		
		/*if (fromYear.equals(toYear)) {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		} else {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		}*/

		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
		
		queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";
		//queryStr += " WHERE ";
		//queryStr += getFinCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));
		
		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MAKE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELGROUP) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and coalesce(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE,'N') in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
		}
		/*if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.getNcbNow) in (" + vals + ")";
		}*/
		
		/*latest*/
		/*if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			
			
			String Nvals = "and (RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_PREM)= '0'";
			String Yvals = "and (RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_PREM)<> '0'";
			int cvalcounter = 0,cvalNcounter = 0;
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				
				 if(filterRequest.getNcbNow().get(i).trim().contains("N")){
					 if(cvalcounter==0)
					queryStr += Nvals;
					 cvalcounter++;
				 }else if(filterRequest.getNcbNow().get(i).trim().contains("Y")){
					if(cvalNcounter==0)
					queryStr += Yvals;
					cvalNcounter++;
				 }
			
				System.out.println("NCB NEW POLICY query------------------------------ " + queryStr);
				
			}
			
			
			
		}*/
		
		
		/*String vals = "";
		for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
			vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
			if (i != filterRequest.getMotorNcbFlag().size() - 1) {
				vals += ",";
			}
		}
		queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
		
		
		queryStr += "  ";*/
		
		if (filterRequest != null && filterRequest.getMotorChannel() != null
				&& !filterRequest.getMotorChannel().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
				if (i != filterRequest.getMotorChannel().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL) in (" + vals + ")";
		}


		if (filterRequest != null && filterRequest.getMotorSubChannel() != null
				&& !filterRequest.getMotorSubChannel().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
				vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
				if (i != filterRequest.getMotorSubChannel().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
		}

		/*if (filterRequest != null && filterRequest.getMotorRegion() != null
				&& !filterRequest.getMotorRegion().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
				vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
				if (i != filterRequest.getMotorRegion().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
		}*/
		
		if (filterRequest != null && filterRequest.getMotorZone() != null
				&& !filterRequest.getMotorZone().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
				if (i != filterRequest.getMotorZone().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorCluster() != null
				&& !filterRequest.getMotorCluster().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
				if (i != filterRequest.getMotorCluster().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorState() != null
				&& !filterRequest.getMotorState().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
				if (i != filterRequest.getMotorState().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCity() != null
				&& !filterRequest.getMotorCity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
				if (i != filterRequest.getMotorCity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorBranch() != null
				&& !filterRequest.getMotorBranch().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
				if (i != filterRequest.getMotorBranch().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorFuelType() != null
				&& !filterRequest.getMotorFuelType().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
				vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
				if (i != filterRequest.getMotorFuelType().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.FUELTYPE) in (" + vals + ")";
		}
		
		/*if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
				&& !filterRequest.getMotorNcbFlag().isEmpty()) {
			
			
			String Nvals = "and (RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_PREM)= '0'";
			String Yvals = "and (RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_PREM)<> '0'";
			int cvalcounter = 0,cvalNcounter = 0;
			for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
				
				 if(filterRequest.getMotorNcbFlag().get(i).trim().contains("N")){
					 if(cvalcounter==0)
					queryStr += Nvals;
					 cvalcounter++;
				 }else if(filterRequest.getMotorNcbFlag().get(i).trim().contains("Y")){
					if(cvalNcounter==0)
					queryStr += Yvals;
					cvalNcounter++;
				 }
			
				System.out.println("NCB NEW POLICY query------------------------------ " + queryStr);
				
			}
			
		}*/
		
		/*String vals = "";
		for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
			vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
			if (i != filterRequest.getMotorNcbFlag().size() - 1) {
				vals += ",";
			}
		}
		queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
		
		
		queryStr += "  ";*/
		
		if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
			int cvalcounter = 0,cvalNHEcounter = 0;
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					 if(cvalcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
					 cvalcounter++;
				 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
					if(cvalNHEcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
					cvalNHEcounter++;
				 }
			
				System.out.println("HE query------------------------------ " + queryStr);
				
			}
			
		}
		
		


		queryStr += " group by category ) x";

		System.out.println("queryStr------------------------------ " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");

		// jsArray = convertToJSON(rs);

		while (rs.next()) {

			PolicyCubeResponseNew res = new PolicyCubeResponseNew();
			res.setWrittenPolicies(rs.getDouble(1));
			res.setWrittenPoliciesComprehensive(rs.getDouble(2));
			res.setWrittenPoliciesTp(rs.getDouble(3));
			res.setWrittenPoliciesOthers(rs.getDouble(4));
			res.setAcqCost(rs.getDouble(5));
			res.setAcqCostComprehensive(rs.getDouble(6));
			res.setAcqCostTp(rs.getDouble(7));
			res.setAcqCostOthers(rs.getDouble(8));
			res.setLivesCovered(rs.getDouble(9));
			kpiResponseList.add(res);
		}

		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	} finally {
		connection.close();
	}
	return kpiResponseList;
	}

@GetMapping("/getUWInsCubeDataUpdatedNew")
@ResponseBody
public List<InsCubeResponseNew> getUWInsCubeDataUpdatedNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
		throws SQLException {
	Connection connection = null;
	List<InsCubeResponseNew> kpiResponseList = new ArrayList<InsCubeResponseNew>();
	long startTime = System.currentTimeMillis();
	try {
//		String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//		String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();

		List<ProductMaster> productMasters = productMasterRepository.findAll();

		String motorProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		String healthProductVals = "'" + productMasters.stream()
				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
		Properties info = new Properties();
		info.put("user", "ADMIN");
		info.put("password", "KYLIN");
		connection = driverManager
				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
		System.out.println("Connection status -------------------------->" + connection);
		Statement stmt = connection.createStatement();

		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];

		
		String queryStr = "SELECT "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN 0 ELSE 0 END) as INS_NWP_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN 0 ELSE 0 END) as INS_NWP_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN 0 ELSE 0 END) as INS_NWP_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_TP *(1-tp_quota_share-tp_obligatory))+(INS_GWP_TP*tp_quota_share*tp_RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_TP *(1-tp_quota_share-tp_obligatory))+(INS_GWP_TP*tp_quota_share*tp_RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_TP *(1-tp_quota_share-tp_obligatory))+(INS_GWP_TP*tp_quota_share*tp_RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_NCB_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_NCB *(1-od_quota_share-od_obligatory))+(INS_GWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_others, "+
				" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_COMP, "+
				" SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_TP, "+
				" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_others "+
				" FROM ( SELECT "+ 
				" SUM(INS_GWP) as INS_GWP "+
				" ,SUM(INS_GWP_OD) as INS_GWP_OD "+
				" ,SUM(INS_GWP_TP) as INS_GWP_TP "+
				" ,SUM(INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD "+
				" ,SUM(INS_NWP) as INS_NWP "+
				" ,SUM(INS_NWP_OD) as INS_NWP_OD "+
				" ,SUM(INS_NWP_TP) as INS_NWP_TP "+
				" ,SUM(INS_NWP_DISCOUNT_OD) as INS_NWP_DISCOUNT_OD "+
				" ,SUM(INS_GWP_DEP) as INS_GWP_DEP "+
				" ,SUM(INS_GWP_NCB) as INS_GWP_NCB "+
				" ,SUM(INS_GWP_OTHERADDON) as INS_GWP_OTHERADDON "+
				" ,SUM(INS_NWP_DEP) as INS_NWP_DEP "+
				" ,SUM(INS_NWP_NCB) as INS_NWP_NCB "+
				" ,SUM(INS_NWP_OTHERADDON) as INS_NWP_OTHERADDON "+
				" , CATEGORY, uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code "+				
				" FROM RSDB.RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_CURRENT as RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW "+
				" LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "+
				" LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "+
				" LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "+
				" LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "+
				" LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "+
				" LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "+
				" LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY "+
				" LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE "+
				" LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "+
				" LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "+
				" LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "+
				" LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT "+
				" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "+
				" LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL "+
				" ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE ";
				
		
		/*if (fromYear.equals(toYear)) {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		} else {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		}*/
		
		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
		
		//queryStr += " WHERE fin_date >='"+finstartDate+"' and fin_date <='"+finEndDate+"' ";
		//queryStr += " WHERE ";
		//queryStr += getFinCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));
		queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";

		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getChannelNow() != null
				&& !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MAKE) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELGROUP) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and coalesce(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE,'N') in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
		}
		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
		}

		
		if (filterRequest != null && filterRequest.getMotorChannel() != null
				&& !filterRequest.getMotorChannel().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
				if (i != filterRequest.getMotorChannel().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorSubChannel() != null
				&& !filterRequest.getMotorSubChannel().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
				vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
				if (i != filterRequest.getMotorSubChannel().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
		}

		/*if (filterRequest != null && filterRequest.getMotorRegion() != null
				&& !filterRequest.getMotorRegion().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
				vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
				if (i != filterRequest.getMotorRegion().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
		}*/
		
		if (filterRequest != null && filterRequest.getMotorZone() != null
				&& !filterRequest.getMotorZone().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
				if (i != filterRequest.getMotorZone().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorCluster() != null
				&& !filterRequest.getMotorCluster().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
				if (i != filterRequest.getMotorCluster().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorState() != null
				&& !filterRequest.getMotorState().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
				if (i != filterRequest.getMotorState().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorCity() != null
				&& !filterRequest.getMotorCity().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
				if (i != filterRequest.getMotorCity().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorBranch() != null
				&& !filterRequest.getMotorBranch().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
				if (i != filterRequest.getMotorBranch().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMotorFuelType() != null
				&& !filterRequest.getMotorFuelType().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
				vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
				if (i != filterRequest.getMotorFuelType().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE) in (" + vals + ")";
		}
		
		if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
				&& !filterRequest.getMotorNcbFlag().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
				vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
				if (i != filterRequest.getMotorNcbFlag().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
		}
		
		/*if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					if (i != filterRequest.getMotorNcbFlag().size() - 1) {
						vals += ",";
					}
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
				}else{
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) not in (" + vals + ")";
				}
			
				System.out.println("HE query------------------------------ " + queryStr);
				
			}
			
		}*/
		
		if (filterRequest != null && filterRequest.getMotorCarType() != null
				&& !filterRequest.getMotorCarType().isEmpty()) {
			String vals = "'HIGHEND','High End'";
			String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
			int cvalcounter = 0,cvalNHEcounter = 0;
			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
				
				 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
					 if(cvalcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
					 cvalcounter++;
				 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
					if(cvalNHEcounter==0)
					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
					cvalNHEcounter++;
				 }
			
				System.out.println("HE query------------------------------ " + queryStr);
				
			}
			
		}
		

		/*queryStr += " group by category ) x";*/
		
		queryStr += " group by category,uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code ) aa,"
				+ " (select r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE,sum(r.OD_OBLIGATORY) OD_OBLIGATORY,sum(r.OD_QUOTA_SHARE) OD_QUOTA_SHARE,sum(OD_RI_COMMISSION) OD_RI_COMMISSION,sum(r.TP_OBLIGATORY) TP_OBLIGATORY,sum(r.TP_QUOTA_SHARE) TP_QUOTA_SHARE,sum(TP_RI_COMMISSION) TP_RI_COMMISSION  "
				+ " from rsa_dwh_ri_obligatory_master1_new r "
				+ "group by r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE) bb "
				+ "where aa.uw_year=bb.underwriting_year and aa.product_Code=bb.xgen_productcode";

		System.out.println("queryStr------------------------------ NWP " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");

		// jsArray = convertToJSON(rs);

		while (rs.next()) {

			InsCubeResponseNew res = new InsCubeResponseNew();
			
			res.setInsGwpPolicyComprehensive(rs.getDouble(1));
			res.setInsGwpPolicyTp(rs.getDouble(2));
			res.setInsGwpPolicyOthers(rs.getDouble(3));
			res.setInsGwpOdPolicyComprehensive(rs.getDouble(4));
			res.setInsGwpOdPolicyTp(rs.getDouble(5));
			res.setInsGwpOdPolicyOthers(rs.getDouble(6));
			res.setInsGwpTpPolicyComprehensive(rs.getDouble(7));
			res.setInsGwpTpPolicyTp(rs.getDouble(8));
			res.setInsGwpTpPolicyOthers(rs.getDouble(9));
			res.setInsGwpDiscountPolicyComprehensive(rs.getDouble(10));
			res.setInsGwpDiscountPolicyTp(rs.getDouble(11));
			res.setInsGwpDiscountPolicyOthers(rs.getDouble(12));
			
			res.setInsNwpPolicyComprehensive(rs.getDouble(13));
			res.setInsNwpPolicyTp(rs.getDouble(14));
			res.setInsNwpPolicyOthers(rs.getDouble(5));
			res.setInsNwpOdPolicyComprehensive(rs.getDouble(16));
			res.setInsNwpOdPolicyTp(rs.getDouble(17));
			res.setInsNwpOdPolicyOthers(rs.getDouble(18));
			res.setInsNwpTpPolicyComprehensive(rs.getDouble(19));
			res.setInsNwpTpPolicyTp(rs.getDouble(20));
			res.setInsNwpTpPolicyOthers(rs.getDouble(21));
			res.setInsNwpDiscountPolicyComprehensive(rs.getDouble(21));
			res.setInsNwpDiscountPolicyTp(rs.getDouble(23));
			res.setInsNwpDiscountPolicyOthers(rs.getDouble(24));
			
			res.setInsGwpDepPolicyComprehensive(rs.getDouble(25));
			res.setInsGwpDepPolicyTp(rs.getDouble(26));
			res.setInsGwpDepPolicyOthers(rs.getDouble(27));
			res.setInsGwpNcbPolicyComprehensive(rs.getDouble(28));
			res.setInsGwpNcbPolicyTp(rs.getDouble(29));
			res.setInsGwpNcbPolicyOthers(rs.getDouble(30));
			res.setInsGwpOtherAddonPolicyComprehensive(rs.getDouble(31));
			res.setInsGwpOtherAddonPolicyTp(rs.getDouble(32));
			res.setInsGwpOtherAddonPolicyOthers(rs.getDouble(33));
			
			res.setInsNwpDepPolicyComprehensive(rs.getDouble(34));
			res.setInsNwpDepPolicyTp(rs.getDouble(35));
			res.setInsNwpDepPolicyOthers(rs.getDouble(36));
			res.setInsNwpNcbPolicyComprehensive(rs.getDouble(37));
			res.setInsNwpNcbPolicyTp(rs.getDouble(38));
			res.setInsNwpNcbPolicyOthers(rs.getDouble(39));
			res.setInsNwpOtherAddonPolicyComprehensive(rs.getDouble(40));
			res.setInsNwpOtherAddonPolicyTp(rs.getDouble(41));
			res.setInsNwpOtherAddonPolicyOthers(rs.getDouble(42));
			
			kpiResponseList.add(res);
		}

		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
	} finally {
		connection.close();
	}
	return kpiResponseList;
}



//@GetMapping("/getUWClaimsCubeDataUpdatedNew")
//@ResponseBody
//public List<ClaimsCubeResponseNew> getUWClaimsCubeDataUpdatedNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
//		throws SQLException {
//	Connection connection = null;
//	List<ClaimsCubeResponseNew> kpiResponseList = new ArrayList<ClaimsCubeResponseNew>();
//	long startTime = System.currentTimeMillis();
//	try {
////		String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
////		String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
//		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
//		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
//		
//		//List<ProductMaster> productMasters = productMasterRepository.findAll();
//
//		/*String motorProductVals = "'" + productMasters.stream()
//				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
//				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";
//
//		String healthProductVals = "'" + productMasters.stream()
//				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
//				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";*/
//
//		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
//		Properties info = new Properties();
//		info.put("user", "ADMIN");
//		info.put("password", "KYLIN");
//		connection = driverManager
//				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
//		System.out.println("Connection status -------------------------->" + connection);
//		Statement stmt = connection.createStatement();
//
//		String fromMonth = fromDate.split("/")[0];
//		String fromYear = fromDate.split("/")[1];
//		String toMonth = toDate.split("/")[0];
//		String toYear = toDate.split("/")[1];
//		/*String claimMovementStartDate = fromYear + "-" + fromMonth + "-01";
//		String claimMovementEndDate = toYear + "-" + toMonth + "-31";*/
//		String finstartDate = fromYear + "-" + fromMonth + "-01";
//		String finEndDate = toYear + "-" + toMonth + "-31";
//
//		String queryStr = "SELECT  SUM(CLAIM_COUNT),SUM(CASE WHEN (CLM_CLAIM_TYPE in ('MUTA','PUBB',"+
//		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND category='Comprehensive' THEN CLAIM_COUNT ELSE 0 END) cat_claim_count_policy_comp,"+
//		"SUM(CASE WHEN (CLM_CLAIM_TYPE in ('MUTA','PUBB',"+
//		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' and x.CATEGORY='TP' THEN CLAIM_COUNT ELSE 0 END) cat_claim_count_policy_tp,"+
//		"SUM(CASE WHEN (CLM_CLAIM_TYPE in ('MUTA','PUBB', 'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CLAIM_COUNT ELSE 0 END) cat_claim_count_policy_others,"+
//		"SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and x.CATEGORY='Comprehensive' THEN CLAIM_COUNT ELSE 0 END) theft_claim_count_policy_comp,   SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and x.CATEGORY='TP' THEN CLAIM_COUNT ELSE 0 END) theft_claim_count_policy_tp,"+   
//		"SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CLAIM_COUNT ELSE 0 END) theft_claim_count_policy_others,"+
//		"SUM(CASE WHEN (CLM_CLAIM_TYPE not in ('MUTA','PUBB',"+
//		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND NATURE_OF_CLAIM<>'VTFO' and x.CATEGORY='Comprehensive' THEN CLAIM_COUNT ELSE 0 END) othert_claim_count_policy_comp,"+ 
//		"SUM(CASE WHEN (CLM_CLAIM_TYPE not in ('MUTA','PUBB',"+
//		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND NATURE_OF_CLAIM<>'VTFO' and x.CATEGORY='TP' THEN CLAIM_COUNT ELSE 0 END) othert_claim_count_policy_tp,"+  
//		"SUM(CASE WHEN (CLM_CLAIM_TYPE not in ('MUTA','PUBB',"+
//		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND NATURE_OF_CLAIM<>'VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CLAIM_COUNT ELSE 0 END) othert_claim_count_policy_others FROM (  SELECT  CLM_NATURE_OF_CLAIM as NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,sum(CLAIM_COUNT)CLAIM_COUNT,category FROM RSDB.RSA_KPI_FACT_CLAIMS_FINAL_CURRENT as RSA_KPI_FACT_CLAIMS_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME  AND  RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";
//
//		
//		/*if (fromYear.equals(toYear)) {
//			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//					+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//		} else {
//			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//					+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//		}*/
//		
//		/*queryStr += " WHERE CLM_MOVEMENT_DATE>='" + claimMovementStartDate + "' AND CLM_MOVEMENT_DATE<='"
//				+ claimMovementEndDate + "'";*/
//		
//		queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";
//		
//		//queryStr += " WHERE ( CSL_MVMT_MONTH between= " + fromYear +fromMonth+ " and " + toYear +toMonth+ " )";
//
//		
//		if (filterRequest != null && filterRequest.getBTypeNow() != null
//				&& !filterRequest.getBTypeNow().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
//				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
//				if (i != filterRequest.getBTypeNow().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
//		}
//		if (filterRequest != null && filterRequest.getChannelNow() != null
//				&& !filterRequest.getChannelNow().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
//				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
//				if (i != filterRequest.getChannelNow().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
//		}
//		if (filterRequest != null && filterRequest.getSubChannelNow() != null
//				&& !filterRequest.getSubChannelNow().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
//				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
//				if (i != filterRequest.getSubChannelNow().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
//		}
//		if (filterRequest != null && filterRequest.getMakeNow() != null
//				&& !filterRequest.getMakeNow().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
//				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
//				if (i != filterRequest.getMakeNow().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MAKE) in (" + vals + ")";
//		}
//		if (filterRequest != null && filterRequest.getModelGroupNow() != null
//				&& !filterRequest.getModelGroupNow().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
//				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
//				if (i != filterRequest.getModelGroupNow().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELGROUP) in (" + vals + ")";
//		}
//		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
//				&& !filterRequest.getFuelTypeNow().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
//				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
//				if (i != filterRequest.getFuelTypeNow().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and coalesce(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE,'N') in (" + vals + ")";
//		}
//		if (filterRequest != null && filterRequest.getStateGroupNow() != null
//				&& !filterRequest.getStateGroupNow().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
//				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
//				if (i != filterRequest.getStateGroupNow().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
//		}
//		if (filterRequest != null && filterRequest.getNcbNow() != null
//				&& !filterRequest.getNcbNow().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
//				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
//				if (i != filterRequest.getNcbNow().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
//		}
//
//		
//		
//		if (filterRequest != null && filterRequest.getMotorChannel() != null
//				&& !filterRequest.getMotorChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
//		}
//
//		
//		if (filterRequest != null && filterRequest.getMotorChannel() != null
//				&& !filterRequest.getMotorChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
//		}
//
//		if (filterRequest != null && filterRequest.getMotorSubChannel() != null
//				&& !filterRequest.getMotorSubChannel().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
//				vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
//				if (i != filterRequest.getMotorSubChannel().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
//		}
//
//		/*if (filterRequest != null && filterRequest.getMotorRegion() != null
//				&& !filterRequest.getMotorRegion().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
//				vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
//				if (i != filterRequest.getMotorRegion().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
//		}*/
//		
//		if (filterRequest != null && filterRequest.getMotorZone() != null
//				&& !filterRequest.getMotorZone().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
//				vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
//				if (i != filterRequest.getMotorZone().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
//		}
//		
//		if (filterRequest != null && filterRequest.getMotorCluster() != null
//				&& !filterRequest.getMotorCluster().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
//				vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
//				if (i != filterRequest.getMotorCluster().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
//		}
//
//		if (filterRequest != null && filterRequest.getMotorState() != null
//				&& !filterRequest.getMotorState().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
//				vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
//				if (i != filterRequest.getMotorState().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
//		}
//
//		if (filterRequest != null && filterRequest.getMotorCity() != null
//				&& !filterRequest.getMotorCity().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
//				vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
//				if (i != filterRequest.getMotorCity().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
//		}
//
//		if (filterRequest != null && filterRequest.getMotorBranch() != null
//				&& !filterRequest.getMotorBranch().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
//				vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
//				if (i != filterRequest.getMotorBranch().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
//		}
//		
//		if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
//				&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
//				vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
//				if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
//		}
//		
//		if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
//				&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
//				vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
//				if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
//		}
//
//		if (filterRequest != null && filterRequest.getMotorFuelType() != null
//				&& !filterRequest.getMotorFuelType().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
//				vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
//				if (i != filterRequest.getMotorFuelType().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE) in (" + vals + ")";
//		}
//		
//		if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
//				&& !filterRequest.getMotorNcbFlag().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
//				vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
//				if (i != filterRequest.getMotorNcbFlag().size() - 1) {
//					vals += ",";
//				}
//			}
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
//		}
//		
//		/*if (filterRequest != null && filterRequest.getMotorCarType() != null
//				&& !filterRequest.getMotorCarType().isEmpty()) {
//			String vals = "'HIGHEND','High End'";
//			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
//				
//				if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
//					if (i != filterRequest.getMotorNcbFlag().size() - 1) {
//						vals += ",";
//					}
//					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
//				}else{
//					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) not in (" + vals + ")";
//				}
//			
//				System.out.println("HE query------------------------------ " + queryStr);
//				
//			}
//			
//		}*/
//		
//		
//		if (filterRequest != null && filterRequest.getMotorCarType() != null
//				&& !filterRequest.getMotorCarType().isEmpty()) {
//			String vals = "'HIGHEND','High End'";
//			String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
//			int cvalcounter = 0,cvalNHEcounter = 0;
//			for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
//				
//				 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
//					 if(cvalcounter==0)
//					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
//					 cvalcounter++;
//				 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
//					if(cvalNHEcounter==0)
//					queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
//					cvalNHEcounter++;
//				 }
//			
//				System.out.println("HE query------------------------------ " + queryStr);
//				
//			}
//			
//		}
//		
//
//
//		queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,category) x";
//
//		System.out.println("queryStr------------------------------ Claim UW --" + queryStr);
//		ResultSet rs = stmt.executeQuery(queryStr);
//		System.out.println("START------------------------------ ");
//
//		// jsArray = convertToJSON(rs);
//
//		while (rs.next()) {
//
//			ClaimsCubeResponseNew res = new ClaimsCubeResponseNew();
//			res.setCatClaimCountPoliciesComprehensive(rs.getDouble(1));
//			res.setCatClaimCountPoliciesTp(rs.getDouble(2));
//			res.setCatClaimCountPoliciesOthers(rs.getDouble(3));
//			res.setTheftClaimCountPoliciesComprehensive(rs.getDouble(4));
//			res.setTheftClaimCountPoliciesTp(rs.getDouble(5));
//			res.setTheftClaimCountPoliciesOthers(rs.getDouble(6));
//			res.setOthersClaimCountPoliciesComprehensive(rs.getDouble(7));
//			res.setOthersClaimCountPoliciesTp(rs.getDouble(8));
//			res.setOthersClaimCountPoliciesOthers(rs.getDouble(9));
//			kpiResponseList.add(res);
//		}
//
//		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//	} catch (Exception e) {
//		System.out.println("kylinDataSource initialize error, ex: " + e);
//		System.out.println();
//		e.printStackTrace();
//	} finally {
//		connection.close();
//	}
//	return kpiResponseList;
//}





//@GetMapping("/getUWSingleLineCubeGicDataUpdatedNew/{claimParamType}")
//@ResponseBody
//public List<SingleLineCubeResponseNew> getUWSingleLineCubeGicDataUpdatedNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest,
//		@PathVariable(value="claimParamType") String claimParamType)
//		throws SQLException {
//	Connection connection = null;
//	double nicTp = 0;
//	List<SingleLineCubeResponseNew> kpiResponseList = new ArrayList<SingleLineCubeResponseNew>();
//	long startTime = System.currentTimeMillis();
//	try {
//		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
//		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
//
//		List<ProductMaster> productMasters = productMasterRepository.findAll();
//
//		/*String motorProductVals = "'" + productMasters.stream()
//				.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
//				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";
//
//		String healthProductVals = "'" + productMasters.stream()
//				.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
//				.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";*/
//
//		Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
//		Properties info = new Properties();
//		info.put("user", "ADMIN");
//		info.put("password", "KYLIN");
//		connection = driverManager
//				.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
//		System.out.println("Connection status -------------------------->" + connection);
//		Statement stmt = connection.createStatement();
//
//		String fromMonth = fromDate.split("/")[0];
//		String fromYear = fromDate.split("/")[1];
//		String toMonth = toDate.split("/")[0];
//		String toYear = toDate.split("/")[1];
//		String queryStr = "";
//		if(claimParamType.equals("GIC")){
//			queryStr = "SELECT SUM(csl_gic)gic,"+
//			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_comp,"+
//			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_tp,"+  
//			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_others,"+  
//			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_comp,"+  
//			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_tp,"+ 
//			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_others,"+  
//			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND CSL_NATURE_OF_CLAIM<>'VTFO' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) other_gic_od_policy_comp,"+  
//			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND CSL_NATURE_OF_CLAIM<>'VTFO' and category='TP' THEN CSL_GIC ELSE 0 END) other_gic_od_policy_tp,"+ 
//			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND CSL_NATURE_OF_CLAIM<>'VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) other_gic_od_policy_others,"+ 
//			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND  CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_comp,"+  
//			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND  CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_tp,"+ 
//			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND  CSL_CLAIM_NO LIKE 'TP%' AND coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_others,"+  
//			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_comp,"+  
//			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_tp,  SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_others,"+
//			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_od,"+
//			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_tp,"+  
//			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
//			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
//			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
//			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
//			"'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_others "+
//			"from (  SELECT   RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHECODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.csl_claim_type ,CATASTROPHIC_MASTER.CAT_TYPE   ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_NATURE_OF_CLAIM,  CSL_CLAIM_NO, category  ,SUM(CSL_GIC) CSL_GIC  FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW_CURRENT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE";
//
//		}else if(claimParamType.equals("NIC")){
//
//			//  nicTp = getNicTp( Integer.valueOf(fromMonth),  Integer.valueOf(toMonth), Integer.valueOf(fromYear) , Integer.valueOf(toYear), filterRequest);
//			
//			queryStr = "SELECT sum(csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY))nic,0 NIC_policy_comp,0 NIC_policy_tp,0 NIC_policy_others,sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive') THEN csl_gic*(1-TP_QUOTA_SHARE-TP_OBLIGATORY) ELSE 0 end) nic_tp_policy_comp,  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='TP') THEN csl_gic*(1-TP_QUOTA_SHARE-TP_OBLIGATORY) ELSE 0 end) nic_tp_policy_tp,   sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and coalesce(A.CATEGORY,'Others')='Others') THEN csl_gic*(1-TP_QUOTA_SHARE-TP_OBLIGATORY) ELSE 0 end) nic_tp_policy_others,sum(case WHEN (CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive') then csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY) else 0 end) nic_od_policy_comp,sum(case WHEN (CSL_CLAIM_NO NOT LIKE  'TP%' and category='TP') then csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY) else 0 end) nic_od_policy_tp,sum(case WHEN (CSL_CLAIM_NO NOT LIKE  'TP%' and coalesce(A.CATEGORY,'Others')='Others') then csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY) else 0 end) nic_od_policy_others FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO   FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW_CURRENT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE";
//			
//			
//					/*+ " WHERE ( CSL_MVMT_MONTH between 201804 and 201903 ) group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,  "
//					+ " (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B  "
//					+ " where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";	*/				
//			/*queryStr += "SELECT  sum(case when category='Comprehensive' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_comp,  sum(case when category='TP' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_tp,  sum(case when coalesce(A.CATEGORY,'Others')='Others' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_others,  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_comp,  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='TP') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_tp,   sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and coalesce(A.CATEGORY,'Others')='Others') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_others, 0 nic_od_policy_comp,0 nic_od_policy_tp,0 nic_od_policy_others FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO   FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL   LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE   LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE   LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE   LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE   LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE   LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE   LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY   LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE   LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE  WHERE ( CSL_MVMT_MONTH between 201804 and 201903 ) and  CSL_CLAIM_NO LIKE  'TP%' group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,   (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B   where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band"+ 
//						" union all "+
//						"SELECT  0 NIC_policy_comp,  0 NIC_policy_tp, 0 NIC_policy_others,  0 nic_tp_policy_comp,  0 nic_tp_policy_tp,   0 nic_tp_policy_others,  sum(case WHEN (category='Comprehensive') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_comp,  sum(case WHEN ( category='TP') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_tp,  sum(case WHEN (coalesce(A.CATEGORY,'Others')='Others') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_others FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO   FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL   LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE   LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE   LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE   LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE   LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE   LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE   LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY   LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE   LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE  WHERE ( CSL_MVMT_MONTH between 201804 and 201903 ) and CSL_CLAIM_NO NOT LIKE 'TP%' group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,   (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B   where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";*/
//		}
//		
//					
//
//		
//		/*if (fromYear.equals(toYear)) {
//			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//					+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//		} else {
//			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//					+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//		}*/
//		
//			//queryStr += " WHERE ( CSL_MVMT_MONTH between " + fromYear +fromMonth+ " and " + toYear +toMonth+ " )";
//		String finstartDate = fromYear + "-" + fromMonth + "-01";
//		String finEndDate = toYear + "-" + toMonth + "-31";
//		queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";
//			
//			if (filterRequest != null && filterRequest.getBTypeNow() != null
//					&& !filterRequest.getBTypeNow().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
//					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
//					if (i != filterRequest.getBTypeNow().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
//			}
//			if (filterRequest != null && filterRequest.getChannelNow() != null
//					&& !filterRequest.getChannelNow().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
//					vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
//					if (i != filterRequest.getChannelNow().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
//			}
//			if (filterRequest != null && filterRequest.getSubChannelNow() != null
//					&& !filterRequest.getSubChannelNow().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
//					vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
//					if (i != filterRequest.getSubChannelNow().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
//			}
//			if (filterRequest != null && filterRequest.getMakeNow() != null
//					&& !filterRequest.getMakeNow().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
//					vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
//					if (i != filterRequest.getMakeNow().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MAKE) in (" + vals + ")";
//			}
//			if (filterRequest != null && filterRequest.getModelGroupNow() != null
//					&& !filterRequest.getModelGroupNow().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
//					vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
//					if (i != filterRequest.getModelGroupNow().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELGROUP) in (" + vals + ")";
//			}
//			if (filterRequest != null && filterRequest.getFuelTypeNow() != null
//					&& !filterRequest.getFuelTypeNow().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
//					vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
//					if (i != filterRequest.getFuelTypeNow().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and coalesce(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE,'N') in (" + vals + ")";
//			}
//			if (filterRequest != null && filterRequest.getStateGroupNow() != null
//					&& !filterRequest.getStateGroupNow().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
//					vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
//					if (i != filterRequest.getStateGroupNow().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (" + vals + ")";
//			}
//			if (filterRequest != null && filterRequest.getNcbNow() != null
//					&& !filterRequest.getNcbNow().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
//					vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
//					if (i != filterRequest.getNcbNow().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
//			}
//			
//		
//			
//			if (filterRequest != null && filterRequest.getMotorChannel() != null
//					&& !filterRequest.getMotorChannel().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//					vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//					if (i != filterRequest.getMotorChannel().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
//			}
//
//			
//			if (filterRequest != null && filterRequest.getMotorChannel() != null
//					&& !filterRequest.getMotorChannel().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
//					vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
//					if (i != filterRequest.getMotorChannel().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
//			}
//
//			if (filterRequest != null && filterRequest.getMotorSubChannel() != null
//					&& !filterRequest.getMotorSubChannel().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
//					vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
//					if (i != filterRequest.getMotorSubChannel().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
//			}
//
//			/*if (filterRequest != null && filterRequest.getMotorRegion() != null
//					&& !filterRequest.getMotorRegion().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
//					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
//					if (i != filterRequest.getMotorRegion().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
//			}*/
//			
//			if (filterRequest != null && filterRequest.getMotorZone() != null
//					&& !filterRequest.getMotorZone().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorZone().size(); i++) {
//					vals += "'" + filterRequest.getMotorZone().get(i).trim() + "'";
//					if (i != filterRequest.getMotorZone().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(KPI_BRANCH_MASTER.ZONE) in (" + vals + ")";
//			}
//			
//			if (filterRequest != null && filterRequest.getMotorCluster() != null
//					&& !filterRequest.getMotorCluster().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorCluster().size(); i++) {
//					vals += "'" + filterRequest.getMotorCluster().get(i).trim() + "'";
//					if (i != filterRequest.getMotorCluster().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in (" + vals + ")";
//			}
//
//			if (filterRequest != null && filterRequest.getMotorState() != null
//					&& !filterRequest.getMotorState().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorState().size(); i++) {
//					vals += "'" + filterRequest.getMotorState().get(i).trim() + "'";
//					if (i != filterRequest.getMotorState().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
//			}
//
//			if (filterRequest != null && filterRequest.getMotorCity() != null
//					&& !filterRequest.getMotorCity().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorCity().size(); i++) {
//					vals += "'" + filterRequest.getMotorCity().get(i).trim() + "'";
//					if (i != filterRequest.getMotorCity().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
//			}
//
//			if (filterRequest != null && filterRequest.getMotorBranch() != null
//					&& !filterRequest.getMotorBranch().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorBranch().size(); i++) {
//					vals += "'" + filterRequest.getMotorBranch().get(i).trim() + "'";
//					if (i != filterRequest.getMotorBranch().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
//			}
//			
//			if (filterRequest != null && filterRequest.getMotorIntermediaryCode() != null
//					&& !filterRequest.getMotorIntermediaryCode().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorIntermediaryCode().size(); i++) {
//					vals += "'" + filterRequest.getMotorIntermediaryCode().get(i).trim() + "'";
//					if (i != filterRequest.getMotorIntermediaryCode().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
//			}
//			
//			if (filterRequest != null && filterRequest.getMotorIntermediaryName() != null
//					&& !filterRequest.getMotorIntermediaryName().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorIntermediaryName().size(); i++) {
//					vals += "'" + filterRequest.getMotorIntermediaryName().get(i).trim() + "'";
//					if (i != filterRequest.getMotorIntermediaryName().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in (" + vals + ")";
//			}
//
//			if (filterRequest != null && filterRequest.getMotorFuelType() != null
//					&& !filterRequest.getMotorFuelType().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
//					vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
//					if (i != filterRequest.getMotorFuelType().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.FUELTYPE) in (" + vals + ")";
//			}
//			
//			if (filterRequest != null && filterRequest.getMotorNcbFlag() != null
//					&& !filterRequest.getMotorNcbFlag().isEmpty()) {
//				String vals = "";
//				for (int i = 0; i < filterRequest.getMotorNcbFlag().size(); i++) {
//					vals += "'" + filterRequest.getMotorNcbFlag().get(i).trim() + "'";
//					if (i != filterRequest.getMotorNcbFlag().size() - 1) {
//						vals += ",";
//					}
//				}
//				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
//			}
//			
//			/*if (filterRequest != null && filterRequest.getMotorCarType() != null
//					&& !filterRequest.getMotorCarType().isEmpty()) {
//				String vals = "'HIGHEND','High End'";
//				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
//					
//					if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
//						if (i != filterRequest.getMotorNcbFlag().size() - 1) {
//							vals += ",";
//						}
//						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
//					}else{
//						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) not in (" + vals + ")";
//					}
//				
//					System.out.println("HE query------------------------------ " + queryStr);
//					
//				}
//				
//			}*/
//			
//			if (filterRequest != null && filterRequest.getMotorCarType() != null
//					&& !filterRequest.getMotorCarType().isEmpty()) {
//				String vals = "'HIGHEND','High End'";
//				String nheVals = "'Sling','OIB','OIB PS','Xcd','Others','SS PS'";
//				int cvalcounter = 0,cvalNHEcounter = 0;
//				for (int i = 0; i < filterRequest.getMotorCarType().size(); i++) {
//					
//					 if(filterRequest.getMotorCarType().get(i).trim().equals("HE")){
//						 if(cvalcounter==0)
//						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + vals + ")";
//						 cvalcounter++;
//					 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
//						if(cvalNHEcounter==0)
//						queryStr += " and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in (" + nheVals + ")";
//						cvalNHEcounter++;
//					 }
//				
//					System.out.println("HE query------------------------------ " + queryStr);
//					
//				}
//				
//			}
//	
//	if(claimParamType.equals("GIC")){
//		queryStr += " group by RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHECODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.csl_claim_type ,CATASTROPHIC_MASTER.CAT_TYPE ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_NATURE_OF_CLAIM,CSL_CLAIM_NO,category ) x";
//	}
//	else if(claimParamType.equals("NIC")){
//		queryStr +=" group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,(select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OD_OBLIGATORY) OD_OBLIGATORY,SUM(OD_QUOTA_SHARE) OD_QUOTA_SHARE,SUM(TP_OBLIGATORY) TP_OBLIGATORY,SUM(TP_QUOTA_SHARE) TP_QUOTA_SHARE from  RSA_DWH_RI_OBLIGATORY_MASTER1_NEW group by underwriting_year,XGEN_PRODUCTCODE,band) B   where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band";
//
//	}
//	
//	
//	
//		
//
//		System.out.println("queryStr------------------------------ "+ claimParamType +"///----///"+ queryStr);
//		ResultSet rs = stmt.executeQuery(queryStr);
//		System.out.println("START------------------------------ ");
//
//		// jsArray = convertToJSON(rs);
//		int count =0 ;
//		while (rs.next()) {
//
//			SingleLineCubeResponseNew res = new SingleLineCubeResponseNew();
//			if(claimParamType.equals("GIC")){
//			res.setCatGicOdComprehensive(rs.getDouble(1));
//			res.setCatGicOdTp(rs.getDouble(2));
//			res.setCatGicOdOthers(rs.getDouble(3));
//			res.setTheftGicOdComprehensive(rs.getDouble(4));
//			res.setTheftGicOdTp(rs.getDouble(5));
//			res.setTheftGicOdOthers(rs.getDouble(6));
//			res.setOthersGicOdComprehensive(rs.getDouble(7));
//			res.setOthersGicOdTp(rs.getDouble(8));
//			res.setOthersGicOdOthers(rs.getDouble(9));
//			
//			res.setCatGicTpComprehensive(rs.getDouble(10));
//			res.setCatGicTpTp(rs.getDouble(11));
//			res.setCatGicTpOthers(rs.getDouble(12));
//			res.setTheftGicTpComprehensive(rs.getDouble(13));
//			res.setTheftGicTpTp(rs.getDouble(14));
//			res.setTheftGicTpOthers(rs.getDouble(15));
//			res.setOthersGicTpComprehensive(rs.getDouble(16));
//			res.setOthersGicTpTp(rs.getDouble(17));
//			res.setOthersGicTpOthers(rs.getDouble(18));
//			}else if(claimParamType.equals("NIC")){
//				/*if(count==0){*/
//					res.setNicComprehensive(rs.getDouble(1));
//					res.setNicTp(rs.getDouble(2));
//					res.setNicOthers(rs.getDouble(3));
//					res.setNicTpComprehensive(nicTp);
//					/*below code has to  be uncommented after category implementation*/
//					/*res.setNicTpComprehensive(rs.getDouble(4));
//					res.setNicTpTp(rs.getDouble(5));
//					res.setNicTpOthers(rs.getDouble(6));*/
//				/*}if(count==1){*/
//					res.setNicOdComprehensive(rs.getDouble(7));
//					res.setNicOdTp(rs.getDouble(8));
//					res.setNicOdOthers(rs.getDouble(9));
//				count ++;
//			}
//			
//			kpiResponseList.add(res);
//		}
//
//		System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//	} catch (Exception e) {
//		System.out.println("kylinDataSource initialize error, ex: " + e);
//		System.out.println();
//		e.printStackTrace();
//	} finally {
//		connection.close();
//	}
//	return kpiResponseList;
//}


@GetMapping("/getR12GicNic")
@ResponseBody
public GicNicPsqlFunction getR12GicNic(HttpServletRequest req, UserMatrixMasterRequest filterRequest) throws SQLException {

	String queryStr="";
	try{
		System.out.println(":::::::::::::::::::::::::: TYPE filterRequest ::::::::::::::::::::"+ filterRequest.toString());
		if (filterRequest != null && filterRequest.getBTypeNow() != null && !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (BUSINESS_TYPE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getChannelNow() != null && !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (SUB_CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (MAKE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (MODELGROUP) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and upper(coalesce(FUELTYPE,'N')) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (STATE_GROUPING) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (ncb_flag) in (" + vals + ")";
		}

	System.out.println(":::::::::::::::::::::::::: TYPE QUERY ::::::::::::::::::::"+ queryStr);

	String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
	String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
	String fromMonth = fromDate.split("/")[0];
	String fromYear = fromDate.split("/")[1];
	String toMonth = toDate.split("/")[0];
	String toYear = toDate.split("/")[1];
	String date = getFinCondQueryForPsql(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));
	System.out.println(":::::::::::::::::::::::::: TYPE YEAR ::::::::::::::::::::"+ date);
	System.out.println("-----call---- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::callR12GicNic ---- Start");
	GicNicPsqlFunction result = null;
		if(filterRequest.getAddOnNew().equals("Include")){
			result = gicNicPsqlRepository.calc_new_r12_gic_nic_include(queryStr, date);
		}else if(filterRequest.getAddOnNew().equals("Exclude")){
			result = gicNicPsqlRepository.calc_new_r12_gic_nic_exclude(queryStr, date);
		}else if(filterRequest.getAddOnNew().equals("Only Addon")){
			result = gicNicPsqlRepository.calc_new_r12_gic_nic_only_addon(queryStr, date);
		}
	
	System.out.println("-----call---- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::callR12GicNic ---- Success ::::"+ result.toString());

	return result;

	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
		return null;
	}
}

@GetMapping("/getcR12Acq")
@ResponseBody
public AcqPsqlFunction getcR12Acq(HttpServletRequest req, UserMatrixMasterRequest filterRequest) throws SQLException {

	String queryStr="";

	try{
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (BUSINESS_TYPE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getChannelNow() != null && !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (SUB_CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (MAKE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (MODELGROUP) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and upper(coalesce(FUELTYPE,'N')) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (STATE_GROUPING) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (ncb_flag) in (" + vals + ")";
		}

		System.out.println(":::::::::::::::::::::::::: TYPE QUERY ::::::::::::::::::::"+ queryStr);

		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];
		String date = getFinCondQueryForPsql(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));
		System.out.println(":::::::::::::::::::::::::: TYPE YEAR ::::::::::::::::::::"+ date);
	System.out.println("-----call---- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::callR12AcqFunction ---- Start");
	AcqPsqlFunction result = null;
	// AcqPsqlFunction result = gicNicPsqlRepository.callR12AcqFunction(queryStr, date);
	if(filterRequest.getAddOnNew().equals("Include")){
		result = gicNicPsqlRepository.calc_new_r12_acq_loss_include(queryStr, date);
	}else if(filterRequest.getAddOnNew().equals("Exclude")){
		result = gicNicPsqlRepository.calc_new_r12_acq_loss_exclude(queryStr, date);
	}else if(filterRequest.getAddOnNew().equals("Only Addon")){
		result = gicNicPsqlRepository.calc_new_r12_acq_loss_only_addon(queryStr, date);
	}
	System.out.println("-----call---- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::callR12AcqFunction ---- Success ::::"+ result.toString());
	return result;
		
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
		return null;
	}
}

@GetMapping("/getR12GwpNwp")
@ResponseBody
public GwpNwpPsqlFunction getR12GwpNwp(HttpServletRequest req, UserMatrixMasterRequest filterRequest) throws SQLException {

	String queryStr="";

	try{

		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (BUSINESS_TYPE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getChannelNow() != null && !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (SUB_CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (MAKE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (MODELGROUP) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and upper(coalesce(FUELTYPE,'N')) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (STATE_GROUPING) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (ncb_flag) in (" + vals + ")";
		}

	System.out.println(":::::::::::::::::::::::::: TYPE QUERY ::::::::::::::::::::"+ queryStr);

	String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
	String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
	String fromMonth = fromDate.split("/")[0];
	String fromYear = fromDate.split("/")[1];
	String toMonth = toDate.split("/")[0];
	String toYear = toDate.split("/")[1];
	String date = getFinCondQueryForPsql(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));
	System.out.println(":::::::::::::::::::::::::: TYPE YEAR ::::::::::::::::::::"+ date);
	System.out.println("-----call---- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::callR12GwpNwp ---- Start");
	// GwpNwpPsqlFunction result = gicNicPsqlRepository.callR12GwpNwp(queryStr, date);
	GwpNwpPsqlFunction result = null;
		if(filterRequest.getAddOnNew().equals("Include")){
			result = gicNicPsqlRepository.calc_new_r12_gwp_nwp_include(queryStr, date);
		}else if(filterRequest.getAddOnNew().equals("Exclude")){
			result = gicNicPsqlRepository.calc_new_r12_gwp_nwp_exclude(queryStr, date);
		}else if(filterRequest.getAddOnNew().equals("Only Addon")){
			result = gicNicPsqlRepository.calc_new_r12_gwp_nwp_only_addon(queryStr, date);
		}
	System.out.println("-----call---- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::callR12GwpNwp ---- Success ::::"+ result.toString());
	return result;
		
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
		return null;
	}
}

@GetMapping("/getR12GepNep")
@ResponseBody
public GepNepPsqlFunctions getR12GepNep(HttpServletRequest req, UserMatrixMasterRequest filterRequest) throws SQLException {

	String queryStr="";
	try{
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (BUSINESS_TYPE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getChannelNow() != null && !filterRequest.getChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getChannelNow().size(); i++) {
				vals += "'" + filterRequest.getChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getSubChannelNow() != null
				&& !filterRequest.getSubChannelNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getSubChannelNow().size(); i++) {
				vals += "'" + filterRequest.getSubChannelNow().get(i).trim() + "'";
				if (i != filterRequest.getSubChannelNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (SUB_CHANNEL) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getMakeNow() != null
				&& !filterRequest.getMakeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getMakeNow().size(); i++) {
				vals += "'" + filterRequest.getMakeNow().get(i).trim() + "'";
				if (i != filterRequest.getMakeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (MAKE) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getModelGroupNow() != null
				&& !filterRequest.getModelGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getModelGroupNow().size(); i++) {
				vals += "'" + filterRequest.getModelGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getModelGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (MODELGROUP) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getFuelTypeNow() != null
				&& !filterRequest.getFuelTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getFuelTypeNow().size(); i++) {
				vals += "'" + filterRequest.getFuelTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getFuelTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and upper(coalesce(FUELTYPE,'N')) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getStateGroupNow() != null
				&& !filterRequest.getStateGroupNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getStateGroupNow().size(); i++) {
				vals += "'" + filterRequest.getStateGroupNow().get(i).trim() + "'";
				if (i != filterRequest.getStateGroupNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (STATE_GROUPING) in (" + vals + ")";
		}

		if (filterRequest != null && filterRequest.getNcbNow() != null
				&& !filterRequest.getNcbNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getNcbNow().size(); i++) {
				vals += "'" + filterRequest.getNcbNow().get(i).trim() + "'";
				if (i != filterRequest.getNcbNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and (ncb_flag) in (" + vals + ")";
		}

		System.out.println(":::::::::::::::::::::::::: TYPE QUERY ::::::::::::::::::::"+ queryStr);

		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
		String fromMonth = fromDate.split("/")[0];
		String fromYear = fromDate.split("/")[1];
		String toMonth = toDate.split("/")[0];
		String toYear = toDate.split("/")[1];
		String date = getFinCondQueryForPsql(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));
		System.out.println(":::::::::::::::::::::::::: TYPE YEAR ::::::::::::::::::::"+ date);
	System.out.println("-----call---- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::callR12GepNep ---- Start");
	// GepNepPsqlFunctions result = gicNicPsqlRepository.callR12GepNep(queryStr, date);
	GepNepPsqlFunctions result = null;
		if(filterRequest.getAddOnNew().equals("Include")){
			result = gicNicPsqlRepository.calc_new_r12_gep_nep_include(queryStr, date);
		}else if(filterRequest.getAddOnNew().equals("Exclude")){
			result = gicNicPsqlRepository.calc_new_r12_gep_nep_exclude(queryStr, date);
		}else if(filterRequest.getAddOnNew().equals("Only Addon")){
			result = gicNicPsqlRepository.calc_new_r12_gep_nep_only_addon(queryStr, date);
		}
	System.out.println("-----call---- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::callR12GepNep ---- Success ::::"+ result.toString());
	return result;
		
	} catch (Exception e) {
		System.out.println("kylinDataSource initialize error, ex: " + e);
		System.out.println();
		e.printStackTrace();
		return null;
	}
}

}