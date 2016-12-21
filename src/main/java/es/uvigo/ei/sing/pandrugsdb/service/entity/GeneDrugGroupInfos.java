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
package es.uvigo.ei.sing.pandrugsdb.service.entity;

import static es.uvigo.ei.sing.pandrugsdb.util.CompareCollections.equalsIgnoreOrder;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.util.Compare;


@XmlRootElement(name = "geneDrugGroups", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneDrugGroupInfos {
	@NotNull
	@XmlElement(name = "geneDrugGroup")
	private List<GeneDrugGroupInfo> geneDrugs;
	
	GeneDrugGroupInfos() {}

	public GeneDrugGroupInfos(List<GeneDrugGroup> geneDrugs) {
		this.geneDrugs = geneDrugs.stream()
			.sorted((g1, g2) -> Compare.objects(g1, g2)
				.byReverseOrderOfDouble(GeneDrugGroup::getDScore)
					.thenByReverseOrderOf(GeneDrugGroup::getGScore)
					.thenBy(GeneDrugGroup::getShowDrugName)
					.thenBy(GeneDrugGroup::getStandardDrugName)
					.thenByReverseOrderOfInt(GeneDrugGroup::countTargetGenes)
					.thenByReverseOrderOfInt(GeneDrugGroup::countDirectGenes)
					.thenByReverseOrderOfInt(GeneDrugGroup::countIndirectGenes)
					.thenByArray(GeneDrugGroup::getFamilies)
					.thenBy(GeneDrugGroup::getStatus)
					.thenByArray(GeneDrugGroup::getCancers)
					.thenBy(GeneDrugGroup::getExtra)
					.thenBy(GeneDrugGroup::isTarget)
				.andGet())
			.map(GeneDrugGroupInfo::new)
		.collect(toList());
	}

	public List<GeneDrugGroupInfo> getGeneDrugs() {
		return unmodifiableList(geneDrugs);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((geneDrugs == null) ? 0 : geneDrugs.hashCode());
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
		GeneDrugGroupInfos other = (GeneDrugGroupInfos) obj;
		if (geneDrugs == null) {
			if (other.geneDrugs != null) {
				return false;
			}
		} else if (!equalsIgnoreOrder(geneDrugs, other.geneDrugs)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append("GeneDrugGroupInfos [geneDrugs=")
			.append(geneDrugs)
			.append("]")
		.toString();
	}
	
	
}
