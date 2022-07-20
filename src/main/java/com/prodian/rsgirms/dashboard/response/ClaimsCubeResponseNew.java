package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsCubeResponseNew {
	
	private double catClaimCountPoliciesComprehensive;
	private double catClaimCountPoliciesTp;
	private double catClaimCountPoliciesOthers;
	
	private double theftClaimCountPoliciesComprehensive;
	private double theftClaimCountPoliciesTp;
	private double theftClaimCountPoliciesOthers;
	
	private double othersClaimCountPoliciesComprehensive;
	private double othersClaimCountPoliciesTp;
	private double othersClaimCountPoliciesOthers;
	
	
	
}
