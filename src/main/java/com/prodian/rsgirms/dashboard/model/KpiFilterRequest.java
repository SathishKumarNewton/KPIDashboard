package com.prodian.rsgirms.dashboard.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KpiFilterRequest {
	
	
	private String fromDate;
	private String toDate;
	private String reportType;
	private List<String> generalChannel;
	private List<String> generalSubChannel;
	private List<String> generalntermediary;
	private List<String> generalRegion;
	private List<String> generalState;
	private List<String> generalCity;
	private List<String> generalBranch;
	private List<String> generalAddOn;
	private String generalNoOfYearsWithRs;
	private String generalBusinessType;
	private List<String> generalProduct;
	private List<String> generalCampaign;
	
	private List<String> motorChannel;
	private List<String> motorSubChannel;
	private List<String> motorRegion;
	private List<String> motorCluster;
	private List<String> motorState;
	private List<String> motorCity;
	private List<String> motorZone;
	private List<String> motorPincode;
	private List<String> motorBranch;
	private List<String> motorT20Location;
	private List<String> motorIntermediaryCode;
	private List<String> motorIntermediaryName;
	private List<String> motorOaCode;
	private List<String> motorOaName;
	private List<String> motorMake;
	private List<String> motorModel;
	private List<String> motorModelGroup;
	private List<String> motorModelClassification;
	private String motorVehicleAge;
	private List<String> motorAddOn;
	private List<String> motorProduct;
	private List<String> motorCampaign;
	private String motorBusinessType;
	private String motorNoOfYearsWithRs;
	
	private List<String> healthChannel;
	private List<String> healthSubChannel;
	private List<String> healthInermediary;
	private List<String> healthRegion;
	private List<String> healthState;
	private List<String> healthCity;
	private List<String> healthBranch;
	private List<String> healthPolicyType;
	private List<String> healthPolicyCategory;
	private List<String> healthAddOn;
	private List<String> healthProduct;
	private List<String> healthCamapaign;
	private List<String> healthSubline;
	private String healthBusinessType;
	private String healthSTPNSTP;
	private String healthSumInsured;
	private String healthMaxAge;
	private String healthNoOfYearsWithRs;
	private String healthNoOfMigratedYears;
	private String healthAgeBand;
	private List<String> healthPreExistingDisease;
	private String healthFamilySize;
	
	
	
}
