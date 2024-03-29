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
import javax.persistence.Embedded;
import javax.persistence.Transient;

@Embeddable
public class VariantsScoreComputationResults {
	
	@Column(name="results_vscore_file")
	private String vscoreFileName;
	
	@Column(name="results_affectedgenes_file")
	private String affectedGenesFileName;

	@Transient
	private Path vscorePath;
	
	@Transient
	private Path affectedGenesPath;
	
	@Embedded
	private VariantsEffectPredictionResults vepResults;

	@Embedded
	private PharmCatResults pharmCatResults;
	
	public VariantsScoreComputationResults(VariantsEffectPredictionResults vepResults, Path vscorePath,
			Path affectedGenesPath
	) {
		this.vepResults = vepResults;
		this.pharmCatResults =  new PharmCatResults();

		if (vscorePath != null) {
			this.vscoreFileName = vscorePath.toString();
			this.vscorePath = vscorePath;
		}

		if (affectedGenesPath != null) {
			this.affectedGenesFileName = affectedGenesPath.toString();
			this.affectedGenesPath = affectedGenesPath;
		}
	}
	
	protected VariantsScoreComputationResults() {}

	public Path getVscorePath() {
		return (vscoreFileName != null) ? Paths.get(vscoreFileName) : null;
	}

	public Path getAffectedGenesPath() {
		return (affectedGenesFileName != null) ? Paths.get(affectedGenesFileName) : null;
	}

	public VariantsEffectPredictionResults getVepResults() {
		return vepResults;
	}

	public PharmCatResults getPharmCatResults() {
		return pharmCatResults;
	}

	public void setPharmCatResults(PharmCatResults pharmCatResults) {
		this.pharmCatResults = pharmCatResults;
	}
}
