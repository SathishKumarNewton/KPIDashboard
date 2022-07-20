package com.prodian.rsgirms.dashboard.model;

import javax.persistence.Column;
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
@Table(name = "BUSINESS_TYPE_MASTER_NOW")
public class BusinessTypeMasterNow {
	
	@Id
	@Column(name = "BUSINESS_TYPE")
	private String businessType;

}
