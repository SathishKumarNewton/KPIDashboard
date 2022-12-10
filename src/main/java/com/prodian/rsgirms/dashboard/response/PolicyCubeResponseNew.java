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
	private double addonWrittenPoliciesComprehensive;
	private double addonWrittenPoliciesTp;
	private double addonWrittenPoliciesOthers;
	private double addonWrittenPolicies;
	private double acqCostComprehensive;
	private double acqCostTp;
	private double acqCostOthers;
	private double acqCost;
	private double addonAcqCostComprehensive;
	private double addonAcqCostTp;
	private double addonAcqCostOthers;
	private double addonAcqCost;
	private double livesCovered;
	
	
}
