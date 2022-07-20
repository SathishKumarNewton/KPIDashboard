package com.prodian.rsgirms.dashboard.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@Table(name = "RSA_DWH_SUB_CHANNEL_MASTER")
public class SubChannelMaster {
	
	@Column(name = "CHANNEL_NAME")
	private String channelName;
	
	
	@Column(name = "SUB_CHANNEL")
	private String subChannel;
	
	@Id
	@Column(name = "id")
	private int id;

}
