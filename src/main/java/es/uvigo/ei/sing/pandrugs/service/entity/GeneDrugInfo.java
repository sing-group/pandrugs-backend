/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.service.entity;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Extra;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugWarning;
import es.uvigo.ei.sing.pandrugs.persistence.entity.ResistanceType;

@XmlRootElement(name = "geneDrugInfo", namespace = "http://sing.ei.uvigo.es/pandrugs")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneDrugInfo {
	private String drug;
	private String showDrugName;
	
	@XmlElementWrapper(name = "genes")
	@XmlElement(name = "gene")
	private GeneInfo[] genes;

	private String target;
	private String alteration;
	private DrugStatus status;
	private String drugStatusInfo;
	private Extra therapy;
	private IndirectGeneInfo indirect;
	
	private ResistanceType sensitivity;

	@XmlElementWrapper(name = "cancers")
	@XmlElement(name = "cancer")
	private CancerType[] cancers;
	
	@XmlElementWrapper(name = "sources")
	@XmlElement(name = "source")
	private String[] sources;

	@XmlElementWrapper(name = "families")
	@XmlElement(name = "family")
	private String[] families;

	@XmlElementWrapper(name = "warnings")
	@XmlElement(name = "warning")
	private String[] warnings;
	
	private double dScore;
	private double gScore;
	
	GeneDrugInfo() {}

	public GeneDrugInfo(GeneDrug geneDrug, GeneDrugGroup group) {
		this(geneDrug, group, false);
	}

	public GeneDrugInfo(GeneDrug geneDrug, GeneDrugGroup group, boolean forceIndirect) {
		final Gene[] queryGenes = group.getQueryGenesForGeneDrug(geneDrug, forceIndirect);
		final Set<String> queryGeneSymbols = stream(group.getQueryGeneSymbolsForGeneDrug(geneDrug, forceIndirect))
			.collect(toSet());
		
		this.drug = geneDrug.getStandardDrugName();
		this.showDrugName = geneDrug.getShowDrugName();
		
		this.genes = stream(queryGenes)
			.map(GeneInfo::new)
		.toArray(GeneInfo[]::new);
		
		this.target = geneDrug.isTarget() ? "target" : "marker";
		this.alteration = geneDrug.getAlteration();
		this.status = geneDrug.getStatus();
		this.therapy = geneDrug.getExtra();
		this.indirect = Optional.ofNullable(group.getIndirectGene(geneDrug, forceIndirect))
			.map(indirectGene -> new IndirectGeneInfo(geneDrug.getGeneSymbol(), indirectGene, genes))
		.orElse(null);
		
		this.sensitivity = geneDrug.getResistance();
		
		this.cancers = geneDrug.getCancers();
		this.sources = geneDrug.getDrugSourceNames().stream().toArray(String[]::new);
		this.families = geneDrug.getDrug().getFamilies();

		final Stream<String> indirectResistances;
		if (group.hasIndirectResistances(geneDrug)) {
			indirectResistances = stream(queryGenes)
				.map(Gene::getGeneSymbol)
				.filter(group::hasIndirectResistance)
				.flatMap(affectedGene -> group.getIndirectResistance(affectedGene).stream()
					.map(resistance -> String.format("%s amplifications induce %s resistance", resistance, affectedGene))
				);
		} else {
			indirectResistances = Stream.empty();
		}
		
		final Stream<String> drugWarnings = group.getDrugWarnings(queryGeneSymbols).stream()
			.map(GeneDrugWarning::getWarning);
		
		this.warnings = Stream.concat(indirectResistances, drugWarnings)
			.toArray(String[]::new);
		
		if (geneDrug.isTarget()) {
			if (!forceIndirect && group.isDirect(geneDrug)) {
				this.gScore = group.getGScore(geneDrug);
				this.drugStatusInfo = String.format(
					"%s is a drug %s that acts as an inhibitor of %s",
					this.showDrugName, geneDrug.getStatus().getDescription(), this.genes[0].getGeneSymbol()
				);
			} else if (group.isIndirect(geneDrug)) {
				final Map<String, Double> indirectGScores =
					group.getIndirectGScores(geneDrug);
				
				this.gScore = stream(this.genes)
					.map(GeneInfo::getGeneSymbol)
					.mapToDouble(indirectGScores::get)
				.max().orElse(0d);
				
				this.drugStatusInfo = String.format(
					"%s is a drug %s that acts as an inhibitor of %s, a protein downstream to %s",
					this.showDrugName, geneDrug.getStatus().getDescription(),
					this.indirect.getGeneInfo().getGeneSymbol(), joinGeneNames(this.genes)
				);
			} else {
				throw new IllegalArgumentException(geneDrug.getGeneSymbol() + " is not indirect but indirect mode was forced.");
			}
		} else if (group.isDirect(geneDrug)) {
			this.gScore = group.getGScore(geneDrug);
			
			this.drugStatusInfo = String.format(
				"Molecular alterations in %s are associated with response to %s, a drug %s",
				this.genes[0].getGeneSymbol(), this.showDrugName, geneDrug.getStatus().getDescription() 
			);
		} else {
			throw new IllegalArgumentException(geneDrug.getGeneSymbol() + " is indirect and marker.");
		}
		
		this.dScore = group.getDScore(geneDrug);
	}
	
	public GeneInfo[] getGenes() {
		return genes;
	}
	
	public String[] getSources() {
		return sources;
	}
	
	public String getDrug() {
		return drug;
	}
	
	public String getShowDrugName() {
		return showDrugName;
	}
	
	public String[] getFamilies() {
		return families;
	}
	
	public DrugStatus getStatus() {
		return status;
	}
	
	public CancerType[] getCancers() {
		return cancers;
	}
	
	public Extra getTherapy() {
		return therapy;
	}
	
	public IndirectGeneInfo getIndirect() {
		return indirect;
	}
	
	public String getTarget() {
		return target;
	}
	
	public ResistanceType getSensitivity() {
		return sensitivity;
	}
	
	public String getAlteration() {
		return alteration;
	}
	
	public String getDrugStatusInfo() {
		return drugStatusInfo;
	}
	
	public String[] getWarnings() {
		return warnings;
	}
	
	public double getDScore() {
		return dScore;
	}
	
	public double getGScore() {
		return gScore;
	}
	
	private final static String joinGeneNames(GeneInfo ... genes) {
		if (genes.length == 1) {
			return genes[0].getGeneSymbol();
		} else if (genes.length == 2) {
			return stream(genes)
				.map(GeneInfo::getGeneSymbol)
			.collect(joining(" and "));
		} else {
			final GeneInfo[] subGeneNames = new GeneInfo[genes.length - 1];
			System.arraycopy(genes, 0, subGeneNames, 0, subGeneNames.length);
			final GeneInfo lastGene = genes[genes.length - 1];
			
			final String csvNames = stream(subGeneNames)
				.map(GeneInfo::getGeneSymbol)
			.collect(joining(", "));
			
			return csvNames + " and " + lastGene.getGeneSymbol();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alteration == null) ? 0 : alteration.hashCode());
		result = prime * result + Arrays.hashCode(cancers);
		long temp;
		temp = Double.doubleToLongBits(dScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((drug == null) ? 0 : drug.hashCode());
		result = prime * result + ((drugStatusInfo == null) ? 0 : drugStatusInfo.hashCode());
		result = prime * result + Arrays.hashCode(families);
		temp = Double.doubleToLongBits(gScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(genes);
		result = prime * result + ((indirect == null) ? 0 : indirect.hashCode());
		result = prime * result + ((sensitivity == null) ? 0 : sensitivity.hashCode());
		result = prime * result + ((showDrugName == null) ? 0 : showDrugName.hashCode());
		result = prime * result + Arrays.hashCode(sources);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((therapy == null) ? 0 : therapy.hashCode());
		result = prime * result + Arrays.hashCode(warnings);
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
		GeneDrugInfo other = (GeneDrugInfo) obj;
		if (alteration == null) {
			if (other.alteration != null)
				return false;
		} else if (!alteration.equals(other.alteration))
			return false;
		if (!Arrays.equals(cancers, other.cancers))
			return false;
		if (Double.doubleToLongBits(dScore) != Double.doubleToLongBits(other.dScore))
			return false;
		if (drug == null) {
			if (other.drug != null)
				return false;
		} else if (!drug.equals(other.drug))
			return false;
		if (drugStatusInfo == null) {
			if (other.drugStatusInfo != null)
				return false;
		} else if (!drugStatusInfo.equals(other.drugStatusInfo))
			return false;
		if (!Arrays.equals(families, other.families))
			return false;
		if (Double.doubleToLongBits(gScore) != Double.doubleToLongBits(other.gScore))
			return false;
		if (!Arrays.equals(genes, other.genes))
			return false;
		if (indirect == null) {
			if (other.indirect != null)
				return false;
		} else if (!indirect.equals(other.indirect))
			return false;
		if (sensitivity != other.sensitivity)
			return false;
		if (showDrugName == null) {
			if (other.showDrugName != null)
				return false;
		} else if (!showDrugName.equals(other.showDrugName))
			return false;
		if (!Arrays.equals(sources, other.sources))
			return false;
		if (status != other.status)
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (therapy != other.therapy)
			return false;
		if (!Arrays.equals(warnings, other.warnings))
			return false;
		return true;
	}
}
