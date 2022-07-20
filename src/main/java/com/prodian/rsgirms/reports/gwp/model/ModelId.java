
package com.prodian.rsgirms.reports.gwp.model;

import java.io.Serializable;

import lombok.NoArgsConstructor;

/**
 * @author Zakir Hussain Syed
 * @created Aug 11, 2020 03:40:15 PM
 * @version 1.0
 * @filename ModelId.java
 * @package com.prodian.rsgirms.reports.gwp.model
 */

@NoArgsConstructor
public class ModelId implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String make;
	private String modelCode;
	
	public ModelId(String modelCode, String make) {
        this.modelCode = modelCode;
        this.make = make;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((make == null) ? 0 : make.hashCode());
		result = prime * result + ((modelCode == null) ? 0 : modelCode.hashCode());
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
		ModelId other = (ModelId) obj;
		if (make == null) {
			if (other.make != null)
				return false;
		} else if (!make.equals(other.make))
			return false;
		if (modelCode == null) {
			if (other.modelCode != null)
				return false;
		} else if (!modelCode.equals(other.modelCode))
			return false;
		return true;
	}
	
}
