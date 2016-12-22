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

import static java.util.Arrays.stream;

import java.util.Arrays;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Extra;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;

@XmlRootElement(name = "geneDrugInfo", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneDrugInfo {
	@XmlElementWrapper(name = "genes")
	@XmlElement(name = "gene")
	private String[] genes;
	private String drug;
	private String family;
	private DrugStatus status;
	private CancerType[] cancers;
	private Extra therapy;
	private String indirect;
	private String target;
	private String sensitivity;
	private String alteration;
	private String drugStatusInfo;
	private double dScore;
	private double gScore;
	@XmlElementWrapper(name = "sources")
	@XmlElement(name = "source")
	private String[] sources;

	GeneDrugInfo() {
	}

	public GeneDrugInfo(GeneDrug geneDrug, GeneDrugGroup group) {
		this(geneDrug, group, false);
	}

	public GeneDrugInfo(GeneDrug geneDrug, GeneDrugGroup group, boolean forceIndirect) {
		this.genes = group.getTargetGeneNames(geneDrug, forceIndirect);
		this.drug = geneDrug.getStandardDrugName();
		this.sources = geneDrug.getDrug().getDrugSourceNames().stream()
			.toArray(String[]::new);
		this.family = geneDrug.getFamily();
		this.status = geneDrug.getStatus();
		this.cancers = geneDrug.getCancers();
		this.therapy = geneDrug.getExtra();
		this.indirect = group.getIndirectGeneName(geneDrug, forceIndirect);
		this.target = geneDrug.isTarget() ? "target" : "marker";
		this.sensitivity = geneDrug.getResistance().name();
		this.alteration = geneDrug.getAlteration();
		
		if (geneDrug.isTarget()) {
			if (!forceIndirect && group.isDirect(geneDrug)) {
				this.gScore = group.getGScore(geneDrug);
				this.drugStatusInfo = String.format(
					"%s is a drug %s that acts as an inhibitor of %s",
					this.drug, geneDrug.getStatus().getDescription(), this.genes[0]
				);
			} else if (group.isIndirect(geneDrug)) {
				final Map<String, Double> indirectGScores =
					group.getIndirectGScores(geneDrug);
				
				this.gScore = stream(this.genes)
					.mapToDouble(indirectGScores::get)
				.max().orElse(0d);
				
				this.drugStatusInfo = String.format(
					"%s is a drug %s that acts as an inhibitor of %s, a protein downstream to %s",
					this.drug, geneDrug.getStatus().getDescription(),
					this.indirect, joinGeneNames(this.genes)
				);
			} else {
				throw new IllegalArgumentException(geneDrug.getGeneSymbol() + " is not indirect but indirect mode was forced.");
			}
		} else if (group.isDirect(geneDrug)) {
			this.gScore = group.getGScore(geneDrug);
			
			this.drugStatusInfo = String.format(
				"Molecular alterations in %s are associated to response to %s, a drug %s",
				this.genes[0], this.drug, geneDrug.getStatus().getDescription() 
			);
		} else {
			throw new IllegalArgumentException(geneDrug.getGeneSymbol() + " is indirect and marker.");
		}
		
		this.dScore = group.getDScore(geneDrug);
	}
	
	public String[] getGenes() {
		return genes;
	}
	
	public String[] getSources() {
		return sources;
	}
	
	public String getDrug() {
		return drug;
	}
	
	public String getFamily() {
		return family;
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
	
	public String getIndirect() {
		return indirect;
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getSensitivity() {
		return sensitivity;
	}
	
	public String getAlteration() {
		return alteration;
	}
	
	public String getDrugStatusInfo() {
		return drugStatusInfo;
	}
	
	public double getDScore() {
		return dScore;
	}
	
	public double getGScore() {
		return gScore;
	}
	
	private final static String joinGeneNames(String ... geneNames) {
		if (geneNames.length == 1) {
			return geneNames[0];
		} else if (geneNames.length == 2) {
			return String.join(" and ", geneNames);
		} else {
			final String[] subGeneNames = new String[geneNames.length - 1];
			System.arraycopy(geneNames, 0, subGeneNames, 0, subGeneNames.length);
			final String lastGene = geneNames[geneNames.length - 1];
			
			return String.join(", ", subGeneNames) + " and " + lastGene;
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
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		temp = Double.doubleToLongBits(gScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(genes);
		result = prime * result + ((indirect == null) ? 0 : indirect.hashCode());
		result = prime * result + ((sensitivity == null) ? 0 : sensitivity.hashCode());
		result = prime * result + Arrays.hashCode(sources);
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
		if (family == null) {
			if (other.family != null)
				return false;
		} else if (!family.equals(other.family))
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
		if (sensitivity == null) {
			if (other.sensitivity != null)
				return false;
		} else if (!sensitivity.equals(other.sensitivity))
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
		return true;
	}
}
