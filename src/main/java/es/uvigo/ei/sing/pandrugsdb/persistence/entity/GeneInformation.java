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
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity(name = "gene_info")
public class GeneInformation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "gene_symbol", length = 255)
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
	
	GeneInformation() {
	}
	
	public GeneInformation(
		String geneSymbol,
		TumorPortalMutationLevel tumorPortalMutationLevel,
		boolean cgc,
		DriverLevel driverLevel,
		Double geneEssentialityScore
	) {
		this.geneSymbol = geneSymbol;
		this.tumorPortalMutationLevel = tumorPortalMutationLevel;
		this.cgc = cgc;
		this.driverLevel = driverLevel;
		this.geneEssentialityScore = geneEssentialityScore;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cgc ? 1231 : 1237);
		result = prime * result
				+ ((driverLevel == null) ? 0 : driverLevel.hashCode());
		result = prime
				* result
				+ ((geneEssentialityScore == null) ? 0 : geneEssentialityScore
						.hashCode());
		result = prime * result
				+ ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime
				* result
				+ ((tumorPortalMutationLevel == null) ? 0
						: tumorPortalMutationLevel.hashCode());
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
		GeneInformation other = (GeneInformation) obj;
		if (cgc != other.cgc) {
			return false;
		}
		if (driverLevel != other.driverLevel) {
			return false;
		}
		if (geneEssentialityScore == null) {
			if (other.geneEssentialityScore != null) {
				return false;
			}
		} else if (!geneEssentialityScore.equals(other.geneEssentialityScore)) {
			return false;
		}
		if (geneSymbol == null) {
			if (other.geneSymbol != null) {
				return false;
			}
		} else if (!geneSymbol.equals(other.geneSymbol)) {
			return false;
		}
		if (tumorPortalMutationLevel != other.tumorPortalMutationLevel) {
			return false;
		}
		return true;
	}
}
