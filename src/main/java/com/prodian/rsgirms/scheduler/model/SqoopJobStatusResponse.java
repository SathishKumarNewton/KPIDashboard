package com.prodian.rsgirms.scheduler.model;

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
@Table(name = "rsa_dwh_sqoop_log")
public class SqoopJobStatusResponse {
	
	@Id
    @Column(name = "id")
    private String id;
	
	@Column(name = "sqoop_date")
	private String sqoopDate;
	
	@Column(name = "sqoop_table")
	private String sqoopTable;
	
	@Column(name = "sqooped_count")
	private String sqoopedCount;
	
	@Column(name = "previous_max_date")
	private String previousMaxDate;
	

}
