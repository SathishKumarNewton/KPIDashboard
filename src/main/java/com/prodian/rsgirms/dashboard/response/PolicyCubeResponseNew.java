package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyCubeResponseNew {
	
	private double writtenPoliciesComprehensive;
	private double writtenPoliciesTp;
	private double writtenPoliciesOthers;
	private double writtenPolicies;
	private double acqCostComprehensive;
	private double acqCostTp;
	private double acqCostOthers;
	private double acqCost;
	private double livesCovered;
	
	
}
