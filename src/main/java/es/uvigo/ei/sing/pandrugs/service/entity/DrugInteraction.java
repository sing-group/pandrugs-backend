/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.service.entity;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;

@XmlRootElement(name = "drugInteraction", namespace = "http://sing.ei.uvigo.es/pandrugs")
@XmlAccessorType(XmlAccessType.FIELD)
public class DrugInteraction {
	private String standardDrugName;
	private String showDrugName;
	private String target;
	
	@XmlElementWrapper(name = "indirectGenes")
	@XmlElement(name = "indirectGene")
	private String[] indirect;
	
	DrugInteraction() {}
	
	public DrugInteraction(GeneDrug geneDrug) {
		this.showDrugName = geneDrug.getShowDrugName();
		this.standardDrugName = geneDrug.getStandardDrugName();
		
		this.target = geneDrug.isTarget() ? "target" : "marker";
		
		this.indirect = null;
	}

	public String getShowDrugName() {
		return showDrugName;
	}

	public void setShowDrugName(String showDrugName) {
		this.showDrugName = showDrugName;
	}

	public String getStandardDrugName() {
		return standardDrugName;
	}

	public void setStandardDrugName(String standardDrugName) {
		this.standardDrugName = standardDrugName;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}

	public String[] getIndirect() {
		return indirect;
	}

	public void setIndirect(String[] indirect) {
		this.indirect = indirect;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(indirect);
		result = prime * result + ((showDrugName == null) ? 0 : showDrugName.hashCode());
		result = prime * result + ((standardDrugName == null) ? 0 : standardDrugName.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		DrugInteraction other = (DrugInteraction) obj;
		if (!Arrays.equals(indirect, other.indirect))
			return false;
		if (showDrugName == null) {
			if (other.showDrugName != null)
				return false;
		} else if (!showDrugName.equals(other.showDrugName))
			return false;
		if (standardDrugName == null) {
			if (other.standardDrugName != null)
				return false;
		} else if (!standardDrugName.equals(other.standardDrugName))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
}
