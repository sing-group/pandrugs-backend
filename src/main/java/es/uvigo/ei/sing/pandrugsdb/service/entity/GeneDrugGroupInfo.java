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

import static es.uvigo.ei.sing.pandrugsdb.util.CompareCollections.equalsIgnoreOrder;

import java.util.Arrays;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Extra;

@XmlRootElement(name = "gene-drug-info", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneDrugGroupInfo {
	@XmlElementWrapper(name = "genes")
	@XmlElement(name = "gene")
	private String[] genes;
	@XmlElement(name = "standard-drug-name")
	private String standardDrugName;
	@XmlElement(name = "show-drug-name")
	private String showDrugName;
	@XmlElementWrapper(name = "families")
	@XmlElement(name = "family")
	private String[] families;
	@XmlElementWrapper(name = "sources")
	@XmlElement(name = "source")
	private SourceAndLink[] sourceLinks;
	@XmlElementWrapper(name = "curated-sources")
	@XmlElement(name = "curated-source")
	private String[] curatedSources;
	private DrugStatus status;
	@XmlElementWrapper(name = "cancers")
	@XmlElement(name = "cancer")
	private CancerType[] cancers;
	private Extra therapy;
	@XmlElementWrapper(name = "indirect-genes")
	@XmlElement(name = "indirect-gene")
	private String[] indirect;
	private boolean target;
	private double dScore;
	private double gScore;

	@XmlElementWrapper(name = "gene-drug-infos")
	@XmlElement(name = "gene-drug-info")
	private GeneDrugInfo[] geneDrugs;
	
	GeneDrugGroupInfo() {
	}
	
	public GeneDrugGroupInfo(GeneDrugGroup gdg) {
		this.genes = gdg.getTargetGenes();
		this.standardDrugName = gdg.getStandardDrugName();
		this.showDrugName = gdg.getShowDrugName();
		this.families = gdg.getFamilies();
		final SortedMap<String, String> sourceShortNames = 
			gdg.getSourceShortNames();
		this.sourceLinks = gdg.getSourceLinks().entrySet().stream()
			.map(e -> new SourceAndLink(e.getKey(), sourceShortNames.get(e.getKey()), e.getValue()))
		.toArray(SourceAndLink[]::new);
		this.curatedSources = gdg.getCuratedSourceNames();
		this.status = gdg.getStatus();
		this.cancers = gdg.getCancers().stream().toArray(CancerType[]::new);
		
		this.therapy = gdg.getExtra();
		this.indirect = gdg.getIndirectGenes();
		this.target = gdg.isTarget();
		this.dScore = gdg.getDScore();
		this.gScore = gdg.getGScore();
		
		this.geneDrugs = gdg.getGeneDrugs().stream()
			.map(gd -> new GeneDrugInfo(gd, gdg))
		.toArray(GeneDrugInfo[]::new);
	}

	public String[] getGenes() {
		return genes;
	}

	public void setGenes(String[] gene) {
		this.genes = gene;
	}

	public String getStandardDrugName() {
		return standardDrugName;
	}

	public void setStandardDrugName(String standardDrugName) {
		this.standardDrugName = standardDrugName;
	}

	public String getShowDrugName() {
		return showDrugName;
	}

	public void setShowDrugName(String showDrugName) {
		this.showDrugName = showDrugName;
	}

	public String[] getFamilies() {
		return families;
	}

	public void setFamilies(String[] families) {
		this.families = families;
	}

	public SourceAndLink[] getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(SourceAndLink[] sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

	public String[] getCuratedSources() {
		return curatedSources;
	}

	public void setCuratedSources(String[] curatedSources) {
		this.curatedSources = curatedSources;
	}

	public DrugStatus getStatus() {
		return status;
	}

	public void setStatus(DrugStatus status) {
		this.status = status;
	}

	public CancerType[] getCancers() {
		return cancers;
	}

	public void setCancers(CancerType[] cancer) {
		this.cancers = cancer;
	}

	public String[] getIndirect() {
		return indirect;
	}

	public void setIndirect(String[] indirect) {
		this.indirect = indirect;
	}

	public boolean isTarget() {
		return target;
	}

	public void setTarget(boolean target) {
		this.target = target;
	}

	public double getDScore() {
		return dScore;
	}

	public void setDScore(double dScore) {
		this.dScore = dScore;
	}

	public double getGScore() {
		return gScore;
	}

	public void setGScore(double gScore) {
		this.gScore = gScore;
	}
	
	public Extra getTherapy() {
		return therapy;
	}

	public void setTherapy(Extra therapy) {
		this.therapy = therapy;
	}
	
	public GeneDrugInfo[] getGeneDrugs() {
		return geneDrugs;
	}

	public void setGeneDrugs(GeneDrugInfo[] geneDrugs) {
		this.geneDrugs = geneDrugs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(cancers);
		result = prime * result + Arrays.hashCode(curatedSources);
		long temp;
		temp = Double.doubleToLongBits(dScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((standardDrugName == null) ? 0 : standardDrugName.hashCode());
		result = prime * result + ((showDrugName == null) ? 0 : showDrugName.hashCode());
		result = prime * result + Arrays.hashCode(families);
		temp = Double.doubleToLongBits(gScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(geneDrugs);
		result = prime * result + Arrays.hashCode(genes);
		result = prime * result + Arrays.hashCode(indirect);
		result = prime * result + Arrays.hashCode(sourceLinks);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + (target ? 1231 : 1237);
		result = prime * result + ((therapy == null) ? 0 : therapy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GeneDrugGroupInfo other = (GeneDrugGroupInfo) obj;
		if (cancers == null) {
			if (other.cancers != null) {
				return false;
			}
		} else if (!equalsIgnoreOrder(cancers, other.cancers)) {
			return false;
		}
		if (!Arrays.equals(curatedSources, other.curatedSources)) {
			return false;
		}
		if (Double.doubleToLongBits(dScore) != Double
				.doubleToLongBits(other.dScore)) {
			return false;
		}
		if (standardDrugName == null) {
			if (other.standardDrugName != null) {
				return false;
			}
		} else if (!standardDrugName.equals(other.standardDrugName)) {
			return false;
		}
		if (showDrugName == null) {
			if (other.showDrugName != null) {
				return false;
			}
		} else if (!showDrugName.equals(other.showDrugName)) {
			return false;
		}
		if (!Arrays.equals(families, other.families)) {
			return false;
		}
		if (Double.doubleToLongBits(gScore) != Double
				.doubleToLongBits(other.gScore)) {
			return false;
		}
		if (!equalsIgnoreOrder(geneDrugs, other.geneDrugs)) {
			return false;
		}
		if (!Arrays.equals(genes, other.genes)) {
			return false;
		}
		if (!Arrays.equals(indirect, other.indirect)) {
			return false;
		}
		if (!Arrays.equals(sourceLinks, other.sourceLinks)) {
			return false;
		}
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!status.equals(other.status)) {
			return false;
		}
		if (target != other.target) {
			return false;
		}
		if (therapy == null) {
			if (other.therapy != null) {
				return false;
			}
		} else if (!therapy.equals(other.therapy)) {
			return false;
		}
		return true;
	}
}