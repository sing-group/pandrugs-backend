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

import static java.util.Arrays.sort;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugs.util.Compare;

@XmlRootElement(name = "geneInfo", namespace = "http://sing.ei.uvigo.es/pandrugs")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneInfo implements Comparable<GeneInfo> {
	@XmlElement(name = "geneSymbol")
	private String geneSymbol;
	
	@XmlElementWrapper(name = "entrezIds")
	@XmlElement(name = "entrezId")
	private int[] entrezIds;
	
	GeneInfo() {}
	
	public GeneInfo(Gene gene) {
		this(
			gene.getGeneSymbol(),
			gene.getEntrezIds().stream().mapToInt(Integer::intValue).toArray()
		);
	}
	
	public GeneInfo(String geneSymbol, int[] entrezIds) {
		this.geneSymbol = geneSymbol;
		this.entrezIds = entrezIds;
		
		sort(this.entrezIds);
	}
	
	public String getGeneSymbol() {
		return geneSymbol;
	}
	
	public int[] getEntrezIds() {
		return entrezIds;
	}
	
	@Override
	public int compareTo(GeneInfo o) {
		return Compare.objects(this, o)
			.by(GeneInfo::getGeneSymbol)
			.thenByIntArray(GeneInfo::getEntrezIds)
		.andGet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(entrezIds);
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
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
		GeneInfo other = (GeneInfo) obj;
		if (!Arrays.equals(entrezIds, other.entrezIds))
			return false;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		return true;
	}
	
}
