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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "protein_pfam")
@IdClass(ProteinPfamId.class)
public class ProteinPfam {
	@Id
	@Column(name = "uniprot_id")
	private String uniprotId;
	
	@Id
	@Column(name = "start")
	private int start;
	
	@Id
	@Column(name = "end")
	private int end;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "uniprot_id",
		referencedColumnName = "uniprot_id",
		insertable = false, updatable = false,
		nullable = true
	)
	private Protein protein;

	ProteinPfam() {}

	public String getUniprotId() {
		return uniprotId;
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
		ProteinPfam other = (ProteinPfam) obj;
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
