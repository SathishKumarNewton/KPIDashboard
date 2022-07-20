
package com.prodian.rsgirms.dashboard.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
@Table(name = "user_dashboard")
@IdClass(UserDashboard.class)
public class UserDashboard implements Serializable {

	private static final long serialVersionUID = 1L;

	/*@Id
	@SequenceGenerator(name ="user_dashboard_user_dashboard_id_seq",sequenceName = "user_dashboard_user_dashboard_id_seq", allocationSize = 1,initialValue = 10)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_dashboard_user_dashboard_id_seq")
	@Column(name = "user_dashboard_id")
	private int userDashboardId;*/
	@Id
	@SequenceGenerator(name ="user_dashboard_user_id_seq",sequenceName = "user_dashboard_user_id_seq", allocationSize = 1,initialValue = 10)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_dashboard_user_id_seq")
	@Column(name = "user_dashboard_id")
	private int userDashboardId;

//	@Id
	@Column(name = "user_id")
	private Integer userId;

//	@Id
	@Column(name = "dashboard_id")
	private Integer dashboardId;

	@Column(name = "dashboard_name")
	private String dashboardName;

	@Column(name = "dashboard_url")
	private String dashboardURL;

}
