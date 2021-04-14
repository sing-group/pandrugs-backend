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

import static es.uvigo.ei.sing.pandrugs.util.Checks.requireStringSize;
import static java.util.Objects.requireNonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity(name = "principal_splice_isoform")
public class PrincipalSpliceIsoform {
	@Id
	@Column(name = "transcript_id", length = 15, columnDefinition = "CHAR(15)")
	private String transcriptId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "isoform_type", length = 9, nullable = false)
	private IsoformType isoformType;

	PrincipalSpliceIsoform() {}
	
	public PrincipalSpliceIsoform(String transcriptId, IsoformType isoformType) {
		this.setTranscriptId(transcriptId);
		this.isoformType = isoformType;
	}
	
	public String getTranscriptId() {
		return transcriptId;
	}
	
	public void setTranscriptId(String transcriptId) {
		this.transcriptId = requireStringSize(transcriptId, 15);
	}

	public IsoformType getIsoformType() {
		return isoformType;
	}

	public void setIsoformType(IsoformType isoformType) {
		this.isoformType = requireNonNull(isoformType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isoformType == null) ? 0 : isoformType.hashCode());
		result = prime * result + ((transcriptId == null) ? 0 : transcriptId.hashCode());
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
		PrincipalSpliceIsoform other = (PrincipalSpliceIsoform) obj;
		if (isoformType != other.isoformType)
			return false;
		if (transcriptId == null) {
			if (other.transcriptId != null)
				return false;
		} else if (!transcriptId.equals(other.transcriptId))
			return false;
		return true;
	}
	
}
