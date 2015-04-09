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

import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugSource;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;

@XmlRootElement(name = "gene-drug-basic-info", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneDrugBasicInfo {
	@XmlAttribute(required = true)
	private String gene;
	@XmlAttribute(required = true)
	private String drug;
	private String family;
	private String status;
	private String cancer;
	private String therapy;
	private String indirect;
	private String target;
	@XmlAttribute(required = true)
	private double score;
	private String sources;

	GeneDrugBasicInfo() {
	}

	public GeneDrugBasicInfo(GeneDrug geneDrug) {
		this.gene = geneDrug.getGeneSymbol();
		this.drug = geneDrug.getStandardDrugName();
		this.sources = geneDrug.getDrugSources().stream()
			.map(DrugSource::getSource)
			.distinct()
		.collect(Collectors.joining(", "));
		this.family = geneDrug.getFamily();
		this.status = geneDrug.getStatus().toString();
		this.cancer = geneDrug.getCancer();
		this.therapy = geneDrug.getExtra();
		this.indirect = "-";
		this.target = geneDrug.isTarget() ? "target" : "marker";
		this.score = geneDrug.getScore();
	}
	
	public String getGene() {
		return gene;
	}
	
	public String getSource() {
		return sources;
	}
	
	public String getDrug() {
		return drug;
	}
	
	public String getFamily() {
		return family;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getCancer() {
		return cancer;
	}
	
	public String getTherapy() {
		return therapy;
	}
	
	public String getIndirect() {
		return indirect;
	}
	
	public String getTarget() {
		return target;
	}
	
	public double getScore() {
		return score;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cancer == null) ? 0 : cancer.hashCode());
		result = prime * result + ((drug == null) ? 0 : drug.hashCode());
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result + ((gene == null) ? 0 : gene.hashCode());
		result = prime * result
				+ ((indirect == null) ? 0 : indirect.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((sources == null) ? 0 : sources.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((therapy == null) ? 0 : therapy.hashCode());
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
		GeneDrugBasicInfo other = (GeneDrugBasicInfo) obj;
		if (cancer == null) {
			if (other.cancer != null)
				return false;
		} else if (!cancer.equals(other.cancer))
			return false;
		if (drug == null) {
			if (other.drug != null)
				return false;
		} else if (!drug.equals(other.drug))
			return false;
		if (family == null) {
			if (other.family != null)
				return false;
		} else if (!family.equals(other.family))
			return false;
		if (gene == null) {
			if (other.gene != null)
				return false;
		} else if (!gene.equals(other.gene))
			return false;
		if (indirect == null) {
			if (other.indirect != null)
				return false;
		} else if (!indirect.equals(other.indirect))
			return false;
		if (Double.doubleToLongBits(score) != Double
				.doubleToLongBits(other.score))
			return false;
		if (sources == null) {
			if (other.sources != null)
				return false;
		} else if (!sources.equals(other.sources))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (therapy == null) {
			if (other.therapy != null)
				return false;
		} else if (!therapy.equals(other.therapy))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GeneDrugBasicInfo [gene=" + gene + ", drug=" + drug + "]";
	}
}
