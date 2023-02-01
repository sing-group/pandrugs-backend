/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2023 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name = "pathway")
public class Pathway implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "kegg_id", length = 8, columnDefinition = "CHAR(8)")
	private String id;
	
	@Column(name = "name", length = 150, columnDefinition = "VARCHAR(150)")
	private String name;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "pathways")
	private Set<Gene> genes;
	
	Pathway() {}
	
	Pathway(String id, String name, Set<Gene> genes) {
		this.id = id;
		this.name = name;
		this.genes = genes;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public boolean hasAnyGene(String ... genes) {
		final Set<String> geneSet = stream(genes).collect(toSet());
		
		return this.genes.stream()
			.map(Gene::getGeneSymbol)
		.anyMatch(geneSet::contains);
	}

	public Set<Gene> getGenes() {
		return unmodifiableSet(genes);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Pathway other = (Pathway) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
