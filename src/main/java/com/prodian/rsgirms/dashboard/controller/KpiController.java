
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
import org.springframework.core.io.AbstractFileResolvingResource;
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
import com.prodian.rsgirms.dashboard.model.response.R12CubeResponse;
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
import com.prodian.rsgirms.dashboard.rsrepository.GicNicPsqlRepository;
import com.prodian.rsgirms.dashboard.service.KpiDashboardService;
import com.prodian.rsgirms.dashboard.modelfunction.UserRole;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterRequest;
import com.prodian.rsgirms.usermatrix.service.UserMatrixService;

import com.prodian.rsgirms.dashboard.modelfunction.GicNicPsqlFunction;

@Controller
public class KpiController {

	@Autowired
	private UserService userService;

	@Autowired
	private KpiDashboardService kpiDashboardService;

	// private Connection connection = null;

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
	
	@GetMapping("/motorKpiNew")
	public ModelAndView getMockMotorKpiDashBoard() {
		ModelAndView model = new ModelAndView("motorKpiNew");
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
		model.addObject("businessTypes", res.getBusinessTypeMasters());
		model.addObject("campaigns", res.getCampaignMasters());
		model.addObject("subChannels", res.getSubChannelMasters());
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

	
	@GetMapping("/getIntermediaries")
	@ResponseBody
	public List<IntermediaryMaster> getIntermediaries()
			 {
		System.out.println("called getIntermediaries()::");
		List<IntermediaryMaster> list = intermediaryMasterRepository.findAll();
		System.out.println("list size-->"+list.size());
		return list;
	}
	
	/*@RequestMapping(value="/getIntermediaries", method=RequestMethod.GET)
	public String getIntermediaries(ModelMap map) {
	    // TODO: retrieve the new value here so you can add it to model map
	    map.addAttribute("intermediaries", intermediaryMasterRepository.findAll());
	    System.out.println("list size-->"+intermediaryMasterRepository.findAll().size());

	    // change "myview" to the name of your view 
	    return "motorKpiNew :: #iCode";
	}*/
	
	
	
	@RequestMapping(value="/getOEMwiseGwp", method=RequestMethod.GET)
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
		
		//queryStr = " SELECT oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END) HIGHEND,upper(fueltype),coalesce(NCB_FLAG,'N') NCB_FL,coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS'),SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(true,false)+"' and   substring(inception_date,1,10) <= DATE '"+getCustomLastDate(true,false)+"' THEN (GWP) ELSE 0.0 END) CM_GWP,   SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(false,true)+"' and    substring(inception_date,1,10) <= DATE '"+getCustomLastDate(false,true)+"' THEN (GWP) ELSE 0.0 END) PM_GWP   FROM(   SELECT inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end as oem_non_oem,HIGH_END,coalesce(c.fueltypE,a.fueltypE) as fueltype ,NCB_FLAG,cityname,statename,   SUM(INS_GWP) GWP FROM RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL a   left join RSA_DWH_CITY_GROUPING_MASTER_FINAL b on a.regLocation=b.citycode    left join RSA_DWH_MODEL_MASTER c on a.make=c.make and a.modelcode=c.model_code  GROUP BY inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end,HIGH_END,coalesce(c.fueltypE,a.fueltypE),NCB_FLAG ,cityname,statename) X   group by oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END),upper(fueltype),coalesce(NCB_FLAG,'N'),coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS') ";
		
		queryStr = " SELECT oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END) HIGHEND,upper(fueltype),coalesce(NCB_FLAG,'N') NCB_FL,coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS'),SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(true,false)+"' and   substring(inception_date,1,10) <= DATE '"+getCustomLastDate(true,false)+"' THEN (GWP) ELSE 0.0 END) CM_GWP,   SUM(CASE WHEN substring(inception_date,1,10) >= DATE '"+getCustomFirstDate(false,true)+"' and    substring(inception_date,1,10) <= DATE '"+getCustomLastDate(false,true)+"' THEN (GWP) ELSE 0.0 END) PM_GWP   FROM( "+
				   " SELECT inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end as oem_non_oem, MM_MODELCLASSIFICATION as HIGH_END ,coalesce(MM_FUELTYPE,PPC_FUELTYPE) as fueltype ,NCB_FLAG,cityname,statename,   SUM(COVERAGE_PREIMUM) GWP FROM RSA_DWH_OEM_CLASIFICATION_FACT  GROUP BY inception_date,case when channel='OEM' then 'OEM' WHEN channel='NONE' then 'NONE' ELSE 'NON_OEM' end, MM_MODELCLASSIFICATION,coalesce(MM_FUELTYPE,PPC_FUELTYPE),NCB_FLAG ,cityname,statename "+ 
				   " ) X   group by oem_non_oem,(CASE WHEN HIGH_END IN ('HIGHEND','High End')  THEN 'HIGHEND' ELSE 'NON_HIGHEND' END),upper(fueltype),coalesce(NCB_FLAG,'N'),coalesce(cityname,'OTHERS'),coalesce(statename,'OTHERS') "; 
		
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
	
	
	@GetMapping("/getGepCubeMonthAscolumnData/{claimType}/{gepReportType}")
	@ResponseBody
	public List<GepCubeResponseFinal> getGepCubeMonthAscolumnData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest,@PathVariable(value="claimType") String claimType,
			@PathVariable(value="gepReportType") String gepReportType)
			throws SQLException {
		Connection connection = null;
		List<GepCubeResponseFinal> kpiResponseList = new ArrayList<GepCubeResponseFinal>();
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
			List<String> measureList = null;

			String queryStr = "";
			// if(claimType.equalsIgnoreCase("R")){
					measureList = getgepBaseMeasures();
					System.out.println("AddOn: "+ filterRequest.getAddOnNew());
					if(filterRequest.getAddOnNew() == "Include") {
						queryStr += "SELECT SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEPCOVERAGE) as GEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEPCOVERAGE) as NEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_OD) as GEP_OD,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_TP) as GEP_TP  ,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_OD) as NEP_OD,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_TP) as NEP_TP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_NILDEP) as GEP_NILDEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_NCB) as GEP_NCB,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_OTHER_ADDON) as GEP_OTHER_ADDON,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.OD_EARNED_POLICIES ) as GEP_OD_EARNED_POLICIES,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_NILDEP) as NEP_NILDEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_NCB) as NEP_NCB,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_OTHER_ADDON) as NEP_OTHER_ADDON,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GIC_TP) as GIC_TP ";	
					}
					else if(filterRequest.getAddOnNew() == "Exclude") {
						queryStr += "SELECT SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEPCOVERAGE) as GEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEPCOVERAGE) as NEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_OD) as GEP_OD,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_TP) as GEP_TP  ,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_OD) as NEP_OD,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_TP) as NEP_TP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_NILDEP) as GEP_NILDEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_NCB) as GEP_NCB,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_OTHER_ADDON) as GEP_OTHER_ADDON,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.OD_EARNED_POLICIES ) as GEP_OD_EARNED_POLICIES,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_NILDEP) as NEP_NILDEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_NCB) as NEP_NCB,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_OTHER_ADDON) as NEP_OTHER_ADDON,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GIC_TP) as GIC_TP ";	
					}else {
						queryStr += "SELECT SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEPCOVERAGE) as GEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEPCOVERAGE) as NEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_OD) as GEP_OD,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_TP) as GEP_TP  ,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_OD) as NEP_OD,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_TP) as NEP_TP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_NILDEP) as GEP_NILDEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_NCB) as GEP_NCB,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_OTHER_ADDON) as GEP_OTHER_ADDON,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.OD_EARNED_POLICIES ) as GEP_OD_EARNED_POLICIES,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_NILDEP) as NEP_NILDEP,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_NCB) as NEP_NCB,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_OTHER_ADDON) as NEP_OTHER_ADDON,"
								+ "SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GIC_TP) as GIC_TP ";	
					}
					
			// }

			/*  This has been commented out because UW Changes
			else if(claimType.equalsIgnoreCase("U")){
					queryStr += "SELECT  SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEPCOVERAGE),SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEPCOVERAGE),SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_OD), SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEP_TP), SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_OD),  SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.NEP_TP), 0, 0, 0, SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.OD_EARNED_POLICIES ), 0, 0,  0, SUM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.GEPCOVERAGE*0.95) as GIC_TP ";
			}*/
			
			// else if(claimType.equalsIgnoreCase("R12") && gepReportType.equalsIgnoreCase("G")){
			// 	measureList = getgepR12SevGicMeasures();
			// }else if(claimType.equalsIgnoreCase("R12") && gepReportType.equalsIgnoreCase("F")){
			// 	measureList = getgepR12FreqMeasures();
			// }else if(claimType.equalsIgnoreCase("UWR12") && gepReportType.equalsIgnoreCase("G")){
			// 	measureList = getgepUWR12SevGicMeasures();
			// }else if(claimType.equalsIgnoreCase("UWR12") && gepReportType.equalsIgnoreCase("F")){
			// 	measureList = getgepUWR12FreqMeasures();
			// }
			
			
			/*if( (claimType.equalsIgnoreCase("UWR12") && gepReportType.equalsIgnoreCase("G"))
					|| (claimType.equalsIgnoreCase("UWR12") && gepReportType.equalsIgnoreCase("F")) ){
				queryStr += " FROM RSDB.GEP_POLICY_FACT_DENORMAL_UPDATED ";
			}else {
				queryStr += " FROM RSDB.GEP_POLICY_FACT_DENORMAL ";
			}*/
			
/*		  	queryStr += "FROM RSDB.GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL as GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL "+
			  "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+
			  "ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "+
			  "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "+
			  "ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "+
			  "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "+
			  "ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "+
			  "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "+
			  "ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "+
			  "LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "+
			  "ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "+
			  "LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+
			  "ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "+
			  "LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT "+
			  "ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE"; */
			
			queryStr += "FROM RSDB.GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL as GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL "
					+"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "
					+"ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "
					+"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "
					+"ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "
					+"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "
					+"ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "
					+"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "
					+"ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "
					+"LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "
					+"ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
					+"LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "
					+"ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
					+"LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT "
					+"ON GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE ";
			
			
			
			
			if(claimType.equalsIgnoreCase("R")){
				queryStr += " WHERE";
				queryStr += getFinGepCondQuery(Integer.valueOf(fromMonth),Integer.valueOf(toMonth),Integer.valueOf(fromYear),Integer.valueOf(toYear));

			}else if(claimType.equalsIgnoreCase("U")){
				String finstartDate = fromYear + "-" + fromMonth + "-01";
				String finEndDate = toYear + "-" + toMonth + "-31";
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
				queryStr += " and TRIM(GEP_POLICY_GEP_MONTH_ON_COLUMN_TRIAL.BUSINESS_TYPE) in (" + vals + ")";
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
				
		

			System.out.println("queryStr------------------------------ "+ claimType +" -----  " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				GepCubeResponseFinal gepCubeResponse = new GepCubeResponseFinal();
				
			if(gepReportType.equalsIgnoreCase("B")){
				gepCubeResponse.setGep(rs.getDouble(1));
				gepCubeResponse.setNep(rs.getDouble(2));
				gepCubeResponse.setGepOd(rs.getDouble(3));
				gepCubeResponse.setGepTp(rs.getDouble(4));
				gepCubeResponse.setNepOd(rs.getDouble(5));
				gepCubeResponse.setNepTp(rs.getDouble(6));
				gepCubeResponse.setGepNildep(rs.getDouble(7));
				gepCubeResponse.setGepNcb(rs.getDouble(8));
				gepCubeResponse.setGepOtherAddon(rs.getDouble(9));
				gepCubeResponse.setGepOdEarnedPolicies(rs.getDouble(10));
				/*gepCubeResponse.setGepDepEarnedPolicies(rs.getDouble(11));
				gepCubeResponse.setGepNcbEarnedPolicies(rs.getDouble(12));
				gepCubeResponse.setGepOtherAddonEarnedPolicies(rs.getDouble(13));*/
				gepCubeResponse.setNepNildep(rs.getDouble(11));
				gepCubeResponse.setNepNcb(rs.getDouble(12));
				gepCubeResponse.setNepOtherAddon(rs.getDouble(13));
				gepCubeResponse.setGicTp(rs.getDouble(14));
				
			}else if(claimType.equalsIgnoreCase("R12") && gepReportType.equalsIgnoreCase("G")){
				gepCubeResponse.setGepSevCatr12(rs.getDouble(1));
				gepCubeResponse.setGepSevTheftr12(rs.getDouble(2));
				gepCubeResponse.setGepSevOthersr12(rs.getDouble(3));
				
				gepCubeResponse.setGepGicCatOdr12(rs.getDouble(4));
				gepCubeResponse.setGepGicTheftOdr12(rs.getDouble(5));
				gepCubeResponse.setGepGicOthersOdr12(rs.getDouble(6));
				
				/*gepCubeResponse.setGepGicCatDepr12(rs.getDouble(7));
				gepCubeResponse.setGepGicTheftDepr12(rs.getDouble(8));
				gepCubeResponse.setGepGicOthersDepr12(rs.getDouble(9));
				
				gepCubeResponse.setGepGicCatNcbr12(rs.getDouble(10));
				gepCubeResponse.setGepGicTheftNcbr12(rs.getDouble(11));
				gepCubeResponse.setGepGicOthersNcbr12(rs.getDouble(12));
				
				gepCubeResponse.setGepGicCatOtherAddonr12(rs.getDouble(13));
				gepCubeResponse.setGepGictheftOtherAddonr12(rs.getDouble(14));
				gepCubeResponse.setGepGicOthersOtherAddonr12(rs.getDouble(15));*/
				
			}else if(claimType.equalsIgnoreCase("R12") && gepReportType.equalsIgnoreCase("F")){
				
				gepCubeResponse.setGepFreqCatOdr12(rs.getDouble(1));
				gepCubeResponse.setGepFreqTheftOdr12(rs.getDouble(2));
				gepCubeResponse.setGepFreqOthersOdr12(rs.getDouble(3));
				
				/*gepCubeResponse.setGepFreqCatDepr12(rs.getDouble(4));
				gepCubeResponse.setGepFreqTheftDepr12(rs.getDouble(5));
				gepCubeResponse.setGepFreqOthersDepr12(rs.getDouble(6));
				
				gepCubeResponse.setGepFreqCatNcbr12(rs.getDouble(7));
				gepCubeResponse.setGepFreqTheftNcbr12(rs.getDouble(8));
				gepCubeResponse.setGepFreqOthersNcbr12(rs.getDouble(9));
				
				gepCubeResponse.setGepFreqCatOtherAddonr12(rs.getDouble(10));
				gepCubeResponse.setGepFreqTheftOtherAddonr12(rs.getDouble(11));
				gepCubeResponse.setGepFreqOthersOtherAddonr12(rs.getDouble(12));*/
				
			}else if(claimType.equalsIgnoreCase("UWR12") && gepReportType.equalsIgnoreCase("G")){
				gepCubeResponse.setGepSevCatr12(rs.getDouble(1));
				gepCubeResponse.setGepSevTheftr12(rs.getDouble(2));
				gepCubeResponse.setGepSevOthersr12(rs.getDouble(3));
				
				gepCubeResponse.setGepGicCatOdr12(rs.getDouble(4));
				gepCubeResponse.setGepGicTheftOdr12(rs.getDouble(5));
				gepCubeResponse.setGepGicOthersOdr12(rs.getDouble(6));
				
				/*gepCubeResponse.setGepGicCatDepr12(rs.getDouble(7));
				gepCubeResponse.setGepGicTheftDepr12(rs.getDouble(8));
				gepCubeResponse.setGepGicOthersDepr12(rs.getDouble(9));
				
				gepCubeResponse.setGepGicCatNcbr12(rs.getDouble(10));
				gepCubeResponse.setGepGicTheftNcbr12(rs.getDouble(11));
				gepCubeResponse.setGepGicOthersNcbr12(rs.getDouble(12));
				
				gepCubeResponse.setGepGicCatOtherAddonr12(rs.getDouble(13));
				gepCubeResponse.setGepGictheftOtherAddonr12(rs.getDouble(14));
				gepCubeResponse.setGepGicOthersOtherAddonr12(rs.getDouble(15));*/
			}else if(claimType.equalsIgnoreCase("UWR12") && gepReportType.equalsIgnoreCase("F")){
				gepCubeResponse.setGepFreqCatOdr12(rs.getDouble(1));
				gepCubeResponse.setGepFreqTheftOdr12(rs.getDouble(2));
				gepCubeResponse.setGepFreqOthersOdr12(rs.getDouble(3));
				
				/*gepCubeResponse.setGepFreqCatDepr12(rs.getDouble(4));
				gepCubeResponse.setGepFreqTheftDepr12(rs.getDouble(5));
				gepCubeResponse.setGepFreqOthersDepr12(rs.getDouble(6));
				
				gepCubeResponse.setGepFreqCatNcbr12(rs.getDouble(7));
				gepCubeResponse.setGepFreqTheftNcbr12(rs.getDouble(8));
				gepCubeResponse.setGepFreqOthersNcbr12(rs.getDouble(9));
				
				gepCubeResponse.setGepFreqCatOtherAddonr12(rs.getDouble(10));
				gepCubeResponse.setGepFreqTheftOtherAddonr12(rs.getDouble(11));
				gepCubeResponse.setGepFreqOthersOtherAddonr12(rs.getDouble(12));*/
			}
				
				
				
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
	
	

	
	
	@GetMapping("/getPolicyCubeDataNew")
	@ResponseBody
	public List<PolicyCubeResponseNew> getPolicyCubeDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
			throws SQLException {
		Connection connection = null;
		List<PolicyCubeResponseNew> kpiResponseList = new ArrayList<PolicyCubeResponseNew>();
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
								" SUM(RSA_KPI_FACT_POLICY_FINAL.LIVESCOVERED) as LIVESCOVERED "+
								" ,SUM(RSA_KPI_FACT_POLICY_FINAL.ACQ_COST) as ACQ_COST "+
								" ,SUM(RSA_KPI_FACT_POLICY_FINAL.POLICY_COUNT ) as POLICY_COUNT "+
								" ,CATEGORY "+ 
								"  FROM RSDB.RSA_KPI_FACT_POLICY_FINAL as RSA_KPI_FACT_POLICY_FINAL "+ 
								" LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_POLICY_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL"+ 
								" LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "+ 
								" LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "+ 
								" LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "+ 
								" LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER as KPI_MODEL_MASTER_NW "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_POLICY_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE"+ 
								" LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "+ 
								" LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "+ 
								" LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY "+ 
								" LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE "+ 
								" LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "+ 
								" LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "+ 
								" LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL "+ 
								" ON RSA_KPI_FACT_POLICY_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "; 

			
			if (fromYear.equals(toYear)) {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			} else {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			}
			
//			if (fromYear.equals(toYear)) {
//				queryStr += " WHERE (( FINANCIAL_YEAR='" + fromYear + "'))";
//			} else {
//				queryStr += " WHERE (( FINANCIAL_YEAR='" + fromYear + "'))";
//			}

			String finstartDate = fromYear + "-" + fromMonth + "-01";
			String finEndDate = toYear + "-" + toMonth + "-01";
			
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.BUSINESS_TYPE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.MAKE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.MODELGROUP) in (" + vals + ")";
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
				queryStr += " and coalesce(RSA_KPI_FACT_POLICY_FINAL.FUELTYPE,'N') in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.NCB_FLAG) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.AGENT_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.FUELTYPE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL.NCB_FLAG) in (" + vals + ")";
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
	
	
	@GetMapping("/getInsCubeDataNew")
	@ResponseBody
	public List<InsCubeResponseNew> getInsCubeDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
			throws SQLException {
		Connection connection = null;
		List<InsCubeResponseNew> kpiResponseList = new ArrayList<InsCubeResponseNew>();
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


			/*String queryStr = "SELECT "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_NWP ELSE 0 END) as INS_NWP_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_NWP ELSE 0 END) as INS_NWP_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_NWP ELSE 0 END) as INS_NWP_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_NWP_OD ELSE 0 END) as INS_NWP_OD_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_NWP_OD ELSE 0 END) as INS_NWP_OD_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_NWP_OD ELSE 0 END) as INS_NWP_OD_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_NWP_TP ELSE 0 END) as INS_NWP_TP_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_NWP_TP ELSE 0 END) as INS_NWP_TP_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_NWP_TP ELSE 0 END) as INS_NWP_TP_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_NWP_DISCOUNT_OD ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_NWP_DISCOUNT_OD ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_NWP_DISCOUNT_OD ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_NCB_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_NWP_DEP ELSE 0 END) as INS_NWP_DEP_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_NWP_DEP ELSE 0 END) as INS_NWP_DEP_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_NWP_DEP ELSE 0 END) as INS_NWP_DEP_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_NWP_NCB ELSE 0 END) as INS_NWP_NCB_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_NWP_NCB ELSE 0 END) as INS_NWP_NCB_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_NWP_NCB ELSE 0 END) as INS_NWP_NCB_POLICY_others, "+
								" SUM(case when x.CATEGORY='Comprehensive' THEN INS_NWP_OTHERADDON ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_COMP, "+
								" SUM(case when x.CATEGORY='TP' THEN INS_NWP_OTHERADDON ELSE 0 END) as INS_NWP_NCB_POLICY_TP, "+
								" SUM(case when coalesce(x.CATEGORY,'Others')='Others' THEN INS_NWP_OTHERADDON ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_others "+
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
								" , CATEGORY "+
								" FROM RSDB.RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL as RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL "+
								" LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "+
								" LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "+
								" LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "+
								" LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "+
								" LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE "+
								" LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "+
								" LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "+
								" LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY "+
								" LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE "+
								" LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "+
								" LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "+
								" LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL "+
								" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE"; */
			
			/*String queryStr = "SELECT "+
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
					" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP *(1-quota_share-obligatory))+(INS_GWP*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN (INS_GWP *(1-quota_share-obligatory))+(INS_GWP*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP *(1-quota_share-obligatory))+(INS_GWP*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_POLICY_others, "+
					" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_NWP_OD *(1-quota_share-obligatory))+(INS_NWP_OD*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_OD *(1-quota_share-obligatory))+(INS_NWP_OD*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_OD *(1-quota_share-obligatory))+(INS_NWP_OD*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_others, "+
					" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_NWP_TP *(1-quota_share-obligatory))+(INS_NWP_TP*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_TP *(1-quota_share-obligatory))+(INS_NWP_TP*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_TP *(1-quota_share-obligatory))+(INS_NWP_TP*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_others, "+
					" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_NWP_DISCOUNT_OD *(1-quota_share-obligatory))+(INS_NWP_DISCOUNT_OD*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_DISCOUNT_OD *(1-quota_share-obligatory))+(INS_NWP_DISCOUNT_OD*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_DISCOUNT_OD *(1-quota_share-obligatory))+(INS_NWP_DISCOUNT_OD*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_others, "+
					" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_others, "+
					" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_others, "+
					" SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_NCB_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_others, "+
					" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_NWP_DEP *(1-quota_share-obligatory))+(INS_NWP_DEP*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_DEP *(1-quota_share-obligatory))+(INS_NWP_DEP*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_DEP *(1-quota_share-obligatory))+(INS_NWP_DEP*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_others, "+
					" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_NWP_NCB *(1-quota_share-obligatory))+(INS_NWP_NCB*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_NCB *(1-quota_share-obligatory))+(INS_NWP_NCB*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_NCB *(1-quota_share-obligatory))+(INS_NWP_NCB*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_others, "+
					" SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_NWP_OTHERADDON *(1-quota_share-obligatory))+(INS_NWP_OTHERADDON*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_COMP, "+
					" SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_OTHERADDON *(1-quota_share-obligatory))+(INS_NWP_OTHERADDON*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_TP, "+
					" SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_OTHERADDON *(1-quota_share-obligatory))+(INS_NWP_OTHERADDON*quota_share*RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_others "+
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
					" , CATEGORY, uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.product_code "+
					" FROM RSDB.RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL as RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL "+
					" LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "+
					" LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "+
					" LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "+
					" LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "+
					" LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE "+
					" LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "+
					" LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "+
					" LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY "+
					" LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE "+
					" LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "+
					" LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "+
					" LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL "+
					" ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE"; */

			
			// New Query Changed
			String queryStr = "SELECT  SUM(INS_GWP),"+
			"SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_COMP,"+  
			"SUM(case when aa.CATEGORY='TP' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_TP,"+
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP ELSE 0 END) as INS_GWP_POLICY_others," +
			"SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_COMP,"+  
			"SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_TP,"+ 
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OD ELSE 0 END) as INS_GWP_OD_POLICY_others,"+ 
			"SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_COMP,"+  
			"SUM(case when aa.CATEGORY='TP' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_TP,"+  
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_TP ELSE 0 END) as INS_GWP_TP_POLICY_others,"+ 
			"SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_COMP,"+
			"SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_TP, "+
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DISCOUNT_OD ELSE 0 END) as INS_GWP_DISCOUNT_OD_POLICY_others,  SUM(case when aa.CATEGORY='Comprehensive' THEN ((INS_GWP *(1-od_quota_share-od_obligatory))+(INS_GWP*od_quota_share*od_RI_COMMISSION)) ELSE 0 END) as INS_NWP_POLICY_COMP, "+ 
			"SUM(case when aa.CATEGORY='TP' THEN ((INS_GWP *(1-od_quota_share-od_obligatory))+(INS_GWP*od_quota_share*od_RI_COMMISSION)) ELSE 0 END) as INS_NWP_POLICY_TP, "+
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN ((INS_GWP *(1-od_quota_share-od_obligatory))+(INS_GWP*od_quota_share*od_RI_COMMISSION)) ELSE 0 END) as INS_NWP_POLICY_others, "+
			"SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_COMP, "+
			"SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_TP,"+
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OD_POLICY_others, "+
			"SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_TP *(1-od_quota_share-od_obligatory))+(INS_GWP_TP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_COMP,  "+
			"SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_TP *(1-od_quota_share-od_obligatory))+(INS_GWP_TP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_TP,"+ 
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_TP *(1-od_quota_share-od_obligatory))+(INS_GWP_TP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_TP_POLICY_others, "+ 
			"SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_COMP,  SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_TP,  SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_DISCOUNT_OD *(1-od_quota_share-od_obligatory))+(INS_GWP_DISCOUNT_OD*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DISCOUNT_OD_POLICY_others,"+ 
			"SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_COMP, "+
			"SUM(case when aa.CATEGORY='TP' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_TP, "+
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_DEP ELSE 0 END) as INS_GWP_DEP_POLICY_others,  "+
			"SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_COMP,"+  
			"SUM(case when aa.CATEGORY='TP' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_TP, "+
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_NCB ELSE 0 END) as INS_GWP_NCB_POLICY_others, "+ 
			"SUM(case when aa.CATEGORY='Comprehensive' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_COMP, "+ 
			"SUM(case when aa.CATEGORY='TP' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_NCB_POLICY_TP,  "+
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN INS_GWP_OTHERADDON ELSE 0 END) as INS_GWP_OTHERADDON_POLICY_others, "+ 
			"SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_COMP,"+ 
			"SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_TP, "+
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_DEP *(1-od_quota_share-od_obligatory))+(INS_GWP_DEP*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_DEP_POLICY_others,  "+
			"SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_NCB *(1-od_quota_share-od_obligatory))+(INS_GWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_COMP,"+ 
			"SUM(case when aa.CATEGORY='TP' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_TP,  "+
			"SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_NWP_NCB *(1-od_quota_share-od_obligatory))+(INS_NWP_NCB*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_NCB_POLICY_others,  "+
			"SUM(case when aa.CATEGORY='Comprehensive' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_COMP,  SUM(case when aa.CATEGORY='TP' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_TP,  SUM(case when coalesce(aa.CATEGORY,'Others')='Others' THEN (INS_GWP_OTHERADDON *(1-od_quota_share-od_obligatory))+(INS_GWP_OTHERADDON*od_quota_share*od_RI_COMMISSION) ELSE 0 END) as INS_NWP_OTHERADDON_POLICY_others  FROM ( SELECT  SUM(INS_GWP) as INS_GWP  ,SUM(INS_GWP_OD) as INS_GWP_OD  ,SUM(INS_GWP_TP) as INS_GWP_TP  ,SUM(INS_GWP_DISCOUNT_OD) as INS_GWP_DISCOUNT_OD  ,SUM(INS_NWP) as INS_NWP  ,SUM(INS_NWP_OD) as INS_NWP_OD  ,SUM(INS_NWP_TP) as INS_NWP_TP  ,SUM(INS_NWP_DISCOUNT_OD) as INS_NWP_DISCOUNT_OD  ,SUM(INS_GWP_DEP) as INS_GWP_DEP  ,SUM(INS_GWP_NCB) as INS_GWP_NCB  ,SUM(INS_GWP_OTHERADDON) as INS_GWP_OTHERADDON  ,SUM(INS_NWP_DEP) as INS_NWP_DEP  ,SUM(INS_NWP_NCB) as INS_NWP_NCB  ,SUM(INS_NWP_OTHERADDON) as INS_NWP_OTHERADDON  , CATEGORY, uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.product_code  FROM RSDB.RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_CURRENT as RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL_NOW.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE";
			
			/*if (fromYear.equals(toYear)) {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			} else {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			}*/
			
			String finstartDate = fromYear + "-" + fromMonth + "-01";
			String finEndDate = toYear + "-" + toMonth + "-01";
			
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.BUSINESS_TYPE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.MAKE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.MODELGROUP) in (" + vals + ")";
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
				queryStr += " and coalesce(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.FUELTYPE,'N') in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.NCB_FLAG) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.AGENT_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.FUELTYPE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.NCB_FLAG) in (" + vals + ")";
			}
			

			/*queryStr += " group by category ) x";*/
			
			queryStr += " group by category,uw_year,RSA_KPI_FACT_INS_POLICY_LEVEL_FINAL.product_code ) aa,"
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


	@GetMapping("/getClaimsCubeDataNew")
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
								" SUM(CASE WHEN ( CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' AND coalesce(CAT_TYPE,'NONE')<>' ' AND CAT_TYPE<>'NULL') )     THEN CLM_CLAIM_NO ELSE 0 END) cat_claim_count_policy_comp, "+
								" SUM(CASE WHEN ( CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' AND coalesce(CAT_TYPE,'NONE')<>' ' AND CAT_TYPE<>'NULL') ) and x.CATEGORY='TP' THEN CLM_CLAIM_NO ELSE 0 END) cat_claim_count_policy_tp, "+
								" SUM(CASE WHEN ( CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' AND coalesce(CAT_TYPE,'NONE')<>' ' AND CAT_TYPE<>'NULL') ) and coalesce(x.CATEGORY,'Others')='Others' THEN CLM_CLAIM_NO ELSE 0 END) cat_claim_count_policy_others, "+
								"  SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and x.CATEGORY='Comprehensive' THEN CLM_CLAIM_NO ELSE 0 END) theft_claim_count_policy_comp, "+
								"  SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and x.CATEGORY='TP' THEN CLM_CLAIM_NO ELSE 0 END) theft_claim_count_policy_tp, "+
								"  SUM(CASE WHEN NATURE_OF_CLAIM='VTFO' and coalesce(x.CATEGORY,'Others')='Others' THEN CLM_CLAIM_NO ELSE 0 END) theft_claim_count_policy_others, "+
								" SUM(CASE WHEN (NATURE_OF_CLAIM<>'VTFO' and ( CAT_TYPE=' ' OR CAT_TYPE='NULL') AND  CATASTROPHECODE<>'CATC' ) and x.CATEGORY='Comprehensive' THEN CLM_CLAIM_NO ELSE 0 END) othert_claim_count_policy_comp, "+
								" SUM(CASE WHEN (NATURE_OF_CLAIM<>'VTFO' and ( CAT_TYPE=' ' OR CAT_TYPE='NULL') AND  CATASTROPHECODE<>'CATC' ) and x.CATEGORY='TP' THEN CLM_CLAIM_NO ELSE 0 END) othert_claim_count_policy_tp, "+
								" SUM(CASE WHEN (NATURE_OF_CLAIM<>'VTFO' and ( CAT_TYPE=' ' OR CAT_TYPE='NULL') AND  CATASTROPHECODE<>'CATC' ) and coalesce(x.CATEGORY,'Others')='Others' THEN CLM_CLAIM_NO ELSE 0 END) othert_claim_count_policy_others "+
								" FROM( "+
								" SELECT "+
								" CLM_NATURE_OF_CLAIM as NATURE_OF_CLAIM "+
								" ,CLM_CATASTROPHECODE as CATASTROPHECODE "+
								" ,CATASTROPHIC_MASTER.CAT_TYPE as CAT_TYPE , category "+
								" , SUM(1) CLM_CLAIM_NO "+
								" FROM RSDB.RSA_KPI_FACT_CLAIMS_FINAL as RSA_KPI_FACT_CLAIMS_FINAL "+
								" LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "+
								" LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "+
								" LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "+
								" LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "+
								" LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER as KPI_MODEL_MASTER_NW "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE "+
								" LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "+
								" LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "+
								" LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY "+
								" LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE "+
								" LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "+
								" LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "+
								" LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.regLocation = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "+
								" LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER "+
								" ON RSA_KPI_FACT_CLAIMS_FINAL.CLM_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE"; 

			
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.BUSINESS_TYPE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.MAKE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.MODELGROUP) in (" + vals + ")";
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
				queryStr += " and coalesce(RSA_KPI_FACT_CLAIMS_FINAL.FUELTYPE,'N') in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.NCB_FLAG) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.AGENT_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.FUELTYPE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_FINAL.NCB_FLAG) in (" + vals + ")";
			}
			
	

			queryStr += " group by CLM_NATURE_OF_CLAIM,CLM_CATASTROPHECODE,CATASTROPHIC_MASTER.CAT_TYPE,category ) x";

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
	
	
	
	@GetMapping("/getSingleLineCubeGicDataNew/{claimParamType}")
	@ResponseBody
	public List<SingleLineCubeResponseNew> getSingleLineCubeDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest,
			@PathVariable(value="claimParamType") String claimParamType)
			throws SQLException {
		Connection connection = null;
		List<SingleLineCubeResponseNew> kpiResponseList = new ArrayList<SingleLineCubeResponseNew>();
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
			String queryStr = "";
			if(claimParamType.equals("GIC")){
				queryStr = "SELECT "+
						" SUM(CASE WHEN ( CSL_CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' AND coalesce(CAT_TYPE,'NONE')<>' ' AND CAT_TYPE<>'NULL')  AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' ) THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_comp, "+
						" SUM(CASE WHEN ( CSL_CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' AND coalesce(CAT_TYPE,'NONE')<>' ' AND CAT_TYPE<>'NULL')  AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='TP' ) THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_tp, "+
						" SUM(CASE WHEN ( CSL_CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' AND coalesce(CAT_TYPE,'NONE')<>' ' AND CAT_TYPE<>'NULL')  AND CSL_CLAIM_NO NOT LIKE 'TP%' AND coalesce(x.CATEGORY,'Others')='Others' ) THEN CSL_GIC ELSE 0 END) cat_gic_od_policy_others, "+
						" SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_comp, "+
						" SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_tp, "+
						" SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) theft_gic_od_policy_others, "+
						" SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and ( CAT_TYPE=' ' OR CAT_TYPE='NULL') AND CSL_CATASTROPHECODE<>'CATC' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' ) THEN CSL_GIC ELSE 0 END) other_gic_od_policy_comp, "+
						" SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and ( CAT_TYPE=' ' OR CAT_TYPE='NULL') AND CSL_CATASTROPHECODE<>'CATC' AND CSL_CLAIM_NO NOT LIKE 'TP%' and category='TP' ) THEN CSL_GIC ELSE 0 END) other_gic_od_policy_tp, "+
						" SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and ( CAT_TYPE=' ' OR CAT_TYPE='NULL') AND CSL_CATASTROPHECODE<>'CATC' AND CSL_CLAIM_NO NOT LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' ) THEN CSL_GIC ELSE 0 END) other_gic_od_policy_others, "+
						" SUM(CASE WHEN ( CSL_CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' AND coalesce(CAT_TYPE,'NONE')<>' ' AND CAT_TYPE<>'NULL')  AND CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' ) THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_comp, "+
						" SUM(CASE WHEN ( CSL_CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' AND coalesce(CAT_TYPE,'NONE')<>' ' AND CAT_TYPE<>'NULL')  AND CSL_CLAIM_NO LIKE 'TP%' and category='TP' ) THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_tp, "+
						" SUM(CASE WHEN ( CSL_CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' AND coalesce(CAT_TYPE,'NONE')<>' ' AND CAT_TYPE<>'NULL')  AND CSL_CLAIM_NO LIKE 'TP%' AND coalesce(x.CATEGORY,'Others')='Others' ) THEN CSL_GIC ELSE 0 END) cat_gic_tp_policy_others, "+
						" SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_comp, "+
						" SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and category='TP' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_tp, "+
						" SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' THEN CSL_GIC ELSE 0 END) theft_gic_tp_policy_others, "+
						" SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and ( CAT_TYPE=' ' OR CAT_TYPE='NULL') AND CSL_CATASTROPHECODE<>'CATC' AND CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive' ) THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_comp, "+
						" SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and ( CAT_TYPE=' ' OR CAT_TYPE='NULL') AND CSL_CATASTROPHECODE<>'CATC' AND CSL_CLAIM_NO LIKE 'TP%' and category='TP' ) THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_tp, "+
						" SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and ( CAT_TYPE=' ' OR CAT_TYPE='NULL') AND CSL_CATASTROPHECODE<>'CATC' AND CSL_CLAIM_NO LIKE 'TP%' and coalesce(x.CATEGORY,'Others')='Others' ) THEN CSL_GIC ELSE 0 END) other_gic_tp_policy_others "+
						" from ( "+
						" SELECT  "+
						" RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHECODE "+ 
						" ,CATASTROPHIC_MASTER.CAT_TYPE  "+
						" ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_NATURE_OF_CLAIM, "+
						" CSL_CLAIM_NO, category "+
						" ,SUM(CSL_GIC) CSL_GIC "+
					/*" SUM(CASE WHEN ( CSL_CATASTROPHECODE='CATC' or (coalesce(CAT_TYPE,'NONE')<>'NONE' AND coalesce(CAT_TYPE,'NONE')<>'' "+
					" AND coalesce(CAT_TYPE,'NONE')<>' ') ) THEN CSL_GIC ELSE 0 END) cat_gic, "+
					" SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' THEN CSL_GIC ELSE 0 END) theft_gic, "+
					" SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and CAT_TYPE=' ' AND CSL_CATASTROPHECODE<>'CATC' ) THEN CSL_GIC ELSE 0 END) other_gic "+
					" from ( "+
					" SELECT  "+
					" RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHECODE "+ 
					" ,CATASTROPHIC_MASTER.CAT_TYPE  "+
					" ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_NATURE_OF_CLAIM "+
					" ,SUM(CSL_GIC) CSL_GIC "+*/
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL "+
					" LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL "+
					" LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "+
					" LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE "+
					" LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE "+
					" LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER as KPI_MODEL_MASTER_NW "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE "+
					" LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE "+
					" LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE "+
					" LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY "+
					" LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE "+
					" LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "+
					" LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "+
					" LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.regLocation = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE "+
					" LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER "+
					" ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE ";
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
			
				queryStr	="SELECT  sum(case when category='Comprehensive' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_comp,"
						+ "  sum(case when category='TP' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_tp,"
						+ "  sum(case when coalesce(A.CATEGORY,'Others')='Others' then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) NIC_policy_others,"
						+ "  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='Comprehensive') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_comp,"
						+ "  sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and category='TP') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_tp,"
						+ "   sum(case WHEN (CSL_CLAIM_NO LIKE 'TP%' and coalesce(A.CATEGORY,'Others')='Others') THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end) nic_tp_policy_others,"
						+ "  sum(case WHEN (CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_comp,"
						+ "  sum(case WHEN (CSL_CLAIM_NO NOT LIKE  'TP%' and category='TP') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_tp,"
						+ "  sum(case WHEN (CSL_CLAIM_NO NOT LIKE  'TP%' and coalesce(A.CATEGORY,'Others')='Others') then csl_gic*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) nic_od_policy_others"
						+ " FROM ( SELECT  sum(csl_gic) csl_gic,uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE' BAND,category,CSL_CLAIM_NO  "
						+ " FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL "
						+ " LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  "
						+ " LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  "
						+ " LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  "
						+ " LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  "
						+ " LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE  "
						+ " LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  "
						+ " LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  "
						+ " LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  "
						+ " LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE "
						+ " LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE "
						+ " LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE "
						+ " LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.regLocation = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  "
						+ " LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE ";
				
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BUSINESS_TYPE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MAKE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.MODELGROUP) in (" + vals + ")";
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
					queryStr += " and coalesce(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.FUELTYPE,'N') in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.NCB_FLAG) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.BRANCH_CODE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.AGENT_CODE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.FUELTYPE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.NCB_FLAG) in (" + vals + ")";
				}
				
		
		if(claimParamType.equals("GIC")){
			queryStr += " group by RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_CATASTROPHECODE ,CATASTROPHIC_MASTER.CAT_TYPE ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.CSL_NATURE_OF_CLAIM,CSL_CLAIM_NO,category ) x";
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
			queryStr +=" group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,  "
			+ " (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OBLIGATORY) OBLIGATORY,SUM(QUOTA_SHARE) QUOTA_SHARE from RSA_DWH_RI_OBLIGATORY_MASTER1 group by underwriting_year,XGEN_PRODUCTCODE,band) B  "
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
	
	
	
	
	@GetMapping("/getSingleLineCubeGicDataNewFIN")
	@ResponseBody
	List<SingleLineCubeResponseNew> getSingleLineCubeGicDataNewFinYear (HttpServletRequest req, UserMatrixMasterRequest filterRequest
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
	
//	if(claimParamType.equals("GIC")){
//		queryStr += " group by RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_CATASTROPHECODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.csl_claim_type ,CATASTROPHIC_MASTER.CAT_TYPE ,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.CSL_NATURE_OF_CLAIM,CSL_CLAIM_NO,category ) x";
//	}
//	else if(claimParamType.equals("NIC")){
//		/*queryStr +=" GROUP by   "+
//				" uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH ) A , "+  
//				" (select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 "+  
//				" group by underwriting_year,XGEN_PRODUCTCODE,band) B   "+
//				" where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band "+   
//				" ) ";*/
//		/*queryStr +=" GROUP by   "+
//				" uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH,category ) A , "+  
//				" (select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 "+  
//				" group by underwriting_year,XGEN_PRODUCTCODE,band) B   "+
//				" where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band "+   
//				" ) ";*/
//		queryStr +=" group by uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_FINAL_NOW.PRODUCT_CODE,'NONE',category,CSL_CLAIM_NO) A ,  "
//		+ " (select underwriting_year,XGEN_PRODUCTCODE,band,SUM(OD_OBLIGATORY) OD_OBLIGATORY,SUM(OD_QUOTA_SHARE) OD_QUOTA_SHARE,SUM(TP_OBLIGATORY) TP_OBLIGATORY,SUM(TP_QUOTA_SHARE) TP_QUOTA_SHARE from "
//		+ " RSA_DWH_RI_OBLIGATORY_MASTER1_NEW group by underwriting_year,XGEN_PRODUCTCODE,band) B  "
//		+ " where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";	
//	}
	
			
	
		

		System.out.println("queryStr------------------------------ " + queryStr);
		ResultSet rs = stmt.executeQuery(queryStr);
		System.out.println("START------------------------------ ");
			
		// jsArray = convertToJSON(rs);
			int count =0 ;
		while (rs.next()) {

			SingleLineCubeResponseNew res = new SingleLineCubeResponseNew();
//			if(claimParamType.equals("GIC")){
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
//			count ++;
			
			
//			}else if(claimParamType.equals("NIC")){
				/*if(count==0){*/
//					res.setNicComprehensive(rs.getDouble(1));
//					res.setNicTp(rs.getDouble(2));
//					res.setNicOthers(rs.getDouble(3));
//					res.setNicTpComprehensive(nicTp);
					/*below code has to  be uncommented after category implementation*/
					/*res.setNicTpComprehensive(rs.getDouble(4));
					res.setNicTpTp(rs.getDouble(5));
					res.setNicTpOthers(rs.getDouble(6));*/
				/*}if(count==1){*/
//					res.setNicOdComprehensive(rs.getDouble(7));
//					res.setNicOdTp(rs.getDouble(8));
//					res.setNicOdOthers(rs.getDouble(9)); 
				
//			}
			
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
	List<SingleLineCubeResponseNew> getSingleLineCubeGicDataNewUw (HttpServletRequest req, UserMatrixMasterRequest filterRequest
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
			
			
			
			

			

//		if (filterRequest != null && filterRequest.getPolicyTypes() != null
//				&& !filterRequest.getPolicyTypes().isEmpty()) {
//			String vals = "";
//			for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
//				vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
//				if (i != filterRequest.getPolicyTypes().size() - 1) {
//					vals += ",";
//				}
//			}
//			
//			queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_NEW_CURRENT_TABLE.POLICY_TYPE) in (" + vals + ")";
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

	
	
	
	
  // Reserve Single Line APi for UnderWritten
	
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
				
//				res.setRslGic(rs.getDouble(1));
//				res.setRslCatGic(rs.getDouble(2));
//				res.setRslTheftGic(rs.getDouble(3));
//				res.setRslOtherGic(rs.getDouble(4));
//				res.setRslTpGic(rs.getDouble(5));
//				
//				
//				res.setRslNic(rs.getDouble(6));
//				res.setRslCatNic(rs.getDouble(7));
//				res.setRslTheftNic(rs.getDouble(8));
//				res.setRslOtherNic(rs.getDouble(9));
//				res.setRslTpNic(rs.getDouble(10));
				
				res.setRslGic(20006870);
				res.setRslCatGic(2079867000);
				res.setRslTheftGic(26780000);
				res.setRslOtherGic(26980000);
				res.setRslTpGic(200696800);
				
				
				res.setRslNic(2007986700);
				res.setRslCatNic(2000698760);
				res.setRslTheftNic(2069876000);
				res.setRslOtherNic(20068700);
				res.setRslTpNic(200698700);


		kpiResponseList.add(res);
	}
		
			
		}catch (Exception e) {
			System.out.println("Error occure when UR query running");
			System.out.println("kylinDataSource initialize error, ex: " + e);
			//System.out.println();
			//e.printStackTrace();
			
		}finally {
			connection.close();
		}
	   
				return kpiResponseList;
	   
   }
   
   
   // reserve single level api for financial year // 
   
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
			
//            2 channel new 
			 
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

//    	    res.setRslGic(rs.getDouble(1));
//			res.setRslCatGic(rs.getDouble(2));
//			res.setRslTheftGic(rs.getDouble(3));
//			res.setRslOtherGic(rs.getDouble(4));
//			res.setRslTpGic(rs.getDouble(5));
//			
//			
//			res.setRslNic(rs.getDouble(6));
//			res.setRslCatNic(rs.getDouble(7));
//			res.setRslTheftNic(rs.getDouble(8));
//			res.setRslOtherNic(rs.getDouble(9));
//			res.setRslTpNic(rs.getDouble(10));
			
			
			res.setRslGic(2000087);
			res.setRslCatGic(2000068);
			res.setRslTheftGic(2000680);
			res.setRslOtherGic(20008790);
			res.setRslTpGic(20009860);
			
			
			res.setRslNic(20007860);
			res.setRslCatNic(20078500);
			res.setRslTheftNic(20234000);
			res.setRslOtherNic(2058000);
			res.setRslTpNic(20058700);
			

				 kpiResponseList.add(res);
    }
	    }catch(Exception e) {
	    	System.out.println("Error occured when query running");
                System.out.println(e.getMessage());	    	
	    }
	    
		return kpiResponseList;
   }
   
   @GetMapping("/getR12NewCubeData")
   @ResponseBody
   public List<R12CubeResponse> getR12NewCubeData(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
   		 throws SQLException {
      
   			Connection connection = null;
   			List<R12CubeResponse> kpiResponseList = new ArrayList<R12CubeResponse>();
   			
   			long startTime = System.currentTimeMillis();
   			try {
   				String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
   				String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
   		
   		
   				System.out.println("SALVATION----------R12----------------SALVATION");
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
   		
   				queryStr += "SELECT" 
   				+" SUM(GEP_OD) AS GEP_OD,"
   				+" SUM(GEP_TP) AS GEP_TP,"
   				+" SUM(NEP_OD) AS NEP_OD,"
   				+" SUM(NEP_TP) AS NEP_TP,"
   				+" SUM(DISCOUNT_GEP_OD) AS DISCOUNT_GEP_OD,"
   				+" SUM(DISCOUNT_NEP_OD) AS DISCOUNT_NEP_OD,"
   				+" SUM(GEP_DEP_OD) AS GEP_DEP_OD,"
   				+" SUM(GEP_DEP_TP) AS GEP_DEP_TP,"
   				+" SUM(GEP_NCB_OD) AS GEP_NCB_OD,"
   				+" SUM(GEP_NCB_TP) AS GEP_NCB_TP,"
   				+" SUM(GEP_OTHER_ADDON_OD) AS GEP_OTHER_ADDON_OD,"
   				+" SUM(GEP_OTHER_ADDON_TP) AS GEP_OTHER_ADDON_TP,"
   				+" SUM(NEP_DEP_OD) AS NEP_DEP_OD,"
   				+" SUM(NEP_DEP_TP) AS NEP_DEP_TP,"
   				+" SUM(NEP_NCB_OD) AS NEP_NCB_OD,"
   				+" SUM(NEP_NCB_TP) AS NEP_NCB_TP,"
   				+" SUM(NEP_OTHER_ADDON_OD) AS NEP_OTHER_ADDON_OD,"
   				+" SUM(NEP_OTHER_ADDON_TP) AS NEP_OTHER_ADDON_TP,"
   				+" SUM(EARNED_POLICIES_OD) AS EARNED_POLICIES_OD,"
   				+" SUM(EARNED_POLICIES_TP) AS EARNED_POLICIES_TP,"
   				+" SUM(ADDON_EARNED_POLICIES_OD) AS ADDON_EARNED_POLICIES_OD,"
   				+" SUM(ADDON_EARNED_POLICIES_TP) AS ADDON_EARNED_POLICIES_TP,"
   				+" SUM(GIC_TPULR) AS GIC_TPULR,"
   				+" SUM(GIC_TPULR_DEP) AS GIC_TPULR_DEP,"
   				+" SUM(GIC_TPULR_NCB) AS GIC_TPULR_NCB,"
   				+" SUM(GIC_TPULR_OTHER_ADDON) AS GIC_TPULR_OTHER_ADDON,"
   				+" SUM(NIC_TPULR) AS NIC_TPULR,"
   				+" SUM(NIC_TPULR_DEP) AS NIC_TPULR_DEP,"
   				+" SUM(NIC_TPULR_NCB) AS NIC_TPULR_NCB,"
   				+" SUM(NIC_TPULR_OTHER_ADDON) AS NIC_TPULR_OTHER_ADDON,"
   				+" SUM(GWP_OD) AS GWP_OD,"
   				+" SUM(GWP_TP) AS GWP_TP,"
   				+" SUM(NWP_OD) AS NWP_OD,"
   				+" SUM(NWP_TP) AS NWP_TP,"
   				+" SUM(GWP_DEP_OD) AS GWP_DEP_OD,"
   				+" SUM(GWP_DEP_TP) AS GWP_DEP_TP,"
   				+" SUM(GWP_NCB_OD) AS GWP_NCB_OD,"
   				+" SUM(GWP_NCB_TP) AS GWP_NCB_TP,"
   				+" SUM(GWP_OTHER_ADDON_OD) AS GWP_OTHER_ADDON_OD,"
   				+" SUM(GWP_OTHER_ADDON_TP) AS GWP_OTHER_ADDON_TP,"
   				+" SUM(NWP_DEP_OD) AS NWP_DEP_OD,"
   				+" SUM(NWP_DEP_TP) AS NWP_DEP_TP,"
   				+" SUM(NWP_NCB_OD) AS NWP_NCB_OD,"
   				+" SUM(NWP_NCB_TP) AS NWP_NCB_TP,"
   				+" SUM(NWP_OTHER_ADDON_OD) AS NWP_OTHER_ADDON_OD,"
   				+" SUM(NWP_OTHER_ADDON_TP) AS NWP_OTHER_ADDON_TP,"
   				+" SUM(POLICY_COUNT_OD) AS POLICY_COUNT_OD,"
   				+" SUM(POLICY_COUNT_TP) AS POLICY_COUNT_TP,"
   				+" SUM(POLICY_COUNT_OTHERS) AS POLICY_COUNT_OTHERS,"
   				+" SUM(ADDON_POLICY_COUNT_OD) AS ADDON_POLICY_COUNT_OD,"
   				+" SUM(ADDON_POLICY_COUNT_TP) AS ADDON_POLICY_COUNT_TP,"
   				+" SUM(ADDON_POLICY_COUNT_OTHERS) AS ADDON_POLICY_COUNT_OTHERS,"
   				+" SUM(ACQ_COST_OD) AS ACQ_COST_OD,"
   				+" SUM(ACQ_COST_TP) AS ACQ_COST_TP,"
   				+" SUM(ADDON_ACQ_COST_OD) AS ADDON_ACQ_COST_OD,"
   				+" SUM(ADDON_ACQ_COST_TP) AS ADDON_ACQ_COST_TP,"
   				+" SUM(CAT_CLAIM_COUNT) AS CAT_CLAIM_COUNT,"
   				+" SUM(THEFT_CLAIM_COUNT) AS THEFT_CLAIM_COUNT,"
   				+" SUM(OTHER_CLAIM_COUNT) AS OTHER_CLAIM_COUNT,"
   				+" SUM(CLAIM_COUNT_TP) AS CLAIM_COUNT_TP,"
   				+" SUM(ADDON_CAT_COUNT) AS ADDON_CAT_COUNT,"
   				+" SUM(ADDON_THEFT_COUNT) AS ADDON_THEFT_COUNT,"
   				+" SUM(ADDON_OTHER_COUNT) AS ADDON_OTHER_COUNT,"
   				+" SUM(ADDON_CLAIM_COUNT_TP) AS ADDON_CLAIM_COUNT_TP,"
   				+" SUM(CAT_GIC) AS CAT_GIC,"
   				+" SUM(THEFT_GIC) AS THEFT_GIC,"
   				+" SUM(OTHER_GIC) AS OTHER_GIC,"
   				+" SUM(GIC_TP) AS GIC_TP,"
   				+" SUM(CAT_NIC) AS CAT_NIC,"
   				+" SUM(THEFT_NIC) AS THEFT_NIC,"
   				+" SUM(OTHER_NIC) AS OTHER_NIC,"
   				+" SUM(NIC_TP) AS NIC_TP,"
   				+" SUM(ADDON_CAT_GIC) AS ADDON_CAT_GIC,"
   				+" SUM(ADDON_THEFT_GIC) AS ADDON_THEFT_GIC,"
   				+" SUM(ADDON_OTHER_GIC) AS ADDON_OTHER_GIC,"
   				+" SUM(ADDON_GIC_TP) AS ADDON_GIC_TP,"
   				+" SUM(ADDON_CAT_NIC) AS ADDON_CAT_NIC,"
   				+" SUM(ADDON_THEFT_NIC) AS ADDON_THEFT_NIC,"
   				+" SUM(ADDON_OTHER_NIC) AS ADDON_OTHER_NIC,"
   				+" SUM(ADDON_NIC_TP) AS ADDON_NIC_TP,"
   				+" SUM(ESTIMATED_GIC) AS ESTIMATED_GIC,"
   				+" SUM(ESTIMATED_CAT_GIC) AS ESTIMATED_CAT_GIC,"
   				+" SUM(ESTIMATED_THEFT_GIC) AS ESTIMATED_THEFT_GIC,"
   				+" SUM(ESTIMATED_OTHER_GIC) AS ESTIMATED_OTHER_GIC,"
   				+" SUM(ESTIMATED_TP_GIC) AS ESTIMATED_TP_GIC,"
   				+" SUM(ESTIMATED_ADDON_GIC) AS ESTIMATED_ADDON_GIC,"
   				+" SUM(ESTIMATED_ADDON_CAT_GIC) AS ESTIMATED_ADDON_CAT_GIC,"
   				+" SUM(ESTIMATED_ADDON_THEFT_GIC) AS ESTIMATED_ADDON_THEFT_GIC,"
   				+" SUM(ESTIMATED_ADDON_OTHER_GIC) AS ESTIMATED_ADDON_OTHER_GIC,"
   				+" SUM(ESTIMATED_ADDON_TP_GIC) AS ESTIMATED_ADDON_TP_GIC,"
   				+" SUM(ESTIMATED_LOSS_RATIO) AS ESTIMATED_LOSS_RATIO,"
   				+" SUM(ESTIMATED_OD_LOSS_RATIO) AS ESTIMATED_OD_LOSS_RATIO,"
   				+" SUM(ESTIMATED_TP_LOSS_RATIO) AS ESTIMATED_TP_LOSS_RATIO,"
   				+" SUM(ESTIMATED_TPULR_LOSS_RATIO) AS ESTIMATED_TPULR_LOSS_RATIO,"
   				+" SUM(ESTIMATED_ADDON_LOSS_RATIO) AS ESTIMATED_ADDON_LOSS_RATIO,"
   				+" SUM(ESTIMATED_ADDON_OD_LOSS_RATIO) AS ESTIMATED_ADDON_OD_LOSS_RATIO,"
   				+" SUM(ESTIMATED_ADDON_TP_LOSS_RATIO) AS ESTIMATED_ADDON_TP_LOSS_RATIO,"
   				+" SUM(ESTIMATED_ADDON_TPULR_LOSS_RATIO) AS ESTIMATED_ADDON_TPULR_LOSS_RATIO,"
   				+" AVG(R12_FREQ) AS R12_FREQ,"
   				+" AVG(R12_CAT_FREQ) AS R12_CAT_FREQ,"
   				+" AVG(R12_THEFT_FREQ) AS R12_THEFT_FREQ,"
   				+" AVG(R12_OTHERS_FREQ) AS R12_OTHERS_FREQ,"
   				+" AVG(R12_TP_FREQ) AS R12_TP_FREQ,"
   				+" AVG(R12_ADDON_FREQ) AS R12_ADDON_FREQ,"
   				+" AVG(R12_ADDON_CAT_FREQ) AS R12_ADDON_CAT_FREQ,"
   				+" AVG(R12_ADDON_THEFT_FREQ) AS R12_ADDON_THEFT_FREQ,"
   				+" AVG(R12_ADDON_OTHERS_FREQ) AS R12_ADDON_OTHERS_FREQ,"
   				+" AVG(R12_ADDON_TP_FREQ) AS R12_ADDON_TP_FREQ,"
   				+" AVG(R12_SEVERITY) AS R12_SEVERITY,"
   				+" AVG(R12_CAT_SEVERITY) AS R12_CAT_SEVERITY,"
   				+" AVG(R12_THEFT_SEVERITY) AS R12_THEFT_SEVERITY,"
   				+" AVG(R12_OTHERS_SEVERITY) AS R12_OTHERS_SEVERITY,"
   				+" AVG(R12_TP_SEVERITY) AS R12_TP_SEVERITY,"
   				+" AVG(R12_ADDON_SEVERITY) AS R12_ADDON_SEVERITY,"
   				+" AVG(R12_ADDON_CAT_SEVERITY) AS R12_ADDON_CAT_SEVERITY,"
   				+" AVG(R12_ADDON_THEFT_SEVERITY) AS R12_ADDON_THEFT_SEVERITY,"
   				+" AVG(R12_ADDON_OTHERS_SEVERITY) AS R12_ADDON_OTHERS_SEVERITY,"
   				+" AVG(R12_ADDON_TP_SEVERITY) AS R12_ADDON_TP_SEVERITY,"
   				+" AVG(R12_LOSS_RATIO) AS R12_LOSS_RATIO,"
   				+" AVG(R12_OD_LOSS_RATIO) AS R12_OD_LOSS_RATIO,"
   				+" AVG(R12_TP_LOSS_RATIO) AS R12_TP_LOSS_RATIO,"
   				+" AVG(R12_TPULR_LOSS_RATIO) AS R12_TPULR_LOSS_RATIO,"
   				+" AVG(R12_ADDON_LOSS_RATIO) AS R12_ADDON_LOSS_RATIO,"
   				+" AVG(R12_ADDON_OD_LOSS_RATIO) AS R12_ADDON_OD_LOSS_RATIO,"
   				+" AVG(R12_ADDON_TP_LOSS_RATIO) AS R12_ADDON_TP_LOSS_RATIO,"
   				+" AVG(R12_ADDON_TPULR_LOSS_RATIO) AS R12_ADDON_TPULR_LOSS_RATIO,"
   				+" ((SUM(CAT_CLAIM_COUNT)+SUM(THEFT_CLAIM_COUNT)+SUM(OTHER_CLAIM_COUNT))/(SUM(POLICY_COUNT_OD)))*100 AS FREQ,"
   				+" ((SUM(CAT_CLAIM_COUNT))/(SUM(POLICY_COUNT_OD)))*100 AS CAT_FREQ,"
   				+" ((SUM(THEFT_CLAIM_COUNT))/(SUM(POLICY_COUNT_OD)))*100 AS THEFT_FREQ,"
   				+" ((SUM(OTHER_CLAIM_COUNT))/(SUM(POLICY_COUNT_OD)))*100 AS OTHER_FREQ,"
   				+" ((SUM(CAT_GIC)+SUM(THEFT_GIC)+SUM(OTHER_GIC))/(SUM(CAT_CLAIM_COUNT)+SUM(THEFT_CLAIM_COUNT)+SUM(OTHER_CLAIM_COUNT))) AS SEVERITY,"
   				+" ((SUM(CAT_GIC))/(SUM(CAT_CLAIM_COUNT)+SUM(THEFT_CLAIM_COUNT)+SUM(OTHER_CLAIM_COUNT))) AS CAT_SEVERITY,"
   				+" ((SUM(THEFT_GIC))/(SUM(CAT_CLAIM_COUNT)+SUM(THEFT_CLAIM_COUNT)+SUM(OTHER_CLAIM_COUNT))) AS THEFT_SEVERITY,"
   				+" ((SUM(OTHER_GIC))/(SUM(CAT_CLAIM_COUNT)+SUM(THEFT_CLAIM_COUNT)+SUM(OTHER_CLAIM_COUNT))) AS OTHER_SEVERITY,"
   				+" ((SUM(CAT_GIC)+SUM(THEFT_GIC)+SUM(OTHER_GIC)+SUM(GIC_TPULR))/(SUM(GEP_OD)+SUM(GEP_TP)))*100 AS LOSS_RATIO,"
   				+" ((SUM(CAT_GIC)+SUM(THEFT_GIC)+SUM(OTHER_GIC))/(SUM(GEP_OD)))*100 AS OD_LOSS_RATIO,"
   				+" ((SUM(GIC_TPULR))/(SUM(GEP_TP)))*100 AS TPulr_LOSS_RATIO,"
   				+" ((SUM(GIC_TP))/(SUM(GEP_TP)))*100 AS TP_LOSS_RATIO"
   				+" FROM RSDB.POLICY_GROUP_LEVEL_FACT_TABLE as POLICY_GROUP_LEVEL_FACT_TABLE"
   				+" LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER"
   				+" ON POLICY_GROUP_LEVEL_FACT_TABLE.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE"
   				+" LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER"
   				+" ON POLICY_GROUP_LEVEL_FACT_TABLE.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE"
   				+" LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER"
   				+" ON POLICY_GROUP_LEVEL_FACT_TABLE.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE"
   				+" LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER"
   				+" ON POLICY_GROUP_LEVEL_FACT_TABLE.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE";
   									
   				String finstartDate = fromYear + "-" + fromMonth + "-01";
   				String finEndDate = toYear + "-" + toMonth + "-31";
   				
   				queryStr += " WHERE  GEP_YEAR in ('"+fromYear+"') and  GEP_MONTH in ('01','02','03','04','05','06','07','08','09','10','11','12')";
   		
   				System.out.println("R12Query" + queryStr);
   				
   				// if (filterRequest != null && filterRequest.getPolicyTypes() != null
   				// 		&& !filterRequest.getPolicyTypes().isEmpty()) {
   				// 	String vals = "";
   				// 	for (int i = 0; i < filterRequest.getPolicyTypes().size(); i++) {
   				// 		vals += "'" + filterRequest.getPolicyTypes().get(i).trim() + "'";
   				// 		if (i != filterRequest.getPolicyTypes().size() - 1) {
   				// 			vals += ",";
   				// 		}
   				// 	}
   					
   				// 	queryStr += "and TRIM (POLICY_GROUP_LEVEL_FACT_TABLE.POLICY_TYPE) in  (" + vals + ")";
   				// }
   		
   				if(filterRequest != null && filterRequest.getPolicyTypeNew() != null && !filterRequest.getPolicyTypeNew().isEmpty()){
   					
   					String vals = "";
   					for (int i = 0; i < filterRequest.getPolicyTypeNew().size(); i++) {
   						vals += "'" + filterRequest.getPolicyTypeNew().get(i).trim() + "'";
   						if (i != filterRequest.getPolicyTypeNew().size() - 1) {
   							vals += ",";
   						}
   					}
   					queryStr += "and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.POLICY_TYPE_NEW) in  (" + vals + ")";
   		
   				}
   		
   				if(filterRequest != null && filterRequest.getChannelNew() != null && !filterRequest.getChannelNew().isEmpty()){
   					
   					String vals = "";
   					for (int i = 0; i < filterRequest.getChannelNew().size(); i++) {
   						vals += "'" + filterRequest.getChannelNew().get(i).trim() + "'";
   						if (i != filterRequest.getChannelNew().size() - 1) {
   							vals += ",";
   						}
   					}
   					queryStr += " and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.CHANNEL_NEW) in  (" + vals + ")";
   		
   				}
   		
   				if(filterRequest != null && filterRequest.getCategorisation() != null && !filterRequest.getCategorisation().isEmpty()){
   					
   					String vals = "";
   					for (int i = 0; i < filterRequest.getCategorisation().size(); i++) {
   						vals += "'" + filterRequest.getCategorisation().get(i).trim() + "'";
   						if (i != filterRequest.getCategorisation().size() - 1) {
   							vals += ",";
   						}
   					}
   					queryStr += "and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.CATEGORISATION) in(" + vals + ")";
   		
   				}
   		
   				if(filterRequest != null && filterRequest.getEngineCapacity() != null && !filterRequest.getEngineCapacity().isEmpty()){
   					
   					String vals = "";
   					for (int i = 0; i < filterRequest.getEngineCapacity().size(); i++) {
   						vals += "'" + filterRequest.getEngineCapacity().get(i).trim() + "'";
   						if (i != filterRequest.getEngineCapacity().size() - 1) {
   							vals += ",";
   						}
   					}
   					queryStr += " and (POLICY_GROUP_LEVEL_FACT_TABLE.ENGINECAPACITY) in (" + vals + ")";
   		
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
   					queryStr += "and (POLICY_GROUP_LEVEL_FACT_TABLE.VEHICLEAGE) in (" + vals + ")";
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
   					queryStr += " and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.BUSINESS_TYPE) in (" + vals + ")";
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
   					queryStr += " and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.CHANNEL) in (" + vals + ")";
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
   					queryStr += " and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.SUB_CHANNEL) in  (" + vals + ")";
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
   					queryStr += "and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.MAKE) in  (" + vals + ")";
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
   					queryStr += " and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.MODELGROUP) in (" + vals + ")";
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
   					queryStr += "and coalesce(POLICY_GROUP_LEVEL_FACT_TABLE.FUEL_TYPE,'N') in   (" + vals + ")";
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
   					queryStr += "and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.STATE_GROUPING) in (" + vals + ")";
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
   					queryStr += "and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.NCB_FLAG) in 	  (" + vals + ")";
   				}
   				
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
   				
   				// if (filterRequest != null && filterRequest.getMotorChannel() != null
   				// 		&& !filterRequest.getMotorChannel().isEmpty()) {
   				// 	String vals = "";
   				// 	for (int i = 0; i < filterRequest.getMotorChannel().size(); i++) {
   				// 		vals += "'" + filterRequest.getMotorChannel().get(i).trim() + "'";
   				// 		if (i != filterRequest.getMotorChannel().size() - 1) {
   				// 			vals += ",";
   				// 		}
   				// 	}
   				// 	queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.CHANNEL) in (" + vals + ")";
   				// }
   		
   		
   				// if (filterRequest != null && filterRequest.getMotorSubChannel() != null
   				// 		&& !filterRequest.getMotorSubChannel().isEmpty()) {
   				// 	String vals = "";
   				// 	for (int i = 0; i < filterRequest.getMotorSubChannel().size(); i++) {
   				// 		vals += "'" + filterRequest.getMotorSubChannel().get(i).trim() + "'";
   				// 		if (i != filterRequest.getMotorSubChannel().size() - 1) {
   				// 			vals += ",";
   				// 		}
   				// 	}
   				// 	queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.SUB_CHANNEL) in (" + vals + ")";
   				// }
   		
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
   					queryStr += " and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.BRANCH_CODE) in (" + vals + ")";
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
   					queryStr += "and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.AGENT_CODE) in  (" + vals + ")";
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
   					queryStr += " and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in  (" + vals + ")";
   				}
   		
   				// if (filterRequest != null && filterRequest.getMotorFuelType() != null
   				// 		&& !filterRequest.getMotorFuelType().isEmpty()) {
   				// 	String vals = "";
   				// 	for (int i = 0; i < filterRequest.getMotorFuelType().size(); i++) {
   				// 		vals += "'" + filterRequest.getMotorFuelType().get(i).trim() + "'";
   				// 		if (i != filterRequest.getMotorFuelType().size() - 1) {
   				// 			vals += ",";
   				// 		}
   				// 	}
   				// 	queryStr += " and TRIM(RSA_KPI_FACT_POLICY_FINAL_NOW.FUELTYPE) in (" + vals + ")";
   				// }
   				
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
   							queryStr += " and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.MODELCLASSIFICATION) in  (" + vals + ")";
   							 cvalcounter++;
   						 }else if(filterRequest.getMotorCarType().get(i).trim().equals("NHE")){
   							if(cvalNHEcounter==0)
   							queryStr += " and TRIM(POLICY_GROUP_LEVEL_FACT_TABLE.MODELCLASSIFICATION) in (" + nheVals + ")";
   							cvalNHEcounter++;
   						 }
   					
   						System.out.println("HE query------------------------------ " + queryStr);
   						
   					}
   					
   				}
   				
   				
   		
   				// if(filterRequest.getAddOnNew().equals("Include")){
   				// 	queryStr += " group by ADDON_TYPE,category ) x";
   				// }else if(filterRequest.getAddOnNew().equals("Exclude")){
   				// 	queryStr += " group by ADDON_TYPE,category ) x ) mm";
   				// }else if(filterRequest.getAddOnNew().equals("Only Addon")){
   				// 	queryStr += " group by ADDON_TYPE,category ) x) mm";
   				// }
   		
   				System.out.println("R12 queryStr------------------------------ " + queryStr);
   				ResultSet rs = stmt.executeQuery(queryStr);
   				System.out.println("START------------------------------ ");
   		
   				
   				while (rs.next()) {
   		
   					R12CubeResponse res = new R12CubeResponse();
   					res.setGepOd(rs.getDouble(1));
   					res.setGepTp(rs.getDouble(2));
   					res.setNepOd(rs.getDouble(3));
   					res.setNepTp(rs.getDouble(4));
   					res.setDiscountGepOd(rs.getDouble(5));
   					res.setDiscountNepOd(rs.getDouble(6));;
   					res.setGepDepOd(rs.getDouble(7));
   					res.setGepDepTp(rs.getDouble(8));
   					res.setGepNcbOd(rs.getDouble(9));
   					res.setGepNcbTp(rs.getDouble(10));
   					res.setGepOtherAddonOd(rs.getDouble(11));
   					res.setGepOtherAddonTp(rs.getDouble(12));
   					res.setNepDepOd(rs.getDouble(13));
   					res.setNepDepTp(rs.getDouble(14));
   					res.setNepNcbOd(rs.getDouble(15));
   					res.setNepNcbTp(rs.getDouble(16));
   					res.setNepOtherAddonOd(rs.getDouble(17));
   					res.setNepOtherAddonTp(rs.getDouble(18));
   					res.setEarnedPoliciesOd(rs.getDouble(19));
   					res.setEarnedPoliciesTp(rs.getDouble(20));
   					res.setAddonEarnedPoliciesOd(rs.getDouble(21));
   					res.setAddonEarnedPoliciesTp(rs.getDouble(22));
   					res.setGicTpUlr(rs.getDouble(23));
   					res.setGicTpUlrDep(rs.getDouble(24));
   					res.setGicTpUlrNcb(rs.getDouble(25));
   					res.setGicTpUlrOtherAddon(rs.getDouble(26));
   					res.setNicTpUlr(rs.getDouble(27));
   					res.setNicTpUlrDep(rs.getDouble(28));
   					res.setNicTpUlrNcb(rs.getDouble(29));
   					res.setNicTpUlrOtherAddon(rs.getDouble(29));
   					res.setGwpOd(rs.getDouble(30));
   					res.setGwpTp(rs.getDouble(31));
   					res.setNwpOd(rs.getDouble(32));
   					res.setNwpTp(rs.getDouble(33));
   					res.setGwpDepOd(rs.getDouble(34));
   					res.setGwpDepTp(rs.getDouble(35));
   					res.setGwpNcbOd(rs.getDouble(36));
   					res.setGwpNcbTp(rs.getDouble(37));
   					res.setGwpOtherAddonOd(rs.getDouble(38));
   					res.setGwpOtherAddonTp(rs.getDouble(39));
   					res.setNwpDepOd(rs.getDouble(40));
   					res.setNwpDepTp(rs.getDouble(41));
   					res.setNwpNcbOd(rs.getDouble(42));
   					res.setNwpNcbTp(rs.getDouble(43));
   					res.setNwpOtherAddonOd(rs.getDouble(44));
   					res.setNwpOtherAddonTp(rs.getDouble(45));
   					res.setPolicyCountTp(rs.getDouble(46));
   					res.setPolicyCountOd(rs.getDouble(47));
   					res.setPolicyCountOthers(rs.getDouble(48));
   					res.setAddonPolicyCountTp(rs.getDouble(49));
   					res.setAddonPolicyCountOd(rs.getDouble(50));
   					res.setAddonPolicyCountOthers(rs.getDouble(51));
   					res.setAcqCostOd(rs.getDouble(52));
   					res.setAcqCostTp(rs.getDouble(53));
   					res.setAddonAcqCostOd(rs.getDouble(54));
   					res.setAddonAcqCostTp(rs.getDouble(55));
   					res.setCatClaimCount(rs.getDouble(56));
   					res.setTheftClaimCount(rs.getDouble(57));
   					res.setOtherClaimCount(rs.getDouble(58));
   					res.setAddonCatCount(rs.getDouble(59));
   					res.setAddonTheftCount(rs.getDouble(60));
   					res.setAddonOtherCount(rs.getDouble(61));
   					res.setAddonClaimCountTp(rs.getDouble(62));
   					res.setCatGic(rs.getDouble(63));
   					res.setTheftGic(rs.getDouble(64));
   					res.setOtherGic(rs.getDouble(65));
   					res.setGicTp(rs.getDouble(66));
   					res.setCatNic(rs.getDouble(67));
   					res.setTheftNic(rs.getDouble(68));
   					res.setOtherNic(rs.getDouble(69));
   					res.setNicTp(rs.getDouble(70));
   					res.setAddonCatGic(rs.getDouble(71));
   					res.setAddonTheftGic(rs.getDouble(72));
   					res.setAddonOtherGic(rs.getDouble(73));
   					res.setAddonGicTp(rs.getDouble(74));
   					res.setAddonCatNic(rs.getDouble(75));
   					res.setAddonTheftNic(rs.getDouble(76));
   					res.setAddonOtherNic(rs.getDouble(77));
   					res.setAddonNicTp(rs.getDouble(78));
   					res.setEstimatedGic(rs.getDouble(79));
   					res.setEstimatedCatGic(rs.getDouble(80));
   					res.setEstimatedTheftGic(rs.getDouble(81));
   					res.setEstimatedOtherGic(rs.getDouble(82));
   					res.setEstimatedTpGic(rs.getDouble(83));
   					res.setEstimatedAddonGic(rs.getDouble(84));
   					res.setEstimatedAddonCatGic(rs.getDouble(85));
   					res.setEstimatedAddonTheftGic(rs.getDouble(86));
   					res.setEstimatedAddonOtherGic(rs.getDouble(87));
   					res.setEstimatedAddonTpGic(rs.getDouble(88));
   					res.setEstimatedLossRatio(rs.getDouble(89));
   					res.setEstimatedOdLossRatio(rs.getDouble(90));
   					res.setEstimatedTpLossRatio(rs.getDouble(91));
   					res.setEstimatedTpUlrLossRatio(rs.getDouble(92));
   					res.setEstimatedAddonLossRatio(rs.getDouble(93));
   					res.setEstimatedAddonOdLossRatio(rs.getDouble(94));
   					res.setEstimatedAddonTpLossRatio(rs.getDouble(95));
   					res.setEstimatedAddonTpUlrLossRatio(rs.getDouble(96));
   					res.setR12Freq(rs.getDouble(97));
   					res.setR12CatFreq(rs.getDouble(98));
   					res.setR12TheftFreq(rs.getDouble(99));
   					res.setR12OthersFreq(rs.getDouble(100));
   					res.setR12TpFreq(rs.getDouble(101));
   					res.setR12AddonFreq(rs.getDouble(102));
   					res.setR12AddonCatFreq(rs.getDouble(103));
   					res.setR12AddonTheftFreq(rs.getDouble(104));
   					res.setR12AddonOthersFreq(rs.getDouble(105));
   					res.setR12AddonTpFreq(rs.getDouble(106));
   					res.setR12Severity(rs.getDouble(107));
   					res.setR12CatSeverity(rs.getDouble(108));
   					res.setR12TheftSeverity(rs.getDouble(109));
   					res.setR12OthersSeverity(rs.getDouble(110));
   					res.setR12TpSeverity(rs.getDouble(111));
   					res.setR12AddonSeverity(rs.getDouble(112));
   					res.setR12AddonCatSeverity(rs.getDouble(113));
   					res.setR12AddonTheftSeverity(rs.getDouble(114));
   					res.setR12AddonOthersSeverity(rs.getDouble(115));
   					res.setR12AddonTpSeverity(rs.getDouble(116));
   					res.setR12LossRatio(rs.getDouble(117));
   					res.setR12OdLossRatio(rs.getDouble(118));
   					res.setR12TpLossRatio(rs.getDouble(119));
   					res.setR12TpUlrLossRatio(rs.getDouble(120));
   					res.setR12AddonLossRatio(rs.getDouble(121));
   					res.setR12AddonOdLossRatio(rs.getDouble(122));
   					res.setR12AddonTpLossRatio(rs.getDouble(123));
   					res.setR12AddonTpUlrLossRatio(rs.getDouble(124));
   					res.setFreq(rs.getDouble(125));
   					res.setCatFreq(rs.getDouble(126));
   					res.setTheftFreq(rs.getDouble(127));
   					res.setOtherFreq(rs.getDouble(128));
   					res.setSeverity(rs.getDouble(129));
   					res.setCatSeverity(rs.getDouble(130));
   					res.setTheftSeverity(rs.getDouble(131));
   					res.setOtherSeverity(rs.getDouble(132));
   					res.setLossRatio(rs.getDouble(133));
   					res.setOdLossRatio(rs.getDouble(134));
   					res.setTpUlrLossRatio(rs.getDouble(135));
   					res.setTpLossRatio(rs.getDouble(136));
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
   			System.out.println("response list------------------------------ " + kpiResponseList);
   		
   			return kpiResponseList;
   		}
   
   
   
    @GetMapping("/getReserveSingleLineCubeGicDataNew/{claimType}/{claimParamType}")
	@ResponseBody
	public List<ReserverSingleLineCubeResponseNew> getReserveSingleLineCubeGicDataNew(HttpServletRequest req, UserMatrixMasterRequest filterRequest,
			@PathVariable(value="claimParamType") String claimParamType,
			@PathVariable(value="claimType") String claimType)
			throws SQLException {
		Connection connection = null;
		List<ReserverSingleLineCubeResponseNew> kpiResponseList = new ArrayList<ReserverSingleLineCubeResponseNew>();
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
			String queryStr = "";
			// Query in error
			if(claimParamType.equals("GIC")){
				queryStr = "SELECT  "
						+ "SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='DEP' THEN RSL_GIC ELSE 0 END)  as rsl_cat_gic_od_policy_comp_dep, "
						+ "SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='NCB'  THEN RSL_GIC ELSE 0 END)  as rsl_cat_gic_od_policy_comp_ncb,  "
						+ "SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='OTHER'  THEN RSL_GIC ELSE 0 END) as rsl_cat_gic_od_policy_comp_other_addon,  "
						+ "SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='NONE'  THEN RSL_GIC ELSE 0 END) as rsl_cat_gic_od_policy_comp_nil_addon,"
						+ "0 as rsl_cat_gic_od_policy_tp_dep,"
						+ "0 as rsl_cat_gic_od_policy_tp_ncb,"
						+ "0 as rsl_cat_gic_od_policy_tp_OTHER_ADDON,"
						+ "0 as rsl_cat_gic_od_policy_tp_NIL_ADDON,"
						+ "0 as rsl_cat_gic_od_policy_others_dep,"
						+ "0 as rsl_cat_gic_od_policy_others_ncb,"
						+ "0 as rsl_cat_gic_od_policy_others_other_addon,"
						+ "0 as rsl_cat_gic_od_policy_others_nil_addon,  "
						+ "SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='DEP' THEN RSL_GIC ELSE 0 END) as rsl_theft_gic_od_policy_comp_dep,"
						+ "SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='NCB' THEN RSL_GIC ELSE 0 END) as rsl_theft_gic_od_policy_comp_ncb,  "
						+ "SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='OTHER' THEN RSL_GIC ELSE 0 END) as rsl_theft_gic_od_policy_comp_other_addon,  "
						+ "SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='NONE' THEN RSL_GIC ELSE 0 END) as rsl_theft_gic_od_policy_comp_nil_addon,"
						+ "0 as rsl_theft_gic_od_policy_tp_dep,"
						+ "0 as rsl_theft_gic_od_policy_tp_ncb,"
						+ "0 as rsl_theft_gic_od_policy_tp_other_addon,"
						+ "0 as rsl_theft_gic_od_policy_tp_nil_addon,"
						+ "0 as rsl_theft_gic_od_policy_others_dep,"
						+ "0 as rsl_theft_gic_od_policy_others_ncb,"
						+ "0 as rsl_theft_gic_od_policy_others_other_addon,"
						+ "0 as rsl_theft_gic_od_policy_others_nil_addon,"
						+ "SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and (csl_claim_type not in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='DEP' ) THEN RSL_GIC ELSE 0 END) as rsl_other_gic_od_policy_comp_dep,  "
						+ "SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and (csl_claim_type not in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='NCB' ) THEN RSL_GIC ELSE 0 END) as rsl_other_gic_od_policy_comp_ncb,  "
						+ "SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and (csl_claim_type not in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='OTHER' ) THEN RSL_GIC ELSE 0 END) as rsl_other_gic_od_policy_comp_other_addon,  "
						+ "SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and (csl_claim_type not in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO NOT LIKE 'TP%' and ADDON_TYPE='NONE' ) THEN RSL_GIC ELSE 0 END) as rsl_other_gic_od_policy_comp_nil_addon,"
						+ "0 as rsl_other_gic_od_policy_tp_dep,"
						+ "0 as rsl_other_gic_od_policy_tp_ncb,"
						+ "0 as rsl_other_gic_od_policy_tp_other_addon,"
						+ "0 as rsl_other_gic_od_policy_tp_nil_addon,"
						+ "0 as rsl_other_gic_od_policy_others_dep,"
						+ "0 as rsl_other_gic_od_policy_others_ncb,"
						+ "0 as rsl_other_gic_od_policy_others_other_addon,"
						+ "0 as rsl_other_gic_od_policy_others_nil_addon,"
						+ "SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV'))  AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='DEP' THEN RSL_GIC ELSE 0 END) as rsl_cat_gic_tp_policy_comp_dep,  "
						+ "SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV'))  AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='NCB' THEN RSL_GIC ELSE 0 END) as rsl_cat_gic_tp_policy_comp_ncb,  "
						+ "SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV'))  AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='OTHER' THEN RSL_GIC ELSE 0 END) as rsl_cat_gic_tp_policy_comp_other_addon,  "
						+ "SUM(CASE WHEN (csl_claim_type in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV'))  AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='NONE' THEN RSL_GIC ELSE 0 END) as rsl_cat_gic_tp_policy_comp_nil_addon,"
						+ "0 as rsl_cat_gic_tp_policy_tp_dep,"
						+ "0 as rsl_cat_gic_tp_policy_tp_ncb,"
						+ "0 as rsl_cat_gic_tp_policy_tp_other_addon,"
						+ "0 as rsl_cat_gic_tp_policy_tp_nil_addon,"
						+ "0 as rsl_cat_gic_tp_policy_others_dep,"
						+ "0 as rsl_cat_gic_tp_policy_others_ncb,"
						+ "0 as rsl_cat_gic_tp_policy_others_other_addon,"
						+ "0 as rsl_cat_gic_tp_policy_others_nil_addon,"
						+ "SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='DEP' THEN RSL_GIC ELSE 0 END) as rsl_theft_gic_tp_policy_comp_dep,  "
						+ "SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='NCB' THEN RSL_GIC ELSE 0 END) as rsl_theft_gic_tp_policy_comp_ncb,  "
						+ "SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='OTHER' THEN RSL_GIC ELSE 0 END) as rsl_theft_gic_tp_policy_comp_other_addon,  "
						+ "SUM(CASE WHEN CSL_NATURE_OF_CLAIM='VTFO' AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='NONE' THEN RSL_GIC ELSE 0 END) as rsl_theft_gic_tp_policy_comp_nil_addon,"
						+ "0 as rsl_theft_gic_tp_policy_tp_dep,"
						+ "0 as rsl_theft_gic_tp_policy_tp_ncb,"
						+ "0 as rsl_theft_gic_tp_policy_tp_other_addon,"
						+ "0 as rsl_theft_gic_tp_policy_tp_nil_addon,"
						+ "0 as rsl_theft_gic_tp_policy_others_dep,"
						+ "0 as rsl_theft_gic_tp_policy_others_ncb,"
						+ "0 as rsl_theft_gic_tp_policy_others_other_addon,"
						+ "0 as rsl_theft_gic_tp_policy_others_nil_addon,"
						+ "SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and (csl_claim_type not in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='DEP' ) THEN RSL_GIC ELSE 0 END) as rsl_other_gic_tp_policy_comp_dep,  "
						+ "SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and (csl_claim_type not in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='NCB' ) THEN RSL_GIC ELSE 0 END) as rsl_other_gic_tp_policy_comp_ncb,  "
						+ "SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and (csl_claim_type not in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='OTHER' ) THEN RSL_GIC ELSE 0 END) as rsl_other_gic_tp_policy_comp_other_addon,  "
						+ "SUM(CASE WHEN (CSL_NATURE_OF_CLAIM<>'VTFO' and (csl_claim_type not in ('MUTA','PUBB',"
						+ "'VCAT','MCAT','HURR','ERTQ','MFLD','CFLD','TMPS','OFLD','FIKA','VARD','MFL3','PRVI','MFL4','KFLD','KMFD','CYCL','JCAT','KFL2','TSU',"
						+ "'OCAT','FAST','BFLD','CAT1','NVAR','FANI','CCAT','UKND','AILA','KRC','MCT1','CCT2','ATFD','FLDG','TANE','CCT1','KAFL','COVD','UKFL',"
						+ "'MH07','NSGA','GFL2','STRM','GAJA','WFLD','TFLD','CFL2','N-EQ','NISA','GCAT','GFLD','ERKO','CAMP','MFL2','YANT',"
						+ "'APLV')) AND CSL_CLAIM_NO LIKE 'TP%' and ADDON_TYPE='NONE' ) THEN RSL_GIC ELSE 0 END) as rsl_other_gic_tp_policy_comp_nil_addon,"
						+ "0 as rsl_other_gic_tp_policy_tp_dep,"
						+ "0 as rsl_other_gic_tp_policy_tp_ncb,"
						+ "0 as rsl_other_gic_tp_policy_tp_other_addon,"
						+ "0 as rsl_other_gic_tp_policy_tp_nil_addon,"
						+ "0 as rsl_other_gic_tp_policy_others_dep,"
						+ "0 as rsl_other_gic_tp_policy_others_ncb,"
						+ "0 as rsl_other_gic_tp_policy_others_other_addon,"
						+ "0 as rsl_other_gic_tp_policy_others_nil_addon "
						+ "from (  SELECT   RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_CATASTROPHECODE  ,RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_CLAIM_TYPE, CATASTROPHIC_MASTER.CAT_TYPE   ,RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_NATURE_OF_CLAIM,  CSL_CLAIM_NO, category,ADDON_TYPE, SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_CLOSING_BALANCE+RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_CLAIM_TRANS_AMT-RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_OPENING_TOTAL) RSL_GIC  FROM RSDB.RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL as RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL  LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE  LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE ";
			}else if(claimParamType.equals("NIC")){
				
				queryStr	="SELECT  "
						+ "sum(case when ADDON_TYPE='DEP' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) as rsl_NIC_policy_comp_dep,"
						+ "sum(case when ADDON_TYPE='NCB' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) as rsl_NIC_policy_comp_ncb,"
						+ "sum(case when ADDON_TYPE='OTHER' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) as rsl_NIC_policy_comp_other_addon,  "
						+ "sum(case when ADDON_TYPE='NONE' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) as rsl_NIC_policy_comp_nil_addon,"
						+ "0 as rsl_NIC_policy_tp_dep,"
						+ "0 as rsl_NIC_policy_tp_ncb,"
						+ "0 as rsl_NIC_policy_tp_other_addon,"
						+ "0 as rsl_NIC_policy_tp_nil_addon,"
						+ "0 as rsl_NIC_policy_others_dep,"
						+ "0 as rsl_NIC_policy_others_ncb,"
						+ "0 as rsl_NIC_policy_others_other_addon,"
						+ "0 as rsl_NIC_policy_others_nil_addon,"
						+ "sum(case when CSL_CLAIM_NO LIKE 'TP%' and category='TP' and ADDON_TYPE='DEP' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) rsl_nic_tp_policy_comp_dep,"
						+ "sum(case when CSL_CLAIM_NO LIKE 'TP%' and category='TP' and ADDON_TYPE='NCB' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) rsl_nic_tp_policy_comp_ncb,"
						+ "sum(case when CSL_CLAIM_NO LIKE 'TP%' and category='TP' and ADDON_TYPE='OTHER' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) rsl_nic_tp_policy_comp_other_addon,  "
						+ "sum(case when CSL_CLAIM_NO LIKE 'TP%' and category='TP' and ADDON_TYPE='NONE' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) rsl_nic_tp_policy_comp_nil_addon,"
						+ "0 as rsl_nic_tp_policy_tp_dep,"
						+ "0 as rsl_nic_tp_policy_tp_ncb,"
						+ "0 as rsl_nic_tp_policy_tp_other_addon,"
						+ "0 as rsl_nic_tp_policy_tp_nil_addon,"
						+ "0 as rsl_nic_tp_policy_others_dep,"
						+ "0 as rsl_nic_tp_policy_others_ncb,"
						+ "0 as rsl_nic_tp_policy_others_other_addon,"
						+ "0 as rsl_nic_tp_policy_others_nil_addon,"
						+ "sum(case when CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' and ADDON_TYPE='DEP' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) rsl_nic_od_policy_comp_dep,"
						+ "sum(case when CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' and ADDON_TYPE='NCB' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) rsl_nic_od_policy_comp_ncb,"
						+ "sum(case when CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' and ADDON_TYPE='OTHER' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) rsl_nic_od_policy_comp_other_addon,  "
						+ "sum(case when CSL_CLAIM_NO NOT LIKE 'TP%' and category='Comprehensive' and ADDON_TYPE='NONE' then RSL_GIC*(1-QUOTA_SHARE-OBLIGATORY) else 0 end) rsl_nic_od_policy_comp_nil_addon,"
						+ "0 as rsl_nic_od_policy_tp_dep,"
						+ "0 as rsl_nic_od_policy_tp_ncb,"
						+ "0 as rsl_nic_od_policy_tp_other_addon,"
						+ "0 as rsl_nic_od_policy_tp_nil_addon,"
						+ "0 as rsl_nic_od_policy_others_dep,"
						+ "0 as rsl_nic_od_policy_others_ncb,"
						+ "0 as rsl_nic_od_policy_others_other_addon,"
						+ "0 as rsl_nic_od_policy_others_nil_addon "
						+ "FROM ( SELECT  SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_CLOSING_BALANCE+RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_CLAIM_TRANS_AMT-RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_OPENING_TOTAL) RSL_GIC,CSL_CLAIM_NO,CSL_MVMT_MONTH,category,ADDON_TYPE,uw_year,RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.PRODUCT_CODE,'NONE' BAND   FROM RSDB.RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL as RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL    LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE    LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE    LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE    LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE  LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE    LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE   LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY  LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE    LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE   LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CITY_CODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE ";
						
			}
			
						

			
			String finstartDate = fromYear + "-" + fromMonth + "-01";
			String finEndDate = toYear + "-" + toMonth + "-31";
			
			if(claimType.equalsIgnoreCase("R")){
				queryStr += " WHERE ( CSL_MVMT_MONTH between " + fromYear +fromMonth+ " and " + toYear +toMonth+ " )";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BUSINESS_TYPE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MAKE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MODELGROUP) in (" + vals + ")";
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
					queryStr += " and coalesce(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.FUELTYPE,'N') in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.NCB_FLAG) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.SUB_CHANNEL) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.FUELTYPE) in (" + vals + ")";
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
					queryStr += " and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.NCB_FLAG) in (" + vals + ")";
				}
				
		
		if(claimParamType.equals("GIC")){
			queryStr += " group by RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_CATASTROPHECODE ,RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_CLAIM_TYPE,CATASTROPHIC_MASTER.CAT_TYPE ,RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_NATURE_OF_CLAIM,CSL_CLAIM_NO,category,ADDON_TYPE ) x ";
		}
		else if(claimParamType.equals("NIC")){
			queryStr +=" GROUP by   "+
					" uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH,category,ADDON_TYPE ) A , "+  
					" (select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 "+  
					" group by underwriting_year,XGEN_PRODUCTCODE,band) B   "+
					" where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band ";
					/*" ) ";*/
		}
		
		
		
			

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

