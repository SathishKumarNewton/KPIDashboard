package com.prodian.rsgirms.dashboard.controller;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.prodian.rsgirms.constants.RMSConstants;
import com.prodian.rsgirms.dashboard.model.ChannelMaster;
import com.prodian.rsgirms.dashboard.model.ModelMaster;
import com.prodian.rsgirms.dashboard.model.MonthlyDasboardNew;
import com.prodian.rsgirms.dashboard.model.MonthlyDashboard;
import com.prodian.rsgirms.dashboard.model.MonthlyDashboardDetails;
import com.prodian.rsgirms.dashboard.model.SubChannelMaster;
import com.prodian.rsgirms.dashboard.model.UserDashboard;
import com.prodian.rsgirms.dashboard.repository.ChannelMasterRepository;
import com.prodian.rsgirms.dashboard.repository.MonthlyDashboardDetailsRepository;
import com.prodian.rsgirms.dashboard.repository.MonthlyDashboardRepository;
import com.prodian.rsgirms.dashboard.repository.SubChannelMasterRepository;
import com.prodian.rsgirms.dashboard.repository.UserDashboardRepository;
import com.prodian.rsgirms.dashboard.response.KpiFiltersResponse;
import com.prodian.rsgirms.dashboard.response.MonthFiltersResponse;
import com.prodian.rsgirms.dashboard.response.MonthlyDashboardDetailsResponse;
import com.prodian.rsgirms.dashboard.response.MonthlyDashboardFilterRequest;
import com.prodian.rsgirms.dashboard.service.KpiDashboardService;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterRequest;
import com.prodian.rsgirms.usermatrix.service.UserMatrixService;

@Controller
public class MonthlyDashboardController {

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
	private MonthlyDashboardRepository monthlyDashboardRepository;

	@Autowired
	private MonthlyDashboardDetailsRepository monthlyDashboardDetailsRepository;

	@Autowired
	private ChannelMasterRepository channelMasterRepository;

	@Autowired
	private SubChannelMasterRepository subChannelMasterRepository;

	private Logger logger = LoggerFactory.getLogger(MonthlyDashboardController.class);

	@Autowired
	private Environment env;

	@GetMapping("/monthlyDashboard")
	public ModelAndView getKpiDashBoard(@RequestParam(required = false) String currentMonth) {
		ModelAndView model = new ModelAndView("monthlyDashboard");

//		KpiFiltersResponse res = kpiDashboardService.getKpiFilters();
//		KpiFiltersResponse res = new KpiFiltersResponse();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
//		int userId = user.getId();
		model.addObject("userName", user.getName() + " " + user.getLastName());
//
//		List<Integer> dashboardIds = userDashboardRepository.findByUserId(userId).stream()
//				.map(UserDashboard::getDashboardId).collect(Collectors.toList());
//		UserMatrixMasterRequest req = new UserMatrixMasterRequest();
//		req.setDashboardId(dashboardIds);
//		UserMatrixMasterRequest filterRequest = userMatrixService.getUserMatrixChildByUserId(userId, req);
//		model.addObject("filters", filterRequest);
		List<ChannelMaster> channelMasters = channelMasterRepository.findAll();
		List<SubChannelMaster> subChannelMasters = subChannelMasterRepository.findAll();
		model.addObject("channels", channelMasters);
		model.addObject("subChannels", subChannelMasters);
		// model.addObject("makes",res.getMakeMasters());
//		List<ModelMaster> models = res.getModelMasters();
//				model.addObject("models", (models == null || models.isEmpty()) ? ""
//						: models.stream().map(ModelMaster::getModel).collect(Collectors.toSet()));
//				model.addObject("makes", (models == null || models.isEmpty()) ? ""
//						: models.stream().map(ModelMaster::getMake).collect(Collectors.toSet()));
//				model.addObject("modelGroups", (models == null || models.isEmpty()) ? ""
//						: models.stream().map(ModelMaster::getModelGroup).collect(Collectors.toSet()));
//				model.addObject("modelClassifications", (models == null || models.isEmpty()) ? ""
//						: models.stream().map(ModelMaster::getModelClassification).collect(Collectors.toSet()));

		List<MonthlyDashboardDetails> monthlyDetails = monthlyDashboardDetailsRepository.findAll();
		Set<String> geos = monthlyDetails.stream().map(MonthlyDashboardDetails::getGeo).collect(Collectors.toSet());
		Set<String> makes = monthlyDetails.stream().map(MonthlyDashboardDetails::getMake).collect(Collectors.toSet());
		Set<String> modelGroups = monthlyDetails.stream().map(MonthlyDashboardDetails::getModelGroup)
				.collect(Collectors.toSet());
		Set<String> fuelTypes = monthlyDetails.stream().map(MonthlyDashboardDetails::getFuelType)
				.collect(Collectors.toSet());
		Set<String> ncbFlags = monthlyDetails.stream().map(MonthlyDashboardDetails::getNcbFlag)
				.collect(Collectors.toSet());

		model.addObject("geos", geos);
		model.addObject("makes", makes);
		model.addObject("modelGroups", modelGroups);
		model.addObject("fuelTypes", fuelTypes);
		model.addObject("ncbFlags", ncbFlags);
		model.addObject("currentMonth", currentMonth);
		return model;
	}

//	@GetMapping("/tpUlrRateChange")
	public ModelAndView tpUlrRateChange() {
		ModelAndView model = new ModelAndView("tpUlrRateChange");

//		KpiFiltersResponse res = kpiDashboardService.getKpiFilters();
		KpiFiltersResponse res = new KpiFiltersResponse();
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
		// model.addObject("makes",res.getMakeMasters());
		List<ModelMaster> models = res.getModelMasters();
//		model.addObject("models", (models == null || models.isEmpty()) ? ""
//				: models.stream().map(ModelMaster::getModel).collect(Collectors.toSet()));
//		model.addObject("makes", (models == null || models.isEmpty()) ? ""
//				: models.stream().map(ModelMaster::getMake).collect(Collectors.toSet()));
//		model.addObject("modelGroups", (models == null || models.isEmpty()) ? ""
//				: models.stream().map(ModelMaster::getModelGroup).collect(Collectors.toSet()));
//		model.addObject("modelClassifications", (models == null || models.isEmpty()) ? ""
//				: models.stream().map(ModelMaster::getModelClassification).collect(Collectors.toSet()));

		model.addObject("userName", user.getName() + " " + user.getLastName());

		return model;
	}

	@GetMapping("/getMonthlyDashboardNewData")
	@ResponseBody
	public List<MonthlyDashboard> getMonthlyDashboardData() throws SQLException {

		return monthlyDashboardRepository.findAll();

//		Connection connection = null;
//		long startTime = System.currentTimeMillis();
//		try {
//			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
//			  Properties info = new Properties();
//			  info.put("user", "postgres");
//			  info.put("password", "Prodian!@34");
//			    connection = driverManager.connect("jdbc:postgresql://localhost:5432/kylin_db", info);
//			    System.out.println("Connection status -------------------------->"+connection);
//			    
//			    Statement stmt = connection.createStatement();
//			    String queryStr = "select * from users";
//			    System.out.println("queryStr------------------------------ "+queryStr);
//		        ResultSet rs = stmt.executeQuery(queryStr);
//		        System.out.println("START------------------------------ ");
//		        
//		        while(rs.next()) {
////		        	CubeBKpiResponseMock cubeAKpiResponse = new CubeBKpiResponseMock();
////		        	cubeAKpiResponse.setGwpOd(rs.getDouble(1));
////		        	cubeAKpiResponse.setGwpTp(rs.getDouble(2));
////		        	cubeAKpiResponse.setNwpOd(rs.getDouble(3));
////		        	cubeAKpiResponse.setNwpTp(rs.getDouble(4));
////		        	cubeAKpiResponse.setDiscountGwpOd(rs.getDouble(5));
////		        	cubeAKpiResponse.setDiscountNwpOd(rs.getDouble(6));
////		        	generalKpiResponseList.add(cubeAKpiResponse);
//		        	
//		        }
//		        
////		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
//		        //System.out.println(jsArray.toString());
//		        System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//			    
//		} catch (Exception e) {
//	    	System.out.println("Postgres initialize error, ex: " +  e);
//	    	e.printStackTrace();
//	    }finally {
//			connection.close();
//		}

	}

//	@GetMapping("/getMonthlyDashboardDetailsData")
//	@ResponseBody
//	public List<MonthlyDashboardDetailsResponse> getMonthlyDashboardDetailsData(MonthlyDashboardFilterRequest req) throws SQLException {
//		Connection connection = null;
//		long startTime = System.currentTimeMillis();
//		try {
//			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
//			Properties info = new Properties();
//			info.put("user", "postgres");
//			info.put("password", "Prodian!@34");
//			connection = driverManager
//					.connect("jdbc:postgresql://" + RMSConstants.POSTGRES_BASE_IP_AND_PORT + "/kylin_db", info);
//			System.out.println("Connection status -------------------------->" + connection);
//
//			Statement stmt = connection.createStatement();
//			String queryStr = "select \r\n" + "business_type,\r\n"
//					+ "sum(cur_mon_gwp_od) cur_mon_gwp_od,sum(cur_mon_gwp_tp) cur_mon_gwp_tp,sum(cur_mon_gwp_od)+sum(cur_mon_gwp_tp) tot_cur_mon_gwp,\r\n"
//					+ "sum(pre_mon_gwp_od) pre_mon_gwp_od,sum(pre_mon_gwp_tp) pre_mon_gwp_tp,sum(pre_mon_gwp_od)+sum(pre_mon_gwp_tp) tot_pre_mon_gwp,\r\n"
//					+ "sum(ytm_gwp_od) ytm_gwp_od,sum(ytm_gwp_tp) ytm_gwp_tp,sum(ytm_gwp_od)+sum(ytm_gwp_tp) tot_ytm_gwp,\r\n"
//					+ "sum(pytm_gwp_od) pytm_gwp_od,sum(pytm_gwp_tp) pytm_gwp_tp,sum(pytm_gwp_od)+sum(pytm_gwp_tp) tot_pytm_gwp,\r\n"
//					+ "sum(cur_mon_policy_od) cur_mon_policy_od,sum(cur_mon_policy_tp) cur_mon_policy_tp,sum(cur_mon_policy_od)+sum(cur_mon_policy_tp) tot_cur_mon_policy,\r\n"
//					+ "sum(pre_mon_policy_od) pre_mon_policy_od,sum(pre_mon_policy_tp) pre_mon_policy_tp,sum(pre_mon_policy_od)+sum(pre_mon_policy_tp) tot_pre_mon_policy,\r\n"
//					+ "sum(ytm_policy_od) ytm_policy_od,sum(ytm_policy_tp) ytm_policy_tp,sum(ytm_policy_od)+sum(ytm_policy_tp) tot_ytm_policy,\r\n"
//					+ "sum(pytm_policy_od) pytm_policy_od,sum(pytm_policy_tp) pytm_policy_tp,sum(pytm_policy_od)+sum(pytm_policy_tp) tot_pytm_policy\r\n"
//					+ "from(\r\n" + "select \r\n" + "business_type,channel,sub_channel,make,model_group,geo,\r\n"
//					+ "(case when inception_month=12 and inception_year=2020 then gwp_od else 0 end) cur_mon_gwp_od,\r\n"
//					+ "(case when inception_month=12 and inception_year=2020 then gwp_tp else 0 end) cur_mon_gwp_tp,\r\n"
//					+ "(case when inception_month=11 and inception_year=2020 then gwp_od else 0 end) pre_mon_gwp_od,\r\n"
//					+ "(case when inception_month=11 and inception_year=2020 then gwp_tp else 0 end) pre_mon_gwp_tp,\r\n"
//					+ "(case when ((inception_month>=4 and inception_year=2020) \r\n"
//					+ "			or (inception_month=11 and inception_year=2020)) then gwp_od else 0 end) ytm_gwp_od,\r\n"
//					+ "(case when ((inception_month>=4 and inception_year=2020) \r\n"
//					+ "			or (inception_month=11 and inception_year=2020)) then gwp_tp else 0 end) ytm_gwp_tp,\r\n"
//					+ "(case when ((inception_month>=4 and inception_year=2019) \r\n"
//					+ "		or (inception_month=11 and inception_year=2019)) then gwp_od else 0 end) pytm_gwp_od,\r\n"
//					+ "(case when ((inception_month>=4 and inception_year=2019) \r\n"
//					+ "		or (inception_month=11 and inception_year=2019)) then gwp_tp else 0 end) pytm_gwp_tp,\r\n"
//					+ "(case when inception_month=12 and inception_year=2020 then policy_od else 0 end) cur_mon_policy_od,\r\n"
//					+ "(case when inception_month=12 and inception_year=2020 then policy_tp else 0 end) cur_mon_policy_tp,\r\n"
//					+ "(case when inception_month=11 and inception_year=2020 then policy_od else 0 end) pre_mon_policy_od,\r\n"
//					+ "(case when inception_month=11 and inception_year=2020 then policy_tp else 0 end) pre_mon_policy_tp,\r\n"
//					+ "(case when ((inception_month>=4 and inception_year=2020) \r\n"
//					+ "		or (inception_month=11 and inception_year=2020)) then policy_od else 0 end) ytm_policy_od,\r\n"
//					+ "(case when ((inception_month>=4 and inception_year=2020) \r\n"
//					+ "		or (inception_month=11 and inception_year=2020)) then policy_tp else 0 end) ytm_policy_tp,\r\n"
//					+ "(case when ((inception_month>=4 and inception_year=2019) \r\n"
//					+ "		or (inception_month=11 and inception_year=2019)) then policy_od else 0 end) pytm_policy_od,\r\n"
//					+ "(case when ((inception_month>=4 and inception_year=2019) \r\n"
//					+ "		or (inception_month=11 and inception_year=2019)) then policy_tp else 0 end) pytm_policy_tp\r\n"
//					+ "from\r\n" + "monthly_dashboard_details_new mo\r\n" + ")mon\r\n where 1=1 " ;
//			
//			if(req!=null && req.getChannel()!=null && !req.getChannel().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getChannel().size(); i++) {
//					vals += "'" + req.getChannel().get(i).trim() + "'";
//					if (i != req.getChannel().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and  TRIM(channel) in (" + vals + ")";
//			}
//			
//			if(req!=null && req.getSubChannel()!=null && !req.getSubChannel().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getSubChannel().size(); i++) {
//					vals += "'" + req.getSubChannel().get(i).trim() + "'";
//					if (i != req.getSubChannel().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and TRIM(sub_channel) in (" + vals + ")";
//			}
//			
//			if(req!=null && req.getMake()!=null && !req.getMake().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getMake().size(); i++) {
//					vals += "'" + req.getMake().get(i).trim() + "'";
//					if (i != req.getMake().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and TRIM(make) in (" + vals + ")";
//			}
//			
//			if(req!=null && req.getModelGroup()!=null && !req.getModelGroup().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getModelGroup().size(); i++) {
//					vals += "'" + req.getModelGroup().get(i).trim() + "'";
//					if (i != req.getModelGroup().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and TRIM(model_group) in (" + vals + ")";
//			}
//			
//			if(req!=null && req.getGeo()!=null && !req.getGeo().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getGeo().size(); i++) {
//					vals += "'" + req.getGeo().get(i).trim() + "'";
//					if (i != req.getGeo().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and TRIM(geo) in (" + vals + ")";
//			}
//					
//			queryStr += "\r\n" + "group by \r\n"	+ "business_type";
//			
//			System.out.println("queryStr------------------------------ " + queryStr);
//			ResultSet rs = stmt.executeQuery(queryStr);
//			System.out.println("START------------------------------ ");
//			List<MonthlyDashboardDetailsResponse> monthlyResponse = new ArrayList<>();
//			while (rs.next()) {
//
//				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
//
//				res.setBusinessType(rs.getString(1));
//				res.setDetailType("CM");
//				res.setGwpOd(rs.getBigDecimal(2));
//				res.setGwpTp(rs.getBigDecimal(3));
//				res.setTotalGwp(rs.getBigDecimal(4));
//				res.setPolicyOd(rs.getLong(14));
//				res.setPolicyTp(rs.getLong(15));
//				res.setTotalPolicy(rs.getLong(16));
//
//				monthlyResponse.add(res);
//
//				res = new MonthlyDashboardDetailsResponse();
//
//				res.setBusinessType(rs.getString(1));
//				res.setDetailType("PM");
//				res.setGwpOd(rs.getBigDecimal(5));
//				res.setGwpTp(rs.getBigDecimal(6));
//				res.setTotalGwp(rs.getBigDecimal(7));
//				res.setPolicyOd(rs.getLong(17));
//				res.setPolicyTp(rs.getLong(18));
//				res.setTotalPolicy(rs.getLong(19));
//
//				monthlyResponse.add(res);
//
//				res = new MonthlyDashboardDetailsResponse();
//
//				res.setBusinessType(rs.getString(1));
//				res.setDetailType("YTM");
//				res.setGwpOd(rs.getBigDecimal(8));
//				res.setGwpTp(rs.getBigDecimal(9));
//				res.setTotalGwp(rs.getBigDecimal(10));
//				res.setPolicyOd(rs.getLong(20));
//				res.setPolicyTp(rs.getLong(21));
//				res.setTotalPolicy(rs.getLong(22));
//
//				monthlyResponse.add(res);
//
//				res = new MonthlyDashboardDetailsResponse();
//
//				res.setBusinessType(rs.getString(1));
//				res.setDetailType("PYTM");
//				res.setGwpOd(rs.getBigDecimal(11));
//				res.setGwpTp(rs.getBigDecimal(12));
//				res.setTotalGwp(rs.getBigDecimal(13));
//				res.setPolicyOd(rs.getLong(23));
//				res.setPolicyTp(rs.getLong(24));
//				res.setTotalPolicy(rs.getLong(25));
//
//				monthlyResponse.add(res);
//
//			}
//
//			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//
//			queryStr = "select \r\n" + "--business_type,\r\n"
//					+ "sum(cur_mon_gwp_od) cur_mon_gwp_od,sum(cur_mon_gwp_tp) cur_mon_gwp_tp,sum(cur_mon_gwp_od)+sum(cur_mon_gwp_tp) tot_cur_mon_gwp,\r\n"
//					+ "sum(pre_mon_gwp_od) pre_mon_gwp_od,sum(pre_mon_gwp_tp) pre_mon_gwp_tp,sum(pre_mon_gwp_od)+sum(pre_mon_gwp_tp) tot_pre_mon_gwp,\r\n"
//					+ "sum(ytm_gwp_od) ytm_gwp_od,sum(ytm_gwp_tp) ytm_gwp_tp,sum(ytm_gwp_od)+sum(ytm_gwp_tp) tot_ytm_gwp,\r\n"
//					+ "sum(pytm_gwp_od) pytm_gwp_od,sum(pytm_gwp_tp) pytm_gwp_tp,sum(pytm_gwp_od)+sum(pytm_gwp_tp) tot_pytm_gwp,\r\n"
//					+ "sum(cur_mon_policy_od) cur_mon_policy_od,sum(cur_mon_policy_tp) cur_mon_policy_tp,sum(cur_mon_policy_od)+sum(cur_mon_policy_tp) tot_cur_mon_policy,\r\n"
//					+ "sum(pre_mon_policy_od) pre_mon_policy_od,sum(pre_mon_policy_tp) pre_mon_policy_tp,sum(pre_mon_policy_od)+sum(pre_mon_policy_tp) tot_pre_mon_policy,\r\n"
//					+ "sum(ytm_policy_od) ytm_policy_od,sum(ytm_policy_tp) ytm_policy_tp,sum(ytm_policy_od)+sum(ytm_policy_tp) tot_ytm_policy,\r\n"
//					+ "sum(pytm_policy_od) pytm_policy_od,sum(pytm_policy_tp) pytm_policy_tp,sum(pytm_policy_od)+sum(pytm_policy_tp) tot_pytm_policy\r\n"
//					+ "from(\r\n" + "select \r\n" + "business_type,channel,sub_channel,make,model_group,geo,\r\n"
//					+ "(case when inception_month=12 and inception_year=2020 then gwp_od else 0 end) cur_mon_gwp_od,\r\n"
//					+ "(case when inception_month=12 and inception_year=2020 then gwp_tp else 0 end) cur_mon_gwp_tp,\r\n"
//					+ "(case when inception_month=11 and inception_year=2020 then gwp_od else 0 end) pre_mon_gwp_od,\r\n"
//					+ "(case when inception_month=11 and inception_year=2020 then gwp_tp else 0 end) pre_mon_gwp_tp,\r\n"
//					+ "(case when  inception_year=2020 then gwp_od_ytm else 0 end) ytm_gwp_od,\r\n"
//					+ "(case when  inception_year=2020 then gwp_tp_ytm else 0 end) ytm_gwp_tp,\r\n"
//					+ "(case when  inception_year=2019 then gwp_od_pytm else 0 end) pytm_gwp_od,\r\n"
//					+ "(case when  inception_year=2019 then gwp_tp_pytm else 0 end) pytm_gwp_tp,\r\n"
//					+ "(case when inception_month=12 and inception_year=2020 then policy_od else 0 end) cur_mon_policy_od,\r\n"
//					+ "(case when inception_month=12 and inception_year=2020 then policy_tp else 0 end) cur_mon_policy_tp,\r\n"
//					+ "(case when inception_month=11 and inception_year=2020 then policy_od else 0 end) pre_mon_policy_od,\r\n"
//					+ "(case when inception_month=11 and inception_year=2020 then policy_tp else 0 end) pre_mon_policy_tp,\r\n"
//					+ "(case when inception_year=2020 then policy_od_ytm else 0 end) ytm_policy_od,\r\n"
//					+ "(case when inception_year=2020 then policy_tp_ytm else 0 end) ytm_policy_tp,\r\n"
//					+ "(case when inception_year=2019 then policy_od_ytm else 0 end) pytm_policy_od,\r\n"
//					+ "(case when inception_year=2019 then policy_tp_pytm else 0 end) pytm_policy_tp\r\n" + "from\r\n"
//					+ "monthly_dashboard_details_new mo\r\n" + ")mon where 1=1";
//			
//			if(req!=null && req.getChannel()!=null && !req.getChannel().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getChannel().size(); i++) {
//					vals += "'" + req.getChannel().get(i).trim() + "'";
//					if (i != req.getChannel().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and  TRIM(channel) in (" + vals + ")";
//			}
//			
//			if(req!=null && req.getSubChannel()!=null && !req.getSubChannel().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getSubChannel().size(); i++) {
//					vals += "'" + req.getSubChannel().get(i).trim() + "'";
//					if (i != req.getSubChannel().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and TRIM(sub_channel) in (" + vals + ")";
//			}
//			
//			if(req!=null && req.getMake()!=null && !req.getMake().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getMake().size(); i++) {
//					vals += "'" + req.getMake().get(i).trim() + "'";
//					if (i != req.getMake().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and TRIM(make) in (" + vals + ")";
//			}
//			
//			if(req!=null && req.getModelGroup()!=null && !req.getModelGroup().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getModelGroup().size(); i++) {
//					vals += "'" + req.getModelGroup().get(i).trim() + "'";
//					if (i != req.getModelGroup().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and TRIM(model_group) in (" + vals + ")";
//			}
//			
//			if(req!=null && req.getGeo()!=null && !req.getGeo().isEmpty()) {
//				String vals = "";
//        		for (int i = 0; i < req.getGeo().size(); i++) {
//					vals += "'" + req.getGeo().get(i).trim() + "'";
//					if (i != req.getGeo().size() - 1) {
//						vals += ",";
//					}
//				}
//	        	queryStr += " and TRIM(geo) in (" + vals + ")";
//			}
//
//			System.out.println("queryStr------------------------------ " + queryStr);
//			rs = stmt.executeQuery(queryStr);
//			System.out.println("START------------------------------ ");
//
//			while (rs.next()) {
//
//				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
//
//				res.setBusinessType("All");
//				res.setDetailType("CM");
//				res.setGwpOd(rs.getBigDecimal(1));
//				res.setGwpTp(rs.getBigDecimal(2));
//				res.setTotalGwp(rs.getBigDecimal(3));
//				res.setPolicyOd(rs.getLong(13));
//				res.setPolicyTp(rs.getLong(14));
//				res.setTotalPolicy(rs.getLong(15));
//
//				monthlyResponse.add(res);
//
//				res = new MonthlyDashboardDetailsResponse();
//
//				res.setBusinessType("All");
//				res.setDetailType("PM");
//				res.setGwpOd(rs.getBigDecimal(4));
//				res.setGwpTp(rs.getBigDecimal(5));
//				res.setTotalGwp(rs.getBigDecimal(6));
//				res.setPolicyOd(rs.getLong(16));
//				res.setPolicyTp(rs.getLong(17));
//				res.setTotalPolicy(rs.getLong(18));
//
//				monthlyResponse.add(res);
//
//				res = new MonthlyDashboardDetailsResponse();
//
//				res.setBusinessType("All");
//				res.setDetailType("YTM");
//				res.setGwpOd(rs.getBigDecimal(7));
//				res.setGwpTp(rs.getBigDecimal(8));
//				res.setTotalGwp(rs.getBigDecimal(9));
//				res.setPolicyOd(rs.getLong(19));
//				res.setPolicyTp(rs.getLong(20));
//				res.setTotalPolicy(rs.getLong(21));
//
//				monthlyResponse.add(res);
//
//				res = new MonthlyDashboardDetailsResponse();
//
//				res.setBusinessType("All");
//				res.setDetailType("PYTM");
//				res.setGwpOd(rs.getBigDecimal(10));
//				res.setGwpTp(rs.getBigDecimal(11));
//				res.setTotalGwp(rs.getBigDecimal(12));
//				res.setPolicyOd(rs.getLong(22));
//				res.setPolicyTp(rs.getLong(23));
//				res.setTotalPolicy(rs.getLong(24));
//
//				monthlyResponse.add(res);
//
//			}
//			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//			return monthlyResponse;
//		} catch (Exception e) {
//			System.out.println("Postgres initialize error, ex: " + e);
//			e.printStackTrace();
//		} finally {
//			connection.close();
//		}
//		return null;
//	}

