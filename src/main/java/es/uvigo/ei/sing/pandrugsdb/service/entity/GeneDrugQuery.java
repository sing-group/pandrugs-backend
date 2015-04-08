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

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gene-drug-query", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneDrugQuery {
    @XmlElementWrapper(name = "genes", required = true, nillable = false)
    @XmlElement(name = "gene", required = true, nillable = false)
	private String[] genes;
	@XmlAttribute(name = "startPosition", required = false)
	private Integer startPosition;
	@XmlAttribute(name = "maxResults", required = false)
	private Integer maxResults;
	
	GeneDrugQuery() {}
	
	public GeneDrugQuery(String[] genes, Integer start, Integer length) {
		this.genes = genes;
		this.startPosition = start;
		this.maxResults = length;
	}
	
	public String[] getGenes() {
		return genes;
	}
	
	public void setGenes(String[] genes) {
		this.genes = genes;
	}
	
	public Optional<Integer> getStartPosition() {
		return Optional.of(this.startPosition);
	}
	
	public void setStartPosition(Integer start) {
		this.startPosition = start;
	}
	
	public Optional<Integer> getMaxResults() {
		return Optional.of(this.maxResults);
	}
	
	public void setMaxResults(Integer length) {
		this.maxResults = length;
	}
}
