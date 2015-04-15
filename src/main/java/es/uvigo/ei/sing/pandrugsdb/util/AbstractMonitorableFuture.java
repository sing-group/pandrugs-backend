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
