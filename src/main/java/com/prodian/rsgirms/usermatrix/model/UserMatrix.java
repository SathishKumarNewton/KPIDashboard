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
@Table(name = "user_matrix")
public class UserMatrix {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "user_role")
	private String userMatrixRole;
	
	@Column(name = "zone")
	private String zone;
	
	@Column(name = "cluster")
	private String cluster;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "branch")
	private String branch;

}
