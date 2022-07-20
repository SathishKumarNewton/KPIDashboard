
package com.prodian.rsgirms.dashboard.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.kylin.jdbc.Driver;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.prodian.rsgirms.constants.RMSConstants;
import com.prodian.rsgirms.dashboard.model.ConfigTableName;
import com.prodian.rsgirms.dashboard.model.Dashboard;
import com.prodian.rsgirms.dashboard.model.MultiUserDashboard;
import com.prodian.rsgirms.dashboard.model.SqoopConfigurationRequest;
import com.prodian.rsgirms.dashboard.model.UserDashboard;
import com.prodian.rsgirms.dashboard.response.GwpResponse;
import com.prodian.rsgirms.dashboard.response.GwpResponseObject;
import com.prodian.rsgirms.dashboard.response.PolicyResponse;
import com.prodian.rsgirms.dashboard.response.UserDashboardResponse;
import com.prodian.rsgirms.dashboard.service.impl.DashboardServiceImpl;
import com.prodian.rsgirms.scheduler.model.CubeStatus;
import com.prodian.rsgirms.scheduler.model.Cubes;
import com.prodian.rsgirms.scheduler.model.SchedulerInfo;
import com.prodian.rsgirms.scheduler.model.SqoopJobStatusResponse;
import com.prodian.rsgirms.scheduler.repository.CubeRepository;
import com.prodian.rsgirms.scheduler.repository.SchedulerInfoRepository;
import com.prodian.rsgirms.scheduler.repository.SchedulerStatusRepository;
import com.prodian.rsgirms.scheduler.repository.SqoopJobStatusRepository;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;
import com.prodian.rsgirms.usermatrix.enums.UserMatrixMasterValueEnum;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterRequest;
import com.prodian.rsgirms.usermatrix.service.UserMatrixService;
import com.prodian.rsgirms.util.UtilityFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Controller
public class DashboardController {

	@Autowired
	private UserService userService;

	@Autowired
	private DashboardServiceImpl dashboardService;

	@Autowired
	private UserMatrixService userMatrixService;
	
	@Autowired
	private SchedulerStatusRepository schedulerStatusRepository;
	
	@Autowired
	private SchedulerInfoRepository schedulerInfoRepository;
	
	@Autowired
	private CubeRepository cubeRepository;
	
	@Autowired
	private SqoopJobStatusRepository sqoopJobStatusRepository;
	
	@Autowired
    private Environment env;
	
	private Connection connection = null;

	@PostMapping("/admin/saveUserDashboardMapping")
	public String saveUserSDashBoardMapping(@ModelAttribute MultiUserDashboard multiUserDashboard, Model model) {
		if ((multiUserDashboard.getUserIds() == null && !multiUserDashboard.getUserIds().isEmpty())
				|| (multiUserDashboard.getDashboardIds() == null && !multiUserDashboard.getDashboardIds().isEmpty())) {
			model.addAttribute("error", "Invalid Values");
			return userDashboardMaster(model);
		}
		try {
			dashboardService.saveMultiUserDashBoardMapping(multiUserDashboard);
			model.addAttribute("success", "Saved Successfully");
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Internal Error");
		}
		return userDashboardMaster(model);
	}

	@GetMapping("/admin/olduserDashboardMapping")
	public String olduserDashboardMaster(Model model) {
		List<User> users = userService.getAllUsers();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		List<UserDashboardResponse> userDashboardList = dashboardService.getUserDashboardListResponse(user.getId());
		model.addAttribute("users", users);
		List<Dashboard> dashboardList = dashboardService.getAllDashboards();
		model.addAttribute("dashboardList", dashboardList);
		model.addAttribute("userDashboardList", userDashboardList);
		model.addAttribute("multiUserDashboard", new MultiUserDashboard());
		return "admin/olduserDashboardMapping";
	}
	
	@GetMapping("/admin/userDashboardMapping")
	public String userDashboardMaster(Model model) {
		List<User> users = userService.getAllUsers();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		List<UserDashboardResponse> userDashboardList = dashboardService.getUserDashboardListResponse(user.getId());
		model.addAttribute("users", users);
		List<Dashboard> dashboardList = dashboardService.getAllDashboards();
		model.addAttribute("dashboardList", dashboardList);
		model.addAttribute("userDashboardList", userDashboardList);
		model.addAttribute("multiUserDashboard", new MultiUserDashboard());
		return "admin/userDashboardMapping";
	}

	@GetMapping("/admin/dashboardMaster")
	public ModelAndView dashboardMaster() {
		ModelAndView model = new ModelAndView("admin/dashboardMaster");
		return model;
	}

	@PostMapping("/admin/deleteDashboard")
	public @ResponseBody void deleteDashboard(@RequestBody List<Dashboard> dashboards) {
		dashboardService.deleteDashboard(dashboards);
	}

	@PostMapping("/admin/editDashboard")
	public String editDashboard(@ModelAttribute Dashboard dashboard) {
		dashboardService.saveDashboard(dashboard);
		return "redirect:/admin/home";
	}

	@PostMapping("/admin/addDashboard")
	public String addDashboard(@ModelAttribute Dashboard dashboard) {
		dashboardService.saveDashboard(dashboard);
		return "redirect:/admin/home";
	}

	@GetMapping("/admin/getDashboardById")
	public @ResponseBody Dashboard getDashboardById(@RequestParam Integer id) {
		return dashboardService.getDashboardById(id);
	}

	@PostMapping("/admin/deleteUserDashboardMapping")
	public @ResponseBody void deleteUserDashboardMapping(@RequestBody List<UserDashboard> userDashboards) {
		dashboardService.deleteUserDashboardMapping(userDashboards);
	}

	@GetMapping("/gwp")
	public String showGWP() {
		return "gwp";
	}

