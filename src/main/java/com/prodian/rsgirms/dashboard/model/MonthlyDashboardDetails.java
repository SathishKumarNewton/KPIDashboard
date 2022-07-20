package com.prodian.rsgirms.dashboard.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "MONTHLY_DASHBOARD_GROUPED_NEW")
public class MonthlyDashboardDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "business_type")
	private String businessType;
	
	@Column(name = "channel")
	private String channel;
	
	@Column(name = "sub_channel")
	private String subChannel;
	
	@Column(name = "make")
	private String make;
	
	@Column(name = "model_group")
	private String modelGroup;
	
//	@Column(name = "geo")
//	private String geo;
	
	@Column(name = "state_grouping")
	private String geo;
	
	@Column(name = "claim_type")
	private String claim_type;
	
	@Column(name = "ncb_flag")
	private String ncbFlag;
	
	@Column(name = "fuel_type")
	private String fuelType;

}
