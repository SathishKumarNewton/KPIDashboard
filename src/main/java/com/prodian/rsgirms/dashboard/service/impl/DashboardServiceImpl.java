
package com.prodian.rsgirms.dashboard.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.prodian.rsgirms.dashboard.model.ConfigTableName;
import com.prodian.rsgirms.dashboard.model.Dashboard;
import com.prodian.rsgirms.dashboard.model.MultiUserDashboard;
import com.prodian.rsgirms.dashboard.model.SqoopConfiguation;
import com.prodian.rsgirms.dashboard.model.SqoopConfigurationRequest;
import com.prodian.rsgirms.dashboard.model.UserDashboard;
import com.prodian.rsgirms.dashboard.repository.DashboardRepository;
import com.prodian.rsgirms.dashboard.repository.SqoopConfigRepository;
import com.prodian.rsgirms.dashboard.repository.UserDashboardRepository;
import com.prodian.rsgirms.dashboard.response.UserDashboardResponse;
import com.prodian.rsgirms.dashboard.service.DashboardService;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.repository.UserRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private UserDashboardRepository userDashboardRepository;

	@Autowired
	private DashboardRepository dashboardRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SqoopConfigRepository sqoopConfigRepository;

	public List<UserDashboard> getUserDashboardList(Integer userId) {
		return userDashboardRepository.findByUserId(userId);
	}

	public void saveUserDashBoardMapping(UserDashboard userDashboard) {
		userDashboardRepository.save(userDashboard);
	}

	public List<Dashboard> getAllDashboards() {
		return dashboardRepository.findAll();
	}

	public void deleteDashboard(List<Dashboard> dashboards) {
		List<Integer> ids = dashboards.stream().map(Dashboard::getId).collect(Collectors.toList());
		for (Integer id : ids) {
			List<UserDashboard> dashboardMappings = userDashboardRepository.findByDashboardId(id);
			for (UserDashboard mapping : dashboardMappings) {
				userDashboardRepository.delete(mapping);
			}
			dashboardRepository.deleteById(id);

		}
	}

	public void saveDashboard(Dashboard dashboard) {
		dashboardRepository.save(dashboard);
	}

	public List<UserDashboardResponse> getUserDashboardListResponse(Integer userId) {
//		List<UserDashboard> dashboards = userDashboardRepository.findByUserId(userId);
		List<UserDashboard> dashboards = userDashboardRepository
				.findAll(Sort.by(Sort.Direction.DESC, "userDashboardId"));
		List<UserDashboardResponse> response = new ArrayList<>();
		for (UserDashboard dashboard : dashboards) {
			UserDashboardResponse res = new UserDashboardResponse();
			res.setDashboardId(dashboard.getDashboardId());
			res.setDashboardName(dashboard.getDashboardName());
			res.setDashboardURL(dashboard.getDashboardURL());
			res.setUserId(dashboard.getUserId());
			res.setUserDashboardId(dashboard.getUserDashboardId());
			User user = userRepository.findById(dashboard.getUserId());
			if (user != null) {
				res.setUser(user);
			}
			response.add(res);
		}
		return response;
	}
	
	public List<UserDashboardResponse> getUserDashboardListResponseByUserId(Integer userId) {
		List<UserDashboard> dashboards = userDashboardRepository.findByUserId(userId);
		List<UserDashboardResponse> response = new ArrayList<>();
		for (UserDashboard dashboard : dashboards) {
			UserDashboardResponse res = new UserDashboardResponse();
			res.setDashboardId(dashboard.getDashboardId());
			res.setDashboardName(dashboard.getDashboardName());
			res.setDashboardURL(dashboard.getDashboardURL());
			res.setUserId(dashboard.getUserId());
			res.setUserDashboardId(dashboard.getUserDashboardId());
			User user = userRepository.findById(dashboard.getUserId());
			if (user != null) {
				res.setUser(user);
			}
			response.add(res);
		}
		return response;
	}

	public Dashboard getDashboardById(Integer id) {
		return dashboardRepository.findDashboardById(id);
	}

	public void saveMultiUserDashBoardMapping(MultiUserDashboard multiUserDashboard) {
		for (Integer userId : multiUserDashboard.getUserIds()) {
			for (Integer dashboardId : multiUserDashboard.getDashboardIds()) {
				List<UserDashboard> mappings = userDashboardRepository.findByUserId(userId);
				System.out.println(mappings);
				System.out.println();
				List<Integer> mappedIds = mappings.stream().filter(f -> f.getDashboardId() == dashboardId)
						.map(m -> m.getDashboardId()).collect(Collectors.toList());
				if (mappedIds != null && !mappedIds.isEmpty()) {
					continue;
				}
				UserDashboard userDashboard = new UserDashboard();
				userDashboard.setUserId(userId);
				userDashboard.setDashboardId(dashboardId);
				Dashboard dashboard = dashboardRepository.findDashboardById(dashboardId);
				userDashboard.setDashboardName(dashboard.getDashboardName());
				userDashboard.setDashboardURL(dashboard.getDashboardURL());
				userDashboardRepository.save(userDashboard);
			}
		}

	}

	public void deleteUserDashboardMapping(List<UserDashboard> userDashboards) {
		for (UserDashboard userDashboard : userDashboards) {
			UserDashboard dashboard = userDashboardRepository.findByUserDashboardId(userDashboard.getUserDashboardId());
			if (dashboard != null) {
				userDashboardRepository.delete(dashboard);
			}
		}
	}

	public void saveScoopConfig(SqoopConfigurationRequest req) {
		if(req!=null && !req.getTableName().isEmpty()) {
			for(ConfigTableName table : req.getTableName()) {
				if(table !=null && table.getSourceTableName()!=null && table.getDestinationTableName()!=null) {
					SqoopConfiguation config = new SqoopConfiguation();
					config.setSourceSchemaUrl(req.getSourceSchemaUrl());
					config.setSourceSchemaUserName(req.getSourceSchemaUserName());
					config.setSourceSchemaPassword(Base64.getEncoder().encodeToString(req.getSourceSchemaPassword().getBytes()));
					config.setDestinationSchemaName(req.getDestinationSchemaName());
					config.setSourceTableName(table.getSourceTableName());
					config.setDestinationTableName(table.getDestinationTableName());
					config.setIsActive(true);
					sqoopConfigRepository.save(config);
				}
			}
		}		
	}

	public Set<String> getSqoopConfigUserNames() {
		Set<String> userNames = sqoopConfigRepository.findAll().stream()
				.map(SqoopConfiguation::getSourceSchemaUserName).collect(Collectors.toSet());
		return userNames;
	}

	public SqoopConfigurationRequest getSqoopConfigByUserName(String userName) {
		List<SqoopConfiguation> sqoopConfigurations = sqoopConfigRepository.findBySourceSchemaUserName(userName);
		SqoopConfigurationRequest req = new SqoopConfigurationRequest();
		List<ConfigTableName> tables = new ArrayList<>();
		for (SqoopConfiguation sqoopConfiguation : sqoopConfigurations) {
			req.setSourceSchemaUrl(sqoopConfiguation.getSourceSchemaUrl());
			req.setSourceSchemaUserName(sqoopConfiguation.getSourceSchemaUserName());
			req.setDestinationSchemaName(sqoopConfiguation.getDestinationSchemaName());
			req.setIsActive(sqoopConfiguation.getIsActive());
			ConfigTableName table = new ConfigTableName();
			table.setSourceTableName(sqoopConfiguation.getSourceTableName());
			table.setDestinationTableName(sqoopConfiguation.getDestinationTableName());
			tables.add(table);
		}
		req.setTableName(tables);
		return req;
	}

	public void editSqoopConfig(SqoopConfigurationRequest req) {
		if(req==null || req.getSourceSchemaUserName()==null) {
			return;
		}
		List<SqoopConfiguation> sqoopConfigurations = sqoopConfigRepository.findBySourceSchemaUserName(req.getSourceSchemaUserName());
		sqoopConfigRepository.deleteAll(sqoopConfigurations);
		saveScoopConfig(req);
		
	}

	public void deleteSqoopConfig(String userName) {
		List<SqoopConfiguation> sqoopConfigurations = sqoopConfigRepository.findBySourceSchemaUserName(userName);
		for (SqoopConfiguation sqoopConfiguation : sqoopConfigurations) {
			sqoopConfiguation.setIsActive(false);
			sqoopConfigRepository.save(sqoopConfiguation);
		}
		
	}

}
