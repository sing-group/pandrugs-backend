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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity(name = "gene_dependency")
@IdClass(GeneDependency.class)
public class GeneDependency implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "gene_dependency_symbol", length = 50, columnDefinition = "VARCHAR(50)")
	private String geneDependencySymbol;
	
	@Id
	@Column(name = "direct_gene_symbol", length = 50, columnDefinition = "VARCHAR(50)")
	private String directGeneSymbol;
	
	@Id
	@Column(name = "drug_id")
	private int drugId;
	
	@Id
	@Column(name = "target")
	private boolean target;
	
	@Column(name = "alteration", length = 3, columnDefinition = "VARCHAR(3)", nullable = false)
	private String alteration;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(
			name = "direct_gene_symbol", referencedColumnName = "gene_symbol",
			insertable = false, updatable = false
		),
		@JoinColumn(
			name = "drug_id", referencedColumnName = "drug_id",
			insertable = false, updatable = false
		),
		@JoinColumn(
			name = "target", referencedColumnName = "target",
			insertable = false, updatable = false
		)
	})
	private GeneDrug geneDrug;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(
		name = "gene_dependency_symbol",
		referencedColumnName = "gene_symbol",
		columnDefinition = "VARCHAR(50)",
		insertable = false, updatable = false
	)
	private Gene gene;
	
	GeneDependency() {
	}
	
	public GeneDependency(GeneDrug geneDrug, Gene gene, String alteration) {
		this.geneDependencySymbol = gene.getGeneSymbol();
		
		this.directGeneSymbol = geneDrug.getGeneSymbol();
		this.drugId = geneDrug.getDrug().getId();
		this.target = geneDrug.isTarget();
		this.alteration = alteration;
		
		this.geneDrug = geneDrug;
		this.gene = gene;
	}

	public String getGeneSymbol() {
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
	
	public String getAlteration() {
		return alteration;
	}

	public GeneDrug getGeneDrug() {
		return geneDrug;
	}
	
	public Gene getGene() {
		return gene;
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
		GeneDependency other = (GeneDependency) obj;
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
