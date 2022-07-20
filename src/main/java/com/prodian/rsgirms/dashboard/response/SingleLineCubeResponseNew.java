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
	
	private double catGicOdComprehensive;
	private double catGicOdTp;
	private double catGicOdOthers;
	private double theftGicOdComprehensive;
	private double theftGicOdTp;
	private double theftGicOdOthers;
	private double othersGicOdComprehensive;
	private double othersGicOdTp;
	private double othersGicOdOthers;
	private double catGicTpComprehensive;
	private double catGicTpTp;
	private double catGicTpOthers;
	private double theftGicTpComprehensive;
	private double theftGicTpTp;
	private double theftGicTpOthers;
	private double othersGicTpComprehensive;
	private double othersGicTpTp;
	private double othersGicTpOthers;
	
	private double nicComprehensive;
	private double nicTp;
	private double nicOthers;
	private double nicOdComprehensive;
	private double nicOdTp;
	private double nicOdOthers;
	private double nicTpComprehensive;
	private double nicTpTp;
	private double nicTpOthers;
}
