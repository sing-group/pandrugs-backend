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

package es.uvigo.ei.sing.pandrugs.core.variantsanalysis;

import static java.lang.ProcessBuilder.Redirect.appendTo;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsEffectPredictionResults;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationResults;
import es.uvigo.ei.sing.pandrugs.util.ContextParameter;
import es.uvigo.ei.sing.pandrugs.util.ContextParameterName;

@Component
public class PerlVEPtoVariantsScoreCalculator implements VEPtoVariantsScoreCalculator {
	private final static Logger LOG = LoggerFactory.getLogger(PerlVEPtoVariantsScoreCalculator.class);

	public static final String VARIANT_SCORES_FILE_NAME = "vep_data.csv";
	public static final String AFFECTED_GENES_FILE_NAME = "genes_affected.csv";

	private static final String VEPTOVSCORE_COMMAND_TEMPLATE_PARAMETER = "veptovscore.command.template";

	@Autowired
	@ContextParameter
	@ContextParameterName(VEPTOVSCORE_COMMAND_TEMPLATE_PARAMETER)
	private String vepToVScoreCommandTemplate;

	@Inject
	private FileSystemConfiguration configuration;

	protected PerlVEPtoVariantsScoreCalculator() { }

	public PerlVEPtoVariantsScoreCalculator(FileSystemConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	public VariantsScoreComputationResults calculateVariantsScore (
			VariantsScoreComputationParameters parameters, VariantsEffectPredictionResults vep) {


		Path vepFilePath = parameters.getResultsBasePath().resolve(vep.getFilePath());
		File vepFile = configuration.getUserDataBaseDirectory().toPath().resolve(vepFilePath).toFile();


		Path affectedGenesPath = Paths.get(AFFECTED_GENES_FILE_NAME);

		String command = createVEPCommand(vepFile.toPath(), configuration.getUserDataBaseDirectory().toPath().resolve(parameters.getResultsBasePath()));
		LOG.info("Starting Perl VSCORE computation over "+vepFile+" with command: "+command);
		try {
			ProcessBuilder pb = new ProcessBuilder(Arrays.asList(command.split(" ")))
								.redirectErrorStream(true)
								.redirectOutput(
									appendTo(new File(vepFile.getParent()+File.separator+"vepparser-out.log")));
			int retValue = pb.start().waitFor();

			if (retValue == 130) {
				// bash exit code for CTRL+C (Interruption)
				// When the server stops, these child processes are interrupted also
				// In this case, we will throw an InterruptedException, indicating that
				// this process has no inherent-error, it was deliberately interrupted, so
				// it maybe restarted with the same parameters in the future.
				LOG.error("Perl VSCORE process over "+vepFile+" was interrupted");
				throw new InterruptedException("VEP process was interrupted");
			}
			if (retValue != 0) {
				LOG.error("Error during Perl VSCORE computation over "+vepFile+" due to non-zero ("+retValue+") exit " +
						"status");
				throw new RuntimeException("Perl VSCORE process had non 0 exit status, exit status: "+retValue);
			}
			LOG.info("Finished Perl VSCORE computation over "+vepFile);
		} catch (InterruptedException | IOException e) {
			LOG.error("Exception during VSCORE computation over "+vepFile+". Exception: "+e);
			throw new RuntimeException(e);
		}

		return new VariantsScoreComputationResults(vep, Paths.get(VARIANT_SCORES_FILE_NAME), affectedGenesPath);
	}

	private String createVEPCommand(Path path, Path resultsBasePath) {
		String commandTemplate = requireNonNull(
				vepToVScoreCommandTemplate,
				"The context init parameter "+ VEPTOVSCORE_COMMAND_TEMPLATE_PARAMETER +" was not found. Please" +
						" configure it in your server configuration");

		if (!path.isAbsolute()) {
			throw new IllegalArgumentException("path must be an absolute path, given: "+path);
		}

		if (!resultsBasePath.isAbsolute()) {
			throw new IllegalArgumentException("resultsBasePath must be an absolute path, given: "+resultsBasePath);
		}

		// command parameters:
		// 1) input VEP absolute path
		// 2) Results absolute path
		// 3) Name for results subdir
		return String.format(commandTemplate, path.toString(), resultsBasePath.getParent().toString(), resultsBasePath
				.getFileName());
	}
}
