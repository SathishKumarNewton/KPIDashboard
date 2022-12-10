package com.prodian.rsgirms.dashboard.model;
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
@Table(name = "RSA_DWH_ENGINE_CAPACITY_MASTER")
public class EngineCapacityMaster {
    
    @Id
	@Column(name = "ENGINE_CAPACITY")
	private String engineCapacity;
}
