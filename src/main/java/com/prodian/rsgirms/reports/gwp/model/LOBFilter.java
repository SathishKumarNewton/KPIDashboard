
package com.prodian.rsgirms.reports.gwp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zakir Hussain Syed
 * @created Aug 11, 2020 03:40:15 PM
 * @version 1.0
 * @filename LOBFilter.java
 * @package com.prodian.rsgirms.reports.gwp.model
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MASTER_PRODUCT_NOW")
public class LOBFilter {
	
	@Id
	@Column(name = "PRODUCT_CODE")
	private String productCode;
	
	@Column(name = "CLASS_OF_BUSINESS")
	private String classOfBusiness;
	
	@Column(name = "PRODUCT")
	private String product;
	
	@Column(name = "PRODUCT_DESCRIPTION")
	private String productDesc;
	
	@Column(name = "RET_NONRET")
	private String retNonRet;
	
	@Column(name = "SEGMENT_NEW")
	private String lob;

}
