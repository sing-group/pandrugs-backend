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
package es.uvigo.ei.sing.pandrugs.service.entity;

import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;

@XmlRootElement(name = "geneDependency", namespace = "https://www.pandrugs.org")
public class GeneDependencyInfo {
	private GeneInfo geneInfo;
	private Map<String, String> alterations;
	
	GeneDependencyInfo() {}

	public GeneDependencyInfo(Gene gene, Map<String, String> alterations) {
		this.geneInfo = new GeneInfo(gene);
		this.alterations = alterations;
	}
	
	public GeneInfo getGeneInfo() {
		return geneInfo;
	}

	public Map<String, String> getAlterations() {
		return alterations;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alterations, geneInfo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneDependencyInfo other = (GeneDependencyInfo) obj;
		return Objects.equals(alterations, other.alterations) && Objects.equals(geneInfo, other.geneInfo);
	}

}
