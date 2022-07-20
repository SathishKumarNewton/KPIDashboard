
package com.prodian.rsgirms.dashboard.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.prodian.rsgirms.constants.RMSConstants;
import com.prodian.rsgirms.dashboard.model.BranchMaster;
import com.prodian.rsgirms.dashboard.model.ModelMaster;
import com.prodian.rsgirms.dashboard.model.MonthlyDashboardDetails;
import com.prodian.rsgirms.dashboard.model.ProductMaster;
import com.prodian.rsgirms.dashboard.model.SubChannelMaster;
import com.prodian.rsgirms.dashboard.model.UserDashboard;
import com.prodian.rsgirms.dashboard.repository.MonthlyDashboardDetailsRepository;
import com.prodian.rsgirms.dashboard.repository.ProductMasterRepository;
import com.prodian.rsgirms.dashboard.repository.SubChannelMasterRepository;
import com.prodian.rsgirms.dashboard.repository.UserDashboardRepository;
import com.prodian.rsgirms.dashboard.response.BudgetCubeResponse;
import com.prodian.rsgirms.dashboard.response.ClaimsCubeResponse;
import com.prodian.rsgirms.dashboard.response.ClaimsSingleLineCubeResponse;
import com.prodian.rsgirms.dashboard.response.CubeAKpiResponse;
import com.prodian.rsgirms.dashboard.response.GepCubeResponse;
import com.prodian.rsgirms.dashboard.response.KpiFiltersResponse;
import com.prodian.rsgirms.dashboard.service.KpiDashboardService;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterRequest;
import com.prodian.rsgirms.usermatrix.service.UserMatrixService;

@Controller
public class KpiControllerBk {

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

	
	//@GetMapping("/motorKpiNew")
	public ModelAndView getMockMotorKpiDashBoard() {
		ModelAndView model = new ModelAndView("motorKpiNew");
		KpiFiltersResponse res = kpiDashboardService.getKpiFilters();
//		KpiFiltersResponse res = new KpiFiltersResponse();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();

		List<Integer> dashboardIds = userDashboardRepository.findByUserId(userId).stream()
				.map(UserDashboard::getDashboardId).collect(Collectors.toList());
		UserMatrixMasterRequest req = new UserMatrixMasterRequest();
		req.setDashboardId(dashboardIds);
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
		
		return model;
	}
	
	//@GetMapping("/getBudgetCubeData")
	@ResponseBody
	public List<BudgetCubeResponse> getBudgetCubeData(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
			throws SQLException {
		List<BudgetCubeResponse> kpiResponseList = new ArrayList<BudgetCubeResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {

			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
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
			String budgetYear = "";
			String budgetFromMonth = "";
			String budgetToMonth = "";

			List<ProductMaster> productMasters = productMasterRepository.findAll();
			List<SubChannelMaster> subChannelMasters = subChannelMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductDescription)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductDescription)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String queryStr = "select sum(gwp),sum(nwp),sum(nep),sum(nic) from BUDGET_DATS ";

			if (fromYear.equals(toYear)) {
				queryStr += " where budget_year = " + (fromYear + (Integer.parseInt(toYear) + 1))
						+ " and budget_month >= " + fromMonth + " and budget_month <= " + toMonth;

			} else {
				budgetYear = fromYear + toYear;
				Integer bFromMon = (Integer.parseInt(fromMonth));
				Integer bToMon = (Integer.parseInt(toMonth));
				//4-19 - 3-20
				queryStr += " where budget_year = "+(fromYear+toYear) +" and"
						+ " (budget_month>= "+fromMonth+" or budget_month <="+toMonth+")";
			}
			
			

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				
				String vals = "'"+productMasters.stream().filter(f->filterRequest.getGeneralProduct().contains(f.getProductCode())).map(ProductMaster::getProductDescription)
						.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

				queryStr += " and TRIM(budget_dats.PRODUCT) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "'"+productMasters.stream().filter(f->filterRequest.getMotorProduct().contains(f.getProductCode())).map(ProductMaster::getProductDescription)
						.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";
				queryStr += " and TRIM(budget_dats.PRODUCT) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

				queryStr += " and TRIM(budget_dats.PRODUCT) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "'"+productMasters.stream().filter(f->filterRequest.getHealthProduct().contains(f.getProductCode())).map(ProductMaster::getProductDescription)
						.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";
				queryStr += " and TRIM(budget_dats.PRODUCT) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
				queryStr += " and TRIM(budget_dats.PRODUCT) in (" + healthProductVals + ")";
			}
			
			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				
				String vals = "'"+subChannelMasters.stream().filter(f->filterRequest.getGeneralChannel().contains(f.getChannelName())).map(SubChannelMaster::getChannelName)
						.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

				queryStr += " and TRIM(budget_dats.channel) in (" + vals + ")";
								
			}

			if (filterRequest != null && filterRequest.getMotorChannel() != null
					&& !filterRequest.getMotorChannel().isEmpty()) {
				String vals = "'"+subChannelMasters.stream().filter(f->filterRequest.getGeneralChannel().contains(f.getChannelName())).map(SubChannelMaster::getChannelName)
						.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

				queryStr += " and TRIM(budget_dats.channel) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "'"+subChannelMasters.stream().filter(f->filterRequest.getGeneralChannel().contains(f.getChannelName())).map(SubChannelMaster::getChannelName)
						.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

				queryStr += " and TRIM(budget_dats.channel) in (" + vals + ")";
			}

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				BudgetCubeResponse res = new BudgetCubeResponse();
				res.setGwp(rs.getDouble(1));
				res.setNwp(rs.getDouble(2));
				res.setNep(rs.getDouble(3));
				res.setNic(rs.getDouble(4));

				kpiResponseList.add(res);
			}

			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}
		System.out.println("CALLED THE METHOD");
		return kpiResponseList;
	}

	
	//@RequestMapping(value = "/getPolicyCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CubeAKpiResponse> getKpiPolicyData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<CubeAKpiResponse> kpiResponseList = new ArrayList<CubeAKpiResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
//			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];

			String queryStr = "select\r\n" + "sum(livescovered) livescovered,\r\n"
					+ "sum(writtenPolicies) writtenPolicies,\r\n" + "sum(acq_cost) acq_cost\r\n" + "from(\r\n"
					+ "select\r\n" + "sum(RSA_KPI_FACT_POLICY.LIVESCOVERED) livescovered,\r\n"
					+ "sum(case when ENDORSEMENT_CODE in('00','11','12') then 1 when ENDORSEMENT_CODE in ('02','08') then -1 else 0 end) writtenPolicies,\r\n"
					+ "sum(RSA_KPI_FACT_POLICY.acq_cost) acq_cost\r\n"
					+ " FROM RSDB.RSA_KPI_FACT_POLICY as RSA_KPI_FACT_POLICY\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n"
					+ "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_POLICY.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n"
					+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n"
					+ "LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_POLICY.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n"
					+ "LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n"
					+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE ";

			if (fromYear.equals(toYear)) {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			} else {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			}

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(KPI_FACT_A_NEW.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_POLICY_TYPE_MASTER.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.VEHICLEAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.VEHICLEAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.SUM_INSURED <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.MAXAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.AGE_BAND <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.FAMILY_SIZE <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and RSA_KPI_FACT_POLICY.DISEASE_CODE in (" + vals + ")";
			}

			queryStr += ")";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				CubeAKpiResponse res = new CubeAKpiResponse();
				res.setWrittenPolicies(rs.getDouble(2));
				res.setLivesCovered(rs.getDouble(1));
				res.setAcqCost(rs.getDouble(3));

				kpiResponseList.add(res);
			}

			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return kpiResponseList;

	}
	
	
	//@RequestMapping(value = "/getClaimsCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ClaimsCubeResponse> getKpiClaimsData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<ClaimsCubeResponse> kpiResponseList = new ArrayList<ClaimsCubeResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
//			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];

			String claimMovementStartDate = fromYear + "-" + fromMonth + "-01";
			String claimMovementEndDate = toYear + "-" + toMonth + "-31";

			String queryStr = " select\r\n" + "sum(registered_claims) registered_claims\r\n" + "from\r\n" + "(\r\n"
					+ "SELECT\r\n" + "RSA_KPI_FACT_CLAIMS_LATEST.EXPIRTY_DATE as RSA_KPI_FACT_CLAIMS_LATEST_EXPIRTY_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_CODE as RSA_KPI_FACT_CLAIMS_LATEST_PRODUCT_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE as RSA_KPI_FACT_CLAIMS_LATEST_BRANCH_CODE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_TYPE_CODE as RSA_KPI_FACT_CLAIMS_LATEST_BUSINESS_TYPE_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.OA_CODE as RSA_KPI_FACT_CLAIMS_LATEST_OA_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_SI as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_SI\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MIGRATION_FLAG as RSA_KPI_FACT_CLAIMS_LATEST_MIGRATION_FLAG\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FLOTER_FLAG as RSA_KPI_FACT_CLAIMS_LATEST_FLOTER_FLAG\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE as RSA_KPI_FACT_CLAIMS_LATEST_CAMPAIN_CODE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_STATUS as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_STATUS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL as RSA_KPI_FACT_CLAIMS_LATEST_CHANNEL\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL as RSA_KPI_FACT_CLAIMS_LATEST_SUB_CHANNEL\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FINANCIAL_YEAR as RSA_KPI_FACT_CLAIMS_LATEST_FINANCIAL_YEAR\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.ENTRY_YEAR as RSA_KPI_FACT_CLAIMS_LATEST_ENTRY_YEAR\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_BAND as RSA_KPI_FACT_CLAIMS_LATEST_BUSINESS_BAND\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.EFF_FIN_YEAR_MONTH as RSA_KPI_FACT_CLAIMS_LATEST_EFF_FIN_YEAR_MONTH\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FAMILY_SIZE as RSA_KPI_FACT_CLAIMS_LATEST_FAMILY_SIZE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_GROUP as RSA_KPI_FACT_CLAIMS_LATEST_PRODUCT_GROUP\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.STP_NSTP as RSA_KPI_FACT_CLAIMS_LATEST_STP_NSTP\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PREVIOUS_SI as RSA_KPI_FACT_CLAIMS_LATEST_PREVIOUS_SI\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.UW_YEAR as RSA_KPI_FACT_CLAIMS_LATEST_UW_YEAR\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_SOURCE_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_SOURCE_TYPE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.ENTRY_DATE as RSA_KPI_FACT_CLAIMS_LATEST_ENTRY_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MAXAGE as RSA_KPI_FACT_CLAIMS_LATEST_MAXAGE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.SUBLINE as RSA_KPI_FACT_CLAIMS_LATEST_SUBLINE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_BUSINESS_TYPE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_PRODUCT_TYPE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.DISEASE_CODE as RSA_KPI_FACT_CLAIMS_LATEST_DISEASE_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS as RSA_KPI_FACT_CLAIMS_LATEST_TOTALNUMBEROFYEARSWITHRS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY as RSA_KPI_FACT_CLAIMS_LATEST_NUMBEROFYEARSWITHRSINMIGRATIONPOLICY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MODELCODE as RSA_KPI_FACT_CLAIMS_LATEST_MODELCODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MAKE as RSA_KPI_FACT_CLAIMS_LATEST_MAKE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MODEL as RSA_KPI_FACT_CLAIMS_LATEST_MODEL\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MODELGROUP as RSA_KPI_FACT_CLAIMS_LATEST_MODELGROUP\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLASSOFVEHICLE as RSA_KPI_FACT_CLAIMS_LATEST_CLASSOFVEHICLE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.VEHICLEAGE as RSA_KPI_FACT_CLAIMS_LATEST_VEHICLEAGE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.SEATINGCAPACITY as RSA_KPI_FACT_CLAIMS_LATEST_SEATINGCAPACITY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FUELTYPE as RSA_KPI_FACT_CLAIMS_LATEST_FUELTYPE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.REGSTATE as RSA_KPI_FACT_CLAIMS_LATEST_REGSTATE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.REGZONE as RSA_KPI_FACT_CLAIMS_LATEST_REGZONE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.REGLOCATION as RSA_KPI_FACT_CLAIMS_LATEST_REGLOCATION\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FIN_DATE as RSA_KPI_FACT_CLAIMS_LATEST_FIN_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_CATEGORY as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_CATEGORY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_MOVEMENT_DATE as RSA_KPI_FACT_CLAIMS_LATEST_CLM_MOVEMENT_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_CLAIM_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_CLM_CLAIM_TYPE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_CLAIM_MVMT_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_CLM_CLAIM_MVMT_TYPE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_FYEAR as RSA_KPI_FACT_CLAIMS_LATEST_CLM_FYEAR\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_REPUDIATEDCLAIMS as RSA_KPI_FACT_CLAIMS_LATEST_CLM_REPUDIATEDCLAIMS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_IFTHEFTCLAIMS as RSA_KPI_FACT_CLAIMS_LATEST_CLM_IFTHEFTCLAIMS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_IFFRAUDCLAIMS as RSA_KPI_FACT_CLAIMS_LATEST_CLM_IFFRAUDCLAIMS\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_SUMINSURED as RSA_KPI_FACT_CLAIMS_LATEST_CLM_SUMINSURED\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_IFCATOSTROPHIC as RSA_KPI_FACT_CLAIMS_LATEST_CLM_IFCATOSTROPHIC\r\n"
					+ ",KPI_FIN_YEAR_MASTER.FIN_YEAR as KPI_FIN_YEAR_MASTER_FIN_YEAR\r\n"
					+ ",KPI_FIN_MONTH_MASTER.FIN_MONTH as KPI_FIN_MONTH_MASTER_FIN_MONTH\r\n"
					+ ",KPI_PRODUCT_MASTER.PRODUCT_CODE as KPI_PRODUCT_MASTER_PRODUCT_CODE\r\n"
					+ ",KPI_PRODUCT_MASTER.PRODUCT_DESCRIPTION as KPI_PRODUCT_MASTER_PRODUCT_DESCRIPTION\r\n"
					+ ",KPI_POLICY_TYPE_MASTER.POLICY_TYPE as KPI_POLICY_TYPE_MASTER_POLICY_TYPE\r\n"
					+ ",KPI_BRANCH_MASTER.BRANCH_CODE as KPI_BRANCH_MASTER_BRANCH_CODE\r\n"
					+ ",KPI_BRANCH_MASTER.REVISED_BRANCH_NAME as KPI_BRANCH_MASTER_REVISED_BRANCH_NAME\r\n"
					+ ",KPI_BRANCH_MASTER.REGION as KPI_BRANCH_MASTER_REGION\r\n"
					+ ",KPI_BRANCH_MASTER.STATE_NEW as KPI_BRANCH_MASTER_STATE_NEW\r\n"
					+ ",KPI_BRANCH_MASTER.CLUSTER_NAME as KPI_BRANCH_MASTER_CLUSTER_NAME\r\n"
					+ ",KPI_BRANCH_MASTER.SUB_CLUSTER as KPI_BRANCH_MASTER_SUB_CLUSTER\r\n"
					+ ",KPI_BRANCH_MASTER.RA_CITY_FLAG as KPI_BRANCH_MASTER_RA_CITY_FLAG\r\n"
					+ ",KPI_BRANCH_MASTER.RA_DESCRIPTION as KPI_BRANCH_MASTER_RA_DESCRIPTION\r\n"
					+ ",KPI_BRANCH_MASTER.ZONE as KPI_BRANCH_MASTER_ZONE\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODEL_CODE as KPI_MODEL_MASTER_NW_MODEL_CODE\r\n"
					+ ",KPI_MODEL_MASTER_NW.MAKE as KPI_MODEL_MASTER_NW_MAKE\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODEL as KPI_MODEL_MASTER_NW_MODEL\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODELGROUP as KPI_MODEL_MASTER_NW_MODELGROUP\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODELCLASSIFICATION as KPI_MODEL_MASTER_NW_MODELCLASSIFICATION\r\n"
					+ ",KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE as KPI_CAMPAIGN_MASTER_CAMPAIGN_CODE\r\n"
					+ ",KPI_CAMPAIGN_MASTER.CAMPAIGN_DESCRIPTION as KPI_CAMPAIGN_MASTER_CAMPAIGN_DESCRIPTION\r\n"
					+ ",KPI_OA_MASTER_NW.OA_CODE as KPI_OA_MASTER_NW_OA_CODE\r\n"
					+ ",KPI_OA_MASTER_NW.OA_NAME as KPI_OA_MASTER_NW_OA_NAME\r\n"
					+ ",KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME as KPI_SUB_CHANNEL_MASTER_NW_CHANNEL_NAME\r\n"
					+ ",KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL as KPI_SUB_CHANNEL_MASTER_NW_SUB_CHANNEL\r\n"
					+ ",KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE as KPI_BUSINESS_TYPE_MASTER_BUSINESS_TYPE\r\n"
					+ ",KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY as KPI_POLICY_CATEGORY_MASTER_NW_POLICY_CATEGORY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_CLAIM_NO as RSA_KPI_FACT_CLAIMS_LATEST_CLM_CLAIM_NO,\r\n"
					+ "(case when  CLM_MOVEMENT_DATE>='" + claimMovementStartDate + "' AND CLM_MOVEMENT_DATE<='"
					+ claimMovementEndDate + "' then 1 else 0 end ) registered_claims\r\n"
					+ " FROM RSDB.RSA_KPI_FACT_CLAIMS_LATEST as RSA_KPI_FACT_CLAIMS_LATEST\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n"
					+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n"
					+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n"
					+ "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n";

			queryStr += " WHERE CLM_MOVEMENT_DATE>='" + claimMovementStartDate + "' AND CLM_MOVEMENT_DATE<='"
					+ claimMovementEndDate + "'";

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(KPI_FACT_A.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.VEHICLE_AGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.VEHICLE_AGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.SUM_INSURED <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.MAXAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.AGE_BAND <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.FAMILY_SIZE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.DISEASE_CODE in (" + vals + ")";
			}

			queryStr += ")";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				ClaimsCubeResponse res = new ClaimsCubeResponse();
				res.setRegisteredClaims(rs.getDouble(1));

				kpiResponseList.add(res);
			}

			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return kpiResponseList;

	}
	
	
	//@RequestMapping(value = "/getSingleLineCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ClaimsSingleLineCubeResponse> getKpiSingleLineData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<ClaimsSingleLineCubeResponse> kpiResponseList = new ArrayList<ClaimsSingleLineCubeResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
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

			String claimMvmtStartMonth = fromYear + fromMonth;
			String claimMvmtEndMonth = toYear + toMonth;

			String claimSingleLineBaseQuery = "";
			String claimSingleLineFinYrCon = "";
			String claimSingleLineFiltersCon = "";
			String queryStr = "";

			claimSingleLineBaseQuery = "select\r\n" + 
					"sum(actual_gic_health) actual_gic_health\r\n" + 
					",sum(claims_paid) claims_paid\r\n" + 
					"from(\r\n" + 
					"SELECT\r\n" + 
					"sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_GIC) actual_gic_health\r\n" + 
					",sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_TOTAL_PAID) claims_paid\r\n" + 
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n";

			claimSingleLineFinYrCon += " where CSL_MVMT_MONTH between '" + claimMvmtStartMonth + "' AND '"
					+ claimMvmtEndMonth + "'";

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL) in (" + vals
						+ ")";
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
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL) in (" + vals
						+ ")";
			}

			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL) in (" + vals
						+ ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL) in ("
						+ vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE) in (" + vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

				String vals = "'VGC','VPC','VMC','VOC'";
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_FACT_A.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE) in (" + vals
						+ ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_FACT_A_UPDATED.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLE_AGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLE_AGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUM_INSURED <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.AGE_BAND <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.DISEASE_CODE in (" + vals + ")";
			}

			String claimSingleLineEnd = " group by\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE\r\n" + 
					") ";

			queryStr = claimSingleLineBaseQuery + claimSingleLineFinYrCon + claimSingleLineFiltersCon
					+ claimSingleLineEnd;

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			ClaimsSingleLineCubeResponse res = new ClaimsSingleLineCubeResponse();
			while (rs.next()) {

//				res.setActualGicOd(rs.getDouble(1));
//				res.setActualGicTp(rs.getDouble(2));
				res.setActualGicHealth(rs.getDouble(1));
				res.setPaid(rs.getDouble(2));

			}

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			String claimsSingleLineReputiatedClaimsBase = "select\r\n" + 
					"count(csl_claim_no)\r\n" + 
					"from(\r\n" + 
					"select\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.csl_claim_no\r\n" + 
					",SUM(csl_closing_os_total_org) as close_tot,\r\n" + 
					"SUM(csl_LOSS_PAID_CUM) as loss \r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"where\r\n" + 
					"(CSL_MVMT_MONTH,CSL_CLAIM_NO) in  (select max(CSL_MVMT_MONTH),CSL_CLAIM_NO \r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "
					+ " where CSL_MVMT_MONTH between '" + claimMvmtStartMonth + "' AND '" + claimMvmtEndMonth
					+ "' ";

			String claimsReputiatedEnd = " group by CSL_CLAIM_NO) GROUP BY \r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.csl_claim_no\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE\r\n" + 
					")y\r\n" + 
					"where  close_tot=0 and loss=0  ";

			queryStr = claimsSingleLineReputiatedClaimsBase + claimSingleLineFiltersCon + claimsReputiatedEnd;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setReputiatedClaims(rs.getDouble(1));
				

			}

			queryStr = "select sum(CSL_OPENING_TOTAL_ORG) from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST "
					+ "where (csl_claim_no,csl_mvmt_month) in "
					+ "( SELECT csl_claim_no,max(csl_mvmt_month) FROM RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n"
					+ "where CSL_MVMT_MONTH between '" + claimMvmtStartMonth + "' AND '" + claimMvmtEndMonth + "'\r\n"
					+ "group by csl_claim_no);";

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setOpeningOsClaims(rs.getDouble(1));

			}

			queryStr = "select sum(CSL_CLOSING_OS_TOTAL_ORG) from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST"
					+ " where (csl_claim_no,csl_mvmt_month) in"
					+ " ( SELECT csl_claim_no,min(csl_mvmt_month) FROM RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n"
					+ "where CSL_MVMT_MONTH between '" + claimMvmtStartMonth + "' AND '" + claimMvmtEndMonth + "' "
					+ "group by csl_claim_no);";

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setClosingOsClaim(rs.getDouble(1));

			}
			
			//actual gic od
			queryStr = "select\r\n" + 
					"sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC)\r\n" + 
					"from(\r\n" + 
					"SELECT\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_INCEPTION_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_SI as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_SI\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MIGRATION_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FLOTER_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FINANCIAL_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_EFF_FIN_YEAR_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FAMILY_SIZE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_GROUP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_GROUP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.STP_NSTP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_STP_NSTP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_UW_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAXAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUBLINE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_BUSINESS_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.DISEASE_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_DISEASE_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_TOTALNUMBEROFYEARSWITHRS\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_NUMBEROFYEARSWITHRSINMIGRATIONPOLICY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CLASSOFVEHICLE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CLASSOFVEHICLE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLEAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_VEHICLEAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SEATINGCAPACITY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SEATINGCAPACITY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FUELTYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FUELTYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGSTATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGSTATE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGZONE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGZONE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGLOCATION as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGLOCATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FIN_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FIN_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_CATEGORY\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHECODE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHECODE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHICTYPE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHICTYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MAX_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MAX_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MIN_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MIN_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MVMT_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MVMT_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REGISTRATION_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REGISTRATION_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE_DESC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE_DESC\r\n" + 
					",KPI_FIN_YEAR_MASTER.FIN_YEAR as KPI_FIN_YEAR_MASTER_FIN_YEAR\r\n" + 
					",KPI_FIN_MONTH_MASTER.FIN_MONTH as KPI_FIN_MONTH_MASTER_FIN_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUB_CHANNEL\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME as KPI_SUB_CHANNEL_MASTER_NW_CHANNEL_NAME\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL as KPI_SUB_CHANNEL_MASTER_NW_SUB_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_CODE as KPI_PRODUCT_MASTER_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_DESCRIPTION as KPI_PRODUCT_MASTER_PRODUCT_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CAMPAIN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE as KPI_CAMPAIGN_MASTER_CAMPAIGN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_DESCRIPTION as KPI_CAMPAIGN_MASTER_CAMPAIGN_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAKE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MODELCODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL_CODE as KPI_MODEL_MASTER_NW_MODEL_CODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MAKE as KPI_MODEL_MASTER_NW_MAKE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL as KPI_MODEL_MASTER_NW_MODEL\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELGROUP as KPI_MODEL_MASTER_NW_MODELGROUP\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELCLASSIFICATION as KPI_MODEL_MASTER_NW_MODELCLASSIFICATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_CODE as KPI_OA_MASTER_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_NAME as KPI_OA_MASTER_OA_NAME\r\n" + 
					",KPI_SUBLINE_MASTER.SUBLINE as KPI_SUBLINE_MASTER_SUBLINE\r\n" + 
					",KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE as KPI_BUSINESS_TYPE_MASTER_BUSINESS_TYPE\r\n" + 
					",KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY as KPI_POLICY_CATEGORY_MASTER_POLICY_CATEGORY\r\n" + 
					",KPI_POLICY_TYPE_MASTER.POLICY_TYPE as KPI_POLICY_TYPE_MASTER_POLICY_TYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_EXPENSE_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID_CUM as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID_CUM\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_EXPENSE_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_COUNT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_COUNT\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_GIC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_TOTAL_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_TOTAL_PAID\r\n" + 
					"\r\n" + 
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST \r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					"where RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO not like 'TP%' \r\n";
			
			queryStr += " and ( CSL_MVMT_MONTH between '" + claimMvmtStartMonth + "' AND '"
					+ claimMvmtEndMonth + "') " +claimSingleLineFiltersCon+" )x 	";
			
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setActualGicOd(rs.getDouble(1));

			}
			
			//actual gic tp
			queryStr = "select\r\n" + 
					"sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC)\r\n" + 
					"from(\r\n" + 
					"SELECT\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_INCEPTION_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_SI as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_SI\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MIGRATION_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FLOTER_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FINANCIAL_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_EFF_FIN_YEAR_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FAMILY_SIZE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_GROUP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_GROUP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.STP_NSTP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_STP_NSTP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_UW_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAXAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUBLINE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_BUSINESS_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.DISEASE_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_DISEASE_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_TOTALNUMBEROFYEARSWITHRS\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_NUMBEROFYEARSWITHRSINMIGRATIONPOLICY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CLASSOFVEHICLE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CLASSOFVEHICLE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLEAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_VEHICLEAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SEATINGCAPACITY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SEATINGCAPACITY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FUELTYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FUELTYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGSTATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGSTATE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGZONE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGZONE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGLOCATION as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGLOCATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FIN_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FIN_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_CATEGORY\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHECODE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHECODE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHICTYPE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHICTYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MAX_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MAX_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MIN_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MIN_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MVMT_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MVMT_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REGISTRATION_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REGISTRATION_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE_DESC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE_DESC\r\n" + 
					",KPI_FIN_YEAR_MASTER.FIN_YEAR as KPI_FIN_YEAR_MASTER_FIN_YEAR\r\n" + 
					",KPI_FIN_MONTH_MASTER.FIN_MONTH as KPI_FIN_MONTH_MASTER_FIN_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUB_CHANNEL\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME as KPI_SUB_CHANNEL_MASTER_NW_CHANNEL_NAME\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL as KPI_SUB_CHANNEL_MASTER_NW_SUB_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_CODE as KPI_PRODUCT_MASTER_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_DESCRIPTION as KPI_PRODUCT_MASTER_PRODUCT_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CAMPAIN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE as KPI_CAMPAIGN_MASTER_CAMPAIGN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_DESCRIPTION as KPI_CAMPAIGN_MASTER_CAMPAIGN_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAKE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MODELCODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL_CODE as KPI_MODEL_MASTER_NW_MODEL_CODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MAKE as KPI_MODEL_MASTER_NW_MAKE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL as KPI_MODEL_MASTER_NW_MODEL\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELGROUP as KPI_MODEL_MASTER_NW_MODELGROUP\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELCLASSIFICATION as KPI_MODEL_MASTER_NW_MODELCLASSIFICATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_CODE as KPI_OA_MASTER_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_NAME as KPI_OA_MASTER_OA_NAME\r\n" + 
					",KPI_SUBLINE_MASTER.SUBLINE as KPI_SUBLINE_MASTER_SUBLINE\r\n" + 
					",KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE as KPI_BUSINESS_TYPE_MASTER_BUSINESS_TYPE\r\n" + 
					",KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY as KPI_POLICY_CATEGORY_MASTER_POLICY_CATEGORY\r\n" + 
					",KPI_POLICY_TYPE_MASTER.POLICY_TYPE as KPI_POLICY_TYPE_MASTER_POLICY_TYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_EXPENSE_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID_CUM as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID_CUM\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_EXPENSE_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_COUNT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_COUNT\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_GIC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_TOTAL_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_TOTAL_PAID\r\n" + 
					"\r\n" + 
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST \r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					"where RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO like 'TP%' \r\n";
			
			queryStr += " and ( CSL_MVMT_MONTH between '" + claimMvmtStartMonth + "' AND '"
					+ claimMvmtEndMonth + "') " +claimSingleLineFiltersCon+" )x 	";
			
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				res.setActualGicTp(rs.getDouble(1));
			}
			
			queryStr = "select sum(NIC),sum(nic_tp),sum(nic_od) FROM(\r\n" + 
					"select A.csl_gic,A.CSL_CLAIM_NO,A.CSL_MVMT_MONTH,B.OBLIGATORY,B.QUOTA_SHARE,B.RETENTION,B.RI_COMMISSION, csl_gic*(1-QUOTA_SHARE-OBLIGATORY) NIC, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end nic_tp, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' then 0 else csl_gic*(1-QUOTA_SHARE-OBLIGATORY) end nic_od from (\r\n" + 
					"select (case when RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R0'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R1'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%102' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R2'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%103' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END) BAND, uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE, sum(csl_gic) csl_gic,CSL_CLAIM_NO,CSL_MVMT_MONTH\r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					" where\r\n" + 
					"(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' or policy_no like '%102' or policy_no like '%103' or policy_no like '%100') and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP'" + 
					" and ( CSL_MVMT_MONTH between '" + claimMvmtStartMonth + "' AND '"
					+ claimMvmtEndMonth + "') " +claimSingleLineFiltersCon+"	"+
					" GROUP by\r\n" + 
					"(case when RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R0'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R1'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%102' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R2'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%103' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END), uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH ) A ,\r\n" + 
					"(select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1\r\n" + 
					" group by underwriting_year,XGEN_PRODUCTCODE,band) B\r\n" + 
					"where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band \r\n" + 
					"\r\n" + 
					"union \r\n" + 
					"select A.csl_gic,A.CSL_CLAIM_NO,A.CSL_MVMT_MONTH,B.OBLIGATORY,B.QUOTA_SHARE,B.RETENTION,B.RI_COMMISSION, csl_gic*(1-QUOTA_SHARE-OBLIGATORY) NIC, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end nic_tp, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' then 0 else csl_gic*(1-QUOTA_SHARE-OBLIGATORY) end nic_od from (\r\n" + 
					"select (case when RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R0'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R1'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%102' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R2'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%103' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END) BAND, uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE, sum(csl_gic) csl_gic,CSL_CLAIM_NO,CSL_MVMT_MONTH\r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					" where\r\n" + 
					"(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no not like '%101' and policy_no not like '%102' and policy_no not like '%103' and policy_no not like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP') or RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code<> 'IHP'\r\n" + 
					" and ( CSL_MVMT_MONTH between '" + claimMvmtStartMonth + "' AND '"
					+ claimMvmtEndMonth + "') " +claimSingleLineFiltersCon+"	"+
					"  GROUP by\r\n" + 
					"(case when RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R0'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R1'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%102' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R2'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%103' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END), uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH ) A ,\r\n" + 
					"(select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 \r\n" + 
					"group by underwriting_year,XGEN_PRODUCTCODE,band) B\r\n" + 
					"where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE\r\n" + 
					"\r\n" + 
					")";
			
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				res.setNic(rs.getDouble(1));
				res.setActualNicTp(rs.getDouble(2));
				res.setActualNicOd(rs.getDouble(3));
				res.setActualNicHealth(rs.getDouble(1));
			}

			kpiResponseList.add(res);

		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return kpiResponseList;
	}
	
	
	//@RequestMapping(value = "/getInsCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CubeAKpiResponse> getKpiInsData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<CubeAKpiResponse> generalKpiResponseList = new ArrayList<CubeAKpiResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
