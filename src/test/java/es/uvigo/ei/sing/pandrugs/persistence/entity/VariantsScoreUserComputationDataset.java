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

package es.uvigo.ei.sing.pandrugs.persistence.entity;

import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.guestUser;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.user;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;

public class VariantsScoreUserComputationDataset {
	private VariantsScoreUserComputationDataset() {}

	public static void copyComputationFilesToDir(String systemTmpDir) throws IOException {
		for (VariantsScoreUserComputation computation : VariantsScoreUserComputationDataset.computations()) {
			File computationDir = new File(
				systemTmpDir +
					File.separator +
					computation.getComputationDetails().getParameters().getResultsBasePath().toString()
			);

			if (!computationDir.exists()) {
				computationDir.mkdir();
			}

			if (computation.getComputationDetails().getParameters().getVcfFile() != null) {
				copyComputationFile("inputVCF.vcf", computationDir, computation);
			}
			if (computation.getComputationDetails().getResults().getVepResults().getFilePath() != null) {
				copyComputationFile("vep.txt", computationDir, computation);
			}
			if (computation.getComputationDetails().getResults().getAffectedGenesPath() != null) {
				copyComputationFile("genes_affected.csv", computationDir, computation);
			}
			if (computation.getComputationDetails().getResults().getVscorePath() != null) {
				copyComputationFile("vep_data.csv", computationDir, computation);
			}
		}
	}

	private static void copyComputationFile(String fileName, File computationDir, VariantsScoreUserComputation computation)
		throws IOException {
		copyInputStreamToFile(
			openComputationFileStream(
				"/META-INF/dataset.variantanalysis.xml.files/" +
					computation.getComputationDetails().getParameters().getResultsBasePath().toString() + "/" +
					fileName
			),
			new File(computationDir.getAbsolutePath() + File.separator + fileName)
		);
	}

	private static InputStream openComputationFileStream(String name) {
		return VariantsScoreUserComputationDataset.class.getResourceAsStream(name);
	}
	
	public final static VariantsScoreUserComputation[] computations() {
		return new VariantsScoreUserComputation[] {
			new VariantsScoreUserComputation(
				"a37a71a5-d7a5-4848-9cc2-db465e996ec8",
				user("pepe"),
				new VariantsScoreComputationDetails(
					new VariantsScoreComputationParameters("inputVCF.vcf", "pepe-1"),
					new VariantsScoreComputationStatus(
						"Computing Variant Scores",
						0.0,
						0.5
					),
					new VariantsScoreComputationResults(
						new VariantsEffectPredictionResults(Paths.get("vep.txt")), null, null
					)
				)
			),
			new VariantsScoreUserComputation(
				"0504448a-41e1-4664-b053-37e327f7992c",
				user("pepe"),
				new VariantsScoreComputationDetails(
					new VariantsScoreComputationParameters("inputVCF.vcf", "pepe-2"),
					new VariantsScoreComputationStatus(
						"Annotation Process Finished",
						1.0,
						1.0
					),
					new VariantsScoreComputationResults(
						new VariantsEffectPredictionResults(Paths.get("vep.txt")),
						Paths.get("vep_data.csv"),
						Paths.get("genes_affected.csv")
					)
				)
			),
			new VariantsScoreUserComputation(
				"318678c4-fb9b-4dc7-a47e-53b532d34840",
				guestUser(),
				new VariantsScoreComputationDetails(
					new VariantsScoreComputationParameters("inputVCF.vcf", "guest-3"),
					new VariantsScoreComputationStatus(
						"Annotation Process Finished",
						1.0,
						1.0
					),
					new VariantsScoreComputationResults(
						new VariantsEffectPredictionResults(Paths.get("vep.txt")),
						Paths.get("vep_data.csv"),
						Paths.get("genes_affected.csv")
					)
				)
			)
		};
	}

	public final static String[] computationIds() {
		return Arrays.stream(computations())
			.map(VariantsScoreUserComputation::getId)
		.toArray(String[]::new);
	}
	
	public final static String unexistantComputationId() {
		return "aaaaaaaa-bbbb-cccc-dddd-000000000000";
	}
}
