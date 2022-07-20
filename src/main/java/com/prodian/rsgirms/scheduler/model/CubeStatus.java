package com.prodian.rsgirms.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mohamed ismaiel.S
 * @created July 04, 2020 05:12:23 PM
 * @version 1.0
 * @filename Cubes.java
 * @package com.prodian.rsgirms.scheduler.model 
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cube_status")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class CubeStatus {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduler_id")
    private Integer schedulerId;
	
    @Column(name = "process_date")
    private String processDate;
    
    @Column(name = "cube_name")
    private String cubeName;
    
    @Column(name = "ref_key")
    private String refKey;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "is_completed")
    private String isCompleted;
    
    @Column(name = "is_build_started")
    private String isBuildStarted;
    
    @Column(name = "progress")
    private String progress;
    
    @Column(name = "response")
    private String response;
    
    @Column(name = "start_time")
    private String startTime;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "duration")
    private String duration;

}
