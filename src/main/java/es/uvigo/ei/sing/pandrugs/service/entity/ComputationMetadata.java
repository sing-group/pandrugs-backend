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

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreUserComputation;

@XmlRootElement(name = "computationStatusMetadata", namespace = "https://www.pandrugs.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComputationMetadata {
	private final Integer variantsInInput;
	private String name;
	private Set<String> affectedGenes;
	private Map<String, Map<String, String>> affectedGenesInfo;
	private double overallProgress;
	private double taskProgress;
	private String taskName;
	private boolean finished;
	private boolean failed;
	private boolean pharmcat;
	private boolean cnvTsvFile;
	private boolean expressionDataFile;

	public ComputationMetadata(VariantsScoreUserComputation computation, Set<String> affectedGenes,
		Map<String, Map<String, String>> affectedGenesInfo
	) {
		this.affectedGenes = affectedGenes;
		this.affectedGenesInfo = affectedGenesInfo;
		this.name = computation.getName();
		this.variantsInInput = computation.getComputationDetails().getParameters().getNumberOfInputVariants();
		this.overallProgress = computation.getComputationDetails().getStatus().getOverallProgress();
		this.taskProgress = computation.getComputationDetails().getStatus().getTaskProgress();
		this.taskName = computation.getComputationDetails().getStatus().getTaskName();
		this.finished = computation.getComputationDetails().getStatus().isFinished();
		this.failed = computation.getComputationDetails().getStatus().hasErrors();
		this.pharmcat = computation.getComputationDetails().getPharmCatComputationParameters().isPharmCat();
		if(computation.getComputationDetails().getCombinedQueryParameters().isPresent()) {
			this.cnvTsvFile = computation.getComputationDetails().getCombinedQueryParameters().get().hasCnvTsvFile();
			this.expressionDataFile = computation.getComputationDetails().getCombinedQueryParameters().get().hasExpressionDataFile();
		} else {
			this.cnvTsvFile = false;
			this.expressionDataFile = false;
		}
	}

	public String getName() {
		return name;
	}

	public Set<String> getAffectedGenes() {
		return affectedGenes;
	}

	public Map<String, Map<String, String>> getAffectedGenesInfo() { return affectedGenesInfo; }

	public Integer getVariantsInInput() { return variantsInInput; }

	public double getOverallProgress() {
		return overallProgress;
	}

	public double getTaskProgress() {
		return taskProgress;
	}

	public String getTaskName() {
		return taskName;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isFailed() {
		return failed;
	}

	public boolean isPharmcat() {
		return pharmcat;
	}

	public boolean isCnvTsvFile() {
		return cnvTsvFile;
	}

	public boolean isExpressionDataFile() {
		return expressionDataFile;
	}
}
