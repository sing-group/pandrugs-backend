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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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
	
	@Enumerated(EnumType.STRING)
	@Column(name = "resistance", length = 12)
	private ResistanceType resistance;
	
	@Column(name = "score")
	private double score;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "drug_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Drug drug;
	
	@OneToMany(mappedBy = "geneDrug", fetch = FetchType.LAZY)
	private List<IndirectGene> indirectGenes;
	
	@OneToMany(mappedBy = "geneDrug", fetch = FetchType.LAZY)
	private List<GeneDependency> geneDependencies;
	
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
	
	@OneToMany(mappedBy = "geneDrug", fetch = FetchType.LAZY)
	private Set<GeneDrugToDrugSource> drugSources;

	GeneDrug() {
	}
	
	GeneDrug(
		Gene gene,
		Drug drug,
		boolean isTarget,
		ResistanceType resistance,
		double score,
		List<Gene> inverseGene,
		Map<Gene, String> geneDependencies
	) {
		this.geneSymbol = gene.getGeneSymbol();
		this.gene = gene;
		this.drugId = drug.getId();
		this.drug = drug;
		this.target = isTarget;
		this.resistance = resistance;
		this.score = score;
		this.indirectGenes = inverseGene.stream()
			.map(gs -> new IndirectGene(this, gs))
		.collect(toList());
		this.geneDependencies = geneDependencies.entrySet().stream()
			.map(entry -> new GeneDependency(this, entry.getKey(), entry.getValue()))
		.collect(toList());
		this.drugSources = drug.getDrugSources().stream()
			.map(ds -> new GeneDrugToDrugSource(this, ds, null))
		.collect(toSet());
	}
	
	GeneDrug(
		Gene gene,
		Drug drug,
		boolean isTarget,
		ResistanceType resistance,
		double score,
		List<Gene> inverseGene,
		Map<Gene, String> geneDependencies,
		Map<String, String> alterationsBySource
	) {
		this.geneSymbol = gene.getGeneSymbol();
		this.gene = gene;
		this.drugId = drug.getId();
		this.drug = drug;
		this.target = isTarget;
		this.resistance = resistance;
		this.score = score;
		this.indirectGenes = inverseGene.stream()
			.map(gs -> new IndirectGene(this, gs))
		.collect(toList());
		this.geneDependencies = geneDependencies.entrySet().stream()
			.map(entry -> new GeneDependency(this, entry.getKey(), entry.getValue()))
		.collect(toList());
		this.drugSources = drug.getDrugSources().stream()
			.map(ds -> new GeneDrugToDrugSource(this, ds, alterationsBySource.get(ds.getSource())))
		.collect(toSet());
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
	
	public Gene getGene() {
		return gene;
	}

	public String getStandardDrugName() {
		return this.drug.getStandardName();
	}

	public String getShowDrugName() {
		return this.drug.getShowName();
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
	
	public Set<GeneDrugToDrugSource> getDrugSources() {
		return unmodifiableSet(drugSources);
	}
	
	public List<String> getDrugSourceNames() {
		return this.drugSources.stream()
			.map(GeneDrugToDrugSource::getDrugSource)
			.map(DrugSource::getSource)
			.distinct()
			.sorted()
		.collect(toList());
	}
	
	public List<GeneDrugToDrugSource> getCuratedDrugSources() {
		return this.drugSources.stream()
			.filter(GeneDrugToDrugSource::isCurated)
		.collect(toList());
	}
	
	public List<String> getCuratedDrugSourceNames() {
		return this.drugSources.stream()
			.filter(GeneDrugToDrugSource::isCurated)
			.map(GeneDrugToDrugSource::getDrugSource)
			.map(DrugSource::getSource)
			.distinct()
		.collect(toList());
	}
	
	public int countCuratedDrugSources() {
		return (int) this.drugSources.stream()
			.filter(GeneDrugToDrugSource::isCurated)
		.count();
	}

	public double getScore() {
		return score;
	}
	
	public List<Gene> getAllGenes() {
		return Stream.concat(
			Stream.of(this.getGene()),
			getIndirectGenes().stream()
		)
			.distinct()
		.collect(toList());
	}
	
	public List<String> getAllGeneSymbols() {
		final List<String> genes = new ArrayList<>(getIndirectGeneSymbols());
		genes.add(this.getGeneSymbol());
		
		return genes;
	}
	
	public Map<String, Optional<Gene>> getIndirectGenesByGeneSymbol() {
		final Map<String, Optional<Gene>> genes = new HashMap<>();
		
		this.getPathwayMemberGenes().stream()
			.forEach(pm -> genes.put(pm.getGeneSymbol(), Optional.ofNullable(pm.getGene())));
		
		this.getGeneDependencies().stream()
			.forEach(gd -> genes.put(gd.getGeneSymbol(), Optional.ofNullable(gd.getGene())));
		
		return genes;
	}
	
	public List<Gene> getIndirectGenes() {
		return Stream.concat(
			this.getPathwayMemberGenes().stream()
				.map(IndirectGene::getGene),
			this.getGeneDependencies().stream()
				.map(GeneDependency::getGene)
		)
			.filter(Objects::nonNull)
			.distinct()
		.collect(toList());
	}
	
	public List<String> getIndirectGeneSymbols() {
		return this.getIndirectGenes().stream()
			.map(Gene::getGeneSymbol)
			.distinct()
		.collect(toList());
	}
	
	public boolean hasPathwayMembers() {
		return !this.indirectGenes.isEmpty();
	}
	
	public List<IndirectGene> getPathwayMemberGenes() {
		return unmodifiableList(this.indirectGenes);
	}
	
	public List<String> getPathwayMemberGeneSymbols() {
		return this.indirectGenes.stream()
			.map(IndirectGene::getGeneSymbol)
		.collect(toList());
	}
	
	public boolean hasGeneDependencies() {
		return !this.geneDependencies.isEmpty();
	}
	
	public List<GeneDependency> getGeneDependencies() {
		return unmodifiableList(this.geneDependencies);
	}
	
	public List<String> getGeneDependenciesGeneSymbols() {
		return this.geneDependencies.stream()
			.map(GeneDependency::getGeneSymbol)
		.collect(toList());
	}

	public Map<String, String> getGeneDependencyAlterationsFor(List<Gene> genes) {
		final Set<String> geneSymbols = genes.stream().map(Gene::getGeneSymbol).collect(toSet());
		
		return this.geneDependencies.stream()
			.filter(gd -> geneSymbols.contains(gd.getGeneSymbol()))
		.collect(toMap(
			GeneDependency::getGeneSymbol,
			GeneDependency::getAlteration
		));
	}
	
	public boolean hasIndirectGene(String geneSymbol) {
		return this.indirectGenes.stream().anyMatch(ig -> ig.getGeneSymbol().equals(geneSymbol))
			|| this.geneDependencies.stream().anyMatch(gd -> gd.getGeneSymbol().equals(geneSymbol));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(drugId, geneSymbol, resistance, score, target);
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
		return drugId == other.drugId && Objects.equals(geneSymbol, other.geneSymbol) && resistance == other.resistance
			&& Double.doubleToLongBits(score) == Double.doubleToLongBits(other.score) && target == other.target;
	}

	@Override
	public String toString() {
		return this.getGeneSymbol() + " - " + this.getStandardDrugName();
	}
}