	@GetMapping("/getMonthlyDashboardDetailsData")
	@ResponseBody
	public List<MonthlyDashboardDetailsResponse> getMonthlyDashboardNewData(MonthlyDashboardFilterRequest req)
			throws SQLException {

		Connection connection = null;
		Statement stmt = null;
		long startTime = System.currentTimeMillis();
		List<MonthlyDashboardDetailsResponse> monthlyResponse = new ArrayList<>();
		try {
			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", env.getProperty("spring.datasource.username"));
			info.put("password", env.getProperty("spring.datasource.password"));
			connection = driverManager
					.connect("jdbc:postgresql://" + RMSConstants.POSTGRES_BASE_IP_AND_PORT + "/kylin_db", info);
			System.out.println("Connection status -------------------------->" + connection);

			stmt = connection.createStatement();
			String queryStr = "select business_type,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,sum(gwp_od_ytm) gwp_od_ytm,sum(gwp_tp_ytm) gwp_tp_ytm,"
					// + " sum(gwp_od_pytm) gwp_od_pytm, "
					// + " sum(gwp_tp_pytm) gwp_tp_pytm, "
					+ " sum(policy_od) policy_od,sum(policy_tp) policy_tp,sum(policy_od_ytm) policy_od_ytm,sum(policy_tp_ytm) policy_tp_ytm,"
					// + " sum(policy_od_pytm) policy_od_pytm ,sum(policy_tp_pytm) policy_tp_pytm ,"
					+ " sum(gwp_od)+sum(gwp_tp) total_gwp,sum(policy_od) +sum(policy_tp) total_policy, "
					+ " sum(gwp_od_ytm)+sum(gwp_tp) total_gwp_ytm, sum(policy_od_ytm) +sum(policy_tp) total_policy_ytm"
					+ " from monthly_dashboard_grouped_new where " + " inception_month=202002 ";

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by business_type ";
			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			int updateStatus = 0;
			int in = 0;

			while (rs.next()) {
				in++;

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				System.out.println("rs.getString(1)-->" + rs.getString(1));
				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setGwpOd(rs.getDouble(2));
					res.setGwpTp(rs.getDouble(3));
					res.setTotalGwp(rs.getDouble(10));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					res.setTotalPolicy(rs.getDouble(11));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setGwpOd(rs.getDouble(4));
					res.setGwpTp(rs.getDouble(5));
					res.setTotalGwp(rs.getDouble(12));
					res.setPolicyOd(rs.getDouble(8));
					res.setPolicyTp(rs.getDouble(9));
					res.setTotalPolicy(rs.getDouble(13));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {
					System.out.println();

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setGwpOd(rs.getDouble(2));
					res.setGwpTp(rs.getDouble(3));
					res.setTotalGwp(rs.getDouble(10));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					res.setTotalPolicy(rs.getDouble(11));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setGwpOd(rs.getDouble(4));
					res.setGwpTp(rs.getDouble(5));
					res.setTotalGwp(rs.getDouble(12));
					res.setPolicyOd(rs.getDouble(8));
					res.setPolicyTp(rs.getDouble(9));
					res.setTotalPolicy(rs.getDouble(13));

					monthlyResponse.add(res);

//					md.setRollOverGwpOdCm(rs.getDouble(2));
//					md.setRollOverGwpTpCm(rs.getDouble(3));
//					// md.setRollOverGwpOdPm(rs.getDouble(2));
//					// md.setRollOverGwpTpPm(rs.getDouble(2));
//					md.setRollOverGwpOdYtm(rs.getDouble(4));
//					md.setRollOverGwpTpYtm(rs.getDouble(5));
//					md.setRollOverGwpOdPytm(rs.getDouble(6));
//					md.setRollOverGwpTpPytm(rs.getDouble(7));
//					md.setRollOverPolicyOdCm(rs.getDouble(8));
//					md.setRollOverPolicyTpCm(rs.getDouble(9));
//					// md.setRollOverPolicyOdPm(rs.getDouble(10));
//					// md.setRollOverPolicyTpPm(rs.getDouble(11));
//					md.setRollOverPolicyOdYtm(rs.getDouble(10));
//					md.setRollOverPolicyTpYtm(rs.getDouble(11));
//					md.setRollOverPolicyOdPytm(rs.getDouble(12));
//					md.setRollOverPolicyTpPytm(rs.getDouble(13));
				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setGwpOd(rs.getDouble(2));
					res.setGwpTp(rs.getDouble(3));
					res.setTotalGwp(rs.getDouble(10));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					res.setTotalPolicy(rs.getDouble(11));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setGwpOd(rs.getDouble(4));
					res.setGwpTp(rs.getDouble(5));
					res.setTotalGwp(rs.getDouble(12));
					res.setPolicyOd(rs.getDouble(8));
					res.setPolicyTp(rs.getDouble(9));
					res.setTotalPolicy(rs.getDouble(13));

					monthlyResponse.add(res);

//					md.setRenewalGwpOdCm(rs.getDouble(2));
//					md.setRenewalGwpTpCm(rs.getDouble(3));
//					// md.setRenewalGwpOdPm(rs.getDouble(2));
//					// md.setRenewalGwpTpPm(rs.getDouble(2));
//					md.setRenewalGwpOdYtm(rs.getDouble(4));
//					md.setRenewalGwpTpYtm(rs.getDouble(5));
//					md.setRenewalGwpOdPytm(rs.getDouble(6));
//					md.setRenewalGwpTpPytm(rs.getDouble(7));
//					md.setRenewalPolicyOdCm(rs.getDouble(8));
//					md.setRenewalPolicyTpCm(rs.getDouble(9));
//					// md.setRenewalPolicyOdPm(rs.getDouble(10));
//					// md.setRenewalPolicyTpPm(rs.getDouble(11));
//					md.setRenewalPolicyOdYtm(rs.getDouble(10));
//					md.setRenewalPolicyTpYtm(rs.getDouble(11));
//					md.setRenewalPolicyOdPytm(rs.getDouble(12));
//					md.setRenewalPolicyTpPytm(rs.getDouble(13));
				}

				// connection.commit();
			}
			rs.close();

			queryStr = "select business_type,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,sum(gwp_od_ytm) gwp_od_ytm,sum(gwp_tp_ytm) gwp_tp_ytm,"
					// + " sum(gwp_od_pytm) gwp_od_pytm, "
					// + " sum(gwp_tp_pytm) gwp_tp_pytm, "
					+ " sum(policy_od) policy_od,sum(policy_tp) policy_tp,sum(policy_od_ytm) policy_od_ytm,sum(policy_tp_ytm) policy_tp_ytm,"
					// + " sum(policy_od_pytm) policy_od_pytm ,sum(policy_tp_pytm) policy_tp_pytm ,"
					+ " sum(gwp_od)+sum(gwp_tp) total_gwp,sum(policy_od) +sum(policy_tp) total_policy, "
					+ " sum(gwp_od_ytm)+sum(gwp_tp) total_gwp_ytm, sum(policy_od_ytm) +sum(policy_tp) total_policy_ytm"
					+ " from monthly_dashboard_grouped_new where " + " inception_month=202001 ";
			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by business_type ";
			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				in++;

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				System.out.println("rs.getString(1)-->" + rs.getString(1));
				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setGwpOd(rs.getDouble(2));
					res.setGwpTp(rs.getDouble(3));
					res.setTotalGwp(rs.getDouble(10));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					res.setTotalPolicy(rs.getDouble(11));

					monthlyResponse.add(res);

//					res = new MonthlyDashboardDetailsResponse();
//
//					res.setBusinessType(rs.getString(1));
//					res.setDetailType("PYTM");
//					res.setGwpOd(rs.getDouble(4));
//					res.setGwpTp(rs.getDouble(5));
//					res.setTotalGwp(rs.getDouble(12));
//					res.setPolicyOd(rs.getDouble(8));
//					res.setPolicyTp(rs.getDouble(9));
//					res.setTotalPolicy(rs.getDouble(13));
//
//					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {
					System.out.println();

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setGwpOd(rs.getDouble(2));
					res.setGwpTp(rs.getDouble(3));
					res.setTotalGwp(rs.getDouble(10));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					res.setTotalPolicy(rs.getDouble(11));

					monthlyResponse.add(res);

//					res = new MonthlyDashboardDetailsResponse();
//
//					res.setBusinessType(rs.getString(1));
//					res.setDetailType("PYTM");
//					res.setGwpOd(rs.getDouble(4));
//					res.setGwpTp(rs.getDouble(5));
//					res.setTotalGwp(rs.getDouble(12));
//					res.setPolicyOd(rs.getDouble(8));
//					res.setPolicyTp(rs.getDouble(9));
//					res.setTotalPolicy(rs.getDouble(13));
//
//					monthlyResponse.add(res);

//					md.setRollOverGwpOdCm(rs.getDouble(2));
//					md.setRollOverGwpTpCm(rs.getDouble(3));
//					// md.setRollOverGwpOdPm(rs.getDouble(2));
//					// md.setRollOverGwpTpPm(rs.getDouble(2));
//					md.setRollOverGwpOdYtm(rs.getDouble(4));
//					md.setRollOverGwpTpYtm(rs.getDouble(5));
//					md.setRollOverGwpOdPytm(rs.getDouble(6));
//					md.setRollOverGwpTpPytm(rs.getDouble(7));
//					md.setRollOverPolicyOdCm(rs.getDouble(8));
//					md.setRollOverPolicyTpCm(rs.getDouble(9));
//					// md.setRollOverPolicyOdPm(rs.getDouble(10));
//					// md.setRollOverPolicyTpPm(rs.getDouble(11));
//					md.setRollOverPolicyOdYtm(rs.getDouble(10));
//					md.setRollOverPolicyTpYtm(rs.getDouble(11));
//					md.setRollOverPolicyOdPytm(rs.getDouble(12));
//					md.setRollOverPolicyTpPytm(rs.getDouble(13));
				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setGwpOd(rs.getDouble(2));
					res.setGwpTp(rs.getDouble(3));
					res.setTotalGwp(rs.getDouble(10));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					res.setTotalPolicy(rs.getDouble(11));

					monthlyResponse.add(res);

//					res = new MonthlyDashboardDetailsResponse();
//
//					res.setBusinessType(rs.getString(1));
//					res.setDetailType("PYTM");
//					res.setGwpOd(rs.getDouble(4));
//					res.setGwpTp(rs.getDouble(5));
//					res.setTotalGwp(rs.getDouble(12));
//					res.setPolicyOd(rs.getDouble(8));
//					res.setPolicyTp(rs.getDouble(9));
//					res.setTotalPolicy(rs.getDouble(13));
//
//					monthlyResponse.add(res);

//					md.setRenewalGwpOdCm(rs.getDouble(2));
//					md.setRenewalGwpTpCm(rs.getDouble(3));
//					// md.setRenewalGwpOdPm(rs.getDouble(2));
//					// md.setRenewalGwpTpPm(rs.getDouble(2));
//					md.setRenewalGwpOdYtm(rs.getDouble(4));
//					md.setRenewalGwpTpYtm(rs.getDouble(5));
//					md.setRenewalGwpOdPytm(rs.getDouble(6));
//					md.setRenewalGwpTpPytm(rs.getDouble(7));
//					md.setRenewalPolicyOdCm(rs.getDouble(8));
//					md.setRenewalPolicyTpCm(rs.getDouble(9));
//					// md.setRenewalPolicyOdPm(rs.getDouble(10));
//					// md.setRenewalPolicyTpPm(rs.getDouble(11));
//					md.setRenewalPolicyOdYtm(rs.getDouble(10));
//					md.setRenewalPolicyTpYtm(rs.getDouble(11));
//					md.setRenewalPolicyOdPytm(rs.getDouble(12));
//					md.setRenewalPolicyTpPytm(rs.getDouble(13));
				}

				// connection.commit();
			}
			rs.close();

