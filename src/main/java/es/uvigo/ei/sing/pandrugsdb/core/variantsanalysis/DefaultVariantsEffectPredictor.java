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
package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import static java.lang.ProcessBuilder.Redirect.appendTo;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsEffectPredictionResults;

@Component
public class DefaultVariantsEffectPredictor implements VariantsEffectPredictor {

	private final static Logger LOG = LoggerFactory.getLogger(DefaultVariantsEffectPredictor.class);
	public static final String VEP_FILE_NAME = "vep.txt";
	
	@Inject
	private FileSystemConfiguration configuration;

	@Inject
	private VEPConfiguration vepConfiguration;

	protected DefaultVariantsEffectPredictor() { }


	public DefaultVariantsEffectPredictor(FileSystemConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	public VariantsEffectPredictionResults predictEffect(Path vcfFile, Path userPath) {
		Path resultsFilePath = Paths.get(VEP_FILE_NAME);
		File outFile = configuration.getUserDataBaseDirectory().toPath().resolve(
							userPath.resolve(resultsFilePath)).toFile();

		File inputFile = configuration.getUserDataBaseDirectory().toPath().resolve(
				userPath.resolve(vcfFile)).toFile();
		try {
			String command = vepConfiguration.createVEPCommand(inputFile.toPath(), outFile.toPath());

			LOG.info("Starting VEP computation over "+inputFile+" with command: "+command);
			ProcessBuilder pb = new ProcessBuilder(asList(command.split(" ")))
									.redirectErrorStream(true)
									.redirectOutput(
											appendTo(new File(inputFile.getParent()+File.separator+"vep-out.log")));
			int retValue = pb.start().waitFor();

			if (retValue == 130) {
				// bash exit code for CTRL+C (Interruption)
				// When the server stops, these child processes are interrupted also
				// In this case, we will throw an InterruptedException, indicating that
				// this process has no inherent-error, it was deliberately interrupted, so
				// it maybe restarted with the same parameters in the future.
				LOG.error("VEP process over "+inputFile+" was interrupted");
				throw new InterruptedException("VEP process was interrupted");
			}
			if (retValue != 0) {
				LOG.error("Error during VEP computation over "+inputFile+" due to non-zero ("+retValue+") exit status");
				throw new RuntimeException("VEP process had non 0 exit status, exit status: "+retValue);
			}
			LOG.info("Finished VEP computation over "+inputFile);
		} catch (IOException | InterruptedException e) {
			LOG.error("Exception during VEP computation over "+inputFile+". Exception: "+e);
			throw new RuntimeException(e);
		}

		return new VariantsEffectPredictionResults(resultsFilePath);
	}

}
