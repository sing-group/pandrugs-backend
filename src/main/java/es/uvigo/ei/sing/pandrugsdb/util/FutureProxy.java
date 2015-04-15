package es.uvigo.ei.sing.pandrugsdb.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface FutureProxy<T> extends Future<T> {
	
	public Future<T> getWrappedFuture();
	
	public void wrapFuture(Future<T> future);
	
	@Override
	public default boolean cancel(boolean mayInterruptIfRunning) {
		return getWrappedFuture().cancel(mayInterruptIfRunning);
	}

	@Override
	public default boolean isCancelled() {
		return getWrappedFuture().isCancelled();
	}

	@Override
	public default boolean isDone() {
		return getWrappedFuture().isDone();
	}

	@Override
	public default T get()
			throws InterruptedException, ExecutionException {
		return getWrappedFuture().get();
	}

	@Override
	public default T get(long timeout,
			TimeUnit unit) throws InterruptedException, ExecutionException,
			TimeoutException {
		return getWrappedFuture().get(timeout, unit);
	}

	
}
