package com.prodian.rsgirms.scheduler.model;

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
@Table(name = "cubes")
public class Cubes {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
	
    @Column(name = "cube_name",unique = true)
    private String cubeName;
    
    @Column(name = "from_date")
    private String fromDate;
    
    @Column(name = "to_date")
    private String toDate;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "priority")
    private String priority;

}
