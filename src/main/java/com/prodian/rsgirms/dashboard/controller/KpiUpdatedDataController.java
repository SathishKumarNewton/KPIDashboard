
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
						+ " SUM(INS_GWP) GWP FROM RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_CURRENT a LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW ON a.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  left join RSA_DWH_CITY_GROUPING_MASTER_FINAL b on RSA_DWH_CITY_MASTER_NOW.CITYCODE=b.citycode    left join RSA_DWH_MODEL_MASTER_CURRENT c on  a.modelcode=c.model_code  GROUP BY inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end,HIGH_END,coalesce(c.fueltypE,a.fueltypE),NCB_FLAG ,b.cityname,statename) X   group by oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END),upper(fueltype),coalesce(NCB_FLAG,'N'),coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS') ";
		
		/*queryStr = " SELECT oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END) HIGHEND,upper(fueltype),coalesce(NCB_FLAG,'N') NCB_FL,coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS'),SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(true,false)+"' and   substring(inception_date,1,10) <= DATE '"+getCustomLastDate(true,false)+"' THEN (GWP) ELSE 0.0 END) CM_GWP,   SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(false,true)+"' and    substring(inception_date,1,10) <= DATE '"+getCustomLastDate(false,true)+"' THEN (GWP) ELSE 0.0 END) PM_GWP   FROM( "+
				   " SELECT inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end as oem_non_oem, MM_MODELCLASSIFICATION as HIGH_END ,coalesce(MM_FUELTYPE,PPC_FUELTYPE) as fueltype ,NCB_FLAG,cityname,statename,   SUM(COVERAGE_PREIMUM) GWP FROM RSA_DWH_OEM_CLASIFICATION_FACT  GROUP BY inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end, MM_MODELCLASSIFICATION,coalesce(MM_FUELTYPE,PPC_FUELTYPE),NCB_FLAG ,cityname,statename "+ 
				   " ) X   group by oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END),upper(fueltype),coalesce(NCB_FLAG,'N'),coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS') ";*/ 
		
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
	
	
	@GetMapping("/getPolicyCubeDataUpdatedNew")
	@ResponseBody
	public List<PolicyCubeResponseNew> getPolicyCubeDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
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


			String queryStr = "select "+
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
								" ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "; 

			
			/*if (fromYear.equals(toYear)) {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			} else {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			}*/

			// New Query Changes
