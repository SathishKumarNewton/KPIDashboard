
package com.prodian.rsgirms.reports.gwp.model;

import java.io.Serializable;

import lombok.NoArgsConstructor;

/**
 * @author Zakir Hussain Syed
 * @created Aug 11, 2020 03:40:15 PM
 * @version 1.0
 * @filename SubChannelId.java
 * @package com.prodian.rsgirms.reports.gwp.model
 */

@NoArgsConstructor
public class SubChannelId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String subChannel;
	private String channelName;
	
	public SubChannelId(String subChannel, String channelName) {
        this.subChannel = subChannel;
        this.channelName = channelName;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channelName == null) ? 0 : channelName.hashCode());
		result = prime * result + ((subChannel == null) ? 0 : subChannel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubChannelId other = (SubChannelId) obj;
		if (channelName == null) {
			if (other.channelName != null)
				return false;
		} else if (!channelName.equals(other.channelName))
			return false;
		if (subChannel == null) {
			if (other.subChannel != null)
				return false;
		} else if (!subChannel.equals(other.subChannel))
			return false;
		return true;
	}
	
}
