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

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreUserComputation;

/**
 * Created by lipido on 10/20/16.
 */
@XmlRootElement(name = "computation-status-metadata", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ComputationMetadata {

	private VariantsScoreUserComputation computation;

	private Integer affectedGenes;

	public ComputationMetadata(VariantsScoreUserComputation computation, Integer affectedGenes) {
		this.computation = computation;
		this.affectedGenes = affectedGenes;
	}

	public String getName() {
		return computation.getName();
	}

	public double getOverallProgress() {
		return computation.getComputationDetails().getStatus().getOverallProgress();
	}

	public double getTaskProgress() {
		return computation.getComputationDetails().getStatus().getTaskProgress();
	}

	public String getTaskName() {
		return computation.getComputationDetails().getStatus().getTaskName();
	}

	public boolean isFinished() {
		return computation.getComputationDetails().getStatus().isFinished();
	}

	public boolean isFailed() {
		return computation.getComputationDetails().getStatus().hasErrors();
	}

	public Integer getAffectedGenes() {
		return affectedGenes;
	}
}
