/*
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2017 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package es.uvigo.ei.sing.pandrugs.service.entity;

import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.CancerType;

@XmlRootElement(name = "cancerType", namespace = "http://sing.ei.uvigo.es/pandrugs")
public class CancerTypeInfo {
	private String name;
	private boolean canBeQueried;
	
	CancerTypeInfo() {}
	
	public CancerTypeInfo(CancerType type) {
		this.name = type.name();
		this.canBeQueried = type.canBeQueried();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCanBeQueried() {
		return canBeQueried;
	}

	public void setCanBeQueried(boolean canBeQueried) {
		this.canBeQueried = canBeQueried;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (canBeQueried ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CancerTypeInfo other = (CancerTypeInfo) obj;
		if (canBeQueried != other.canBeQueried)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
