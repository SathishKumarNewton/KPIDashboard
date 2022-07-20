package com.prodian.rsgirms.usermatrix.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_matrix_master")
public class UserMatrixMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "user_role")
	private String userMatrixRole;
	
	@Column(name = "zone")
	private String zone;
	
	@Column(name = "cluster")
	private String cluster;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "branch")
	private String branch;
	
	@Column(name = "product")
	private String product;
	
	@Column(name = "lob")
	private String lob;
	
	@Column(name = "business_type")
	private String businessType;
	
	@Column(name = "channel")
	private String channel;
	
	@Column(name = "sub_channel")
	private String subChannel;
	
	@Column(name = "make")
	private String make;
	
	@Column(name = "model")
	private String model;
	
	@Column(name = "dashboard_id")
	private Integer dashboardId;
	
	@Column(name = "region")
	private String region;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "t20_location")
	private String t20Location;
	
	@Column(name = "pincode")
	private String pincode;
	
	@Column(name = "model_group")
	private String modelGroup;
	
	@Column(name = "model_classification")
	private String modelClasification;
	
//	@Column(name = "dashboard_id")
//	private String vehicleAge;
	
//	@Column(name = "dashboard_id")
//	private String ageWithRs;
	
	@Column(name = "add_ons")
	private String addOns;
	
	@Column(name = "intermediary_code")
	private String intermediaryCode;
	
	@Column(name = "intermediary_name")
	private String intermediaryName;
	
	@Column(name = "oa_code")
	private String oaCode;
	
	@Column(name = "oa_name")
	private String oaName;
	
	@Column(name = "policy_type")
	private String policyType;
	
	@Column(name = "policy_category")
	private String policyCategory;
	
//	private String sumInsured;
	
//	private String maxAge;

}
