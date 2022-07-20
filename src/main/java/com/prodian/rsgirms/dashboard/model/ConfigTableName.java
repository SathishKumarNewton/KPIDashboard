package com.prodian.rsgirms.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigTableName {
	
	private String sourceTableName;
	private String destinationTableName;

}
