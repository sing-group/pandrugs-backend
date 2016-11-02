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

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsEffectPredictionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DefaultVariantsEffectPredictor implements VariantsEffectPredictor {

	private final static Logger LOG = LoggerFactory.getLogger(DefaultVariantsEffectPredictor.class);
	public static final String VEP_FILE_NAME = "ensembl_vep.csv";
	
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
			LOG.info("Starting VEP computation with command: "+command);
			Process p = Runtime.getRuntime().exec(command);
			int retValue = p.waitFor();
			if (retValue != 0) {
				LOG.error("Error during VEP computation due to non-zero exit status");
				throw new RuntimeException("VEP process had non 0 exit status, exit status: "+retValue);
			}
			LOG.info("Finished VEP computation with command: "+command);
		} catch (IOException | InterruptedException e) {
			LOG.error("Exception during VEP computation. Exception: "+e);
			throw new RuntimeException(e);
		}

		return new VariantsEffectPredictionResults(resultsFilePath);
	}

}
