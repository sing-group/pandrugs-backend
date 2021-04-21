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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

@XmlRootElement(name = "geneDrugInfo", namespace = "https://www.pandrugs.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneDrugInfo {
	private String drug;
	private String showDrugName;
	
	@XmlElementWrapper(name = "genes")
	@XmlElement(name = "gene")
	private GeneInfo[] genes;

	private String target;
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
	
	@XmlElementWrapper(name = "alterations")
	@XmlElement(name = "alteration")
	private AlterationInfo[] alterations;

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
		
		this.drug = geneDrug.getStandardDrugName();
		this.showDrugName = geneDrug.getShowDrugName();
		
		this.genes = stream(queryGenes)
			.map(GeneInfo::new)
		.toArray(GeneInfo[]::new);
		
		this.target = geneDrug.isTarget() ? "target" : "marker";
		this.status = geneDrug.getStatus();
		this.therapy = geneDrug.getExtra();
		this.indirect = Optional.ofNullable(group.getIndirectGene(geneDrug, forceIndirect))
			.map(indirectGene -> new IndirectGeneInfo(geneDrug.getGeneSymbol(), indirectGene, genes))
		.orElse(null);
		
		this.sensitivity = geneDrug.getResistance();
		
		this.cancers = geneDrug.getCancers();
		this.sources = geneDrug.getDrugSourceNames().stream().toArray(String[]::new);
		this.families = geneDrug.getDrug().getFamilies();
		
		this.alterations = geneDrug.getDrugSources().stream()
			.filter(ds -> ds.getAlteration() != null)
			.map(ds -> new AlterationInfo(ds.getGeneDrug().getGeneSymbol(), ds.getAlteration(), ds.getDrugSource().getSource()))
			.distinct()
		.toArray(AlterationInfo[]::new);

		final Stream<String> geneDrugWarnings = group.hasWarning(geneDrug, forceIndirect)
			? group.getWarning(geneDrug, forceIndirect).stream().map(GeneDrugWarning::getWarning)
			: Stream.empty();
		
		this.warnings = geneDrugWarnings.toArray(String[]::new);
		
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
	
	public String getDrugStatusInfo() {
		return drugStatusInfo;
	}
	
	public AlterationInfo[] getAlterations() {
		return alterations;
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
		result = prime * result + Arrays.hashCode(alterations);
		result = prime * result + Arrays.hashCode(cancers);
		result = prime * result + Arrays.hashCode(families);
		result = prime * result + Arrays.hashCode(genes);
		result = prime * result + Arrays.hashCode(sources);
		result = prime * result + Arrays.hashCode(warnings);
		result = prime * result + Objects.hash(dScore, drug, drugStatusInfo, gScore, indirect, sensitivity, showDrugName, status, target, therapy);
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
		
		return Arrays.equals(alterations, other.alterations) && Arrays.equals(cancers, other.cancers)
			&& Double.doubleToLongBits(dScore) == Double.doubleToLongBits(other.dScore) && Objects.equals(drug, other.drug)
			&& Objects.equals(drugStatusInfo, other.drugStatusInfo) && Arrays.equals(families, other.families)
			&& Double.doubleToLongBits(gScore) == Double.doubleToLongBits(other.gScore) && Arrays.equals(genes, other.genes)
			&& Objects.equals(indirect, other.indirect) && sensitivity == other.sensitivity && Objects.equals(showDrugName, other.showDrugName)
			&& Arrays.equals(sources, other.sources) && status == other.status && Objects.equals(target, other.target) && therapy == other.therapy
			&& Arrays.equals(warnings, other.warnings);
	}
}
