/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.persistence.entity;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class VariantsScoreComputationDetails {

	@Embedded
	private VariantsScoreComputationParameters parameters = 
		new VariantsScoreComputationParameters();
	
	@Embedded
	private VariantsScoreComputationStatus status = 
		new VariantsScoreComputationStatus();
	
	@Embedded
	private VariantsScoreComputationResults results = null;

	public VariantsScoreComputationDetails() {	}

	VariantsScoreComputationDetails(
			VariantsScoreComputationParameters parameters,
			VariantsScoreComputationStatus status,
			VariantsScoreComputationResults results) {
		this.parameters = parameters;
		this.status = status;
		this.results = results;
	}

	public VariantsScoreComputationParameters getParameters() {
		return parameters;
	}

	public void setParameters(VariantsScoreComputationParameters parameters) {
		this.parameters = parameters;
	}

	public VariantsScoreComputationStatus getStatus() {
		return status;
	}

	public void setStatus(VariantsScoreComputationStatus status) {
		this.status = status;
	}

	public VariantsScoreComputationResults getResults() {
		return results;
	}

	public void setResults(VariantsScoreComputationResults results) {
		this.results = results;
	}
	
	
}
