package com.prodian.rsgirms.usermatrix.model;

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
@Table(name = "MASTER_BUSINESS_TYPE_NOW")
public class BusinessType {

	@Id
	@Column(name = "BUSINESS_TYPE")
	private String businessType;

}
