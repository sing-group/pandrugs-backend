/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class DrugSourceId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String source;
	
	private String sourceDrugName;

	DrugSourceId() {}
	
	public DrugSourceId(String source, String sourceDrugName) {
		this.source = source;
		this.sourceDrugName = sourceDrugName;
	}

	public String getSource() {
		return source;
	}

	public String getSourceDrugName() {
		return sourceDrugName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((sourceDrugName == null) ? 0 : sourceDrugName.hashCode());
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
		DrugSourceId other = (DrugSourceId) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (sourceDrugName == null) {
			if (other.sourceDrugName != null)
				return false;
		} else if (!sourceDrugName.equals(other.sourceDrugName))
			return false;
		return true;
	}
}
