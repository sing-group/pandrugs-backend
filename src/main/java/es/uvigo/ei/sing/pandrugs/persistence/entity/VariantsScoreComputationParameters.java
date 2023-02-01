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

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class VariantsScoreComputationParameters {

	@Column(name = "parameter_vcf_file")
	private String vcfFile;

	@Column(name = "parameter_number_of_input_variants")
	private Integer numberOfInputVariants;

	@Column(name = "parameter_base_path")
	private String basePath;

	@Column(name = "results_url_template")
	private String resultsURLTemplate;

	@Column(name = "parameter_consequence_filter_active")
	private boolean consequenceFilterActive = true;

	@Column(name = "parameter_gene_frequency_threshold")
	private double geneFrequencyThreshold = 0.0d;

	@Transient
	private Path resultsBasePath;

	public VariantsScoreComputationParameters() { }

	public VariantsScoreComputationParameters(String vcfFile, String basePath) {
		this.vcfFile = vcfFile;
		this.basePath = basePath;
	}

	public VariantsScoreComputationParameters(String vcfFile, String basePath, String resultsURLTemplate) {
		this.vcfFile = vcfFile;
		this.basePath = basePath;
		this.resultsURLTemplate = resultsURLTemplate;
	}

	public Path getVcfFile() {
		return Paths.get(this.vcfFile);
	}
	
	public void setVcfFile(Path vcfFile) {
		this.vcfFile = vcfFile.toString();
	}

	public Integer getNumberOfInputVariants() {
		return numberOfInputVariants;
	}

	public void setNumberOfInputVariants(Integer numberOfInputVariants) {
		this.numberOfInputVariants = numberOfInputVariants;
	}

	public void setResultsBasePath(Path resultsBasePath) {
		this.resultsBasePath = resultsBasePath;
		this.basePath = resultsBasePath.toString();
	}
	
	public Path getResultsBasePath() {
		return Paths.get(this.basePath);
	}

	public String getResultsURLTemplate() {
		return resultsURLTemplate;
	}

	public void setResultsURLTemplate(String resultsURLTemplate) {
		this.resultsURLTemplate = resultsURLTemplate;
	}

	public boolean isConsequenceFilterActive() {
		return consequenceFilterActive;
	}

	public void setConsequenceFilterActive(boolean consequenceFilterActive) {
		this.consequenceFilterActive = consequenceFilterActive;
	}

	public double getGeneFrequencyThreshold() {
		return geneFrequencyThreshold;
	}

	public void setGeneFrequencyThreshold(double geneFrequencyThreshold) {
		this.geneFrequencyThreshold = geneFrequencyThreshold;
	}
}
