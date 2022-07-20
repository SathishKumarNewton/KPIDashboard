package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CubeAKpiResponseMock {
	
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
	
	private double gwp;
	private double nwp;
	private double livesCovered;

}
