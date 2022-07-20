package com.prodian.rsgirms.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthFiltersResponse {
	
	private String currentMonth;
	private String previousMonth;
	private String ytmStartMonth;
	private String ytmEndMonth;
	private String pytmStartMonth;
	private String pytmEndMonth;
	private String inceptionMonthThreshold; // t-14 month from cm
	private String consideringPolicyUWStartMonth;
	private String consideringPolicyUWEndMonth;
	private String excludingBusinessUWStartMonth;
	private String excludingBusinessUWEndMonth;

}
