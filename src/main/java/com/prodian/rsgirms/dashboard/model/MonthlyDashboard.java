package com.prodian.rsgirms.dashboard.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MONTHLY_DASHBOARD")
public class MonthlyDashboard {
	
	@Id
	private Integer id;
	
	private Double totalCurrentMonthGwp;
	private Double totalPreMonthGwp;
	private Double totalCurrentYTMYearGwp;
	private Double totalCurrentYTMPreYearGwp;
	
	private Double newVehCurrentMonthGwp;
	private Double newVehPreMonthGwp;
	private Double newVehCurrentYTMYearGwp;
	private Double newVehCurrentYTMPreYearGwp;
	
	private Double renewalCurrentMonthGwp;
	private Double renewalPreMonthGwp;
	private Double renewalCurrentYTMYearGwp;
	private Double renewalCurrentYTMPreYearGwp;
	
	private Double rollCurrentMonthGwp;
	private Double rollPreMonthGwp;
	private Double rollCurrentYTMYearGwp;
	private Double rollCurrentYTMPreYearGwp;
	
	private Double totalCurrentMonthPolicyCount;
	private Double totalPreMonthPolicyCount;
	private Double totalCurrentYTMYearPolicyCount;
	private Double totalCurrentYTMPreYearPolicyCount;
	
	private Double newVehCurrentMonthPolicyCount;
	private Double newVehPreMonthPolicyCount;
	private Double newVehCurrentYTMYearPolicyCount;
	private Double newVehCurrentYTMPreYearPolicyCount;
	
	private Double renewalCurrentMonthPolicyCount;
	private Double renewalPreMonthPolicyCount;
	private Double renewalCurrentYTMYearPolicyCount;
	private Double renewalCurrentYTMPreYearPolicyCount;
	
	private Double rollCurrentMonthPolicyCount;
	private Double rollPreMonthPolicyCount;
	private Double rollCurrentYTMYearPolicyCount;
	private Double rollCurrentYTMPreYearPolicyCount;
	
	private Double policyUWCurrentMonthGwp;
	private Double policyUWPreMonthGwp;
	private Double policyUWCurrentYTMYearGwp;
	private Double policyUWCurrentYTMPreYearGwp;

}
