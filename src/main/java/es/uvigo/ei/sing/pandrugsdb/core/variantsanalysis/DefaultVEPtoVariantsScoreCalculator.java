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

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsEffectPredictionResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationResults;
import es.uvigo.ei.sing.vcfparser.vcf.VCFParseException;
import es.uvigo.ei.sing.vcfparser.vcf.VCFReader;
import es.uvigo.ei.sing.vcfparser.vcf.vep.VEPMetaData;
import es.uvigo.ei.sing.vcfparser.vcf.vep.VEPMetaDataBuilder;
import es.uvigo.ei.sing.vcfparser.vcf.vep.VEPVariant;
import es.uvigo.ei.sing.vcfparser.vcf.vep.VEPVariantDataBuilder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

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
			VariantsScoreComputationParameters parameters, VariantsEffectPredictionResults vep) {


		Path affectedGenesPath = Paths.get(AFFECTED_GENES_FILE_NAME);
		Path affectedGenesFilePath = parameters.getResultsBasePath().resolve(affectedGenesPath);
		File affectedGenesFile = configuration.getUserDataBaseDirectory().toPath().resolve(affectedGenesFilePath).toFile();

		try (
				PrintStream genesAffectedOut = new PrintStream(new FileOutputStream(affectedGenesFile)))
		{

			Map<String, Double> geneScores = computeGeneScores(parameters, vep);

			geneScores.entrySet().stream().forEach(
					(e) -> genesAffectedOut.println(e.getKey() + "\t" + e.getValue())
			);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return new VariantsScoreComputationResults(vep, Paths.get(VARIANT_SCORES_FILE_NAME), affectedGenesPath);
	}

	private Map<String, Double> computeGeneScores(VariantsScoreComputationParameters parameters, VariantsEffectPredictionResults vep) throws IOException, VCFParseException {
		VCFReader<VEPMetaData, VEPVariant> reader = createVCFReader(vep, parameters.getResultsBasePath());

		Path vscoreResultsPath = Paths.get(VARIANT_SCORES_FILE_NAME);
		Path vscoreFilePath = parameters.getResultsBasePath().resolve(vscoreResultsPath);
		File vscoresFile = configuration.getUserDataBaseDirectory().toPath().resolve(vscoreFilePath).toFile();

		try (PrintStream vScoresOut = new PrintStream(new FileOutputStream(vscoresFile))) {

			return reader.getVariants().stream().filter( v -> this.getGeneName(v) != null).collect(toMap(
					v -> getGeneName(v),
					v -> computeVScore(v, vScoresOut),
					(score1, score2) -> Math.max(score1, score2)));
		}
	}

	private double computeVScore(VEPVariant vepVariant, PrintStream vScoresOut) {

		return 0.5;
	}

	private String getGeneName(VEPVariant vepVariant) {
		Object attValue = vepVariant.getCSQAttribute(0, "SYMBOL");
		System.out.println(attValue);
		return attValue == null? null : attValue.toString();
	}

	private VCFReader<VEPMetaData, VEPVariant> createVCFReader(VariantsEffectPredictionResults vep, Path userPath)
			throws MalformedURLException {
		Path vepFilePath = userPath.resolve(vep.getFilePath());
		File vepFile = configuration.getUserDataBaseDirectory().toPath().resolve(vepFilePath).toFile();

		System.out.println("created vcf reader for file "+vepFile.toURI().toURL());
		return new VCFReader<>(
				vepFile.toURI().toURL(), new VEPMetaDataBuilder(),
				new VEPVariantDataBuilder());
	}

}
