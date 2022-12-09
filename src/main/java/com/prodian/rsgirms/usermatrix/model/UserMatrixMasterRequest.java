package com.prodian.rsgirms.usermatrix.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMatrixMasterRequest {

	private List<Integer> userIds;
	private List<String> userNames;
	private List<String> userMatrixRoleName;
	private List<String> zones;
	private List<String> clusters;
	private List<String> states;
	private List<String> branchCodes;
	private List<String> products;
	private List<String> lobs;
	private List<String> businessTypes;
	private List<String> channels;
	private List<String> subChannels;
	private List<String> makes;
	private List<String> models;
	private List<String> region;

	private List<Integer> dashboardId;

	private List<String> cities;

	private List<String> t20Locations;

	private List<String> pincodes;

	private List<String> modelGroups;

	private List<String> modelClasifications;

	private List<String> vehicleAges;

	private List<String> ageWithRs;

	private List<String> addOns;

	private List<String> intermediaryCodes;

	private List<String> intermediaryNames;

	private List<String> oaCodes;

	private List<String> oaNames;

	private List<String> policyTypes;

	private List<String> policyCategories;

	private String sumInsured;

	private String maxAges;

	private String fromDate;
	private String toDate;
	private String uwMonth;
	private String reportType;
	private String addOnNew;
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
	private String motorWithCat;
	private String motorWithTheft;
	private String motorWithQst;
	private List<String> motorFuelType;
	private List<String> motorNcbFlag;

	private List<String> healthChannel;
	private List<String> healthSubChannel;
	private List<String> healthIntermediaryName;
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

	private List<String> channelNow;
	private List<String> subChannelNow;
	private List<String> bTypeNow;
	private List<String> makeNow;
	private List<String> modelGroupNow;
	private List<String> fuelTypeNow;
	private List<String> stateGroupNow;
	private List<String> ncbNow;

	private List<String> motorCarType;
	
//	private List<String> channelNew;
//	private List<String> policyTypeNew;
//	private List<String> categorisation;
//	private List<String> engineCapacity;
//	private List<String> vehicleAge;
}
