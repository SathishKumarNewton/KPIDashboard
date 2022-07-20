package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CubeBKpiResponse {
	
	/*private String policyNo;
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
	private String productDesc;*/
	
	private double gwpOd;
	private double gwpTp;
	private double nwpOd;
	private double nwpTp;
	private double discountGwpOd;
	private double discountNwpOd;
	
	
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
