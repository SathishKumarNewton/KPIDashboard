package com.prodian.rsgirms.dashboard.model;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "RSA_DWH_BRANCH_MASTER")
public class BranchMaster {
	
	@Id
	@Column(name = "BRANCH_CODE")
	private String branchCode;
	
	@Column(name = "REVISED_BRANCH_NAME")
	private String revisedBranchName;
	
	@Column(name = "REGION")
	private String region;
	
	@Column(name = "STATE_NEW")
	private String stateNew;
	
	@Column(name = "CLUSTER_NAME")
	private String clusterName; 
	
	@Column(name = "SUB_CLUSTER")
	private String subCluster;
	
	@Column(name = "RA_CITY_FLAG")
	private String raCityFlag;
	
	@Column(name = "RA_DESCRIPTION")
	private String raDescription;
	
	@Column(name = "ZONE")
	private String zone;

}
