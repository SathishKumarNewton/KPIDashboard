package com.prodian.rsgirms.dashboard.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyDashboardFilterRequest {
	
	private List<String> channel;
	private List<String> subChannel;
	private List<String> geo;
	private List<String> make;
	private List<String> modelGroup;
	private List<String> fuelType;
	private List<String> ncbFlag;

}
