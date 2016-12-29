/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 - 2016 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
package es.uvigo.ei.sing.pandrugsdb.service.entity;

import static java.util.Arrays.stream;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Drug;

@XmlRootElement(name = "drugNames", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class DrugNames {
	public static DrugNames[] of(Drug ... drugs) {
		return stream(drugs)
			.map(DrugNames::new)
		.toArray(DrugNames[]::new);
	}
	
	private String standardName;
	
	private String showName;

	@XmlElementWrapper(name = "sourceNames")
	@XmlElement(name = "sourceName")
	private DrugSourceName[] sourceNames;
	
	DrugNames() {}

	public DrugNames(Drug drug) {
		this.standardName = drug.getStandardName();
		this.showName = drug.getShowName();
		
		this.sourceNames = drug.getCuratedDrugSources().stream()
			.map(DrugSourceName::new)
		.toArray(DrugSourceName[]::new);
		
	}

	public String getStandardName() {
		return standardName;
	}


	public String getShowName() {
		return showName;
	}


	public DrugSourceName[] getSourceNames() {
		return sourceNames;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((showName == null) ? 0 : showName.hashCode());
		result = prime * result + Arrays.hashCode(sourceNames);
		result = prime * result + ((standardName == null) ? 0 : standardName.hashCode());
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
		DrugNames other = (DrugNames) obj;
		if (showName == null) {
			if (other.showName != null)
				return false;
		} else if (!showName.equals(other.showName))
			return false;
		if (!Arrays.equals(sourceNames, other.sourceNames))
			return false;
		if (standardName == null) {
			if (other.standardName != null)
				return false;
		} else if (!standardName.equals(other.standardName))
			return false;
		return true;
	}
	
}
