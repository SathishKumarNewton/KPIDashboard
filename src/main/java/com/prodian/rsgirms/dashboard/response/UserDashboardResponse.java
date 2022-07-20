package com.prodian.rsgirms.dashboard.response;

import com.prodian.rsgirms.userapp.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardResponse {

	private Integer userDashboardId;

	private Integer userId;

	private Integer dashboardId;

	private String dashboardName;

	private String dashboardURL;
	
	private User user;

}
