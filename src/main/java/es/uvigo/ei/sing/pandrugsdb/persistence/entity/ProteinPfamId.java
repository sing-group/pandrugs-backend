/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 - 2016 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
public class ProteinPfamId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String uniprotId;
	
	private String accession;
	
	private int start;
	
	private int end;
	
	ProteinPfamId() {}
	
	ProteinPfamId(String uniprotId, int start, int end, String accession) {
		this.uniprotId = uniprotId;
		this.start = start;
		this.end = end;
		this.accession = accession;
	}

	public String getUniprotId() {
		return uniprotId;
	}
	
	public String getAccession() {
		return accession;
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
		result = prime * result + ((accession == null) ? 0 : accession.hashCode());
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
		ProteinPfamId other = (ProteinPfamId) obj;
		if (accession == null) {
			if (other.accession != null)
				return false;
		} else if (!accession.equals(other.accession))
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
