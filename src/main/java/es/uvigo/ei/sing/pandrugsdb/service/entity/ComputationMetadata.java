/*
 * #%L
 * PanDrugsDB Backend
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
package es.uvigo.ei.sing.pandrugsdb.service.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreUserComputation;

@XmlRootElement(name = "computationStatusMetadata", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComputationMetadata {
	private String name;
	private Integer affectedGenes;
	private double overallProgress;
	private double taskProgress;
	private String taskName;
	private boolean finished;
	private boolean failed;

	public ComputationMetadata(VariantsScoreUserComputation computation, Integer affectedGenes) {
		this.affectedGenes = affectedGenes;
		this.name = computation.getName();
		this.overallProgress = computation.getComputationDetails().getStatus().getOverallProgress();
		this.taskProgress = computation.getComputationDetails().getStatus().getTaskProgress();
		this.taskName = computation.getComputationDetails().getStatus().getTaskName();
		this.finished = computation.getComputationDetails().getStatus().isFinished();
		this.failed = computation.getComputationDetails().getStatus().hasErrors();
	}

	public String getName() {
		return name;
	}

	public Integer getAffectedGenes() {
		return affectedGenes;
	}

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
}