//				ReserverSingleLineCubeResponseNew res = new ReserverSingleLineCubeResponseNew();
//				if(claimParamType.equals("GIC")){
//				res.setCatGicOdComprehensiveDep(rs.getDouble(1));
//				res.setCatGicOdComprehensiveNcb(rs.getDouble(2));
//				res.setCatGicOdComprehensiveOtherAddon(rs.getDouble(3));
//				res.setCatGicOdComprehensiveNoAddon(rs.getDouble(4));
//				res.setCatGicOdTpDep(rs.getDouble(5));
//				res.setCatGicOdTpNcb(rs.getDouble(6));
//				res.setCatGicOdTpOtherAddon(rs.getDouble(7));
//				res.setCatGicOdTpNoAddon(rs.getDouble(8));
//				res.setCatGicOdOthersDep(rs.getDouble(9));
//				res.setCatGicOdOthersNcb(rs.getDouble(10));
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
//				kpiResponseList.add(res);
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
		list.add("GIC_TP");
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

	public String getFinCondQuery(int fromMonth,int toMonth,int fromYear,int toYear){
		
		String txt = ""; 
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
		
		if(fromYear == toYear && fromMonth==toMonth){
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

// @GetMapping("/getGicNic")
// public List<UserRole> getGicNic(){
// 	try{
// 		System.out.println("-----call---- callR12GicNic ---- Start");
// 		List<UserRole> result = gicNicPsqlRepository.findAll();

// 		System.out.println("-----call---- callR12GicNic ---- Success");
// 		return result;
// 	}catch (Exception e) {
// 		System.out.println("-----call---- callR12GicNic ---- Failed");
// 		return null;
// 	}
// }


/*@GetMapping("/getSubChannelByChannel/{channelId}")
@ResponseBody
public List<String> getSubChannelByChannel()
		 {
	System.out.println("called getSubChannelByChannel()::");
	List<IntermediaryMaster> list = subChannelMasterRepository.findByChannelName("");
	System.out.println("list size-->"+list.size());
	return list;
}*/

 /* SELECT  
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_GIC) as RSL_GIC,  
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_CAT_GIC) as RSL_CAT_GIC,  
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_THEFT_GIC) as RSL_THEFT_GIC,  
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_OTHER_GIC) as RSL_OTHER_GIC,
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_TP_GIC) as RSL_TP_GIC,  
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_NIC) as RSL_NIC,  
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_CAT_NIC) as RSL_CAT_NIC,  
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_THEFT_NIC) as RSL_THEFT_NIC,  
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_OTHER_NIC) as RSL_OTHER_NIC,  
SUM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.RSL_TP_NIC) as RSL_TP_NIC
FROM RSDB.RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL as RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL
LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  
LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  
LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  
LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  
LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  
LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  
LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  
LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  
LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  
LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE
WHERE (SUBSTRING(inception_date,1,10) >='2019-04-01' and SUBSTRING(inception_date,1,10) <='2020-03-31')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CHANNEL) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CHANNEL_NEW) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.SUB_CHANNEL) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE) in ('')
and coalesce(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.FUELTYPE,'N') in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.NCB_FLAG) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.BUSINESS_TYPE) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.MAKE) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.MODELGROUP) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.POLICY_TYPE) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.POLICY_TYPE_NEW) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.CATEGORISATION) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.VEHICLEAGE) in ('')
and TRIM(RSA_KPI_FACT_UW_RESERVE_SINGLE_LINE_FINAL.ENGINECAPACITY) in ('')
and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in ('')
and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in ('')
and TRIM(KPI_BRANCH_MASTER.ZONE) in ('')
and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in ('')
and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in ('')
and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in ('')
and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in ('');    */


/* SELECT  
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_GIC) as RSL_GIC,  
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_CAT_GIC) as RSL_CAT_GIC,  
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_THEFT_GIC) as RSL_THEFT_GIC,  
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_OTHER_GIC) as RSL_OTHER_GIC,
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_TP_GIC) as RSL_TP_GIC,  
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_NIC) as RSL_NIC,  
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_CAT_NIC) as RSL_CAT_NIC,  
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_THEFT_NIC) as RSL_THEFT_NIC,  
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_OTHER_NIC) as RSL_OTHER_NIC,  
SUM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.RSL_TP_NIC) as RSL_TP_NIC
FROM RSDB.RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL as RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL
LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  
LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  
LEFT JOIN RSDB.RSA_DWH_MODEL_MASTER_CURRENT as RSA_DWH_MODEL_MASTER_CURRENT  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MODELCODE = RSA_DWH_MODEL_MASTER_CURRENT.MODEL_CODE  
LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  
LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  
LEFT JOIN RSDB.RSA_DWH_INTERMEDIARY_MASTER as RSA_DWH_INTERMEDIARY_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE = RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_CODE  
LEFT JOIN RSDB.RSA_DWH_COVERCODE_MASTER as RSA_DWH_COVERCODE_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.COVER_CODE = RSA_DWH_COVERCODE_MASTER.COVER_CODE  
LEFT JOIN RSDB.RSA_DWH_CITY_MASTER_NOW as RSA_DWH_CITY_MASTER_NOW  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.REGLOCATION = RSA_DWH_CITY_MASTER_NOW.CITYNAME  
LEFT JOIN RSDB.RSA_DWH_CITY_GROUPING_MASTER_FINAL as RSA_DWH_CITY_GROUPING_MASTER_FINAL  ON RSA_DWH_CITY_MASTER_NOW.CITYCODE = RSA_DWH_CITY_GROUPING_MASTER_FINAL.CITYCODE  
LEFT JOIN RSDB.CATASTROPHIC_MASTER as CATASTROPHIC_MASTER  ON RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CSL_CATASTROPHICTYPE = CATASTROPHIC_MASTER.CAT_TYPE
WHERE ( CSL_MVMT_MONTH between 201904 and 202003 )
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CHANNEL) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CHANNEL_NEW) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.SUB_CHANNEL) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.AGENT_CODE) in ('')
and coalesce(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.FUELTYPE,'N') in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.NCB_FLAG) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BRANCH_CODE) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.BUSINESS_TYPE) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MAKE) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.MODELGROUP) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.POLICY_TYPE) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.POLICY_TYPE_NEW) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.CATEGORISATION) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.VEHICLEAGE) in ('')
and TRIM(RSA_KPI_FACT_RESERVE_SINGLE_LINE_FINAL.ENGINECAPACITY) in ('')
and TRIM(RSA_DWH_INTERMEDIARY_MASTER.INTERMEDIARY_NAME) in ('')
and TRIM(RSA_DWH_MODEL_MASTER_CURRENT.MODELCLASSIFICATION) in ('')
and TRIM(KPI_BRANCH_MASTER.ZONE) in ('')
and TRIM(KPI_BRANCH_MASTER.CLUSTER_NAME) in ('')
and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in ('')
and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in ('')
and TRIM(RSA_DWH_CITY_GROUPING_MASTER_FINAL.STATE_GROUPING) in (''); */


}