//			String fromDate = req.getParameter("fromDate") == null ? "" : req.getParameter("fromDate");
//			String toDate = req.getParameter("toDate") == null ? "" : req.getParameter("toDate");
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];

			String finYearStart = fromYear + "-" + fromMonth + "-01";
			String finYearEnd = toYear + "-" + toMonth + "-31";

//			String queryStr = "select\r\n" + 
//					"sum(gwp) gwp\r\n" + 
//					",sum(nwp) nwp\r\n" + 
//					",sum(gwp_od) gwp_od\r\n" + 
//					",SUM(gwp_tp) gwp_tp\r\n" + 
//					",sum(nwp_od) nwp_od\r\n" + 
//					",sum(nwp_tp) nwp_tp\r\n" + 
//					",sum(discount_gwp_od) discount_gwp_od\r\n" + 
//					",sum(discount_nwp_od) discount_nwp_od\r\n" + 
//					"from(\r\n" + 
//					"select\r\n" + 
//					"product_code            \r\n" + 
//					",branch_code             \r\n" + 
//					",oa_code                 \r\n" + 
//					",policy_si               \r\n" + 
//					",migration_flag          \r\n" + 
//					",floter_flag             \r\n" + 
//					",campain_code            \r\n" + 
//					",channel                 \r\n" + 
//					",sub_channel             \r\n" + 
//					",financial_year          \r\n" + 
//					",eff_fin_year_month      \r\n" + 
//					",family_size             \r\n" + 
//					",product_group           \r\n" + 
//					",stp_nstp                \r\n" + 
//					",uw_year                 \r\n" + 
//					",maxage                  \r\n" + 
//					",subline                 \r\n" + 
//					",business_type           \r\n" + 
//					",disease_code            \r\n" + 
//					",totalnumberofyearswithrs        \r\n" + 
//					",numberofyearswithrsinmigrationpolicy    \r\n" + 
//					",modelcode               \r\n" + 
//					",make                    \r\n" + 
//					",model                   \r\n" + 
//					",modelgroup              \r\n" + 
//					",classofvehicle          \r\n" + 
//					",vehicleage              \r\n" + 
//					",seatingcapacity         \r\n" + 
//					",fueltype              \r\n" + 
//					",fin_date                \r\n" + 
//					",policy_category         \r\n" + 
//					",addon                  \r\n" + 
//					",ins_coverage_type  \r\n" + 
//					",ins_coverage_category\r\n" + 
//					",sum(gwp) gwp\r\n" + 
//					",sum(nwp) nwp\r\n" + 
//					",sum(gwp_od) gwp_od\r\n" + 
//					",SUM(gwp_tp) gwp_tp\r\n" + 
//					",sum(nwp_od) nwp_od\r\n" + 
//					",sum(nwp_tp) nwp_tp\r\n" + 
//					",sum(discount_gwp_od) discount_gwp_od\r\n" + 
//					",sum(discount_nwp_od) discount_nwp_od\r\n" + 
//					"from(\r\n" + 
//					"select      \r\n" + 
//					"ins.product_code            \r\n" + 
//					",ins.branch_code             \r\n" + 
//					",ins.oa_code                 \r\n" + 
//					",ins.policy_si               \r\n" + 
//					",ins.migration_flag          \r\n" + 
//					",ins.floter_flag             \r\n" + 
//					",ins.campain_code            \r\n" + 
//					",ins.channel                 \r\n" + 
//					",ins.sub_channel             \r\n" + 
//					",ins.financial_year          \r\n" + 
//					",ins.eff_fin_year_month      \r\n" + 
//					",ins.family_size             \r\n" + 
//					",ins.product_group           \r\n" + 
//					",ins.stp_nstp                \r\n" + 
//					",ins.uw_year                 \r\n" + 
//					",ins.maxage                  \r\n" + 
//					",ins.subline                 \r\n" + 
//					",ins.business_type           \r\n" + 
//					",ins.disease_code            \r\n" + 
//					",ins.totalnumberofyearswithrs        \r\n" + 
//					",ins.numberofyearswithrsinmigrationpolicy    \r\n" + 
//					",ins.modelcode               \r\n" + 
//					",ins.make                    \r\n" + 
//					",KPI_MODEL_MASTER_NW.model                   \r\n" + 
//					",KPI_MODEL_MASTER_NW.modelgroup              \r\n" + 
//					",ins.classofvehicle          \r\n" + 
//					",ins.vehicleage              \r\n" + 
//					",ins.seatingcapacity         \r\n" + 
//					",ins.fueltype               \r\n" + 
//					",ins.fin_date                \r\n" + 
//					",ins.policy_category         \r\n" + 
//					",ins.addon                  \r\n" + 
//					",ins.ins_coverage_type  \r\n" + 
//					",ins.ins_coverage_category\r\n" + 
//					",ins.INS_COVERAGE_PREMIUM gwp\r\n" + 
//					",ins.INS_NWP nwp\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY = 'OD' then INS_COVERAGE_PREMIUM else 0 end) gwp_od\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_COVERAGE_PREMIUM else 0 end) gwp_tp\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY = 'OD' then INS_NWP else 0 end) nwp_od\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_NWP else 0 end) nwp_tp\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_gwp_od\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_nwp_od\r\n" + 
//					"from RSA_KPI_FACT_INS_NEW ins\r\n" + 
//					"LEFT JOIN KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
//					"ON ins.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
//					"LEFT JOIN KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
//					"ON ins.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
//					"LEFT JOIN KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
//					"ON ins.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
//					"LEFT JOIN KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
//					"ON ins.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
//					"LEFT JOIN KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
//					"ON ins.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
//					"LEFT JOIN KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
//					"ON ins.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
//					"LEFT JOIN KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
//					"ON ins.MAKE = KPI_MODEL_MASTER_NW.MAKE AND ins.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
//					"LEFT JOIN KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
//					"ON ins.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND ins.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
//					"LEFT JOIN KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
//					"ON ins.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
//					"LEFT JOIN KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
//					"ON ins.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY ";

//			String queryStr = "select\r\n" + 
//					"sum(gwp) gwp\r\n" + 
//					",sum(nwp) nwp\r\n" + 
//					",sum(gwp_od) gwp_od\r\n" + 
//					",SUM(gwp_tp) gwp_tp\r\n" + 
//					",sum(nwp_od) nwp_od\r\n" + 
//					",sum(nwp_tp) nwp_tp\r\n" + 
//					",sum(discount_gwp_od) discount_gwp_od\r\n" + 
//					",sum(discount_nwp_od) discount_nwp_od\r\n" + 
//					"from (\r\n" + 
//					"select\r\n" + 
//					"sum(INS_COVERAGE_PREMIUM) gwp\r\n" + 
//					",sum(ins_nwp) nwp\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_COVERAGE_PREMIUM else 0 end) gwp_od\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_COVERAGE_PREMIUM else 0 end) gwp_tp\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_NWP else 0 end) nwp_od\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_NWP else 0 end) nwp_tp\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_gwp_od\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_NWP else 0 end) discount_nwp_od\r\n" + 
//					"from RSA_KPI_FACT_INS_NEW ins\r\n" + 
//					"LEFT JOIN KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
//					"ON ins.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
//					"LEFT JOIN KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
//					"ON ins.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
//					"LEFT JOIN KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
//					"ON ins.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
//					"LEFT JOIN KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
//					"ON ins.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
//					"LEFT JOIN KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
//					"ON ins.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
//					"LEFT JOIN KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
//					"ON ins.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
//					"LEFT JOIN KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
//					"ON ins.MAKE = KPI_MODEL_MASTER_NW.MAKE AND ins.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
//					"LEFT JOIN KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
//					"ON ins.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND ins.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
//					"LEFT JOIN KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
//					"ON ins.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
//					"LEFT JOIN KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
//					"ON ins.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY";

			String queryStr = "select  sum(gwp) gwp  ,sum(nwp) nwp  ,sum(gwp_od) gwp_od  ,SUM(gwp_tp) gwp_tp  ,sum(nwp_od) nwp_od  ,sum(nwp_tp) nwp_tp  ,sum(discount_gwp_od) discount_gwp_od  ,sum(discount_nwp_od) discount_nwp_od  from (  select  sum(INS_COVERAGE_PREMIUM) gwp  ,sum(ins_nwp) nwp  ,sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_COVERAGE_PREMIUM else 0 end) gwp_od  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_COVERAGE_PREMIUM else 0 end) gwp_tp  ,sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_NWP else 0 end) nwp_od  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_NWP else 0 end) nwp_tp  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_gwp_od  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_NWP else 0 end) discount_nwp_od  "
					+ "from  RSA_KPI_FACT_INS_LATEST ins  "
					+ "LEFT JOIN KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER  ON ins.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR  LEFT JOIN KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER  ON ins.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH  LEFT JOIN KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON ins.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON ins.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON ins.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON ins.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON ins.MAKE = KPI_MODEL_MASTER_NW.MAKE AND ins.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE  LEFT JOIN KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON ins.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND ins.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON ins.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON ins.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY ";

