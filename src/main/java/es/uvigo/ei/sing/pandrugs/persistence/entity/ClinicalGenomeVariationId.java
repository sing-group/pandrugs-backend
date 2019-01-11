/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2019 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ClinicalGenomeVariationId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String chromosome;
	
	private int start;
	
	private int end;
	
	private String hgvs;
	
	private String disease;
	
	private String accession;
	
	ClinicalGenomeVariationId() {}
	
	public ClinicalGenomeVariationId(String chromosome, int start, int end, String hgvs, String disease,
			String accession) {
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.hgvs = hgvs;
		this.disease = disease;
		this.accession = accession;
	}

	public static ClinicalGenomeVariationId of(ClinicalGenomeVariation cgv) {
		return new ClinicalGenomeVariationId(cgv.getChromosome(), cgv.getStart(), cgv.getEnd(), cgv.getHgvs(), cgv.getDisease(), cgv.getAccession());
	}

	public String getChromosome() {
		return chromosome;
	}

	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public String getHgvs() {
		return hgvs;
	}

	public String getDisease() {
		return disease;
	}
	
	public String getAccession() {
		return accession;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accession == null) ? 0 : accession.hashCode());
		result = prime * result + ((chromosome == null) ? 0 : chromosome.hashCode());
		result = prime * result + ((disease == null) ? 0 : disease.hashCode());
		result = prime * result + end;
		result = prime * result + ((hgvs == null) ? 0 : hgvs.hashCode());
		result = prime * result + start;
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
		ClinicalGenomeVariationId other = (ClinicalGenomeVariationId) obj;
		if (accession == null) {
			if (other.accession != null)
				return false;
		} else if (!accession.equals(other.accession))
			return false;
		if (chromosome == null) {
			if (other.chromosome != null)
				return false;
		} else if (!chromosome.equals(other.chromosome))
			return false;
		if (disease == null) {
			if (other.disease != null)
				return false;
		} else if (!disease.equals(other.disease))
			return false;
		if (end != other.end)
			return false;
		if (hgvs == null) {
			if (other.hgvs != null)
				return false;
		} else if (!hgvs.equals(other.hgvs))
			return false;
		if (start != other.start)
			return false;
		return true;
	}

}
