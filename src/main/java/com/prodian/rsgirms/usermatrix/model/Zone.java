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
@Table(name = "MASTER_ZONE_NOW")
public class Zone {

//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	@Column(name = "zone_id")
//	private Integer id;

	@Id
	@Column(name = "ZONE_NAME")
	private String zoneName;

}
