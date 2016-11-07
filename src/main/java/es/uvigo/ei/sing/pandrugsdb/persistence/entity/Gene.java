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

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
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
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tumor_portal_mutation_level", nullable = true)
	private TumorPortalMutationLevel tumorPortalMutationLevel;
	
	@Column(name = "cgc")
	private boolean cgc;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "driver_level", nullable = true)
	private DriverLevel driverLevel;

	@Column(name = "gene_essentiality_score", nullable = true, precision = 10)
	private Double geneEssentialityScore;
	
	@OneToMany(mappedBy = "gene", fetch = FetchType.LAZY)
	private Set<GeneDrug> geneDrugs;
	
	@ManyToMany(mappedBy = "genes", fetch = FetchType.LAZY)
	private Set<Protein> proteins;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "gene_gene",
		joinColumns = @JoinColumn(name = "gene_gene_symbol", referencedColumnName = "gene_symbol"),
		inverseJoinColumns = @JoinColumn(name = "gene_interacting_gene_symbol", referencedColumnName = "gene_symbol")
	)
	private Set<Gene> interactingGene;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "gene_pathway",
		joinColumns = @JoinColumn(name = "gene_gene_symbol", referencedColumnName = "gene_symbol"),
		inverseJoinColumns = @JoinColumn(name = "pathway_kegg_id", referencedColumnName = "kegg_id")
	)
	private Set<Pathway> pathways;
	
	Gene() {
	}
	
	public Gene(String geneSymbol) {
		this(geneSymbol, null, false, null, null);
	}
	
	public Gene(
		String geneSymbol,
		TumorPortalMutationLevel tumorPortalMutationLevel,
		boolean cgc,
		DriverLevel driverLevel,
		Double geneEssentialityScore
	) {
		this(
			geneSymbol,
			tumorPortalMutationLevel,
			cgc,
			driverLevel,
			geneEssentialityScore,
			emptySet(),
			emptySet(),
			emptySet(),
			emptySet()
		);
	}

	
	public Gene(
		String geneSymbol,
		TumorPortalMutationLevel tumorPortalMutationLevel,
		boolean cgc,
		DriverLevel driverLevel,
		Double geneEssentialityScore,
		Set<GeneDrug> geneDrugs,
		Set<Protein> proteins,
		Set<Gene> interactingGene,
		Set<Pathway> pathways
	) {
		this.geneSymbol = geneSymbol;
		this.tumorPortalMutationLevel = tumorPortalMutationLevel;
		this.cgc = cgc;
		this.driverLevel = driverLevel;
		this.geneEssentialityScore = geneEssentialityScore;
		this.geneDrugs = geneDrugs;
		this.proteins = proteins;
		this.interactingGene = interactingGene;
		this.pathways = pathways;
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

	public double getGeneEssentialityScore() {
		return Optional.ofNullable(this.geneEssentialityScore)
			.orElse(0d);
	}

	public double getGScore() {
		double gScore = 0d;
		
		if (this.tumorPortalMutationLevel != null) {
			gScore += this.tumorPortalMutationLevel.getWeight();
		}
		
		if (this.cgc) {
			gScore += 0.2d;
		}
		
		if (this.driverLevel != null) {
			gScore += this.driverLevel.getWeight();
		}
		
		if (this.geneEssentialityScore != null) {
			gScore += this.geneEssentialityScore * 0.4d;
		}
		
		return gScore;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cgc ? 1231 : 1237);
		result = prime * result + ((driverLevel == null) ? 0 : driverLevel.hashCode());
		result = prime * result + ((geneEssentialityScore == null) ? 0 : geneEssentialityScore.hashCode());
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
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
		if (cgc != other.cgc)
			return false;
		if (driverLevel != other.driverLevel)
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
			.append(", GES: ").append(this.geneEssentialityScore)
			.append("]")
		.toString();
	}
}
