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

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "drug_source")
@IdClass(DrugSourceId.class)
public class DrugSource implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "source", length = 50, columnDefinition = "VARCHAR(50)")
	private String source;
	
	@Id
	@Column(name = "source_drug_name", length = 500, columnDefinition = "VARCHAR(500)")
	private String sourceDrugName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source", referencedColumnName = "source",
		insertable = false, updatable = false)
	private SourceInformation sourceInformation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "drug_id", referencedColumnName = "id")
	private Drug drug;
	
	DrugSource() {}
	
	DrugSource(
		String source,
		String sourceDrugName,
		Drug drug,
		SourceInformation sourceInformation
	) {
		this.source = source;
		this.sourceDrugName = sourceDrugName;
		this.drug = drug;
		this.sourceInformation = sourceInformation;
	}
	
	public String getSource() {
		return this.source;
	}
	
	public String getSourceDrugName() {
		return this.sourceDrugName;
	}
	
	public Drug getDrug() {
		return drug;
	}
	
	public SourceInformation getSourceInformation() {
		return sourceInformation;
	}
	
	public boolean isCurated() {
		return this.getSourceInformation().isCurated();
	}
	
	public String getDrugURL(String ... genes) {
		final String geneNames = String.join(",", genes);
		
		return Optional.ofNullable(this.sourceInformation)
			.map(SourceInformation::getUrlTemplate)
			.map(template -> template.replaceAll("\\[GENES\\]", geneNames))
			.map(template -> template.replaceAll("\\[DRUG\\]", this.getDrug().getStandardName()))
		.orElse("");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((drug == null) ? 0 : drug.hashCode());
		result = prime * result + ((sourceDrugName == null) ? 0 : sourceDrugName.hashCode());
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
		DrugSource other = (DrugSource) obj;
		if (drug == null) {
			if (other.drug != null)
				return false;
		} else if (!drug.equals(other.drug))
			return false;
		if (sourceDrugName == null) {
			if (other.sourceDrugName != null)
				return false;
		} else if (!sourceDrugName.equals(other.sourceDrugName))
			return false;
		return true;
	}
}
