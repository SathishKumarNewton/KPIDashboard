package com.prodian.rsgirms.scheduler.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sqoop_job_response")
public class SqoopJobResponse {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
	
	@Column(name = "sqoop_config_id")
	private Integer sqoopConfigId;
	
	@Column(name = "exit_code")
	private Integer exitCode;
	
	@Column(name = "response")
	private String response;
	
	@Column(name = "error_message")
	private String errorMessage;
	
	@Column(name = "type")
	private String type;
	
	@Column(name="process_date")
	@Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;
	
	

}
