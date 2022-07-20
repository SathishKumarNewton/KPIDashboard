package com.prodian.rsgirms.dashboard.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyDashboardDetailsResponse {
	
	private String businessType;
	private String claimType;
	private String detailType;
	
	private Double gwpOd;
	private Double gwpTp;
	private Double totalGwp;
	
	private Double policyOd;
	private Double policyTp;
	private Double totalPolicy;
	
	private Double freqOd;
	private Double freqTp;
	private Double freq;
	
	private Double sevOd;
	private Double sevTp;
	private Double sev;
	
	private Double totalGep;
	private Double gepOd;
	private Double gepTp;
	
	private Double totalGic;
	private Double gicOd;
	private Double gicTp;

}
