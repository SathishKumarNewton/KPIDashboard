package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsCubeResponseNew {
	
	private double claimCount;
	private double addOnClaimCount;
	
	private double catClaimCount;
	private double addOnCatClaimCount;
	
	private double theftClaimCount;
	private double addOnTheftClaimCount;
	
	private double otherClaimCount;
	private double addOnOtherClaimCount;
	
	private double claimCountTp;
	private double addOnClaimCountTp;
	
	
	
}
