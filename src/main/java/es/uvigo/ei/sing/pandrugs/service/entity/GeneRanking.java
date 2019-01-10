/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

import static es.uvigo.ei.sing.pandrugs.util.ConversionUtils.toLinkedHashMap;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "geneRanking", namespace = "https://www.pandrugs.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneRanking {
	@NotNull
	@XmlElement(name = "geneRank")
	private List<GeneRank> geneRank;
	
	GeneRanking() {}

	public GeneRanking(Map<String, Double> ranking) {
		this.geneRank = ranking.entrySet().stream()
			.sorted((e1, e2) -> Double.compare(e1.getValue(), e2.getValue()))
			.map(entry -> new GeneRank(entry.getKey(), entry.getValue()))
		.collect(toList());
	}
	
	public List<GeneRank> getGeneRank() {
		return geneRank;
	}
	
	public Map<String, Double> asMap() {
		return this.geneRank.stream()
			.collect(toLinkedHashMap(GeneRank::getGene, GeneRank::getRank));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geneRank == null) ? 0 : geneRank.hashCode());
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
		GeneRanking other = (GeneRanking) obj;
		if (geneRank == null) {
			if (other.geneRank != null)
				return false;
		} else if (!geneRank.equals(other.geneRank))
			return false;
		return true;
	}
}
