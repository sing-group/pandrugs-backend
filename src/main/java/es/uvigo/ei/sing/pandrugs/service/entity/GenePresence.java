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

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "genePresence", namespace = "https://www.pandrugs.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenePresence {
	@XmlElement(name = "present")
	private String[] presentGenes;
	
	@XmlElement(name = "absent")
	private String[] absentGenes;
	
	public GenePresence(Map<String, Boolean> genePresence) {
		final Predicate<Entry<String, Boolean>> presencePredicate = Map.Entry::getValue;
		final Predicate<Entry<String, Boolean>> absencePredicate = presencePredicate.negate();
		
		this.presentGenes = genePresence.entrySet().stream()
			.filter(presencePredicate)
			.map(Map.Entry::getKey)
		.toArray(String[]::new);

		this.absentGenes = genePresence.entrySet().stream()
			.filter(absencePredicate)
			.map(Map.Entry::getKey)
		.toArray(String[]::new);
	}
	
	public String[] getPresentGenes() {
		return presentGenes;
	}
	
	public String[] getAbsentGenes() {
		return absentGenes;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(absentGenes);
		result = prime * result + Arrays.hashCode(presentGenes);
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
		GenePresence other = (GenePresence) obj;
		return Arrays.equals(absentGenes, other.absentGenes) && Arrays.equals(presentGenes, other.presentGenes);
	}

	@Override
	public String toString() {
		return "GenePresence [presentGenes=" + Arrays.toString(presentGenes) + ", absentGenes=" + Arrays.toString(absentGenes) + "]";
	}
}
