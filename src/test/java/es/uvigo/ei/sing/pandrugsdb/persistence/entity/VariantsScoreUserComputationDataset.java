/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 - 2016 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import java.nio.file.Paths;

public class VariantsScoreUserComputationDataset {
	private VariantsScoreUserComputationDataset() {}

	public final static VariantsScoreUserComputation[] computations() {
		return new VariantsScoreUserComputation[] {
				new VariantsScoreUserComputation(
						1, UserDataset.users()[0],
						new VariantsScoreComputationDetails(
								new VariantsScoreComputationParameters("inputVCF.vcf", "pepe-1"),
								new VariantsScoreComputationStatus(
										"Computing Variant Scores",
										0.0,
										0.5),
								new VariantsScoreComputationResults(
										new VariantsEffectPredictionResults(Paths.get("vep.txt")), null, null)
						)
				),
				new VariantsScoreUserComputation(
						2, UserDataset.users()[1],
						new VariantsScoreComputationDetails(
								new VariantsScoreComputationParameters("inputVCF.vcf", "pepe-2"),
								new VariantsScoreComputationStatus(
										"Finished",
										1.0,
										1.0),
								new VariantsScoreComputationResults(
										new VariantsEffectPredictionResults(Paths.get("vep.txt")),
										Paths.get("vep_data.csv"),
										Paths.get("genes_affected.csv"))
						)
				)
			};
	}
}
