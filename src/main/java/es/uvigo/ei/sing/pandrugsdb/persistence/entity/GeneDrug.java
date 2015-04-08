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
import java.util.Collections;
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
import javax.persistence.UniqueConstraint;

@Entity(name = "gene_drug")
@IdClass(GeneDrugId.class)
public class GeneDrug implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "gene_symbol")
	private String geneSymbol;
	
	@Id
	@Column(name = "standard_drug_name")
	private String standardDrugName;
	
	private String family;
	@Enumerated(EnumType.STRING)
	private DrugStatus status;
	private String pathology;
	private String cancer;
	private String extra;
	private boolean target;
	private String resistance;
	private String alteration;
	private double score;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "gene_drug_drug_source",
		uniqueConstraints = @UniqueConstraint(columnNames = {"gene_symbol", "standard_drug_name", "source_id"}),
		joinColumns = {
			@JoinColumn(name = "gene_symbol", referencedColumnName = "gene_symbol"),
			@JoinColumn(name = "standard_drug_name", referencedColumnName = "standard_drug_name")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "source_id", referencedColumnName = "id"),
		}
	)
	private List<DrugSource> drugSources;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "gene_drug_pathway",
		uniqueConstraints = @UniqueConstraint(columnNames = {"gene_symbol", "standard_drug_name", "pathway_id"}),
		joinColumns = {
			@JoinColumn(name = "gene_symbol", referencedColumnName = "gene_symbol"),
			@JoinColumn(name = "standard_drug_name", referencedColumnName = "standard_drug_name")
		},
		inverseJoinColumns = @JoinColumn(name = "pathway_id", referencedColumnName = "id")
	)
	private List<Pathway> pathways;
	
	GeneDrug() {}

	GeneDrug(
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
		List<DrugSource> drugSources, 
		List<Pathway> pathways
	) {
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
		this.drugSources = drugSources;
		this.pathways = pathways;
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

	public String getAlteration() {
		return alteration;
	}

	public double getScore() {
		return score;
	}
	
	public List<DrugSource> getDrugSources() {
		return Collections.unmodifiableList(this.drugSources);
	}
	
	public List<Pathway> getPathways() {
		return Collections.unmodifiableList(pathways);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((alteration == null) ? 0 : alteration.hashCode());
		result = prime * result + ((cancer == null) ? 0 : cancer.hashCode());
		result = prime * result + ((extra == null) ? 0 : extra.hashCode());
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result
				+ ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result
				+ ((pathology == null) ? 0 : pathology.hashCode());
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
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (pathology == null) {
			if (other.pathology != null)
				return false;
		} else if (!pathology.equals(other.pathology))
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
