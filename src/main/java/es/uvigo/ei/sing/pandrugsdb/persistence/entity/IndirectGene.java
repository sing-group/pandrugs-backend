/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Entity(name = "indirect_gene")
@IdClass(IndirectGeneId.class)
public class IndirectGene implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "indirect_gene_symbol", length = 50, columnDefinition = "VARCHAR(50)")
	private String indirectGeneSymbol;
	
	@Id
	@Column(name = "direct_gene_symbol", length = 50, columnDefinition = "VARCHAR(50)")
	private String directGeneSymbol;
	
	@Id
	@Column(name = "drug_id")
	private int drugId;
	
	@Id
	private boolean target;
	
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
	
	IndirectGene() {
	}
	
	public IndirectGene(GeneDrug geneDrug, String geneSymbol) {
		this.indirectGeneSymbol = geneSymbol;
		
		this.directGeneSymbol = geneDrug.getGeneSymbol();
		this.drugId = geneDrug.getDrug().getId();
		this.target = geneDrug.isTarget();
		
		this.geneDrug = geneDrug;
	}

	public String getGeneSymbol() {
		return indirectGeneSymbol;
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

	public GeneDrug getGeneDrug() {
		return geneDrug;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directGeneSymbol == null) ? 0 : directGeneSymbol.hashCode());
		result = prime * result + drugId;
		result = prime * result + ((indirectGeneSymbol == null) ? 0 : indirectGeneSymbol.hashCode());
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
		IndirectGene other = (IndirectGene) obj;
		if (directGeneSymbol == null) {
			if (other.directGeneSymbol != null)
				return false;
		} else if (!directGeneSymbol.equals(other.directGeneSymbol))
			return false;
		if (drugId != other.drugId)
			return false;
		if (indirectGeneSymbol == null) {
			if (other.indirectGeneSymbol != null)
				return false;
		} else if (!indirectGeneSymbol.equals(other.indirectGeneSymbol))
			return false;
		if (target != other.target)
			return false;
		return true;
	}
}
