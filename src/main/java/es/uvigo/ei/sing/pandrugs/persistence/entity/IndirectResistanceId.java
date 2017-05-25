/*
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2017 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
public class IndirectResistanceId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String queryGene;
	private String affectedGene;
	
	IndirectResistanceId() {}
	
	public IndirectResistanceId(String queryGene, String affectedGene) {
		this.queryGene = queryGene;
		this.affectedGene = affectedGene;
	}

	public String getQueryGene() {
		return queryGene;
	}

	public String getAffectedGene() {
		return affectedGene;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((affectedGene == null) ? 0 : affectedGene.hashCode());
		result = prime * result + ((queryGene == null) ? 0 : queryGene.hashCode());
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
		IndirectResistanceId other = (IndirectResistanceId) obj;
		if (affectedGene == null) {
			if (other.affectedGene != null)
				return false;
		} else if (!affectedGene.equals(other.affectedGene))
			return false;
		if (queryGene == null) {
			if (other.queryGene != null)
				return false;
		} else if (!queryGene.equals(other.queryGene))
			return false;
		return true;
	}

}