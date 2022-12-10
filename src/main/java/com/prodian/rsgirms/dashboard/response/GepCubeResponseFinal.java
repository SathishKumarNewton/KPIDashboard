package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GepCubeResponseFinal {
	
	private double gep;
	private double Nep;
	private double gepOd;
	private double gepTp;
	private double nepOd;
	private double nepTp;
	
	private double discountGepOd;
	private double discountGepTp;
	
	private double gepDep;
	private double gepDepOd;
	private double gepDepTp;
	
	private double gepNcb;
	private double gepNcbOd;
	private double gepNcbTp;
	
	private double gepOtherAddon;
	private double gepOtherAddonOd;
	private double gepOtherAddonTp;
	
	private double nepDep;
	private double nepDepOd;
	private double nepDepTp;
	
	private double nepNcb;
	private double nepNcbOd;
	private double nepNcbTp;
	
	private double nepOtherAddon;
	private double nepOtherAddonOd;
	private double nepOtherAddonTp;
	
	private double earnedPolicies;
	private double earnedPoliciesOd;
	private double earnedPoliciesTp;
	
	private double addonEarnedPolicies;
	private double addonEarnedPoliciesOd;
	private double addonEarnedPoliciesTp;
	
	private double gicTpulr;
	private double gicTpulrDep;
	private double gicTpulrNcb;
	private double gicTpulrOtherAddon;
	
	private double nicTpulr;
	private double nicTpulrDep;
	private double nicTpulrNcb;
	private double nicTpulrOtherAddon;
}
