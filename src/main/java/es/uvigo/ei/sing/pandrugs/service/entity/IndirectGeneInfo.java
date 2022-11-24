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

package es.uvigo.ei.sing.pandrugs.service.entity;

import static java.util.Arrays.stream;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugs.util.Compare;

@XmlRootElement(name = "indirectGene", namespace = "https://www.pandrugs.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class IndirectGeneInfo implements Comparable<IndirectGeneInfo> {
	private GeneInfo geneInfo;

	@XmlElementWrapper(name = "pathways")
	@XmlElement(name = "pathway")
	private PathwayInfo[] pathways;

	IndirectGeneInfo() {}

	public IndirectGeneInfo(String directGeneSymbol, Gene gene, GeneInfo[] queryGenes) {
		final String[] queryGeneSymbols = stream(queryGenes)
			.map(GeneInfo::getGeneSymbol)
		.toArray(String[]::new);
		
		this.geneInfo = new GeneInfo(gene);
		this.pathways = gene.getPathways().stream()
			.filter(pathway -> pathway.hasAnyGene(queryGeneSymbols))
			.map(pathway -> new PathwayInfo(pathway, queryGenes))
			.sorted()
		.toArray(PathwayInfo[]::new);
	}
	
	public GeneInfo getGeneInfo() {
		return geneInfo;
	}
	
	public PathwayInfo[] getPathways() {
		return pathways;
	}

	@Override
	public int compareTo(IndirectGeneInfo o) {
		return Compare.objects(this, o)
			.by(IndirectGeneInfo::getGeneInfo)
			.thenByArray(IndirectGeneInfo::getPathways)
		.andGet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geneInfo == null) ? 0 : geneInfo.hashCode());
		result = prime * result + Arrays.hashCode(pathways);
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
		IndirectGeneInfo other = (IndirectGeneInfo) obj;
		if (geneInfo == null) {
			if (other.geneInfo != null)
				return false;
		} else if (!geneInfo.equals(other.geneInfo))
			return false;
		if (!Arrays.equals(pathways, other.pathways))
			return false;
		return true;
	}
}
