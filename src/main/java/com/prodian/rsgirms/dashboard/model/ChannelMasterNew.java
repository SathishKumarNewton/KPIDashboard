package com.prodian.rsgirms.dashboard.model;
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
@Table(name = "RSA_DWH_CHANNEL_NEW_MASTER")
public class ChannelMasterNew {
	
	@Id
	@Column(name = "CHANNEL_NEW_NAME")
	private String channelNewName;

}
