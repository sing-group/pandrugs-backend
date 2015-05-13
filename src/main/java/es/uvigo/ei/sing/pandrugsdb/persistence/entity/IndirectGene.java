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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "indirect_gene")
@Table(
	indexes = @Index(name = "idx_gene_symbol", columnList = "gene_symbol")
)
@IdClass(IndirectGeneId.class)
public class IndirectGene implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "direct_gene_id")
	private int directGeneId;
	
	@Id
	@Column(name = "gene_symbol", length = 255, columnDefinition = "VARCHAR(255)")
	private String geneSymbol;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "direct_gene_id", referencedColumnName = "id",
		insertable = false, updatable = false
	)
	private GeneDrug geneDrug;
	
	IndirectGene() {
	}
	
	public IndirectGene(GeneDrug geneDrug, String geneSymbol) {
		this.directGeneId = geneDrug.getId();
		this.geneSymbol = geneSymbol;
		this.geneDrug = geneDrug;
	}
	
	public int getDirectGeneId() {
		return directGeneId;
	}

	public void setDirectGeneId(int id) {
		this.directGeneId = id;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}
	
	public GeneDrug getGeneDrug() {
		return geneDrug;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + directGeneId;
		result = prime * result
				+ ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IndirectGene other = (IndirectGene) obj;
		if (directGeneId != other.directGeneId) {
			return false;
		}
		if (geneSymbol == null) {
			if (other.geneSymbol != null) {
				return false;
			}
		} else if (!geneSymbol.equals(other.geneSymbol)) {
			return false;
		}
		return true;
	}
}
