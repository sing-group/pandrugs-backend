/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2019 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

package es.uvigo.ei.sing.pandrugs.util;

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
