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

import static java.util.Collections.unmodifiableSet;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "protein_change")
@Table(indexes = @Index(columnList = "protein_change"))
@IdClass(ProteinChangeId.class)
public class ProteinChange implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "uniprot_id")
	private String uniprotId;

	@Id
	@Column(name = "protein_change", length = 10, columnDefinition = "VARCHAR(10)")
	private String change;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "uniprot_id",
		referencedColumnName = "uniprot_id",
		insertable = false, updatable = false
	)
	private Protein protein;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "changes")
	private Set<ProteinChangesPublication> publications;

	ProteinChange() {}

	public Protein getProtein() {
		return protein;
	}

	public String getChange() {
		return change;
	}
	
	public Set<ProteinChangesPublication> getPublications() {
		return unmodifiableSet(publications);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((change == null) ? 0 : change.hashCode());
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
		ProteinChange other = (ProteinChange) obj;
		if (change == null) {
			if (other.change != null)
				return false;
		} else if (!change.equals(other.change))
			return false;
		if (uniprotId == null) {
			if (other.uniprotId != null)
				return false;
		} else if (!uniprotId.equals(other.uniprotId))
			return false;
		return true;
	}
}
