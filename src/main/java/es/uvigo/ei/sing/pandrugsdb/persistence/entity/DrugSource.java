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

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name = "drug_source")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
	"source", "source_drug_name", "standard_drug_name", "show_drug_name"
}))
public class DrugSource {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "source", length = 255)
	private String source;
	
	@Column(name = "source_drug_name", length = 1000)
	private String sourceDrugName;
	
	@Column(name = "standard_drug_name", length = 1000)
	private String standardDrugName;

	@Column(name = "show_drug_name", length = 1000)
	private String showDrugName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source", referencedColumnName = "source", insertable = false, updatable = false)
	private SourceInformation sourceInformation;
	
	DrugSource() {}
	
	DrugSource(
		int id,
		String source,
		String sourceDrugName,
		String standardDrugName,
		String showDrugName,
		SourceInformation sourceInformation
	) {
		this.id = id;
		this.source = source;
		this.sourceDrugName = sourceDrugName;
		this.standardDrugName = standardDrugName;
		this.showDrugName = showDrugName;
		this.sourceInformation = sourceInformation;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getSourceDrugName() {
		return sourceDrugName;
	}

	public String getStandardDrugName() {
		return standardDrugName;
	}
	
	public String getShowDrugName() {
		return showDrugName;
	}
	
	public SourceInformation getSourceInformation() {
		return sourceInformation;
	}
	
	public String getDrugURL(String genes) {
		return Optional.ofNullable(this.sourceInformation)
			.map(SourceInformation::getUrlTemplate)
			.map(template -> template.replaceAll("\\[GENES\\]", genes))
			.map(template -> template.replaceAll("\\[DRUG\\]", this.standardDrugName))
		.orElse(null);
	}
}
