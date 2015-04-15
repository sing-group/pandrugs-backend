package es.uvigo.ei.sing.pandrugsdb.util;

import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface MonitorableFuture<T> extends Future <T> {
	
	public double getCurrentTaskProgress();
	
	public double getOverallProgress();
	
	public String getCurrentTaskName();
	
	public void onCurrentTaskProgressChange(Consumer<Double> consumer);
	
	public void onOverallProgressChange(Consumer<Double> consumer);
	
	public void onCurrentTaskNameChange(Consumer<String> consumer);
	
}
