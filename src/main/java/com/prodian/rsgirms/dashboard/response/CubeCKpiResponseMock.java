package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CubeCKpiResponseMock {
	
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
	

}
