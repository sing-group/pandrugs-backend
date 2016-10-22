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

@Component
public class DefaultVariantsEffectPredictor implements VariantsEffectPredictor {

	public static final String VEP_FILE_NAME = "ensembl_vep.csv";
	
	@Inject
	private FileSystemConfiguration configuration;
	
	protected DefaultVariantsEffectPredictor() { }
	
	
	public DefaultVariantsEffectPredictor(FileSystemConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	public VariantsEffectPredictionResults predictEffect(Path vcfFile, Path outPath) {
		Path resultsPath = Paths.get(VEP_FILE_NAME);
		Path outFilePath = outPath.resolve(resultsPath);
		File outFile = configuration.getUserDataBaseDirectory().toPath().resolve(outFilePath).toFile();
		
		//write to outFile ...
		try {
			PrintStream out = new PrintStream(new FileOutputStream(outFile));
			out.println("caca");
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return new VariantsEffectPredictionResults(resultsPath);
	}

}
