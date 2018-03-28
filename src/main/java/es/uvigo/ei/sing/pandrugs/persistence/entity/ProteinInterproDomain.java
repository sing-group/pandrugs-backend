/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "protein_interpro_domain")
@IdClass(ProteinInterproDomainId.class)
public class ProteinInterproDomain implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(
		name = "uniprot_id",
		insertable = false, updatable = false
	)
	private String uniprotId;
	
	@Id
	@Column(
		name = "domain_id",
		length = 9, columnDefinition = "CHAR(9)",
		insertable = false, updatable = false
	)
	private String domainId;

	@Id
	@Column(name = "start")
	private int start;
	
	@Id
	@Column(name = "end")
	private int end;
	
	@ManyToOne
	@JoinColumn(
		name = "uniprot_id",
		referencedColumnName = "uniprot_id"
	)
	private Protein protein;

	@ManyToOne
	@JoinColumn(
		name = "domain_id",
		referencedColumnName = "id"
	)
	private InterproDomain interproDomain;
	
	ProteinInterproDomain() {
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
	
	public Protein getProtein() {
		return protein;
	}
	
	public InterproDomain getInterproDomain() {
		return interproDomain;
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
		ProteinInterproDomain other = (ProteinInterproDomain) obj;
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
