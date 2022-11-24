/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2022 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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
public class VariantsEffectPredictionResults {

	@Column(name="results_vep_file")
	private String fileName;
	
	@Transient
	private Path filePath;
	
	protected VariantsEffectPredictionResults() {}
	
	public VariantsEffectPredictionResults(Path filePath) {
		this.fileName = filePath.toString();
		this.filePath = filePath;
	}

	public Path getFilePath() {		
		return (this.fileName != null) ? Paths.get(this.fileName) : null;
	}
	
	public void setFilePath(Path filePath) {
		this.fileName = filePath.toString();
		this.filePath = filePath;
	}
}
