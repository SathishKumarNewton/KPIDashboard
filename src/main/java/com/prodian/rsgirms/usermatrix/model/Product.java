package com.prodian.rsgirms.usermatrix.model;

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
@Table(name = "MASTER_PRODUCT_NOW")
public class Product {
	
	@Id
	@Column(name = "PRODUCT_CODE")
	private String productCode;
	
	@Column(name = "PRODUCT")
	private String product;
	
	@Column(name = "PRODUCT_DESCRIPTION")
	private String productDescription;
	
	@Column(name = "RET_NONRET")
	private String retNonRet;
	
	@Column(name = "CLASS_OF_BUSINESS")
	private String classOfBusiness;
	
	@Column(name = "SEGMENT_NEW")
	private String segmentNew;

}
