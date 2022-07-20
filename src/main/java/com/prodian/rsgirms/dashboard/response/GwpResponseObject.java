package com.prodian.rsgirms.dashboard.response;

import java.util.List;

import com.prodian.rsgirms.userapp.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GwpResponseObject {

	private String lob;
	
	private String zone;
	
	private String cluster;

	private String state;
	
	private String branchcode;
	
	private String channel;
	
	private String subchannel;
	
	private String product;
	
	private String productDesc;
	
	private String make;
	
	private String model;
	
	private double mtdGWP;
	
	private double ytdGWP;

}
