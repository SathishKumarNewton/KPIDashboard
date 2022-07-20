
package com.prodian.rsgirms.reports.gwp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zakir Hussain Syed
 * @created Aug 11, 2020 03:40:15 PM
 * @version 1.0
 * @filename ModelFilter.java
 * @package com.prodian.rsgirms.reports.gwp.model
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MASTER_MODEL_NOW")
@IdClass(ModelId.class)
public class ModelFilter {
	
	@Id
	@Column(name = "MAKE")
	private String make;
	
	@Id
	@Column(name = "MODEL_CODE")
	private String modelCode;
	
	@Column(name = "MODEL_NAME")
	private String modelName;
	
	@Column(name = "MODELFINAL")
	private String modelFinal;
	
	@Column(name = "SC_BAND")
	private String scBand;
	
	@Column(name = "BUSINESS_STATUS")
	private String businessStatus;
	
	@Column(name = "EFFECTIVE_END_DATE")
	private String effectiveEndDate;

}
