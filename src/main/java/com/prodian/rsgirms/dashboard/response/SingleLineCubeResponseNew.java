package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleLineCubeResponseNew {
	
	private double cslGic;
	private double cslCatGic;
	private double cslTheftGic;
	private double cslOtherGic;
	private double cslTpGic;
	private double cslNic;
	private double cslCatNic;
	private double cslTheftNic;
	private double cslOtherNic;
	private double cslTpNic;
}
