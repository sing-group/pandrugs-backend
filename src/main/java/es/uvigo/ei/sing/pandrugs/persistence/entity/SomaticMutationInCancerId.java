/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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
public class SomaticMutationInCancerId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int sampleId;
	
	private String geneSymbol;
	
	private int mutationId;
	
	private String status;
	
	SomaticMutationInCancerId() {}

	public SomaticMutationInCancerId(int sampleId, String geneSymbol, int mutationId, String status) {
		this.sampleId = sampleId;
		this.geneSymbol = geneSymbol;
		this.mutationId = mutationId;
		this.status = status;
	}
	
	public static SomaticMutationInCancerId of(SomaticMutationInCancer smic) {
		return new SomaticMutationInCancerId(
			smic.getSampleId(), smic.getGeneSymbol(),
			smic.getMutationId(), smic.getStatus()
		);
	}

	public int getSampleId() {
		return sampleId;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public int getMutationId() {
		return mutationId;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + mutationId;
		result = prime * result + sampleId;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		SomaticMutationInCancerId other = (SomaticMutationInCancerId) obj;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (mutationId != other.mutationId)
			return false;
		if (sampleId != other.sampleId)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	
}
