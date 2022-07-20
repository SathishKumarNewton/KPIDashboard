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
@Table(name = "MASTER_CLUSTER_NOW")
public class Cluster {

//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	@Column(name = "cluster_id")
//	private Integer id;

	@Id
	@Column(name = "cluster_name")
	private String clusterName;

	@Column(name = "zone_name")
	private String zoneName;

}