//			if (fromYear.equals(toYear)) {
//				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//			} else {
//				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//			}

			queryStr += " where (ins.FIN_DATE >= '" + finYearStart + "' AND ins.FIN_DATE < '" + finYearEnd + "') ";

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(ins.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(ins.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(KPI_BRANCH_MASTER.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(RSA_KPI_FACT_INS_NEW.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_POLICY_TYPE_MASTER.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and ins.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and ins.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and ins.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.VEHICLEAGE <= " + minVal +
//			        			" and ins.VEHICLEAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.SUM_INSURED <= " + minVal +
//			        			" and ins.SUM_INSURED >= "+maxVal;
//					queryStr += " and ins.POLICY_SI <= " + minVal + " and ins.POLICY_SI >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.MAXAGE <= " + minVal +
//			        			" and ins.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and ins.NOOFYEARSWITHRSINMIG >= "+maxVal;
					
//		        	queryStr += " and ins.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY <= " + minVal +
//        			" and ins.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.AGE_BAND <= " + minVal +
//			        			" and ins.AGE_BAND >= "+maxVal;
					
//		        	queryStr += " and ins.VEHICLEAGE <= " + minVal +
//        			" and ins.VEHICLEAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.FAMILY_SIZE <= " + minVal +
//			        			" and ins.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and ins.DISEASE_CODE in (" + vals + ")";
			}

			queryStr += " group by  ins.policy_category    ,ins.product_code  ,ins.oa_code  ,ins.campain_code  ,ins.channel  ,ins.sub_channel  ,ins.financial_year  ,ins.eff_fin_year_month  ,ins.business_type  ,ins.branch_code  ,ins.uw_year  ,ins.modelcode  ,ins.make    ,KPI_MODEL_MASTER_NW.modelgroup)";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				CubeAKpiResponse cubeAKpiResponse = new CubeAKpiResponse();
				cubeAKpiResponse.setGwp(rs.getDouble(1));
				cubeAKpiResponse.setNwp(rs.getDouble(2));
				cubeAKpiResponse.setGwpOd(rs.getDouble(3));
				cubeAKpiResponse.setGwpTp(rs.getDouble(4));
				cubeAKpiResponse.setNwpOd(rs.getDouble(5));
				cubeAKpiResponse.setNwpTp(rs.getDouble(6));
				cubeAKpiResponse.setDiscountGwpOd(rs.getDouble(7));
				cubeAKpiResponse.setDiscountNwpOd(rs.getDouble(8));

//				cubeAKpiResponse.setLivesCovered(rs.getDouble(3));
//				cubeAKpiResponse.setAvgGwp(rs.getDouble(4));
//				cubeAKpiResponse.setWrittenPolicies(rs.getDouble(5));
				generalKpiResponseList.add(cubeAKpiResponse);

			}

			System.out.println("--------------------------------------------" + generalKpiResponseList.size());
			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return generalKpiResponseList;
	}
	
	
	//@GetMapping("/getGepCubeData")
	@ResponseBody
	public List<GepCubeResponse> getGEPCubeData(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
			throws SQLException {
		Connection connection = null;
		List<GepCubeResponse> kpiResponseList = new ArrayList<GepCubeResponse>();
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

			/*
			 * String queryStr = "select \r\n" + "sum(gep) gep,\r\n" + "sum(nep) nep\r\n" +
			 * "sum(gep_od) gep_od,\r\n" + "sum(nep_od) nep_od,\r\n" +
			 * "sum(discount_gep_od) discount_gep_od,\r\n" +
			 * "sum(discount_nep_od) discount_nep_od,\r\n" + "sum(gep_tp) gep_tp,\r\n" +
			 * "sum(nep_od) nep_tp,\r\n" + "sum(gep)*0.3 burn_cost,\r\n" +
			 * "sum(gep_od)*0.1 ibnr_gic_od,\r\n" + "sum(gep_od)*0.2 nbnr_gic_od,\r\n" +
			 * "sum(nep_od)*0.1 ibnr_nic_od,\r\n" + "sum(nep_od)*0.2 nbnr_nic_od,\r\n" +
			 * "sum(gep)*1.5 ulr_gic_tp,\r\n" + "sum(nep)*1.5 ulr_nic_tp,\r\n" +
			 * "sum(gep_tp)*0.1 ibner_gic_tp,\r\n" + "sum(nep_tp)*0.1 ibner_nic_tp,\r\n" +
			 * "sum(gep)*0.3 xol_cost,\r\n" + "sum(gep_health)*0.1 ibnr_gic_health,\r\n" +
			 * "sum(gep_health)*0.2 nbnr_gic_health,\r\n" +
			 * "sum(nep_health)*0.1 ibnr_nic_health,\r\n" +
			 * "sum(nep_health)*0.2 nbnr_nic_health,\r\n" +
			 * "sum(gep_earned_days) earned_days\r\n" + "sum(gep)*0.3 expenses\r\n" +
			 * "from\r\n" + "(\r\n" + "select \r\n" + "g.agent_code,\r\n" +
			 * "g.product_code,\r\n" + "g.branch_code,\r\n" + "g.oa_code,\r\n" +
			 * "g.policy_si,\r\n" + "g.floter_flag,\r\n" + "g.campain_code,\r\n" +
			 * "g.channel,\r\n" + "g.sub_channel,\r\n" + "g.financial_year,\r\n" +
			 * "g.eff_fin_year_month,\r\n" + "g.family_size,\r\n" + "g.product_group,\r\n" +
			 * "g.stp_nstp,\r\n" + "g.previous_si,\r\n" + "g.uw_year,\r\n" + "g.maxage,\r\n"
			 * + "g.subline,\r\n" + "g.business_type,\r\n" + "g.product_type,\r\n" +
			 * "g.disease_code,\r\n" + "g.totalnumberofyearswithrs,\r\n" +
			 * "g.numberofyearswithrsinmigrationpolicy,\r\n" + "g.migration_flag,\r\n" +
			 * "g.modelcode,\r\n" + "g.make,\r\n" + "g.model,\r\n" + "g.modelgroup,\r\n" +
			 * "g.classofvehicle,\r\n" + "g.vehicleage,\r\n" + "g.seatingcapacity,\r\n" +
			 * "g.fueltype,\r\n" + "g.gep_month,\r\n" + "g.gep_year,\r\n" +
			 * "g.gep_earned_days,\r\n" + "g.gep_gepcoverage gep,\r\n" +
			 * "g.gep_nepcoverage nep\r\n" +
			 * ",(case when gep_coveragecategory='OD' then gep_gepcoverage else 0 end) gep_od,\r\n"
			 * +
			 * "(case when gep_coveragecategory='OD' then gep_nepcoverage else 0 end) nep_od,\r\n"
			 * +
			 * "(case when gep_coveragetype='DC' and gep_coveragecategory='od' then gep_gepcoverage else 0 end ) discount_gep_od,\r\n"
			 * +
			 * "(case when gep_coveragetype='DC' and gep_coveragecategory='od' then gep_nepcoverage else 0 end ) discount_nep_od,\r\n"
			 * +
			 * "(case when  gep_coveragecategory in ('TP','PA') then gep_gepcoverage else 0 end ) gep_tp,\r\n"
			 * +
			 * "(case when  gep_coveragecategory in ('TP','PA') then gep_nepcoverage else 0 end ) nep_tp,\r\n"
			 * +
			 * "(case when product_group like '%Health%' then gep_gepcoverage else 0 end ) gep_health,\r\n"
			 * +
			 * "(case when product_group like '%Health%' then gep_nepcoverage else 0 end ) nep_health\r\n"
			 * + "from  rsa_kpi_fact_gep as g\r\n" + "left join kpi_fin_year_master \r\n" +
			 * "on g.financial_year = kpi_fin_year_master.fin_year\r\n" +
			 * "left join kpi_policy_type_master \r\n" +
			 * "on g.floter_flag = kpi_policy_type_master.policy_type\r\n" +
			 * "left join kpi_product_master \r\n" +
			 * "on g.product_code = kpi_product_master.product_code\r\n" +
			 * "left join kpi_branch_master \r\n" +
			 * "on g.branch_code = kpi_branch_master.branch_code\r\n" +
			 * "left join kpi_campaign_master \r\n" +
			 * "on g.campain_code = kpi_campaign_master.campaign_code\r\n" +
			 * "left join kpi_oa_master_nw \r\n" +
			 * "on g.oa_code = kpi_oa_master_nw.oa_code\r\n" +
			 * "left join kpi_model_master_nw \r\n" +
			 * "on g.make = kpi_model_master_nw.make and g.modelcode = kpi_model_master_nw.model_code\r\n"
			 * + "left join kpi_sub_channel_master_nw \r\n" +
			 * "on g.channel = kpi_sub_channel_master_nw.channel_name and g.sub_channel = kpi_sub_channel_master_nw.sub_channel\r\n"
			 * + "left join kpi_business_type_master \r\n" +
			 * "on g.business_type = kpi_business_type_master.business_type\r\n";
			 */

			String queryStr = "select \r\n" + "sum(gep) gep,\r\n" + "sum(nep) nep,\r\n" + "sum(gep_od) gep_od,\r\n"
					+ "sum(nep_od) nep_od,\r\n" + "sum(discount_gep_od) discount_gep_od,\r\n"
					+ "sum(discount_nep_od) discount_nep_od,\r\n" + "sum(gep_tp) gep_tp,\r\n"
					+ "sum(nep_od) nep_tp,\r\n" + "sum(gep)*0.3 burn_cost,\r\n" + "sum(gep_od)*0.1 ibnr_gic_od,\r\n"
					+ "sum(gep_od)*0.2 nbnr_gic_od,\r\n" + "sum(nep_od)*0.1 ibnr_nic_od,\r\n"
					+ "sum(nep_od)*0.2 nbnr_nic_od,\r\n" + "sum(gep_tp)*0.1 ibnr_gic_tp,\r\n"
					+ "sum(gep_tp)*0.2 nbnr_gic_tp,\r\n" + "sum(nep_tp)*0.1 ibnr_nic_tp,\r\n"
					+ "sum(nep_tp)*0.2 nbnr_nic_tp,\r\n" + "sum(gep_health)*0.1 ibnr_gic_health,\r\n"
					+ "sum(gep_health)*0.2 nbnr_gic_health,\r\n" + "sum(nep_health)*0.1 ibnr_nic_health,\r\n"
					+ "sum(nep_health)*0.2 nbnr_nic_health,\r\n" + "sum(gep)*1.5 ulr_gic_tp,\r\n"
					+ "sum(nep)*1.5 ulr_nic_tp,\r\n" + "sum(gep_tp)*0.1 ibner_gic_tp,\r\n"
					+ "sum(nep_tp)*0.1 ibner_nic_tp,\r\n" + "sum(gep)*0.2 xol_cost,\r\n" + "sum(gep)*0.3 expenses,\r\n"
					+ "sum(earned_days) earned_days\r\n"
					+ "from\r\n" + "(\r\n" + "select \r\n" + "sum(g.gep_gepcoverage) gep,\r\n"
					+ "sum(g.gep_nepcoverage) nep,\r\n"
					+ "sum(g.GEP_EARNED_DAYS) earned_days,\r\n"
					+ "sum(case when gep_coveragecategory='OD' then gep_gepcoverage else 0 end) gep_od,\r\n"
					+ "sum(case when gep_coveragecategory='OD' then gep_nepcoverage else 0 end) nep_od,\r\n"
					+ "sum(case when gep_coveragetype='DC' and gep_coveragecategory='OD' then gep_gepcoverage else 0 end ) discount_gep_od,\r\n"
					+ "sum(case when gep_coveragetype='DC' and gep_coveragecategory='OD' then gep_nepcoverage else 0 end ) discount_nep_od,\r\n"
					+ "sum(case when  gep_coveragecategory in ('TP','PA') then gep_gepcoverage else 0 end ) gep_tp,\r\n"
					+ "sum(case when  gep_coveragecategory in ('TP','PA') then gep_nepcoverage else 0 end ) nep_tp,\r\n"
					+ "sum(case when product_group like '%Health%' then gep_gepcoverage else 0 end ) gep_health,\r\n"
					+ "sum(case when product_group like '%Health%' then gep_nepcoverage else 0 end )   nep_health\r\n"
					+ "from  rsa_kpi_fact_gep_latest as g\r\n" + "left join kpi_fin_year_master \r\n"
					+ "on g.financial_year = kpi_fin_year_master.fin_year\r\n" + "left join kpi_policy_type_master \r\n"
					+ "on g.floter_flag = kpi_policy_type_master.policy_type\r\n" + "left join kpi_product_master \r\n"
					+ "on g.product_code = kpi_product_master.product_code\r\n" + "left join kpi_branch_master \r\n"
					+ "on g.branch_code = kpi_branch_master.branch_code\r\n" + "left join kpi_campaign_master \r\n"
					+ "on g.campain_code = kpi_campaign_master.campaign_code\r\n" + "left join kpi_oa_master_nw \r\n"
					+ "on g.oa_code = kpi_oa_master_nw.oa_code\r\n" + "left join kpi_model_master_nw \r\n"
					+ "on g.make = kpi_model_master_nw.make and g.modelcode = kpi_model_master_nw.model_code\r\n"
					+ "left join kpi_sub_channel_master_nw \r\n"
					+ "on g.channel = kpi_sub_channel_master_nw.channel_name and g.sub_channel = kpi_sub_channel_master_nw.sub_channel\r\n"
					+ "left join kpi_business_type_master \r\n"
					+ "on g.business_type = kpi_business_type_master.business_type ";

			if (fromYear.equals(toYear)) {
				if (fromMonth.equals(toMonth)) {
					queryStr += " where ( gep_year= " + fromYear + " and gep_month = " + fromMonth + " )";
				} else {
					queryStr += " WHERE (( gep_year=" + fromYear + " and gep_month >= " + fromMonth
							+ " and gep_month <=" + toMonth + " ))";
				}
			} else {
				queryStr += " WHERE (( gep_year=" + fromYear + " and gep_month >= " + fromMonth + " ) or ( gep_year="
						+ toYear + " and gep_month <=" + toMonth + " ))";
			}
			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(g.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(g.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(g.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(KPI_FACT_A.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and g.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and g.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and g.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.VEHICLE_AGE <= " + minVal +
//			        			" and g.VEHICLE_AGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.SUM_INSURED <= " + minVal +
//			        			" and g.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.MAXAGE <= " + minVal +
//			        			" and g.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and g.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.AGE_BAND <= " + minVal +
//			        			" and g.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.FAMILY_SIZE <= " + minVal +
//			        			" and g.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and g.DISEASE in (" + vals + ")";
			}

			queryStr += " group by\r\n" + "g.product_code,\r\n" + "g.branch_code,\r\n" + "g.channel,\r\n"
					+ "g.sub_channel,\r\n" + "g.financial_year,\r\n" + "g.eff_fin_year_month,\r\n"
					+ "g.business_type,\r\n" + "g.modelcode,\r\n" + "g.make\r\n" + ") x  ";
//			queryStr += " ) x";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				GepCubeResponse gepCubeResponse = new GepCubeResponse();
				gepCubeResponse.setGep(rs.getDouble(1));
				gepCubeResponse.setNep(rs.getDouble(2));
				gepCubeResponse.setGepOd(rs.getDouble(3));
				gepCubeResponse.setNepOd(rs.getDouble(4));
				gepCubeResponse.setDiscountGepOd(rs.getDouble(5));
				gepCubeResponse.setDiscountNepOd(rs.getDouble(6));
				gepCubeResponse.setGepTp(rs.getDouble(7));
				gepCubeResponse.setNepTp(rs.getDouble(8));
				gepCubeResponse.setBurnCost(rs.getDouble(9));
				gepCubeResponse.setIbnrGicOd(rs.getDouble(10));
				gepCubeResponse.setNbnrGicOd(rs.getDouble(11));
				gepCubeResponse.setIbnrNicOd(rs.getDouble(12));
				gepCubeResponse.setNbnrNicOd(rs.getDouble(13));

				gepCubeResponse.setIbnrGicTp(rs.getDouble(14));
				gepCubeResponse.setNbnrGicTp(rs.getDouble(15));
				gepCubeResponse.setIbnrNicTp(rs.getDouble(16));
				gepCubeResponse.setNbnrNicTp(rs.getDouble(17));

				gepCubeResponse.setIbnrGicHealth(rs.getDouble(18));
				gepCubeResponse.setNbnrGicHealth(rs.getDouble(19));
				gepCubeResponse.setIbnrNicHealth(rs.getDouble(20));
				gepCubeResponse.setNbnrNicHealth(rs.getDouble(21));
				gepCubeResponse.setUlrGicTp(rs.getDouble(22));
				gepCubeResponse.setUlrNicTp(rs.getDouble(23));

				gepCubeResponse.setIbnerGicTp(rs.getDouble(24));
				gepCubeResponse.setIbnerNicTp(rs.getDouble(25));
				gepCubeResponse.setXolCost(rs.getDouble(26));
				gepCubeResponse.setExpenses(rs.getDouble(27));

				gepCubeResponse.setEarnedDays(rs.getDouble(28));
//				gepCubeResponse.setAverageNep(rs.getDouble(21));
//				gepCubeResponse.setAverageGep(rs.getDouble(22));
//				gepCubeResponse.setEarnedPolicies(rs.getDouble(23));
//				gepCubeResponse.setNac(rs.getDouble(24));

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

	
	//@GetMapping("/getUwGepCubeData")
	@ResponseBody
	
public List<GepCubeResponse> getUWGEPCubeData(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
			throws SQLException {
		Connection connection = null;
		List<GepCubeResponse> kpiResponseList = new ArrayList<GepCubeResponse>();
		long startTime = System.currentTimeMillis();
		try {
			String fromDate = filterRequest.getUwMonth() == null ? "" : filterRequest.getUwMonth();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = fromYear+"-"+fromMonth+"-31";

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

//			String fromMonth = fromDate.split("/")[0];
//			String fromYear = fromDate.split("/")[1];
			String toMonth = fromDate.split("/")[0];
			String toYear = fromDate.split("/")[1];

			/*
			 * String queryStr = "select \r\n" + "sum(gep) gep,\r\n" + "sum(nep) nep\r\n" +
			 * "sum(gep_od) gep_od,\r\n" + "sum(nep_od) nep_od,\r\n" +
			 * "sum(discount_gep_od) discount_gep_od,\r\n" +
			 * "sum(discount_nep_od) discount_nep_od,\r\n" + "sum(gep_tp) gep_tp,\r\n" +
			 * "sum(nep_od) nep_tp,\r\n" + "sum(gep)*0.3 burn_cost,\r\n" +
			 * "sum(gep_od)*0.1 ibnr_gic_od,\r\n" + "sum(gep_od)*0.2 nbnr_gic_od,\r\n" +
			 * "sum(nep_od)*0.1 ibnr_nic_od,\r\n" + "sum(nep_od)*0.2 nbnr_nic_od,\r\n" +
			 * "sum(gep)*1.5 ulr_gic_tp,\r\n" + "sum(nep)*1.5 ulr_nic_tp,\r\n" +
			 * "sum(gep_tp)*0.1 ibner_gic_tp,\r\n" + "sum(nep_tp)*0.1 ibner_nic_tp,\r\n" +
			 * "sum(gep)*0.3 xol_cost,\r\n" + "sum(gep_health)*0.1 ibnr_gic_health,\r\n" +
			 * "sum(gep_health)*0.2 nbnr_gic_health,\r\n" +
			 * "sum(nep_health)*0.1 ibnr_nic_health,\r\n" +
			 * "sum(nep_health)*0.2 nbnr_nic_health,\r\n" +
			 * "sum(gep_earned_days) earned_days\r\n" + "sum(gep)*0.3 expenses\r\n" +
			 * "from\r\n" + "(\r\n" + "select \r\n" + "g.agent_code,\r\n" +
			 * "g.product_code,\r\n" + "g.branch_code,\r\n" + "g.oa_code,\r\n" +
			 * "g.policy_si,\r\n" + "g.floter_flag,\r\n" + "g.campain_code,\r\n" +
			 * "g.channel,\r\n" + "g.sub_channel,\r\n" + "g.financial_year,\r\n" +
			 * "g.eff_fin_year_month,\r\n" + "g.family_size,\r\n" + "g.product_group,\r\n" +
			 * "g.stp_nstp,\r\n" + "g.previous_si,\r\n" + "g.uw_year,\r\n" + "g.maxage,\r\n"
			 * + "g.subline,\r\n" + "g.business_type,\r\n" + "g.product_type,\r\n" +
			 * "g.disease_code,\r\n" + "g.totalnumberofyearswithrs,\r\n" +
			 * "g.numberofyearswithrsinmigrationpolicy,\r\n" + "g.migration_flag,\r\n" +
			 * "g.modelcode,\r\n" + "g.make,\r\n" + "g.model,\r\n" + "g.modelgroup,\r\n" +
			 * "g.classofvehicle,\r\n" + "g.vehicleage,\r\n" + "g.seatingcapacity,\r\n" +
			 * "g.fueltype,\r\n" + "g.gep_month,\r\n" + "g.gep_year,\r\n" +
			 * "g.gep_earned_days,\r\n" + "g.gep_gepcoverage gep,\r\n" +
			 * "g.gep_nepcoverage nep\r\n" +
			 * ",(case when gep_coveragecategory='OD' then gep_gepcoverage else 0 end) gep_od,\r\n"
			 * +
			 * "(case when gep_coveragecategory='OD' then gep_nepcoverage else 0 end) nep_od,\r\n"
			 * +
			 * "(case when gep_coveragetype='DC' and gep_coveragecategory='od' then gep_gepcoverage else 0 end ) discount_gep_od,\r\n"
			 * +
			 * "(case when gep_coveragetype='DC' and gep_coveragecategory='od' then gep_nepcoverage else 0 end ) discount_nep_od,\r\n"
			 * +
			 * "(case when  gep_coveragecategory in ('TP','PA') then gep_gepcoverage else 0 end ) gep_tp,\r\n"
			 * +
			 * "(case when  gep_coveragecategory in ('TP','PA') then gep_nepcoverage else 0 end ) nep_tp,\r\n"
			 * +
			 * "(case when product_group like '%Health%' then gep_gepcoverage else 0 end ) gep_health,\r\n"
			 * +
			 * "(case when product_group like '%Health%' then gep_nepcoverage else 0 end ) nep_health\r\n"
			 * + "from  rsa_kpi_fact_gep as g\r\n" + "left join kpi_fin_year_master \r\n" +
			 * "on g.financial_year = kpi_fin_year_master.fin_year\r\n" +
			 * "left join kpi_policy_type_master \r\n" +
			 * "on g.floter_flag = kpi_policy_type_master.policy_type\r\n" +
			 * "left join kpi_product_master \r\n" +
			 * "on g.product_code = kpi_product_master.product_code\r\n" +
			 * "left join kpi_branch_master \r\n" +
			 * "on g.branch_code = kpi_branch_master.branch_code\r\n" +
			 * "left join kpi_campaign_master \r\n" +
			 * "on g.campain_code = kpi_campaign_master.campaign_code\r\n" +
			 * "left join kpi_oa_master_nw \r\n" +
			 * "on g.oa_code = kpi_oa_master_nw.oa_code\r\n" +
			 * "left join kpi_model_master_nw \r\n" +
			 * "on g.make = kpi_model_master_nw.make and g.modelcode = kpi_model_master_nw.model_code\r\n"
			 * + "left join kpi_sub_channel_master_nw \r\n" +
			 * "on g.channel = kpi_sub_channel_master_nw.channel_name and g.sub_channel = kpi_sub_channel_master_nw.sub_channel\r\n"
			 * + "left join kpi_business_type_master \r\n" +
			 * "on g.business_type = kpi_business_type_master.business_type\r\n";
			 */

			String queryStr = "select \r\n" + "sum(gep) gep,\r\n" + "sum(nep) nep,\r\n" + "sum(gep_od) gep_od,\r\n"
					+ "sum(nep_od) nep_od,\r\n" + "sum(discount_gep_od) discount_gep_od,\r\n"
					+ "sum(discount_nep_od) discount_nep_od,\r\n" + "sum(gep_tp) gep_tp,\r\n"
					+ "sum(nep_od) nep_tp,\r\n" + "sum(gep)*0.3 burn_cost,\r\n" + "sum(gep_od)*0.1 ibnr_gic_od,\r\n"
					+ "sum(gep_od)*0.2 nbnr_gic_od,\r\n" + "sum(nep_od)*0.1 ibnr_nic_od,\r\n"
					+ "sum(nep_od)*0.2 nbnr_nic_od,\r\n" + "sum(gep_tp)*0.1 ibnr_gic_tp,\r\n"
					+ "sum(gep_tp)*0.2 nbnr_gic_tp,\r\n" + "sum(nep_tp)*0.1 ibnr_nic_tp,\r\n"
					+ "sum(nep_tp)*0.2 nbnr_nic_tp,\r\n" + "sum(gep_health)*0.1 ibnr_gic_health,\r\n"
					+ "sum(gep_health)*0.2 nbnr_gic_health,\r\n" + "sum(nep_health)*0.1 ibnr_nic_health,\r\n"
					+ "sum(nep_health)*0.2 nbnr_nic_health,\r\n" + "sum(gep)*1.5 ulr_gic_tp,\r\n"
					+ "sum(nep)*1.5 ulr_nic_tp,\r\n" + "sum(gep_tp)*0.1 ibner_gic_tp,\r\n"
					+ "sum(nep_tp)*0.1 ibner_nic_tp,\r\n" + "sum(gep)*0.2 xol_cost,\r\n" + "sum(gep)*0.3 expenses,\r\n"
					+ "sum(EARNED_DAYS) earned_days\r\n"
					+ "from\r\n" + "(\r\n" + "select \r\n" + "sum(g.gep_gepcoverage) gep,\r\n"
					+ "sum(g.gep_nepcoverage) nep,\r\n"
					+ "sum(g.GEP_EARNED_DAYS) earned_days,"
					+ "sum(case when gep_coveragecategory='OD' then gep_gepcoverage else 0 end) gep_od,\r\n"
					+ "sum(case when gep_coveragecategory='OD' then gep_nepcoverage else 0 end) nep_od,\r\n"
					+ "sum(case when gep_coveragetype='DC' and gep_coveragecategory='OD' then gep_gepcoverage else 0 end ) discount_gep_od,\r\n"
					+ "sum(case when gep_coveragetype='DC' and gep_coveragecategory='OD' then gep_nepcoverage else 0 end ) discount_nep_od,\r\n"
					+ "sum(case when gep_coveragecategory in ('TP','PA') then gep_gepcoverage else 0 end ) gep_tp,\r\n"
					+ "sum(case when gep_coveragecategory in ('TP','PA') then gep_nepcoverage else 0 end ) nep_tp,\r\n"
					+ "sum(case when product_group like '%Health%' then gep_gepcoverage else 0 end ) gep_health,\r\n"
					+ "sum(case when product_group like '%Health%' then gep_nepcoverage else 0 end )   nep_health\r\n"
					+ "from  rsa_kpi_fact_gep_latest as g\r\n" + "left join kpi_fin_year_master \r\n"
					+ "on g.financial_year = kpi_fin_year_master.fin_year\r\n" + "left join kpi_policy_type_master \r\n"
					+ "on g.floter_flag = kpi_policy_type_master.policy_type\r\n" + "left join kpi_product_master \r\n"
					+ "on g.product_code = kpi_product_master.product_code\r\n" + "left join kpi_branch_master \r\n"
					+ "on g.branch_code = kpi_branch_master.branch_code\r\n" + "left join kpi_campaign_master \r\n"
					+ "on g.campain_code = kpi_campaign_master.campaign_code\r\n" + "left join kpi_oa_master_nw \r\n"
					+ "on g.oa_code = kpi_oa_master_nw.oa_code\r\n" + "left join kpi_model_master_nw \r\n"
					+ "on g.make = kpi_model_master_nw.make and g.modelcode = kpi_model_master_nw.model_code\r\n"
					+ "left join kpi_sub_channel_master_nw \r\n"
					+ "on g.channel = kpi_sub_channel_master_nw.channel_name and g.sub_channel = kpi_sub_channel_master_nw.sub_channel\r\n"
					+ "left join kpi_business_type_master \r\n"
					+ "on g.business_type = kpi_business_type_master.business_type ";

			if (fromYear.equals(toYear)) {
				if (fromMonth.equals(toMonth)) {
					queryStr += " where ( gep_year= " + fromYear + " and gep_month = " + fromMonth + " )";
				} else {
					queryStr += " WHERE (( gep_year=" + fromYear + " and gep_month >= " + fromMonth
							+ " and gep_month <=" + toMonth + " ))";
				}
			} else {
				queryStr += " WHERE (( gep_year=" + fromYear + " and gep_month >= " + fromMonth + " ) or ( gep_year="
						+ toYear + " and gep_month <=" + toMonth + " ))";
			}
			
//			queryStr += " where INCEPTION_DATE between '"+inceptionStartDate+"' and '"+inceptionEndDate+"' ";
			
			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(g.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(g.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(g.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(KPI_FACT_A.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and g.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and g.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and g.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.VEHICLE_AGE <= " + minVal +
//			        			" and g.VEHICLE_AGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.SUM_INSURED <= " + minVal +
//			        			" and g.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.MAXAGE <= " + minVal +
//			        			" and g.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and g.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.AGE_BAND <= " + minVal +
//			        			" and g.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.FAMILY_SIZE <= " + minVal +
//			        			" and g.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and g.DISEASE in (" + vals + ")";
			}

			queryStr += " group by\r\n" + "g.product_code,\r\n" + "g.branch_code,\r\n" + "g.channel,\r\n"
					+ "g.sub_channel,\r\n" + "g.financial_year,\r\n" + "g.eff_fin_year_month,\r\n"
					+ "g.business_type,\r\n" + "g.modelcode,\r\n" + "g.make\r\n" + ") x  ";
//			queryStr += " ) x";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				GepCubeResponse gepCubeResponse = new GepCubeResponse();
				gepCubeResponse.setGep(rs.getDouble(1));
				gepCubeResponse.setNep(rs.getDouble(2));
				gepCubeResponse.setGepOd(rs.getDouble(3));
				gepCubeResponse.setNepOd(rs.getDouble(4));
				gepCubeResponse.setDiscountGepOd(rs.getDouble(5));
				gepCubeResponse.setDiscountNepOd(rs.getDouble(6));
				gepCubeResponse.setGepTp(rs.getDouble(7));
				gepCubeResponse.setNepTp(rs.getDouble(8));
				gepCubeResponse.setBurnCost(rs.getDouble(9));
				gepCubeResponse.setIbnrGicOd(rs.getDouble(10));
				gepCubeResponse.setNbnrGicOd(rs.getDouble(11));
				gepCubeResponse.setIbnrNicOd(rs.getDouble(12));
				gepCubeResponse.setNbnrNicOd(rs.getDouble(13));

				gepCubeResponse.setIbnrGicTp(rs.getDouble(14));
				gepCubeResponse.setNbnrGicTp(rs.getDouble(15));
				gepCubeResponse.setIbnrNicTp(rs.getDouble(16));
				gepCubeResponse.setNbnrNicTp(rs.getDouble(17));

				gepCubeResponse.setIbnrGicHealth(rs.getDouble(18));
				gepCubeResponse.setNbnrGicHealth(rs.getDouble(19));
				gepCubeResponse.setIbnrNicHealth(rs.getDouble(20));
				gepCubeResponse.setNbnrNicHealth(rs.getDouble(21));
				gepCubeResponse.setUlrGicTp(rs.getDouble(22));
				gepCubeResponse.setUlrNicTp(rs.getDouble(23));

				gepCubeResponse.setIbnerGicTp(rs.getDouble(24));
				gepCubeResponse.setIbnerNicTp(rs.getDouble(25));
				gepCubeResponse.setXolCost(rs.getDouble(26));
				gepCubeResponse.setExpenses(rs.getDouble(27));
				gepCubeResponse.setEarnedDays(rs.getDouble(28));

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
	
	
	
	
	
	
	//@RequestMapping(value = "/getUWPolicyCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CubeAKpiResponse> getKpiUwPolicyData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<CubeAKpiResponse> kpiResponseList = new ArrayList<CubeAKpiResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
			String fromDate = filterRequest.getUwMonth() == null ? "" : filterRequest.getUwMonth();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = fromDate.split("/")[0];
			String toYear = fromDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = fromYear+"-"+fromMonth+"-31";
			
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";
			
			String queryStr = "select\r\n" + "sum(livescovered) livescovered,\r\n"
					+ "sum(writtenPolicies) writtenPolicies,\r\n" + "sum(acq_cost) acq_cost\r\n" + "from(\r\n"
					+ "select\r\n" + "sum(RSA_KPI_FACT_POLICY.LIVESCOVERED) livescovered,\r\n"
					+ "sum(case when ENDORSEMENT_CODE in('00','11','12') then 1 when ENDORSEMENT_CODE in ('02','08') then -1 else 0 end) writtenPolicies,\r\n"
					+ "sum(RSA_KPI_FACT_POLICY.acq_cost) acq_cost\r\n"
					+ " FROM RSDB.RSA_KPI_FACT_POLICY as RSA_KPI_FACT_POLICY\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n"
					+ "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_POLICY.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n"
					+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n"
					+ "LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_POLICY.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n"
					+ "LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n"
					+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE ";
			
//			queryStr += " where inception_date between '"+inceptionStartDate+"' and '"+inceptionEndDate+"'";

			if (fromYear.equals(toYear)) {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			} else {
				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
			}

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(KPI_FACT_A_NEW.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_POLICY_TYPE_MASTER.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.VEHICLEAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.VEHICLEAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.SUM_INSURED <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.MAXAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.AGE_BAND <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.FAMILY_SIZE <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and RSA_KPI_FACT_POLICY.DISEASE_CODE in (" + vals + ")";
			}

			queryStr += ")";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				CubeAKpiResponse res = new CubeAKpiResponse();
				res.setWrittenPolicies(rs.getDouble(2));
				res.setLivesCovered(rs.getDouble(1));
				res.setAcqCost(rs.getDouble(3));

				kpiResponseList.add(res);
			}

			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			e.printStackTrace();
		} finally {
			connection.close();
		}

		return kpiResponseList;

	}
	
	//@RequestMapping(value = "/getUwClaimsCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ClaimsCubeResponse> getKpiUwClaimsData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<ClaimsCubeResponse> kpiResponseList = new ArrayList<ClaimsCubeResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
			String fromDate = filterRequest.getUwMonth() == null ? "" : filterRequest.getUwMonth();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = fromYear+"-"+fromMonth+"-31";
			
//			String claimMovementStartDate = fromYear + "-" + fromMonth + "-01";
//			String claimMovementEndDate = toYear + "-" + toMonth + "-31";
			
			
			
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String queryStr = " select\r\n" + "sum(registered_claims) registered_claims\r\n" + "from\r\n" + "(\r\n"
					+ "SELECT\r\n" + "RSA_KPI_FACT_CLAIMS_LATEST.EXPIRTY_DATE as RSA_KPI_FACT_CLAIMS_LATEST_EXPIRTY_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_CODE as RSA_KPI_FACT_CLAIMS_LATEST_PRODUCT_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE as RSA_KPI_FACT_CLAIMS_LATEST_BRANCH_CODE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_TYPE_CODE as RSA_KPI_FACT_CLAIMS_LATEST_BUSINESS_TYPE_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.OA_CODE as RSA_KPI_FACT_CLAIMS_LATEST_OA_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_SI as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_SI\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MIGRATION_FLAG as RSA_KPI_FACT_CLAIMS_LATEST_MIGRATION_FLAG\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FLOTER_FLAG as RSA_KPI_FACT_CLAIMS_LATEST_FLOTER_FLAG\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE as RSA_KPI_FACT_CLAIMS_LATEST_CAMPAIN_CODE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_STATUS as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_STATUS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL as RSA_KPI_FACT_CLAIMS_LATEST_CHANNEL\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL as RSA_KPI_FACT_CLAIMS_LATEST_SUB_CHANNEL\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FINANCIAL_YEAR as RSA_KPI_FACT_CLAIMS_LATEST_FINANCIAL_YEAR\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.ENTRY_YEAR as RSA_KPI_FACT_CLAIMS_LATEST_ENTRY_YEAR\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_BAND as RSA_KPI_FACT_CLAIMS_LATEST_BUSINESS_BAND\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.EFF_FIN_YEAR_MONTH as RSA_KPI_FACT_CLAIMS_LATEST_EFF_FIN_YEAR_MONTH\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FAMILY_SIZE as RSA_KPI_FACT_CLAIMS_LATEST_FAMILY_SIZE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_GROUP as RSA_KPI_FACT_CLAIMS_LATEST_PRODUCT_GROUP\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.STP_NSTP as RSA_KPI_FACT_CLAIMS_LATEST_STP_NSTP\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PREVIOUS_SI as RSA_KPI_FACT_CLAIMS_LATEST_PREVIOUS_SI\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.UW_YEAR as RSA_KPI_FACT_CLAIMS_LATEST_UW_YEAR\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_SOURCE_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_SOURCE_TYPE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.ENTRY_DATE as RSA_KPI_FACT_CLAIMS_LATEST_ENTRY_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MAXAGE as RSA_KPI_FACT_CLAIMS_LATEST_MAXAGE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.SUBLINE as RSA_KPI_FACT_CLAIMS_LATEST_SUBLINE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_BUSINESS_TYPE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_PRODUCT_TYPE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.DISEASE_CODE as RSA_KPI_FACT_CLAIMS_LATEST_DISEASE_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS as RSA_KPI_FACT_CLAIMS_LATEST_TOTALNUMBEROFYEARSWITHRS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY as RSA_KPI_FACT_CLAIMS_LATEST_NUMBEROFYEARSWITHRSINMIGRATIONPOLICY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MODELCODE as RSA_KPI_FACT_CLAIMS_LATEST_MODELCODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MAKE as RSA_KPI_FACT_CLAIMS_LATEST_MAKE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MODEL as RSA_KPI_FACT_CLAIMS_LATEST_MODEL\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MODELGROUP as RSA_KPI_FACT_CLAIMS_LATEST_MODELGROUP\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLASSOFVEHICLE as RSA_KPI_FACT_CLAIMS_LATEST_CLASSOFVEHICLE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.VEHICLEAGE as RSA_KPI_FACT_CLAIMS_LATEST_VEHICLEAGE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.SEATINGCAPACITY as RSA_KPI_FACT_CLAIMS_LATEST_SEATINGCAPACITY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FUELTYPE as RSA_KPI_FACT_CLAIMS_LATEST_FUELTYPE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.REGSTATE as RSA_KPI_FACT_CLAIMS_LATEST_REGSTATE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.REGZONE as RSA_KPI_FACT_CLAIMS_LATEST_REGZONE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.REGLOCATION as RSA_KPI_FACT_CLAIMS_LATEST_REGLOCATION\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FIN_DATE as RSA_KPI_FACT_CLAIMS_LATEST_FIN_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_CATEGORY as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_CATEGORY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_MOVEMENT_DATE as RSA_KPI_FACT_CLAIMS_LATEST_CLM_MOVEMENT_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_CLAIM_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_CLM_CLAIM_TYPE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_CLAIM_MVMT_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_CLM_CLAIM_MVMT_TYPE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_FYEAR as RSA_KPI_FACT_CLAIMS_LATEST_CLM_FYEAR\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_REPUDIATEDCLAIMS as RSA_KPI_FACT_CLAIMS_LATEST_CLM_REPUDIATEDCLAIMS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_IFTHEFTCLAIMS as RSA_KPI_FACT_CLAIMS_LATEST_CLM_IFTHEFTCLAIMS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_IFFRAUDCLAIMS as RSA_KPI_FACT_CLAIMS_LATEST_CLM_IFFRAUDCLAIMS\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_SUMINSURED as RSA_KPI_FACT_CLAIMS_LATEST_CLM_SUMINSURED\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_IFCATOSTROPHIC as RSA_KPI_FACT_CLAIMS_LATEST_CLM_IFCATOSTROPHIC\r\n"
					+ ",KPI_FIN_YEAR_MASTER.FIN_YEAR as KPI_FIN_YEAR_MASTER_FIN_YEAR\r\n"
					+ ",KPI_FIN_MONTH_MASTER.FIN_MONTH as KPI_FIN_MONTH_MASTER_FIN_MONTH\r\n"
					+ ",KPI_PRODUCT_MASTER.PRODUCT_CODE as KPI_PRODUCT_MASTER_PRODUCT_CODE\r\n"
					+ ",KPI_PRODUCT_MASTER.PRODUCT_DESCRIPTION as KPI_PRODUCT_MASTER_PRODUCT_DESCRIPTION\r\n"
					+ ",KPI_POLICY_TYPE_MASTER.POLICY_TYPE as KPI_POLICY_TYPE_MASTER_POLICY_TYPE\r\n"
					+ ",KPI_BRANCH_MASTER.BRANCH_CODE as KPI_BRANCH_MASTER_BRANCH_CODE\r\n"
					+ ",KPI_BRANCH_MASTER.REVISED_BRANCH_NAME as KPI_BRANCH_MASTER_REVISED_BRANCH_NAME\r\n"
					+ ",KPI_BRANCH_MASTER.REGION as KPI_BRANCH_MASTER_REGION\r\n"
					+ ",KPI_BRANCH_MASTER.STATE_NEW as KPI_BRANCH_MASTER_STATE_NEW\r\n"
					+ ",KPI_BRANCH_MASTER.CLUSTER_NAME as KPI_BRANCH_MASTER_CLUSTER_NAME\r\n"
					+ ",KPI_BRANCH_MASTER.SUB_CLUSTER as KPI_BRANCH_MASTER_SUB_CLUSTER\r\n"
					+ ",KPI_BRANCH_MASTER.RA_CITY_FLAG as KPI_BRANCH_MASTER_RA_CITY_FLAG\r\n"
					+ ",KPI_BRANCH_MASTER.RA_DESCRIPTION as KPI_BRANCH_MASTER_RA_DESCRIPTION\r\n"
					+ ",KPI_BRANCH_MASTER.ZONE as KPI_BRANCH_MASTER_ZONE\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODEL_CODE as KPI_MODEL_MASTER_NW_MODEL_CODE\r\n"
					+ ",KPI_MODEL_MASTER_NW.MAKE as KPI_MODEL_MASTER_NW_MAKE\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODEL as KPI_MODEL_MASTER_NW_MODEL\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODELGROUP as KPI_MODEL_MASTER_NW_MODELGROUP\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODELCLASSIFICATION as KPI_MODEL_MASTER_NW_MODELCLASSIFICATION\r\n"
					+ ",KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE as KPI_CAMPAIGN_MASTER_CAMPAIGN_CODE\r\n"
					+ ",KPI_CAMPAIGN_MASTER.CAMPAIGN_DESCRIPTION as KPI_CAMPAIGN_MASTER_CAMPAIGN_DESCRIPTION\r\n"
					+ ",KPI_OA_MASTER_NW.OA_CODE as KPI_OA_MASTER_NW_OA_CODE\r\n"
					+ ",KPI_OA_MASTER_NW.OA_NAME as KPI_OA_MASTER_NW_OA_NAME\r\n"
					+ ",KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME as KPI_SUB_CHANNEL_MASTER_NW_CHANNEL_NAME\r\n"
					+ ",KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL as KPI_SUB_CHANNEL_MASTER_NW_SUB_CHANNEL\r\n"
					+ ",KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE as KPI_BUSINESS_TYPE_MASTER_BUSINESS_TYPE\r\n"
					+ ",KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY as KPI_POLICY_CATEGORY_MASTER_NW_POLICY_CATEGORY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_CLAIM_NO as RSA_KPI_FACT_CLAIMS_LATEST_CLM_CLAIM_NO,\r\n"
					+ "(case when  CLM_REGISTRATION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "' then 1 else 0 end ) registered_claims\r\n"
					+ " FROM RSDB.RSA_KPI_FACT_CLAIMS_LATEST as RSA_KPI_FACT_CLAIMS_LATEST\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n"
					+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n"
					+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n"
					+ "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n";

			queryStr += " WHERE CLM_REGISTRATION_DATE between '" + inceptionStartDate + "' and '"+ inceptionEndDate + "'";

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(KPI_FACT_A.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.VEHICLE_AGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.VEHICLE_AGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.SUM_INSURED <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.MAXAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.AGE_BAND <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.FAMILY_SIZE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.DISEASE in (" + vals + ")";
			}

			queryStr += ")";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				ClaimsCubeResponse res = new ClaimsCubeResponse();
				res.setRegisteredClaims(rs.getDouble(1));

				kpiResponseList.add(res);
			}

			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return kpiResponseList;

	}
	
	//@RequestMapping(value = "/getUWSingleLineCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ClaimsSingleLineCubeResponse> getKpiUwSingleLineData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<ClaimsSingleLineCubeResponse> kpiResponseList = new ArrayList<ClaimsSingleLineCubeResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
			String fromDate = filterRequest.getUwMonth() == null ? "" : filterRequest.getUwMonth();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = fromYear+"-"+fromMonth+"-31";
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

//			String fromMonth = fromDate.split("/")[0];
//			String fromYear = fromDate.split("/")[1];
			String toMonth = fromDate.split("/")[0];
			String toYear = fromDate.split("/")[1];

			String claimMvmtStartMonth = fromYear + fromMonth;
			String claimMvmtEndMonth = toYear + toMonth;

			String claimSingleLineBaseQuery = "";
			String claimSingleLineFinYrCon = "";
			String claimSingleLineFiltersCon = "";
			String queryStr = "";

			claimSingleLineBaseQuery = "select\r\n" + 
					"sum(actual_gic_health) actual_gic_health\r\n" + 
					",sum(claims_paid) claims_paid\r\n" + 
					"from(\r\n" + 
					"SELECT\r\n" + 
					"sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_GIC) actual_gic_health\r\n" + 
					",sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_TOTAL_PAID) claims_paid\r\n" + 
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n";

//			claimSingleLineFinYrCon += " where INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
//					+ inceptionEndDate + "'";
			
			claimSingleLineFinYrCon += " where CSL_MVMT_MONTH between '" + claimMvmtStartMonth + "' AND '"
					+ claimMvmtEndMonth + "'";

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL) in (" + vals
						+ ")";
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
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL) in (" + vals
						+ ")";
			}

			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL) in (" + vals
						+ ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL) in ("
						+ vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE) in (" + vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

				String vals = "'VGC','VPC','VMC','VOC'";
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_FACT_A.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE) in (" + vals
						+ ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_FACT_A_UPDATED.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLE_AGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLE_AGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUM_INSURED <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.AGE_BAND <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.DISEASE in (" + vals + ")";
			}

			String claimSingleLineEnd = " group by\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE\r\n" + 
					") ";

			queryStr = claimSingleLineBaseQuery + claimSingleLineFinYrCon + claimSingleLineFiltersCon
					+ claimSingleLineEnd;

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			ClaimsSingleLineCubeResponse res = new ClaimsSingleLineCubeResponse();
			while (rs.next()) {

//				res.setActualGicOd(rs.getDouble(1));
//				res.setActualGicTp(rs.getDouble(2));
				res.setActualGicHealth(rs.getDouble(1));
				res.setPaid(rs.getDouble(2));

			}

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			String claimsSingleLineReputiatedClaimsBase = "select\r\n" + 
					"count(csl_claim_no)\r\n" + 
					"from(\r\n" + 
					"select\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.csl_claim_no\r\n" + 
					",SUM(csl_closing_os_total_org) as close_tot,\r\n" + 
					"SUM(csl_LOSS_PAID_CUM) as loss \r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"where\r\n" + 
					"(CSL_MVMT_MONTH,CSL_CLAIM_NO) in  (select max(CSL_MVMT_MONTH),CSL_CLAIM_NO \r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "
					+ " where INCEPTION_DATE between '" + inceptionStartDate + "' AND '" + inceptionEndDate
					+ "' ";

					String claimsReputiatedEnd = " group by CSL_CLAIM_NO) GROUP BY \r\n" + 
							"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.csl_claim_no\r\n" + 
							",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE\r\n" + 
							")y\r\n" + 
							"where  close_tot=0 and loss=0  ";

			queryStr = claimsSingleLineReputiatedClaimsBase + claimSingleLineFiltersCon + claimsReputiatedEnd;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setReputiatedClaims(rs.getDouble(1));
				

			}

			queryStr = "select sum(CSL_OPENING_TOTAL_ORG) from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST "
					+ "where (csl_claim_no,csl_mvmt_month) in "
					+ "( SELECT csl_claim_no,max(csl_mvmt_month) FROM RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n"
					+ "where INCEPTION_DATE between '" + inceptionStartDate + "' AND '" + inceptionEndDate + "'\r\n"
					+ "group by csl_claim_no);";

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setOpeningOsClaims(rs.getDouble(1));

			}

			queryStr = "select sum(CSL_CLOSING_OS_TOTAL_ORG) from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST"
					+ " where (csl_claim_no,csl_mvmt_month) in"
					+ " ( SELECT csl_claim_no,min(csl_mvmt_month) FROM RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n"
					+ "where INCEPTION_DATE between '" + inceptionStartDate + "' AND '" + inceptionEndDate + "' "
					+ "group by csl_claim_no);";

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setClosingOsClaim(rs.getDouble(1));

			}
			
			//actual gic od
			queryStr = "select\r\n" + 
					"sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC)\r\n" + 
					"from(\r\n" + 
					"SELECT\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_INCEPTION_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_SI as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_SI\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MIGRATION_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FLOTER_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FINANCIAL_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_EFF_FIN_YEAR_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FAMILY_SIZE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_GROUP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_GROUP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.STP_NSTP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_STP_NSTP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_UW_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAXAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUBLINE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_BUSINESS_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.DISEASE_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_DISEASE_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_TOTALNUMBEROFYEARSWITHRS\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_NUMBEROFYEARSWITHRSINMIGRATIONPOLICY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CLASSOFVEHICLE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CLASSOFVEHICLE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLEAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_VEHICLEAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SEATINGCAPACITY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SEATINGCAPACITY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FUELTYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FUELTYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGSTATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGSTATE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGZONE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGZONE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGLOCATION as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGLOCATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FIN_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FIN_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_CATEGORY\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHECODE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHECODE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHICTYPE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHICTYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MAX_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MAX_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MIN_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MIN_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MVMT_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MVMT_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REGISTRATION_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REGISTRATION_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE_DESC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE_DESC\r\n" + 
					",KPI_FIN_YEAR_MASTER.FIN_YEAR as KPI_FIN_YEAR_MASTER_FIN_YEAR\r\n" + 
					",KPI_FIN_MONTH_MASTER.FIN_MONTH as KPI_FIN_MONTH_MASTER_FIN_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUB_CHANNEL\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME as KPI_SUB_CHANNEL_MASTER_NW_CHANNEL_NAME\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL as KPI_SUB_CHANNEL_MASTER_NW_SUB_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_CODE as KPI_PRODUCT_MASTER_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_DESCRIPTION as KPI_PRODUCT_MASTER_PRODUCT_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CAMPAIN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE as KPI_CAMPAIGN_MASTER_CAMPAIGN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_DESCRIPTION as KPI_CAMPAIGN_MASTER_CAMPAIGN_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAKE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MODELCODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL_CODE as KPI_MODEL_MASTER_NW_MODEL_CODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MAKE as KPI_MODEL_MASTER_NW_MAKE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL as KPI_MODEL_MASTER_NW_MODEL\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELGROUP as KPI_MODEL_MASTER_NW_MODELGROUP\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELCLASSIFICATION as KPI_MODEL_MASTER_NW_MODELCLASSIFICATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_CODE as KPI_OA_MASTER_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_NAME as KPI_OA_MASTER_OA_NAME\r\n" + 
					",KPI_SUBLINE_MASTER.SUBLINE as KPI_SUBLINE_MASTER_SUBLINE\r\n" + 
					",KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE as KPI_BUSINESS_TYPE_MASTER_BUSINESS_TYPE\r\n" + 
					",KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY as KPI_POLICY_CATEGORY_MASTER_POLICY_CATEGORY\r\n" + 
					",KPI_POLICY_TYPE_MASTER.POLICY_TYPE as KPI_POLICY_TYPE_MASTER_POLICY_TYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_EXPENSE_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID_CUM as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID_CUM\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_EXPENSE_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_COUNT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_COUNT\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_GIC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_TOTAL_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_TOTAL_PAID\r\n" + 
					"\r\n" + 
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST \r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					"where RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO not like 'TP%' \r\n";
			
			queryStr += " and ( INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "') " +claimSingleLineFiltersCon+" )x 	";
			
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setActualGicOd(rs.getDouble(1));

			}
			
			//actual gic tp
			queryStr = "select\r\n" + 
					"sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC)\r\n" + 
					"from(\r\n" + 
					"SELECT\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_INCEPTION_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_SI as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_SI\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MIGRATION_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FLOTER_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FINANCIAL_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_EFF_FIN_YEAR_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FAMILY_SIZE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_GROUP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_GROUP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.STP_NSTP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_STP_NSTP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_UW_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAXAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUBLINE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_BUSINESS_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.DISEASE_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_DISEASE_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_TOTALNUMBEROFYEARSWITHRS\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_NUMBEROFYEARSWITHRSINMIGRATIONPOLICY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CLASSOFVEHICLE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CLASSOFVEHICLE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLEAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_VEHICLEAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SEATINGCAPACITY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SEATINGCAPACITY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FUELTYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FUELTYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGSTATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGSTATE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGZONE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGZONE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGLOCATION as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGLOCATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FIN_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FIN_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_CATEGORY\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHECODE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHECODE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHICTYPE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHICTYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MAX_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MAX_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MIN_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MIN_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MVMT_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MVMT_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REGISTRATION_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REGISTRATION_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE_DESC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE_DESC\r\n" + 
					",KPI_FIN_YEAR_MASTER.FIN_YEAR as KPI_FIN_YEAR_MASTER_FIN_YEAR\r\n" + 
					",KPI_FIN_MONTH_MASTER.FIN_MONTH as KPI_FIN_MONTH_MASTER_FIN_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUB_CHANNEL\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME as KPI_SUB_CHANNEL_MASTER_NW_CHANNEL_NAME\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL as KPI_SUB_CHANNEL_MASTER_NW_SUB_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_CODE as KPI_PRODUCT_MASTER_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_DESCRIPTION as KPI_PRODUCT_MASTER_PRODUCT_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CAMPAIN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE as KPI_CAMPAIGN_MASTER_CAMPAIGN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_DESCRIPTION as KPI_CAMPAIGN_MASTER_CAMPAIGN_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAKE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MODELCODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL_CODE as KPI_MODEL_MASTER_NW_MODEL_CODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MAKE as KPI_MODEL_MASTER_NW_MAKE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL as KPI_MODEL_MASTER_NW_MODEL\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELGROUP as KPI_MODEL_MASTER_NW_MODELGROUP\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELCLASSIFICATION as KPI_MODEL_MASTER_NW_MODELCLASSIFICATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_CODE as KPI_OA_MASTER_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_NAME as KPI_OA_MASTER_OA_NAME\r\n" + 
					",KPI_SUBLINE_MASTER.SUBLINE as KPI_SUBLINE_MASTER_SUBLINE\r\n" + 
					",KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE as KPI_BUSINESS_TYPE_MASTER_BUSINESS_TYPE\r\n" + 
					",KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY as KPI_POLICY_CATEGORY_MASTER_POLICY_CATEGORY\r\n" + 
					",KPI_POLICY_TYPE_MASTER.POLICY_TYPE as KPI_POLICY_TYPE_MASTER_POLICY_TYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_EXPENSE_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID_CUM as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID_CUM\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_EXPENSE_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_COUNT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_COUNT\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_GIC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_TOTAL_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_TOTAL_PAID\r\n" + 
					"\r\n" + 
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST \r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					"where RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO like 'TP%' \r\n";
			
			queryStr += " and ( INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "') " +claimSingleLineFiltersCon+" )x 	";
			
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				res.setActualGicTp(rs.getDouble(1));
			}
			
			queryStr = "select sum(NIC),sum(nic_tp),sum(nic_od) FROM(\r\n" + 
					"select A.csl_gic,A.CSL_CLAIM_NO,A.CSL_MVMT_MONTH,B.OBLIGATORY,B.QUOTA_SHARE,B.RETENTION,B.RI_COMMISSION, csl_gic*(1-QUOTA_SHARE-OBLIGATORY) NIC, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end nic_tp, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' then 0 else csl_gic*(1-QUOTA_SHARE-OBLIGATORY) end nic_od from (\r\n" + 
					"select (case when RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R0'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R1'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%102' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R2'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%103' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END) BAND, uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE, sum(csl_gic) csl_gic,CSL_CLAIM_NO,CSL_MVMT_MONTH\r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					" where\r\n" + 
					"(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' or policy_no like '%102' or policy_no like '%103' or policy_no like '%100') and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP'" + 
					" and ( INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "') " +claimSingleLineFiltersCon+"	"+
					" GROUP by\r\n" + 
					"(case when RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R0'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R1'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%102' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R2'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%103' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END), uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH ) A ,\r\n" + 
					"(select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1\r\n" + 
					" group by underwriting_year,XGEN_PRODUCTCODE,band) B\r\n" + 
					"where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band \r\n" + 
					"\r\n" + 
					"union \r\n" + 
					"select A.csl_gic,A.CSL_CLAIM_NO,A.CSL_MVMT_MONTH,B.OBLIGATORY,B.QUOTA_SHARE,B.RETENTION,B.RI_COMMISSION, csl_gic*(1-QUOTA_SHARE-OBLIGATORY) NIC, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end nic_tp, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' then 0 else csl_gic*(1-QUOTA_SHARE-OBLIGATORY) end nic_od from (\r\n" + 
					"select (case when RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R0'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R1'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%102' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R2'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%103' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END) BAND, uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE, sum(csl_gic) csl_gic,CSL_CLAIM_NO,CSL_MVMT_MONTH\r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					" where\r\n" + 
					"(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no not like '%101' and policy_no not like '%102' and policy_no not like '%103' and policy_no not like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP') or RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code<> 'IHP'\r\n" + 
					" and ( INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "') " +claimSingleLineFiltersCon+"	"+
					"  GROUP by\r\n" + 
					"(case when RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%100' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R0'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%101' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R1'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%102' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R2'\r\n" + 
					"WHEN RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.policy_no like '%103' and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END), uw_year,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH ) A ,\r\n" + 
					"(select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 \r\n" + 
					"group by underwriting_year,XGEN_PRODUCTCODE,band) B\r\n" + 
					"where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE\r\n" + 
					"\r\n" + 
					")";
			
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				res.setNic(rs.getDouble(1));
				res.setActualNicTp(rs.getDouble(2));
				res.setActualNicOd(rs.getDouble(3));
				res.setActualNicHealth(rs.getDouble(1));
			}

			kpiResponseList.add(res);

		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return kpiResponseList;
	}
	
	
	
	//@RequestMapping(value = "/getUwInsCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CubeAKpiResponse> getKpiUwInsData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<CubeAKpiResponse> generalKpiResponseList = new ArrayList<CubeAKpiResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
			String fromDate = filterRequest.getUwMonth() == null ? "" : filterRequest.getUwMonth();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = fromYear+"-"+fromMonth+"-31";
			
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";