			////////////////////////////

			queryStr = "select \r\n" + "business_type,\r\n"
					+ "sum(gwp_od_ytm) gwp_od_ytm,sum(gwp_tp_ytm) gwp_tp_ytm,\r\n"
					+ "sum(policy_od_ytm) policy_od_ytm,sum(policy_tp_ytm) policy_tp_ytm,\r\n"
					+ "sum(gwp_od_ytm)+sum(gwp_tp_ytm) total_gwp_ytm, \r\n"
					+ "sum(policy_od_ytm) +sum(policy_tp_ytm) total_policy_ytm\r\n"
					+ "from monthly_dashboard_grouped_new where   inception_month=201902";
			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by business_type ";
			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				in++;

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				System.out.println("rs.getString(1)-->" + rs.getString(1));
				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setGwpOd(rs.getDouble(2));
					res.setGwpTp(rs.getDouble(3));
					res.setTotalGwp(rs.getDouble(6));
					res.setPolicyOd(rs.getDouble(4));
					res.setPolicyTp(rs.getDouble(5));
					res.setTotalPolicy(rs.getDouble(7));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setGwpOd(rs.getDouble(2));
					res.setGwpTp(rs.getDouble(3));
					res.setTotalGwp(rs.getDouble(6));
					res.setPolicyOd(rs.getDouble(4));
					res.setPolicyTp(rs.getDouble(5));
					res.setTotalPolicy(rs.getDouble(7));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setGwpOd(rs.getDouble(2));
					res.setGwpTp(rs.getDouble(3));
					res.setTotalGwp(rs.getDouble(6));
					res.setPolicyOd(rs.getDouble(4));
					res.setPolicyTp(rs.getDouble(5));
					res.setTotalPolicy(rs.getDouble(7));

					monthlyResponse.add(res);

					// connection.commit();
				}
			}
			rs.close();

			////////////////////////////////////////////////////////////////
			queryStr = "select sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,sum(gwp_od_ytm) gwp_od_ytm,sum(gwp_tp_ytm) gwp_tp_ytm,"
					+ " sum(policy_od) policy_od,sum(policy_tp) policy_tp,sum(policy_od_ytm) policy_od_ytm,sum(policy_tp_ytm) policy_tp_ytm,"
					+ " sum(gwp_od)+sum(gwp_tp) total_gwp,sum(policy_od) +sum(policy_tp) total_policy, "
					+ " sum(gwp_od_ytm)+sum(gwp_tp) total_gwp_ytm, sum(policy_od_ytm) +sum(policy_tp) total_policy_ytm"
					+ " from monthly_dashboard_grouped_new where " + " inception_month=202002 ";

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				in++;

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				System.out.println("rs.getString(1)-->" + rs.getString(1));

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("CM");
				res.setGwpOd(rs.getDouble(1));
				res.setGwpTp(rs.getDouble(2));
				res.setTotalGwp(rs.getDouble(9));
				res.setPolicyOd(rs.getDouble(5));
				res.setPolicyTp(rs.getDouble(6));
				res.setTotalPolicy(rs.getDouble(10));

				monthlyResponse.add(res);

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("YTM");
				res.setGwpOd(rs.getDouble(3));
				res.setGwpTp(rs.getDouble(4));
				res.setTotalGwp(rs.getDouble(11));
				res.setPolicyOd(rs.getDouble(7));
				res.setPolicyTp(rs.getDouble(8));
				res.setTotalPolicy(rs.getDouble(12));

				monthlyResponse.add(res);

				// connection.commit();
			}
			rs.close();

			queryStr = "select sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,sum(gwp_od_ytm) gwp_od_ytm,sum(gwp_tp_ytm) gwp_tp_ytm,"
					// + " sum(gwp_od_pytm) gwp_od_pytm, "
					// + " sum(gwp_tp_pytm) gwp_tp_pytm, "
					+ " sum(policy_od) policy_od,sum(policy_tp) policy_tp,sum(policy_od_ytm) policy_od_ytm,sum(policy_tp_ytm) policy_tp_ytm,"
					// + " sum(policy_od_pytm) policy_od_pytm ,sum(policy_tp_pytm) policy_tp_pytm ,"
					+ " sum(gwp_od)+sum(gwp_tp) total_gwp,sum(policy_od) +sum(policy_tp) total_policy, "
					+ " sum(gwp_od_ytm)+sum(gwp_tp) total_gwp_ytm, sum(policy_od_ytm) +sum(policy_tp) total_policy_ytm"
					+ " from monthly_dashboard_grouped_new where " + " inception_month=202001 ";
			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by business_type ";
			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				in++;

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				System.out.println("rs.getString(1)-->" + rs.getString(1));

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("PM");
				res.setGwpOd(rs.getDouble(1));
				res.setGwpTp(rs.getDouble(2));
				res.setTotalGwp(rs.getDouble(9));
				res.setPolicyOd(rs.getDouble(7));
				res.setPolicyTp(rs.getDouble(6));
				res.setTotalPolicy(rs.getDouble(10));

				monthlyResponse.add(res);

//				res = new MonthlyDashboardDetailsResponse();
//
//				res.setBusinessType("All");
//				res.setDetailType("PYTM");
//				res.setGwpOd(rs.getDouble(3));
//				res.setGwpTp(rs.getDouble(4));
//				res.setTotalGwp(rs.getDouble(11));
//				res.setPolicyOd(rs.getDouble(7));
//				res.setPolicyTp(rs.getDouble(8));
//				res.setTotalPolicy(rs.getDouble(12));
//
//				monthlyResponse.add(res);

				// connection.commit();
			}
			rs.close();

			queryStr = "select \r\n" + "sum(gwp_od_ytm) gwp_od_ytm,sum(gwp_tp_ytm) gwp_tp_ytm,\r\n"
					+ "sum(policy_od_ytm) policy_od_ytm,sum(policy_tp_ytm) policy_tp_ytm,\r\n"
					+ "sum(gwp_od_ytm)+sum(gwp_tp_ytm) total_gwp_ytm, \r\n"
					+ "sum(policy_od_ytm) +sum(policy_tp_ytm) total_policy_ytm\r\n"
					+ "from monthly_dashboard_grouped_new where   inception_month=201902";
			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				in++;

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				System.out.println("rs.getString(1)-->" + rs.getString(1));

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("PYTM");
				res.setGwpOd(rs.getDouble(1));
				res.setGwpTp(rs.getDouble(2));
				res.setTotalGwp(rs.getDouble(5));
				res.setPolicyOd(rs.getDouble(3));
				res.setPolicyTp(rs.getDouble(4));
				res.setTotalPolicy(rs.getDouble(6));

				monthlyResponse.add(res);

			}
			rs.close();

			//////////////////////// freq sev cm,ytm

			queryStr = "select business_type,claim_type,\r\n" + "sum(theft_freq_expected) theft_freq,\r\n"
					+ "sum(cat_freq_expected) cat_freq,\r\n" + "sum(others_freq_expected) others_freq,\r\n"
					+ "sum(theft_freq_expected_ytm) theft_freq_ytm,\r\n"
					+ "sum(cat_freq_expected_ytm) cat_freq_ytm,\r\n"
					+ "sum(others_freq_expected_ytm) others_freq_ytm,\r\n"
					+ "sum(theft_claim_cost_expected) theft_sev,\r\n" + "sum(cat_claim_cost_expected) cat_sev,\r\n"
					+ "sum(others_claim_cost_expected) others_sev,\r\n" + "sum(theft_claim_cost_ytm) theft_sev_ytm,\r\n"
					+ "sum(cat_claim_cost_ytm) cat_sev_ytm,\r\n" + "sum(others_claim_cost_ytm) others_sev_ytm\r\n"
					+ "from monthly_dashboard_grouped_new\r\n" + "where  inception_month=202002 \r\n";

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by business_type,claim_type";

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				in++;

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(3));
						res.setSevOd(rs.getDouble(9));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(4));
						res.setSevOd(rs.getDouble(10));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(5));
						res.setSevOd(rs.getDouble(11));
					}

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(6));
						res.setSevOd(rs.getDouble(12));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(7));
						res.setSevOd(rs.getDouble(13));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(8));
						res.setSevOd(rs.getDouble(14));
					}

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(3));
						res.setSevOd(rs.getDouble(9));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(4));
						res.setSevOd(rs.getDouble(10));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(5));
						res.setSevOd(rs.getDouble(11));
					}

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(6));
						res.setSevOd(rs.getDouble(12));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(7));
						res.setSevOd(rs.getDouble(13));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(8));
						res.setSevOd(rs.getDouble(14));
					}

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(3));
						res.setSevOd(rs.getDouble(9));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(4));
						res.setSevOd(rs.getDouble(10));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(5));
						res.setSevOd(rs.getDouble(11));
					}

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(6));
						res.setSevOd(rs.getDouble(12));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(7));
						res.setSevOd(rs.getDouble(13));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(8));
						res.setSevOd(rs.getDouble(14));
					}

					monthlyResponse.add(res);

				}

				// connection.commit();
			}
			rs.close();

//		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			/////////////////////////////////

			//////////////////////// freq sev pm

			queryStr = "select business_type,claim_type,\r\n" + "sum(theft_freq_expected) theft_freq,\r\n"
					+ "sum(cat_freq_expected) cat_freq,\r\n" + "sum(others_freq_expected) others_freq,\r\n"
					+ "sum(theft_freq_expected_ytm) theft_freq_ytm,\r\n"
					+ "sum(cat_freq_expected_ytm) cat_freq_ytm,\r\n"
					+ "sum(others_freq_expected_ytm) others_freq_ytm,\r\n"
					+ "sum(theft_claim_cost_expected) theft_sev,\r\n" + "sum(cat_claim_cost_expected) cat_sev,\r\n"
					+ "sum(others_claim_cost_expected) others_sev,\r\n" + "sum(theft_claim_cost_ytm) theft_sev_ytm,\r\n"
					+ "sum(cat_claim_cost_ytm) cat_sev_ytm,\r\n" + "sum(others_claim_cost_ytm) others_sev_ytm\r\n"
					+ "from monthly_dashboard_grouped_new\r\n" + "where  inception_month=202001 \r\n";

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by business_type,claim_type";

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				in++;

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(3));
						res.setSevOd(rs.getDouble(9));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(4));
						res.setSevOd(rs.getDouble(10));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(5));
						res.setSevOd(rs.getDouble(11));
					}

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(6));
						res.setSevOd(rs.getDouble(12));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(7));
						res.setSevOd(rs.getDouble(13));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(8));
						res.setSevOd(rs.getDouble(14));
					}

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(3));
						res.setSevOd(rs.getDouble(9));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(4));
						res.setSevOd(rs.getDouble(10));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(5));
						res.setSevOd(rs.getDouble(11));
					}

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(6));
						res.setSevOd(rs.getDouble(12));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(7));
						res.setSevOd(rs.getDouble(13));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(8));
						res.setSevOd(rs.getDouble(14));
					}

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(3));
						res.setSevOd(rs.getDouble(9));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(4));
						res.setSevOd(rs.getDouble(10));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(5));
						res.setSevOd(rs.getDouble(11));
					}

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(6));
						res.setSevOd(rs.getDouble(12));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(7));
						res.setSevOd(rs.getDouble(13));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(8));
						res.setSevOd(rs.getDouble(14));
					}

					monthlyResponse.add(res);

				}

				// connection.commit();
			}
			rs.close();

			queryStr = "select \r\n" + "business_type,claim_type, \r\n"
					+ "sum(theft_freq_expected_ytm) theft_freq_ytm,\r\n"
					+ "sum(cat_freq_expected_ytm) cat_freq_ytm,\r\n"
					+ "sum(others_freq_expected_ytm) others_freq_ytm,\r\n"
					+ "sum(theft_claim_cost_ytm) theft_sev_ytm,\r\n" + "sum(cat_claim_cost_ytm) cat_sev_ytm, \r\n"
					+ "sum(others_claim_cost_ytm) others_sev_ytm\r\n" + "from monthly_dashboard_grouped_new \r\n"
					+ "where  \r\n" + "inception_month=201902 ";

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by business_type,claim_type";

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				in++;

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(3));
						res.setSevOd(rs.getDouble(6));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(4));
						res.setSevOd(rs.getDouble(7));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(5));
						res.setSevOd(rs.getDouble(8));
					}

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(3));
						res.setSevOd(rs.getDouble(6));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(4));
						res.setSevOd(rs.getDouble(7));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(5));
						res.setSevOd(rs.getDouble(8));
					}

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					if (rs.getString(2).equalsIgnoreCase("THEFT")) {
						res.setClaimType("THEFT");
						res.setFreqOd(rs.getDouble(3));
						res.setSevOd(rs.getDouble(6));
					} else if (rs.getString(2).equalsIgnoreCase("CAT")) {
						res.setClaimType("CAT");
						res.setFreqOd(rs.getDouble(4));
						res.setSevOd(rs.getDouble(7));
					} else if (rs.getString(2).equalsIgnoreCase("OTHERS")) {
						res.setClaimType("OTHERS");
						res.setFreqOd(rs.getDouble(5));
						res.setSevOd(rs.getDouble(8));
					}

					monthlyResponse.add(res);

				}

				// connection.commit();
			}
			rs.close();

//   System.out.println("--------------------------------------------"+generalKpiResponseList.size());
// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

