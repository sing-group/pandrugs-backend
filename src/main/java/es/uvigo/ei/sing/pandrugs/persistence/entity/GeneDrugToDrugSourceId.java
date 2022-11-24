/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2022 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class GeneDrugToDrugSourceId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String geneSymbol;

	private int drugId;

	private boolean target;

	private String source;

	private String sourceDrugName;

	GeneDrugToDrugSourceId() {}

	public GeneDrugToDrugSourceId(String geneSymbol, int drugId, boolean target, String source, String sourceDrugName) {
		this.geneSymbol = geneSymbol;
		this.drugId = drugId;
		this.target = target;
		this.source = source;
		this.sourceDrugName = sourceDrugName;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public int getDrugId() {
		return drugId;
	}

	public boolean isTarget() {
		return target;
	}

	public String getSource() {
		return source;
	}

	public String getSourceDrugName() {
		return sourceDrugName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(drugId, geneSymbol, source, sourceDrugName, target);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneDrugToDrugSourceId other = (GeneDrugToDrugSourceId) obj;
		return drugId == other.drugId && Objects.equals(geneSymbol, other.geneSymbol) && Objects.equals(source, other.source)
			&& Objects.equals(sourceDrugName, other.sourceDrugName) && target == other.target;
	}
}
