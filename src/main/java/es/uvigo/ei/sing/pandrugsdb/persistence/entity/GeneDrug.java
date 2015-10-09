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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity(name = "gene_drug")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
	"gene_symbol", "drug_id", "target"
}))
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
	
	@ManyToOne
	@JoinColumn(name = "drug_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Drug drug;

	@Column(name = "family", length = 50, columnDefinition = "VARCHAR(50)")
	private String family;
	
	@Enumerated(EnumType.STRING)
	private ResistanceType resistance;

	@Column(name = "alteration", length = 100, columnDefinition = "VARCHAR(100)")
	private String alteration;
	
	private double score;
	
	@OneToMany(mappedBy = "geneDrug")
	private List<IndirectGene> indirectGenes;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "gene_drug_drug_source",
		joinColumns = {
			@JoinColumn(name = "gene_symbol", referencedColumnName = "gene_symbol"),
			@JoinColumn(name = "drug_id", referencedColumnName = "drug_id"),
			@JoinColumn(name = "target", referencedColumnName = "target")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "source", referencedColumnName = "source"),
			@JoinColumn(name = "source_drug_name", referencedColumnName = "source_drug_name"),
		}
	)
	private List<DrugSource> drugSources;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "gene_drug_pathway",
		joinColumns = {
			@JoinColumn(name = "gene_symbol_id", referencedColumnName = "gene_symbol"),
			@JoinColumn(name = "drug_id", referencedColumnName = "drug_id"),
			@JoinColumn(name = "target", referencedColumnName = "target")
		},
		inverseJoinColumns = @JoinColumn(name = "pathway_id", referencedColumnName = "id")
	)
	private List<Pathway> pathways;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(
		name = "gene_symbol",
		referencedColumnName = "gene_symbol",
		columnDefinition = "VARCHAR(50)",
		insertable = false, updatable = false,
		nullable = true
	)
	private GeneInformation geneInformation;
	
	GeneDrug() {}
	
	GeneDrug(
		String geneSymbol,
		Drug drug,
		String family,
		boolean target,
		ResistanceType resistance,
		String alteration,
		double score,
		List<String> inverseGene,
		List<DrugSource> drugSources, 
		List<Pathway> pathways,
		GeneInformation geneInformation
	) {
		this.geneSymbol = geneSymbol;
		this.drugId = drug.getId();
		this.drug = drug;
		this.family = family;
		this.target = target;
		this.resistance = resistance;
		this.alteration = alteration;
		this.score = score;
		this.indirectGenes = inverseGene.stream()
			.map(gs -> new IndirectGene(this, gs))
		.collect(toList());
		this.drugSources = drugSources;
		this.pathways = pathways;
		this.geneInformation = geneInformation;
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
		final List<String> genes = new ArrayList<>(getIndirectGenes());
		genes.add(this.getGeneSymbol());
		
		return genes;
	}
	
	public List<String> getIndirectGenes() {
		return this.indirectGenes.stream()
			.map(IndirectGene::getGeneSymbol)
		.collect(toList());
	}
	
	public List<DrugSource> getDrugSources() {
		return unmodifiableList(this.drugSources);
	}

	public List<String> getDrugSourceNames() {
		return this.drugSources.stream()
			.map(DrugSource::getSource)
			.distinct()
			.sorted()
		.collect(toList());
	}
	
	public List<DrugSource> getCuratedDrugSources() {
		return this.drugSources.stream()
			.filter(DrugSource::isCurated)
		.collect(toList());
	}
	
	public List<String> getCuratedDrugSourceNames() {
		return this.drugSources.stream()
			.filter(DrugSource::isCurated)
			.map(DrugSource::getSource)
			.distinct()
		.collect(toList());
	}
	
	public List<Pathway> getPathways() {
		return unmodifiableList(pathways);
	}
	
	public GeneInformation getGeneInformation() {
		return geneInformation;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alteration == null) ? 0 : alteration.hashCode());
		result = prime * result + ((drug == null) ? 0 : drug.hashCode());
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result + ((geneInformation == null) ? 0 : geneInformation.hashCode());
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
		if (drug == null) {
			if (other.drug != null)
				return false;
		} else if (!drug.equals(other.drug))
			return false;
		if (family == null) {
			if (other.family != null)
				return false;
		} else if (!family.equals(other.family))
			return false;
		if (geneInformation == null) {
			if (other.geneInformation != null)
				return false;
		} else if (!geneInformation.equals(other.geneInformation))
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
