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
package es.uvigo.ei.sing.pandrugsdb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractMonitorableFuture<T> implements MonitorableFuture<T> {

	private double currentTaskProgress, overallProgress;
	private String currentTask;

	private List<Consumer<Double>> onCurrentTaskProgressChangeListeners = 
			new ArrayList<>();
	private List<Consumer<Double>> onOverallProgressChangeListeners = 
			new ArrayList<>();
	private List<Consumer<String>> onCurrentTaskNameChangeListeners = 
			new ArrayList<>();
	@Override
	public double getCurrentTaskProgress() {
		return currentTaskProgress;
	}
	
	public void setCurrentTaskProgress(double currentTaskProgress) {
		this.currentTaskProgress = currentTaskProgress;
		
		onCurrentTaskProgressChangeListeners.stream()
			.forEach(c -> c.accept(currentTaskProgress));
	}


	@Override
	public double getOverallProgress() {
		return overallProgress;
	}
	
	public void setOverallProgress(double overallProgress) {
		this.overallProgress = overallProgress;
		onOverallProgressChangeListeners.stream()
			.forEach(c -> c.accept(overallProgress));
	}

	@Override
	public String getCurrentTaskName() {
		return currentTask;
	}
	
	@Override
	public void onCurrentTaskNameChange(Consumer<String> consumer) {
		this.onCurrentTaskNameChangeListeners.add(consumer);
		
	}
	
	public void onCurrentTaskProgressChange(java.util.function.Consumer<Double> consumer) {
		this.onCurrentTaskProgressChangeListeners.add(consumer);
	};
	
	@Override
	public void onOverallProgressChange(Consumer<Double> consumer) {
		this.onOverallProgressChangeListeners.add(consumer);
		
	}
	
	public void setCurrentTaskName(String currentTask) {
		this.currentTask = currentTask;
		onCurrentTaskNameChangeListeners.stream()
		.forEach(c -> c.accept(currentTask));
	}
	
	
	
}
