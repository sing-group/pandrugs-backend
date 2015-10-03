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

import static es.uvigo.ei.sing.pandrugsdb.util.CompareCollections.equalsIgnoreOrder;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name = "drug")
@Table(
	uniqueConstraints = @UniqueConstraint(
		name = "unique_standard_name", columnNames = "standard_name"
	)
)
public class Drug {
	@Id
	@GeneratedValue
	private int id;
	
	@Column(name = "standard_name", length = 2000, columnDefinition = "VARCHAR(2000)", unique = true, nullable = false)
	private String standardName;
	
	@Column(name = "show_name", length = 2000, columnDefinition = "VARCHAR(2000)", unique = true, nullable = false)
	private String showName;
	
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private DrugStatus status;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "cancer", joinColumns = @JoinColumn(name = "drug_id"))
	@Column(name = "name", length = 15, nullable = false)
	@Enumerated(EnumType.STRING)
	private List<CancerType> cancer;
	
	@Column(name = "extra", nullable = true)
	@Enumerated(EnumType.STRING)
	private Extra extra;
	
	@Column(name = "extra_details", length = 1000, columnDefinition = "VARCHAR(1000)", nullable = true)
	private String extraDetails;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "pathology", joinColumns = @JoinColumn(name = "drug_id"))
	@Column(name = "name", length = 50, columnDefinition = "VARCHAR(50)", nullable = false)
	private List<String> pathologies;
	
	Drug() {}

	Drug(String standardName, String showName, DrugStatus status, List<CancerType> cancer,
			Extra extra, List<String> pathologies) {
		this.standardName = standardName;
		this.showName = showName;
		this.status = status;
		this.cancer = cancer == null ? emptyList() : cancer;
		this.extra = extra;
		this.pathologies = pathologies == null ? emptyList() : pathologies;
	}
	
	public int getId() {
		return id;
	}
	
	public String getStandardName() {
		return standardName;
	}
	
	public String getShowName() {
		return showName;
	}

	public List<CancerType> getCancer() {
		return cancer;
	}

	public Extra getExtra() {
		return extra;
	}

	public List<String> getPathologies() {
		return unmodifiableList(this.pathologies);
	}

	public DrugStatus getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cancer == null) ? 0 : cancer.hashCode());
		result = prime * result + ((extra == null) ? 0 : extra.hashCode());
		result = prime * result + ((extraDetails == null) ? 0 : extraDetails.hashCode());
		result = prime * result + ((pathologies == null) ? 0 : pathologies.hashCode());
		result = prime * result + ((showName == null) ? 0 : showName.hashCode());
		result = prime * result + ((standardName == null) ? 0 : standardName.hashCode());
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
		Drug other = (Drug) obj;
		if (cancer == null) {
			if (other.cancer != null)
				return false;
		} else if (!equalsIgnoreOrder(cancer, other.cancer))
			return false;
		if (extra != other.extra)
			return false;
		if (extraDetails == null) {
			if (other.extraDetails != null)
				return false;
		} else if (!extraDetails.equals(other.extraDetails))
			return false;
		if (pathologies == null) {
			if (other.pathologies != null)
				return false;
		} else if (!equalsIgnoreOrder(pathologies, other.pathologies))
			return false;
		if (showName == null) {
			if (other.showName != null)
				return false;
		} else if (!showName.equals(other.showName))
			return false;
		if (standardName == null) {
			if (other.standardName != null)
				return false;
		} else if (!standardName.equals(other.standardName))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
}
