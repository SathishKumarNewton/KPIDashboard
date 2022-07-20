package com.prodian.rsgirms.dashboard.model;

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
@Table(name = "sqoop_configuration")
public class SqoopConfiguation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "source_schema_url")
	private String sourceSchemaUrl;
	
	@Column(name = "source_schema_user_name")
	private String sourceSchemaUserName;
	
	@Column(name = "source_schema_password")
	private String sourceSchemaPassword;
	
	@Column(name = "destination_schema_name")
	private String destinationSchemaName;
	
	@Column(name = "source_table_name")
	private String sourceTableName;
	
	@Column(name = "destination_table_name")
	private String destinationTableName;
	
	@Column(name = "is_active")
	private Boolean isActive;
	
	@Column(name = "type")
	private String type;

}
