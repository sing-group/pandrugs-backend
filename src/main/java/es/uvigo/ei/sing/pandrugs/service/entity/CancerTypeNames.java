/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
 * and Miguel Reboiro-Jato
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

import java.util.Arrays;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.CancerType;

@XmlRootElement(name = "cancerTypes", namespace = "https://www.pandrugs.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class CancerTypeNames {
	@XmlElement(name = "cancer", nillable = false)
	@NotNull
	private CancerTypeInfo[] cancerTypes;
	
	public CancerTypeNames() {
		this.cancerTypes = Arrays.stream(CancerType.values())
			.map(CancerTypeInfo::new)
		.toArray(CancerTypeInfo[]::new);
	}

	public CancerTypeInfo[] getCancerTypes() {
		return cancerTypes;
	}

	public void setCancerTypes(CancerTypeInfo[] cancerTypes) {
		this.cancerTypes = cancerTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(cancerTypes);
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
		CancerTypeNames other = (CancerTypeNames) obj;
		if (!Arrays.equals(cancerTypes, other.cancerTypes))
			return false;
		return true;
	}

}
