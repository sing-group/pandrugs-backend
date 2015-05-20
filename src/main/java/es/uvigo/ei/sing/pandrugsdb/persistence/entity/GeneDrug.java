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

import static es.uvigo.ei.sing.pandrugsdb.util.CompareCollections.equalsIgnoreOrder;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
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
	"gene_symbol", "standard_drug_name", "target"
}),
indexes = {
	@Index(name = "idx_gene_symbol", columnList = "gene_symbol"),
	@Index(name = "idx_standard_drug_name", columnList = "standard_drug_name"),
	@Index(name = "idx_cancer", columnList = "cancer"),
	@Index(name = "idx_status", columnList = "status"),
	@Index(name = "idx_target", columnList = "target"),
	@Index(name = "idx_gene_query", columnList = "gene_symbol,cancer,status")
})
public class GeneDrug implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "gene_symbol", length = 255, columnDefinition = "VARCHAR(255)")
	private String geneSymbol;
	
	@Column(name = "standard_drug_name", length = 1000, columnDefinition = "VARCHAR(1000)")
	private String standardDrugName;
	
	@Column(name = "target")
	private boolean target;
	
	private String family;
	@Enumerated(EnumType.STRING)
	private DrugStatus status;
	private String pathology;
	private String cancer;
	private String extra;
	private String resistance;
	private String alteration;
	private double score;
	
	@OneToMany(mappedBy = "geneDrug")
	private List<IndirectGene> indirectGenes;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "gene_drug_drug_source",
		joinColumns = @JoinColumn(name = "gene_drug_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "source_id", referencedColumnName = "id")
	)
	private List<DrugSource> drugSources;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "gene_drug_pathway",
		joinColumns = @JoinColumn(name = "gene_drug_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "pathway_id", referencedColumnName = "id")
	)
	private List<Pathway> pathways;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(
		name = "gene_symbol",
		referencedColumnName = "gene_symbol",
		columnDefinition = "VARCHAR(255)",
		insertable = false, updatable = false,
		nullable = true
	)
	private GeneInformation geneInformation;
	
	GeneDrug() {}

	GeneDrug(
		int id,
		String geneSymbol,
		String standardDrugName,
		String family, 
		DrugStatus status,
		String pathology,
		String cancer,
		String extra,
		boolean target,
		String resistance,
		String alteration,
		double score,
		List<String> inverseGene,
		List<DrugSource> drugSources, 
		List<Pathway> pathways,
		GeneInformation geneInformation
	) {
		this.id = id;
		this.geneSymbol = geneSymbol;
		this.standardDrugName = standardDrugName;
		this.family = family;
		this.status = status;
		this.pathology = pathology;
		this.cancer = cancer;
		this.extra = extra;
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
	
	public int getId() {
		return id;
	}

	public String getGeneSymbol() {
		return this.geneSymbol;
	}

	public String getStandardDrugName() {
		return this.standardDrugName;
	}

	public String getFamily() {
		return family;
	}

	public DrugStatus getStatus() {
		return status;
	}

	public String getPathology() {
		return pathology;
	}

	public String getCancer() {
		return cancer;
	}

	public String getExtra() {
		return extra;
	}

	public boolean isTarget() {
		return target;
	}
	
	public String getResistance() {
		return resistance;
	}
	
	public boolean isResistance() {
		return Optional.ofNullable(this.resistance)
			.map(r -> r.equals("resistance"))
		.orElse(Boolean.FALSE);
	}

	public String getAlteration() {
		return alteration;
	}

	public double getScore() {
		return score;
	}
	
	public List<String> getDirectAndIndirectGenes() {
		final List<String> genes = new ArrayList<>(getIndirectGenes());
		genes.add(this.geneSymbol);
		
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
		.collect(Collectors.toList());
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
		result = prime * result
				+ ((alteration == null) ? 0 : alteration.hashCode());
		result = prime * result + ((cancer == null) ? 0 : cancer.hashCode());
		result = prime * result
				+ ((drugSources == null) ? 0 : drugSources.hashCode());
		result = prime * result + ((extra == null) ? 0 : extra.hashCode());
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result
				+ ((geneInformation == null) ? 0 : geneInformation.hashCode());
		result = prime * result
				+ ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((indirectGenes == null) ? 0 : indirectGenes.hashCode());
		result = prime * result
				+ ((pathology == null) ? 0 : pathology.hashCode());
		result = prime * result
				+ ((pathways == null) ? 0 : pathways.hashCode());
		result = prime * result
				+ ((resistance == null) ? 0 : resistance.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((standardDrugName == null) ? 0 : standardDrugName.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		if (cancer == null) {
			if (other.cancer != null)
				return false;
		} else if (!cancer.equals(other.cancer))
			return false;
		
		if (!equalsIgnoreOrder(drugSources, other.drugSources)) {
			return false;
		}
		if (extra == null) {
			if (other.extra != null)
				return false;
		} else if (!extra.equals(other.extra))
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
		if (id != other.id)
			return false;
		
		if (!equalsIgnoreOrder(indirectGenes, other.indirectGenes))
				return false;
		
		if (pathology == null) {
			if (other.pathology != null)
				return false;
		} else if (!pathology.equals(other.pathology))
			return false;
		
		if (!equalsIgnoreOrder(pathways, other.pathways))
				return false;
		
		if (resistance == null) {
			if (other.resistance != null)
				return false;
		} else if (!resistance.equals(other.resistance))
			return false;
		if (Double.doubleToLongBits(score) != Double
				.doubleToLongBits(other.score))
			return false;
		if (standardDrugName == null) {
			if (other.standardDrugName != null)
				return false;
		} else if (!standardDrugName.equals(other.standardDrugName))
			return false;
		if (status != other.status)
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
