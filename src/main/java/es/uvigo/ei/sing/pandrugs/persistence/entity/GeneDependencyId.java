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

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class GeneDependencyId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String geneDependencySymbol;
	
	private String directGeneSymbol;

	private int drugId;

	private boolean target;
	
	
	GeneDependencyId() {
	}
	
	public String getGeneDependencySymbol() {
		return geneDependencySymbol;
	}
	
	public String getDirectGeneSymbol() {
		return directGeneSymbol;
	}

	public int getDrugId() {
		return drugId;
	}
	
	public boolean isTarget() {
		return target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directGeneSymbol == null) ? 0 : directGeneSymbol.hashCode());
		result = prime * result + drugId;
		result = prime * result + ((geneDependencySymbol == null) ? 0 : geneDependencySymbol.hashCode());
		result = prime * result + (target ? 1231 : 1237);
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
		GeneDependencyId other = (GeneDependencyId) obj;
		if (directGeneSymbol == null) {
			if (other.directGeneSymbol != null)
				return false;
		} else if (!directGeneSymbol.equals(other.directGeneSymbol))
			return false;
		if (drugId != other.drugId)
			return false;
		if (geneDependencySymbol == null) {
			if (other.geneDependencySymbol != null)
				return false;
		} else if (!geneDependencySymbol.equals(other.geneDependencySymbol))
			return false;
		if (target != other.target)
			return false;
		return true;
	}
}
