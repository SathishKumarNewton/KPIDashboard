
package com.prodian.rsgirms.dashboard.service;

import java.util.List;

import com.prodian.rsgirms.dashboard.model.UserDashboard;

/**
 * @author CSS
 *
 */
public interface DashboardService {
	
	List<UserDashboard> getUserDashboardList(Integer userId);

}
