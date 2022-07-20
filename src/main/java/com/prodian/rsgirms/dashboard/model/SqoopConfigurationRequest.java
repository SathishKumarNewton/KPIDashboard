package com.prodian.rsgirms.dashboard.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SqoopConfigurationRequest {
	
	private String sourceSchemaUrl;
	private String sourceSchemaUserName;
	private String sourceSchemaPassword;
	private String destinationSchemaName;
	private Boolean isActive;
	private List<ConfigTableName> tableName;

}
