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
@Table(name = "MASTER_MODEL_NOW")
public class Model {

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
