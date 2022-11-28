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

import static es.uvigo.ei.sing.pandrugs.util.CompareCollections.equalsIgnoreOrder;
import static es.uvigo.ei.sing.pandrugs.util.StringFormatter.newStringFormatter;
import static java.util.Arrays.sort;
import static java.util.Arrays.stream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.controller.entity.CalculatedGeneAnnotations;
import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.GermLineAnnotation;
import es.uvigo.ei.sing.pandrugs.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Extra;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.service.CalculatedGeneAnnotation;
import es.uvigo.ei.sing.pandrugs.util.Compare;
import es.uvigo.ei.sing.pandrugs.util.StringJoiner;

@XmlRootElement(name = "geneDrugInfo", namespace = "https://www.pandrugs.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneDrugGroupInfo {
	@XmlElementWrapper(name = "genes")
	@XmlElement(name = "gene")
	private GeneInfo[] genes;
	
	private String standardDrugName;
	private String showDrugName;
	
	@XmlElementWrapper(name = "families")
	@XmlElement(name = "family")
	private String[] families;
	
	@XmlElementWrapper(name = "sources")
	@XmlElement(name = "source")
	private SourceAndLink[] sourceLinks;
	
	@XmlElementWrapper(name = "curatedSources")
	@XmlElement(name = "curatedSource")
	private String[] curatedSources;
	
	private DrugStatus status;
	private String statusDescription;
	
	@XmlElementWrapper(name = "cancers")
	@XmlElement(name = "cancer")
	private CancerType[] cancers;
	
	private Extra therapy;
	
	@XmlElementWrapper(name = "indirectGenes")
	@XmlElement(name = "indirectGene")
	private GeneInfo[] indirect;
	
	private boolean target;
	private int[] pubchemId;
	private double dScore;
	private double gScore;

	private GermLineAnnotation pharmCatGermLineAnnotation;
	private CalculatedGeneAnnotation calculatedGeneAnnotations;

	@XmlElementWrapper(name = "geneDrugInfos")
	@XmlElement(name = "geneDrugInfo")
	private GeneDrugInfo[] geneDrugs;
	
	GeneDrugGroupInfo() {}
	
	public GeneDrugGroupInfo(GeneDrugGroup gdg) {
		this.genes = stream(gdg.getQueryGenes())
			.map(GeneInfo::new)
		.toArray(GeneInfo[]::new);
		sort(this.genes);
		
		this.standardDrugName = gdg.getStandardDrugName();
		this.showDrugName = gdg.getShowDrugName();
		this.families = gdg.getFamilies();
		sort(this.families);
		
		final SortedMap<String, String> sourceShortNames = 
			gdg.getSourceShortNames();
		this.sourceLinks = gdg.getSourceLinks().entrySet().stream()
			.map(e -> new SourceAndLink(e.getKey(), sourceShortNames.get(e.getKey()), e.getValue()))
			.sorted()
		.toArray(SourceAndLink[]::new);
		this.curatedSources = gdg.getCuratedSourceNames();
		sort(this.curatedSources);
		this.status = gdg.getStatus();
		this.cancers = gdg.getCancers();
		sort(this.cancers);
		this.therapy = gdg.getExtra();
		this.indirect = stream(gdg.getIndirectGenes())
			.map(GeneInfo::new)
		.toArray(GeneInfo[]::new);
		sort(this.indirect);
		this.target = gdg.isTarget();
		this.pubchemId = gdg.getPubchemId();
		sort(this.pubchemId);
		this.dScore = gdg.getDScore();
		this.gScore = gdg.getGScore();
		this.pharmCatGermLineAnnotation = gdg.getPharmCatGermLineAnnotation();
		this.calculatedGeneAnnotations = CalculatedGeneAnnotation.from(gdg.getCalculatedGeneAnnotations());
		
		switch (this.status) {
		case APPROVED:
			if (this.cancers.length == 0) {
				this.statusDescription = this.status.getTitle();
			} else if (this.cancers[0] == CancerType.CLINICAL_CANCER) {
				this.statusDescription = "Cancer Clinical Trials and approved for other pathologies";
			} else {
				this.statusDescription = String.format("%s for %s cancer",
					this.status.getTitle(), formatCancerList(this.cancers)
				);
			}
			
			break;
		default:
			this.statusDescription = this.status.getTitle();
		}
		
		this.geneDrugs = createGeneDrugInfos(gdg);
	}

	private static String formatCancerList(final CancerType[] cancers) {
		final String[] cancerNames = stream(cancers)
			.map(CancerType::name)
		.toArray(String[]::new);
		
		return StringJoiner.join(cancerNames)
			.withSeparator(", ")
			.withLastSeparator(" and ")
			.withFormatter(
				newStringFormatter()
					.replaceAll("_", " ")
					.toLowerCase()
				.build()
			)
		.andGet();
	}

	private static GeneDrugInfo[] createGeneDrugInfos(GeneDrugGroup gdg) {
		final Stream<GeneDrugInfo> directAndIndirectGDIs = gdg.getGeneDrugs().stream()
			.map(gd -> new GeneDrugInfo(gd, gdg));
		
		final Stream<GeneDrugInfo> directAsIndirectGDIs = gdg.getGeneDrugs().stream()
			.filter(gdg::isDirectAndIndirect)
			.filter(GeneDrug::isTarget)
		.map(gd -> new GeneDrugInfo(gd, gdg, true));
		
		return Stream.concat(directAndIndirectGDIs, directAsIndirectGDIs)
			.sorted((g1, g2) -> Compare.objects(g1, g2)
				.byReverseOrderOf(GeneDrugInfo::getDScore)
					.thenByReverseOrderOf(GeneDrugInfo::getGScore)
					.thenBy(GeneDrugInfo::getDrug)
					.thenBy(GeneDrugInfo::getStatus)
					.thenBy(GeneDrugInfo::getTarget)
					.thenByArray(GeneDrugInfo::getGenes)
					.thenBy(GeneDrugInfo::getIndirect)
					.thenByArray(GeneDrugInfo::getCancers)
					.thenByArray(GeneDrugInfo::getFamilies)
					.thenBy(GeneDrugInfo::getTherapy)
					.thenByArray(GeneDrugInfo::getSources)
					.thenBy(GeneDrugInfo::getDrugStatusInfo)
				.andGet()
			)
		.toArray(GeneDrugInfo[]::new);
	}

	public GeneInfo[] getGenes() {
		return genes;
	}

	public void setGenes(GeneInfo[] gene) {
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
	
	public String getStatusDescription() {
		return statusDescription;
	}
	
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public CancerType[] getCancers() {
		return cancers;
	}

	public void setCancers(CancerType[] cancer) {
		this.cancers = cancer;
	}

	public GeneInfo[] getIndirect() {
		return indirect;
	}

	public void setIndirect(GeneInfo[] indirect) {
		this.indirect = indirect;
	}

	public boolean isTarget() {
		return target;
	}

	public void setTarget(boolean target) {
		this.target = target;
	}
	
	public int[] getPubchemId() {
		return pubchemId;
	}
	
	public void setPubchemId(int[] pubchemId) {
		this.pubchemId = pubchemId;
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
		result = prime * result + Arrays.hashCode(families);
		temp = Double.doubleToLongBits(gScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(geneDrugs);
		result = prime * result + Arrays.hashCode(genes);
		result = prime * result + Arrays.hashCode(indirect);
		result = prime * result + Arrays.hashCode(pubchemId);
		result = prime * result + ((showDrugName == null) ? 0 : showDrugName.hashCode());
		result = prime * result + Arrays.hashCode(sourceLinks);
		result = prime * result + ((standardDrugName == null) ? 0 : standardDrugName.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + (target ? 1231 : 1237);
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
		GeneDrugGroupInfo other = (GeneDrugGroupInfo) obj;
		if (!equalsIgnoreOrder(cancers, other.cancers))
			return false;
		if (!Arrays.equals(curatedSources, other.curatedSources))
			return false;
		if (Double.doubleToLongBits(dScore) != Double.doubleToLongBits(other.dScore))
			return false;
		if (!Arrays.equals(families, other.families))
			return false;
		if (Double.doubleToLongBits(gScore) != Double.doubleToLongBits(other.gScore))
			return false;
		if (!equalsIgnoreOrder(geneDrugs, other.geneDrugs))
			return false;
		if (!Arrays.equals(genes, other.genes))
			return false;
		if (!Arrays.equals(indirect, other.indirect))
			return false;
		if (!Arrays.equals(pubchemId, other.pubchemId))
			return false;
		if (showDrugName == null) {
			if (other.showDrugName != null)
				return false;
		} else if (!showDrugName.equals(other.showDrugName))
			return false;
		if (!Arrays.equals(sourceLinks, other.sourceLinks))
			return false;
		if (standardDrugName == null) {
			if (other.standardDrugName != null)
				return false;
		} else if (!standardDrugName.equals(other.standardDrugName))
			return false;
		if (status != other.status)
			return false;
		if (target != other.target)
			return false;
		if (therapy != other.therapy)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append("GeneDrugGroupInfo [genes=")
			.append(Arrays.toString(genes))
			.append(", standardDrugName=")
			.append(standardDrugName)
			.append(", statusDescription=")
			.append(statusDescription)
			.append(", dScore=")
			.append(dScore)
			.append(", gScore=")
			.append(gScore)
			.append("]")
		.toString();
	}
}