//			String finYearStart = fromYear + "-" + fromMonth + "-01";
//			String finYearEnd = toYear + "-" + toMonth + "-31";

//			String queryStr = "select\r\n" + 
//					"sum(gwp) gwp\r\n" + 
//					",sum(nwp) nwp\r\n" + 
//					",sum(gwp_od) gwp_od\r\n" + 
//					",SUM(gwp_tp) gwp_tp\r\n" + 
//					",sum(nwp_od) nwp_od\r\n" + 
//					",sum(nwp_tp) nwp_tp\r\n" + 
//					",sum(discount_gwp_od) discount_gwp_od\r\n" + 
//					",sum(discount_nwp_od) discount_nwp_od\r\n" + 
//					"from(\r\n" + 
//					"select\r\n" + 
//					"product_code            \r\n" + 
//					",branch_code             \r\n" + 
//					",oa_code                 \r\n" + 
//					",policy_si               \r\n" + 
//					",migration_flag          \r\n" + 
//					",floter_flag             \r\n" + 
//					",campain_code            \r\n" + 
//					",channel                 \r\n" + 
//					",sub_channel             \r\n" + 
//					",financial_year          \r\n" + 
//					",eff_fin_year_month      \r\n" + 
//					",family_size             \r\n" + 
//					",product_group           \r\n" + 
//					",stp_nstp                \r\n" + 
//					",uw_year                 \r\n" + 
//					",maxage                  \r\n" + 
//					",subline                 \r\n" + 
//					",business_type           \r\n" + 
//					",disease_code            \r\n" + 
//					",totalnumberofyearswithrs        \r\n" + 
//					",numberofyearswithrsinmigrationpolicy    \r\n" + 
//					",modelcode               \r\n" + 
//					",make                    \r\n" + 
//					",model                   \r\n" + 
//					",modelgroup              \r\n" + 
//					",classofvehicle          \r\n" + 
//					",vehicleage              \r\n" + 
//					",seatingcapacity         \r\n" + 
//					",fueltype              \r\n" + 
//					",fin_date                \r\n" + 
//					",policy_category         \r\n" + 
//					",addon                  \r\n" + 
//					",ins_coverage_type  \r\n" + 
//					",ins_coverage_category\r\n" + 
//					",sum(gwp) gwp\r\n" + 
//					",sum(nwp) nwp\r\n" + 
//					",sum(gwp_od) gwp_od\r\n" + 
//					",SUM(gwp_tp) gwp_tp\r\n" + 
//					",sum(nwp_od) nwp_od\r\n" + 
//					",sum(nwp_tp) nwp_tp\r\n" + 
//					",sum(discount_gwp_od) discount_gwp_od\r\n" + 
//					",sum(discount_nwp_od) discount_nwp_od\r\n" + 
//					"from(\r\n" + 
//					"select      \r\n" + 
//					"ins.product_code            \r\n" + 
//					",ins.branch_code             \r\n" + 
//					",ins.oa_code                 \r\n" + 
//					",ins.policy_si               \r\n" + 
//					",ins.migration_flag          \r\n" + 
//					",ins.floter_flag             \r\n" + 
//					",ins.campain_code            \r\n" + 
//					",ins.channel                 \r\n" + 
//					",ins.sub_channel             \r\n" + 
//					",ins.financial_year          \r\n" + 
//					",ins.eff_fin_year_month      \r\n" + 
//					",ins.family_size             \r\n" + 
//					",ins.product_group           \r\n" + 
//					",ins.stp_nstp                \r\n" + 
//					",ins.uw_year                 \r\n" + 
//					",ins.maxage                  \r\n" + 
//					",ins.subline                 \r\n" + 
//					",ins.business_type           \r\n" + 
//					",ins.disease_code            \r\n" + 
//					",ins.totalnumberofyearswithrs        \r\n" + 
//					",ins.numberofyearswithrsinmigrationpolicy    \r\n" + 
//					",ins.modelcode               \r\n" + 
//					",ins.make                    \r\n" + 
//					",KPI_MODEL_MASTER_NW.model                   \r\n" + 
//					",KPI_MODEL_MASTER_NW.modelgroup              \r\n" + 
//					",ins.classofvehicle          \r\n" + 
//					",ins.vehicleage              \r\n" + 
//					",ins.seatingcapacity         \r\n" + 
//					",ins.fueltype               \r\n" + 
//					",ins.fin_date                \r\n" + 
//					",ins.policy_category         \r\n" + 
//					",ins.addon                  \r\n" + 
//					",ins.ins_coverage_type  \r\n" + 
//					",ins.ins_coverage_category\r\n" + 
//					",ins.INS_COVERAGE_PREMIUM gwp\r\n" + 
//					",ins.INS_NWP nwp\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY = 'OD' then INS_COVERAGE_PREMIUM else 0 end) gwp_od\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_COVERAGE_PREMIUM else 0 end) gwp_tp\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY = 'OD' then INS_NWP else 0 end) nwp_od\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_NWP else 0 end) nwp_tp\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_gwp_od\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_nwp_od\r\n" + 
//					"from RSA_KPI_FACT_INS_LATEST ins\r\n" + 
//					"LEFT JOIN KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
//					"ON ins.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
//					"LEFT JOIN KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
//					"ON ins.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
//					"LEFT JOIN KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
//					"ON ins.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
//					"LEFT JOIN KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
//					"ON ins.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
//					"LEFT JOIN KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
//					"ON ins.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
//					"LEFT JOIN KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
//					"ON ins.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
//					"LEFT JOIN KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
//					"ON ins.MAKE = KPI_MODEL_MASTER_NW.MAKE AND ins.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
//					"LEFT JOIN KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
//					"ON ins.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND ins.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
//					"LEFT JOIN KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
//					"ON ins.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
//					"LEFT JOIN KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
//					"ON ins.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY ";