	@GetMapping("/gwpPivot")
	public ModelAndView showGWPPivot() {
		ModelAndView model = new ModelAndView("gwppivot");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equalsIgnoreCase("ADMIN"));
		model.addObject("isAdmin", isAdmin);
		return model;
	}

	/*
	 * @GetMapping("/gwppivot") public String showgwpPivot() { return "gwp_Pivot"; }
	 */

	private String getMtd() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-yyyy");
		String strDate = formatter.format(date).toUpperCase();
		return strDate;
	}

	private String getPreMtd() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-yyyy");
		String strDate = formatter.format(cal.getTime()).toUpperCase();
		return strDate;
	}

	private String getYtd() {
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.MONTH) <= 3) {
			return (cal.get(Calendar.YEAR) - 1) + "-" + cal.get(Calendar.YEAR);
		} else {
			return (cal.get(Calendar.YEAR)) + "-" + (cal.get(Calendar.YEAR) + 1);
		}
	}

	private String getPreYtd() {
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.MONTH) <= 3) {
			return (cal.get(Calendar.YEAR) - 2) + "-" + (cal.get(Calendar.YEAR) - 1);
		} else {
			return (cal.get(Calendar.YEAR) - 1) + "-" + (cal.get(Calendar.YEAR));
		}
	}

	@RequestMapping(value = "/getChart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getRouteConfig() throws SQLException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		UserDashboard dashboard = userMatrixService.getDashboardByUserIdAndDashboardName(userId, RMSConstants.GWP_DASHBOARD);
		UserMatrixMasterRequest request = new UserMatrixMasterRequest();
		request.setDashboardId(Arrays.asList(dashboard.getDashboardId()));
		UserMatrixMasterRequest masterRequest = userMatrixService.getUserMatrixChildByUserId(userId, request);
		String maps = "{}";
		long startTime = System.currentTimeMillis();
		JSONArray jsArray = new JSONArray();
		System.out.println("Started query execution");
		try {
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			// connection =
//			 driverManager.connect("jdbc:kylin://13.126.161.86:7070/learn_kylin", info);
			connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
//			connection = driverManager.connect("jdbc:kylin://localhost:7070/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();
			String queryStr = " select  GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUB_CHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE,";
			// ,GWP_23_7_CUBE.STATE_CODE, ";
			queryStr += " SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY, ";
			queryStr += " SUM(PRE_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PRE_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PRE_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PRE_YTD_POLICY_COUNT) LASTYR_YTD_POLICY";
			queryStr += " from ( ";
			queryStr += " SELECT ";
			queryStr += " DS_POLICY_FACT_15_07_NW.ZONE_NAME as ZONE_NAME";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME as CLUSTER_NAME";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.STATE as STATE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE as BRANCH_CODE ";
			queryStr += " ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME as BRANCH_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE as PRODUCT_CODE ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT as PRODUCT ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION as PRODUCT_DESC ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW as LOB ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE as BUSINESS_TYPE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CHANNEL as CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL as SUB_CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MAKE as MAKE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MODEL_CODE as MODEL_CODE ";
			queryStr += " ,DS_MASTER_MODEL_NW.MODEL_NAME as MODEL_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.FIN_YEAR as FACT_FIN_YEAR ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG as FACT_MONTH_FLAG, ";
			queryStr += " SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT, ";
			queryStr += " SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getPreMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  PRE_MTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  PRE_YTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getPreMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) PRE_MTD_POLICY_COUNT ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) PRE_YTD_POLICY_COUNT ";
			queryStr += " FROM RSDB.DS_POLICY_FACT_15_07_NW as DS_POLICY_FACT_15_07_NW ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.ZONE_NAME = DS_MASTER_ZONE_NOW.ZONE_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_CLUSTER_NOW as DS_MASTER_CLUSTER_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME = DS_MASTER_CLUSTER_NOW.CLUSTER_NAME ";// AND
																											// DS_POLICY_FACT_15_07_NW.ZONE_NAME
																											// =
																											// DS_MASTER_CLUSTER_NOW.ZONE_NAME
																											// ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW ";
			// queryStr += " ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME =
			// DS_MASTER_STATE_NOW.CLUSTER_NAME AND DS_POLICY_FACT_15_07_NW.STATE =
			// DS_MASTER_STATE_NOW.STATE ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.STATE = DS_MASTER_STATE_NOW.STATE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_BRANCH_NOW as DS_MASTER_BRANCH_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.BRANCH_CODE = DS_MASTER_BRANCH_NOW.BRANCH_CODE ";// AND
																										// DS_POLICY_FACT_15_07_NW.CLUSTER_NAME
																										// =
																										// DS_MASTER_BRANCH_NOW.CLUSTER_NAME
																										// AND
																										// DS_POLICY_FACT_15_07_NW.STATE
																										// =
																										// DS_MASTER_BRANCH_NOW.STATE_NEW
																										// AND
																										// DS_POLICY_FACT_15_07_NW.ZONE_NAME
																										// =
																										// DS_MASTER_BRANCH_NOW.ZONE
																										// ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND DS_POLICY_FACT_15_07_NW.SUB_CHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_MAKE_NOW as DS_MASTER_MAKE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MAKE = DS_MASTER_MAKE_NOW.MAKE_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_MODEL_NW as DS_MASTER_MODEL_NW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MAKE = DS_MASTER_MODEL_NW.MAKE AND DS_POLICY_FACT_15_07_NW.MODEL_CODE = DS_MASTER_MODEL_NW.MODEL_CODE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_FIN_YEAR as DS_MASTER_FIN_YEAR ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.FIN_YEAR = DS_MASTER_FIN_YEAR.FIN_YEAR ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_FIN_MONTH as DS_MASTER_FIN_MONTH ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MONTH_FLAG = DS_MASTER_FIN_MONTH.ENTRY_MONTH ";
			queryStr += " WHERE 1=1 ";
//			if (userId == 2) {
//				queryStr += " and DS_POLICY_FACT_15_07_NW.ZONE_NAME in ('South Zone')";
//			}
//			if (userId == 3) {
//				queryStr += " and DS_POLICY_FACT_15_07_NW.BRANCH_CODE in ('X1')";
//			}
			if (masterRequest.getZones() != null && !masterRequest.getZones().isEmpty()
					&& !masterRequest.getZones().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getZones().size(); i++) {
					vals += "'" + masterRequest.getZones().get(i) + "'";
					if (i != masterRequest.getZones().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.ZONE_NAME in (" + vals + ")";
			}
			if (masterRequest.getClusters() != null && !masterRequest.getClusters().isEmpty()
					&& !masterRequest.getClusters().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getClusters().size(); i++) {
					vals += "'" + masterRequest.getClusters().get(i) + "'";
					if (i != masterRequest.getClusters().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.CLUSTER_NAME in (" + vals + ")";
			}
			if (masterRequest.getStates() != null && !masterRequest.getStates().isEmpty()
					&& !masterRequest.getStates().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getStates().size(); i++) {
					vals += "'" + masterRequest.getStates().get(i) + "'";
					if (i != masterRequest.getStates().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.STATE in (" + vals + ")";
			}
			if (masterRequest.getBranchCodes() != null && !masterRequest.getBranchCodes().isEmpty()
					&& !masterRequest.getBranchCodes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getBranchCodes().size(); i++) {
					vals += "'" + masterRequest.getBranchCodes().get(i) + "'";
					if (i != masterRequest.getBranchCodes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.BRANCH_CODE in (" + vals + ")";
			}
			if (masterRequest.getProducts() != null && !masterRequest.getProducts().isEmpty()
					&& !masterRequest.getProducts().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getProducts().size(); i++) {
					vals += "'" + masterRequest.getProducts().get(i) + "'";
					if (i != masterRequest.getProducts().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.PRODUCT_CODE in (" + vals + ")";
			}
			if (masterRequest.getBusinessTypes() != null && !masterRequest.getBusinessTypes().isEmpty()
					&& !masterRequest.getBusinessTypes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getBusinessTypes().size(); i++) {
					vals += "'" + masterRequest.getBusinessTypes().get(i) + "'";
					if (i != masterRequest.getBusinessTypes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_PRODUCT_NOW.BUSINESS_TYPE in (" + vals + ")";
			}
			if (masterRequest.getLobs() != null && !masterRequest.getLobs().isEmpty()
					&& !masterRequest.getLobs().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getLobs().size(); i++) {
					vals += "'" + masterRequest.getLobs().get(i) + "'";
					if (i != masterRequest.getLobs().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_PRODUCT_NOW.SEGMENT_NEW in (" + vals + ")";
			}
			if (masterRequest.getChannels() != null && !masterRequest.getChannels().isEmpty()
					&& !masterRequest.getChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getChannels().size(); i++) {
					vals += "'" + masterRequest.getChannels().get(i) + "'";
					if (i != masterRequest.getChannels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.CHANNEL in (" + vals + ")";
			}
			if (masterRequest.getSubChannels() != null && !masterRequest.getSubChannels().isEmpty()
					&& !masterRequest.getSubChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getSubChannels().size(); i++) {
					vals += "'" + masterRequest.getSubChannels().get(i) + "'";
					if (i != masterRequest.getSubChannels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.SUB_CHANNEL in (" + vals + ")";
			}
			if (masterRequest.getMakes() != null && !masterRequest.getMakes().isEmpty()
					&& !masterRequest.getMakes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getMakes().size(); i++) {
					vals += "'" + masterRequest.getMakes().get(i) + "'";
					if (i != masterRequest.getMakes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.MAKE in (" + vals + ")";
			}
			if (masterRequest.getModels() != null && !masterRequest.getModels().isEmpty()
					&& !masterRequest.getModels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getModels().size(); i++) {
					vals += "'" + masterRequest.getModels().get(i) + "'";
					if (i != masterRequest.getModels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_MODEL_NW.MODEL_NAME in (" + vals + ")";
			}
			queryStr += " group by ";
			queryStr += " DS_POLICY_FACT_15_07_NW.ZONE_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.STATE ";
			// queryStr += " ,DS_MASTER_STATE_NOW.STATE_CODE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE ";
			queryStr += " ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MAKE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MODEL_CODE ";
			queryStr += " ,DS_MASTER_MODEL_NW.MODEL_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.FIN_YEAR ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG ";
			queryStr += " ,DS_MASTER_FIN_MONTH.ENTRY_MONTH ";
			queryStr += " ) GWP_23_7_CUBE ";
			queryStr += " GROUP BY ";
			queryStr += " GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUB_CHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE";
			// ,GWP_23_7_CUBE.STATE_CODE ";
			// String queryStr = "select
			// CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,
			// SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE)
			// YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,
			// SUM(PRE_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PRE_YTD_GWP_OUR_SHARE)
			// LASTYR_YTD_GWP,SUM(PRE_MTD_POLICY_COUNT)
			// LASTYR_MTD_POLICY,SUM(PRE_YTD_POLICY_COUNT) LASTYR_YTD_POLICY from ( SELECT
			// NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME ,NEW_POLICY_FACT.CLUSTER_NAME as
			// CLUSTER_NAME ,NEW_POLICY_FACT.STATE as STATE ,NEW_MASTER_STATE.STATE_CODE AS
			// STATE_CODE ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE
			// ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME
			// ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE ,NEW_MASTER_PRODUCT.PRODUCT as
			// PRODUCT ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC
			// ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB ,NEW_POLICY_FACT.BUSINESS_TYPE as
			// BUSINESS_TYPE ,NEW_POLICY_FACT.CHANNEL as CHANNEL
			// ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL ,NEW_POLICY_FACT.MAKE as MAKE
			// ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE ,NEW_MASTER_MODEL.MODEL_NAME as
			// MODEL_NAME ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR
			// ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG, SUM(case when
			// (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR=
			// '2019-2020') then NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end) as
			// MTD_GWP_OUR_SHARE , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then
			// NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end) as YTD_GWP_OUR_SHARE , SUM(case
			// when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR=
			// '2019-2020') then NEW_POLICY_FACT . POLICY_COUNT else 0.0 end)
			// MTD_POLICY_COUNT , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then
			// NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT, SUM(case when
			// (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR=
			// '2018-2019') then NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end) as
			// PRE_MTD_GWP_OUR_SHARE , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019'
			// then NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end) as PRE_YTD_GWP_OUR_SHARE
			// , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and
			// NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then NEW_POLICY_FACT . POLICY_COUNT
			// else 0.0 end) PRE_MTD_POLICY_COUNT , SUM(case when NEW_POLICY_FACT.FIN_YEAR=
			// '2018-2019' then NEW_POLICY_FACT . POLICY_COUNT else 0.0 end)
			// PRE_YTD_POLICY_COUNT FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT INNER JOIN
			// RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE ON NEW_POLICY_FACT.ZONE_NAME =
			// NEW_MASTER_ZONE.ZONE_NAME INNER JOIN RSDB.NEW_MASTER_CLUSTER as
			// NEW_MASTER_CLUSTER ON NEW_POLICY_FACT.CLUSTER_NAME =
			// NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME =
			// NEW_MASTER_CLUSTER.ZONE_NAME INNER JOIN RSDB.NEW_MASTER_STATE as
			// NEW_MASTER_STATE ON NEW_POLICY_FACT.CLUSTER_NAME =
			// NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE =
			// NEW_MASTER_STATE.STATE INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH
			// ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND
			// NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND
			// NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND
			// NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE INNER JOIN
			// RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT ON NEW_POLICY_FACT.PRODUCT_CODE
			// = NEW_MASTER_PRODUCT.PRODUCT_CODE INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as
			// NEW_MASTER_BUSINESS_TYPE ON NEW_POLICY_FACT.BUSINESS_TYPE =
			// NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE INNER JOIN RSDB.NEW_MASTER_CHANNEL as
			// NEW_MASTER_CHANNEL ON NEW_POLICY_FACT.CHANNEL =
			// NEW_MASTER_CHANNEL.CHANNEL_NAME INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as
			// NEW_MASTER_SUB_CHANNEL ON NEW_POLICY_FACT.CHANNEL =
			// NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL =
			// NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL INNER JOIN RSDB.NEW_MASTER_MAKE as
			// NEW_MASTER_MAKE ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME INNER
			// JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL ON NEW_POLICY_FACT.MAKE =
			// NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE =
			// NEW_MASTER_MODEL.MODEL_CODE INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as
			// NEW_MASTER_FIN_YEAR ON NEW_POLICY_FACT.FIN_YEAR =
			// NEW_MASTER_FIN_YEAR.FIN_YEAR INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as
			// NEW_MASTER_FIN_MONTH ON NEW_POLICY_FACT.MONTH_FLAG =
			// NEW_MASTER_FIN_MONTH.ENTRY_MONTH WHERE 1=1 and NEW_MASTER_PRODUCT.SEGMENT_NEW
			// in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by
			// NEW_POLICY_FACT.ZONE_NAME ,NEW_POLICY_FACT.CLUSTER_NAME
			// ,NEW_POLICY_FACT.STATE ,NEW_MASTER_STATE.STATE_CODE
			// ,NEW_POLICY_FACT.BRANCH_CODE ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME
			// ,NEW_POLICY_FACT.PRODUCT_CODE ,NEW_MASTER_PRODUCT.PRODUCT
			// ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION ,NEW_MASTER_PRODUCT.SEGMENT_NEW
			// ,NEW_POLICY_FACT.BUSINESS_TYPE ,NEW_POLICY_FACT.CHANNEL
			// ,NEW_POLICY_FACT.SUB_CHANNEL ,NEW_POLICY_FACT.MAKE
			// ,NEW_POLICY_FACT.MODEL_CODE ,NEW_MASTER_MODEL.MODEL_NAME
			// ,NEW_POLICY_FACT.FIN_YEAR ,NEW_POLICY_FACT.MONTH_FLAG ) CUR_ACT_CUBE GROUP BY
			// CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE
			// ";
			System.out.println("START------------------------------ ");
			System.out.println(queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");
			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			String[] myArray = {};
			ArrayList<String> arrayList = new ArrayList<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				arrayList.add(rsmd.getColumnName(i));
			}
//		        System.out.println(arrayList);
			jsArray.add(arrayList);
			int counter = 0;
			while (rs.next()) {
				// if( counter <400000 ) {
				ArrayList<String> tmpArray = new ArrayList<String>();
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
					tmpArray.add(rs.getString(i));
				}
				// arrayList.addAll(tmpArray);
//		        		System.out.println(jsArray);
				jsArray.add(tmpArray);
				// }
				counter++;
			}

			System.out.println("--------------------------------------------" + counter);
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
//		return jsArray.toJSONString();
		return jsArray.toString();
	}

	/*
	 * public static JSONArray convertToJSON(ResultSet resultSet) throws Exception {
	 * JSONArray jsonArray = new JSONArray(); while (resultSet.next()) { int
	 * total_rows = resultSet.getMetaData().getColumnCount(); for (int i = 0; i <
	 * total_rows; i++) { JSONObject obj = new JSONObject();
	 * obj.put(resultSet.getMetaData().getColumnLabel(i + 1) .toLowerCase(),
	 * resultSet.getObject(i + 1)); jsonArray.put(obj); } } return jsonArray; }
	 */

	@RequestMapping(value = "/getFilteredPivot", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JSONArray getFilteredPivot(HttpServletRequest req) throws SQLException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		UserDashboard dashboard = userMatrixService.getDashboardByUserIdAndDashboardName(userId, RMSConstants.GWP_DASHBOARD);
		UserMatrixMasterRequest request = new UserMatrixMasterRequest();
		request.setDashboardId(Arrays.asList(dashboard.getDashboardId()));
		UserMatrixMasterRequest masterRequest = userMatrixService.getUserMatrixChildByUserId(userId, request);
		// String maps= "{}";
		long startTime = System.currentTimeMillis();
		JSONArray jsArray = new JSONArray();
		System.out.println("Started query execution");

		String lob = req.getParameter("lob") == null ? "" : req.getParameter("lob");
		String zone = req.getParameter("zone") == null ? "" : req.getParameter("zone");
		String cluster = req.getParameter("cluster") == null ? "" : req.getParameter("cluster");
		String state = req.getParameter("state") == null ? "" : req.getParameter("state");
		String branchcode = req.getParameter("branchcode") == null ? "" : req.getParameter("branchcode");
		String channel = req.getParameter("channel") == null ? "" : req.getParameter("channel");
		String subchannel = req.getParameter("subchannel") == null ? "" : req.getParameter("subchannel");
		String product = req.getParameter("product") == null ? "" : req.getParameter("product");
		String productdesc = req.getParameter("productdesc") == null ? "" : req.getParameter("productdesc");
		String make = req.getParameter("make") == null ? "" : req.getParameter("make");
		String modelname = req.getParameter("modelname") == null ? "" : req.getParameter("modelname");

		try {
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
//			connection = driverManager.connect("jdbc:kylin://localhost:7070/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();
			String queryStr = " select  GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUB_CHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE,";
			// ,GWP_23_7_CUBE.STATE_CODE, ";
			queryStr += " SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY, ";
			queryStr += " SUM(PRE_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PRE_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PRE_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PRE_YTD_POLICY_COUNT) LASTYR_YTD_POLICY";
			queryStr += " from ( ";
			queryStr += " SELECT ";
			queryStr += " DS_POLICY_FACT_15_07_NW.ZONE_NAME as ZONE_NAME";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME as CLUSTER_NAME";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.STATE as STATE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE as BRANCH_CODE ";
			queryStr += " ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME as BRANCH_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE as PRODUCT_CODE ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT as PRODUCT ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION as PRODUCT_DESC ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW as LOB ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE as BUSINESS_TYPE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CHANNEL as CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL as SUB_CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MAKE as MAKE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MODEL_CODE as MODEL_CODE ";
			queryStr += " ,DS_MASTER_MODEL_NW.MODEL_NAME as MODEL_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.FIN_YEAR as FACT_FIN_YEAR ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG as FACT_MONTH_FLAG, ";
			queryStr += " SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT, ";
			queryStr += " SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getPreMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  PRE_MTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  PRE_YTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getPreMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) PRE_MTD_POLICY_COUNT ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) PRE_YTD_POLICY_COUNT ";
			queryStr += " FROM RSDB.DS_POLICY_FACT_15_07_NW as DS_POLICY_FACT_15_07_NW ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.ZONE_NAME = DS_MASTER_ZONE_NOW.ZONE_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_CLUSTER_NOW as DS_MASTER_CLUSTER_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME = DS_MASTER_CLUSTER_NOW.CLUSTER_NAME ";// AND
																											// DS_POLICY_FACT_15_07_NW.ZONE_NAME
																											// =
																											// DS_MASTER_CLUSTER_NOW.ZONE_NAME
																											// ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW ";
			// queryStr += " ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME =
			// DS_MASTER_STATE_NOW.CLUSTER_NAME AND DS_POLICY_FACT_15_07_NW.STATE =
			// DS_MASTER_STATE_NOW.STATE ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.STATE = DS_MASTER_STATE_NOW.STATE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_BRANCH_NOW as DS_MASTER_BRANCH_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.BRANCH_CODE = DS_MASTER_BRANCH_NOW.BRANCH_CODE ";// AND
																										// DS_POLICY_FACT_15_07_NW.CLUSTER_NAME
																										// =
																										// DS_MASTER_BRANCH_NOW.CLUSTER_NAME
																										// AND
																										// DS_POLICY_FACT_15_07_NW.STATE
																										// =
																										// DS_MASTER_BRANCH_NOW.STATE_NEW
																										// AND
																										// DS_POLICY_FACT_15_07_NW.ZONE_NAME
																										// =
																										// DS_MASTER_BRANCH_NOW.ZONE
																										// ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND DS_POLICY_FACT_15_07_NW.SUB_CHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_MAKE_NOW as DS_MASTER_MAKE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MAKE = DS_MASTER_MAKE_NOW.MAKE_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_MODEL_NW as DS_MASTER_MODEL_NW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MAKE = DS_MASTER_MODEL_NW.MAKE AND DS_POLICY_FACT_15_07_NW.MODEL_CODE = DS_MASTER_MODEL_NW.MODEL_CODE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_FIN_YEAR as DS_MASTER_FIN_YEAR ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.FIN_YEAR = DS_MASTER_FIN_YEAR.FIN_YEAR ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_FIN_MONTH as DS_MASTER_FIN_MONTH ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MONTH_FLAG = DS_MASTER_FIN_MONTH.ENTRY_MONTH ";
			queryStr += " WHERE 1=1 ";
//			if (userId == 2) {
//				queryStr += " and DS_POLICY_FACT_15_07_NW.ZONE_NAME in ('South Zone')";
//			}
//			if (userId == 3) {
//				queryStr += " and DS_POLICY_FACT_15_07_NW.BRANCH_CODE in ('X1')";
//			}
			if (masterRequest.getZones() != null && !masterRequest.getZones().isEmpty()
					&& !masterRequest.getZones().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getZones().size(); i++) {
					vals += "'" + masterRequest.getZones().get(i) + "'";
					if (i != masterRequest.getZones().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.ZONE_NAME in (" + vals + ")";
			}
			if (masterRequest.getClusters() != null && !masterRequest.getClusters().isEmpty()
					&& !masterRequest.getClusters().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getClusters().size(); i++) {
					vals += "'" + masterRequest.getClusters().get(i) + "'";
					if (i != masterRequest.getClusters().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.CLUSTER_NAME in (" + vals + ")";
			}
			if (masterRequest.getStates() != null && !masterRequest.getStates().isEmpty()
					&& !masterRequest.getStates().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getStates().size(); i++) {
					vals += "'" + masterRequest.getStates().get(i) + "'";
					if (i != masterRequest.getStates().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.STATE in (" + vals + ")";
			}
			if (masterRequest.getBranchCodes() != null && !masterRequest.getBranchCodes().isEmpty()
					&& !masterRequest.getBranchCodes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getBranchCodes().size(); i++) {
					vals += "'" + masterRequest.getBranchCodes().get(i) + "'";
					if (i != masterRequest.getBranchCodes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.BRANCH_CODE in (" + vals + ")";
			}
			if (masterRequest.getProducts() != null && !masterRequest.getProducts().isEmpty()
					&& !masterRequest.getProducts().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getProducts().size(); i++) {
					vals += "'" + masterRequest.getProducts().get(i) + "'";
					if (i != masterRequest.getProducts().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.PRODUCT_CODE in (" + vals + ")";
			}
			if (masterRequest.getBusinessTypes() != null && !masterRequest.getBusinessTypes().isEmpty()
					&& !masterRequest.getBusinessTypes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getBusinessTypes().size(); i++) {
					vals += "'" + masterRequest.getBusinessTypes().get(i) + "'";
					if (i != masterRequest.getBusinessTypes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_PRODUCT_NOW.BUSINESS_TYPE in (" + vals + ")";
			}
			if (masterRequest.getLobs() != null && !masterRequest.getLobs().isEmpty()
					&& !masterRequest.getLobs().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getLobs().size(); i++) {
					vals += "'" + masterRequest.getLobs().get(i) + "'";
					if (i != masterRequest.getLobs().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_PRODUCT_NOW.SEGMENT_NEW in (" + vals + ")";
			}
			if (masterRequest.getChannels() != null && !masterRequest.getChannels().isEmpty()
					&& !masterRequest.getChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getChannels().size(); i++) {
					vals += "'" + masterRequest.getChannels().get(i) + "'";
					if (i != masterRequest.getChannels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.CHANNEL in (" + vals + ")";
			}
			if (masterRequest.getSubChannels() != null && !masterRequest.getSubChannels().isEmpty()
					&& !masterRequest.getSubChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getSubChannels().size(); i++) {
					vals += "'" + masterRequest.getSubChannels().get(i) + "'";
					if (i != masterRequest.getSubChannels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.SUB_CHANNEL in (" + vals + ")";
			}
			if (masterRequest.getMakes() != null && !masterRequest.getMakes().isEmpty()
					&& !masterRequest.getMakes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getMakes().size(); i++) {
					vals += "'" + masterRequest.getMakes().get(i) + "'";
					if (i != masterRequest.getMakes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.MAKE in (" + vals + ")";
			}
			if (masterRequest.getModels() != null && !masterRequest.getModels().isEmpty()
					&& !masterRequest.getModels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getModels().size(); i++) {
					vals += "'" + masterRequest.getModels().get(i) + "'";
					if (i != masterRequest.getModels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_MODEL_NW.MODEL_NAME in (" + vals + ")";
			}
			queryStr += " group by ";
			queryStr += " DS_POLICY_FACT_15_07_NW.ZONE_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.STATE ";
			// queryStr += " ,DS_MASTER_STATE_NOW.STATE_CODE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE ";
			queryStr += " ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MAKE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MODEL_CODE ";
			queryStr += " ,DS_MASTER_MODEL_NW.MODEL_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.FIN_YEAR ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG ";
			queryStr += " ,DS_MASTER_FIN_MONTH.ENTRY_MONTH ";
			queryStr += " ) GWP_23_7_CUBE ";
			queryStr += " GROUP BY ";
			queryStr += " GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUB_CHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE";
			// ,GWP_23_7_CUBE.STATE_CODE ";
			System.out.println("START------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");
			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			String[] myArray = {};
			ArrayList<String> arrayList = new ArrayList<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				arrayList.add(rsmd.getColumnName(i));
			}
//		        System.out.println(arrayList);
			jsArray.add(arrayList);
			int counter = 0;
			while (rs.next()) {
				// if( counter <400000 ) {
				ArrayList<String> tmpArray = new ArrayList<String>();
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
					tmpArray.add(rs.getString(i));
				}
				// arrayList.addAll(tmpArray);
//		        		System.out.println(jsArray);
				jsArray.add(tmpArray);
				// }
				counter++;
			}

			System.out.println("--------------------------------------------" + counter);
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
//		return jsArray.toJSONString();
		return jsArray;
	}

	/*
	 * @GetMapping("/gwppivot") public String showgwpPivot() { return "gwp_Pivot"; }
	 */

	@RequestMapping(value = "/getChartObject", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody GwpResponse getGWPListObj() throws SQLException {
		GwpResponse GwpResponse = new GwpResponse();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		UserDashboard dashboard = userMatrixService.getDashboardByUserIdAndDashboardName(userId, RMSConstants.GWP_DASHBOARD);
		UserMatrixMasterRequest request = new UserMatrixMasterRequest();
		request.setDashboardId(Arrays.asList(dashboard.getDashboardId()));
		UserMatrixMasterRequest masterRequest = userMatrixService.getUserMatrixChildByUserId(userId, request);
		long startTime = System.currentTimeMillis();
		// JSONArray jsArray = new JSONArray();
		System.out.println("Started query execution");
		try {
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
//			connection = driverManager.connect("jdbc:kylin://localhost:7070/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();
			String queryStr = " select  GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUB_CHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE,";
			// ,GWP_23_7_CUBE.STATE_CODE, ";
			queryStr += " SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY, ";
			queryStr += " SUM(PRE_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PRE_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PRE_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PRE_YTD_POLICY_COUNT) LASTYR_YTD_POLICY";
			queryStr += " from ( ";
			queryStr += " SELECT ";
			queryStr += " DS_POLICY_FACT_15_07_NW.ZONE_NAME as ZONE_NAME";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME as CLUSTER_NAME";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.STATE as STATE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE as BRANCH_CODE ";
			queryStr += " ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME as BRANCH_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE as PRODUCT_CODE ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT as PRODUCT ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION as PRODUCT_DESC ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW as LOB ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE as BUSINESS_TYPE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CHANNEL as CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL as SUB_CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MAKE as MAKE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MODEL_CODE as MODEL_CODE ";
			queryStr += " ,DS_MASTER_MODEL_NW.MODEL_NAME as MODEL_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.FIN_YEAR as FACT_FIN_YEAR ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG as FACT_MONTH_FLAG, ";
			queryStr += " SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT, ";
			queryStr += " SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getPreMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  PRE_MTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  PRE_YTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getPreMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) PRE_MTD_POLICY_COUNT ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) PRE_YTD_POLICY_COUNT ";
			queryStr += " FROM RSDB.DS_POLICY_FACT_15_07_NW as DS_POLICY_FACT_15_07_NW ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.ZONE_NAME = DS_MASTER_ZONE_NOW.ZONE_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_CLUSTER_NOW as DS_MASTER_CLUSTER_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME = DS_MASTER_CLUSTER_NOW.CLUSTER_NAME ";// AND
																											// DS_POLICY_FACT_15_07_NW.ZONE_NAME
																											// =
																											// DS_MASTER_CLUSTER_NOW.ZONE_NAME
																											// ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW ";
			// queryStr += " ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME =
			// DS_MASTER_STATE_NOW.CLUSTER_NAME AND DS_POLICY_FACT_15_07_NW.STATE =
			// DS_MASTER_STATE_NOW.STATE ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.STATE = DS_MASTER_STATE_NOW.STATE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_BRANCH_NOW as DS_MASTER_BRANCH_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.BRANCH_CODE = DS_MASTER_BRANCH_NOW.BRANCH_CODE ";// AND
																										// DS_POLICY_FACT_15_07_NW.CLUSTER_NAME
																										// =
																										// DS_MASTER_BRANCH_NOW.CLUSTER_NAME
																										// AND
																										// DS_POLICY_FACT_15_07_NW.STATE
																										// =
																										// DS_MASTER_BRANCH_NOW.STATE_NEW
																										// AND
																										// DS_POLICY_FACT_15_07_NW.ZONE_NAME
																										// =
																										// DS_MASTER_BRANCH_NOW.ZONE
																										// ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND DS_POLICY_FACT_15_07_NW.SUB_CHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_MAKE_NOW as DS_MASTER_MAKE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MAKE = DS_MASTER_MAKE_NOW.MAKE_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_MODEL_NW as DS_MASTER_MODEL_NW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MAKE = DS_MASTER_MODEL_NW.MAKE AND DS_POLICY_FACT_15_07_NW.MODEL_CODE = DS_MASTER_MODEL_NW.MODEL_CODE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_FIN_YEAR as DS_MASTER_FIN_YEAR ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.FIN_YEAR = DS_MASTER_FIN_YEAR.FIN_YEAR ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_FIN_MONTH as DS_MASTER_FIN_MONTH ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MONTH_FLAG = DS_MASTER_FIN_MONTH.ENTRY_MONTH ";
			queryStr += " WHERE 1=1 ";
//			if (userId == 2) {
//				queryStr += " and DS_POLICY_FACT_15_07_NW.ZONE_NAME in ('South Zone')";
//			}
//			if (userId == 3) {
//				queryStr += " and DS_POLICY_FACT_15_07_NW.BRANCH_CODE in ('X1')";
//			}
			if (masterRequest.getZones() != null && !masterRequest.getZones().isEmpty()
					&& !masterRequest.getZones().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getZones().size(); i++) {
					vals += "'" + masterRequest.getZones().get(i) + "'";
					if (i != masterRequest.getZones().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.ZONE_NAME in (" + vals + ")";
			}
			if (masterRequest.getClusters() != null && !masterRequest.getClusters().isEmpty()
					&& !masterRequest.getClusters().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getClusters().size(); i++) {
					vals += "'" + masterRequest.getClusters().get(i) + "'";
					if (i != masterRequest.getClusters().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.CLUSTER_NAME in (" + vals + ")";
			}
			if (masterRequest.getStates() != null && !masterRequest.getStates().isEmpty()
					&& !masterRequest.getStates().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getStates().size(); i++) {
					vals += "'" + masterRequest.getStates().get(i) + "'";
					if (i != masterRequest.getStates().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.STATE in (" + vals + ")";
			}
			if (masterRequest.getBranchCodes() != null && !masterRequest.getBranchCodes().isEmpty()
					&& !masterRequest.getBranchCodes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getBranchCodes().size(); i++) {
					vals += "'" + masterRequest.getBranchCodes().get(i) + "'";
					if (i != masterRequest.getBranchCodes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.BRANCH_CODE in (" + vals + ")";
			}
			if (masterRequest.getProducts() != null && !masterRequest.getProducts().isEmpty()
					&& !masterRequest.getProducts().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getProducts().size(); i++) {
					vals += "'" + masterRequest.getProducts().get(i) + "'";
					if (i != masterRequest.getProducts().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.PRODUCT_CODE in (" + vals + ")";
			}
			if (masterRequest.getBusinessTypes() != null && !masterRequest.getBusinessTypes().isEmpty()
					&& !masterRequest.getBusinessTypes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getBusinessTypes().size(); i++) {
					vals += "'" + masterRequest.getBusinessTypes().get(i) + "'";
					if (i != masterRequest.getBusinessTypes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_PRODUCT_NOW.BUSINESS_TYPE in (" + vals + ")";
			}
			if (masterRequest.getLobs() != null && !masterRequest.getLobs().isEmpty()
					&& !masterRequest.getLobs().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getLobs().size(); i++) {
					vals += "'" + masterRequest.getLobs().get(i) + "'";
					if (i != masterRequest.getLobs().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_PRODUCT_NOW.SEGMENT_NEW in (" + vals + ")";
			}
			if (masterRequest.getChannels() != null && !masterRequest.getChannels().isEmpty()
					&& !masterRequest.getChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getChannels().size(); i++) {
					vals += "'" + masterRequest.getChannels().get(i) + "'";
					if (i != masterRequest.getChannels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.CHANNEL in (" + vals + ")";
			}
			if (masterRequest.getSubChannels() != null && !masterRequest.getSubChannels().isEmpty()
					&& !masterRequest.getSubChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getSubChannels().size(); i++) {
					vals += "'" + masterRequest.getSubChannels().get(i) + "'";
					if (i != masterRequest.getSubChannels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.SUB_CHANNEL in (" + vals + ")";
			}
			if (masterRequest.getMakes() != null && !masterRequest.getMakes().isEmpty()
					&& !masterRequest.getMakes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getMakes().size(); i++) {
					vals += "'" + masterRequest.getMakes().get(i) + "'";
					if (i != masterRequest.getMakes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.MAKE in (" + vals + ")";
			}
			if (masterRequest.getModels() != null && !masterRequest.getModels().isEmpty()
					&& !masterRequest.getModels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getModels().size(); i++) {
					vals += "'" + masterRequest.getModels().get(i) + "'";
					if (i != masterRequest.getModels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_MODEL_NW.MODEL_NAME in (" + vals + ")";
			}
			queryStr += " group by ";
			queryStr += " DS_POLICY_FACT_15_07_NW.ZONE_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.STATE ";
			// queryStr += " ,DS_MASTER_STATE_NOW.STATE_CODE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE ";
			queryStr += " ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MAKE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MODEL_CODE ";
			queryStr += " ,DS_MASTER_MODEL_NW.MODEL_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.FIN_YEAR ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG ";
			queryStr += " ,DS_MASTER_FIN_MONTH.ENTRY_MONTH ";
			queryStr += " ) GWP_23_7_CUBE ";
			queryStr += " GROUP BY ";
			queryStr += " GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUB_CHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE";
			// ,GWP_23_7_CUBE.STATE_CODE ";

			// String queryStr = "select
			// GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUBCHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE,GWP_23_7_CUBE.STATE_CODE,
			// SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE)
			// YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,
			// SUM(PRE_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PRE_YTD_GWP_OUR_SHARE)
			// LASTYR_YTD_GWP,SUM(PRE_MTD_POLICY_COUNT)
			// LASTYR_MTD_POLICY,SUM(PRE_YTD_POLICY_COUNT) LASTYR_YTD_POLICY from ( SELECT
			// DS_POLICY_FACT_15_07_NW.ZONE_NAME as ZONE_NAME
			// ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME as
			// CLUSTER_NAME ,DS_POLICY_FACT_15_07_NW.STATE as STATE
			// ,DS_MASTER_STATE_NOW.STATE_CODE AS
			// STATE_CODE ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE as BRANCH_CODE
			// ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME as BRANCH_NAME
			// ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE as PRODUCT_CODE
			// ,DS_MASTER_PRODUCT_NOW.PRODUCT as
			// PRODUCT ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION as PRODUCT_DESC
			// ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW as LOB
			// ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE as
			// BUSINESS_TYPE ,DS_POLICY_FACT_15_07_NW.CHANNEL as CHANNEL
			// ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL as SUBCHANNEL
			// ,DS_POLICY_FACT_15_07_NW.MAKE as MAKE
			// ,DS_POLICY_FACT_15_07_NW.MODEL_CODE as MODEL_CODE
			// ,DS_MASTER_MODEL_NW.MODEL_NAME as
			// MODEL_NAME ,DS_POLICY_FACT_15_07_NW.FIN_YEAR as FACT_FIN_YEAR
			// ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG as FACT_MONTH_FLAG, SUM(case when
			// (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = 'MAY-2019' and
			// DS_POLICY_FACT_15_07_NW.FIN_YEAR=
			// '2019-2020') then DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end) as
			// MTD_GWP_OUR_SHARE , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR=
			// '2019-2020' then
			// DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end) as YTD_GWP_OUR_SHARE ,
			// SUM(case
			// when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = 'MAY-2019' and
			// DS_POLICY_FACT_15_07_NW.FIN_YEAR=
			// '2019-2020') then DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end)
			// MTD_POLICY_COUNT , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR=
			// '2019-2020' then
			// DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT,
			// SUM(case when
			// (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = 'MAY-2018' and
			// DS_POLICY_FACT_15_07_NW.FIN_YEAR=
			// '2018-2019') then DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end) as
			// PRE_MTD_GWP_OUR_SHARE , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR=
			// '2018-2019'
			// then DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end) as
			// PRE_YTD_GWP_OUR_SHARE
			// , SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = 'MAY-2018' and
			// DS_POLICY_FACT_15_07_NW.FIN_YEAR= '2018-2019') then DS_POLICY_FACT_15_07_NW .
			// POLICY_COUNT
			// else 0.0 end) PRE_MTD_POLICY_COUNT , SUM(case when
			// DS_POLICY_FACT_15_07_NW.FIN_YEAR=
			// '2018-2019' then DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end)
			// PRE_YTD_POLICY_COUNT FROM RSDB.DS_POLICY_FACT_15_07_NW as
			// DS_POLICY_FACT_15_07_NW INNER JOIN
			// RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE ON DS_POLICY_FACT_15_07_NW.ZONE_NAME
			// =
			// DS_MASTER_ZONE_NOW.ZONE_NAME INNER JOIN RSDB.DS_MASTER_CLUSTER_NOW as
			// DS_MASTER_CLUSTER_NOW ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME =
			// DS_MASTER_CLUSTER_NOW.CLUSTER_NAME AND DS_POLICY_FACT_15_07_NW.ZONE_NAME =
			// DS_MASTER_CLUSTER_NOW.ZONE_NAME INNER JOIN RSDB.DS_MASTER_STATE_NOW as
			// DS_MASTER_STATE_NOW ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME =
			// DS_MASTER_STATE_NOW.CLUSTER_NAME AND DS_POLICY_FACT_15_07_NW.STATE =
			// DS_MASTER_STATE_NOW.STATE INNER JOIN RSDB.DS_MASTER_BRANCH_NOW as
			// DS_MASTER_BRANCH_NOW
			// ON DS_POLICY_FACT_15_07_NW.BRANCH_CODE = DS_MASTER_BRANCH_NOW.BRANCH_CODE AND
			// DS_POLICY_FACT_15_07_NW.CLUSTER_NAME = DS_MASTER_BRANCH_NOW.CLUSTER_NAME AND
			// DS_POLICY_FACT_15_07_NW.STATE = DS_MASTER_BRANCH_NOW.STATE_NEW AND
			// DS_POLICY_FACT_15_07_NW.ZONE_NAME = DS_MASTER_BRANCH_NOW.ZONE INNER JOIN
			// RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW ON
			// DS_POLICY_FACT_15_07_NW.PRODUCT_CODE
			// = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE INNER JOIN
			// RSDB.DS_MASTER_BUSINESS_TYPE_NOW as
			// DS_MASTER_BUSINESS_TYPE_NOW ON DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE =
			// DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE INNER JOIN
			// RSDB.DS_MASTER_CHANNEL_NOW as
			// DS_MASTER_CHANNEL_NOW ON DS_POLICY_FACT_15_07_NW.CHANNEL =
			// DS_MASTER_CHANNEL_NOW.CHANNEL_NAME INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW
			// as
			// DS_MASTER_SUBCHANNEL_NOW ON DS_POLICY_FACT_15_07_NW.CHANNEL =
			// DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND DS_POLICY_FACT_15_07_NW.SUB_CHANNEL
			// =
			// DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL INNER JOIN RSDB.DS_MASTER_MAKE_NOW as
			// DS_MASTER_MAKE_NOW ON DS_POLICY_FACT_15_07_NW.MAKE =
			// DS_MASTER_MAKE_NOW.MAKE_NAME INNER
			// JOIN RSDB.DS_MASTER_MODEL_NW as DS_MASTER_MODEL_NW ON
			// DS_POLICY_FACT_15_07_NW.MAKE =
			// DS_MASTER_MODEL_NW.MAKE AND DS_POLICY_FACT_15_07_NW.MODEL_CODE =
			// DS_MASTER_MODEL_NW.MODEL_CODE INNER JOIN RSDB.DS_MASTER_FIN_YEAR as
			// DS_MASTER_FIN_YEAR ON DS_POLICY_FACT_15_07_NW.FIN_YEAR =
			// DS_MASTER_FIN_YEAR.FIN_YEAR INNER JOIN RSDB.DS_MASTER_FIN_MONTH as
			// DS_MASTER_FIN_MONTH ON DS_POLICY_FACT_15_07_NW.MONTH_FLAG =
			// DS_MASTER_FIN_MONTH.ENTRY_MONTH WHERE 1=1 and
			// DS_MASTER_PRODUCT_NOW.SEGMENT_NEW
			// in ('MOTOR') and DS_POLICY_FACT_15_07_NW.ZONE_NAME in ('South Zone') group by
			// DS_POLICY_FACT_15_07_NW.ZONE_NAME ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME
			// ,DS_POLICY_FACT_15_07_NW.STATE ,DS_MASTER_STATE_NOW.STATE_CODE
			// ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE
			// ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME
			// ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE ,DS_MASTER_PRODUCT_NOW.PRODUCT
			// ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW
			// ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE ,DS_POLICY_FACT_15_07_NW.CHANNEL
			// ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL ,DS_POLICY_FACT_15_07_NW.MAKE
			// ,DS_POLICY_FACT_15_07_NW.MODEL_CODE ,DS_MASTER_MODEL_NW.MODEL_NAME
			// ,DS_POLICY_FACT_15_07_NW.FIN_YEAR ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG )
			// GWP_23_7_CUBE GROUP BY
			// GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUBCHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE,GWP_23_7_CUBE.STATE_CODE
			// ";
			System.out.println("START------------------------------ ");
			System.out.println(queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");
			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			List<String> lobList = new ArrayList<>();
			List<String> zoneList = new ArrayList<>();
			List<String> clusterList = new ArrayList<>();
			List<String> stateList = new ArrayList<>();
			List<String> branchCodeList = new ArrayList<>();
			List<String> channelList = new ArrayList<>();
			List<String> subChannelList = new ArrayList<>();
			List<String> prodcuList = new ArrayList<>();
			List<String> productDescList = new ArrayList<>();
			List<String> makeList = new ArrayList<>();
			List<String> modelList = new ArrayList<>();
			List<Double> mtdGwpList = new ArrayList<>();
			List<Double> ytdGwpList = new ArrayList<>();
			List<Double> mtdPoclicyCountList = new ArrayList<>();
			List<Double> ytdPolicyCountList = new ArrayList<>();
			List<Double> preMtdGwpList = new ArrayList<>();
			List<Double> preYtdGwpList = new ArrayList<>();
			List<Double> preMtdPolicyCountList = new ArrayList<>();
			List<Double> preYtdPolicyCountList = new ArrayList<>();

			while (rs.next()) {
				lobList.add(rs.getString(3));
				zoneList.add(rs.getString(11));
				clusterList.add(rs.getString(12));
				stateList.add(rs.getString(13));
				branchCodeList.add(rs.getString(14));
				channelList.add(rs.getString(4));
				subChannelList.add(rs.getString(5));
				prodcuList.add(rs.getString(2));
				productDescList.add(rs.getString(1));
				makeList.add(rs.getString(6));
				modelList.add(rs.getString(7));
				mtdGwpList.add(rs.getDouble(15));
				ytdGwpList.add(rs.getDouble(16));
				mtdPoclicyCountList.add(rs.getDouble(17));
				ytdPolicyCountList.add(rs.getDouble(18));
				preMtdGwpList.add(rs.getDouble(19));
				preYtdGwpList.add(rs.getDouble(20));
				preMtdPolicyCountList.add(rs.getDouble(21));
				preYtdPolicyCountList.add(rs.getDouble(22));
			}
			GwpResponse.setLob(lobList);
			GwpResponse.setZone(zoneList);
			GwpResponse.setCluster(clusterList);
			GwpResponse.setState(stateList);
			GwpResponse.setBranchcode(branchCodeList);
			GwpResponse.setChannel(channelList);
			GwpResponse.setSubchannel(subChannelList);
			GwpResponse.setProduct(prodcuList);
			GwpResponse.setProductDesc(productDescList);
			GwpResponse.setMake(makeList);
			GwpResponse.setModel(modelList);
			GwpResponse.setMtdGWP(mtdGwpList);
			GwpResponse.setYtdGWP(ytdGwpList);
			GwpResponse.setMtdPolicyCount(mtdPoclicyCountList);
			GwpResponse.setYtdPolicyCount(ytdPolicyCountList);
			GwpResponse.setPreMtdGWP(preMtdGwpList);
			GwpResponse.setPreYtdGWP(preMtdGwpList);
			GwpResponse.setPreMtdPolicyCount(preMtdPolicyCountList);
			GwpResponse.setPreYtdPolicyCount(preYtdPolicyCountList);

			System.out.println("--------------------------------------------" + GwpResponse.getLob().size());
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
//		return jsArray.toJSONString();
		return GwpResponse;
	}

	@RequestMapping(value = "/getChartObjectList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<GwpResponseObject> getGWPResponseObjList() throws SQLException {
		List<GwpResponseObject> GwpResponseList = new ArrayList<GwpResponseObject>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		UserDashboard dashboard = userMatrixService.getDashboardByUserIdAndDashboardName(userId, RMSConstants.GWP_DASHBOARD);
		UserMatrixMasterRequest request = new UserMatrixMasterRequest();
		request.setDashboardId(Arrays.asList(dashboard.getDashboardId()));
		UserMatrixMasterRequest masterRequest = userMatrixService.getUserMatrixChildByUserId(userId, request);
		String maps = "{}";
		long startTime = System.currentTimeMillis();
		// JSONArray jsArray = new JSONArray();
		System.out.println("Started query execution");
		try {
			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager.connect("jdbc:kylin://"+RMSConstants.KYLIN_RS_BASE_IP_AND_PORT+"/learn_kylin", info);
//			connection = driverManager.connect("jdbc:kylin://localhost:7070/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			Statement stmt = connection.createStatement();
			String queryStr = " select  GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUB_CHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE,";
			// ,GWP_23_7_CUBE.STATE_CODE, ";
			queryStr += " SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE) YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY, ";
			queryStr += " SUM(PRE_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PRE_YTD_GWP_OUR_SHARE) LASTYR_YTD_GWP,SUM(PRE_MTD_POLICY_COUNT) LASTYR_MTD_POLICY,SUM(PRE_YTD_POLICY_COUNT) LASTYR_YTD_POLICY";
			queryStr += " from ( ";
			queryStr += " SELECT ";
			queryStr += " DS_POLICY_FACT_15_07_NW.ZONE_NAME as ZONE_NAME";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME as CLUSTER_NAME";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.STATE as STATE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE as BRANCH_CODE ";
			queryStr += " ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME as BRANCH_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE as PRODUCT_CODE ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT as PRODUCT ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION as PRODUCT_DESC ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW as LOB ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE as BUSINESS_TYPE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CHANNEL as CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL as SUB_CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MAKE as MAKE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MODEL_CODE as MODEL_CODE ";
			queryStr += " ,DS_MASTER_MODEL_NW.MODEL_NAME as MODEL_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.FIN_YEAR as FACT_FIN_YEAR ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG as FACT_MONTH_FLAG, ";
			queryStr += " SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  MTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  YTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) MTD_POLICY_COUNT ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT, ";
			queryStr += " SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getPreMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  PRE_MTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . GWP_OUR_SHARE else 0.0 end)  as  PRE_YTD_GWP_OUR_SHARE ";
			queryStr += " , SUM(case when (DS_POLICY_FACT_15_07_NW.MONTH_FLAG = '" + getPreMtd()
					+ "' and DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "') then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) PRE_MTD_POLICY_COUNT ";
			queryStr += " , SUM(case when DS_POLICY_FACT_15_07_NW.FIN_YEAR= '" + getPreYtd()
					+ "' then  DS_POLICY_FACT_15_07_NW . POLICY_COUNT else 0.0 end) PRE_YTD_POLICY_COUNT ";
			queryStr += " FROM RSDB.DS_POLICY_FACT_15_07_NW as DS_POLICY_FACT_15_07_NW ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_ZONE_NOW as DS_MASTER_ZONE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.ZONE_NAME = DS_MASTER_ZONE_NOW.ZONE_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_CLUSTER_NOW as DS_MASTER_CLUSTER_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME = DS_MASTER_CLUSTER_NOW.CLUSTER_NAME ";// AND
																											// DS_POLICY_FACT_15_07_NW.ZONE_NAME
																											// =
																											// DS_MASTER_CLUSTER_NOW.ZONE_NAME
																											// ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_STATE_NOW as DS_MASTER_STATE_NOW ";
			// queryStr += " ON DS_POLICY_FACT_15_07_NW.CLUSTER_NAME =
			// DS_MASTER_STATE_NOW.CLUSTER_NAME AND DS_POLICY_FACT_15_07_NW.STATE =
			// DS_MASTER_STATE_NOW.STATE ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.STATE = DS_MASTER_STATE_NOW.STATE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_BRANCH_NOW as DS_MASTER_BRANCH_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.BRANCH_CODE = DS_MASTER_BRANCH_NOW.BRANCH_CODE ";// AND
																										// DS_POLICY_FACT_15_07_NW.CLUSTER_NAME
																										// =
																										// DS_MASTER_BRANCH_NOW.CLUSTER_NAME
																										// AND
																										// DS_POLICY_FACT_15_07_NW.STATE
																										// =
																										// DS_MASTER_BRANCH_NOW.STATE_NEW
																										// AND
																										// DS_POLICY_FACT_15_07_NW.ZONE_NAME
																										// =
																										// DS_MASTER_BRANCH_NOW.ZONE
																										// ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_PRODUCT_NOW as DS_MASTER_PRODUCT_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.PRODUCT_CODE = DS_MASTER_PRODUCT_NOW.PRODUCT_CODE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_BUSINESS_TYPE_NOW as DS_MASTER_BUSINESS_TYPE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE = DS_MASTER_BUSINESS_TYPE_NOW.BUSINESS_TYPE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_CHANNEL_NOW as DS_MASTER_CHANNEL_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CHANNEL = DS_MASTER_CHANNEL_NOW.CHANNEL_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_SUBCHANNEL_NOW as DS_MASTER_SUBCHANNEL_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.CHANNEL = DS_MASTER_SUBCHANNEL_NOW.CHANNEL_NAME AND DS_POLICY_FACT_15_07_NW.SUB_CHANNEL = DS_MASTER_SUBCHANNEL_NOW.SUB_CHANNEL ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_MAKE_NOW as DS_MASTER_MAKE_NOW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MAKE = DS_MASTER_MAKE_NOW.MAKE_NAME ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_MODEL_NW as DS_MASTER_MODEL_NW ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MAKE = DS_MASTER_MODEL_NW.MAKE AND DS_POLICY_FACT_15_07_NW.MODEL_CODE = DS_MASTER_MODEL_NW.MODEL_CODE ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_FIN_YEAR as DS_MASTER_FIN_YEAR ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.FIN_YEAR = DS_MASTER_FIN_YEAR.FIN_YEAR ";
			queryStr += " INNER JOIN RSDB.DS_MASTER_FIN_MONTH as DS_MASTER_FIN_MONTH ";
			queryStr += " ON DS_POLICY_FACT_15_07_NW.MONTH_FLAG = DS_MASTER_FIN_MONTH.ENTRY_MONTH ";
			queryStr += " WHERE 1=1 ";
//			if (userId == 2) {
//				queryStr += " and DS_POLICY_FACT_15_07_NW.ZONE_NAME in ('South Zone')";
//			}
//			if (userId == 3) {
//				queryStr += " and DS_POLICY_FACT_15_07_NW.BRANCH_CODE in ('X1')";
//			}
			if (masterRequest.getZones() != null && !masterRequest.getZones().isEmpty()
					&& !masterRequest.getZones().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getZones().size(); i++) {
					vals += "'" + masterRequest.getZones().get(i) + "'";
					if (i != masterRequest.getZones().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.ZONE_NAME in (" + vals + ")";
			}
			if (masterRequest.getClusters() != null && !masterRequest.getClusters().isEmpty()
					&& !masterRequest.getClusters().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getClusters().size(); i++) {
					vals += "'" + masterRequest.getClusters().get(i) + "'";
					if (i != masterRequest.getClusters().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.CLUSTER_NAME in (" + vals + ")";
			}
			if (masterRequest.getStates() != null && !masterRequest.getStates().isEmpty()
					&& !masterRequest.getStates().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getStates().size(); i++) {
					vals += "'" + masterRequest.getStates().get(i) + "'";
					if (i != masterRequest.getStates().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.STATE in (" + vals + ")";
			}
			if (masterRequest.getBranchCodes() != null && !masterRequest.getBranchCodes().isEmpty()
					&& !masterRequest.getBranchCodes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getBranchCodes().size(); i++) {
					vals += "'" + masterRequest.getBranchCodes().get(i) + "'";
					if (i != masterRequest.getBranchCodes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.BRANCH_CODE in (" + vals + ")";
			}
			if (masterRequest.getProducts() != null && !masterRequest.getProducts().isEmpty()
					&& !masterRequest.getProducts().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getProducts().size(); i++) {
					vals += "'" + masterRequest.getProducts().get(i) + "'";
					if (i != masterRequest.getProducts().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.PRODUCT_CODE in (" + vals + ")";
			}
			if (masterRequest.getBusinessTypes() != null && !masterRequest.getBusinessTypes().isEmpty()
					&& !masterRequest.getBusinessTypes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getBusinessTypes().size(); i++) {
					vals += "'" + masterRequest.getBusinessTypes().get(i) + "'";
					if (i != masterRequest.getBusinessTypes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_PRODUCT_NOW.BUSINESS_TYPE in (" + vals + ")";
			}
			if (masterRequest.getLobs() != null && !masterRequest.getLobs().isEmpty()
					&& !masterRequest.getLobs().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getLobs().size(); i++) {
					vals += "'" + masterRequest.getLobs().get(i) + "'";
					if (i != masterRequest.getLobs().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_PRODUCT_NOW.SEGMENT_NEW in (" + vals + ")";
			}
			if (masterRequest.getChannels() != null && !masterRequest.getChannels().isEmpty()
					&& !masterRequest.getChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getChannels().size(); i++) {
					vals += "'" + masterRequest.getChannels().get(i) + "'";
					if (i != masterRequest.getChannels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.CHANNEL in (" + vals + ")";
			}
			if (masterRequest.getSubChannels() != null && !masterRequest.getSubChannels().isEmpty()
					&& !masterRequest.getSubChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getSubChannels().size(); i++) {
					vals += "'" + masterRequest.getSubChannels().get(i) + "'";
					if (i != masterRequest.getSubChannels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.SUB_CHANNEL in (" + vals + ")";
			}
			if (masterRequest.getMakes() != null && !masterRequest.getMakes().isEmpty()
					&& !masterRequest.getMakes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getMakes().size(); i++) {
					vals += "'" + masterRequest.getMakes().get(i) + "'";
					if (i != masterRequest.getMakes().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_POLICY_FACT_15_07_NW.MAKE in (" + vals + ")";
			}
			if (masterRequest.getModels() != null && !masterRequest.getModels().isEmpty()
					&& !masterRequest.getModels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				String vals = "";
				for (int i = 0; i < masterRequest.getModels().size(); i++) {
					vals += "'" + masterRequest.getModels().get(i) + "'";
					if (i != masterRequest.getModels().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and DS_MASTER_MODEL_NW.MODEL_NAME in (" + vals + ")";
			}
			queryStr += " group by ";
			queryStr += " DS_POLICY_FACT_15_07_NW.ZONE_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CLUSTER_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.STATE ";
			// queryStr += " ,DS_MASTER_STATE_NOW.STATE_CODE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BRANCH_CODE ";
			queryStr += " ,DS_MASTER_BRANCH_NOW.REVISED_BRANCH_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.PRODUCT_CODE ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.PRODUCT_DESCRIPTION ";
			queryStr += " ,DS_MASTER_PRODUCT_NOW.SEGMENT_NEW ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.BUSINESS_TYPE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.SUB_CHANNEL ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MAKE ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MODEL_CODE ";
			queryStr += " ,DS_MASTER_MODEL_NW.MODEL_NAME ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.FIN_YEAR ";
			queryStr += " ,DS_POLICY_FACT_15_07_NW.MONTH_FLAG ";
			queryStr += " ,DS_MASTER_FIN_MONTH.ENTRY_MONTH ";
			queryStr += " ) GWP_23_7_CUBE ";
			queryStr += " GROUP BY ";
			queryStr += " GWP_23_7_CUBE.PRODUCT,GWP_23_7_CUBE.PRODUCT_DESC,GWP_23_7_CUBE.LOB,GWP_23_7_CUBE.CHANNEL,GWP_23_7_CUBE.SUB_CHANNEL,GWP_23_7_CUBE.MAKE,GWP_23_7_CUBE.MODEL_NAME,GWP_23_7_CUBE.PRODUCT_CODE,GWP_23_7_CUBE.BRANCH_NAME,GWP_23_7_CUBE.BUSINESS_TYPE,GWP_23_7_CUBE.ZONE_NAME,GWP_23_7_CUBE.CLUSTER_NAME,GWP_23_7_CUBE.STATE,GWP_23_7_CUBE.BRANCH_CODE";
			// ,GWP_23_7_CUBE.STATE_CODE ";
			// String queryStr = "select
			// CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE,
			// SUM(MTD_GWP_OUR_SHARE) MTD_GWP,SUM(YTD_GWP_OUR_SHARE)
			// YTD_GWP,SUM(MTD_POLICY_COUNT) MTD_POLICY,SUM(YTD_POLICY_COUNT) YTD_POLICY,
			// SUM(PRE_MTD_GWP_OUR_SHARE) LASTYR_MTD_GWP,SUM(PRE_YTD_GWP_OUR_SHARE)
			// LASTYR_YTD_GWP,SUM(PRE_MTD_POLICY_COUNT)
			// LASTYR_MTD_POLICY,SUM(PRE_YTD_POLICY_COUNT) LASTYR_YTD_POLICY from ( SELECT
			// NEW_POLICY_FACT.ZONE_NAME as ZONE_NAME ,NEW_POLICY_FACT.CLUSTER_NAME as
			// CLUSTER_NAME ,NEW_POLICY_FACT.STATE as STATE ,NEW_MASTER_STATE.STATE_CODE AS
			// STATE_CODE ,NEW_POLICY_FACT.BRANCH_CODE as BRANCH_CODE
			// ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME as BRANCH_NAME
			// ,NEW_POLICY_FACT.PRODUCT_CODE as PRODUCT_CODE ,NEW_MASTER_PRODUCT.PRODUCT as
			// PRODUCT ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION as PRODUCT_DESC
			// ,NEW_MASTER_PRODUCT.SEGMENT_NEW as LOB ,NEW_POLICY_FACT.BUSINESS_TYPE as
			// BUSINESS_TYPE ,NEW_POLICY_FACT.CHANNEL as CHANNEL
			// ,NEW_POLICY_FACT.SUB_CHANNEL as SUBCHANNEL ,NEW_POLICY_FACT.MAKE as MAKE
			// ,NEW_POLICY_FACT.MODEL_CODE as MODEL_CODE ,NEW_MASTER_MODEL.MODEL_NAME as
			// MODEL_NAME ,NEW_POLICY_FACT.FIN_YEAR as FACT_FIN_YEAR
			// ,NEW_POLICY_FACT.MONTH_FLAG as FACT_MONTH_FLAG, SUM(case when
			// (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR=
			// '2019-2020') then NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end) as
			// MTD_GWP_OUR_SHARE , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then
			// NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end) as YTD_GWP_OUR_SHARE , SUM(case
			// when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2019' and NEW_POLICY_FACT.FIN_YEAR=
			// '2019-2020') then NEW_POLICY_FACT . POLICY_COUNT else 0.0 end)
			// MTD_POLICY_COUNT , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2019-2020' then
			// NEW_POLICY_FACT . POLICY_COUNT else 0.0 end) YTD_POLICY_COUNT, SUM(case when
			// (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and NEW_POLICY_FACT.FIN_YEAR=
			// '2018-2019') then NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end) as
			// PRE_MTD_GWP_OUR_SHARE , SUM(case when NEW_POLICY_FACT.FIN_YEAR= '2018-2019'
			// then NEW_POLICY_FACT . GWP_OUR_SHARE else 0.0 end) as PRE_YTD_GWP_OUR_SHARE
			// , SUM(case when (NEW_POLICY_FACT.MONTH_FLAG = 'MAY-2018' and
			// NEW_POLICY_FACT.FIN_YEAR= '2018-2019') then NEW_POLICY_FACT . POLICY_COUNT
			// else 0.0 end) PRE_MTD_POLICY_COUNT , SUM(case when NEW_POLICY_FACT.FIN_YEAR=
			// '2018-2019' then NEW_POLICY_FACT . POLICY_COUNT else 0.0 end)
			// PRE_YTD_POLICY_COUNT FROM RSDB.NEW_POLICY_FACT as NEW_POLICY_FACT INNER JOIN
			// RSDB.NEW_MASTER_ZONE as NEW_MASTER_ZONE ON NEW_POLICY_FACT.ZONE_NAME =
			// NEW_MASTER_ZONE.ZONE_NAME INNER JOIN RSDB.NEW_MASTER_CLUSTER as
			// NEW_MASTER_CLUSTER ON NEW_POLICY_FACT.CLUSTER_NAME =
			// NEW_MASTER_CLUSTER.CLUSTER_NAME AND NEW_POLICY_FACT.ZONE_NAME =
			// NEW_MASTER_CLUSTER.ZONE_NAME INNER JOIN RSDB.NEW_MASTER_STATE as
			// NEW_MASTER_STATE ON NEW_POLICY_FACT.CLUSTER_NAME =
			// NEW_MASTER_STATE.CLUSTER_NAME AND NEW_POLICY_FACT.STATE =
			// NEW_MASTER_STATE.STATE INNER JOIN RSDB.NEW_MASTER_BRANCH as NEW_MASTER_BRANCH
			// ON NEW_POLICY_FACT.BRANCH_CODE = NEW_MASTER_BRANCH.BRANCH_CODE AND
			// NEW_POLICY_FACT.CLUSTER_NAME = NEW_MASTER_BRANCH.CLUSTER_NAME AND
			// NEW_POLICY_FACT.STATE = NEW_MASTER_BRANCH.STATE_NEW AND
			// NEW_POLICY_FACT.ZONE_NAME = NEW_MASTER_BRANCH.ZONE INNER JOIN
			// RSDB.NEW_MASTER_PRODUCT as NEW_MASTER_PRODUCT ON NEW_POLICY_FACT.PRODUCT_CODE
			// = NEW_MASTER_PRODUCT.PRODUCT_CODE INNER JOIN RSDB.NEW_MASTER_BUSINESS_TYPE as
			// NEW_MASTER_BUSINESS_TYPE ON NEW_POLICY_FACT.BUSINESS_TYPE =
			// NEW_MASTER_BUSINESS_TYPE.BUSINESS_TYPE INNER JOIN RSDB.NEW_MASTER_CHANNEL as
			// NEW_MASTER_CHANNEL ON NEW_POLICY_FACT.CHANNEL =
			// NEW_MASTER_CHANNEL.CHANNEL_NAME INNER JOIN RSDB.NEW_MASTER_SUB_CHANNEL as
			// NEW_MASTER_SUB_CHANNEL ON NEW_POLICY_FACT.CHANNEL =
			// NEW_MASTER_SUB_CHANNEL.CHANNEL_NAME AND NEW_POLICY_FACT.SUB_CHANNEL =
			// NEW_MASTER_SUB_CHANNEL.SUB_CHANNEL INNER JOIN RSDB.NEW_MASTER_MAKE as
			// NEW_MASTER_MAKE ON NEW_POLICY_FACT.MAKE = NEW_MASTER_MAKE.MAKE_NAME INNER
			// JOIN RSDB.NEW_MASTER_MODEL as NEW_MASTER_MODEL ON NEW_POLICY_FACT.MAKE =
			// NEW_MASTER_MODEL.MAKE AND NEW_POLICY_FACT.MODEL_CODE =
			// NEW_MASTER_MODEL.MODEL_CODE INNER JOIN RSDB.NEW_MASTER_FIN_YEAR as
			// NEW_MASTER_FIN_YEAR ON NEW_POLICY_FACT.FIN_YEAR =
			// NEW_MASTER_FIN_YEAR.FIN_YEAR INNER JOIN RSDB.NEW_MASTER_FIN_MONTH as
			// NEW_MASTER_FIN_MONTH ON NEW_POLICY_FACT.MONTH_FLAG =
			// NEW_MASTER_FIN_MONTH.ENTRY_MONTH WHERE 1=1 and NEW_MASTER_PRODUCT.SEGMENT_NEW
			// in ('MOTOR') and NEW_POLICY_FACT.ZONE_NAME in ('South Zone') group by
			// NEW_POLICY_FACT.ZONE_NAME ,NEW_POLICY_FACT.CLUSTER_NAME
			// ,NEW_POLICY_FACT.STATE ,NEW_MASTER_STATE.STATE_CODE
			// ,NEW_POLICY_FACT.BRANCH_CODE ,NEW_MASTER_BRANCH.REVISED_BRANCH_NAME
			// ,NEW_POLICY_FACT.PRODUCT_CODE ,NEW_MASTER_PRODUCT.PRODUCT
			// ,NEW_MASTER_PRODUCT.PRODUCT_DESCRIPTION ,NEW_MASTER_PRODUCT.SEGMENT_NEW
			// ,NEW_POLICY_FACT.BUSINESS_TYPE ,NEW_POLICY_FACT.CHANNEL
			// ,NEW_POLICY_FACT.SUB_CHANNEL ,NEW_POLICY_FACT.MAKE
			// ,NEW_POLICY_FACT.MODEL_CODE ,NEW_MASTER_MODEL.MODEL_NAME
			// ,NEW_POLICY_FACT.FIN_YEAR ,NEW_POLICY_FACT.MONTH_FLAG ) CUR_ACT_CUBE GROUP BY
			// CUR_ACT_CUBE.PRODUCT,CUR_ACT_CUBE.PRODUCT_DESC,CUR_ACT_CUBE.LOB,CUR_ACT_CUBE.CHANNEL,CUR_ACT_CUBE.SUBCHANNEL,CUR_ACT_CUBE.MAKE,CUR_ACT_CUBE.MODEL_NAME,CUR_ACT_CUBE.PRODUCT_CODE,CUR_ACT_CUBE.BRANCH_NAME,CUR_ACT_CUBE.BUSINESS_TYPE,CUR_ACT_CUBE.ZONE_NAME,CUR_ACT_CUBE.CLUSTER_NAME,CUR_ACT_CUBE.STATE,CUR_ACT_CUBE.BRANCH_CODE,CUR_ACT_CUBE.STATE_CODE
			// ";
			System.out.println("START------------------------------ ");
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");
			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println("START------------------------------ ");

			// jsArray = convertToJSON(rs);

			while (rs.next()) {
				GwpResponseObject GwpResponseObj = new GwpResponseObject();
				GwpResponseObj.setLob(rs.getString(3));
				GwpResponseObj.setZone(rs.getString(11));
				GwpResponseObj.setCluster(rs.getString(12));
				GwpResponseObj.setState(rs.getString(13));
				GwpResponseObj.setBranchcode(rs.getString(14));
				GwpResponseObj.setChannel(rs.getString(4));
				GwpResponseObj.setSubchannel(rs.getString(5));
				GwpResponseObj.setProduct(rs.getString(2));
				GwpResponseObj.setProductDesc(rs.getString(1));
				GwpResponseObj.setMake(rs.getString(6));
				GwpResponseObj.setModel(rs.getString(7));
				GwpResponseObj.setMtdGWP(rs.getDouble(16));
				GwpResponseObj.setYtdGWP(rs.getDouble(17));

				GwpResponseList.add(GwpResponseObj);

			}

			System.out.println("--------------------------------------------" + GwpResponseList.size());
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
//		return jsArray.toJSONString();
		return GwpResponseList;
	}

	@RequestMapping(value = "/getPolicyLevelData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<PolicyResponse> getPolicyLevelData(
			@RequestParam(value = "paramKeyArr[]") List<String> paramKeyArr,
			@RequestParam(value = "paramValueArr[]") List<String> paramValueArr) throws SQLException {

		String dynamicQuery = "";
		for (int i = 0; i < paramKeyArr.size(); i++) {
			if (i == 0)
				dynamicQuery = " where " + paramKeyArr.get(i) + "='" + paramValueArr.get(i) + "'";
			else
				dynamicQuery += " and " + paramKeyArr.get(i) + "='" + paramValueArr.get(i) + "'";
		}

		System.out.println(paramKeyArr.size() + "<-------dynamicQuery-->" + dynamicQuery);

		List<PolicyResponse> list = new ArrayList<PolicyResponse>();
		String driverName = "org.apache.hive.jdbc.HiveDriver";
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		// replace "hive" here with the name of the user the queries should run as

		String sql = "SELECT policyno policyNo,endtno endtno,product_code productCode,month_flag monthFlag,fin_year finYear,policy_count policyCount,gwp_our_share gwpOurShare FROM rsdb.NEW_POLICY_FACT"
				+ dynamicQuery;
		System.out.println("Running: " + sql);

//		Connection con = DriverManager.getConnection("jdbc:hive2://15.206.146.26:10000/default", "rsbiadmin",
//				"Prodian123");
		Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "rsbiadmin",
				"Prodian123");
		Statement stmt = con.createStatement();
		ResultSet res = stmt.executeQuery(sql);

		// list = getPolicyResponseList(sql);

		/*
		 * ResultSetMapper<PolicyResponse> resultSetMapper = new
		 * ResultSetMapper<PolicyResponse>(); List<PolicyResponse> pojoList =
		 * resultSetMapper.mapRersultSetToObject(res, PolicyResponse.class);
		 */

		int counter = 0;
		while (res.next()) {
			counter++;
			if (counter % 10000 == 0) {
				System.out.println(paramKeyArr.size() + "counter-->" + counter);
			}
			PolicyResponse policyResponse = new PolicyResponse();
			policyResponse.setPolicyNo(res.getString(1));
			policyResponse.setEndtno(res.getString(2));
			policyResponse.setProductCode(res.getString(3));
			policyResponse.setMonthFlag(res.getString(4));
			policyResponse.setFinYear(res.getString(5));
			policyResponse.setPolicyCount(res.getDouble(6));
			policyResponse.setGwpOurShare(res.getDouble(7));
			list.add(policyResponse);
			policyResponse = null;
		}
		System.out.println(paramKeyArr.size() + "<-------list-->" + list.size());

		return list;

	}
	
//	public List<PolicyResponse> getPolicyResponseList(final String query) throws SQLException {
//		Sql2o sql2o = new Sql2o("jdbc:hive2://15.206.146.26:10000/default", "rsbiadmin", "Prodian123");
//		  try (org.sql2o.Connection con = sql2o.open()) {
//		   /* final String query =
//		        "SELECT id, name, address " +
//		        "FROM customers WHERE id = :customerId";*/
//
//		    return con.createQuery(query)
//		        .executeAndFetch(PolicyResponse.class);
//		  }
//	}
	
	
	@GetMapping("/admin/cubeJobStatus")
	public String cubeJobStatus(Model model) {
		List<CubeStatus> cubJobList = schedulerStatusRepository.getCubeJobStatusByBuildDate(UtilityFile.createSpecifiedDateFormat("dd/MM/YYYY"));
		
		model.addAttribute("cubeJobList", cubJobList);
		return "admin/dailyJobStatus";
	}
	
	@GetMapping(value="admin/cubeJobStatusByBuildDate")
	public String cubeJobStatusByBuildDate(Model model,HttpServletRequest req) {
		String buildDate = req.getParameter("buildDate") == null ? "" : req.getParameter("buildDate");
		List<CubeStatus> cubJobList = schedulerStatusRepository.getCubeJobStatusByBuildDate(buildDate);
		model.addAttribute("cubeJobList", cubJobList);
        
    return "admin/dailyJobStatus::article_type";
	}

	
	@RequestMapping(value = "/admin/getCurrentJobDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	CubeStatus getCurrentJobDetails(HttpServletRequest req) throws SQLException {
		
		String id = req.getParameter("id") == null ? "" : req.getParameter("id");
		CubeStatus curJob = schedulerStatusRepository.getOne(Integer.valueOf(id));
		return curJob;
	}
	
	@GetMapping("/admin/SchedulerInfoDetails")
	public String SchedulerInfoDetails(Model model) {
		return "admin/schedulerInfo";
	}
	
	@RequestMapping(value = "/admin/getSchdeulerTransactionDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<SchedulerInfo> getSchdeulerTransactionDetails(HttpServletRequest req) throws SQLException, ParseException {
		
		String startDate = req.getParameter("startDate") == null ? "" : req.getParameter("startDate");
		String endDate = req.getParameter("endDate") == null ? "" : req.getParameter("endDate");
		String processName = req.getParameter("processName") == null ? "" : req.getParameter("processName");
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS");
		Date parsedStartDate = (Date) dateFormat.parse(startDate + " 00:00:00:000");
		Date parsedEndDate = (Date) dateFormat.parse(endDate + " 23:59:59:999");

		Timestamp timestampStartDate = new java.sql.Timestamp(parsedStartDate.getTime());
		Timestamp timestampEndDate = new java.sql.Timestamp(parsedEndDate.getTime());
		List<SchedulerInfo> list = schedulerInfoRepository.getSchdeulerTransactionDetails(timestampStartDate,timestampEndDate,processName);
		String timeStamp = "";
		for (SchedulerInfo transaction : list) {
			Date date = new Date();
			if (transaction.getSchedulerStartDate() != null) {
				date.setTime(transaction.getSchedulerStartDate().getTime());
				timeStamp = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS").format(date);
				transaction.setStartDate(timeStamp);
			}
			if (transaction.getSchedulerEndDate() != null) {
				date.setTime(transaction.getSchedulerStartDate().getTime());
				timeStamp = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS").format(date);
				transaction.setEndDate(timeStamp);
			}
		}
		
		
		return list;
	}
	
	@GetMapping("/admin/cubeSchedulerConfig")
	public String cubeSchedulerConfig(Model model) throws IOException {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder().url(RMSConstants.KYLIN_BASE_URL+"/kylin/api/cubes").get()
				.addHeader("authorization", "Basic  QURNSU46S1lMSU4=").addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();
		String respnseText= response.body().string();
		
		System.out.println("CubesListResponse-->" + respnseText);
		
		org.json.JSONArray array = new org.json.JSONArray(respnseText);
		 
		//JSONObject object = new JSONObject(respnseText);
		//org.json.JSONArray array = object.getJSONArray("name");
		List<Cubes> cubesList = new ArrayList<>();
		List<Cubes> selectedList = cubeRepository.findAll();
		List<String> selectedCubeNames = new ArrayList<>();
		for(Cubes cubes :selectedList){
			selectedCubeNames.add(cubes.getCubeName());
		}
		System.out.println("selectedCubeNames size-->"+selectedCubeNames.size());
		int counter = 0;
		//boolean skipThisCube = false;
		for (int i=0; i < array.length(); i++) {
			if(array.getJSONObject(i).get("status").equals("READY")){
				System.out.println("currentcubename-->"+array.getJSONObject(i).get("name")+"");
				/*for(Cubes cubes :selectedList){
					if(cubes.getCubeName().equals(array.getJSONObject(i).get("name")+"")){
						skipThisCube = true;
						break;
					}
				}*/
					if(!selectedCubeNames.contains(array.getJSONObject(i).get("name")+"")){
						System.out.println("selectedlist doesnot cotains"+array.getJSONObject(i).get("name")+"");
						counter++;
						Cubes cube = new Cubes();
						cube.setCubeName(array.getJSONObject(i).get("name")+"");
						cube.setId(counter);
						cube.setStatus("N");
						cubesList.add(cube);
					}else{
						System.out.println("already added--"+array.getJSONObject(i).get("name")+"");
					}
				}
			}
		
		model.addAttribute("selectedCubeList",selectedList );
		model.addAttribute("cubeList", cubesList);
		
		
		
		//model.addAttribute("cubeJobList", cubJobList);
		return "admin/cubeSchedulerConfig";
	}
	
	
	@PostMapping("admin/insertCubeNames")
	public @ResponseBody String insertSlotValues(HttpServletRequest req) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{


		
		String cubeNames = req.getParameter("cubeNames") == null ? "" : req.getParameter("cubeNames");
		if (cubeNames.length() > 0) {
			cubeNames = cubeNames.endsWith(",") ? cubeNames.substring(0, cubeNames.length() - 1) : cubeNames;
		}
		List<Cubes> timeSlotDetailList = new ArrayList<>();
		deleteCubes();
			System.out.println("cubeNames.length()-->"+cubeNames.length());
			if (cubeNames.length() > 0) {
				String[] slotNameArr = cubeNames.split(",");
				System.out.println("slotNameArr-->"+slotNameArr.length);
				for (int i=0;i<slotNameArr.length;i++) {
					Cubes cube = new Cubes();
					cube.setCubeName(slotNameArr[i]);
					System.out.println("cubeName-->"+slotNameArr[i]);
					timeSlotDetailList.add(cube);
				}
				System.out.println(timeSlotDetailList.size());
				cubeRepository.saveAll(timeSlotDetailList);
			}

		return "Cubes Added";
	}
	
	public boolean deleteCubes() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		boolean isDeleted =false;;
		String Sql = "";
		PreparedStatement statement = null;
		
		Connection conn = null;
		try{
		//Class.forName("com.mysql.jdbc.Driver");  
		String connInstance = env.getProperty("spring.datasource.url");
		String username = env.getProperty("spring.datasource.username");
		String password = env.getProperty("spring.datasource.password");
		conn = DriverManager.getConnection(connInstance, username, password);
		
		Sql = "delete from cubes";
		statement = conn.prepareStatement(Sql);
		statement.execute();
		statement.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("isDeleted-->"+isDeleted);
		return isDeleted;
	}

	@GetMapping("/admin/oldsqoopConfig")
	public String oldscoopConfig(Model model) {
		model.addAttribute("sqoopConfig", new SqoopConfigurationRequest());
		model.addAttribute("configTableName", new ConfigTableName());
		model.addAttribute("userNames", dashboardService.getSqoopConfigUserNames());
		return "admin/sqoopConfig";
	}
	
	@GetMapping("/admin/sqoopConfig")
	public String scoopConfig(Model model) {
		model.addAttribute("sqoopConfig", new SqoopConfigurationRequest());
		model.addAttribute("configTableName", new ConfigTableName());
		model.addAttribute("userNames", dashboardService.getSqoopConfigUserNames());
		return "admin/sqoop";
	}

	@PostMapping("/admin/saveSqoopConfig")
	public String saveScoopConfig(@ModelAttribute SqoopConfigurationRequest req) {
		dashboardService.saveScoopConfig(req);
		return "redirect:/admin/sqoopConfig";
	}

	@GetMapping("/admin/getSqoopConfigByUserName")
	public @ResponseBody SqoopConfigurationRequest getSqoopConfigByUserName(@RequestParam String userName) {
		return dashboardService.getSqoopConfigByUserName(userName);
	}

	@PostMapping("/admin/editSqoopConfig")
	public String editSqoopConfig(@ModelAttribute SqoopConfigurationRequest req) {
		dashboardService.editSqoopConfig(req);
		return "redirect:/admin/sqoopConfig";
	}

	@GetMapping("/admin/deleteSqoopConfig")
	public @ResponseBody void deleteSqoopConfig(@RequestParam String userName) {
		dashboardService.deleteSqoopConfig(userName);
	}

	@GetMapping("/admin/sqoopJobStatus")
	public String sqoopJobStatus(Model model) {
		List<SqoopJobStatusResponse> sqoopJobList = sqoopJobStatusRepository.findBySqoopDate(UtilityFile.createSpecifiedDateFormat("dd/MM/YYYY"));
		
		model.addAttribute("sqoopJobList", sqoopJobList);
		return "admin/sqoopJobStatus";
	}
	
	@GetMapping(value="admin/sqoopJobStatusBySqoopDate")
	public String sqoopJobStatusBySqoopDate(Model model,HttpServletRequest req) {
		String sqoopDate = req.getParameter("sqoopDate") == null ? "" : req.getParameter("sqoopDate");
		List<SqoopJobStatusResponse> sqoopJobList = sqoopJobStatusRepository.findBySqoopDate(sqoopDate);
		model.addAttribute("sqoopJobList", sqoopJobList);
        
    return "admin/sqoopJobStatus::article_type";
	}
}
