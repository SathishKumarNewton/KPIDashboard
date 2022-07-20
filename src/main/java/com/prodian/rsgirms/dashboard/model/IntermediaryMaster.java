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
@Table(name = "RSA_DWH_INTERMEDIARY_MASTER")
public class IntermediaryMaster {
	
	@Id
	@Column(name = "INTERMEDIARY_CODE")
	private String intermediaryCode;
	
	@Column(name = "INTERMEDIARY_NAME")
	private String intermediaryName;

}
