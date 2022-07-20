package com.prodian.rsgirms.usermatrix.model;

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
@Table(name = "MASTER_SUBCHANNEL_NOW")
public class SubChannel {
	
	@Column(name = "CHANNEL_NAME")
	private String channelName;
	
	@Column(name = "S_NO")
	private String sno;	
	
	@Id
	@Column(name = "SUB_CHANNEL")
	private String subChannel;
	
	@Column(name = "SUB_CHANNEL_NEW")
	private String subChannelNew;

}
