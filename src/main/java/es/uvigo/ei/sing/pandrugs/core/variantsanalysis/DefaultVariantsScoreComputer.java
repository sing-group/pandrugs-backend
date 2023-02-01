/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2023 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

package es.uvigo.ei.sing.pandrugs.core.variantsanalysis;

import static java.util.Collections.synchronizedList;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.PharmCatRunner;
import es.uvigo.ei.sing.pandrugs.persistence.entity.PharmCatComputationParameters;
import es.uvigo.ei.sing.pandrugs.persistence.entity.PharmCatResults;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsEffectPredictionResults;
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
	private PharmCatRunner pharmCatRunner;

	@Inject
	private VcfPreprocessorRunner vcfPreprocessorRunner;

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
	public VariantsScoreComputation createComputation(VariantsScoreComputationParameters parameters,
			PharmCatComputationParameters pharmCatComputationParameters) {
		return createAndStartWholeComputation(parameters, pharmCatComputationParameters);
	}

	private DefaultVariantsScoreComputation createAndStartWholeComputation(
			VariantsScoreComputationParameters parameters,
			PharmCatComputationParameters pharmCatComputationParameters
	) {

		final CompletableFuture<Map<String, Object>> inputParameters = new CompletableFuture<>();
		Map<String, Object> inputParametersMap = new HashMap<>();
		inputParametersMap.put("variants", parameters);
		inputParametersMap.put("pharmcat", pharmCatComputationParameters);

		final DefaultVariantsScoreComputation computation = new DefaultVariantsScoreComputation();
		final Notifications notifications = new Notifications();
		
		computation.getStatus().setStatus("Submitted", 0.0, 0.0);
		CompletableFuture<VariantsScoreComputationResults> tasks = inputParameters
			.thenApply(
				(params) -> {
					Map<String, Object> results = new HashMap<>();

					VariantsScoreComputationParameters vscParams = (VariantsScoreComputationParameters) params.get("variants");
					PharmCatComputationParameters pharmCatParams = (PharmCatComputationParameters) params.get("pharmcat");

					LOG.info("Starting computation. Path: " + vscParams.getResultsBasePath());
					// use another thread to notify status, because if listeners try to call get()
					// they will lock, because they are in the same thread as the computation.

					notifications.submit(() -> 
						computation.getStatus().setStatus("VCF checking and preprocessing", 0.0, 0.0)
					);
					LOG.info("Starting VCF checking and preprocessing. Path: " + vscParams.getResultsBasePath());

					vcfPreprocessorRunner.checkAndPreprocessVcf(
						vscParams.getVcfFile(), pharmCatParams.isPharmCat(), vscParams.getResultsBasePath()
					);
					LOG.info("Completed VCF checking and preprocessing. Path: " + vscParams.getResultsBasePath());
					
					if(pharmCatParams.isPharmCat()) {
						notifications.submit(() -> 
							computation.getStatus().setStatus("Running PharmCAT", 0.0, 0.25)
						);
						LOG.info("Starting PharmCat computation. Path: " + parameters.getResultsBasePath());

						/*
						Path pharmCatVcfFile = new File(
							vscParams.getVcfFile().toString().replace("input.vcf", "input.pharmcat.vcf")
						).toPath();
						*/

						Path pharmCatVcfFile = vcfPreprocessorRunner.getPharmCatVcfFile(vscParams.getVcfFile());

						if (pharmCatParams.hasPharmCatPhenotyperTsvFile()) {
							PharmCatResults pharmCatResults = pharmCatRunner.pharmCat(
								pharmCatVcfFile,
								pharmCatParams.getPharmCatPhenotyperTsvFile(),
								vscParams.getResultsBasePath()
							);
							results.put("pharmcat", pharmCatResults);
						} else {
							PharmCatResults pharmCatResults = pharmCatRunner.pharmCat(
								pharmCatVcfFile,
								vscParams.getResultsBasePath()
							);
							results.put("pharmcat", pharmCatResults);
						}
						
						LOG.info("Completed PharmCat computation. Path: " + vscParams.getResultsBasePath());
					}
					
					notifications.submit(() -> 
						computation.getStatus().setStatus("Computing VEP", 0.0, pharmCatParams.isPharmCat() ? 0.5 : 0.33)
					);

					/*
					Path vepVcfFile = new File(
							vscParams.getVcfFile().toString().replace("input.vcf", "input.vep.vcf")
						).toPath();
					*/
					VariantsEffectPredictionResults vepResults = effectPredictor.predictEffect(
						vcfPreprocessorRunner.getVepVcfFile(vscParams.getVcfFile()), vscParams.getResultsBasePath()
					);

					results.put("variants", vepResults);
					results.put("params", params);
					
					return results;
				}
			)
			.whenComplete(
				(previousResults, exception) -> {
					if (exception == null) {
						LOG.info("Completed VEP computation. Path: " + parameters.getResultsBasePath());
						
						@SuppressWarnings("unchecked")
						final Map<String, Object> params = (Map<String, Object>) previousResults.get("params");
						PharmCatComputationParameters pharmCatParams = (PharmCatComputationParameters) params.get("pharmcat");

						notifications.submit(() -> 
							computation.getStatus().setStatus("Computing Variant Scores", 0.0, pharmCatParams.isPharmCat() ? 0.75 : 0.66)
						);
					} else {
						LOG.error("Error in VEP computation. Path: " + parameters.getResultsBasePath());
					}
				}
			)
			.thenApply(
				(previousResults) -> {
					@SuppressWarnings("unchecked")
					final Map<String, Object> params = (Map<String, Object>) previousResults.get("params");
					PharmCatComputationParameters pharmCatParams = (PharmCatComputationParameters) params.get("pharmcat");
					VariantsEffectPredictionResults vepResults = (VariantsEffectPredictionResults) previousResults.get("variants");
					PharmCatResults pharmCatResults = (PharmCatResults) previousResults.get("pharmcat");

					VariantsScoreComputationResults results = variantsScoreCalculator.calculateVariantsScore(parameters, vepResults);
					if (pharmCatParams.isPharmCat()) {
						results.setPharmCatResults(pharmCatResults);
					}

					return results;
				}
			)
			.whenComplete(
				(result, exception) -> {
					if (exception == null) {
						LOG.info("Finished VSCORE computation. Path: " + parameters.getResultsBasePath());

						notifications.submit(() -> 
							computation.getStatus().setStatus("Annotation Process Finished", 0.0, 1.0)
						);
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

					vcfPreprocessorRunner.deleteVcfs(parameters.getResultsBasePath(), parameters.getVcfFile());
				}
			);

		computation.wrapFuture(tasks);

		LOG.info("Submitted computation. Path: " + parameters.getResultsBasePath());

		this.executorService.execute(() -> {
			inputParameters.complete(inputParametersMap);
		});

		return computation;
	}

	private class Notifications {
		private final List<Future<?>> submittedNotifications;
		
		public Notifications() {
			this.submittedNotifications = synchronizedList(new LinkedList<>());
		}
		
		public void submit(Runnable notification) {
			this.submittedNotifications.add(notificationExecutorService.submit(() -> {
				try {
					notification.run();
				} catch(Exception e) {
					LOG.error("Exception during notification processing: "+e.getMessage());
					e.printStackTrace();
				}
			}));
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
			return this.createAndStartWholeComputation(computation.getParameters(), computation.getPharmCatComputationParameters());
		} else {
			throw new IllegalArgumentException("The computation was finished");
		}
	}
}
