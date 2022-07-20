package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CubeDKpiResponseMock {
	
	private double repdiatedClaimsPerc;
	private double severityPerc;
	private double nic;
	private double claimRatio;
	private double expenseRatio;
	private double actualGicOd;
	private double actualGicTp;
	private double actualNicOd;
	private double actualNicTp;
	private double acr;
	private double repudiatedClaims;
	private double cor;
	private double registeredClaims;

}
