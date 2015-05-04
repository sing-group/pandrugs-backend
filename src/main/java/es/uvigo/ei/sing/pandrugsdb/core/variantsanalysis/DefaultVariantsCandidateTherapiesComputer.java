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
