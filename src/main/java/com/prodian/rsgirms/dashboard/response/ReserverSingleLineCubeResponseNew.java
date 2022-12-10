package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserverSingleLineCubeResponseNew {
	
	/*
	 * private double catGicOdComprehensiveDep; private double
	 * catGicOdComprehensiveNcb; private double catGicOdComprehensiveOtherAddon;
	 * private double catGicOdComprehensiveNoAddon;
	 * 
	 * private double catGicOdTpDep; private double catGicOdTpNcb; private double
	 * catGicOdTpOtherAddon; private double catGicOdTpNoAddon;
	 * 
	 * private double catGicOdOthersDep; private double catGicOdOthersNcb; private
	 * double catGicOdOthersOtherAddon; private double catGicOdOthersNoAddon;
	 * 
	 * private double theftGicOdComprehensiveDep; private double
	 * theftGicOdComprehensiveNcb; private double theftGicOdComprehensiveOtherAddon;
	 * private double theftGicOdComprehensiveNoAddon;
	 * 
	 * private double theftGicOdTpDep; private double theftGicOdTpNcb; private
	 * double theftGicOdTpOtherAddon; private double theftGicOdTpNoAddon;
	 * 
	 * private double theftGicOdOthersDep; private double theftGicOdOthersNcb;
	 * private double theftGicOdOthersOtherAddon; private double
	 * theftGicOdOthersNoAddon;
	 * 
	 * private double othersGicOdComprehensiveDep; private double
	 * othersGicOdComprehensiveNcb; private double
	 * othersGicOdComprehensiveOtherAddon; private double
	 * othersGicOdComprehensiveNoAddon;
	 * 
	 * private double othersGicOdTpDep; private double othersGicOdTpNcb; private
	 * double othersGicOdTpOtherAddon; private double othersGicOdTpNoAddon;
	 * 
	 * private double othersGicOdOthersDep; private double othersGicOdOthersNcb;
	 * private double othersGicOdOthersOtherAddon; private double
	 * othersGicOdOthersNoAddon;
	 * 
	 * private double catGicTpComprehensiveDep; private double
	 * catGicTpComprehensiveNcb; private double catGicTpComprehensiveOtherAddon;
	 * private double catGicTpComprehensiveNoAddon;
	 * 
	 * private double catGicTpTpDep; private double catGicTpTpNcb; private double
	 * catGicTpTpOtherAddon; private double catGicTpTpNoAddon;
	 * 
	 * private double catGicTpOthersDep; private double catGicTpOthersNcb; private
	 * double catGicTpOthersOtherAddon; private double catGicTpOthersNoAddon;
	 * 
	 * private double theftGicTpComprehensiveDep; private double
	 * theftGicTpComprehensiveNcb; private double theftGicTpComprehensiveOtherAddon;
	 * private double theftGicTpComprehensiveNoAddon;
	 * 
	 * private double theftGicTpTpDep; private double theftGicTpTpNcb; private
	 * double theftGicTpTpOtherAddon; private double theftGicTpTpNoAddon;
	 * 
	 * private double theftGicTpOthersDep; private double theftGicTpOthersNcb;
	 * private double theftGicTpOthersOtherAddon; private double
	 * theftGicTpOthersNoAddon;
	 * 
	 * private double othersGicTpComprehensiveDep; private double
	 * othersGicTpComprehensiveNcb; private double
	 * othersGicTpComprehensiveOtherAddon; private double
	 * othersGicTpComprehensiveNoAddon;
	 * 
	 * private double othersGicTpTpDep; private double othersGicTpTpNcb; private
	 * double othersGicTpTpOtherAddon; private double othersGicTpTpNoAddon;
	 * 
	 * private double othersGicTpOthersDep; private double othersGicTpOthersNcb;
	 * private double othersGicTpOthersOtherAddon; private double
	 * othersGicTpOthersNoAddon;
	 * 
	 * private double nicComprehensiveDep; private double nicComprehensiveNcb;
	 * private double nicComprehensiveOtherAddon; private double
	 * nicComprehensiveNoAddon;
	 * 
	 * private double nicTpDep; private double nicTpNcb; private double
	 * nicTpOtherAddon; private double nicTpNoAddon;
	 * 
	 * private double nicOthersDep; private double nicOthersNcb; private double
	 * nicOthersOtherAddon; private double nicOthersNoAddon;
	 * 
	 * private double nicOdComprehensiveDep; private double nicOdComprehensiveNcb;
	 * private double nicOdComprehensiveOtherAddon; private double
	 * nicOdComprehensiveNoAddon;
	 * 
	 * private double nicOdTpDep; private double nicOdTpNcb; private double
	 * nicOdTpOtherAddon; private double nicOdTpNoAddon;
	 * 
	 * private double nicOdOthersDep; private double nicOdOthersNcb; private double
	 * nicOdOthersOtherAddon; private double nicOdOthersNoAddon;
	 * 
	 * private double nicTpComprehensiveDep; private double nicTpComprehensiveNcb;
	 * private double nicTpComprehensiveOtherAddon; private double
	 * nicTpComprehensiveNoAddon;
	 * 
	 * private double nicTpTpDep; private double nicTpTpNcb; private double
	 * nicTpTpOtherAddon; private double nicTpTpNoAddon;
	 * 
	 * private double nicTpOthersDep; private double nicTpOthersNcb; private double
	 * nicTpOthersOtherAddon; private double nicTpOthersNoAddon;
	 */
	
	private double rslGic;
	private double rslCatGic;
	private double rslTheftGic;
	private double rslOtherGic;
	private double rslTpGic;
	
	
	private double rslNic;
	private double rslCatNic;
	private double rslTheftNic;
	private double rslOtherNic;
	private double rslTpNic;
	
	
}
