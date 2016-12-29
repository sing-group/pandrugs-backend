/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.core.variantsanalysis;

import static java.util.Collections.synchronizedList;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationDetails;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationResults;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationStatus;
import es.uvigo.ei.sing.pandrugs.util.FutureProxy;

@Component
public class DefaultVariantsScoreComputer implements VariantsScoreComputer {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultVariantsScoreComputer.class);

	@Inject
	private VariantsEffectPredictor effectPredictor;

	@Inject
	private VEPtoVariantsScoreCalculator variantsScoreCalculator;

	@Inject
	private ExecutorService executorService;

	private ExecutorService notificationExecutorService = Executors.newSingleThreadExecutor();

	protected DefaultVariantsScoreComputer() {}
	
	public DefaultVariantsScoreComputer(VariantsEffectPredictor effectPredictor,
			VEPtoVariantsScoreCalculator variantsScoreCalculator, ExecutorService executorService) {
		super();
		this.effectPredictor = effectPredictor;
		this.variantsScoreCalculator = variantsScoreCalculator;
		this.executorService = executorService;
	}
	
	@Override
	public VariantsScoreComputation createComputation(VariantsScoreComputationParameters parameters) {
		return createAndStartWholeComputation(parameters);
	}
	
	private DefaultVariantsScoreComputation createAndStartWholeComputation(VariantsScoreComputationParameters parameters) {
		final CompletableFuture<VariantsScoreComputationParameters> input =
			new CompletableFuture<VariantsScoreComputationParameters>();

		final DefaultVariantsScoreComputation computation = new DefaultVariantsScoreComputation();
		final Notifications notifications = new Notifications();
		
		computation.getStatus().setStatus("Submitted", 0.0, 0.0);

		// compute VEP
		CompletableFuture<VariantsScoreComputationResults> tasks = input
			.thenApply(
				(params) -> {
					LOG.info("Starting computation. Path: " + parameters.getResultsBasePath());
					// use another thread to notify status, because if listeners try to call get()
					// they will lock, because they are in the same thread as the computation.
					notifications.submit(() -> computation.getStatus().setTaskName("Computing VEP"));
					
					return effectPredictor.predictEffect(params.getVcfFile(), params.getResultsBasePath());
				}
			)
			.whenComplete(
				(result, exception) -> {
					if (exception == null) {
						LOG.info("Completed VEP computation. Path: " + parameters.getResultsBasePath());
						
						notifications.submit(() -> computation.getStatus().setStatus("Computing Variant Scores", 0.0, 0.5));
					} else {
						LOG.error("Error in VEP computation. Path: " + parameters.getResultsBasePath());
					}
				}
			)
			.thenApply((vepResults) -> variantsScoreCalculator.calculateVariantsScore(parameters, vepResults))
			.whenComplete(
				(result, exception) -> {
					if (exception == null) {
						LOG.info("Finished VSCORE computation. Path: " + parameters.getResultsBasePath());
						
						notifications.submit(() -> computation.getStatus().setStatus("Finished", 0.0, 1.0));
					} else if (ExceptionUtils.getRootCause(exception) instanceof InterruptedException) {
						LOG.warn("Interrupted computation in path " + parameters.getResultsBasePath());
						
						notifications.submit(() -> computation.getStatus().setTaskName("Interrupted"));
					} else {
						LOG.error(
							"Error during computation in path " + parameters.getResultsBasePath() + ": " +
								"" + exception + ". Root cause: " + ExceptionUtils.getRootCause(exception),
							exception
						);
						notifications.submit(() -> computation.getStatus().setStatus("Error", 0.0, 1.0));
					}
				}
			);
		
		computation.wrapFuture(tasks);

		LOG.info("Submitted computation. Path: " + parameters.getResultsBasePath());

		this.executorService.execute(() -> {
			input.complete(parameters);
		});
		
		return computation;
	}
	
	private class Notifications {
		private final List<Future<?>> submittedNotifications;
		
		public Notifications() {
			this.submittedNotifications = synchronizedList(new LinkedList<>());
		}
		
		public void submit(Runnable notification) {
			this.submittedNotifications.add(notificationExecutorService.submit(notification));
		}
	}
	
	static class DefaultVariantsScoreComputation 
		implements VariantsScoreComputation, FutureProxy<VariantsScoreComputationResults> {

		private Future<VariantsScoreComputationResults> future;

		private VariantsScoreComputationStatus status = new VariantsScoreComputationStatus();
		
		@Override
		public void wrapFuture(Future<VariantsScoreComputationResults> future) {
			this.future = future;
		}
		
		@Override
		public Future<VariantsScoreComputationResults> getWrappedFuture() {
			return future;
		}
		
		@Override
		public VariantsScoreComputationStatus getStatus() {
			return this.status;
		}
	}


	@Override
	public VariantsScoreComputation resumeComputation(VariantsScoreComputationDetails computation) {
		if (!computation.getStatus().isFinished()) {
			return this.createAndStartWholeComputation(computation.getParameters());
		} else {
			throw new IllegalArgumentException("The computation was finished");
		}
	}
	
}
