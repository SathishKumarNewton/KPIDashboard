
package com.prodian.rsgirms.dashboard.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CSS
 *
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dashboard")
public class Dashboard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dashboard_id")
	private Integer id;

	@Column(name = "dashboard_name")
	@NotEmpty(message = "*Please enter a dashboard name")
	private String dashboardName;

	@Column(name = "dashboard_url")
	@NotEmpty(message = "Please enter a dashboard url")
	private String dashboardURL;

}
