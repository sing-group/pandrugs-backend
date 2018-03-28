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

import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity(name = "protein")
public class Protein {
	@Id
	@Column(name = "uniprot_id")
	private String uniprotId;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "interaction_id", referencedColumnName = "uniprot_id")
	private Set<Protein> iteractions;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "gene", referencedColumnName = "gene_symbol")
	private Set<Gene> genes;
	
	@OneToMany(mappedBy = "protein", fetch = FetchType.LAZY)
	private Set<ProteinPfam> pfams;
	
	@OneToMany(mappedBy = "protein", fetch = FetchType.LAZY)
	private Set<ProteinChange> changes;
	
	@OneToMany(mappedBy = "protein", fetch = FetchType.LAZY)
	private Set<ProteinInterproDomain> interproDomains;
	
	Protein() {}
	
	Protein(String uniprotId) {
		this.uniprotId = uniprotId;
	}

	public String getUniprotId() {
		return uniprotId;
	}
	
	public Set<Protein> getInteractions() {
		return unmodifiableSet(iteractions);
	}

	public Set<Gene> getGenes() {
		return unmodifiableSet(genes);
	}
	
	public Set<ProteinPfam> getPfams() {
		return unmodifiableSet(pfams);
	}
	
	public Set<ProteinChange> getChanges() {
		return unmodifiableSet(changes);
	}
	
	public Set<ProteinInterproDomain> getInterproDomains() {
		return unmodifiableSet(interproDomains);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Protein other = (Protein) obj;
		if (uniprotId == null) {
			if (other.uniprotId != null)
				return false;
		} else if (!uniprotId.equals(other.uniprotId))
			return false;
		return true;
	}
}
