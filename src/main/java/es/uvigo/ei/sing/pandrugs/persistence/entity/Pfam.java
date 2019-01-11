/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2019 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name = "pfam")
public class Pfam {
	@Id
	@Column(name = "accession", length = 7, columnDefinition = "CHAR(7)")
	private String accession;
	
	@Column(name = "domain_description", length = 80, columnDefinition = "VARCHAR(80)")
	private String domainDescription;

	@OneToMany(mappedBy = "pfam", fetch = FetchType.LAZY)
	private Set<ProteinPfam> proteins;
	
	Pfam() {}
	
	public String getAccession() {
		return accession;
	}
	
	public String getDomainDescription() {
		return domainDescription;
	}
	
	public Set<ProteinPfam> getProteins() {
		return proteins;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accession == null) ? 0 : accession.hashCode());
		result = prime * result + ((domainDescription == null) ? 0 : domainDescription.hashCode());
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
		Pfam other = (Pfam) obj;
		if (accession == null) {
			if (other.accession != null)
				return false;
		} else if (!accession.equals(other.accession))
			return false;
		if (domainDescription == null) {
			if (other.domainDescription != null)
				return false;
		} else if (!domainDescription.equals(other.domainDescription))
			return false;
		return true;
	}
}
