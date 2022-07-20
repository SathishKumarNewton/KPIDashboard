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
import javax.persistence.Transient;

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
@Table(name = "scheduler_info")
public class SchedulerInfo {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
	
	@Column(name = "process_name")
	private String processName;

	@Column(name = "scheduler_status")
	private String schedulerStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "scheduler_start_date")
	private Date schedulerStartDate;

	@Column(name = "scheduler_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date schedulerEndDate;

	@Transient
	private String startDate;

	@Transient
	private String endDate;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "error_msg")
    private String errorMsg;
    
    @Column(name = "process_count")
    private String processCount;
    

}
