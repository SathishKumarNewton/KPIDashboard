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
	private double gepNildep;
	private double gepNcb;
	private double gepOtherAddon;
	private double gepOdEarnedPolicies;
	private double gepDepEarnedPolicies;
	private double gepNcbEarnedPolicies;
	private double gepOtherAddonEarnedPolicies;
	private double nepNildep;
	private double nepNcb;
	private double nepOtherAddon;
	
	private double gepFreqCatOdr12;
	private double gepFreqTheftOdr12;
	private double gepFreqOthersOdr12;
	
	private double gepFreqCatDepr12;
	private double gepFreqTheftDepr12;
	private double gepFreqOthersDepr12;
	
	private double gepFreqCatNcbr12;
	private double gepFreqTheftNcbr12;
	private double gepFreqOthersNcbr12;
	
	private double gepFreqCatOtherAddonr12;
	private double gepFreqTheftOtherAddonr12;
	private double gepFreqOthersOtherAddonr12;
	
	private double gepSevCatr12;
	private double gepSevTheftr12;
	private double gepSevOthersr12;
	
	private double gepGicCatOdr12;
	private double gepGicTheftOdr12;
	private double gepGicOthersOdr12;
	
	private double gepGicCatDepr12;
	private double gepGicTheftDepr12;
	private double gepGicOthersDepr12;
	
	private double gepGicTheftNcbr12;
	private double gepGicOthersNcbr12;
	private double gepGicCatNcbr12;
	
	private double gepGicCatOtherAddonr12;	
	private double gepGictheftOtherAddonr12;
	private double gepGicOthersOtherAddonr12;
	
	private double gicTp;
	
}
