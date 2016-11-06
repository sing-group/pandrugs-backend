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
package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationDetails;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationStatus;
import es.uvigo.ei.sing.pandrugsdb.util.FutureProxy;

@Component
public class DefaultVariantsScoreComputer implements
		VariantsScoreComputer {
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
	public VariantsScoreComputation 
		createComputation(VariantsScoreComputationParameters parameters) {
		
		DefaultVariantsScoreComputation computation = createAndStartWholeComputation(parameters);
		
		return computation;
	}
	
	public DefaultVariantsScoreComputation createAndStartWholeComputation(VariantsScoreComputationParameters parameters) {
		final CompletableFuture<VariantsScoreComputationParameters> input = new CompletableFuture<VariantsScoreComputationParameters>();

		final DefaultVariantsScoreComputation  computation =
				new DefaultVariantsScoreComputation();

		computation.getStatus().setOverallProgress(0f);
		computation.getStatus().setTaskName("Submitted");
		computation.getStatus().setTaskProgress(0f);

		// compute VEP
		CompletableFuture<VariantsScoreComputationResults> tasks = 
				input
				.thenApply((params) -> {
					LOG.info("Starting computation. Path: "+parameters.getResultsBasePath());
					//use another thread to notify status, because if listeners try to call
					//get() they will lock, because they are in the same thread as the computation.
					notificationExecutorService.execute( () -> {
						computation.getStatus().setTaskName("Computing VEP");
					});
					return effectPredictor.predictEffect(params.getVcfFile(), params.getResultsBasePath());
				})
				.whenComplete((result, exception) -> {
					if (exception == null) {
						LOG.info("Completed VEP computation. Path: "+parameters.getResultsBasePath());
						notificationExecutorService.execute( () -> {
							computation.getStatus().setOverallProgress(0.5f);
							computation.getStatus().setTaskName("Computing Variant Scores");
							computation.getStatus().setTaskProgress(0f);
						});
					} else {
						LOG.error("Error in VEP computation. Path: "+parameters.getResultsBasePath());
					}
				})
				.thenApply((vepResults) -> variantsScoreCalculator.calculateVariantsScore(parameters, vepResults))
				.whenComplete((result, exception) -> {
					if (exception == null) {
						LOG.info("Finished VEP computation. Path: "+parameters.getResultsBasePath());
						notificationExecutorService.execute( () -> {
							computation.getStatus().setOverallProgress(1.0f);
							computation.getStatus().setTaskName("Finished");
							computation.getStatus().setTaskProgress(0f);
						});
					}  else {
						if (ExceptionUtils.getRootCause(exception) instanceof InterruptedException){
							LOG.warn("Interrupted computation in path "+parameters.getResultsBasePath());
							notificationExecutorService.execute( () -> {
								computation.getStatus().setTaskName("Interrupted");
							});
						} else {
							LOG.error("Error during computation in path "+parameters.getResultsBasePath()+": " +
									""+exception+". Root cause: "+ExceptionUtils.getRootCause(exception));
							exception.printStackTrace();
							notificationExecutorService.execute(() -> {
								computation.getStatus().setOverallProgress(1.0f);
								computation.getStatus().setTaskName("Error");
								computation.getStatus().setTaskProgress(0f);
							});
						}
					}
				});
		
		computation.wrapFuture(tasks);

		LOG.info("Submitted computation. Path: "+parameters.getResultsBasePath());

		this.executorService.execute(() -> {
			input.complete(parameters);
		});
		
		return computation;
	}
	
	static class DefaultVariantsScoreComputation 
		implements VariantsScoreComputation,
		FutureProxy<VariantsScoreComputationResults> {

		private Future<VariantsScoreComputationResults> future;

		private VariantsScoreComputationStatus status = new VariantsScoreComputationStatus();

		
		@Override
		public void wrapFuture(Future<VariantsScoreComputationResults>
			future) {
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