/////////////////////////////////

			return monthlyResponse;

		} catch (Exception e) {
			System.out.println("Postgres initialize error, ex: " + e);
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}

			if (connection != null) {
				connection.close();
			}

		}
		return monthlyResponse;

	}

	@GetMapping("/getClaimsExpectedfreqData")
	@ResponseBody
	public MonthlyDasboardNew getClaimsExpectedfreqData() throws SQLException {

		Connection connection = null;
		Statement stmt = null;
		long startTime = System.currentTimeMillis();
		MonthlyDasboardNew md = new MonthlyDasboardNew();
		List<MonthlyDashboardDetailsResponse> monDetailsRes = new ArrayList<>();
		try {
			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", env.getProperty("spring.datasource.username"));
			info.put("password", env.getProperty("spring.datasource.password"));
			connection = driverManager
					.connect("jdbc:postgresql://" + RMSConstants.POSTGRES_BASE_IP_AND_PORT + "/kylin_db", info);
			System.out.println("Connection status -------------------------->" + connection);

			stmt = connection.createStatement();
			String queryStr = "select business_type,claim_type,sum(expected_claim) expected_claims,"
					+ "sum(expected_claims_ytm) expected_claims_ytm," + "sum(expected_claims_pytm) expected_claims_pytm"
					+ "from monthly_dashboard_details_new where "
					+ " inception_month=2 and inception_year=2019 group by business_type,claim_type ";
			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			int updateStatus = 0;
			int i = 0;
			while (rs.next()) {
				i++;
				System.out.println("rs.getString(1)-->" + rs.getString(1));
				if (rs.getString(1).equals("New")) {
					if (rs.getString(2).equals("NewTheft")) {
						md.setNewTheftExpectedfreqPolicyCm(rs.getDouble(3));
						md.setNewTheftExpectedfreqPolicyYtm(rs.getDouble(4));
						md.setNewTheftExpectedfreqPolicyPytm(rs.getDouble(5));
					} else if (rs.getString(2).equals("CAT")) {
						md.setNewCatExpectedfreqPolicyCm(rs.getDouble(3));
						md.setNewCatExpectedfreqPolicyYtm(rs.getDouble(4));
						md.setNewCatExpectedfreqPolicyPytm(rs.getDouble(5));
					} else if (rs.getString(2).equals("OTHERS")) {
						md.setNewOthersExpectedfreqPolicyCm(rs.getDouble(3));
						md.setNewOthersExpectedfreqPolicyYtm(rs.getDouble(4));
						md.setNewOthersExpectedfreqPolicyPytm(rs.getDouble(5));
					}
				} else if (rs.getString(1).equals("Roll-Over")) {
					if (rs.getString(2).equals("NewTheft")) {
						md.setRollOverTheftExpectedfreqPolicyCm(rs.getDouble(3));
						md.setRollOverTheftExpectedfreqPolicyYtm(rs.getDouble(4));
						md.setRollOverTheftExpectedfreqPolicyPytm(rs.getDouble(5));
					} else if (rs.getString(2).equals("CAT")) {
						md.setRollOverCatExpectedfreqPolicyCm(rs.getDouble(3));
						md.setRollOverCatExpectedfreqPolicyYtm(rs.getDouble(4));
						md.setRollOverCatExpectedfreqPolicyPytm(rs.getDouble(5));
					} else if (rs.getString(2).equals("OTHERS")) {
						md.setRollOverOthersExpectedfreqPolicyCm(rs.getDouble(3));
						md.setRollOverOthersExpectedfreqPolicyYtm(rs.getDouble(4));
						md.setRollOverOthersExpectedfreqPolicyPytm(rs.getDouble(5));
					}
				} else if (rs.getString(1).equals("Renewal")) {
					if (rs.getString(2).equals("NewTheft")) {
						md.setRenewalTheftExpectedfreqPolicyCm(rs.getDouble(3));
						md.setRenewalTheftExpectedfreqPolicyYtm(rs.getDouble(4));
						md.setRenewalTheftExpectedfreqPolicyPytm(rs.getDouble(5));
					} else if (rs.getString(2).equals("CAT")) {
						md.setRenewalCatExpectedfreqPolicyCm(rs.getDouble(3));
						md.setRenewalCatExpectedfreqPolicyYtm(rs.getDouble(4));
						md.setRenewalCatExpectedfreqPolicyPytm(rs.getDouble(5));
					} else if (rs.getString(2).equals("OTHERS")) {
						md.setRenewalOthersExpectedfreqPolicyCm(rs.getDouble(3));
						md.setRenewalOthersExpectedfreqPolicyYtm(rs.getDouble(4));
						md.setRenewalOthersExpectedfreqPolicyPytm(rs.getDouble(5));
					}
				}

				// connection.commit();
			}
			rs.close();

			queryStr = "select business_type,claim_type,sum(gwp_od) wp_od "
					+ " from monthly_dashboard_details_new where " + " inception_month=1 and inception_year=2020 "
					+ " group by business_type,claim_type ";
			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				if (rs.getString(1).equals("New")) {
					if (rs.getString(2).equals("NewTheft")) {
						md.setNewTheftExpectedfreqPolicyPm(rs.getDouble(3));
					} else if (rs.getString(2).equals("CAT")) {
						md.setNewCatExpectedfreqPolicyPm(rs.getDouble(3));
					} else if (rs.getString(2).equals("OTHERS")) {
						md.setNewOthersExpectedfreqPolicyPm(rs.getDouble(3));
					}
				} else if (rs.getString(1).equals("Roll-Over")) {
					if (rs.getString(2).equals("NewTheft")) {
						md.setRollOverTheftExpectedfreqPolicyPm(rs.getDouble(3));
					} else if (rs.getString(2).equals("CAT")) {
						md.setRollOverCatExpectedfreqPolicyPm(rs.getDouble(3));
					} else if (rs.getString(2).equals("OTHERS")) {
						md.setRollOverOthersExpectedfreqPolicyPm(rs.getDouble(3));
					}
				} else if (rs.getString(1).equals("Renewal")) {
					if (rs.getString(2).equals("NewTheft")) {
						md.setRenewalTheftExpectedfreqPolicyPm(rs.getDouble(3));
					} else if (rs.getString(2).equals("CAT")) {
						md.setRenewalCatExpectedfreqPolicyPm(rs.getDouble(3));
					} else if (rs.getString(2).equals("OTHERS")) {
						md.setRenewalOthersExpectedfreqPolicyPm(rs.getDouble(3));
					}
				}

			}
			rs.close();

//		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			return md;

		} catch (Exception e) {
			System.out.println("Postgres initialize error, ex: " + e);
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}

			if (connection != null) {
				connection.close();
			}

		}
		return md;

	}

	@GetMapping("/computeAndLoadCurrentMonthData")
	@ResponseBody
	public boolean computeAndLoadCurrentMonthData() throws SQLException {

		Connection connection = null;
		Statement stmt = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		long startTime = System.currentTimeMillis();
		try {
			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", env.getProperty("spring.datasource.username"));
			info.put("password", env.getProperty("spring.datasource.password"));
			connection = driverManager
					.connect("jdbc:postgresql://" + RMSConstants.POSTGRES_BASE_IP_AND_PORT + "/kylin_db", info);
			System.out.println("Connection status -------------------------->" + connection);

			stmt = connection.createStatement();
			String queryStr = "select distinct INCEPTION_MONTH,INCEPTION_YEAR from monthly_dashboard_details_new order by INCEPTION_YEAR,INCEPTION_month desc";
			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// updateRolling12Data(rs);
			int updateStatus = 0;
			int i = 0;
			while (rs.next()) {
				i++;

				/* computing & Loading r12 policies and claims */
				/*
				 * String updateSql =
				 * "update monthly_dashboard_details_new a set r12_polices=(select COALESCE(sum(policy_od),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and "
				 * +
				 * " (b.INCEPTION_YEAR::text||LPAD(b.INCEPTION_MONTH::text,2,'0') >= to_char((TO_DATE(a.INCEPTION_YEAR::text||LPAD(a.INCEPTION_MONTH::text,2,'0'),'YYYYMM') - INTERVAL '12 month' ),'YYYYMM') AND b.INCEPTION_YEAR::text||LPAD(b.INCEPTION_MONTH::text,2,'0') <  ("
				 * +rs.getInt(2)+"::text||LPAD("+rs.getInt(1)+"::text,2,'0')) ) ), "+
				 * " r12_claims=(select COALESCE(sum(claims_od),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and "
				 * +
				 * " ( b.INCEPTION_YEAR::text||LPAD(b.INCEPTION_MONTH::text,2,'0') >= to_char((TO_DATE(a.INCEPTION_YEAR::text||LPAD(a.INCEPTION_MONTH::text,2,'0'),'YYYYMM') - INTERVAL '12 month' ),'YYYYMM') AND b.INCEPTION_YEAR::text||LPAD(b.INCEPTION_MONTH::text,2,'0') <  ("
				 * +rs.getInt(2)+"::text||LPAD("+rs.getInt(1)+"::text,2,'0')) ) ) "+
				 * " where a.INCEPTION_MONTH="+rs.getInt(1)+" AND a.INCEPTION_YEAR="+rs.getInt(2
				 * )+"";
				 */

				String updateSql = "update monthly_dashboard_details_new a set r12_polices=c.r12_p, r12_claims=c.r12_c "
						+ " from (select COALESCE(sum(policy_od),0) r12_p,COALESCE(sum(claims_od),0) r12_c,business_type,channel,sub_channel,make,model_group,geo, "
						+ " claim_type from monthly_dashboard_details_new b where (b.INCEPTION_YEAR::text||LPAD(b.INCEPTION_MONTH::text,2,'0') >= "
						+ " to_char((TO_DATE(" + rs.getInt(2) + "::text||LPAD(" + rs.getInt(1)
						+ "::text,2,'0'),'YYYYMM') - INTERVAL '12 month' ),'YYYYMM') "
						+ " AND b.INCEPTION_YEAR::text||LPAD(b.INCEPTION_MONTH::text,2,'0') <  (" + rs.getInt(2)
						+ "::text||LPAD(" + rs.getInt(1) + "::text,2,'0')) ) "
						+ " group by business_type,channel,sub_channel,make,model_group,geo,claim_type ) as c "
						+ " where a.business_type=c.business_type "
						+ " and a.channel=c.channel and a.sub_channel=c.sub_channel and a.make=c.make and a.model_group=c.model_group "
						+ " and a.geo=c.geo and a.claim_type=c.claim_type and a.INCEPTION_MONTH=" + rs.getInt(1)
						+ " AND a.INCEPTION_YEAR=" + rs.getInt(2) + "";

				System.out.println(i + "<-- iteration count - updated r12 record sql-->" + updateSql);
				stmt1 = connection.createStatement();
				updateStatus = stmt1.executeUpdate(updateSql);

				/* computing and loading freq & expected claims */
				updateSql = "update monthly_dashboard_details_new a set freq=(case when r12_polices<>0 then round(cast((r12_claims/r12_polices) as numeric),2) else 0 end), expected_claim=(case when r12_polices<>0 then round(cast((r12_claims/r12_polices)*policy_od as numeric),0) else 0 end)"
						+ " where a.INCEPTION_MONTH=" + rs.getInt(1) + " AND a.INCEPTION_YEAR=" + rs.getInt(2) + "";
				System.out.println(i + "<-- iteration count -updated freq & claim record sql-->" + updateSql);
				stmt2 = connection.createStatement();
				updateStatus = stmt2.executeUpdate(updateSql);

				// connection.commit();
			}

//		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			System.out.println("Postgres initialize error, ex: " + e);
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (stmt1 != null) {
				stmt1.close();
			}
			if (stmt2 != null) {
				stmt2.close();
			}
			if (connection != null) {
				connection.close();
			}

		}

		return true;
	}

	@GetMapping("/computeAndLoadYTMAndPYTMData")
	@ResponseBody
	public boolean computeAndLoadYTMAndPYTMData() throws SQLException {

		Connection connection = null;
		Statement stmt = null;
		Statement stmt1 = null;
		long startTime = System.currentTimeMillis();
		try {
			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", env.getProperty("spring.datasource.username"));
			info.put("password", env.getProperty("spring.datasource.password"));
			connection = driverManager
					.connect("jdbc:postgresql://" + RMSConstants.POSTGRES_BASE_IP_AND_PORT + "/kylin_db", info);
			System.out.println("Connection status -------------------------->" + connection);

			stmt = connection.createStatement();
			String queryStr = "select distinct INCEPTION_MONTH,INCEPTION_YEAR from monthly_dashboard_details_new order by INCEPTION_YEAR,INCEPTION_month desc";
			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// updateRolling12Data(rs);
			int updateStatus = 0;
			int i = 0;
			while (rs.next()) {
				i++;

				/*
				 * computing & Loading metrics gwp od, gwp tp, policy od, policy tp,claims
				 * od,claims tp for ytm & pytm
				 */
				/*
				 * String updateSql =
				 * "update monthly_dashboard_details_new a set gwp_od_ytm=(select COALESCE(sum(gwp_od),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and  ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +" and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-1 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"  AND inception_month <="+rs.getInt(1)+" ) end)), "+
				 * " gwp_tp_ytm=(select COALESCE(sum(gwp_tp),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +" and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-1 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,policy_od_ytm=(select COALESCE(sum(policy_od),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +" and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-1 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,policy_tp_ytm=(select COALESCE(sum(policy_tp),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +" and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-1 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,claims_od_ytm=(select COALESCE(sum(claims_od),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +" and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-1 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,claims_tp_ytm=(select COALESCE(sum(claims_tp),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +" and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-1 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,gwp_od_pytm=(select COALESCE(sum(gwp_od),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +"-1 and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-2 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"-1  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,gwp_tp_pytm=(select COALESCE(sum(gwp_tp),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +"-1 and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-2 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"-1  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,policy_od_pytm=(select COALESCE(sum(policy_od),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +"-1 and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-2 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"-1  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,policy_tp_pytm=(select COALESCE(sum(policy_tp),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +"-1 and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-2 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"-1  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,claims_od_pytm=(select COALESCE(sum(claims_od),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +"-1 and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-2 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"-1  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " ,claims_tp_pytm=(select COALESCE(sum(claims_tp),0) from monthly_dashboard_details_new b where "
				 * +
				 * " a.business_type=b.business_type and a.channel=b.channel and a.sub_channel=b.sub_channel and a.make=b.make and a.model_group=b.model_group and a.geo=b.geo and a.claim_type=b.claim_type and ( case when "
				 * +rs.getInt(1)+" >03 then (inception_year="+rs.getInt(2)
				 * +"-1 and inception_month <= "+rs.getInt(1)
				 * +" and inception_month>03  ) else (inception_year="+rs.getInt(2)
				 * +"-2 AND inception_month > 03 )  or (inception_year="+rs.getInt(2)
				 * +"-1  AND inception_month <="+rs.getInt(1)+" ) end)) "+
				 * " where a.INCEPTION_MONTH="+rs.getInt(1)+" AND a.INCEPTION_YEAR="+rs.getInt(2
				 * )+"";
				 */

				String updateSql = "update monthly_dashboard_details_new a set gwp_od_ytm= c.gwp_od_ytm,gwp_tp_ytm = c.gwp_tp_ytm,policy_od_ytm= c.policy_od_ytm, "
						+ " policy_tp_ytm= c.policy_tp_ytm,claims_od_ytm= c.claims_od_ytm,claims_tp_ytm= c.claims_tp_ytm,r12_policies_ytm=c.r12_policies_ytm,r12_claims_ytm=c.r12_claims_ytm,freq_ytm=(case when c.r12_policies_ytm<>0 then round(cast((c.r12_claims_ytm/c.r12_policies_ytm) as numeric),2) else 0 end), expected_claims_ytm=(case when c.r12_policies_ytm<>0 then round(cast((c.r12_claims_ytm/c.r12_policies_ytm)*(c.policy_od_ytm) as numeric),2) else 0 end), "
						+ " gwp_od_pytm= c.gwp_od_ytm,gwp_tp_pytm = c.gwp_tp_pytm,policy_od_pytm= c.policy_od_pytm, "
						+ " policy_tp_pytm= c.policy_tp_pytm,claims_od_pytm= c.claims_od_pytm,claims_tp_pytm= c.claims_tp_pytm,r12_policies_pytm=c.r12_policies_pytm,r12_claims_pytm=c.r12_claims_pytm,freq_pytm=(case when c.r12_policies_pytm<>0 then round(cast((c.r12_claims_pytm/c.r12_policies_pytm) as numeric),2) else 0 end), expected_claims_pytm=(case when c.r12_policies_pytm<>0 then round(cast((c.r12_claims_pytm/c.r12_policies_pytm)*(c.policy_od_pytm) as numeric),2) else 0 end)  "
						+ " from (select sum(gwp_od_ytm) gwp_od_ytm, "
						+ " sum(gwp_tp_ytm) gwp_tp_ytm,sum(policy_od_ytm) policy_od_ytm, sum(policy_tp_ytm) policy_tp_ytm, sum(claims_od_ytm) claims_od_ytm, "
						+ " sum(claims_tp_ytm) claims_tp_ytm,sum(gwp_od_pytm) gwp_od_pytm, sum(gwp_tp_pytm) gwp_tp_pytm, sum(policy_od_pytm) policy_od_pytm, "
						+ " sum(policy_tp_pytm)policy_tp_pytm,sum(claims_od_pytm)claims_od_pytm,sum(claims_tp_pytm) claims_tp_pytm, sum(r12_policies_ytm) r12_policies_ytm,sum(r12_claims_ytm) r12_claims_ytm, sum(r12_policies_pytm) r12_policies_pytm,sum(r12_claims_pytm) r12_claims_pytm, "
						+ " business_type,channel,sub_channel,make,model_group,geo, " + " claim_type from ( "
						+ " select COALESCE(sum(gwp_od),0) gwp_od_ytm,COALESCE(sum(gwp_tp),0) gwp_tp_ytm,COALESCE(sum(policy_od),0) policy_od_ytm, "
						+ " COALESCE(sum(policy_tp),0) policy_tp_ytm,COALESCE(sum(claims_od),0) claims_od_ytm,COALESCE(sum(claims_tp),0) claims_tp_ytm, "
						+ " 0 gwp_od_pytm,0 gwp_tp_pytm,0 policy_od_pytm,0 policy_tp_pytm,0 claims_od_pytm,0 claims_tp_pytm, COALESCE(sum(r12_polices),0) r12_policies_ytm,COALESCE(sum(r12_claims),0) r12_claims_ytm, 0 r12_policies_pytm,0 r12_claims_pytm, "
						+ " business_type,channel,sub_channel,make,model_group,geo, "
						+ " claim_type from monthly_dashboard_details_new b where ( case when " + rs.getInt(1)
						+ " >03 then (inception_year=" + rs.getInt(2) + " and inception_month <= " + rs.getInt(1)
						+ " and inception_month>03  ) else (inception_year=" + rs.getInt(2)
						+ "-1 AND inception_month > 03 )  or (inception_year=" + rs.getInt(2)
						+ "  AND inception_month <=" + rs.getInt(1)
						+ " ) end) group by business_type,channel,sub_channel,make,model_group,geo,claim_type "
						+ " union all "
						+ " select 0 gwp_od_ytm,0 gwp_tp_ytm,0 policy_od_ytm,0 policy_tp_ytm,0 claims_od_ytm,0 claims_tp_ytm,COALESCE(sum(gwp_od),0) gwp_od_pytm,COALESCE(sum(gwp_tp),0) gwp_tp_pytm,COALESCE(sum(policy_od),0) policy_od_pytm, "
						+ " COALESCE(sum(policy_tp),0) policy_tp_pytm,COALESCE(sum(claims_od),0) claims_od_pytm,COALESCE(sum(claims_tp),0) claims_tp_pytm, 0 r12_policies_ytm,0 r12_claims_ytm, COALESCE(sum(r12_polices),0) r12_policies_pytm,COALESCE(sum(r12_claims),0) r12_claims_pytm, "
						+ " business_type,channel,sub_channel,make,model_group,geo, "
						+ " claim_type from monthly_dashboard_details_new b where ( case when " + rs.getInt(1)
						+ " >03 then (inception_year=" + rs.getInt(2) + "-1 and inception_month <= " + rs.getInt(1)
						+ " and inception_month>03  ) else (inception_year=" + rs.getInt(2)
						+ "-2 AND inception_month > 03 )  or (inception_year=" + rs.getInt(2)
						+ "-1  AND inception_month <=" + rs.getInt(1)
						+ " ) end) group by business_type,channel,sub_channel,make,model_group,geo,claim_type "
						+ " ) m group by business_type,channel,sub_channel,make,model_group,geo,claim_type ) c "
						+ " where a.business_type=c.business_type and a.channel=c.channel and a.sub_channel=c.sub_channel and a.make=c.make and a.model_group=c.model_group "
						+ " and a.geo=c.geo and a.claim_type=c.claim_type and a.INCEPTION_MONTH=" + rs.getInt(1)
						+ " AND a.INCEPTION_YEAR=" + rs.getInt(2) + "";

				System.out.println(i + "<-- iteration count - updated r12 record sql-->" + updateSql);
				stmt1 = connection.createStatement();
				updateStatus = stmt1.executeUpdate(updateSql);

				/*
				 * updateSql =
				 * "update monthly_dashboard_details_new a set freq=(case when r12_polices<>0 then round(cast((r12_claims/r12_polices) as numeric),2) else 0 end), expected_claim=(case when r12_polices<>0 then round(cast((r12_claims/r12_polices)*policy_od as numeric),0) else 0 end)"
				 * +
				 * " where a.INCEPTION_MONTH="+rs.getInt(1)+" AND a.INCEPTION_YEAR="+rs.getInt(2
				 * )+""; System.out.println(
				 * i+"<-- iteration count -updated freq & claim record sql-->"+updateSql );
				 * stmt2 = connection.createStatement(); updateStatus =
				 * stmt2.executeUpdate(updateSql);
				 */

				// connection.commit();
			}

//		        System.out.println("--------------------------------------------"+generalKpiResponseList.size());
			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			System.out.println("Postgres initialize error, ex: " + e);
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (stmt1 != null) {
				stmt1.close();
			}
			if (connection != null) {
				connection.close();
			}

		}

		return true;
	}

	/*
	 * private void updateRolling12Data(ResultSet rs) throws SQLException { String
	 * bType="",channel="",sChannel="",make="",modelGroup="",geo="",claimType="";
	 * int month=0,year=0; while(rs.next()) { bType=rs.getString(1); channel =
	 * rs.getString(2); sChannel= rs.getString(3); make= rs.getString(4); modelGroup
	 * = rs.getString(5); geo = rs.getString(6); claimType= rs.getString(7); month =
	 * rs.getInt(8); year = rs.getInt(9);
	 * calculateRolling12Records(bType,channel,sChannel,make,modelGroup,geo,
	 * claimType,month,year); }
	 * 
	 * }
	 * 
	 * private void calculateRolling12Records(String bType, String channel, String
	 * sChannel, String make, String modelGroup, String geo, String claimType, int
	 * month, int year) {
	 * 
	 * UPDATE TABLE SET R12POLICY=
	 * 
	 * 
	 * }
	 */

