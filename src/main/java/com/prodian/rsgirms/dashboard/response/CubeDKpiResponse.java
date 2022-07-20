package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CubeDKpiResponse {
	
	private double repdiatedClaimsPerc;
	private double severityPerc;
	private double nic;
	private double gic;
	private double paid;
	
	/*private double claimRatio;
	private double expenseRatio;*/
	private double actualGicOd;
	private double actualGicTp;
	private double actualNicOd;
	private double actualNicTp;
	/*private double acr;*/
	private double repudiatedClaims;
	/*private double cor;*/
	private double registeredClaims;
	
	private double claimFrequency;
	private double theftClaimFreq;
	private double actualGicHealth;
	private double actualNicHealth;
	
	/*private String ageBand;
	private String branchName;
	private String region;
	private String state;
	private String clusterName;
	private String zone;
	private String city;
	private String make;
	private String model;
	private String modelGroup;
	private String classOfVehicle;
	private String vehicleAge;
	private String familySize;
	private String stpNstp;
	private String disease;
	private String numberOfYearsWithRsInMigrationPolicy;
	private String totalNoOfYearsWithRs;
	private String oaName;
	private String pincode;
	private String intermediaryName;
	private String maxAge;
	private String sumInsured;
	private String version;
	private String policyType;
	private String policyCategory;
	private String oaCode;
	private String businessType;
	private String productCode;
	private String campaignCode;
	private String channel;
	private String subChannel;*/

}
