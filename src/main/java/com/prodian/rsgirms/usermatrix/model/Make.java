package com.prodian.rsgirms.usermatrix.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MASTER_MAKE_NOW", uniqueConstraints = @UniqueConstraint(columnNames = {"MAKE_NAME"}))
public class Make {
	
	@Column(name = "MAKEID")
	private Float makeId;
	
	@Id
	@Column(name = "MAKE_NAME", unique = true)
	private String makeName;

}
