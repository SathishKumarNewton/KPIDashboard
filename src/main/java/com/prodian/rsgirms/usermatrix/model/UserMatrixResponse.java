package com.prodian.rsgirms.usermatrix.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMatrixResponse {
	
	private Integer userId;
	private String userName;
	private String userMatrixRole;
	private String zone;
	private String cluster;
	private String state;
	private String branchCode;
	private String branchName;
	private Integer dashboardId;
	private String dashboardName;

}
