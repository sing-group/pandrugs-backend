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

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity(name = "gene_drug")
@IdClass(GeneDrugId.class)
public class GeneDrug implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "gene_symbol", length = 50, columnDefinition = "VARCHAR(50)")
	private String geneSymbol;

	@Id
	@Column(name = "drug_id")
	private int drugId;
	
	@Id
	@Column(name = "target")
	private boolean target;

	@Column(name = "family", length = 100, columnDefinition = "VARCHAR(100)")
	private String family;
	
	@Enumerated(EnumType.STRING)
	private ResistanceType resistance;

	@Column(name = "alteration", length = 100, columnDefinition = "VARCHAR(100)")
	private String alteration;
	
	@Column(name = "score")
	private double score;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "drug_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Drug drug;
	
	@OneToMany(mappedBy = "geneDrug", fetch = FetchType.LAZY)
	private List<IndirectGene> indirectGenes;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(
		name = "gene_symbol",
		referencedColumnName = "gene_symbol",
		columnDefinition = "VARCHAR(50)",
		insertable = false, updatable = false,
		nullable = true
	)
	private Gene gene;

	GeneDrug() {
	}
	
	GeneDrug(
		Gene gene,
		Drug drug,
		boolean isTarget,
		String family,
		String alteration,
		ResistanceType resistance,
		double score,
		List<String> inverseGene
	) {
		this.geneSymbol = gene.getGeneSymbol();
		this.gene = gene;
		this.drugId = drug.getId();
		this.drug = drug;
		this.family = family;
		this.target = isTarget;
		this.resistance = resistance;
		this.alteration = alteration;
		this.score = score;
		this.indirectGenes = inverseGene.stream()
			.map(gs -> new IndirectGene(this, gs))
		.collect(toList());
	}
	
	public int getDrugId() {
		return drugId;
	}
	
	public Drug getDrug() {
		return drug;
	}
	
	public String getGeneSymbol() {
		return this.geneSymbol;
	}

	public String getStandardDrugName() {
		return this.drug.getStandardName();
	}

	public String getShowDrugName() {
		return this.drug.getShowName();
	}

	public String getFamily() {
		return family;
	}

	public DrugStatus getStatus() {
		return this.drug.getStatus();
	}

	public String[] getPathologies() {
		return this.drug.getPathologies();
	}

	public CancerType[] getCancers() {
		return this.drug.getCancers();
	}
	
	public int[] getPubMedIds() {
		return this.drug.getPubChemIds();
	}

	public Extra getExtra() {
		return this.drug.getExtra();
	}

	public boolean isTarget() {
		return target;
	}
	
	public ResistanceType getResistance() {
		return this.resistance;
	}
	
	public boolean isResistance() {
		return this.resistance == null || this.resistance == ResistanceType.RESISTANCE;
	}

	public String getAlteration() {
		return alteration;
	}

	public double getScore() {
		return score;
	}
	
	public List<String> getDirectAndIndirectGenes() {
		final List<String> genes = new ArrayList<>(getIndirectGeneSymbols());
		genes.add(this.getGeneSymbol());
		
		return genes;
	}
	
	public List<IndirectGene> getIndirectGenes() {
		return unmodifiableList(this.indirectGenes);
	}
	
	public List<String> getIndirectGeneSymbols() {
		return this.indirectGenes.stream()
			.map(IndirectGene::getGeneSymbol)
		.collect(toList());
	}
	
	public Gene getGene() {
		return gene;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alteration == null) ? 0 : alteration.hashCode());
		result = prime * result + drugId;
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + ((resistance == null) ? 0 : resistance.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		GeneDrug other = (GeneDrug) obj;
		if (alteration == null) {
			if (other.alteration != null)
				return false;
		} else if (!alteration.equals(other.alteration))
			return false;
		if (drugId != other.drugId)
			return false;
		if (family == null) {
			if (other.family != null)
				return false;
		} else if (!family.equals(other.family))
			return false;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (resistance != other.resistance)
			return false;
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;
		if (target != other.target)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getGeneSymbol() + " - " + this.getStandardDrugName();
	}
}