//	@GetMapping("/computeAndLoadCurrentMonthDataNew")
//	@ResponseBody
//	public boolean computeAndLoadCurrentMonthDataNew() throws SQLException {
//
//		Connection connection = null;
//		Statement stmt = null;
//		Statement stmt1 = null;
//		Statement stmt2 = null;
//		long startTime = System.currentTimeMillis();
//		String queryStr = "", updateSql = "";
//		int thresholdMonthYear = 0;
//		try {
//			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
//			Properties info = new Properties();
//			info.put("user", "postgres");
//			info.put("password", "root");
//			connection = driverManager.connect("jdbc:postgresql://localhost:5432/kylin_db", info);
//			System.out.println("Connection status -------------------------->" + connection);
//
//			stmt = connection.createStatement();
//
//			queryStr = "select to_char((current_Date- INTERVAL '14 month')::date,'YYYYMM')";
//			System.out.println("queryStr------------------------------ " + queryStr);
//			ResultSet rs = stmt.executeQuery(queryStr);
//			System.out.println("START------------------------------ ");
//
//			while (rs.next()) {
//				thresholdMonthYear = rs.getInt(1);
//				System.out.println("thresholdMonthYear-->" + thresholdMonthYear);
//			}
//
//			rs.close();
//
//			System.out.println("thresholdMonthYear-->" + thresholdMonthYear);
//
//			queryStr = "select distinct INCEPTION_MONTH from monthly_dashboard_grouped_new order by INCEPTION_MONTH asc";
//			System.out.println("queryStr------------------------------ " + queryStr);
//			rs = stmt.executeQuery(queryStr);
//			System.out.println("START------------------------------ ");
//
//			// updateRolling12Data(rs);
//			int updateStatus = 0;
//			int i = 0;
//			while (rs.next()) {
//				i++;
//
//				/* computing & Loading r12 policies and claims */
//				System.out.println(rs.getInt(1) + "::thresholdMonthYear-->" + Integer.valueOf(thresholdMonthYear));
//				if (rs.getInt(1) < Integer.valueOf(thresholdMonthYear)) {
//					updateSql = "update monthly_dashboard_grouped_new a set gwp_od_r12=c.gwp_od_r12, gwp_tp_r12=c.gwp_tp_r12, gwp_r12=(c.gwp_od_r12+c.gwp_tp_r12),policy_count_r12=c.policy_count_r12,cat_claims_count_r12=c.cat_claims_count_r12,theft_claims_count_r12=c.theft_claims_count_r12,others_claims_count_r12=c.others_claims_count_r12,tot_claims_count_r12=(c.cat_claims_count_r12+c.theft_claims_count_r12+c.others_claims_count_r12),cat_claims_amt_r12=c.cat_claims_amt_r12,theft_claims_amt_r12=c.theft_claims_amt_r12,others_claims_amt_r12=c.others_claims_amt_r12,tot_claims_amt_r12=(c.cat_claims_amt_r12+c.theft_claims_amt_r12+c.others_claims_amt_r12) "
//							+ " from ( select COALESCE(sum(gwp_od),0) gwp_od_r12,COALESCE(sum(gwp_tp),0) gwp_tp_r12,COALESCE(sum(policy_count),0) policy_count_r12,COALESCE(sum(cat_claim_count),0) cat_claims_count_r12,COALESCE(sum(theft_claim_count),0) theft_claims_count_r12,COALESCE(sum(others_claims_count),0) others_claims_count_r12,COALESCE(sum(cat_claims_amt),0) cat_claims_amt_r12,COALESCE(sum(theft_claims_amt),0) theft_claims_amt_r12,COALESCE(sum(others_claims_amt),0) others_claims_amt_r12,product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type "
//							+ " from monthly_dashboard_grouped_new b where (b.INCEPTION_month) >= "
//							+ " to_char((TO_DATE('" + rs.getInt(1)
//							+ "','YYYYMM') - INTERVAL '12 month' ),'YYYYMM')::numeric " + " AND b.INCEPTION_MONTH <  ('"
//							+ rs.getInt(1) + "')::numeric  "
//							+ " group by product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type ) as c "
//							+ " where a.product_code=c.product_code and a.business_type=c.business_type and a.fuel_type=c.fuel_type and a.city_name=c.city_name and a.state_name=c.state_name and a.state_grouping=c.state_grouping and a.make=c.make and a.model_group=c.model_group and a.claim_type=c.claim_type and a.ncb_flag=c.ncb_flag and a.policy_type=c.policy_type "
//							+ "  and a.INCEPTION_MONTH='" + rs.getInt(1) + "' ";
//
//					System.out.println(i + "<-- iteration count less than 14 - updated r12 record sql-->" + updateSql);
//					stmt1 = connection.createStatement();
//					updateStatus = stmt1.executeUpdate(updateSql);
//
//				} else {
//					System.out.println(i + "<-- iteration count - > 14 updated r12 record sql-->");
//					updateSql = "update monthly_dashboard_grouped_new a set gwp_od_r12=c.gwp_od_r12, gwp_tp_r12=c.gwp_tp_r12, gwp_r12=c.gwp_r12 ,policy_count_r12=c.policy_count_r12,cat_claims_count_r12=c.cat_claims_count_r12,theft_claims_count_r12=c.theft_claims_count_r12,others_claims_count_r12=c.others_claims_count_r12,tot_claims_count_r12=c.tot_claims_count_r12,cat_claims_amt_r12=c.cat_claims_amt_r12,theft_claims_amt_r12=c.theft_claims_amt_r12,others_claims_amt_r12=c.others_claims_amt_r12,tot_claims_amt_r12=c.tot_claims_amt_r12 "
//							+ " from (select COALESCE(sum(gwp_od_r12),0) gwp_od_r12, COALESCE(sum(gwp_tp_r12),0) gwp_tp_r12, COALESCE(sum(gwp_r12),0) gwp_r12,COALESCE(sum(policy_count_r12),0) policy_count_r12,COALESCE(sum(cat_claims_count_r12),0) cat_claims_count_r12,COALESCE(sum(theft_claims_count_r12),0) theft_claims_count_r12,COALESCE(sum(others_claims_count_r12),0) others_claims_count_r12,COALESCE(sum(tot_claims_count_r12),0) tot_claims_count_r12,COALESCE(sum(cat_claims_amt_r12),0) cat_claims_amt_r12,COALESCE(sum(theft_claims_amt_r12),0) theft_claims_amt_r12,COALESCE(sum(others_claims_amt_r12),0) others_claims_amt_r12,COALESCE(sum(tot_claims_amt_r12),0) tot_claims_amt_r12,product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type "
//							+ " from monthly_dashboard_grouped_new b where (b.INCEPTION_month) = "
//							+ " to_char((TO_DATE('" + rs.getInt(1)
//							+ "','YYYYMM') - INTERVAL '14 month' ),'YYYYMM')::numeric "
//							+ " group by product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type ) as c "
//							+ " where a.product_code=c.product_code and a.business_type=c.business_type and a.fuel_type=c.fuel_type and a.city_name=c.city_name and a.state_name=c.state_name and a.state_grouping=c.state_grouping and a.make=c.make and a.model_group=c.model_group and a.claim_type=c.claim_type and a.ncb_flag=c.ncb_flag and a.policy_type=c.policy_type "
//							+ " and  a.INCEPTION_MONTH=" + rs.getInt(1) + " ";
//
//					System.out.println(i + "<-- iteration count - > 14 updated r12 record sql-->" + updateSql);
//					stmt1 = connection.createStatement();
//					updateStatus = stmt1.executeUpdate(updateSql);
//
//				}
//
//				/* computing and loading freq & expected claims */
//				updateSql = "update monthly_dashboard_grouped_new a set cat_freq_avg=(case when policy_count_r12<>0 then round(cast((cat_claims_count_r12/policy_count_r12) as numeric),2) else 0 end), theft_freq_avg=(case when policy_count_r12<>0 then round(cast((theft_claims_count_r12/policy_count_r12) as numeric),2) else 0 end), others_freq_avg=(case when policy_count_r12<>0 then round(cast((others_claims_count_r12/policy_count_r12) as numeric),2) else 0 end), "
//						+ " cat_claim_cost_avg=(case when cat_claims_count_r12<>0 then round(cast((cat_claims_amt_r12/cat_claims_count_r12) as numeric),2) else 0 end), theft_claim_cost_avg=(case when theft_claims_count_r12<>0 then round(cast((theft_claims_amt_r12/theft_claims_count_r12) as numeric),2) else 0 end), others_claim_cost_avg=(case when others_claims_count_r12<>0 then round(cast((others_claims_amt_r12/others_claims_count_r12) as numeric),2) else 0 end) "
//						+ " where a.INCEPTION_MONTH=" + rs.getInt(1) + " ";
//
//				System.out.println(i + "<-- iteration count -updated avg freq & claim record sql-->" + updateSql);
//				stmt2 = connection.createStatement();
//				updateStatus = stmt2.executeUpdate(updateSql);
//
//				updateSql = "update monthly_dashboard_grouped_new a set cat_freq_expected= COALESCE(cat_claim_count,cat_freq_avg)*policy_count, theft_freq_expected= COALESCE(theft_claim_count,theft_freq_avg)*policy_count, others_freq_expected= COALESCE(others_claims_count,others_freq_avg)*policy_count, "
//						+ " cat_claim_cost_expected=(COALESCE(cat_claim_count,cat_freq_avg)*policy_count*cat_claim_cost_avg*102),theft_claim_cost_expected=(COALESCE(theft_claim_count,theft_freq_avg)*policy_count*theft_claim_cost_avg*102),others_claim_cost_expected=(COALESCE(others_claims_count,others_freq_avg)*policy_count*others_claim_cost_avg*102) "
//						+ " where a.INCEPTION_MONTH=" + rs.getInt(1) + " ";
//
//				System.out.println(i + "<-- iteration count -updated expected freq & claim record sql-->" + updateSql);
//				updateStatus = stmt2.executeUpdate(updateSql);
//
//				// connection.commit();
//			}
//
////	         System.out.println("--------------------------------------------"+generalKpiResponseList.size());
//			// System.out.println(jsArray.toString());
//			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//
//		} catch (Exception e) {
//			System.out.println("Postgres initialize error, ex: " + e);
//			e.printStackTrace();
//		} finally {
//			if (stmt != null) {
//				stmt.close();
//			}
//			if (stmt1 != null) {
//				stmt1.close();
//			}
//			if (stmt2 != null) {
//				stmt2.close();
//			}
//			if (connection != null) {
//				connection.close();
//			}
//
//		}
//
//		return true;
//	}
//
//	@GetMapping("/computeAndLoadYTMDataNew")
//	@ResponseBody
//	public boolean computeAndLoadYTMDataNew() throws SQLException {
//
//		String queryStr = "";
//		int thresholdMonthYear = 0;
//		Connection connection = null;
//		Statement stmt = null;
//		Statement stmt1 = null;
//		long startTime = System.currentTimeMillis();
//		try {
//			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
//			Properties info = new Properties();
//			info.put("user", "postgres");
//			info.put("password", "root");
//			connection = driverManager.connect("jdbc:postgresql://localhost:5432/kylin_db", info);
//			System.out.println("Connection status -------------------------->" + connection);
//
//			stmt = connection.createStatement();
//			queryStr = "select distinct INCEPTION_MONTH from monthly_dashboard_grouped_new order by INCEPTION_month asc";
//			System.out.println("queryStr------------------------------ " + queryStr);
//			ResultSet rs = stmt.executeQuery(queryStr);
//			System.out.println("START------------------------------ ");
//
//			// updateRolling12Data(rs);
//			int updateStatus = 0;
//			int i = 0;
//			while (rs.next()) {
//				i++;
//
//				String updateSql = "update monthly_dashboard_grouped_new a set gwp_od_ytm= c.gwp_od_ytm,gwp_tp_ytm = c.gwp_tp_ytm,policy_od_ytm= c.policy_od_ytm, "
//						+ " policy_tp_ytm= c.policy_tp_ytm, policy_ytm=(c.policy_od_ytm+c.policy_tp_ytm), cat_freq_expected_ytm=cat_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm),theft_freq_expected_ytm=c.theft_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm),others_freq_expected_ytm=c.others_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm), cat_claim_cost_ytm= cat_avg_cost_ytm*(cat_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm))*102,theft_claim_cost_ytm= theft_avg_cost_ytm*(theft_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm))*102,others_claim_cost_ytm= others_avg_cost_ytm*(others_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm))*102   "
//						+ " from (select COALESCE(sum(gwp_od),0) gwp_od_ytm,COALESCE(sum(gwp_tp),0) gwp_tp_ytm,COALESCE(sum(policy_od),0) policy_od_ytm,COALESCE(sum(policy_tp),0)  policy_tp_ytm,sum(COALESCE(cat_claim_count,cat_freq_avg)) cat_avg_freq_ytm,sum(COALESCE(theft_claim_count,theft_freq_avg)) theft_avg_freq_ytm,sum(COALESCE(others_claims_count,others_freq_avg)) others_avg_freq_ytm, sum(COALESCE(cat_claims_amt,cat_claim_cost_avg)) cat_avg_cost_ytm,sum(COALESCE(theft_claims_amt,theft_claim_cost_avg)) theft_avg_cost_ytm,sum(COALESCE(others_claims_amt,others_claim_cost_avg)) others_avg_cost_ytm,   "
//						+ " product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type "
//						+ "  from monthly_dashboard_grouped_new b where ( case when substring('" + rs.getInt(1)
//						+ "',5,2)::numeric >03 then (substring(inception_month::text,1,4)=substring('" + rs.getInt(1)
//						+ "',1,4) and substring(inception_month::text,5,2)::numeric <= substring('" + rs.getInt(1)
//						+ "',5,2)::numeric and substring(inception_month::text,5,2)::numeric>03  ) else ((substring(inception_month::text,1,4)::numeric=substring('"
//						+ rs.getInt(1)
//						+ "',1,4)::numeric-1 AND substring(inception_month::text,5,2)::numeric > 03 )  or (substring(inception_month::text,1,4)=substring('"
//						+ rs.getInt(1) + "',1,4)  AND substring(inception_month::text,5,2)::numeric <=substring('"
//						+ rs.getInt(1)
//						+ "',5,2)::numeric ) ) end)  group by product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type ) c "
//						+ " where a.product_code=c.product_code and a.business_type=c.business_type and a.fuel_type=c.fuel_type and a.city_name=c.city_name and a.state_name=c.state_name and a.state_grouping=c.state_grouping and a.make=c.make and a.model_group=c.model_group and a.claim_type=c.claim_type and a.ncb_flag=c.ncb_flag and a.policy_type=c.policy_type and a.INCEPTION_MONTH='"
//						+ rs.getInt(1) + "' ";
//
//				System.out.println(i + "<-- iteration count - updated r12 record sql-->" + updateSql);
//				stmt1 = connection.createStatement();
//				updateStatus = stmt1.executeUpdate(updateSql);
//
//				/*
//				 * }else{
//				 * 
//				 * }
//				 */
//
//				/*
//				 * updateSql =
//				 * "update monthly_dashboard_details_new a set freq=(case when r12_polices<>0 then round(cast((r12_claims/r12_polices) as numeric),2) else 0 end), expected_claim=(case when r12_polices<>0 then round(cast((r12_claims/r12_polices)*policy_od as numeric),0) else 0 end)"
//				 * +
//				 * " where a.INCEPTION_MONTH="+rs.getInt(1)+" AND a.INCEPTION_YEAR="+rs.getInt(2
//				 * )+""; System.out.println(
//				 * i+"<-- iteration count -updated freq & claim record sql-->"+updateSql );
//				 * stmt2 = connection.createStatement(); updateStatus =
//				 * stmt2.executeUpdate(updateSql);
//				 */
//
//				// connection.commit();
//			}
//
////	         System.out.println("--------------------------------------------"+generalKpiResponseList.size());
//			// System.out.println(jsArray.toString());
//			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));
//
//		} catch (Exception e) {
//			System.out.println("Postgres initialize error, ex: " + e);
//			e.printStackTrace();
//		} finally {
//			if (stmt != null) {
//				stmt.close();
//			}
//			if (stmt1 != null) {
//				stmt1.close();
//			}
//			if (connection != null) {
//				connection.close();
//			}
//
//		}
//
//		return true;
//	}

	@GetMapping("/computeAndLoadCurrentMonthDataNew")
	@ResponseBody
	public boolean computeAndLoadCurrentMonthDataNew() throws SQLException {

		Connection connection = null;
		Statement stmt = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		long startTime = System.currentTimeMillis();
		String queryStr = "", updateSql = "";
		int thresholdMonthYear = 0;
		try {
			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "postgres");
			info.put("password", "root");
			connection = driverManager.connect("jdbc:postgresql://localhost:5432/kylin_db", info);
			System.out.println("Connection status -------------------------->" + connection);

			stmt = connection.createStatement();

			queryStr = "select to_char((current_Date- INTERVAL '14 month')::date,'YYYYMM')";
			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				thresholdMonthYear = rs.getInt(1);
				System.out.println("thresholdMonthYear-->" + thresholdMonthYear);
			}

			rs.close();

			System.out.println("thresholdMonthYear-->" + thresholdMonthYear);

			queryStr = "select distinct INCEPTION_MONTH from monthly_dashboard_grouped_new order by INCEPTION_MONTH asc";
			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// updateRolling12Data(rs);
			int updateStatus = 0;
			int i = 0;
			while (rs.next()) {
				i++;

				/* computing & Loading r12 policies and claims */
				System.out.println(rs.getInt(1) + "::thresholdMonthYear-->" + Integer.valueOf(thresholdMonthYear));
				if (rs.getInt(1) < Integer.valueOf(thresholdMonthYear)) {
					updateSql = "update monthly_dashboard_grouped_new a set gwp_od_r12=c.gwp_od_r12, gwp_tp_r12=c.gwp_tp_r12, gwp_r12=(c.gwp_od_r12+c.gwp_tp_r12),policy_count_r12=c.policy_count_r12,cat_claims_count_r12=c.cat_claims_count_r12,theft_claims_count_r12=c.theft_claims_count_r12,others_claims_count_r12=c.others_claims_count_r12,tot_claims_count_r12=(c.cat_claims_count_r12+c.theft_claims_count_r12+c.others_claims_count_r12),cat_claims_amt_r12=c.cat_claims_amt_r12,theft_claims_amt_r12=c.theft_claims_amt_r12,others_claims_amt_r12=c.others_claims_amt_r12,tot_claims_amt_r12=(c.cat_claims_amt_r12+c.theft_claims_amt_r12+c.others_claims_amt_r12) "
							+ " from ( select COALESCE(sum(gwp_od),0) gwp_od_r12,COALESCE(sum(gwp_tp),0) gwp_tp_r12,COALESCE(sum(policy_count),0) policy_count_r12,COALESCE(sum(cat_claim_count),0) cat_claims_count_r12,COALESCE(sum(theft_claim_count),0) theft_claims_count_r12,COALESCE(sum(others_claims_count),0) others_claims_count_r12,COALESCE(sum(cat_claims_amt),0) cat_claims_amt_r12,COALESCE(sum(theft_claims_amt),0) theft_claims_amt_r12,COALESCE(sum(others_claims_amt),0) others_claims_amt_r12,product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type "
							+ " from monthly_dashboard_grouped_new b where (b.INCEPTION_month) >= "
							+ " to_char((TO_DATE('" + rs.getInt(1)
							+ "','YYYYMM') - INTERVAL '12 month' ),'YYYYMM')::numeric " + " AND b.INCEPTION_MONTH <  ('"
							+ rs.getInt(1) + "')::numeric  "
							+ " group by product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type ) as c "
							+ " where a.product_code=c.product_code and a.business_type=c.business_type and a.fuel_type=c.fuel_type and a.city_name=c.city_name and a.state_name=c.state_name and a.state_grouping=c.state_grouping and a.make=c.make and a.model_group=c.model_group and a.claim_type=c.claim_type and a.ncb_flag=c.ncb_flag and a.policy_type=c.policy_type "
							+ "  and a.INCEPTION_MONTH='" + rs.getInt(1) + "' ";

					System.out.println(i + "<-- iteration count less than 14 - updated r12 record sql-->" + updateSql);
					stmt1 = connection.createStatement();
					updateStatus = stmt1.executeUpdate(updateSql);

				} else {
					System.out.println(i + "<-- iteration count - > 14 updated r12 record sql-->");
					updateSql = "update monthly_dashboard_grouped_new a set gwp_od_r12=c.gwp_od_r12, gwp_tp_r12=c.gwp_tp_r12, gwp_r12=c.gwp_r12 ,policy_count_r12=c.policy_count_r12,cat_claims_count_r12=c.cat_claims_count_r12,theft_claims_count_r12=c.theft_claims_count_r12,others_claims_count_r12=c.others_claims_count_r12,tot_claims_count_r12=c.tot_claims_count_r12,cat_claims_amt_r12=c.cat_claims_amt_r12,theft_claims_amt_r12=c.theft_claims_amt_r12,others_claims_amt_r12=c.others_claims_amt_r12,tot_claims_amt_r12=c.tot_claims_amt_r12 "
							+ " from (select COALESCE(sum(gwp_od_r12),0) gwp_od_r12, COALESCE(sum(gwp_tp_r12),0) gwp_tp_r12, COALESCE(sum(gwp_r12),0) gwp_r12,COALESCE(sum(policy_count_r12),0) policy_count_r12,COALESCE(sum(cat_claims_count_r12),0) cat_claims_count_r12,COALESCE(sum(theft_claims_count_r12),0) theft_claims_count_r12,COALESCE(sum(others_claims_count_r12),0) others_claims_count_r12,COALESCE(sum(tot_claims_count_r12),0) tot_claims_count_r12,COALESCE(sum(cat_claims_amt_r12),0) cat_claims_amt_r12,COALESCE(sum(theft_claims_amt_r12),0) theft_claims_amt_r12,COALESCE(sum(others_claims_amt_r12),0) others_claims_amt_r12,COALESCE(sum(tot_claims_amt_r12),0) tot_claims_amt_r12,product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type "
							+ " from monthly_dashboard_grouped_new b where (b.INCEPTION_month) = "
							+ " to_char((TO_DATE('" + rs.getInt(1)
							+ "','YYYYMM') - INTERVAL '14 month' ),'YYYYMM')::numeric "
							+ " group by product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type ) as c "
							+ " where a.product_code=c.product_code and a.business_type=c.business_type and a.fuel_type=c.fuel_type and a.city_name=c.city_name and a.state_name=c.state_name and a.state_grouping=c.state_grouping and a.make=c.make and a.model_group=c.model_group and a.claim_type=c.claim_type and a.ncb_flag=c.ncb_flag and a.policy_type=c.policy_type "
							+ " and  a.INCEPTION_MONTH=" + rs.getInt(1) + " ";

					System.out.println(i + "<-- iteration count - > 14 updated r12 record sql-->" + updateSql);
					stmt1 = connection.createStatement();
					updateStatus = stmt1.executeUpdate(updateSql);

				}

				/* computing and loading freq & expected claims */
				updateSql = "update monthly_dashboard_grouped_new a set cat_freq_avg=(case when policy_count_r12<>0 then round(cast((cat_claims_count_r12/policy_count_r12) as numeric),2) else 0 end), theft_freq_avg=(case when policy_count_r12<>0 then round(cast((theft_claims_count_r12/policy_count_r12) as numeric),2) else 0 end), others_freq_avg=(case when policy_count_r12<>0 then round(cast((others_claims_count_r12/policy_count_r12) as numeric),2) else 0 end), "
						+ " cat_claim_cost_avg=(case when cat_claims_count_r12<>0 then round(cast((cat_claims_amt_r12/cat_claims_count_r12) as numeric),2) else 0 end), theft_claim_cost_avg=(case when theft_claims_count_r12<>0 then round(cast((theft_claims_amt_r12/theft_claims_count_r12) as numeric),2) else 0 end), others_claim_cost_avg=(case when others_claims_count_r12<>0 then round(cast((others_claims_amt_r12/others_claims_count_r12) as numeric),2) else 0 end) "
						+ " where a.INCEPTION_MONTH=" + rs.getInt(1) + " ";

				System.out.println(i + "<-- iteration count -updated avg freq & claim record sql-->" + updateSql);
				stmt2 = connection.createStatement();
				updateStatus = stmt2.executeUpdate(updateSql);

				/*
				 * updateSql =
				 * "update monthly_dashboard_grouped_new a set cat_freq_expected= COALESCE(cat_claim_count,cat_freq_avg)*policy_count, theft_freq_expected= COALESCE(theft_claim_count,theft_freq_avg)*policy_count, others_freq_expected= COALESCE(others_claims_count,others_freq_avg)*policy_count, "
				 * +
				 * " cat_claim_cost_expected=(COALESCE(cat_claim_count,cat_freq_avg)*policy_count*cat_claim_cost_avg*102),theft_claim_cost_expected=(COALESCE(theft_claim_count,theft_freq_avg)*policy_count*theft_claim_cost_avg*102),others_claim_cost_expected=(COALESCE(others_claims_count,others_freq_avg)*policy_count*others_claim_cost_avg*102) "
				 * + " where a.INCEPTION_MONTH="+rs.getInt(1)+" ";
				 */
				updateSql = "update monthly_dashboard_grouped_new a set cat_freq_expected=( case when cat_claim_count is null then (cat_freq_avg*policy_count) else cat_claim_count end), theft_freq_expected= ( case when theft_claim_count is null then (theft_freq_avg*policy_count) else theft_claim_count end) , others_freq_expected=( case when others_claims_count is null then (others_freq_avg*policy_count) else others_claims_count end)  "
						+
						// " cat_claim_cost_expected=(case when cat_claim_count is null then (( case
						// when cat_claim_count is null then (cat_freq_avg*policy_count) else
						// cat_claim_count end)*cat_claim_cost_avg*102) else
						// (cat_freq_avg*cat_claim_cost_avg*102)
						// end)(COALESCE(cat_claim_count,cat_freq_avg)*policy_count),theft_claim_cost_expected=(COALESCE(theft_claim_count,theft_freq_avg)*policy_count*theft_claim_cost_avg*102),others_claim_cost_expected=(COALESCE(others_claims_count,others_freq_avg)*policy_count*others_claim_cost_avg*102)
						// "+
						" where a.INCEPTION_MONTH=" + rs.getInt(1) + " ";

				System.out.println(i + "<-- iteration count -updated expected freq & claim record sql-->" + updateSql);
				updateStatus = stmt2.executeUpdate(updateSql);

				updateSql = "update monthly_dashboard_grouped_new a set cat_claim_cost_expected=( case when cat_claims_amt is null then (cat_claim_cost_avg*cat_freq_expected*102) else (cat_claims_amt*cat_freq_expected*102) end), theft_claim_cost_expected=( case when theft_claims_amt is null then (theft_claim_cost_avg*theft_freq_expected*102) else (theft_claims_amt*cat_freq_expected*102) end) , others_claim_cost_expected=( case when others_claims_amt is null then (others_claim_cost_avg*others_freq_expected*102) else (others_claims_amt*others_freq_expected*102) end)  "
						+
						// " cat_claim_cost_expected=(case when cat_claim_count is null then (( case
						// when cat_claim_count is null then (cat_freq_avg*policy_count) else
						// cat_claim_count end)*cat_claim_cost_avg*102) else
						// (cat_freq_avg*cat_claim_cost_avg*102)
						// end)(COALESCE(cat_claim_count,cat_freq_avg)*policy_count),theft_claim_cost_expected=(COALESCE(theft_claim_count,theft_freq_avg)*policy_count*theft_claim_cost_avg*102),others_claim_cost_expected=(COALESCE(others_claims_count,others_freq_avg)*policy_count*others_claim_cost_avg*102)
						// "+
						" where a.INCEPTION_MONTH=" + rs.getInt(1) + " ";

				System.out.println(i + "<-- iteration count -updated expected claim cost record sql-->" + updateSql);
				updateStatus = stmt2.executeUpdate(updateSql);

				// connection.commit();
			}

