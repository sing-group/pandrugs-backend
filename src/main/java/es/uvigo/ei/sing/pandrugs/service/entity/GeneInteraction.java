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

import static java.util.stream.Collectors.groupingBy;

import java.util.Set;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;

@XmlRootElement(name = "geneInteraction", namespace = "https://www.pandrugs.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneInteraction {
	private String geneSymbol;
	
	@XmlElementWrapper(name = "geneInteractions")
	@XmlElement(name = "geneInteraction")
	private String[] geneInteractions;
	
	@XmlElementWrapper(name = "drugInteractions")
	@XmlElement(name = "drugInteraction")
	private DrugInteraction[] drugInteractions;
	
	GeneInteraction() {}
	
	public GeneInteraction(Gene gene) {
		this.geneSymbol = gene.getGeneSymbol();
		
		this.geneInteractions = gene.getInteractingGenes().stream()
			.map(Gene::getGeneSymbol)
		.toArray(String[]::new);
		
		final Stream<DrugInteraction> directInteractions = gene.getGeneDrugs().stream()
			.map(DrugInteraction::new);
		
		final Stream<DrugInteraction> indirectInteractions = gene.getInteractingGenes().stream()
			.map(Gene::getGeneDrugs)
			.flatMap(Set::stream)
			.collect(groupingBy(DrugInteraction::new))
			.entrySet().stream()
			.map(entry -> {
				entry.getKey().setIndirect(entry.getValue().stream()
					.map(GeneDrug::getGeneSymbol)
				.toArray(String[]::new));
				
				return entry.getKey();
			});
		
		this.drugInteractions = Stream.concat(directInteractions, indirectInteractions)
			.toArray(DrugInteraction[]::new);
	}
	
	public String[] getGeneInteractions() {
		return geneInteractions;
	}
	
	public void setGeneInteractions(String[] interactions) {
		this.geneInteractions = interactions;
	}
	
	public String getGeneSymbol() {
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}
	
	public DrugInteraction[] getDrugInteractions() {
		return drugInteractions;
	}
	
	public void setDrugInteractions(DrugInteraction[] drugInteractions) {
		this.drugInteractions = drugInteractions;
	}
}