//			String queryStr = "select\r\n" + 
//					"sum(gwp) gwp\r\n" + 
//					",sum(nwp) nwp\r\n" + 
//					",sum(gwp_od) gwp_od\r\n" + 
//					",SUM(gwp_tp) gwp_tp\r\n" + 
//					",sum(nwp_od) nwp_od\r\n" + 
//					",sum(nwp_tp) nwp_tp\r\n" + 
//					",sum(discount_gwp_od) discount_gwp_od\r\n" + 
//					",sum(discount_nwp_od) discount_nwp_od\r\n" + 
//					"from (\r\n" + 
//					"select\r\n" + 
//					"sum(INS_COVERAGE_PREMIUM) gwp\r\n" + 
//					",sum(ins_nwp) nwp\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_COVERAGE_PREMIUM else 0 end) gwp_od\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_COVERAGE_PREMIUM else 0 end) gwp_tp\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_NWP else 0 end) nwp_od\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_NWP else 0 end) nwp_tp\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_gwp_od\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_NWP else 0 end) discount_nwp_od\r\n" + 
//					"from RSA_KPI_FACT_INS_LATEST ins\r\n" + 
//					"LEFT JOIN KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
//					"ON ins.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
//					"LEFT JOIN KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
//					"ON ins.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
//					"LEFT JOIN KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
//					"ON ins.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
//					"LEFT JOIN KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
//					"ON ins.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
//					"LEFT JOIN KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
//					"ON ins.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
//					"LEFT JOIN KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
//					"ON ins.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
//					"LEFT JOIN KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
//					"ON ins.MAKE = KPI_MODEL_MASTER_NW.MAKE AND ins.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
//					"LEFT JOIN KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
//					"ON ins.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND ins.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
//					"LEFT JOIN KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
//					"ON ins.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
//					"LEFT JOIN KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
//					"ON ins.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY";

			String queryStr = "select  sum(gwp) gwp  ,sum(nwp) nwp  ,sum(gwp_od) gwp_od  ,SUM(gwp_tp) gwp_tp  ,sum(nwp_od) nwp_od  ,sum(nwp_tp) nwp_tp  ,sum(discount_gwp_od) discount_gwp_od  ,sum(discount_nwp_od) discount_nwp_od  from (  select  sum(INS_COVERAGE_PREMIUM) gwp  ,sum(ins_nwp) nwp  ,sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_COVERAGE_PREMIUM else 0 end) gwp_od  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_COVERAGE_PREMIUM else 0 end) gwp_tp  ,sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_NWP else 0 end) nwp_od  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_NWP else 0 end) nwp_tp  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_gwp_od  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_NWP else 0 end) discount_nwp_od  from  RSA_KPI_FACT_INS_LATEST ins  LEFT JOIN KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER  ON ins.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR  LEFT JOIN KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER  ON ins.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH  LEFT JOIN KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON ins.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON ins.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON ins.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON ins.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON ins.MAKE = KPI_MODEL_MASTER_NW.MAKE AND ins.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE  LEFT JOIN KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON ins.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND ins.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON ins.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON ins.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY ";

//			if (fromYear.equals(toYear)) {
//				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//			} else {
//				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//			}

			queryStr += " where (ins.INCEPTION_DATE between '" + inceptionStartDate + "' AND '" + inceptionEndDate + "') ";

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(ins.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(ins.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(KPI_BRANCH_MASTER.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(RSA_KPI_FACT_INS_LATEST.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_POLICY_TYPE_MASTER.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and ins.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and ins.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and ins.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.VEHICLEAGE <= " + minVal +
//			        			" and ins.VEHICLEAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.SUM_INSURED <= " + minVal +
//			        			" and ins.SUM_INSURED >= "+maxVal;
//					queryStr += " and ins.POLICY_SI <= " + minVal + " and ins.POLICY_SI >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.MAXAGE <= " + minVal +
//			        			" and ins.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and ins.NOOFYEARSWITHRSINMIG >= "+maxVal;
					
//		        	queryStr += " and ins.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY <= " + minVal +
//        			" and ins.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.AGE_BAND <= " + minVal +
//			        			" and ins.AGE_BAND >= "+maxVal;
					
//		        	queryStr += " and ins.VEHICLEAGE <= " + minVal +
//        			" and ins.VEHICLEAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.FAMILY_SIZE <= " + minVal +
//			        			" and ins.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and ins.DISEASE_CODE in (" + vals + ")";
			}

			queryStr += " group by  ins.policy_category    ,ins.product_code  ,ins.oa_code  ,ins.campain_code  ,ins.channel  ,ins.sub_channel  ,ins.financial_year  ,ins.eff_fin_year_month  ,ins.business_type  ,ins.branch_code  ,ins.uw_year  ,ins.modelcode  ,ins.make    ,KPI_MODEL_MASTER_NW.modelgroup)";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				CubeAKpiResponse cubeAKpiResponse = new CubeAKpiResponse();
				cubeAKpiResponse.setGwp(rs.getDouble(1));
				cubeAKpiResponse.setNwp(rs.getDouble(2));
				cubeAKpiResponse.setGwpOd(rs.getDouble(3));
				cubeAKpiResponse.setGwpTp(rs.getDouble(4));
				cubeAKpiResponse.setNwpOd(rs.getDouble(5));
				cubeAKpiResponse.setNwpTp(rs.getDouble(6));
				cubeAKpiResponse.setDiscountGwpOd(rs.getDouble(7));
				cubeAKpiResponse.setDiscountNwpOd(rs.getDouble(8));

				generalKpiResponseList.add(cubeAKpiResponse);

			}

			System.out.println("--------------------------------------------" + generalKpiResponseList.size());
			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return generalKpiResponseList;
	}
	
	//@RequestMapping(value = "/getUwYrPolicyCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CubeAKpiResponse> getUwYrPolicyCubeData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<CubeAKpiResponse> kpiResponseList = new ArrayList<CubeAKpiResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = toYear+"-"+toMonth+"-31";
			
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			
			

			String queryStr = "select\r\n" + "sum(livescovered) livescovered,\r\n"
					+ "sum(writtenPolicies) writtenPolicies,\r\n" + "sum(acq_cost) acq_cost\r\n" + "from(\r\n"
					+ "select\r\n" + "sum(RSA_KPI_FACT_POLICY.LIVESCOVERED) livescovered,\r\n"
					+ "sum(case when ENDORSEMENT_CODE in('00','11','12') then 1 when ENDORSEMENT_CODE in ('02','08') then -1 else 0 end) writtenPolicies,\r\n"
					+ "sum(RSA_KPI_FACT_POLICY.acq_cost) acq_cost\r\n"
					+ " FROM RSDB.RSA_KPI_FACT_POLICY as RSA_KPI_FACT_POLICY\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n"
					+ "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_POLICY.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n"
					+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n"
					+ "LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_POLICY.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n"
					+ "LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n"
					+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_POLICY.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_POLICY.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE ";
			
			queryStr += " where inception_date between '"+inceptionStartDate+"' and '"+inceptionEndDate+"'";

//			if (fromYear.equals(toYear)) {
//				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//			} else {
//				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//			}

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(KPI_FACT_A_NEW.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_POLICY_TYPE_MASTER.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_POLICY.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.VEHICLEAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.VEHICLEAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.SUM_INSURED <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.MAXAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.AGE_BAND <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_POLICY.FAMILY_SIZE <= " + minVal +
//			        			" and RSA_KPI_FACT_POLICY.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and RSA_KPI_FACT_POLICY.DISEASE_CODE in (" + vals + ")";
			}

			queryStr += ")";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				CubeAKpiResponse res = new CubeAKpiResponse();
				res.setWrittenPolicies(rs.getDouble(2));
				res.setLivesCovered(rs.getDouble(1));
				res.setAcqCost(rs.getDouble(3));

				kpiResponseList.add(res);
			}

			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			e.printStackTrace();
		} finally {
			connection.close();
		}

		return kpiResponseList;

	}
	
	//@RequestMapping(value = "/getUwYrInsCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CubeAKpiResponse> getUwYrInsCubeData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<CubeAKpiResponse> generalKpiResponseList = new ArrayList<CubeAKpiResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = toYear+"-"+toMonth+"-31";
			
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";


//			String finYearStart = fromYear + "-" + fromMonth + "-01";
//			String finYearEnd = toYear + "-" + toMonth + "-31";

