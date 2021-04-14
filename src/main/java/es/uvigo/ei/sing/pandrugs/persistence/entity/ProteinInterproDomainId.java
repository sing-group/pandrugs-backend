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

package es.uvigo.ei.sing.pandrugs.persistence.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ProteinInterproDomainId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String uniprotId;
	
	private String domainId;
	
	private int start;
	
	private int end;
	
	ProteinInterproDomainId() {
	}
	
	public String getUniprotId() {
		return uniprotId;
	}

	public String getDomainId() {
		return domainId;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domainId == null) ? 0 : domainId.hashCode());
		result = prime * result + end;
		result = prime * result + start;
		result = prime * result + ((uniprotId == null) ? 0 : uniprotId.hashCode());
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
		ProteinInterproDomainId other = (ProteinInterproDomainId) obj;
		if (domainId == null) {
			if (other.domainId != null)
				return false;
		} else if (!domainId.equals(other.domainId))
			return false;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		if (uniprotId == null) {
			if (other.uniprotId != null)
				return false;
		} else if (!uniprotId.equals(other.uniprotId))
			return false;
		return true;
	}
	
}
