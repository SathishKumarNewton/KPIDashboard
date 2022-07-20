package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralKpiResponse {
	
	private String policyNo;
	private String entryDate;
	private String entryMonth;
	private String finYear;
	private String underWritingYear;
	private String clusterName;
	private String channelName;
	private String subChannelName;
	private String region;
	private String state;
	private String businessType;
	private String productCode;
	private String productDesc;
	
	private double gwp;
	private double nwp;
	private double gep;
	private double nep;
	private double avgGwp;
	private double avgNep;
	private double avgGep;
	private double gepTp;
	private double nepTp;
	private double severityPerc;
	private double repudiatedClaimPerc;
	private double registeredClaim;
	private double redpudiatedClaim;
	private double nic;
	private double nicNepRatio;
	private double claimFrequency;
	private double wp;
	private double netAcquisitionCost;
	private double expenseRatio;
	private double acquisitionCostRatio;
	private double claimsRatio;
	private double cor;
	private double xolCost;
	private double expense;
	private double earnedPolicies;
	private double gic;

}