//			String queryStr = "select\r\n" + 
//					"sum(gwp) gwp\r\n" + 
//					",sum(nwp) nwp\r\n" + 
//					",sum(gwp_od) gwp_od\r\n" + 
//					",SUM(gwp_tp) gwp_tp\r\n" + 
//					",sum(nwp_od) nwp_od\r\n" + 
//					",sum(nwp_tp) nwp_tp\r\n" + 
//					",sum(discount_gwp_od) discount_gwp_od\r\n" + 
//					",sum(discount_nwp_od) discount_nwp_od\r\n" + 
//					"from(\r\n" + 
//					"select\r\n" + 
//					"product_code            \r\n" + 
//					",branch_code             \r\n" + 
//					",oa_code                 \r\n" + 
//					",policy_si               \r\n" + 
//					",migration_flag          \r\n" + 
//					",floter_flag             \r\n" + 
//					",campain_code            \r\n" + 
//					",channel                 \r\n" + 
//					",sub_channel             \r\n" + 
//					",financial_year          \r\n" + 
//					",eff_fin_year_month      \r\n" + 
//					",family_size             \r\n" + 
//					",product_group           \r\n" + 
//					",stp_nstp                \r\n" + 
//					",uw_year                 \r\n" + 
//					",maxage                  \r\n" + 
//					",subline                 \r\n" + 
//					",business_type           \r\n" + 
//					",disease_code            \r\n" + 
//					",totalnumberofyearswithrs        \r\n" + 
//					",numberofyearswithrsinmigrationpolicy    \r\n" + 
//					",modelcode               \r\n" + 
//					",make                    \r\n" + 
//					",model                   \r\n" + 
//					",modelgroup              \r\n" + 
//					",classofvehicle          \r\n" + 
//					",vehicleage              \r\n" + 
//					",seatingcapacity         \r\n" + 
//					",fueltype              \r\n" + 
//					",fin_date                \r\n" + 
//					",policy_category         \r\n" + 
//					",addon                  \r\n" + 
//					",ins_coverage_type  \r\n" + 
//					",ins_coverage_category\r\n" + 
//					",sum(gwp) gwp\r\n" + 
//					",sum(nwp) nwp\r\n" + 
//					",sum(gwp_od) gwp_od\r\n" + 
//					",SUM(gwp_tp) gwp_tp\r\n" + 
//					",sum(nwp_od) nwp_od\r\n" + 
//					",sum(nwp_tp) nwp_tp\r\n" + 
//					",sum(discount_gwp_od) discount_gwp_od\r\n" + 
//					",sum(discount_nwp_od) discount_nwp_od\r\n" + 
//					"from(\r\n" + 
//					"select      \r\n" + 
//					"ins.product_code            \r\n" + 
//					",ins.branch_code             \r\n" + 
//					",ins.oa_code                 \r\n" + 
//					",ins.policy_si               \r\n" + 
//					",ins.migration_flag          \r\n" + 
//					",ins.floter_flag             \r\n" + 
//					",ins.campain_code            \r\n" + 
//					",ins.channel                 \r\n" + 
//					",ins.sub_channel             \r\n" + 
//					",ins.financial_year          \r\n" + 
//					",ins.eff_fin_year_month      \r\n" + 
//					",ins.family_size             \r\n" + 
//					",ins.product_group           \r\n" + 
//					",ins.stp_nstp                \r\n" + 
//					",ins.uw_year                 \r\n" + 
//					",ins.maxage                  \r\n" + 
//					",ins.subline                 \r\n" + 
//					",ins.business_type           \r\n" + 
//					",ins.disease_code            \r\n" + 
//					",ins.totalnumberofyearswithrs        \r\n" + 
//					",ins.numberofyearswithrsinmigrationpolicy    \r\n" + 
//					",ins.modelcode               \r\n" + 
//					",ins.make                    \r\n" + 
//					",KPI_MODEL_MASTER_NW.model                   \r\n" + 
//					",KPI_MODEL_MASTER_NW.modelgroup              \r\n" + 
//					",ins.classofvehicle          \r\n" + 
//					",ins.vehicleage              \r\n" + 
//					",ins.seatingcapacity         \r\n" + 
//					",ins.fueltype               \r\n" + 
//					",ins.fin_date                \r\n" + 
//					",ins.policy_category         \r\n" + 
//					",ins.addon                  \r\n" + 
//					",ins.ins_coverage_type  \r\n" + 
//					",ins.ins_coverage_category\r\n" + 
//					",ins.INS_COVERAGE_PREMIUM gwp\r\n" + 
//					",ins.INS_NWP nwp\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY = 'OD' then INS_COVERAGE_PREMIUM else 0 end) gwp_od\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_COVERAGE_PREMIUM else 0 end) gwp_tp\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY = 'OD' then INS_NWP else 0 end) nwp_od\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_NWP else 0 end) nwp_tp\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_gwp_od\r\n" + 
//					",(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_nwp_od\r\n" + 
//					"from RSA_KPI_FACT_INS_LATEST ins\r\n" + 
//					"LEFT JOIN KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
//					"ON ins.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
//					"LEFT JOIN KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
//					"ON ins.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
//					"LEFT JOIN KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
//					"ON ins.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
//					"LEFT JOIN KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
//					"ON ins.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
//					"LEFT JOIN KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
//					"ON ins.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
//					"LEFT JOIN KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
//					"ON ins.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
//					"LEFT JOIN KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
//					"ON ins.MAKE = KPI_MODEL_MASTER_NW.MAKE AND ins.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
//					"LEFT JOIN KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
//					"ON ins.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND ins.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
//					"LEFT JOIN KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
//					"ON ins.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
//					"LEFT JOIN KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
//					"ON ins.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY ";

//			String queryStr = "select\r\n" + 
//					"sum(gwp) gwp\r\n" + 
//					",sum(nwp) nwp\r\n" + 
//					",sum(gwp_od) gwp_od\r\n" + 
//					",SUM(gwp_tp) gwp_tp\r\n" + 
//					",sum(nwp_od) nwp_od\r\n" + 
//					",sum(nwp_tp) nwp_tp\r\n" + 
//					",sum(discount_gwp_od) discount_gwp_od\r\n" + 
//					",sum(discount_nwp_od) discount_nwp_od\r\n" + 
//					"from (\r\n" + 
//					"select\r\n" + 
//					"sum(INS_COVERAGE_PREMIUM) gwp\r\n" + 
//					",sum(ins_nwp) nwp\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_COVERAGE_PREMIUM else 0 end) gwp_od\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_COVERAGE_PREMIUM else 0 end) gwp_tp\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_NWP else 0 end) nwp_od\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_NWP else 0 end) nwp_tp\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_gwp_od\r\n" + 
//					",sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_NWP else 0 end) discount_nwp_od\r\n" + 
//					"from RSA_KPI_FACT_INS_LATEST ins\r\n" + 
//					"LEFT JOIN KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
//					"ON ins.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
//					"LEFT JOIN KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
//					"ON ins.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
//					"LEFT JOIN KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
//					"ON ins.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
//					"LEFT JOIN KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
//					"ON ins.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
//					"LEFT JOIN KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
//					"ON ins.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
//					"LEFT JOIN KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
//					"ON ins.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
//					"LEFT JOIN KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
//					"ON ins.MAKE = KPI_MODEL_MASTER_NW.MAKE AND ins.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
//					"LEFT JOIN KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
//					"ON ins.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND ins.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
//					"LEFT JOIN KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
//					"ON ins.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
//					"LEFT JOIN KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
//					"ON ins.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY";

			String queryStr = "select  sum(gwp) gwp  ,sum(nwp) nwp  ,sum(gwp_od) gwp_od  ,SUM(gwp_tp) gwp_tp  ,sum(nwp_od) nwp_od  ,sum(nwp_tp) nwp_tp  ,sum(discount_gwp_od) discount_gwp_od  ,sum(discount_nwp_od) discount_nwp_od  from (  select  sum(INS_COVERAGE_PREMIUM) gwp  ,sum(ins_nwp) nwp  ,sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_COVERAGE_PREMIUM else 0 end) gwp_od  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_COVERAGE_PREMIUM else 0 end) gwp_tp  ,sum(case when INS_COVERAGE_CATEGORY = 'OD' then INS_NWP else 0 end) nwp_od  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') then INS_NWP else 0 end) nwp_tp  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_COVERAGE_PREMIUM else 0 end) discount_gwp_od  ,sum(case when INS_COVERAGE_CATEGORY in ('TP','PA') and INS_COVERAGE_TYPE = 'DC' then INS_NWP else 0 end) discount_nwp_od  from  RSA_KPI_FACT_INS_LATEST ins  LEFT JOIN KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER  ON ins.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR  LEFT JOIN KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER  ON ins.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH  LEFT JOIN KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER  ON ins.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE  LEFT JOIN KPI_BRANCH_MASTER as KPI_BRANCH_MASTER  ON ins.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE  LEFT JOIN KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER  ON ins.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE  LEFT JOIN KPI_OA_MASTER_NW as KPI_OA_MASTER_NW  ON ins.OA_CODE = KPI_OA_MASTER_NW.OA_CODE  LEFT JOIN KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW  ON ins.MAKE = KPI_MODEL_MASTER_NW.MAKE AND ins.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE  LEFT JOIN KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW  ON ins.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND ins.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL  LEFT JOIN KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER  ON ins.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE  LEFT JOIN KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW  ON ins.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY ";

