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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "gene_drug_warning")
public class GeneDrugWarning implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "gene_symbol", length = 100, columnDefinition = "VARCHAR(100)")
	private String geneSymbol;

	@Column(name = "standard_drug_name", length = 2000, columnDefinition = "VARCHAR(2000)")
	private String standardDrugName;
	
	@Column(name = "warning", length = 100, columnDefinition = "VARCHAR(100)")
	private String warning;
	
	public Long getId() {
		return id;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public String getStandardDrugName() {
		return standardDrugName;
	}

	public String getWarning() {
		return warning;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((standardDrugName == null) ? 0 : standardDrugName.hashCode());
		result = prime * result + ((warning == null) ? 0 : warning.hashCode());
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
		GeneDrugWarning other = (GeneDrugWarning) obj;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (standardDrugName == null) {
			if (other.standardDrugName != null)
				return false;
		} else if (!standardDrugName.equals(other.standardDrugName))
			return false;
		if (warning == null) {
			if (other.warning != null)
				return false;
		} else if (!warning.equals(other.warning))
			return false;
		return true;
	}
}
