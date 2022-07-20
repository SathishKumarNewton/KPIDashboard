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
@Table(name = "MODEL_GROUP_MASTER_NOW")
public class ModelGroupMasterNow {
	
	@Id
	@Column(name = "MODEL_GROUP")
	private String modelGroup;

}
