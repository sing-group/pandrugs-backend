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

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity(name = "gene")
public class Gene implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "gene_symbol", length = 50, columnDefinition = "VARCHAR(50)")
	private String geneSymbol;
	
	@Column(name = "gscore", nullable = true)
	private Double gscore;

	@ElementCollection
	@CollectionTable(name = "gene_entrez", joinColumns = @JoinColumn(name = "gene_symbol", insertable = false, updatable = false, nullable = true))
	@Column(name = "entrez_id", nullable = false, updatable = false, insertable = false)
	private Set<Integer> entrezIds;

	@Enumerated(EnumType.STRING)
	@Column(name = "tumor_portal_mutation_level", nullable = true)
	private TumorPortalMutationLevel tumorPortalMutationLevel;

	@Column(name = "cgc", nullable = false)
	private boolean cgc;

	@Enumerated(EnumType.STRING)
	@Column(name = "driver_level", nullable = true)
	private DriverLevel driverLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "driver_gene", nullable = true, length = 12)
	private DriverGene driverGene;

	@Column(name = "gene_essentiality_score", nullable = true, precision = 10)
	private Double geneEssentialityScore;

	@Column(name = "oncoscape_score", nullable = true, precision = 10)
	private Double oncoscapeScore;

	@Column(name = "ccle", nullable = false)
	private boolean ccle;

	@Enumerated(EnumType.STRING)
	@Column(name = "oncodrive_role", nullable = false)
	private OncodriveRole oncodriveRole;

	@OneToMany(mappedBy = "gene", fetch = FetchType.LAZY)
	private Set<GeneDrug> geneDrugs;

	@ManyToMany(mappedBy = "genes", fetch = FetchType.LAZY)
	private Set<Protein> proteins;

	@ElementCollection
	@JoinTable(name = "cancer_domain", joinColumns = @JoinColumn(name = "gene_symbol", referencedColumnName = "gene_symbol", columnDefinition = "VARCHAR(50)", insertable = false, updatable = false, nullable = true))
	@Column(name = "code", length = 7, columnDefinition = "CHAR(7)", nullable = false, updatable = false, insertable = false)
	private Set<String> cancerDomains;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "gene_gene", joinColumns = @JoinColumn(name = "gene_gene_symbol", referencedColumnName = "gene_symbol"), inverseJoinColumns = @JoinColumn(name = "gene_interacting_gene_symbol", referencedColumnName = "gene_symbol"))
	private Set<Gene> interactingGene;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "gene_pathway", joinColumns = @JoinColumn(name = "gene_gene_symbol", referencedColumnName = "gene_symbol"), inverseJoinColumns = @JoinColumn(name = "pathway_kegg_id", referencedColumnName = "kegg_id"))
	private Set<Pathway> pathways;

	Gene() {}

	Gene(String geneSymbol) {
		this(geneSymbol, null, false, null, null, null, false, null, OncodriveRole.NONE);
	}

	Gene(
		String geneSymbol,
		TumorPortalMutationLevel tumorPortalMutationLevel,
		boolean cgc,
		DriverLevel driverLevel,
		DriverGene driverGene,
		Double geneEssentialityScore,
		boolean ccle,
		Double oncoscapeScore, OncodriveRole oncodriveRole
	) {
		this(
			geneSymbol,
			tumorPortalMutationLevel,
			cgc,
			driverLevel,
			driverGene,
			geneEssentialityScore,
			ccle,
			oncoscapeScore,
			oncodriveRole,
			emptySet(),
			emptySet(),
			emptySet(),
			emptySet(),
			emptySet(), emptySet()
		);
	}

	Gene(
		String geneSymbol,
		TumorPortalMutationLevel tumorPortalMutationLevel,
		boolean cgc,
		DriverLevel driverLevel,
		DriverGene driverGene,
		Double geneEssentialityScore,
		boolean ccle,
		Double oncoscapeScore,
		OncodriveRole oncodriveRole,
		Set<Integer> entrezIds,
		Set<String> cancerDomains,
		Set<GeneDrug> geneDrugs,
		Set<Protein> proteins, Set<Gene> interactingGene, Set<Pathway> pathways
	) {
		this.geneSymbol = requireNonNull(geneSymbol);
		this.tumorPortalMutationLevel = tumorPortalMutationLevel;
		this.cgc = cgc;
		this.driverLevel = driverLevel;
		this.driverGene = driverGene;
		this.geneEssentialityScore = geneEssentialityScore;
		this.ccle = ccle;
		this.oncodriveRole = requireNonNull(oncodriveRole);
		this.oncoscapeScore = oncoscapeScore;
		this.entrezIds = entrezIds;
		this.cancerDomains = cancerDomains;
		this.geneDrugs = geneDrugs;
		this.proteins = proteins;
		this.interactingGene = interactingGene;
		this.pathways = pathways;
		this.gscore = this.calculateGScore();
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public TumorPortalMutationLevel getTumorPortalMutationLevel() {
		return tumorPortalMutationLevel;
	}

	public boolean isCgc() {
		return cgc;
	}

	public DriverLevel getDriverLevel() {
		return driverLevel;
	}
	
	public DriverGene getDriverGene() {
		return driverGene;
	}

	public boolean isCcle() {
		return ccle;
	}

	public OncodriveRole getOncodriveRole() {
		return oncodriveRole;
	}

	public double getGeneEssentialityScore() {
		return Optional.ofNullable(this.geneEssentialityScore)
			.orElse(0d);
	}

	public double getOncoscapeScore() {
		return Optional.ofNullable(this.oncoscapeScore)
			.orElse(0d);
	}

	public double getGScore() {
		return this.gscore == null ? this.calculateGScore() : this.gscore;
	}
	
	private double calculateGScore() {
		double gScore = 0d;

		if (this.tumorPortalMutationLevel != null) {
			gScore += this.tumorPortalMutationLevel.getWeight(); // Max 0.1
		}

		if (this.cgc) {
			gScore += 0.1d;
		}

		if (this.driverLevel != null) {
			gScore += this.driverLevel.getWeight(); // Max 0.1
		}

		if (this.geneEssentialityScore != null) {
			gScore += this.geneEssentialityScore * 0.4d;
		}

		if (this.oncoscapeScore != null) {
			gScore += (this.oncoscapeScore / 4) * 0.3d;
		}

		return gScore;
	}

	public Set<Integer> getEntrezIds() {
		return unmodifiableSet(entrezIds);
	}

	public Set<Protein> getProteins() {
		return unmodifiableSet(proteins);
	}

	public Set<Gene> getInteractingGenes() {
		return unmodifiableSet(interactingGene);
	}

	public Set<GeneDrug> getGeneDrugs() {
		return unmodifiableSet(geneDrugs);
	}

	public Set<Pathway> getPathways() {
		return unmodifiableSet(pathways);
	}

	public Set<String> getCancerDomains() {
		return unmodifiableSet(cancerDomains);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ccle ? 1231 : 1237);
		result = prime * result + (cgc ? 1231 : 1237);
		result = prime * result + ((driverLevel == null) ? 0 : driverLevel.hashCode());
		result = prime * result + ((driverGene == null) ? 0 : driverGene.hashCode());
		result = prime * result + ((geneEssentialityScore == null) ? 0 : geneEssentialityScore.hashCode());
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + ((oncoscapeScore == null) ? 0 : oncoscapeScore.hashCode());
		result = prime * result + ((oncodriveRole == null) ? 0 : oncodriveRole.hashCode());
		result = prime * result + ((tumorPortalMutationLevel == null) ? 0 : tumorPortalMutationLevel.hashCode());
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
		Gene other = (Gene) obj;
		if (ccle != other.ccle)
			return false;
		if (cgc != other.cgc)
			return false;
		if (driverLevel != other.driverLevel)
			return false;
		if (driverGene != other.driverGene)
			return false;
		if (geneEssentialityScore == null) {
			if (other.geneEssentialityScore != null)
				return false;
		} else if (!geneEssentialityScore.equals(other.geneEssentialityScore))
			return false;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (oncoscapeScore == null) {
			if (other.oncoscapeScore != null)
				return false;
		} else if (!oncoscapeScore.equals(other.oncoscapeScore))
			return false;
		if (oncodriveRole != other.oncodriveRole)
			return false;
		if (tumorPortalMutationLevel != other.tumorPortalMutationLevel)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder(this.geneSymbol)
			.append(" [TPML: ").append(this.tumorPortalMutationLevel)
			.append(", CGC: ").append(this.cgc)
			.append(", DL: ").append(this.driverLevel)
			.append(", DG: ").append(this.driverGene)
			.append(", GES: ").append(this.geneEssentialityScore)
			.append(", CCLE: ").append(this.ccle)
			.append(", OS: ").append(this.oncoscapeScore)
			.append("]")
			.toString();
	}
}
