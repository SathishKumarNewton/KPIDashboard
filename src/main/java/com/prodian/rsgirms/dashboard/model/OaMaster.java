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
@Table(name = "RSA_DWH_OA_MASTER")
public class OaMaster {
	
	@Id
	@Column(name = "OA_CODE")
	private String oaCode;
	
	@Column(name = "OA_NAME")
	private String oaName;

}
