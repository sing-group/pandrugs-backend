/*
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2017 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
package es.uvigo.ei.sing.pandrugs.service.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Pathway;
import es.uvigo.ei.sing.pandrugs.util.Compare;

@XmlRootElement(name = "pathway", namespace = "http://sing.ei.uvigo.es/pandrugs")
@XmlAccessorType(XmlAccessType.FIELD)
public class PathwayInfo implements Comparable<PathwayInfo> {
	private String keggId;
	
	private String name;
	
	PathwayInfo() {}
	
	public PathwayInfo(Pathway pathway) {
		this.keggId = pathway.getId();
		this.name = pathway.getName();
	}

	public String getKeggId() {
		return keggId;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(PathwayInfo o) {
		return Compare.objects(this, o)
			.by(PathwayInfo::getName)
			.thenBy(PathwayInfo::getKeggId)
		.andGet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keggId == null) ? 0 : keggId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		PathwayInfo other = (PathwayInfo) obj;
		if (keggId == null) {
			if (other.keggId != null)
				return false;
		} else if (!keggId.equals(other.keggId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