//			if (fromYear.equals(toYear)) {
//				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//						+ "' and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//			} else {
//				queryStr += " WHERE (( FINANCIAL_YEAR=" + fromYear + " and EFF_FIN_YEAR_MONTH >= '" + fromMonth
//						+ "' ) or ( FINANCIAL_YEAR=" + toYear + " and EFF_FIN_YEAR_MONTH <='" + toMonth + "' ))";
//			}

			queryStr += " where (ins.INCEPTION_DATE between '" + inceptionStartDate + "' AND '" + inceptionEndDate + "') ";

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(ins.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(ins.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(KPI_BRANCH_MASTER.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ins.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(RSA_KPI_FACT_INS_LATEST.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_POLICY_TYPE_MASTER.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and ins.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and ins.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and ins.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.VEHICLEAGE <= " + minVal +
//			        			" and ins.VEHICLEAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.SUM_INSURED <= " + minVal +
//			        			" and ins.SUM_INSURED >= "+maxVal;
//					queryStr += " and ins.POLICY_SI <= " + minVal + " and ins.POLICY_SI >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.MAXAGE <= " + minVal +
//			        			" and ins.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and ins.NOOFYEARSWITHRSINMIG >= "+maxVal;
					
//		        	queryStr += " and ins.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY <= " + minVal +
//        			" and ins.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.AGE_BAND <= " + minVal +
//			        			" and ins.AGE_BAND >= "+maxVal;
					
//		        	queryStr += " and ins.VEHICLEAGE <= " + minVal +
//        			" and ins.VEHICLEAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and ins.FAMILY_SIZE <= " + minVal +
//			        			" and ins.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and ins.DISEASE_CODE in (" + vals + ")";
			}

			queryStr += " group by  ins.policy_category    ,ins.product_code  ,ins.oa_code  ,ins.campain_code  ,ins.channel  ,ins.sub_channel  ,ins.financial_year  ,ins.eff_fin_year_month  ,ins.business_type  ,ins.branch_code  ,ins.uw_year  ,ins.modelcode  ,ins.make    ,KPI_MODEL_MASTER_NW.modelgroup)";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				CubeAKpiResponse cubeAKpiResponse = new CubeAKpiResponse();
				cubeAKpiResponse.setGwp(rs.getDouble(1));
				cubeAKpiResponse.setNwp(rs.getDouble(2));
				cubeAKpiResponse.setGwpOd(rs.getDouble(3));
				cubeAKpiResponse.setGwpTp(rs.getDouble(4));
				cubeAKpiResponse.setNwpOd(rs.getDouble(5));
				cubeAKpiResponse.setNwpTp(rs.getDouble(6));
				cubeAKpiResponse.setDiscountGwpOd(rs.getDouble(7));
				cubeAKpiResponse.setDiscountNwpOd(rs.getDouble(8));

				generalKpiResponseList.add(cubeAKpiResponse);

			}

			System.out.println("--------------------------------------------" + generalKpiResponseList.size());
			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return generalKpiResponseList;
	}
	
	//@GetMapping("/getUwYrGepCubeData")
	@ResponseBody
	public List<GepCubeResponse> getUWYrGEPCubeData(HttpServletRequest req, UserMatrixMasterRequest filterRequest)
			throws SQLException {
		Connection connection = null;
		List<GepCubeResponse> kpiResponseList = new ArrayList<GepCubeResponse>();
		long startTime = System.currentTimeMillis();
		try {
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = toYear+"-"+toMonth+"-31";

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

//			String fromMonth = fromDate.split("/")[0];
//			String fromYear = fromDate.split("/")[1];
//			String toMonth = toDate.split("/")[0];
//			String toYear = toDate.split("/")[1];

			/*
			 * String queryStr = "select \r\n" + "sum(gep) gep,\r\n" + "sum(nep) nep\r\n" +
			 * "sum(gep_od) gep_od,\r\n" + "sum(nep_od) nep_od,\r\n" +
			 * "sum(discount_gep_od) discount_gep_od,\r\n" +
			 * "sum(discount_nep_od) discount_nep_od,\r\n" + "sum(gep_tp) gep_tp,\r\n" +
			 * "sum(nep_od) nep_tp,\r\n" + "sum(gep)*0.3 burn_cost,\r\n" +
			 * "sum(gep_od)*0.1 ibnr_gic_od,\r\n" + "sum(gep_od)*0.2 nbnr_gic_od,\r\n" +
			 * "sum(nep_od)*0.1 ibnr_nic_od,\r\n" + "sum(nep_od)*0.2 nbnr_nic_od,\r\n" +
			 * "sum(gep)*1.5 ulr_gic_tp,\r\n" + "sum(nep)*1.5 ulr_nic_tp,\r\n" +
			 * "sum(gep_tp)*0.1 ibner_gic_tp,\r\n" + "sum(nep_tp)*0.1 ibner_nic_tp,\r\n" +
			 * "sum(gep)*0.3 xol_cost,\r\n" + "sum(gep_health)*0.1 ibnr_gic_health,\r\n" +
			 * "sum(gep_health)*0.2 nbnr_gic_health,\r\n" +
			 * "sum(nep_health)*0.1 ibnr_nic_health,\r\n" +
			 * "sum(nep_health)*0.2 nbnr_nic_health,\r\n" +
			 * "sum(gep_earned_days) earned_days\r\n" + "sum(gep)*0.3 expenses\r\n" +
			 * "from\r\n" + "(\r\n" + "select \r\n" + "g.agent_code,\r\n" +
			 * "g.product_code,\r\n" + "g.branch_code,\r\n" + "g.oa_code,\r\n" +
			 * "g.policy_si,\r\n" + "g.floter_flag,\r\n" + "g.campain_code,\r\n" +
			 * "g.channel,\r\n" + "g.sub_channel,\r\n" + "g.financial_year,\r\n" +
			 * "g.eff_fin_year_month,\r\n" + "g.family_size,\r\n" + "g.product_group,\r\n" +
			 * "g.stp_nstp,\r\n" + "g.previous_si,\r\n" + "g.uw_year,\r\n" + "g.maxage,\r\n"
			 * + "g.subline,\r\n" + "g.business_type,\r\n" + "g.product_type,\r\n" +
			 * "g.disease_code,\r\n" + "g.totalnumberofyearswithrs,\r\n" +
			 * "g.numberofyearswithrsinmigrationpolicy,\r\n" + "g.migration_flag,\r\n" +
			 * "g.modelcode,\r\n" + "g.make,\r\n" + "g.model,\r\n" + "g.modelgroup,\r\n" +
			 * "g.classofvehicle,\r\n" + "g.vehicleage,\r\n" + "g.seatingcapacity,\r\n" +
			 * "g.fueltype,\r\n" + "g.gep_month,\r\n" + "g.gep_year,\r\n" +
			 * "g.gep_earned_days,\r\n" + "g.gep_gepcoverage gep,\r\n" +
			 * "g.gep_nepcoverage nep\r\n" +
			 * ",(case when gep_coveragecategory='OD' then gep_gepcoverage else 0 end) gep_od,\r\n"
			 * +
			 * "(case when gep_coveragecategory='OD' then gep_nepcoverage else 0 end) nep_od,\r\n"
			 * +
			 * "(case when gep_coveragetype='DC' and gep_coveragecategory='od' then gep_gepcoverage else 0 end ) discount_gep_od,\r\n"
			 * +
			 * "(case when gep_coveragetype='DC' and gep_coveragecategory='od' then gep_nepcoverage else 0 end ) discount_nep_od,\r\n"
			 * +
			 * "(case when  gep_coveragecategory in ('TP','PA') then gep_gepcoverage else 0 end ) gep_tp,\r\n"
			 * +
			 * "(case when  gep_coveragecategory in ('TP','PA') then gep_nepcoverage else 0 end ) nep_tp,\r\n"
			 * +
			 * "(case when product_group like '%Health%' then gep_gepcoverage else 0 end ) gep_health,\r\n"
			 * +
			 * "(case when product_group like '%Health%' then gep_nepcoverage else 0 end ) nep_health\r\n"
			 * + "from  rsa_kpi_fact_gep as g\r\n" + "left join kpi_fin_year_master \r\n" +
			 * "on g.financial_year = kpi_fin_year_master.fin_year\r\n" +
			 * "left join kpi_policy_type_master \r\n" +
			 * "on g.floter_flag = kpi_policy_type_master.policy_type\r\n" +
			 * "left join kpi_product_master \r\n" +
			 * "on g.product_code = kpi_product_master.product_code\r\n" +
			 * "left join kpi_branch_master \r\n" +
			 * "on g.branch_code = kpi_branch_master.branch_code\r\n" +
			 * "left join kpi_campaign_master \r\n" +
			 * "on g.campain_code = kpi_campaign_master.campaign_code\r\n" +
			 * "left join kpi_oa_master_nw \r\n" +
			 * "on g.oa_code = kpi_oa_master_nw.oa_code\r\n" +
			 * "left join kpi_model_master_nw \r\n" +
			 * "on g.make = kpi_model_master_nw.make and g.modelcode = kpi_model_master_nw.model_code\r\n"
			 * + "left join kpi_sub_channel_master_nw \r\n" +
			 * "on g.channel = kpi_sub_channel_master_nw.channel_name and g.sub_channel = kpi_sub_channel_master_nw.sub_channel\r\n"
			 * + "left join kpi_business_type_master \r\n" +
			 * "on g.business_type = kpi_business_type_master.business_type\r\n";
			 */

			String queryStr = "select \r\n" + "sum(gep) gep,\r\n" + "sum(nep) nep,\r\n" + "sum(gep_od) gep_od,\r\n"
					+ "sum(nep_od) nep_od,\r\n" + "sum(discount_gep_od) discount_gep_od,\r\n"
					+ "sum(discount_nep_od) discount_nep_od,\r\n" + "sum(gep_tp) gep_tp,\r\n"
					+ "sum(nep_od) nep_tp,\r\n" + "sum(gep)*0.3 burn_cost,\r\n" + "sum(gep_od)*0.1 ibnr_gic_od,\r\n"
					+ "sum(gep_od)*0.2 nbnr_gic_od,\r\n" + "sum(nep_od)*0.1 ibnr_nic_od,\r\n"
					+ "sum(nep_od)*0.2 nbnr_nic_od,\r\n" + "sum(gep_tp)*0.1 ibnr_gic_tp,\r\n"
					+ "sum(gep_tp)*0.2 nbnr_gic_tp,\r\n" + "sum(nep_tp)*0.1 ibnr_nic_tp,\r\n"
					+ "sum(nep_tp)*0.2 nbnr_nic_tp,\r\n" + "sum(gep_health)*0.1 ibnr_gic_health,\r\n"
					+ "sum(gep_health)*0.2 nbnr_gic_health,\r\n" + "sum(nep_health)*0.1 ibnr_nic_health,\r\n"
					+ "sum(nep_health)*0.2 nbnr_nic_health,\r\n" + "sum(gep)*1.5 ulr_gic_tp,\r\n"
					+ "sum(nep)*1.5 ulr_nic_tp,\r\n" + "sum(gep_tp)*0.1 ibner_gic_tp,\r\n"
					+ "sum(nep_tp)*0.1 ibner_nic_tp,\r\n" + "sum(gep)*0.2 xol_cost,\r\n" + "sum(gep)*0.3 expenses,\r\n"
					+ "sum(EARNED_DAYS) earned_days\r\n"
					+ "from\r\n" + "(\r\n" + "select \r\n" + "sum(g.gep_gepcoverage) gep,\r\n"
					+ "sum(g.gep_nepcoverage) nep,\r\n"
					+ "sum(g.GEP_EARNED_DAYS) earned_days,\r\n"
					+ "sum(case when gep_coveragecategory='OD' then gep_gepcoverage else 0 end) gep_od,\r\n"
					+ "sum(case when gep_coveragecategory='OD' then gep_nepcoverage else 0 end) nep_od,\r\n"
					+ "sum(case when gep_coveragetype='DC' and gep_coveragecategory='OD' then gep_gepcoverage else 0 end ) discount_gep_od,\r\n"
					+ "sum(case when gep_coveragetype='DC' and gep_coveragecategory='OD' then gep_nepcoverage else 0 end ) discount_nep_od,\r\n"
					+ "sum(case when gep_coveragecategory in ('TP','PA') then gep_gepcoverage else 0 end ) gep_tp,\r\n"
					+ "sum(case when gep_coveragecategory in ('TP','PA') then gep_nepcoverage else 0 end ) nep_tp,\r\n"
					+ "sum(case when product_group like '%Health%' then gep_gepcoverage else 0 end ) gep_health,\r\n"
					+ "sum(case when product_group like '%Health%' then gep_nepcoverage else 0 end )   nep_health\r\n"
					+ "from  rsa_kpi_fact_gep_latest as g\r\n" + "left join kpi_fin_year_master \r\n"
					+ "on g.financial_year = kpi_fin_year_master.fin_year\r\n" + "left join kpi_policy_type_master \r\n"
					+ "on g.floter_flag = kpi_policy_type_master.policy_type\r\n" + "left join kpi_product_master \r\n"
					+ "on g.product_code = kpi_product_master.product_code\r\n" + "left join kpi_branch_master \r\n"
					+ "on g.branch_code = kpi_branch_master.branch_code\r\n" + "left join kpi_campaign_master \r\n"
					+ "on g.campain_code = kpi_campaign_master.campaign_code\r\n" + "left join kpi_oa_master_nw \r\n"
					+ "on g.oa_code = kpi_oa_master_nw.oa_code\r\n" + "left join kpi_model_master_nw \r\n"
					+ "on g.make = kpi_model_master_nw.make and g.modelcode = kpi_model_master_nw.model_code\r\n"
					+ "left join kpi_sub_channel_master_nw \r\n"
					+ "on g.channel = kpi_sub_channel_master_nw.channel_name and g.sub_channel = kpi_sub_channel_master_nw.sub_channel\r\n"
					+ "left join kpi_business_type_master \r\n"
					+ "on g.business_type = kpi_business_type_master.business_type ";

//			if (fromYear.equals(toYear)) {
//				if (fromMonth.equals(toMonth)) {
//					queryStr += " where ( gep_year= " + fromYear + " and gep_month = " + fromMonth + " )";
//				} else {
//					queryStr += " WHERE (( gep_year=" + fromYear + " and gep_month >= " + fromMonth
//							+ " and gep_month <=" + toMonth + " ))";
//				}
//			} else {
//				queryStr += " WHERE (( gep_year=" + fromYear + " and gep_month >= " + fromMonth + " ) or ( gep_year="
//						+ toYear + " and gep_month <=" + toMonth + " ))";
//			}
			
			queryStr += " where INCEPTION_DATE between '"+inceptionStartDate+"' and '"+inceptionEndDate+"' ";
			
			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(g.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(g.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(g.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(g.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(KPI_FACT_A.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(g.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and g.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and g.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and g.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.VEHICLE_AGE <= " + minVal +
//			        			" and g.VEHICLE_AGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.SUM_INSURED <= " + minVal +
//			        			" and g.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.MAXAGE <= " + minVal +
//			        			" and g.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and g.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.AGE_BAND <= " + minVal +
//			        			" and g.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and g.FAMILY_SIZE <= " + minVal +
//			        			" and g.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and g.DISEASE in (" + vals + ")";
			}

			queryStr += " group by\r\n" + "g.product_code,\r\n" + "g.branch_code,\r\n" + "g.channel,\r\n"
					+ "g.sub_channel,\r\n" + "g.financial_year,\r\n" + "g.eff_fin_year_month,\r\n"
					+ "g.business_type,\r\n" + "g.modelcode,\r\n" + "g.make\r\n" + ") x  ";
//			queryStr += " ) x";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				GepCubeResponse gepCubeResponse = new GepCubeResponse();
				gepCubeResponse.setGep(rs.getDouble(1));
				gepCubeResponse.setNep(rs.getDouble(2));
				gepCubeResponse.setGepOd(rs.getDouble(3));
				gepCubeResponse.setNepOd(rs.getDouble(4));
				gepCubeResponse.setDiscountGepOd(rs.getDouble(5));
				gepCubeResponse.setDiscountNepOd(rs.getDouble(6));
				gepCubeResponse.setGepTp(rs.getDouble(7));
				gepCubeResponse.setNepTp(rs.getDouble(8));
				gepCubeResponse.setBurnCost(rs.getDouble(9));
				gepCubeResponse.setIbnrGicOd(rs.getDouble(10));
				gepCubeResponse.setNbnrGicOd(rs.getDouble(11));
				gepCubeResponse.setIbnrNicOd(rs.getDouble(12));
				gepCubeResponse.setNbnrNicOd(rs.getDouble(13));

				gepCubeResponse.setIbnrGicTp(rs.getDouble(14));
				gepCubeResponse.setNbnrGicTp(rs.getDouble(15));
				gepCubeResponse.setIbnrNicTp(rs.getDouble(16));
				gepCubeResponse.setNbnrNicTp(rs.getDouble(17));

				gepCubeResponse.setIbnrGicHealth(rs.getDouble(18));
				gepCubeResponse.setNbnrGicHealth(rs.getDouble(19));
				gepCubeResponse.setIbnrNicHealth(rs.getDouble(20));
				gepCubeResponse.setNbnrNicHealth(rs.getDouble(21));
				gepCubeResponse.setUlrGicTp(rs.getDouble(22));
				gepCubeResponse.setUlrNicTp(rs.getDouble(23));

				gepCubeResponse.setIbnerGicTp(rs.getDouble(24));
				gepCubeResponse.setIbnerNicTp(rs.getDouble(25));
				gepCubeResponse.setXolCost(rs.getDouble(26));
				gepCubeResponse.setExpenses(rs.getDouble(27));
				gepCubeResponse.setEarnedDays(rs.getDouble(28));

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
	
	//@RequestMapping(value = "/getUwYrSingleLineCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ClaimsSingleLineCubeResponse> getUWYrSingleLineCubeData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<ClaimsSingleLineCubeResponse> kpiResponseList = new ArrayList<ClaimsSingleLineCubeResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = toYear+"-"+toMonth+"-31";
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

//			String fromMonth = fromDate.split("/")[0];
//			String fromYear = fromDate.split("/")[1];
//			String toMonth = toDate.split("/")[0];
//			String toYear = toDate.split("/")[1];

//			String claimMvmtStartMonth = fromYear + fromMonth;
//			String claimMvmtEndMonth = toYear + toMonth;

			String claimSingleLineBaseQuery = "";
			String claimSingleLineFinYrCon = "";
			String claimSingleLineFiltersCon = "";
			String queryStr = "";

			claimSingleLineBaseQuery = "select\r\n" + 
					"sum(actual_gic_health) actual_gic_health\r\n" + 
					",sum(claims_paid) claims_paid\r\n" + 
					"from(\r\n" + 
					"SELECT\r\n" + 
					"sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_GIC) actual_gic_health\r\n" + 
					",sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_TOTAL_PAID) claims_paid\r\n" + 
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n";

			claimSingleLineFinYrCon += " where INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "'";

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL) in (" + vals
						+ ")";
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
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL) in (" + vals
						+ ")";
			}

			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL) in (" + vals
						+ ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL) in ("
						+ vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE) in (" + vals + ")";
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
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

				String vals = "'VGC','VPC','VMC','VOC'";
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				claimSingleLineFiltersCon += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				claimSingleLineFiltersCon += " and TRIM(KPI_FACT_A.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE) in (" + vals
						+ ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(KPI_FACT_A_UPDATED.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and TRIM(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY) in ("
						+ vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLE_AGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLE_AGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUM_INSURED <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.AGE_BAND <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				claimSingleLineFiltersCon += " and RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.DISEASE in (" + vals + ")";
			}

			String claimSingleLineEnd = " group by\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE\r\n" + 
					") ";

			queryStr = claimSingleLineBaseQuery + claimSingleLineFinYrCon + claimSingleLineFiltersCon
					+ claimSingleLineEnd;

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			ClaimsSingleLineCubeResponse res = new ClaimsSingleLineCubeResponse();
			while (rs.next()) {

//				res.setActualGicOd(rs.getDouble(1));
//				res.setActualGicTp(rs.getDouble(2));
				res.setActualGicHealth(rs.getDouble(1));
				res.setPaid(rs.getDouble(2));

			}

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			String claimsSingleLineReputiatedClaimsBase = "select\r\n" + 
					"count(csl_claim_no)\r\n" + 
					"from(\r\n" + 
					"select\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.csl_claim_no\r\n" + 
					",SUM(csl_closing_os_total_org) as close_tot,\r\n" + 
					"SUM(csl_LOSS_PAID_CUM) as loss \r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"where\r\n" + 
					"(CSL_MVMT_MONTH,CSL_CLAIM_NO) in  (select max(CSL_MVMT_MONTH),CSL_CLAIM_NO \r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE "
					+ " where INCEPTION_DATE between '" + inceptionStartDate + "' AND '" + inceptionEndDate
					+ "' ";

					String claimsReputiatedEnd = " group by CSL_CLAIM_NO) GROUP BY \r\n" + 
							"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.csl_claim_no\r\n" + 
							",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE,RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE\r\n" + 
							")y\r\n" + 
							"where  close_tot=0 and loss=0  ";

			queryStr = claimsSingleLineReputiatedClaimsBase + claimSingleLineFiltersCon + claimsReputiatedEnd;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setReputiatedClaims(rs.getDouble(1));
				

			}

			queryStr = "select sum(CSL_OPENING_TOTAL_ORG) from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST "
					+ "where (csl_claim_no,csl_mvmt_month) in "
					+ "( SELECT csl_claim_no,max(csl_mvmt_month) FROM RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n"
					+ "where INCEPTION_DATE between '" + inceptionStartDate + "' AND '" + inceptionEndDate + "'\r\n"
					+ "group by csl_claim_no);";

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setOpeningOsClaims(rs.getDouble(1));

			}

			queryStr = "select sum(CSL_CLOSING_OS_TOTAL_ORG) from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST"
					+ " where (csl_claim_no,csl_mvmt_month) in"
					+ " ( SELECT csl_claim_no,min(csl_mvmt_month) FROM RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST\r\n"
					+ "where INCEPTION_DATE between '" + inceptionStartDate + "' AND '" + inceptionEndDate + "' "
					+ "group by csl_claim_no);";

			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setClosingOsClaim(rs.getDouble(1));

			}
			
			//actual gic od
			queryStr = "select\r\n" + 
					"sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC)\r\n" + 
					"from(\r\n" + 
					"SELECT\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_INCEPTION_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_SI as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_SI\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MIGRATION_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FLOTER_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FINANCIAL_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_EFF_FIN_YEAR_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FAMILY_SIZE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_GROUP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_GROUP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.STP_NSTP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_STP_NSTP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_UW_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAXAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUBLINE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_BUSINESS_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.DISEASE_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_DISEASE_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_TOTALNUMBEROFYEARSWITHRS\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_NUMBEROFYEARSWITHRSINMIGRATIONPOLICY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CLASSOFVEHICLE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CLASSOFVEHICLE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLEAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_VEHICLEAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SEATINGCAPACITY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SEATINGCAPACITY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FUELTYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FUELTYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGSTATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGSTATE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGZONE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGZONE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGLOCATION as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGLOCATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FIN_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FIN_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_CATEGORY\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHECODE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHECODE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHICTYPE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHICTYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MAX_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MAX_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MIN_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MIN_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MVMT_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MVMT_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REGISTRATION_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REGISTRATION_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE_DESC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE_DESC\r\n" + 
					",KPI_FIN_YEAR_MASTER.FIN_YEAR as KPI_FIN_YEAR_MASTER_FIN_YEAR\r\n" + 
					",KPI_FIN_MONTH_MASTER.FIN_MONTH as KPI_FIN_MONTH_MASTER_FIN_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUB_CHANNEL\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME as KPI_SUB_CHANNEL_MASTER_NW_CHANNEL_NAME\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL as KPI_SUB_CHANNEL_MASTER_NW_SUB_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_CODE as KPI_PRODUCT_MASTER_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_DESCRIPTION as KPI_PRODUCT_MASTER_PRODUCT_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CAMPAIN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE as KPI_CAMPAIGN_MASTER_CAMPAIGN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_DESCRIPTION as KPI_CAMPAIGN_MASTER_CAMPAIGN_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAKE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MODELCODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL_CODE as KPI_MODEL_MASTER_NW_MODEL_CODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MAKE as KPI_MODEL_MASTER_NW_MAKE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL as KPI_MODEL_MASTER_NW_MODEL\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELGROUP as KPI_MODEL_MASTER_NW_MODELGROUP\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELCLASSIFICATION as KPI_MODEL_MASTER_NW_MODELCLASSIFICATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_CODE as KPI_OA_MASTER_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_NAME as KPI_OA_MASTER_OA_NAME\r\n" + 
					",KPI_SUBLINE_MASTER.SUBLINE as KPI_SUBLINE_MASTER_SUBLINE\r\n" + 
					",KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE as KPI_BUSINESS_TYPE_MASTER_BUSINESS_TYPE\r\n" + 
					",KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY as KPI_POLICY_CATEGORY_MASTER_POLICY_CATEGORY\r\n" + 
					",KPI_POLICY_TYPE_MASTER.POLICY_TYPE as KPI_POLICY_TYPE_MASTER_POLICY_TYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_EXPENSE_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID_CUM as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID_CUM\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_EXPENSE_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_COUNT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_COUNT\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_GIC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_TOTAL_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_TOTAL_PAID\r\n" + 
					"\r\n" + 
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST \r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					"where RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO not like 'TP%' \r\n";
			
			queryStr += " and ( INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "') " +claimSingleLineFiltersCon+" )x 	";
			
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				res.setActualGicOd(rs.getDouble(1));

			}
			
			//actual gic tp
			queryStr = "select\r\n" + 
					"sum(RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC)\r\n" + 
					"from(\r\n" + 
					"SELECT\r\n" + 
					"RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.INCEPTION_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_INCEPTION_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_SI as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_SI\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MIGRATION_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MIGRATION_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FLOTER_FLAG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FINANCIAL_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_EFF_FIN_YEAR_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FAMILY_SIZE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FAMILY_SIZE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_GROUP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_GROUP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.STP_NSTP as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_STP_NSTP\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.UW_YEAR as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_UW_YEAR\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAXAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAXAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUBLINE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_BUSINESS_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.DISEASE_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_DISEASE_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.TOTALNUMBEROFYEARSWITHRS as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_TOTALNUMBEROFYEARSWITHRS\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_NUMBEROFYEARSWITHRSINMIGRATIONPOLICY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CLASSOFVEHICLE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CLASSOFVEHICLE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.VEHICLEAGE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_VEHICLEAGE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SEATINGCAPACITY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SEATINGCAPACITY\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FUELTYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FUELTYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGSTATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGSTATE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGZONE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGZONE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.REGLOCATION as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_REGLOCATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FIN_DATE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_FIN_DATE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_CATEGORY\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHECODE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHECODE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CATASTROPHICTYPE as //RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CATASTROPHICTYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_TYPE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLAIM_TYPE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MAX_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MAX_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MIN_MVMT_NO as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MIN_MVMT_NO\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_MVMT_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_MVMT_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REGISTRATION_MONTH as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REGISTRATION_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_REPUDIATED_CODE_DESC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_REPUDIATED_CODE_DESC\r\n" + 
					",KPI_FIN_YEAR_MASTER.FIN_YEAR as KPI_FIN_YEAR_MASTER_FIN_YEAR\r\n" + 
					",KPI_FIN_MONTH_MASTER.FIN_MONTH as KPI_FIN_MONTH_MASTER_FIN_MONTH\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_SUB_CHANNEL\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME as KPI_SUB_CHANNEL_MASTER_NW_CHANNEL_NAME\r\n" + 
					",KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL as KPI_SUB_CHANNEL_MASTER_NW_SUB_CHANNEL\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_CODE as KPI_PRODUCT_MASTER_PRODUCT_CODE\r\n" + 
					",KPI_PRODUCT_MASTER.PRODUCT_DESCRIPTION as KPI_PRODUCT_MASTER_PRODUCT_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CAMPAIN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE as KPI_CAMPAIGN_MASTER_CAMPAIGN_CODE\r\n" + 
					",KPI_CAMPAIGN_MASTER.CAMPAIGN_DESCRIPTION as KPI_CAMPAIGN_MASTER_CAMPAIGN_DESCRIPTION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MAKE\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_MODELCODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL_CODE as KPI_MODEL_MASTER_NW_MODEL_CODE\r\n" + 
					",KPI_MODEL_MASTER_NW.MAKE as KPI_MODEL_MASTER_NW_MAKE\r\n" + 
					",KPI_MODEL_MASTER_NW.MODEL as KPI_MODEL_MASTER_NW_MODEL\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELGROUP as KPI_MODEL_MASTER_NW_MODELGROUP\r\n" + 
					",KPI_MODEL_MASTER_NW.MODELCLASSIFICATION as KPI_MODEL_MASTER_NW_MODELCLASSIFICATION\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_CODE as KPI_OA_MASTER_OA_CODE\r\n" + 
					",KPI_OA_MASTER_NW.OA_NAME as KPI_OA_MASTER_OA_NAME\r\n" + 
					",KPI_SUBLINE_MASTER.SUBLINE as KPI_SUBLINE_MASTER_SUBLINE\r\n" + 
					",KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE as KPI_BUSINESS_TYPE_MASTER_BUSINESS_TYPE\r\n" + 
					",KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY as KPI_POLICY_CATEGORY_MASTER_POLICY_CATEGORY\r\n" + 
					",KPI_POLICY_TYPE_MASTER.POLICY_TYPE as KPI_POLICY_TYPE_MASTER_POLICY_TYPE\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_EXPENSE_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLOSING_OS_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_CLOSING_OS_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_LOSS_PAID_CUM as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_LOSS_PAID_CUM\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_CHARGES_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_CHARGES_ORG\r\n" + 
					//",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_EXPENSE_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_EXPENSE_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_OS_LOSS_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_OS_LOSS_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_OPENING_TOTAL_ORG as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_OPENING_TOTAL_ORG\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_COUNT as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_POLICY_COUNT\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_GIC as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_GIC\r\n" + 
					",RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_TOTAL_PAID as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST_CSL_TOTAL_PAID\r\n" + 
					"\r\n" + 
					" FROM RSDB.RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST as RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST \r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					"where RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST.CSL_CLAIM_NO like 'TP%' \r\n";
			
			queryStr += " and ( INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "') " +claimSingleLineFiltersCon+" )x 	";
			
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				res.setActualGicTp(rs.getDouble(1));
			}
			
			queryStr = "select sum(NIC),sum(nic_tp),sum(nic_od) FROM(\r\n" + 
					"select A.csl_gic,A.CSL_CLAIM_NO,A.CSL_MVMT_MONTH,B.OBLIGATORY,B.QUOTA_SHARE,B.RETENTION,B.RI_COMMISSION, csl_gic*(1-QUOTA_SHARE-OBLIGATORY) NIC, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end nic_tp, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' then 0 else csl_gic*(1-QUOTA_SHARE-OBLIGATORY) end nic_od from (\r\n" + 
					"select (case when p.policy_no like '%100' and p.product_code='IHP' then 'R0'\r\n" + 
					"WHEN p.policy_no like '%101' and p.product_code='IHP' then 'R1'\r\n" + 
					"WHEN p.policy_no like '%102' and p.product_code='IHP' then 'R2'\r\n" + 
					"WHEN p.policy_no like '%103' and p.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END) BAND, uw_year,p.PRODUCT_CODE, sum(csl_gic) csl_gic,CSL_CLAIM_NO,CSL_MVMT_MONTH\r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST p\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON p.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON p.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON p.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON p.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON p.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND p.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON p.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON p.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON p.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON p.MAKE = KPI_MODEL_MASTER_NW.MAKE AND p.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON p.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON p.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON p.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					" where\r\n" + 
					"(p.policy_no like '%101' or policy_no like '%102' or policy_no like '%103' or policy_no like '%100') and p.product_code='IHP'" + 
					" and ( INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "') " +claimSingleLineFiltersCon+"	"+
					" GROUP by\r\n" + 
					"(case when p.policy_no like '%100' and p.product_code='IHP' then 'R0'\r\n" + 
					"WHEN p.policy_no like '%101' and p.product_code='IHP' then 'R1'\r\n" + 
					"WHEN p.policy_no like '%102' and p.product_code='IHP' then 'R2'\r\n" + 
					"WHEN p.policy_no like '%103' and p.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END), uw_year,PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH ) A ,\r\n" + 
					"(select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1\r\n" + 
					" group by underwriting_year,XGEN_PRODUCTCODE,band) B\r\n" + 
					"where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE AND A.BAND=B.band \r\n" + 
					"\r\n" + 
					"union \r\n" + 
					"select A.csl_gic,A.CSL_CLAIM_NO,A.CSL_MVMT_MONTH,B.OBLIGATORY,B.QUOTA_SHARE,B.RETENTION,B.RI_COMMISSION, csl_gic*(1-QUOTA_SHARE-OBLIGATORY) NIC, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' THEN csl_gic*(1-QUOTA_SHARE-OBLIGATORY) ELSE 0 end nic_tp, CASE WHEN CSL_CLAIM_NO LIKE 'TP%' then 0 else csl_gic*(1-QUOTA_SHARE-OBLIGATORY) end nic_od from (\r\n" + 
					"select (case when p.policy_no like '%100' and p.product_code='IHP' then 'R0'\r\n" + 
					"WHEN p.policy_no like '%101' and p.product_code='IHP' then 'R1'\r\n" + 
					"WHEN p.policy_no like '%102' and p.product_code='IHP' then 'R2'\r\n" + 
					"WHEN p.policy_no like '%103' and p.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END) BAND, uw_year,p.PRODUCT_CODE, sum(csl_gic) csl_gic,CSL_CLAIM_NO,CSL_MVMT_MONTH\r\n" + 
					"from RSA_KPI_FACT_CLAIMS_SINGLE_LINE_LATEST p\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n" + 
					"ON p.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n" + 
					"LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n" + 
					"ON p.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n" + 
					"LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n" + 
					"ON p.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n" + 
					"ON p.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n" + 
					"ON p.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND p.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n" + 
					"ON p.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n" + 
					"LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n" + 
					"ON p.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n" + 
					"LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n" + 
					"ON p.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n" + 
					"ON p.MAKE = KPI_MODEL_MASTER_NW.MAKE AND p.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n" + 
					"ON p.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n" + 
					"LEFT JOIN RSDB.KPI_SUBLINE_MASTER as KPI_SUBLINE_MASTER\r\n" + 
					"ON p.SUBLINE = KPI_SUBLINE_MASTER.SUBLINE\r\n" + 
					"LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n" + 
					"ON p.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n" + 
					" where\r\n" + 
					"(p.policy_no not like '%101' and policy_no not like '%102' and policy_no not like '%103' and policy_no not like '%100' and p.product_code='IHP') or p.product_code<> 'IHP'\r\n" + 
					" and ( INCEPTION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "') " +claimSingleLineFiltersCon+"	"+
					"  GROUP by\r\n" + 
					"(case when p.policy_no like '%100' and p.product_code='IHP' then 'R0'\r\n" + 
					"WHEN p.policy_no like '%101' and p.product_code='IHP' then 'R1'\r\n" + 
					"WHEN p.policy_no like '%102' and p.product_code='IHP' then 'R2'\r\n" + 
					"WHEN p.policy_no like '%103' and p.product_code='IHP' then 'R3 and above'\r\n" + 
					"else 'NONE' END), uw_year,p.PRODUCT_CODE,CSL_CLAIM_NO,CSL_MVMT_MONTH ) A ,\r\n" + 
					"(select underwriting_year,XGEN_PRODUCTCODE,band,sum(OBLIGATORY) OBLIGATORY,sum(QUOTA_SHARE) QUOTA_SHARE,sum(RETENTION) RETENTION,sum(RI_COMMISSION) RI_COMMISSION from RSA_DWH_RI_OBLIGATORY_MASTER1 \r\n" + 
					"group by underwriting_year,XGEN_PRODUCTCODE,band) B\r\n" + 
					"where B.underwriting_year=A.uw_year AND A.PRODUCT_CODE=B.XGEN_PRODUCTCODE\r\n" + 
					"\r\n" + 
					")";
			
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				res.setNic(rs.getDouble(1));
				res.setActualNicTp(rs.getDouble(2));
				res.setActualNicOd(rs.getDouble(3));
				res.setActualNicHealth(rs.getDouble(1));
			}

			kpiResponseList.add(res);

		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return kpiResponseList;
	}
	
	//@RequestMapping(value = "/getUwYrClaimsCubeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ClaimsCubeResponse> getUwYrClaimsCubeData(HttpServletRequest req,
			UserMatrixMasterRequest filterRequest) throws SQLException {
		List<ClaimsCubeResponse> kpiResponseList = new ArrayList<ClaimsCubeResponse>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		Connection connection = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Started query execution");
		try {
			String fromDate = filterRequest.getFromDate() == null ? "" : filterRequest.getFromDate();
			String toDate = filterRequest.getToDate() == null ? "" : filterRequest.getToDate();
			String fromMonth = fromDate.split("/")[0];
			String fromYear = fromDate.split("/")[1];
			String toMonth = toDate.split("/")[0];
			String toYear = toDate.split("/")[1];
			String inceptionStartDate = fromYear+"-"+fromMonth+"-01";
			String inceptionEndDate = toYear+"-"+toMonth+"-31";
			
//			String claimMovementStartDate = fromYear + "-" + fromMonth + "-01";
//			String claimMovementEndDate = toYear + "-" + toMonth + "-31";
			
			
			
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();

			List<ProductMaster> productMasters = productMasterRepository.findAll();

			String motorProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("motor")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String healthProductVals = "'" + productMasters.stream()
					.filter(p -> p.getProductType().toLowerCase().contains("health")).map(ProductMaster::getProductCode)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining("','")) + "'";

			String queryStr = " select\r\n" + "sum(registered_claims) registered_claims\r\n" + "from\r\n" + "(\r\n"
					+ "SELECT\r\n" + "RSA_KPI_FACT_CLAIMS_LATEST.EXPIRTY_DATE as RSA_KPI_FACT_CLAIMS_LATEST_EXPIRTY_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_CODE as RSA_KPI_FACT_CLAIMS_LATEST_PRODUCT_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE as RSA_KPI_FACT_CLAIMS_LATEST_BRANCH_CODE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_TYPE_CODE as RSA_KPI_FACT_CLAIMS_LATEST_BUSINESS_TYPE_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.OA_CODE as RSA_KPI_FACT_CLAIMS_LATEST_OA_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_SI as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_SI\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MIGRATION_FLAG as RSA_KPI_FACT_CLAIMS_LATEST_MIGRATION_FLAG\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FLOTER_FLAG as RSA_KPI_FACT_CLAIMS_LATEST_FLOTER_FLAG\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE as RSA_KPI_FACT_CLAIMS_LATEST_CAMPAIN_CODE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_STATUS as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_STATUS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL as RSA_KPI_FACT_CLAIMS_LATEST_CHANNEL\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL as RSA_KPI_FACT_CLAIMS_LATEST_SUB_CHANNEL\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FINANCIAL_YEAR as RSA_KPI_FACT_CLAIMS_LATEST_FINANCIAL_YEAR\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.ENTRY_YEAR as RSA_KPI_FACT_CLAIMS_LATEST_ENTRY_YEAR\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_BAND as RSA_KPI_FACT_CLAIMS_LATEST_BUSINESS_BAND\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.EFF_FIN_YEAR_MONTH as RSA_KPI_FACT_CLAIMS_LATEST_EFF_FIN_YEAR_MONTH\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FAMILY_SIZE as RSA_KPI_FACT_CLAIMS_LATEST_FAMILY_SIZE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_GROUP as RSA_KPI_FACT_CLAIMS_LATEST_PRODUCT_GROUP\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.STP_NSTP as RSA_KPI_FACT_CLAIMS_LATEST_STP_NSTP\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PREVIOUS_SI as RSA_KPI_FACT_CLAIMS_LATEST_PREVIOUS_SI\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.UW_YEAR as RSA_KPI_FACT_CLAIMS_LATEST_UW_YEAR\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_SOURCE_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_SOURCE_TYPE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.ENTRY_DATE as RSA_KPI_FACT_CLAIMS_LATEST_ENTRY_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MAXAGE as RSA_KPI_FACT_CLAIMS_LATEST_MAXAGE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.SUBLINE as RSA_KPI_FACT_CLAIMS_LATEST_SUBLINE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_BUSINESS_TYPE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_PRODUCT_TYPE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.DISEASE_CODE as RSA_KPI_FACT_CLAIMS_LATEST_DISEASE_CODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS as RSA_KPI_FACT_CLAIMS_LATEST_TOTALNUMBEROFYEARSWITHRS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.NUMBEROFYEARSWITHRSINMIGRATIONPOLICY as RSA_KPI_FACT_CLAIMS_LATEST_NUMBEROFYEARSWITHRSINMIGRATIONPOLICY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MODELCODE as RSA_KPI_FACT_CLAIMS_LATEST_MODELCODE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MAKE as RSA_KPI_FACT_CLAIMS_LATEST_MAKE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MODEL as RSA_KPI_FACT_CLAIMS_LATEST_MODEL\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.MODELGROUP as RSA_KPI_FACT_CLAIMS_LATEST_MODELGROUP\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLASSOFVEHICLE as RSA_KPI_FACT_CLAIMS_LATEST_CLASSOFVEHICLE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.VEHICLEAGE as RSA_KPI_FACT_CLAIMS_LATEST_VEHICLEAGE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.SEATINGCAPACITY as RSA_KPI_FACT_CLAIMS_LATEST_SEATINGCAPACITY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FUELTYPE as RSA_KPI_FACT_CLAIMS_LATEST_FUELTYPE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.REGSTATE as RSA_KPI_FACT_CLAIMS_LATEST_REGSTATE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.REGZONE as RSA_KPI_FACT_CLAIMS_LATEST_REGZONE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.REGLOCATION as RSA_KPI_FACT_CLAIMS_LATEST_REGLOCATION\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.FIN_DATE as RSA_KPI_FACT_CLAIMS_LATEST_FIN_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.POLICY_CATEGORY as RSA_KPI_FACT_CLAIMS_LATEST_POLICY_CATEGORY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_MOVEMENT_DATE as RSA_KPI_FACT_CLAIMS_LATEST_CLM_MOVEMENT_DATE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_CLAIM_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_CLM_CLAIM_TYPE\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_CLAIM_MVMT_TYPE as RSA_KPI_FACT_CLAIMS_LATEST_CLM_CLAIM_MVMT_TYPE\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_FYEAR as RSA_KPI_FACT_CLAIMS_LATEST_CLM_FYEAR\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_REPUDIATEDCLAIMS as RSA_KPI_FACT_CLAIMS_LATEST_CLM_REPUDIATEDCLAIMS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_IFTHEFTCLAIMS as RSA_KPI_FACT_CLAIMS_LATEST_CLM_IFTHEFTCLAIMS\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_IFFRAUDCLAIMS as RSA_KPI_FACT_CLAIMS_LATEST_CLM_IFFRAUDCLAIMS\r\n"
					//+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_SUMINSURED as RSA_KPI_FACT_CLAIMS_LATEST_CLM_SUMINSURED\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_IFCATOSTROPHIC as RSA_KPI_FACT_CLAIMS_LATEST_CLM_IFCATOSTROPHIC\r\n"
					+ ",KPI_FIN_YEAR_MASTER.FIN_YEAR as KPI_FIN_YEAR_MASTER_FIN_YEAR\r\n"
					+ ",KPI_FIN_MONTH_MASTER.FIN_MONTH as KPI_FIN_MONTH_MASTER_FIN_MONTH\r\n"
					+ ",KPI_PRODUCT_MASTER.PRODUCT_CODE as KPI_PRODUCT_MASTER_PRODUCT_CODE\r\n"
					+ ",KPI_PRODUCT_MASTER.PRODUCT_DESCRIPTION as KPI_PRODUCT_MASTER_PRODUCT_DESCRIPTION\r\n"
					+ ",KPI_POLICY_TYPE_MASTER.POLICY_TYPE as KPI_POLICY_TYPE_MASTER_POLICY_TYPE\r\n"
					+ ",KPI_BRANCH_MASTER.BRANCH_CODE as KPI_BRANCH_MASTER_BRANCH_CODE\r\n"
					+ ",KPI_BRANCH_MASTER.REVISED_BRANCH_NAME as KPI_BRANCH_MASTER_REVISED_BRANCH_NAME\r\n"
					+ ",KPI_BRANCH_MASTER.REGION as KPI_BRANCH_MASTER_REGION\r\n"
					+ ",KPI_BRANCH_MASTER.STATE_NEW as KPI_BRANCH_MASTER_STATE_NEW\r\n"
					+ ",KPI_BRANCH_MASTER.CLUSTER_NAME as KPI_BRANCH_MASTER_CLUSTER_NAME\r\n"
					+ ",KPI_BRANCH_MASTER.SUB_CLUSTER as KPI_BRANCH_MASTER_SUB_CLUSTER\r\n"
					+ ",KPI_BRANCH_MASTER.RA_CITY_FLAG as KPI_BRANCH_MASTER_RA_CITY_FLAG\r\n"
					+ ",KPI_BRANCH_MASTER.RA_DESCRIPTION as KPI_BRANCH_MASTER_RA_DESCRIPTION\r\n"
					+ ",KPI_BRANCH_MASTER.ZONE as KPI_BRANCH_MASTER_ZONE\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODEL_CODE as KPI_MODEL_MASTER_NW_MODEL_CODE\r\n"
					+ ",KPI_MODEL_MASTER_NW.MAKE as KPI_MODEL_MASTER_NW_MAKE\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODEL as KPI_MODEL_MASTER_NW_MODEL\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODELGROUP as KPI_MODEL_MASTER_NW_MODELGROUP\r\n"
					+ ",KPI_MODEL_MASTER_NW.MODELCLASSIFICATION as KPI_MODEL_MASTER_NW_MODELCLASSIFICATION\r\n"
					+ ",KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE as KPI_CAMPAIGN_MASTER_CAMPAIGN_CODE\r\n"
					+ ",KPI_CAMPAIGN_MASTER.CAMPAIGN_DESCRIPTION as KPI_CAMPAIGN_MASTER_CAMPAIGN_DESCRIPTION\r\n"
					+ ",KPI_OA_MASTER_NW.OA_CODE as KPI_OA_MASTER_NW_OA_CODE\r\n"
					+ ",KPI_OA_MASTER_NW.OA_NAME as KPI_OA_MASTER_NW_OA_NAME\r\n"
					+ ",KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME as KPI_SUB_CHANNEL_MASTER_NW_CHANNEL_NAME\r\n"
					+ ",KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL as KPI_SUB_CHANNEL_MASTER_NW_SUB_CHANNEL\r\n"
					+ ",KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE as KPI_BUSINESS_TYPE_MASTER_BUSINESS_TYPE\r\n"
					+ ",KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY as KPI_POLICY_CATEGORY_MASTER_NW_POLICY_CATEGORY\r\n"
					+ ",RSA_KPI_FACT_CLAIMS_LATEST.CLM_CLAIM_NO as RSA_KPI_FACT_CLAIMS_LATEST_CLM_CLAIM_NO,\r\n"
					+ "(case when  CLM_REGISTRATION_DATE between '" + inceptionStartDate + "' AND '"
					+ inceptionEndDate + "' then 1 else 0 end ) registered_claims\r\n"
					+ " FROM RSDB.RSA_KPI_FACT_CLAIMS_LATEST as RSA_KPI_FACT_CLAIMS_LATEST\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_YEAR_MASTER as KPI_FIN_YEAR_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.FINANCIAL_YEAR = KPI_FIN_YEAR_MASTER.FIN_YEAR\r\n"
					+ "LEFT JOIN RSDB.KPI_FIN_MONTH_MASTER as KPI_FIN_MONTH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.EFF_FIN_YEAR_MONTH = KPI_FIN_MONTH_MASTER.FIN_MONTH\r\n"
					+ "LEFT JOIN RSDB.KPI_PRODUCT_MASTER as KPI_PRODUCT_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.PRODUCT_CODE = KPI_PRODUCT_MASTER.PRODUCT_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_TYPE_MASTER as KPI_POLICY_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.FLOTER_FLAG = KPI_POLICY_TYPE_MASTER.POLICY_TYPE\r\n"
					+ "LEFT JOIN RSDB.KPI_BRANCH_MASTER as KPI_BRANCH_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE = KPI_BRANCH_MASTER.BRANCH_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_MODEL_MASTER_NW as KPI_MODEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.MAKE = KPI_MODEL_MASTER_NW.MAKE AND RSA_KPI_FACT_CLAIMS_LATEST.MODELCODE = KPI_MODEL_MASTER_NW.MODEL_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_CAMPAIGN_MASTER as KPI_CAMPAIGN_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE = KPI_CAMPAIGN_MASTER.CAMPAIGN_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_OA_MASTER_NW as KPI_OA_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.OA_CODE = KPI_OA_MASTER_NW.OA_CODE\r\n"
					+ "LEFT JOIN RSDB.KPI_SUB_CHANNEL_MASTER_NW as KPI_SUB_CHANNEL_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.CHANNEL_NAME AND RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL = KPI_SUB_CHANNEL_MASTER_NW.SUB_CHANNEL\r\n"
					+ "LEFT JOIN RSDB.KPI_BUSINESS_TYPE_MASTER as KPI_BUSINESS_TYPE_MASTER\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.BUSINESS_TYPE = KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE\r\n"
					+ "LEFT JOIN RSDB.KPI_POLICY_CATEGORY_MASTER_NW as KPI_POLICY_CATEGORY_MASTER_NW\r\n"
					+ "ON RSA_KPI_FACT_CLAIMS_LATEST.POLICY_CATEGORY = KPI_POLICY_CATEGORY_MASTER_NW.POLICY_CATEGORY\r\n";

			queryStr += " WHERE CLM_REGISTRATION_DATE between '" + inceptionStartDate + "' and '"+ inceptionEndDate + "'";

			if (filterRequest != null && filterRequest.getGeneralChannel() != null
					&& !filterRequest.getGeneralChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL) in (" + vals + ")";
			}
			if (filterRequest != null && filterRequest.getHealthChannel() != null
					&& !filterRequest.getHealthChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralSubChannel() != null
					&& !filterRequest.getGeneralSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralSubChannel().size(); i++) {
					vals += "'" + filterRequest.getGeneralSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getGeneralSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSubChannel() != null
					&& !filterRequest.getHealthSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthSubChannel().size(); i++) {
					vals += "'" + filterRequest.getHealthSubChannel().get(i).trim() + "'";
					if (i != filterRequest.getHealthSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.SUB_CHANNEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralRegion() != null
					&& !filterRequest.getGeneralRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralRegion().size(); i++) {
					vals += "'" + filterRequest.getGeneralRegion().get(i).trim() + "'";
					if (i != filterRequest.getGeneralRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorRegion() != null
					&& !filterRequest.getMotorRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorRegion().size(); i++) {
					vals += "'" + filterRequest.getMotorRegion().get(i).trim() + "'";
					if (i != filterRequest.getMotorRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthRegion() != null
					&& !filterRequest.getHealthRegion().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthRegion().size(); i++) {
					vals += "'" + filterRequest.getHealthRegion().get(i).trim() + "'";
					if (i != filterRequest.getHealthRegion().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.REGION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralState() != null
					&& !filterRequest.getGeneralState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralState().size(); i++) {
					vals += "'" + filterRequest.getGeneralState().get(i).trim() + "'";
					if (i != filterRequest.getGeneralState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthState() != null
					&& !filterRequest.getHealthState().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthState().size(); i++) {
					vals += "'" + filterRequest.getHealthState().get(i).trim() + "'";
					if (i != filterRequest.getHealthState().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.STATE_NEW) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCity() != null
					&& !filterRequest.getGeneralCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCity().size(); i++) {
					vals += "'" + filterRequest.getGeneralCity().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
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

			if (filterRequest != null && filterRequest.getHealthCity() != null
					&& !filterRequest.getHealthCity().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCity().size(); i++) {
					vals += "'" + filterRequest.getHealthCity().get(i).trim() + "'";
					if (i != filterRequest.getHealthCity().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_BRANCH_MASTER.RA_DESCRIPTION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBranch() != null
					&& !filterRequest.getGeneralBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralBranch().size(); i++) {
					vals += "'" + filterRequest.getGeneralBranch().get(i).trim() + "'";
					if (i != filterRequest.getGeneralBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE) in (" + vals + ")";
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
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBranch() != null
					&& !filterRequest.getHealthBranch().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthBranch().size(); i++) {
					vals += "'" + filterRequest.getHealthBranch().get(i).trim() + "'";
					if (i != filterRequest.getHealthBranch().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.BRANCH_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralCampaign() != null
					&& !filterRequest.getGeneralCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralCampaign().size(); i++) {
					vals += "'" + filterRequest.getGeneralCampaign().get(i).trim() + "'";
					if (i != filterRequest.getGeneralCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorCampaign() != null
					&& !filterRequest.getMotorCampaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorCampaign().size(); i++) {
					vals += "'" + filterRequest.getMotorCampaign().get(i).trim() + "'";
					if (i != filterRequest.getMotorCampaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthCamapaign() != null
					&& !filterRequest.getHealthCamapaign().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthCamapaign().size(); i++) {
					vals += "'" + filterRequest.getHealthCamapaign().get(i).trim() + "'";
					if (i != filterRequest.getHealthCamapaign().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.CAMPAIN_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralProduct() != null
					&& !filterRequest.getGeneralProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getGeneralProduct().size(); i++) {
					vals += "'" + filterRequest.getGeneralProduct().get(i).trim() + "'";
					if (i != filterRequest.getGeneralProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("GENERAL")) {

			}

			if (filterRequest != null && filterRequest.getMotorProduct() != null
					&& !filterRequest.getMotorProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorProduct().size(); i++) {
					vals += "'" + filterRequest.getMotorProduct().get(i).trim() + "'";
					if (i != filterRequest.getMotorProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("MOTOR")) {

//				String vals = "'VGC','VPC','VMC','VOC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + motorProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthProduct() != null
					&& !filterRequest.getHealthProduct().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthProduct().size(); i++) {
					vals += "'" + filterRequest.getHealthProduct().get(i).trim() + "'";
					if (i != filterRequest.getHealthProduct().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + vals + ")";
			} else if (filterRequest != null && filterRequest.getReportType().equalsIgnoreCase("HEALTH")) {
//				String vals = "'AME','IHP','APA','AHC','BMG','AMC'";
				queryStr += " and TRIM(KPI_PRODUCT_MASTER.PRODUCT_CODE) in (" + healthProductVals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralBusinessType())) {
				String vals = "'" + filterRequest.getGeneralBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorBusinessType())) {
				String vals = "'" + filterRequest.getMotorBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthBusinessType() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthBusinessType())) {
				String vals = "'" + filterRequest.getHealthBusinessType().trim() + "'";
				queryStr += " and TRIM(KPI_BUSINESS_TYPE_MASTER.BUSINESS_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthSTPNSTP() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSTPNSTP())) {
				String vals = "'" + filterRequest.getHealthSTPNSTP().trim() + "'";
				queryStr += " and TRIM(KPI_FACT_A.STP_NSTP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaCode() != null
					&& !filterRequest.getMotorOaCode().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaCode().size(); i++) {
					vals += "'" + filterRequest.getMotorOaCode().get(i) + "'";
					if (i != filterRequest.getMotorOaCode().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER.OA_CODE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorOaName() != null
					&& !filterRequest.getMotorOaName().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorOaName().size(); i++) {
					vals += "'" + filterRequest.getMotorOaName().get(i) + "'";
					if (i != filterRequest.getMotorOaName().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_OA_MASTER_NW.OA_NAME) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorMake() != null
					&& !filterRequest.getMotorMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorMake().size(); i++) {
					vals += "'" + filterRequest.getMotorMake().get(i).trim() + "'";
					if (i != filterRequest.getMotorMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MAKE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModel() != null
					&& !filterRequest.getMotorModel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModel().size(); i++) {
					vals += "'" + filterRequest.getMotorModel().get(i).trim() + "'";
					if (i != filterRequest.getMotorModel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODEL) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelGroup() != null
					&& !filterRequest.getMotorModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelGroup().size(); i++) {
					vals += "'" + filterRequest.getMotorModelGroup().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELGROUP) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getMotorModelClassification() != null
					&& !filterRequest.getMotorModelClassification().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getMotorModelClassification().size(); i++) {
					vals += "'" + filterRequest.getMotorModelClassification().get(i).trim() + "'";
					if (i != filterRequest.getMotorModelClassification().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(KPI_MODEL_MASTER_NW.MODELCLASSIFICATION) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyType() != null
					&& !filterRequest.getHealthPolicyType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyType().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyType().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.POLICY_TYPE) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getHealthPolicyCategory() != null
					&& !filterRequest.getHealthPolicyCategory().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPolicyCategory().size(); i++) {
					vals += "'" + filterRequest.getHealthPolicyCategory().get(i).trim() + "'";
					if (i != filterRequest.getHealthPolicyCategory().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(RSA_KPI_FACT_CLAIMS_LATEST.POLICY_CATEGORY) in (" + vals + ")";
			}

			if (filterRequest != null && filterRequest.getGeneralNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getGeneralNoOfYearsWithRs())) {
				String vals[] = filterRequest.getGeneralNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getMotorNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorNoOfYearsWithRs())) {
				String vals[] = filterRequest.getMotorNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfYearsWithRs() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfYearsWithRs())) {
				String vals[] = filterRequest.getHealthNoOfYearsWithRs().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.TOTALNUMBEROFYEARSWITHRS >= "+maxVal;
				}
				;

			}

			if (filterRequest != null && filterRequest.getMotorVehicleAge() != null
					&& !StringUtils.isEmpty(filterRequest.getMotorVehicleAge())) {
				String vals[] = filterRequest.getMotorVehicleAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.VEHICLE_AGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.VEHICLE_AGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthSumInsured() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthSumInsured())) {
				String vals[] = filterRequest.getHealthSumInsured().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.SUM_INSURED <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.SUM_INSURED >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthMaxAge() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthMaxAge())) {
				String vals[] = filterRequest.getHealthMaxAge().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.MAXAGE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.MAXAGE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthNoOfMigratedYears() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthNoOfMigratedYears())) {
				String vals[] = filterRequest.getHealthNoOfMigratedYears().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.NOOFYEARSWITHRSINMIG <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.NOOFYEARSWITHRSINMIG >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthAgeBand() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthAgeBand())) {
				String vals[] = filterRequest.getHealthAgeBand().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.AGE_BAND <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.AGE_BAND >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthFamilySize() != null
					&& !StringUtils.isEmpty(filterRequest.getHealthFamilySize())) {
				String vals[] = filterRequest.getHealthFamilySize().trim().split("-");
				if (vals.length >= 2) {
					String minVal = vals[0].trim();
					String maxVal = vals[1].trim();
//			        	queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.FAMILY_SIZE <= " + minVal +
//			        			" and RSA_KPI_FACT_CLAIMS_LATEST.FAMILY_SIZE >= "+maxVal;
				}

			}

			if (filterRequest != null && filterRequest.getHealthPreExistingDisease() != null
					&& !filterRequest.getHealthPreExistingDisease().isEmpty()) {
				String vals = "";
				for (int i = 0; i < filterRequest.getHealthPreExistingDisease().size(); i++) {
					vals += "'" + filterRequest.getHealthPreExistingDisease().get(i) + "'";
					if (i != filterRequest.getHealthPreExistingDisease().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and RSA_KPI_FACT_CLAIMS_LATEST.DISEASE in (" + vals + ")";
			}

			queryStr += ")";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {

				ClaimsCubeResponse res = new ClaimsCubeResponse();
				res.setRegisteredClaims(rs.getDouble(1));

				kpiResponseList.add(res);
			}

			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("kylinDataSource initialize error, ex: " + e);
			System.out.println();
			System.out.println();
			System.out.println();
			e.printStackTrace();
		} finally {
			connection.close();
		}

		System.out.println("CALLED THE METHOD");
		return kpiResponseList;

	}
	
}
