package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CubeCKpiResponse {
	
	private double gep;
	private double nep;
	private double gepOd;
	private double nepOd;
	private double gepTp;
	private double nepTp;
	private double discountGepOd;
	private double discountNepOd;
	private double ibnrGicOd;
	private double nbnrGicOd;
	private double ibnrNicOd;
	private double nbnrNicOd;
	private double ibnrGicHealth;
	private double nbnrGicHealth;
	private double ibnrNicHealth;
	private double nbnrNicHealth;
	
	private double averageNep;
	private double averageGep;
	private double earnedPolicies;
	private double nac;
	private double expenses;
	private double ulrGicTp;
	private double ibnerGicTp;
	
	private double ibnrGicTp;
	private double ibnrNicTp;
	private double nbnrGicTp;
	private double nbnrNicTp;
	
	
	/*private String addOn;
	private String ageBand;
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
