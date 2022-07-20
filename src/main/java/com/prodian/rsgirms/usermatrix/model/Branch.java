package com.prodian.rsgirms.usermatrix.model;

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
@Table(name = "MASTER_BRANCH_NOW")
public class Branch {

	@Id
	@Column(name = "branch_code")
	private String branchCode;

	@Column(name = "revised_branch_name")
	private String revisedBranchName;

	@Column(name = "region")
	private String region;

	@Column(name = "state_new")
	private String stateNew;

	@Column(name = "cluster_name")
	private String clusterName;

	@Column(name = "sub_cluster")
	private String subCluster;

	@Column(name = "ra_city_flag")
	private String raCityFlag;

	@Column(name = "ra_description")
	private String raDescription;

	@Column(name = "zone")
	private String zone;

}
