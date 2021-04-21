/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "alterationInfo", namespace = "https://www.pandrugs.org")
public class AlterationInfo {
	private String gene;
	private String alteration;
	private String drugSource;
	
	AlterationInfo() {}

	public AlterationInfo(String gene, String alteration, String drugSource) {
		this.gene = gene;
		this.alteration = alteration;
		this.drugSource = drugSource;
	}
	
	public String getGene() {
		return gene;
	}

	public String getAlteration() {
		return alteration;
	}

	public String getDrugSource() {
		return drugSource;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alteration, drugSource, gene);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlterationInfo other = (AlterationInfo) obj;
		return Objects.equals(alteration, other.alteration) && Objects.equals(drugSource, other.drugSource) && Objects.equals(gene, other.gene);
	}

}
