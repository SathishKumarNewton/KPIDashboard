package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GwpkpiResponse {

	private String oemType;
	
	private String highEnd;

	private String fuelType;
	
	private String ncb;

	//private String stateGrouping;
	private String state;
	private String city;
	
	private double cmGwp;
	
	private double pmGwp;

}