//	         System.out.println("--------------------------------------------"+generalKpiResponseList.size());
			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			System.out.println("Postgres initialize error, ex: " + e);
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (stmt1 != null) {
				stmt1.close();
			}
			if (stmt2 != null) {
				stmt2.close();
			}
			if (connection != null) {
				connection.close();
			}

		}

		return true;
	}

	@GetMapping("/computeAndLoadYTMDataNew")
	@ResponseBody
	public boolean computeAndLoadYTMDataNew() throws SQLException {

		String queryStr = "";
		int thresholdMonthYear = 0;
		Connection connection = null;
		Statement stmt = null;
		Statement stmt1 = null;
		long startTime = System.currentTimeMillis();
		try {
			Driver driverManager = (Driver) Class.forName("org.postgresql.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "postgres");
			info.put("password", "root");
			connection = driverManager.connect("jdbc:postgresql://localhost:5432/kylin_db", info);
			System.out.println("Connection status -------------------------->" + connection);

			stmt = connection.createStatement();
			queryStr = "select distinct INCEPTION_MONTH from monthly_dashboard_grouped_new order by INCEPTION_month asc";
			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			// updateRolling12Data(rs);
			int updateStatus = 0;
			int i = 0;
			while (rs.next()) {
				i++;

				/*
				 * String updateSql =
				 * "update monthly_dashboard_grouped_new a set gwp_od_ytm= c.gwp_od_ytm,gwp_tp_ytm = c.gwp_tp_ytm,policy_od_ytm= c.policy_od_ytm, "
				 * +
				 * " policy_tp_ytm= c.policy_tp_ytm, policy_ytm=(c.policy_od_ytm+c.policy_tp_ytm), cat_freq_expected_ytm=cat_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm),theft_freq_expected_ytm=c.theft_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm),others_freq_expected_ytm=c.others_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm), cat_claim_cost_ytm= cat_avg_cost_ytm*(cat_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm))*102,theft_claim_cost_ytm= theft_avg_cost_ytm*(theft_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm))*102,others_claim_cost_ytm= others_avg_cost_ytm*(others_avg_freq_ytm*(c.policy_od_ytm+c.policy_tp_ytm))*102   "
				 * +
				 * " from (select COALESCE(sum(gwp_od),0) gwp_od_ytm,COALESCE(sum(gwp_tp),0) gwp_tp_ytm,COALESCE(sum(policy_od),0) policy_od_ytm,COALESCE(sum(policy_tp),0)  policy_tp_ytm,sum(COALESCE(cat_claim_count,cat_freq_avg)) cat_avg_freq_ytm,sum(COALESCE(theft_claim_count,theft_freq_avg)) theft_avg_freq_ytm,sum(COALESCE(others_claims_count,others_freq_avg)) others_avg_freq_ytm, sum(COALESCE(cat_claims_amt,cat_claim_cost_avg)) cat_avg_cost_ytm,sum(COALESCE(theft_claims_amt,theft_claim_cost_avg)) theft_avg_cost_ytm,sum(COALESCE(others_claims_amt,others_claim_cost_avg)) others_avg_cost_ytm,   "
				 * +
				 * " product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type "
				 * + "  from monthly_dashboard_grouped_new b where ( case when substring('"+rs.
				 * getInt(1)
				 * +"',5,2)::numeric >03 then (substring(inception_month::text,1,4)=substring('"
				 * +rs.getInt(1)
				 * +"',1,4) and substring(inception_month::text,5,2)::numeric <= substring('"+rs
				 * .getInt(1)
				 * +"',5,2)::numeric and substring(inception_month::text,5,2)::numeric>03  ) else ((substring(inception_month::text,1,4)::numeric=substring('"
				 * +rs.getInt(1)
				 * +"',1,4)::numeric-1 AND substring(inception_month::text,5,2)::numeric > 03 )  or (substring(inception_month::text,1,4)=substring('"
				 * +rs.getInt(1)
				 * +"',1,4)  AND substring(inception_month::text,5,2)::numeric <=substring('"+rs
				 * .getInt(1)
				 * +"',5,2)::numeric ) ) end)  group by product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type ) c "
				 * +
				 * " where a.product_code=c.product_code and a.business_type=c.business_type and a.fuel_type=c.fuel_type and a.city_name=c.city_name and a.state_name=c.state_name and a.state_grouping=c.state_grouping and a.make=c.make and a.model_group=c.model_group and a.claim_type=c.claim_type and a.ncb_flag=c.ncb_flag and a.policy_type=c.policy_type and a.INCEPTION_MONTH='"
				 * +rs.getInt(1)+"' ";
				 */

				String updateSql = "update monthly_dashboard_grouped_new a set gwp_od_ytm= c.gwp_od_ytm,gwp_tp_ytm = c.gwp_tp_ytm,policy_od_ytm= c.policy_od_ytm, "
						+ " policy_tp_ytm= c.policy_tp_ytm, policy_ytm=(c.policy_od_ytm+c.policy_tp_ytm), cat_freq_expected_ytm=c.cat_freq_expected_ytm,theft_freq_expected_ytm=c.theft_freq_expected_ytm,others_freq_expected_ytm=c.others_freq_expected_ytm, cat_claim_cost_ytm= c.cat_claim_cost_ytm,theft_claim_cost_ytm=c.theft_claim_cost_ytm,others_claim_cost_ytm=c.others_claim_cost_ytm  "
						+ " from (select COALESCE(sum(gwp_od),0) gwp_od_ytm,COALESCE(sum(gwp_tp),0) gwp_tp_ytm,COALESCE(sum(policy_od),0) policy_od_ytm,COALESCE(sum(policy_tp),0)  policy_tp_ytm,sum(COALESCE(cat_freq_expected,0)) cat_freq_expected_ytm, sum(COALESCE(theft_freq_expected,0)) theft_freq_expected_ytm, sum(COALESCE(others_freq_expected,0)) others_freq_expected_ytm, sum(COALESCE(cat_claims_amt,0)) cat_claim_cost_ytm,  sum(COALESCE(theft_claims_amt,0)) theft_claim_cost_ytm,sum(COALESCE(others_claims_amt,0)) others_claim_cost_ytm,  "
						+ " product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type "
						+ "  from monthly_dashboard_grouped_new b where ( case when substring('" + rs.getInt(1)
						+ "',5,2)::numeric >03 then (substring(inception_month::text,1,4)=substring('" + rs.getInt(1)
						+ "',1,4) and substring(inception_month::text,5,2)::numeric <= substring('" + rs.getInt(1)
						+ "',5,2)::numeric and substring(inception_month::text,5,2)::numeric>03  ) else ((substring(inception_month::text,1,4)::numeric=substring('"
						+ rs.getInt(1)
						+ "',1,4)::numeric-1 AND substring(inception_month::text,5,2)::numeric > 03 )  or (substring(inception_month::text,1,4)=substring('"
						+ rs.getInt(1) + "',1,4)  AND substring(inception_month::text,5,2)::numeric <=substring('"
						+ rs.getInt(1)
						+ "',5,2)::numeric ) ) end)  group by product_code,business_type,fuel_type,city_name,state_name,state_grouping,make,model_group,claim_type,ncb_flag,policy_type ) c "
						+ " where a.product_code=c.product_code and a.business_type=c.business_type and a.fuel_type=c.fuel_type and a.city_name=c.city_name and a.state_name=c.state_name and a.state_grouping=c.state_grouping and a.make=c.make and a.model_group=c.model_group and a.claim_type=c.claim_type and a.ncb_flag=c.ncb_flag and a.policy_type=c.policy_type and a.INCEPTION_MONTH='"
						+ rs.getInt(1) + "' ";
				System.out.println(i + "<-- iteration count - updated r12 record sql-->" + updateSql);
				stmt1 = connection.createStatement();
				updateStatus = stmt1.executeUpdate(updateSql);

				/*
				 * }else{
				 * 
				 * }
				 */

				/*
				 * updateSql =
				 * "update monthly_dashboard_details_new a set freq=(case when r12_polices<>0 then round(cast((r12_claims/r12_polices) as numeric),2) else 0 end), expected_claim=(case when r12_polices<>0 then round(cast((r12_claims/r12_polices)*policy_od as numeric),0) else 0 end)"
				 * +
				 * " where a.INCEPTION_MONTH="+rs.getInt(1)+" AND a.INCEPTION_YEAR="+rs.getInt(2
				 * )+""; System.out.println(
				 * i+"<-- iteration count -updated freq & claim record sql-->"+updateSql );
				 * stmt2 = connection.createStatement(); updateStatus =
				 * stmt2.executeUpdate(updateSql);
				 */

				// connection.commit();
			}

//	         System.out.println("--------------------------------------------"+generalKpiResponseList.size());
			// System.out.println(jsArray.toString());
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			System.out.println("Postgres initialize error, ex: " + e);
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (stmt1 != null) {
				stmt1.close();
			}
			if (connection != null) {
				connection.close();
			}

		}

		return true;
	}

	// for monthly dash board table 1
	@GetMapping("/getMonthlyDashboardDetailsDataFromKylin")
	@ResponseBody
	public List<MonthlyDashboardDetailsResponse> getMonthlyDashboardDetailsDataFromKylin(
			MonthlyDashboardFilterRequest req,String currentMonth) throws SQLException {

		Connection connection = null;
		Statement stmt = null;
		long startTime = System.currentTimeMillis();
		List<MonthlyDashboardDetailsResponse> monthlyResponse = new ArrayList<>();
		
		try {
			MonthFiltersResponse monthFiltersResponse = getMonthFiltersFromCM(currentMonth);
			currentMonth = monthFiltersResponse.getCurrentMonth();
			String preMonth = monthFiltersResponse.getPreviousMonth();
			String ytmStartMonth = monthFiltersResponse.getPytmStartMonth();
			String ytmEndMonth = monthFiltersResponse.getPytmEndMonth();
			String pytmStartMonth = monthFiltersResponse.getPytmStartMonth();
			String pytmEndMonth = monthFiltersResponse.getPytmEndMonth();

//			SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
//			Date curMon = sd.parse(currentMonth);
//			System.out.println(curMon);
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(curMon);
//			cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) - 14));

			String inceptionMonthThreshold = monthFiltersResponse.getInceptionMonthThreshold();

			System.out.println("threshold month from current month ---> " + inceptionMonthThreshold);

			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			stmt = connection.createStatement();

			// current month with btype
			String queryStr = "select btype,sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,"
					+ " sum(policycount) policy_count,"
					+ " sum(policycount_od) policycount_od, sum(policycount_tp) policycount_tp"
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1 " + " where inception_month=" + currentMonth;

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by btype";

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {
					System.out.println();

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));

					monthlyResponse.add(res);

				}

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// current month wo btype
			queryStr = "select sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,"
					+ " sum(policycount) policy_count,"
					+ " sum(policycount_od) policycount_od, sum(policycount_tp) policycount_tp"
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1 " + " where inception_month=" + currentMonth;

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("CM");
				res.setTotalGwp(rs.getDouble(1));
				res.setGwpOd(rs.getDouble(2));
				res.setGwpTp(rs.getDouble(3));
				res.setTotalPolicy(rs.getDouble(4));
				res.setPolicyOd(rs.getDouble(5));
				res.setPolicyTp(rs.getDouble(6));
				monthlyResponse.add(res);

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// pre month with btype
			queryStr = "select btype,sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,"
					+ "sum(policycount) policy_count,"
					+ " sum(policycount_od) policycount_od, sum(policycount_tp) policycount_tp"
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1 " + " where inception_month=" + preMonth;

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by btype";

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {
					System.out.println();

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));

					monthlyResponse.add(res);

				}

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// pre month wo btype
			queryStr = "select sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,"
					+ "sum(policycount) policy_count,"
					+ " sum(policycount_od) policycount_od, sum(policycount_tp) policycount_tp"
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1 " + " where inception_month=" + preMonth;

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("PM");
				res.setTotalGwp(rs.getDouble(1));
				res.setGwpOd(rs.getDouble(2));
				res.setGwpTp(rs.getDouble(3));
				res.setTotalPolicy(rs.getDouble(4));
				res.setPolicyOd(rs.getDouble(5));
				res.setPolicyTp(rs.getDouble(6));
				monthlyResponse.add(res);

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// ytm with btype
			queryStr = "select btype,sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,"
					+ "sum(policycount) policy_count,"
					+ " sum(policycount_od) policycount_od, sum(policycount_tp) policycount_tp"
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1 " + " where inception_month>=" + ytmStartMonth
					+ " and inception_month<=" + ytmEndMonth;

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by btype";

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {
					System.out.println();

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));

					monthlyResponse.add(res);

				}

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// ytm month wo btype
			queryStr = "select sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,"
					+ "sum(policycount) policy_count,"
					+ " sum(policycount_od) policycount_od, sum(policycount_tp) policycount_tp"
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1 " + " where inception_month>=" + ytmStartMonth
					+ " and inception_month<=" + ytmEndMonth;

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("YTM");
				res.setTotalGwp(rs.getDouble(1));
				res.setGwpOd(rs.getDouble(2));
				res.setGwpTp(rs.getDouble(3));
				res.setTotalPolicy(rs.getDouble(4));
				res.setPolicyOd(rs.getDouble(5));
				res.setPolicyTp(rs.getDouble(6));
				monthlyResponse.add(res);

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// pytm with btype
			queryStr = "select btype,sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,"
					+ "sum(policycount) policy_count,"
					+ " sum(policycount_od) policycount_od, sum(policycount_tp) policycount_tp"
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1 " + " where inception_month>=" + pytmStartMonth
					+ " and inception_month<=" + pytmEndMonth;

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			queryStr += " group by btype";

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {
					System.out.println();

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					res.setTotalPolicy(rs.getDouble(5));
					res.setPolicyOd(rs.getDouble(6));
					res.setPolicyTp(rs.getDouble(7));

					monthlyResponse.add(res);

				}

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// pytm month wo btype
			queryStr = "select sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp,"
					+ "sum(policycount) policy_count,"
					+ " sum(policycount_od) policycount_od, sum(policycount_tp) policycount_tp"
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1 " + " where inception_month>=" + pytmStartMonth
					+ " and inception_month<=" + pytmEndMonth;

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				queryStr += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("PYTM");
				res.setTotalGwp(rs.getDouble(1));
				res.setGwpOd(rs.getDouble(2));
				res.setGwpTp(rs.getDouble(3));
				res.setTotalPolicy(rs.getDouble(4));
				res.setPolicyOd(rs.getDouble(5));
				res.setPolicyTp(rs.getDouble(6));
				monthlyResponse.add(res);

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// exp freq, sev cm

			String exFreqAndSevBaseQuery = "select btype" + " ,sum(case when claim_cat=0 and inception_month > "
					+ inceptionMonthThreshold + " then ef_cat_co else claim_cat end) ef_cat_co"
					+ " ,sum(case when claim_theft=0 and inception_month > " + inceptionMonthThreshold
					+ "  then ef_theft_co else claim_theft end) ef_theft_co"
					+ " ,sum(case when claim_others=0 and inception_month > " + inceptionMonthThreshold
					+ "  then ef_others_co else claim_others end) ef_others_co"
					+ " ,sum(case when total_claim_count=0 and inception_month > " + inceptionMonthThreshold
					+ " then ef_total_co else total_claim_count end) ef_total_co"
					+ " ,sum(case when claim_amount_cat=0 and inception_month > " + inceptionMonthThreshold
					+ " then exp_cat_cost else claim_amount_cat end) exp_cat_cost"
					+ " ,sum(case when claim_amount_theft=0 and inception_month > " + inceptionMonthThreshold
					+ " then exp_theft_cost else claim_amount_theft end) exp_theft_cost"
					+ " ,sum(case when claim_amount_others=0 and inception_month > " + inceptionMonthThreshold
					+ " then exp_others_cost else claim_amount_others end) exp_others_cost"
					+ " ,sum(case when total_claim_amount=0 and inception_month > " + inceptionMonthThreshold
					+ " then exp_total_cost else total_claim_amount end) exp_total_cost" + " from("
					+ " select btype,inception_month,channel,subchannel,state_grouping,make,modelgroup,fueltype,ncb_flag,"
					+ " sum(claim_cat) claim_cat,sum(claim_theft) claim_theft,sum(claim_others) claim_others,sum(total_claim_count) total_claim_count,"
					+ " sum(ef_cat_co) ef_cat_co ,sum(ef_theft_co) ef_theft_co ,sum(ef_others_co) ef_others_co,sum(ef_total_co) ef_total_co,"
					+ " sum(claim_amount_cat) claim_amount_cat,sum(claim_amount_theft) claim_amount_theft,sum(claim_amount_others) claim_amount_others,sum(total_claim_amount) total_claim_amount,"
					+ " sum(exp_cat_cost) exp_cat_cost,sum(exp_theft_cost) exp_theft_cost,sum(exp_others_cost) exp_others_cost,sum(exp_total_cost) exp_total_cost"
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1 ";

			String exFreqFilterCond = " ";

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				exFreqFilterCond += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				exFreqFilterCond += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				exFreqFilterCond += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				exFreqFilterCond += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				exFreqFilterCond += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				exFreqFilterCond += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				exFreqFilterCond += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			String exFreqSevGroupBy = " group by"
					+ " btype,inception_month,channel,subchannel,state_grouping,make,modelgroup,fueltype,ncb_flag"
					+ " )" + " group by btype;";

			// ex freq sev cm
			queryStr = exFreqAndSevBaseQuery + " where inception_month= " + currentMonth + exFreqFilterCond
					+ exFreqSevGroupBy;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				}

				// connection.commit();
			}
			rs.close();

			// ex freq sev pm
			queryStr = exFreqAndSevBaseQuery + " where inception_month= " + preMonth + exFreqFilterCond
					+ exFreqSevGroupBy;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				}

				// connection.commit();
			}
			rs.close();

			// ex freq sev YTM
			queryStr = exFreqAndSevBaseQuery + " where inception_month>= " + ytmStartMonth + " and inception_month<= "
					+ ytmEndMonth + exFreqFilterCond + exFreqSevGroupBy;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("YTM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				}

				// connection.commit();
			}
			rs.close();

			// ex freq sev PYTM
			queryStr = exFreqAndSevBaseQuery + " where inception_month>= " + pytmStartMonth + " and inception_month<="
					+ pytmEndMonth + exFreqFilterCond + exFreqSevGroupBy;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {

				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setClaimType("CAT");
					res.setFreqOd(rs.getDouble(2));
					res.setSevOd(rs.getDouble(6));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setClaimType("THEFT");
					res.setFreqOd(rs.getDouble(3));
					res.setSevOd(rs.getDouble(7));

					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("PYTM");
					res.setClaimType("OTHERS");
					res.setFreqOd(rs.getDouble(4));
					res.setSevOd(rs.getDouble(8));

					monthlyResponse.add(res);

				}

				// connection.commit();
			}
			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				connection.close();
			}

		}

		return monthlyResponse;
	}

	public MonthFiltersResponse getMonthFiltersFromCM(String currentMonth) throws ParseException {		
		MonthFiltersResponse res = new MonthFiltersResponse();
		try {
			SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
			Calendar cal = Calendar.getInstance();
			if(currentMonth==null || StringUtils.isEmpty(currentMonth)) {
				cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH))-1);
				currentMonth = sd.format(cal.getTime());
			}
			
			if(currentMonth.contains("/")) {
				SimpleDateFormat sd1 = new SimpleDateFormat("MM/yyyy");
//				sd.applyPattern("yyyyMM");
				sd1.parse(currentMonth);
				cal.setTime(sd1.parse(currentMonth));
				currentMonth = sd.format(cal.getTime());
			}
			
			Date curMon = sd.parse(currentMonth);
			System.out.println("current month ---> "+curMon);
			
			
			//current month - jan 2021 is previous completed month ex for if date is feb 4 
			res.setCurrentMonth(currentMonth);
			
			//t-14 month ex - nov 2019 if jan 2021 is cm
			cal.setTime(new Date());
			cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) - 15));
			res.setInceptionMonthThreshold(sd.format(cal.getTime()));
			
			//Previous month - dec 2012
			curMon = sd.parse(currentMonth);
			if(cal.get(Calendar.MONTH)<=0) {
				
			}
			cal.setTime(curMon);
			cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH))-1);
			res.setPreviousMonth(sd.format(cal.getTime()));
			
			//YTM
			curMon = sd.parse(currentMonth);
			cal.setTime(curMon);										//YTM Start Month ex - apr 2021 if cm may 2021
			if(cal.get(Calendar.MONTH)>3) { 
				cal.set(Calendar.MONTH, 3);
				res.setYtmStartMonth(sd.format(cal.getTime()));
			}else {														//YTM Start Month ex - apr 2020 if cm jan 2021
				cal.set(Calendar.MONTH, 3);
				cal.set(Calendar.YEAR, (cal.get(Calendar.YEAR)-1));
				res.setYtmStartMonth(sd.format(cal.getTime()));
			}
			
			//YTM End month = current month ex jan 2021 or may 2021
			res.setYtmEndMonth(currentMonth);
			
			//PYTM Start Month - apr 2019 if cm jan 2021
			curMon = sd.parse(currentMonth);
			cal.setTime(curMon);		
			if(cal.get(Calendar.MONTH)>3) {								//PYTM Start Month ex - apr 2020 if cm may 2021
				cal.set(Calendar.MONTH, 3);
				cal.set(Calendar.YEAR, (cal.get(Calendar.YEAR)-1));
				res.setPytmStartMonth(sd.format(cal.getTime()));
			}else {														//PYTM Start Month ex - apr 2019 if cm jan 2021
				cal.set(Calendar.MONTH, 3);
				cal.set(Calendar.YEAR, (cal.get(Calendar.YEAR)-2));
				res.setPytmStartMonth(sd.format(cal.getTime()));
			}
			
			//PYTM End month ex may 2020 or jan 2020
			curMon = sd.parse(currentMonth);
			cal.setTime(curMon);		
			cal.set(Calendar.YEAR, (cal.get(Calendar.YEAR)-1));
			res.setPytmEndMonth(sd.format(cal.getTime()));
			
			//considering policy uw start month
			curMon = sd.parse(currentMonth);
			cal.setTime(curMon);										//Start Month ex - apr 2021 if cm may 2021
			if(cal.get(Calendar.MONTH)>3) { 
				cal.set(Calendar.MONTH, 3);
				res.setConsideringPolicyUWStartMonth(sd.format(cal.getTime()));
			}else {														//Start Month ex - apr 2020 if cm jan 2021
				cal.set(Calendar.MONTH, 3);
				cal.set(Calendar.YEAR, (cal.get(Calendar.YEAR)-1));
				res.setConsideringPolicyUWStartMonth(sd.format(cal.getTime()));
			}
			
			//considering policy uw end month
			curMon = sd.parse(currentMonth);
			cal.setTime(curMon);
			cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH))+1);
			res.setConsideringPolicyUWEndMonth(sd.format(cal.getTime()));
			
			//Excluding business uw start month
			curMon = sd.parse(currentMonth);
			cal.setTime(curMon);														//Start Month ex - apr 2021 if cm may 2021
			if(cal.get(Calendar.MONTH)>3) { 
				cal.set(Calendar.MONTH, 3);
				res.setExcludingBusinessUWStartMonth(sd.format(cal.getTime()));
			}else {																		//Start Month ex - apr 2020 if cm jan 2021
				cal.set(Calendar.MONTH, 3);
				cal.set(Calendar.YEAR, (cal.get(Calendar.YEAR)-1));
				res.setExcludingBusinessUWStartMonth(sd.format(cal.getTime()));
			}
			
			//Excluding business uw end month = current month
			res.setExcludingBusinessUWEndMonth(currentMonth);
			
		}catch(ParseException e) {
			
			
		}
		
		return res;

	}

	// for monthly dash board table 2
	@GetMapping("/getMonthlyDashboardDetailsData2FromKylin")
	@ResponseBody
	public List<MonthlyDashboardDetailsResponse> getMonthlyDashboardDetailsData2FromKylin(
			MonthlyDashboardFilterRequest req,@RequestParam(required = false) String currentMonth) throws SQLException {

		Connection connection = null;
		Statement stmt = null;
		long startTime = System.currentTimeMillis();
		List<MonthlyDashboardDetailsResponse> monthlyResponse = new ArrayList<>();

		try {

			MonthFiltersResponse months = getMonthFiltersFromCM(currentMonth);

//			SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
//			Date curMon = sd.parse(currentMonth);
//			System.out.println(curMon);
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(curMon);
//			cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) - 14));

			String inceptionMonthThreshold = months.getInceptionMonthThreshold();

			System.out.println("threshold month from current month ---> " + inceptionMonthThreshold);

			Driver driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
			Properties info = new Properties();
			info.put("user", "ADMIN");
			info.put("password", "KYLIN");
			connection = driverManager
					.connect("jdbc:kylin://" + RMSConstants.KYLIN_RS_BASE_IP_AND_PORT + "/learn_kylin", info);
			System.out.println("Connection status -------------------------->" + connection);
			stmt = connection.createStatement();

			String mtfyBaseGwpWithBtypeQuery = "select "
					+ " btype,sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp "
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1  ";

			String mtfyBaseGwpWOBtypeQuery = "select " + " sum(gwp) total_gwp,sum(gwp_od) gwp_od,sum(gwp_tp) gwp_tp "
					+ " from DWH_MONTHLY_DASHBOARD_TABLE1_1  ";

			String mtfyBaseGepGicWithBtypeQuery = "select " + " btype, " + " sum(MTFY_GEPOD) gep_od, "
					+ " sum(MTFY_GEPTP) gep_tp, " + " sum(MTFY_GEPOD)+sum(MTFY_GEPTP) total_gep, "
					+ " sum(mtfy_gicod_cat) gic_od_cat, " + " sum(mtfy_gicod_theft) gic_od_theft, "
					+ " sum(mtfy_gicod_others) gic_od_others " + " from DWH_MONTHLY_DASHBOARD_TABLE3_2 ";

			String mtfyBaseGepGicWOBtypeQuery = "select " + " sum(MTFY_GEPOD) gep_od, " + " sum(MTFY_GEPTP) gep_tp, "
					+ " sum(MTFY_GEPOD)+sum(MTFY_GEPTP) total_gep, " + " sum(mtfy_gicod_cat) gic_od_cat, "
					+ " sum(mtfy_gicod_theft) gic_od_theft, " + " sum(mtfy_gicod_others) gic_od_others "
					+ " from DWH_MONTHLY_DASHBOARD_TABLE3_2 ";

			String groupbyBtype = " group by btype ";

			String consideringPolicyMonthCond = " where inception_month>= " + months.getConsideringPolicyUWStartMonth()
					+ " and inception_month<= " + months.getConsideringPolicyUWEndMonth() + " ";

			String excludingBusinessUWMonthCond = " where inception_month>= "
					+ months.getExcludingBusinessUWStartMonth() + " and inception_month<= "
					+ months.getExcludingBusinessUWEndMonth() + " ";

			String filtersCond = " ";

			if (req != null && req.getChannel() != null && !req.getChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getChannel().size(); i++) {
					vals += "'" + req.getChannel().get(i).trim() + "'";
					if (i != req.getChannel().size() - 1) {
						vals += ",";
					}
				}
				filtersCond += " and  TRIM(channel) in (" + vals + ")";
			}

			if (req != null && req.getSubChannel() != null && !req.getSubChannel().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getSubChannel().size(); i++) {
					vals += "'" + req.getSubChannel().get(i).trim() + "'";
					if (i != req.getSubChannel().size() - 1) {
						vals += ",";
					}
				}
				filtersCond += " and TRIM(sub_channel) in (" + vals + ")";
			}

			if (req != null && req.getMake() != null && !req.getMake().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getMake().size(); i++) {
					vals += "'" + req.getMake().get(i).trim() + "'";
					if (i != req.getMake().size() - 1) {
						vals += ",";
					}
				}
				filtersCond += " and TRIM(make) in (" + vals + ")";
			}

			if (req != null && req.getModelGroup() != null && !req.getModelGroup().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getModelGroup().size(); i++) {
					vals += "'" + req.getModelGroup().get(i).trim() + "'";
					if (i != req.getModelGroup().size() - 1) {
						vals += ",";
					}
				}
				filtersCond += " and TRIM(model_group) in (" + vals + ")";
			}

			if (req != null && req.getGeo() != null && !req.getGeo().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getGeo().size(); i++) {
					vals += "'" + req.getGeo().get(i).trim() + "'";
					if (i != req.getGeo().size() - 1) {
						vals += ",";
					}
				}
				filtersCond += " and TRIM(state_grouping) in (" + vals + ")";
			}

			if (req != null && req.getFuelType() != null && !req.getFuelType().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getFuelType().size(); i++) {
					vals += "'" + req.getFuelType().get(i).trim() + "'";
					if (i != req.getFuelType().size() - 1) {
						vals += ",";
					}
				}
				filtersCond += " and TRIM(fuel_type) in (" + vals + ")";
			}

			if (req != null && req.getNcbFlag() != null && !req.getNcbFlag().isEmpty()) {
				String vals = "";
				for (int i = 0; i < req.getNcbFlag().size(); i++) {
					vals += "'" + req.getNcbFlag().get(i).trim() + "'";
					if (i != req.getNcbFlag().size() - 1) {
						vals += ",";
					}
				}
				filtersCond += " and TRIM(ncb_flag) in (" + vals + ")";
			}

			// Considering Policy UW in current month with btype gwp
			startTime = System.currentTimeMillis();

			String queryStr = mtfyBaseGwpWithBtypeQuery + consideringPolicyMonthCond + filtersCond + groupbyBtype;

			System.out.println("queryStr------------------------------ " + queryStr);
			ResultSet rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					monthlyResponse.add(res);

				}

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			startTime = System.currentTimeMillis();
			// Considering Policy UW in current month wo btype gwp
			queryStr = mtfyBaseGwpWOBtypeQuery + consideringPolicyMonthCond + filtersCond;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("CONSIDER_POLICY_UW");
				res.setTotalGwp(rs.getDouble(1));
				res.setGwpOd(rs.getDouble(2));
				res.setGwpTp(rs.getDouble(3));
				monthlyResponse.add(res);
			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			startTime = System.currentTimeMillis();

			////////////////////////////////////////////

			// Considering Policy UW in current month gep, gic with btype
			queryStr = mtfyBaseGepGicWithBtypeQuery + consideringPolicyMonthCond + filtersCond + groupbyBtype;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setGepOd(rs.getDouble(2));
					res.setGepTp(rs.getDouble(3));
					res.setTotalGep(rs.getDouble(4));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setClaimType("CAT");
					res.setGicOd(rs.getDouble(5));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setClaimType("THEFT");
					res.setGicOd(rs.getDouble(6));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setClaimType("OTHERS");
					res.setGicOd(rs.getDouble(7));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setGepOd(rs.getDouble(2));
					res.setGepTp(rs.getDouble(3));
					res.setTotalGep(rs.getDouble(4));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setClaimType("CAT");
					res.setGicOd(rs.getDouble(5));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setClaimType("THEFT");
					res.setGicOd(rs.getDouble(6));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setClaimType("OTHERS");
					res.setGicOd(rs.getDouble(7));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setGepOd(rs.getDouble(2));
					res.setGepTp(rs.getDouble(3));
					res.setTotalGep(rs.getDouble(4));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setClaimType("CAT");
					res.setGicOd(rs.getDouble(5));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setClaimType("THEFT");
					res.setGicOd(rs.getDouble(6));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("CONSIDER_POLICY_UW");
					res.setClaimType("OTHERS");
					res.setGicOd(rs.getDouble(7));
					monthlyResponse.add(res);

				}

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// Considering Policy UW in current month gep, gic with btype
			queryStr = mtfyBaseGepGicWOBtypeQuery + consideringPolicyMonthCond + filtersCond;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("CONSIDER_POLICY_UW");
				res.setGepOd(rs.getDouble(1));
				res.setGepTp(rs.getDouble(2));
				res.setTotalGep(rs.getDouble(3));
				monthlyResponse.add(res);

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			//////////////////////////////////////////////////////////////////

			// Excluding Business UW in current month with btype
			startTime = System.currentTimeMillis();

			queryStr = mtfyBaseGwpWithBtypeQuery + excludingBusinessUWMonthCond + filtersCond + groupbyBtype;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setTotalGwp(rs.getDouble(2));
					res.setGwpOd(rs.getDouble(3));
					res.setGwpTp(rs.getDouble(4));
					monthlyResponse.add(res);

				}

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			startTime = System.currentTimeMillis();
			// Excluding Business UW in current month wo btype
			queryStr = mtfyBaseGwpWOBtypeQuery + excludingBusinessUWMonthCond + filtersCond;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("EXCLUDE_BUSINESS_UW");
				res.setTotalGwp(rs.getDouble(1));
				res.setGwpOd(rs.getDouble(2));
				res.setGwpTp(rs.getDouble(3));
				monthlyResponse.add(res);
			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			startTime = System.currentTimeMillis();

			// Excluding Business in current month gep, gic with btype
			queryStr = mtfyBaseGepGicWithBtypeQuery + excludingBusinessUWMonthCond + filtersCond + groupbyBtype;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();
				System.out.println("rs.getString(1)-->" + rs.getString(1));

				if (rs.getString(1).equals("New")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setGepOd(rs.getDouble(2));
					res.setGepTp(rs.getDouble(3));
					res.setTotalGep(rs.getDouble(4));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setClaimType("CAT");
					res.setGicOd(rs.getDouble(5));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setClaimType("THEFT");
					res.setGicOd(rs.getDouble(6));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setClaimType("OTHERS");
					res.setGicOd(rs.getDouble(7));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Roll-Over")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setGepOd(rs.getDouble(2));
					res.setGepTp(rs.getDouble(3));
					res.setTotalGep(rs.getDouble(4));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setClaimType("CAT");
					res.setGicOd(rs.getDouble(5));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setClaimType("THEFT");
					res.setGicOd(rs.getDouble(6));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setClaimType("OTHERS");
					res.setGicOd(rs.getDouble(7));
					monthlyResponse.add(res);

				} else if (rs.getString(1).equals("Renewal")) {

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setGepOd(rs.getDouble(2));
					res.setGepTp(rs.getDouble(3));
					res.setTotalGep(rs.getDouble(4));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setClaimType("CAT");
					res.setGicOd(rs.getDouble(5));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setClaimType("THEFT");
					res.setGicOd(rs.getDouble(6));
					monthlyResponse.add(res);

					res = new MonthlyDashboardDetailsResponse();

					res.setBusinessType(rs.getString(1));
					res.setDetailType("EXCLUDE_BUSINESS_UW");
					res.setClaimType("OTHERS");
					res.setGicOd(rs.getDouble(7));
					monthlyResponse.add(res);

				}

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

			// Excluding Business UW in current month gep, gic with btype
			queryStr = mtfyBaseGepGicWOBtypeQuery + excludingBusinessUWMonthCond + filtersCond;

			System.out.println("queryStr------------------------------ " + queryStr);
			rs = stmt.executeQuery(queryStr);
			System.out.println("START------------------------------ ");

			while (rs.next()) {
				MonthlyDashboardDetailsResponse res = new MonthlyDashboardDetailsResponse();

				res = new MonthlyDashboardDetailsResponse();

				res.setBusinessType("All");
				res.setDetailType("EXCLUDE_BUSINESS_UW");
				res.setGepOd(rs.getDouble(1));
				res.setGepTp(rs.getDouble(2));
				res.setTotalGep(rs.getDouble(3));
				monthlyResponse.add(res);

			}
			rs.close();
			System.out.println("Query execution time " + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				connection.close();
			}

		}
		return monthlyResponse;
	}

}
