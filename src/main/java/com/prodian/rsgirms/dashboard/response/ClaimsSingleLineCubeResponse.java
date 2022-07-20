package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsSingleLineCubeResponse {
	
	private double nic;
	private double actualGicOd;
	private double actualNicOd;
	private double actualGicTp;
	private double actualNicTp;
	private double openingOsClaims;
	private double reputiatedClaims;
	private double paid;
	private double actualNicHealth;
	private double actualGicHealth;
	private double closingOsClaim;	

}
