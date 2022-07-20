package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GepCubeResponse {
	
	private double gep;
	private double nep;
	private double gepOd;
	private double nepOd;
	private double gepTp;
	private double nepTp;
	private double discountGepOd;
	private double discountNepOd;
	private double ibnrGicOd;
	private double nbnrGicOd;
	private double ibnrNicOd;
	private double nbnrNicOd;
	private double ibnrGicHealth;
	private double nbnrGicHealth;
	private double ibnrNicHealth;
	private double nbnrNicHealth;
	
	private double averageNep;
	private double averageGep;
	private double earnedPolicies;
	private double nac;
	private double expenses;
	private double ulrGicTp;
	private double ulrNicTp;
	private double ibnerGicTp;
	private double ibnerNicTp;
	
	private double ibnrGicTp;
	private double ibnrNicTp;
	private double nbnrGicTp;
	private double nbnrNicTp;
	
	private double burnCost;
	private double xolCost;
	private double earnedDays;
	

}