//			if (fromYear.equals(toYear)) {
//				queryStr += " WHERE (( FINANCIAL_YEAR='" + fromYear + "''))";
//			} else {
//				queryStr += " WHERE (( FINANCIAL_YEAR='" + toYear + "' ))";
//			}

			/*String finstartDate = fromYear + "-" + fromMonth + "-01";
			String finEndDate = toYear + "-" + toMonth + "-01";*/
			
			//queryStr += " WHERE fin_date >='"+finstartDate+"' and fin_date <='"+finEndDate+"' ";
			queryStr += " WHERE ";
			queryStr += getFinCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));
			
			
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
	
	
	@GetMapping("/getInsCubeDataUpdatedNew")
	@ResponseBody
	public List<InsCubeResponseNew> getInsCubeDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
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

			
			String queryStr = "SELECT "
					+"SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_others,  SUM(case when aa.CATEGORY='Comprehensive' THEN ((INS_GWP *(1-od_quota_share-od_obligatory))+(INS_GWP*od_quota_share*od_RI_COMMISSION)) ELSE 0 END) as INS_NWP_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN ((INS_GWP *(1-od_quota_share-od_obligatory))+(INS_GWP*od_quota_share*od_RI_COMMISSION)) ELSE 0 END) as INS_NWP_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN ((INS_GWP *(1-od_quota_share-od_obligatory))+(INS_GWP*od_quota_share*od_RI_COMMISSION)) ELSE 0 END) as INS_NWP_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_TP,\r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_TP *(1-od_quota_share-od_obligatory))+(INS_GWP_TP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_TP *(1-od_quota_share-od_obligatory))+(INS_GWP_TP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_TP *(1-od_quota_share-od_obligatory))+(INS_GWP_TP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_COMP,  SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_TP,  SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_NCB_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_NCB *(1-od_quota_share-od_obligatory))+(INS_GWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_COMP, \r\n"
					+ "SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_TP, \r\n"
					+ "SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_others, \r\n"
					+ "SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_COMP,  SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_TP,  SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_others  FROM ( SELECT  SUM(INS_GWP) as INS_GWP  ,SUM(INS_GWP_OD) as INS_GWP_OD  ,SUM(INS_GWP_TP) as INS_GWP_TP  ,SUM(INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD  ,SUM(INS_NWP) as INS_NWP  ,SUM(INS_NWP_OD) as INS_NWP_OD  ,SUM(INS_NWP_TP) as INS_NWP_TP  ,SUM(INS_NWP_DISCOUNT_OD) as INS_NWP_DISCOUNT_OD  ,SUM(INS_GWP_DEP) as INS_GWP_DEP  ,SUM(INS_GWP_NCB) as INS_GWP_NCB  ,SUM(INS_GWP_OTHERADDON) as INS_GWP_OTHERADDON  ,SUM(INS_NWP_DEP) as INS_NWP_DEP  ,SUM(INS_NWP_NCB) as INS_NWP_NCB  ,SUM(INS_NWP_OTHERADDON) as INS_NWP_OTHERADDON  , CATEGORY, uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code  FROM RSDB.RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_CURRENT as RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";
			
			/*if (fromYear.equals(toYear)) {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			} else {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			}*/
			
			String finstartDate = fromYear + "-" + fromMonth + "-01";
			String finEndDate = toYear + "-" + toMonth + "-31";
			
			queryStr += " WHERE  ";
			// queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+ "'";
			queryStr += getFinCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));

			
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
			
			queryStr += "group by category,uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code ) aa, (select r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE,sum(r.OD_OBLIGATORY) OD_OBLIGATORY,sum(r.OD_QUOTA_SHARE) OD_QUOTA_SHARE,sum(OD_RI_COMMISSION) OD_RI_COMMISSION,sum(r.TP_OBLIGATORY) TP_OBLIGATORY,sum(r.TP_QUOTA_SHARE) TP_QUOTA_SHARE,sum(TP_RI_COMMISSION) TP_RI_COMMISSION   from rsa_dwh_ri_obligatory_master1_new r group by r.UNDERWRITING_YEAR,r.XGEN_PRODUCTCODE) bb "
			+"where aa.uw_year = bb.underwriting_year "
			+"and aa.product_Code = bb.xgen_productcode";
			
			
			
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

	
	
	@GetMapping("/getClaimsCubeDataUpdatedNew")
	@ResponseBody
	public List<ClaimsCubeResponseNew> getClaimsCubeDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
			throws SQLException {
		Connection connection = null;
		List<ClaimsCubeResponseNew> kpiResponseList = new ArrayList<ClaimsCubeResponseNew>();
		long startTime = System.currentTimeMillis();
		try {
//			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
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
			String claimMovementStartDate = fromYear + "-" + fromMonth + "-01";
			String claimMovementEndDate = toYear + "-" + toMonth + "-31";

			String queryStr = "SELECT "+
								" SUM(CASE WHEN (CLM_CLAIM_TYPE in ('MUTA','PUBB',\r\n"
								+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',\r\n"
								+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',\r\n"
								+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',\r\n"
								+ "'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND category='Comprehensive' THEN CLAIM_COUNT ELSE 0 END) cat_claim_count_policy_comp, "+
								" SUM(CASE WHEN (CLM_CLAIM_TYPE in ('MUTA','PUBB',\r\n"
								+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',\r\n"
								+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',\r\n"
								+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',\r\n"
								+ "'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' and x.CATEGORY='TP' THEN CLAIM_COUNT ELSE 0 END) cat_claim_count_policy_tp, "+
								" SUM(CASE WHEN (CLM_CLAIM_TYPE in ('MUTA','PUBB', 'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',\r\n"
								+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',\r\n"
								+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',\r\n"
								+ "'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CLAIM_COUNT ELSE 0 END) cat_claim_count_policy_others, "+
								"  SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and x.CATEGORY='Comprehensive' THEN CLAIM_COUNT ELSE 0 END) theft_claim_count_policy_comp, "+
								"  SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and x.CATEGORY='TP' THEN CLAIM_COUNT ELSE 0 END) theft_claim_count_policy_tp, "+
								"  SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CLAIM_COUNT ELSE 0 END) theft_claim_count_policy_others, "+
								" SUM(CASE WHEN (CLM_CLAIM_TYPE not in ('MUTA','PUBB',\r\n"
								+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',\r\n"
								+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',\r\n"
								+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',\r\n"
								+ "'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND NATURE_OF_CLAIM<>'VTFO' and x.CATEGORY='Comprehensive' THEN CLAIM_COUNT ELSE 0 END)  othert_claim_count_policy_comp, "+
								" SUM(CASE WHEN (CLM_CLAIM_TYPE not in ('MUTA','PUBB',\r\n"
								+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',\r\n"
								+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',\r\n"
								+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',\r\n"
								+ "'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND NATURE_OF_CLAIM<>'VTFO' and x.CATEGORY='TP' THEN CLAIM_COUNT ELSE 0 END)  othert_claim_count_policy_tp, "+
								" SUM(CASE WHEN (CLM_CLAIM_TYPE not in ('MUTA','PUBB',\r\n"
								+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',\r\n"
								+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',\r\n"
								+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',\r\n"
								+ "'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND NATURE_OF_CLAIM<>'VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CLAIM_COUNT ELSE 0 END)  othert_claim_count_policy_others "+
								" FROM( "+
								" SELECT "+
								"CLM_NATURE_OF_CLAIM as NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,sum(CLAIM_COUNT)CLAIM_COUNT,category FROM RSDB.RSA_KPI_FACT_CLAIMS_FINAL_CURRENT as RSA_KPI_FACT_CLAIMS_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME  AND  RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";

			
			/*if (fromYear.equals(toYear)) {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			} else {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			}*/
			
			queryStr += " WHERE CLM_MOVEMENT_DATE>='" + claimMovementStartDate + "' AND CLM_MOVEMENT_DATE<='"
					+ claimMovementEndDate + "'";
			
			//queryStr += " WHERE ( CSL_MVMT_MONTH between= " + fromYear +fromMonth+ " and " + toYear +toMonth+ " )";

			
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
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
			
	

			queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,category) x";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				ClaimsCubeResponseNew res = new ClaimsCubeResponseNew();
				res.setCatClaimCountPoliciesComprehensive(rs.getDouble(1));
				res.setCatClaimCountPoliciesTp(rs.getDouble(2));
				res.setCatClaimCountPoliciesOthers(rs.getDouble(3));
				res.setTheftClaimCountPoliciesComprehensive(rs.getDouble(4));
				res.setTheftClaimCountPoliciesTp(rs.getDouble(5));
				res.setTheftClaimCountPoliciesOthers(rs.getDouble(6));
				res.setOthersClaimCountPoliciesComprehensive(rs.getDouble(7));
				res.setOthersClaimCountPoliciesTp(rs.getDouble(8));
				res.setOthersClaimCountPoliciesOthers(rs.getDouble(9));
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





@GetMapping("/getSingleLineCubeGicDataUpdatedNew/{claimParamType}")
@ResponseBody
public List<SingleLineCubeResponseNew> getSingleLineCubeDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest,
		@PathVariable(value="claimParamType") String claimParamType)
		throws SQLException {
	Connection connection = null;
	double nicTp = 0;
	List<SingleLineCubeResponseNew> kpiResponseList = new ArrayList<SingleLineCubeResponseNew>();
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
		String queryStr = "";
		// New Query Changed
		if(claimParamType.equals("GIC")){
			queryStr = "SELECT "+
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_comp,"+ 
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_tp,"+
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_others,"+  
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_comp,  "+
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_tp,"+  
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_others,"+  
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND CSL_NATURE_OF_CLAIM<>'VTFO' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) other_gic_od_policy_comp,"+  
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND CSL_NATURE_OF_CLAIM<>'VTFO' and category='TP' THEN CSL_GIC ELSE 0 END) other_gic_od_policy_tp,"+  
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND CSL_NATURE_OF_CLAIM<>'VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) other_gic_od_policy_others,"+  
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND  CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_comp,"+
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND  CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_tp,  "+
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND  CSL_CLAIM_NO LIKE 'TP%' AND coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_others, "+ 
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_comp,"+ 
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_tp, " + 
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_others,"+
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_od,"+
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_tp,  "+
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_others  "+
			"from (  SELECT   RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHECODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.csl_claim_type ,CATASTROPHIC_MASTER.CAT_TYPE   ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_NATURE_OF_CLAIM,  CSL_CLAIM_NO, category  ,SUM(CSL_GIC) CSL_GIC  FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW_CURRENT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE";

		}else if(claimParamType.equals("NIC")){
			/*queryStr	="SELECT sum(NIC_policy_comp),sum(NIC_policy_tp),sum(NIC_policy_others),  sum(nic_tp_policy_comp),sum(nic_tp_policy_tp),"
							+ " sum(nic_tp_policy_others), sum(nic_od_policy_comp),sum(nic_od_policy_tp),sum(nic_od_policy_others) " 
							+ " FROM ( SELECT  csl_gic,CSL_CLAIM_NO,CSL_MVMT_MONTH, (case when category='Comprehensive' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_comp, "
							+ " (case when category='TP' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_tp, "
									+ " (case when coalesce(A.CATEGORY,'Others')='Others' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_others, "
									+ " (CASE WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_comp, " 
									+ " (CASE WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='TP') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_tp,  "
									+ " (CASE WHEN (CSL_CLAIM_NO LIKE 'TP%' and coalesce(A.CATEGORY,'Others')='Others') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_others, "
									+ " (CASE WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive') then 0 else csl_gic*(1-QUOTA_SHARE-OBLIGATORY) end) nic_od_policy_comp, "
									+ " (CASE WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='TP') then 0 else csl_gic*(1-QUOTA_SHARE-OBLIGATORY) end) nic_od_policy_tp, "
									+ " (CASE WHEN (CSL_CLAIM_NO LIKE 'TP%' and coalesce(A.CATEGORY,'Others')='Others') then 0 else csl_gic*(1-QUOTA_SHARE-OBLIGATORY) end) nic_od_policy_others "
									+ " FROM ( SELECT  sum(csl_gic) csl_gic,CSL_CLAIM_NO,CSL_MVMT_MONTH,category,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE' BAND  "
									+ " FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL   "
									+ " LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "  
									+ " LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE   "
									+ " LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE   "
									+ " LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE   "
									+ " LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE "  
									+ " LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE   "
									+ " LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  "
									+ " LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY "  
									+ " LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE   "
									+ " LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "  
									+ " LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  "
									+ " LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "  
									+ " LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE" ;*/

			 nicTp = getNicTp( Integer.valueOf(fromMonth),  Integer.valueOf(toMonth), Integer.valueOf(fromYear) , Integer.valueOf(toYear), filterRequest);
			// New Query Changed
			queryStr	="SELECT  "
					+ " 0 NIC_policy_comp,"
					+ "  0 NIC_policy_tp,"
					+ "  0 NIC_policy_others,"
					+ "  sum(case WHEN (CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive') THEN csl_gic*(1-TP_QUOTA_SHARE-TP_OBLIGATORY) ELSE 0 end) nic_tp_policy_comp,"
					+ "  sum(case WHEN (CSL_CLAIM_NO NOT LIKE 'TP%' and category='TP') THEN csl_gic*(1-TP_QUOTA_SHARE-TP_OBLIGATORY) ELSE 0 end) nic_tp_policy_tp,"
					+ "   sum(case WHEN (CSL_CLAIM_NO NOT LIKE 'TP%' and coalesce(A.CATEGORY,'Others')='Others') THEN csl_gic*(1-TP_QUOTA_SHARE-TP_OBLIGATORY) ELSE 0 end) nic_tp_policy_others,"
					+ "  sum(case WHEN (CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive') then csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY) else 0 end) nic_od_policy_comp,"
					+ "  sum(case WHEN (CSL_CLAIM_NO NOT LIKE  'TP%' and category='TP') then csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY) else 0 end) nic_od_policy_tp,"
					+ "  sum(case WHEN (CSL_CLAIM_NO NOT LIKE  'TP%' and coalesce(A.CATEGORY,'Others')='Others') then csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY) else 0 end) nic_od_policy_others"
					+ " FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO  " +
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW_CURRENT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW "+
					" LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "+
					" LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "+
					" LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "+
					" LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "+
					" LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "+
					" LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "+
					" LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY "+
					" LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE "+
					" LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "+
					" LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "+
					" LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME "+
					" LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE "+
					" LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL "+
					" ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "+
					" LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE ";
			
			
					/*+ " WHERE ( CSL_MVMT_MONTH between 201804 and 201903 ) group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,  "
					+ " (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B  "
					+ " where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";	*/				
			/*queryStr += "SELECT  sum(case when category='Comprehensive' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_comp,  sum(case when category='TP' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_tp,  sum(case when coalesce(A.CATEGORY,'Others')='Others' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_others,  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_comp,  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='TP') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_tp,   sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and coalesce(A.CATEGORY,'Others')='Others') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_others, 0 nic_od_policy_comp,0 nic_od_policy_tp,0 nic_od_policy_others FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO   FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL   LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE   LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE   LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE   LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE   LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE   LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE   LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY   LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE   LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE  WHERE ( CSL_MVMT_MONTH between 201804 and 201903 ) and  CSL_CLAIM_NO LIKE  'TP%' group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,   (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B   where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band"+ 
						" union all "+
						"SELECT  0 NIC_policy_comp,  0 NIC_policy_tp, 0 NIC_policy_others,  0 nic_tp_policy_comp,  0 nic_tp_policy_tp,   0 nic_tp_policy_others,  sum(case WHEN (category='Comprehensive') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_comp,  sum(case WHEN ( category='TP') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_tp,  sum(case WHEN (coalesce(A.CATEGORY,'Others')='Others') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_others FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO   FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL   LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE   LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE   LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE   LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE   LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE   LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE   LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY   LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE   LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE  WHERE ( CSL_MVMT_MONTH between 201804 and 201903 ) and CSL_CLAIM_NO NOT LIKE 'TP%' group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,   (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B   where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";*/
		}
		
					

		
		/*if (fromYear.equals(toYear)) {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		} else {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		}*/
		
			queryStr += " WHERE ( CSL_MVMT_MONTH between " + fromYear +fromMonth+ " and " + toYear +toMonth+ " )";

			
			if (filterRequest != null && filterRequest.getBTypeNow() != null
					&& !filterRequest.getBTypeNow().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
					vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
					if (i != filterRequest.getBTypeNow().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
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
	
	if(claimParamType.equals("GIC")){
		queryStr += " group by RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHECODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.csl_claim_type ,CATASTROPHIC_MASTER.CAT_TYPE ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_NATURE_OF_CLAIM,CSL_CLAIM_NO,category ) x";
	}
	else if(claimParamType.equals("NIC")){
		/*queryStr +=" GROUP by   "+
				" uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH ) A , "+  
				" (select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 "+  
				" group by underwriting_year,XGEN_PRODUCTCODE,band) B   "+
				" where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band "+   
				" ) ";*/
		/*queryStr +=" GROUP by   "+
				" uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH,category ) A , "+  
				" (select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 "+  
				" group by underwriting_year,XGEN_PRODUCTCODE,band) B   "+
				" where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band "+   
				" ) ";*/
		queryStr +=" group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,  "
		+ " (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OD_OBLIGATORY) OD_OBLIGATORY,SUM(OD_QUOTA_SHARE) OD_QUOTA_SHARE,SUM(TP_OBLIGATORY) TP_OBLIGATORY,SUM(TP_QUOTA_SHARE) TP_QUOTA_SHARE from "
		+ " RSA_DWH_RI_OBLIGATORY_MASTER1_NEW group by underwriting_year,XGEN_PRODUCTCODE,band) B  "
		+ " where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";	
	}
	
	
	
		

		System.out.println("queryStr------------------------------ " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");

		// jsArray = convertToJSON(rs);
		int count =0 ;
		while (rs.next()) {

			SingleLineCubeResponseNew res = new SingleLineCubeResponseNew();
			if(claimParamType.equals("GIC")){
			res.setCatGicOdComprehensive(rs.getDouble(1));
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
			res.setOthersGicTpOthers(rs.getDouble(18));
			}else if(claimParamType.equals("NIC")){
				/*if(count==0){*/
					res.setNicComprehensive(rs.getDouble(1));
					res.setNicTp(rs.getDouble(2));
					res.setNicOthers(rs.getDouble(3));
					res.setNicTpComprehensive(nicTp);
					/*below code has to  be uncommented after category implementation*/
					/*res.setNicTpComprehensive(rs.getDouble(4));
					res.setNicTpTp(rs.getDouble(5));
					res.setNicTpOthers(rs.getDouble(6));*/
				/*}if(count==1){*/
					res.setNicOdComprehensive(rs.getDouble(7));
					res.setNicOdTp(rs.getDouble(8));
					res.setNicOdOthers(rs.getDouble(9));
				count ++;
			}
			
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

public List<String> getgepUWR12SevGicMeasures(){
	
	List<String> list = new ArrayList<>();
	list.add("SEV_CAT_UW_R12");
	list.add("SEV_THEFT_UW_R12");
	list.add("SEV_OTHERS_UW_R12");
	list.add("GIC_CAT_OD_UW_R12");
	list.add("GIC_THEFT_OD_UW_R12");
	list.add("GIC_OTHERS_OD_UW_R12");
	/*list.add("GIC_CAT_DEP_UW_R12");
	list.add("GIC_THEFT_DEP_UW_R12");
	list.add("GIC_OTHERS_DEP_UW_R12");
	list.add("GIC_CAT_NCB_UW_R12");
	list.add("GIC_THEFT_NCB_UW_R12");
	list.add("GIC_OTHERS_NCB_UW_R12");
	list.add("GIC_CAT_OTHERADDON_UW_R12");
	list.add("GIC_THEFT_OTHERADDON_UW_R12");
	list.add("GIC_OTHERS_OTHERADDON_UW_R12");*/
	
	return list;
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



public double getNicTp(Integer fromMonth, Integer toMonth,Integer fromYear , Integer toYear,UserMatrixMasterRequest filterRequest) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{

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
	
	
	String queryStr= "select sum(gic_tp*(1-TP_QUOTA_SHARE-TP_OBLIGATORY)) from (select ",
			monthPrefix="",year="",measure="gic_tp"; int counter = 0, measureCount = 0;
	
	
	
	//for(String measure : measureList){
		counter = 0;
		List<String> prefixArr = getColumnPrefixWithYear(fromMonth, toMonth, fromYear , toYear);
		if(measureCount>0){
			queryStr += ",";
		}
		queryStr +=" sum(";
		for(String prefix : prefixArr){
		if(counter>0)
			queryStr += "+";
		monthPrefix = prefix.split("@@")[0];
		year = prefix.split("@@")[1];
		queryStr += "(case when gep_year='"+year+"' then "+monthPrefix+measure+" else 0 end)";
		counter++;
		}
		queryStr+=") gic_tp,uw_year,GEP_POLICY_FACT_DENORMAL.PRODUCT_CODE,'NONE' band";
		measureCount++;
	//}
		System.out.println("nic tp select------------------------------ " + queryStr);
		
		queryStr += " FROM RSDB.GEP_POLICY_GEP_MONTH_ON_COLUMN as GEP_POLICY_FACT_DENORMAL LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER ON GEP_POLICY_FACT_DENORMAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+
				  " ON GEP_POLICY_FACT_DENORMAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND GEP_POLICY_FACT_DENORMAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER ON GEP_POLICY_FACT_DENORMAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  "+
				  "	LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER ON GEP_POLICY_FACT_DENORMAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+
				  " ON GEP_POLICY_FACT_DENORMAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE";
		
		
		if (fromYear.equals(toYear)) {
			if (fromMonth.equals(toMonth)) {
				queryStr += " where ( gep_year= " + fromYear + " )";
			} else {
				queryStr += " WHERE (( gep_year=" + fromYear + " ))";
			}
		} else {
			queryStr += " WHERE (( gep_year=" + fromYear + " ) or ( gep_year="
					+ toYear + " ))";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.BUSINESS_TYPE) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.CHANNEL) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.SUB_CHANNEL) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.MAKE) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.MODELGROUP) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.FUEL_TYPE) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.STATE_GROUPING) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.NCB) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.CHANNEL) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.SUB_CHANNEL) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.BRANCH_CODE) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.AGENT_CODE) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.FUEL_TYPE) in (" + vals + ")";
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
			queryStr += " and TRIM(GEP_POLICY_FACT_DENORMAL.NCB) in (" + vals + ")";
		}
		
		queryStr +=" group by uw_year,PRODUCT_CODE,'NONE') A ,  "
				+ " (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OD_OBLIGATORY) OD_OBLIGATORY,SUM(OD_QUOTA_SHARE) OD_QUOTA_SHARE,SUM(TP_OBLIGATORY) TP_OBLIGATORY,SUM(TP_QUOTA_SHARE) TP_QUOTA_SHARE from "
				+ " RSA_DWH_RI_OBLIGATORY_MASTER1_NEW group by underwriting_year,XGEN_PRODUCTCODE,band) B  "
				+ " where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";
		
		
		
		
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



