package com.prodian.rsgirms.dashboard.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GwpResponse {

	private List<String> lob;

	private List<String> zone;

	private List<String> cluster;

	private List<String> state;

	private List<String> branchcode;

	private List<String> channel;

	private List<String> subchannel;

	private List<String> product;

	private List<String> productDesc;

	private List<String> make;

	private List<String> model;

	private List<Double> mtdGWP;

	private List<Double> ytdGWP;

	private List<Double> mtdPolicyCount;

	private List<Double> ytdPolicyCount;

	private List<Double> preMtdGWP;

	private List<Double> preYtdGWP;

	private List<Double> preMtdPolicyCount;

	private List<Double> preYtdPolicyCount;

}
