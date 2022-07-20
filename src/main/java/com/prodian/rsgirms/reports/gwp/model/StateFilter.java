
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
 * @filename StateFilter.java
 * @package com.prodian.rsgirms.reports.gwp.model
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MASTER_STATE_NOW")
public class StateFilter {
	
	@Id
	@Column(name = "STATE")
	private String state;
	
	@Column(name = "CLUSTER_NAME")
	private String clusterName;

}