@GetMapping("/getUWClaimsCubeDataUpdatedNew")
@ResponseBody
public List<ClaimsCubeResponseNew> getUWClaimsCubeDataUpdatedNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
		throws SQLException {
	Connection connection = null;
	List<ClaimsCubeResponseNew> kpiResponseList = new ArrayList<ClaimsCubeResponseNew>();
	long startTime = System.currentTimeMillis();
	try {
//		String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//		String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
		String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
		String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
		
		//List<ProductMaster> productMasters = productMasterRepository.findAll();

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
		/*String claimMovementStartDate = fromYear + "-" + fromMonth + "-01";
		String claimMovementEndDate = toYear + "-" + toMonth + "-31";*/
		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";

		String queryStr = "SELECT  SUM(CLAIM_COUNT),SUM(CASE WHEN (CLM_CLAIM_TYPE in ('MUTA','PUBB',"+
		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND category='Comprehensive' THEN CLAIM_COUNT ELSE 0 END) cat_claim_count_policy_comp,"+
		"SUM(CASE WHEN (CLM_CLAIM_TYPE in ('MUTA','PUBB',"+
		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' and x.CATEGORY='TP' THEN CLAIM_COUNT ELSE 0 END) cat_claim_count_policy_tp,"+
		"SUM(CASE WHEN (CLM_CLAIM_TYPE in ('MUTA','PUBB', 'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CLAIM_COUNT ELSE 0 END) cat_claim_count_policy_others,"+
		"SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and x.CATEGORY='Comprehensive' THEN CLAIM_COUNT ELSE 0 END) theft_claim_count_policy_comp,   SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and x.CATEGORY='TP' THEN CLAIM_COUNT ELSE 0 END) theft_claim_count_policy_tp,"+   
		"SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CLAIM_COUNT ELSE 0 END) theft_claim_count_policy_others,"+
		"SUM(CASE WHEN (CLM_CLAIM_TYPE not in ('MUTA','PUBB',"+
		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND NATURE_OF_CLAIM<>'VTFO' and x.CATEGORY='Comprehensive' THEN CLAIM_COUNT ELSE 0 END) othert_claim_count_policy_comp,"+ 
		"SUM(CASE WHEN (CLM_CLAIM_TYPE not in ('MUTA','PUBB',"+
		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND NATURE_OF_CLAIM<>'VTFO' and x.CATEGORY='TP' THEN CLAIM_COUNT ELSE 0 END) othert_claim_count_policy_tp,"+  
		"SUM(CASE WHEN (CLM_CLAIM_TYPE not in ('MUTA','PUBB',"+
		"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
		"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
		"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
		"'APLV')) AND CLM_CLAIM_NO NOT LIKE 'TP%' AND NATURE_OF_CLAIM<>'VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CLAIM_COUNT ELSE 0 END) othert_claim_count_policy_others FROM (  SELECT  CLM_NATURE_OF_CLAIM as NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,sum(CLAIM_COUNT)CLAIM_COUNT,category FROM RSDB.RSA_KPI_FACT_CLAIMS_FINAL_CURRENT as RSA_KPI_FACT_CLAIMS_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME  AND  RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";

		
		/*if (fromYear.equals(toYear)) {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		} else {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		}*/
		
		/*queryStr += " WHERE CLM_MOVEMENT_DATE>='" + claimMovementStartDate + "' AND CLM_MOVEMENT_DATE<='"
				+ claimMovementEndDate + "'";*/
		
		queryStr += " WHERE SUBSTRING(inception_date,1,10) >='"+finstartDate+"' and SUBSTRING(inception_date,1,10) <='"+finEndDate+"' ";
		
		//queryStr += " WHERE ( CSL_MVMT_MONTH between= " + fromYear +fromMonth+ " and " + toYear +toMonth+ " )";

		
		if (filterRequest != null && filterRequest.getBTypeNow() != null
				&& !filterRequest.getBTypeNow().isEmpty()) {
			String vals = "";
			for (int i = 0; i < filterRequest.getBTypeNow().size(); i++) {
				vals += "'" + filterRequest.getBTypeNow().get(i).trim() + "'";
				if (i != filterRequest.getBTypeNow().size() - 1) {
					vals += ",";
				}
			}
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
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
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
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
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
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
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
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
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
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
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
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
			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
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
		


		queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CLAIM_TYPE,CLM_CLAIM_NO,category) x";

		System.out.println("queryStr------------------------------ Claim UW --" + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");

		// jsArray = convertToJSON(rs);

		while (rs.next()) {

			ClaimsCubeResponseNew res = new ClaimsCubeResponseNew();
			res.setCatClaimCountPoliciesComprehensive(rs.getDouble(1));
			res.setCatClaimCountPoliciesTp(rs.getDouble(2));
			res.setCatClaimCountPoliciesOthers(rs.getDouble(3));
			res.setTheftClaimCountPoliciesComprehensive(rs.getDouble(4));
			res.setTheftClaimCountPoliciesTp(rs.getDouble(5));
			res.setTheftClaimCountPoliciesOthers(rs.getDouble(6));
			res.setOthersClaimCountPoliciesComprehensive(rs.getDouble(7));
			res.setOthersClaimCountPoliciesTp(rs.getDouble(8));
			res.setOthersClaimCountPoliciesOthers(rs.getDouble(9));
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





@GetMapping("/getUWSingleLineCubeGicDataUpdatedNew/{claimParamType}")
@ResponseBody
public List<SingleLineCubeResponseNew> getUWSingleLineCubeGicDataUpdatedNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest,
		@PathVariable(value="claimParamType") String claimParamType)
		throws SQLException {
	Connection connection = null;
	double nicTp = 0;
	List<SingleLineCubeResponseNew> kpiResponseList = new ArrayList<SingleLineCubeResponseNew>();
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
		if(claimParamType.equals("GIC")){
			queryStr = "SELECT SUM(csl_gic)gic,"+
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_comp,"+
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_tp,"+  
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_others,"+  
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_comp,"+  
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_tp,"+ 
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_others,"+  
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND CSL_NATURE_OF_CLAIM<>'VTFO' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) other_gic_od_policy_comp,"+  
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND CSL_NATURE_OF_CLAIM<>'VTFO' and category='TP' THEN CSL_GIC ELSE 0 END) other_gic_od_policy_tp,"+ 
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND csl_claim_no NOT LIKE 'TP%' AND CSL_NATURE_OF_CLAIM<>'VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) other_gic_od_policy_others,"+ 
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND  CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_comp,"+  
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND  CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_tp,"+ 
			"SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND  CSL_CLAIM_NO LIKE 'TP%' AND coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_others,"+  
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_comp,"+  
			"SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_tp,  SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_others,"+
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_od,"+
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_tp,"+  
			"SUM(CASE WHEN (csl_claim_type not in ('MUTA','PUBB',"+
			"'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"+
			"'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"+
			"'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"+
			"'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_others "+
			"from (  SELECT   RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHECODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.csl_claim_type ,CATASTROPHIC_MASTER.CAT_TYPE   ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_NATURE_OF_CLAIM,  CSL_CLAIM_NO, category  ,SUM(CSL_GIC) CSL_GIC  FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW_CURRENT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE";

		}else if(claimParamType.equals("NIC")){

			//  nicTp = getNicTp( Integer.valueOf(fromMonth),  Integer.valueOf(toMonth), Integer.valueOf(fromYear) , Integer.valueOf(toYear), filterRequest);
			
			queryStr = "SELECT sum(csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY))nic,0 NIC_policy_comp,0 NIC_policy_tp,0 NIC_policy_others,sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive') THEN csl_gic*(1-TP_QUOTA_SHARE-TP_OBLIGATORY) ELSE 0 end) nic_tp_policy_comp,  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='TP') THEN csl_gic*(1-TP_QUOTA_SHARE-TP_OBLIGATORY) ELSE 0 end) nic_tp_policy_tp,   sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and coalesce(A.CATEGORY,'Others')='Others') THEN csl_gic*(1-TP_QUOTA_SHARE-TP_OBLIGATORY) ELSE 0 end) nic_tp_policy_others,sum(case WHEN (CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive') then csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY) else 0 end) nic_od_policy_comp,sum(case WHEN (CSL_CLAIM_NO NOT LIKE  'TP%' and category='TP') then csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY) else 0 end) nic_od_policy_tp,sum(case WHEN (CSL_CLAIM_NO NOT LIKE  'TP%' and coalesce(A.CATEGORY,'Others')='Others') then csl_gic*(1-OD_QUOTA_SHARE-OD_OBLIGATORY) else 0 end) nic_od_policy_others FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO   FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW_CURRENT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE";
			
			
					/*+ " WHERE ( CSL_MVMT_MONTH between 201804 and 201903 ) group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,  "
					+ " (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B  "
					+ " where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";	*/				
			/*queryStr += "SELECT  sum(case when category='Comprehensive' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_comp,  sum(case when category='TP' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_tp,  sum(case when coalesce(A.CATEGORY,'Others')='Others' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_others,  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_comp,  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='TP') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_tp,   sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and coalesce(A.CATEGORY,'Others')='Others') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_others, 0 nic_od_policy_comp,0 nic_od_policy_tp,0 nic_od_policy_others FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO   FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL   LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE   LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE   LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE   LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE   LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE   LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE   LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY   LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE   LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE  WHERE ( CSL_MVMT_MONTH between 201804 and 201903 ) and  CSL_CLAIM_NO LIKE  'TP%' group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,   (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B   where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band"+ 
						" union all "+
						"SELECT  0 NIC_policy_comp,  0 NIC_policy_tp, 0 NIC_policy_others,  0 nic_tp_policy_comp,  0 nic_tp_policy_tp,   0 nic_tp_policy_others,  sum(case WHEN (category='Comprehensive') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_comp,  sum(case WHEN ( category='TP') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_tp,  sum(case WHEN (coalesce(A.CATEGORY,'Others')='Others') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_others FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO   FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL   LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE   LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE   LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE   LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE   LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE   LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE   LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY   LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE   LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE  WHERE ( CSL_MVMT_MONTH between 201804 and 201903 ) and CSL_CLAIM_NO NOT LIKE 'TP%' group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,   (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B   where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";*/
		}
		
					

		
		/*if (fromYear.equals(toYear)) {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		} else {
			queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
					+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
		}*/
		
			//queryStr += " WHERE ( CSL_MVMT_MONTH between " + fromYear +fromMonth+ " and " + toYear +toMonth+ " )";
		String finstartDate = fromYear + "-" + fromMonth + "-01";
		String finEndDate = toYear + "-" + toMonth + "-31";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BUSINESS_TYPE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.AGENT_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.NCB_FLAG) in (" + vals + ")";
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
	
	if(claimParamType.equals("GIC")){
		queryStr += " group by RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHECODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.csl_claim_type ,CATASTROPHIC_MASTER.CAT_TYPE ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_NATURE_OF_CLAIM,CSL_CLAIM_NO,category ) x";
	}
	else if(claimParamType.equals("NIC")){
		queryStr +=" group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,   (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OD_OBLIGATORY) OD_OBLIGATORY,SUM(OD_QUOTA_SHARE) OD_QUOTA_SHARE,SUM(TP_OBLIGATORY) TP_OBLIGATORY,SUM(TP_QUOTA_SHARE) TP_QUOTA_SHARE from  RSA_DWH_RI_OBLIGATORY_MASTER1_NEW group by underwriting_year,XGEN_PRODUCTCODE,band) B   where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band";

	}
	
	
	
		

		System.out.println("queryStr------------------------------ "+ claimParamType +"///----///"+ queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");

		// jsArray = convertToJSON(rs);
		int count =0 ;
		while (rs.next()) {

			SingleLineCubeResponseNew res = new SingleLineCubeResponseNew();
			if(claimParamType.equals("GIC")){
			res.setCatGicOdComprehensive(rs.getDouble(1));
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
			res.setOthersGicTpOthers(rs.getDouble(18));
			}else if(claimParamType.equals("NIC")){
				/*if(count==0){*/
					res.setNicComprehensive(rs.getDouble(1));
					res.setNicTp(rs.getDouble(2));
					res.setNicOthers(rs.getDouble(3));
					res.setNicTpComprehensive(nicTp);
					/*below code has to  be uncommented after category implementation*/
					/*res.setNicTpComprehensive(rs.getDouble(4));
					res.setNicTpTp(rs.getDouble(5));
					res.setNicTpOthers(rs.getDouble(6));*/
				/*}if(count==1){*/
					res.setNicOdComprehensive(rs.getDouble(7));
					res.setNicOdTp(rs.getDouble(8));
					res.setNicOdOthers(rs.getDouble(9));
				count ++;
			}
			
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
}