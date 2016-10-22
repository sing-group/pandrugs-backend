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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsEffectPredictionResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationResults;

@Component
public class DefaultVEPtoVariantsScoreCalculator implements
		VEPtoVariantsScoreCalculator {

	public static final String VARIANT_SCORES_FILE_NAME = "vep_data.csv";
	public static final String AFFECTED_GENES_FILE_NAME = "genes_affected.csv";
	@Inject
	private FileSystemConfiguration configuration;
	
	protected DefaultVEPtoVariantsScoreCalculator() { }
	
	public DefaultVEPtoVariantsScoreCalculator(FileSystemConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	public VariantsScoreComputationResults calculateVariantsScore (
			VariantsEffectPredictionResults vep, Path outPath) {
		Path vscoreResultsPath = Paths.get(VARIANT_SCORES_FILE_NAME);
		Path vscoreFilePath = outPath.resolve(vscoreResultsPath);
		File vscoresFile = configuration.getUserDataBaseDirectory().toPath().resolve(vscoreFilePath).toFile();
		
		//write to outFile ...
		try {
			PrintStream out = new PrintStream(new FileOutputStream(vscoresFile));
			out.println("caca");
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		Path affectedGenesPath = Paths.get(AFFECTED_GENES_FILE_NAME);
		Path affectedGenesFilePath = outPath.resolve(affectedGenesPath);
		File affectedGenesFile = configuration.getUserDataBaseDirectory().toPath().resolve(affectedGenesFilePath).toFile();
		
		//write to outFile ...
		try {
			PrintStream out = new PrintStream(new FileOutputStream(affectedGenesFile));
			out.println("caca");
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		return new VariantsScoreComputationResults(vep, vscoreResultsPath, affectedGenesPath);
	}

}
