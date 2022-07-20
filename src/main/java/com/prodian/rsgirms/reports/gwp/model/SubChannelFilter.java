
package com.prodian.rsgirms.reports.gwp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zakir Hussain Syed
 * @created Aug 11, 2020 03:40:15 PM
 * @version 1.0
 * @filename SubChannelFilter.java
 * @package com.prodian.rsgirms.reports.gwp.model
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MASTER_SUBCHANNEL_NOW")
@IdClass(SubChannelId.class)
public class SubChannelFilter {
	
	@Id
	@Column(name = "SUB_CHANNEL")
	private String subChannel;
	
	@Id
	@Column(name = "CHANNEL_NAME")
	private String channelName;
	
	@Column(name = "S_NO")
	private String sNo;
	
	@Column(name = "SUB_CHANNEL_NEW")
	private String subChannelNew;

}
