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

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "genePresence", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
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
}