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
public class UserMatrixRequest {

	private List<Integer> userIds;
	private List<Integer> userMatrixRoleIds;
	private List<Integer> zoneIds;
	private List<Integer> clusterIds;
	private List<Integer> stateIds;
	private List<String> branchCodes;
	private List<String> products;
	private List<String> businessTypes;
	private List<String> channels;
	private List<String> subChannels;
	private List<String> makes;
	private List<String> models;

}
