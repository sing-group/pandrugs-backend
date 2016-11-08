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
package es.uvigo.ei.sing.pandrugsdb.controller.entity;

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.DriverLevel.CANDIDATE_DRIVER;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.DriverLevel.HIGH_CONFIDENCE_DRIVER;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.HIGHLY_SIGNIFICANTLY_MUTATED;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.NEAR_SIGNIFICANCE;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.SIGNIFICANTLY_MUTATED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DriverLevel;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel;

@RunWith(Parameterized.class)
public class GeneGScoreTest extends EasyMockSupport {
	private final static double ALLOWED_ERROR = 0.00001d;
	
	private final Gene gene;
	private final double gScore;

	public GeneGScoreTest(
		String geneSymbol,
		TumorPortalMutationLevel tumorPortalMutationLevel,
		boolean cgc,
		DriverLevel driverLevel,
		Double geneEssentialityScore,
		double gScore
	) {
		this.gene = new Gene(
			geneSymbol, tumorPortalMutationLevel, cgc, driverLevel,
			geneEssentialityScore, false
		);
		this.gScore = gScore;
	}
	
	@Parameters(name = "{0}")
	public static Object[][] parameters() {
		return new Object[][] {
			{ "NoScore", null, false, null, null, 0d },
			{ "TPM_NS", NEAR_SIGNIFICANCE, false, null, null, NEAR_SIGNIFICANCE.getWeight()},
			{ "TPM_SM", SIGNIFICANTLY_MUTATED, false, null, null, SIGNIFICANTLY_MUTATED.getWeight() },
			{ "TPM_HSM", HIGHLY_SIGNIFICANTLY_MUTATED, false, null, null, HIGHLY_SIGNIFICANTLY_MUTATED.getWeight() },
			{ "CGC", null, true, null, null, 0.2d },
			{ "DL_CD", null, false, CANDIDATE_DRIVER, null, CANDIDATE_DRIVER.getWeight() },
			{ "DL_HCD", null, false, HIGH_CONFIDENCE_DRIVER, null, HIGH_CONFIDENCE_DRIVER.getWeight() },
			{ "GES_0.0", null, false, null, 0d, 0d },
			{ "GES_0.5", null, false, null, 0.5d, 0.2d },
			{ "GES_1.0", null, false, null, 1.0d, 0.4d },
			{ "LOW", NEAR_SIGNIFICANCE, false, CANDIDATE_DRIVER, 0.125d, 0.2d },
			{ "MEDIUM", SIGNIFICANTLY_MUTATED, true, CANDIDATE_DRIVER, 0.5d, 0.6d },
			{ "HIGHEST", HIGHLY_SIGNIFICANTLY_MUTATED, true, HIGH_CONFIDENCE_DRIVER, 1d, 1d }
		};
	}
	
	@Test
	public void testSingle() {
		assertThat(this.gene.getGScore(), is(closeTo(gScore, ALLOWED_ERROR)));
	}
}
