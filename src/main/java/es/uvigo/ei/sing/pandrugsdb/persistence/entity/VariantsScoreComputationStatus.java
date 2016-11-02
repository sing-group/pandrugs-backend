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
package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class VariantsScoreComputationStatus {
	@Column(name="status_task_progress")
	private double taskProgress;
	
	@Column(name="status_overall_progress")
	private double overallProgress;
	
	@Column(name="status_task_name")
	private String taskName;
	
	@Transient
	private List<Consumer<VariantsScoreComputationStatus>> changeListeners = new ArrayList<>();

	public VariantsScoreComputationStatus() {}

	public VariantsScoreComputationStatus(String taskName, double taskProgress, double overallProgress) {
		this.taskName = taskName;
		this.taskProgress = taskProgress;
		this.overallProgress = overallProgress;
	}

	public double getTaskProgress() {
		return taskProgress;
	}
	
	public void setTaskProgress(double taskProgress) {
		this.taskProgress = taskProgress;
		notifyListeners();
	}
	

	public double getOverallProgress() {
		return overallProgress;
	}
	
	public void setOverallProgress(double overallProgress) {
		this.overallProgress = overallProgress;
		notifyListeners();
	}
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
		notifyListeners();
	}
	
	public void onChange(Consumer<VariantsScoreComputationStatus> action) {
		this.changeListeners.add(action);
	}


	private void notifyListeners() {
		for (Consumer<VariantsScoreComputationStatus> listener: this.changeListeners) {
			listener.accept(this);
		}
	}
	
	public boolean isFinished() {
		return this.overallProgress == 1.0f;
	}
}
