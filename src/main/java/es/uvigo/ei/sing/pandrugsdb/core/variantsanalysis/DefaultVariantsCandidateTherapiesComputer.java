package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.inject.Inject;

import es.uvigo.ei.sing.pandrugsdb.util.AbstractMonitorableFuture;
import es.uvigo.ei.sing.pandrugsdb.util.FutureProxy;

public class DefaultVariantsCandidateTherapiesComputer implements
		VariantsCandidateTherapiesComputer {

	@Inject
	private VariantEffectPredictor effectPredictor;
	
	@Inject
	private CandidateTherapiesCalculator candidateTherapyCalculator; 
	
	@Override
	public VariantsCandidateTherapiesComputation 
		createComputation(URL vcfFile) {
		
		final CompletableFuture<URL> input = new CompletableFuture<URL>();
		
		AbstractVariantCallingCandidateTherapiesComputation  computation =
				new AbstractVariantCallingCandidateTherapiesComputation();
		
		System.out.println(computation);
		computation.setCurrentTaskName("Calculate VEP");
		computation.setOverallProgress(0f);
		computation.setCurrentTaskProgress(0f);
		
		// compute VEP
		CompletableFuture<VariantsEffectPredictionResults> vepFuture = 
				input
				.thenApply(effectPredictor::predictEffect)
				.whenComplete((result, exception) -> {
					if (exception == null) {
						computation.setCurrentTaskName("Calculate therapies");
						computation.setCurrentTaskProgress(0f);
						computation.setOverallProgress(0.5f);
					}
				}
				);
		
		// Compute Candidate Therapies and combine them with VEP results
		CompletableFuture<CandidateTherapiesComputationResults> tasks = 
				vepFuture
				.thenCombine(
					vepFuture
						.thenApply(
								candidateTherapyCalculator::calculateTherapies)
						.whenComplete((result, exception) -> {
								if (exception == null) {
									computation.setCurrentTaskName("none");
									computation.setOverallProgress(100f);
									computation.setCurrentTaskProgress(0f);
								} 
							}
						), 
					(vepRes, list) -> 
						new CandidateTherapiesComputationResults(list, vepRes)
		);
		
		computation.wrapFuture(tasks);
		input.complete(vcfFile);
		return computation;
	}
	
	static class AbstractVariantCallingCandidateTherapiesComputation 
		extends AbstractMonitorableFuture<CandidateTherapiesComputationResults>
		implements VariantsCandidateTherapiesComputation,
		FutureProxy<CandidateTherapiesComputationResults> {

		private Future<CandidateTherapiesComputationResults> future;
		
		@Override
		public void wrapFuture(Future<CandidateTherapiesComputationResults>
			future) {
			this.future = future;
		}
		
		@Override
		public Future<CandidateTherapiesComputationResults> getWrappedFuture() {
			return future;
		}
	}
}
