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

package es.uvigo.ei.sing.pandrugs.controller.entity;

import static es.uvigo.ei.sing.pandrugs.persistence.entity.DriverLevel.CANDIDATE_DRIVER;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.DriverLevel.HIGH_CONFIDENCE_DRIVER;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.newGene;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.TumorPortalMutationLevel.HIGHLY_SIGNIFICANTLY_MUTATED;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.TumorPortalMutationLevel.NEAR_SIGNIFICANCE;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.TumorPortalMutationLevel.SIGNIFICANTLY_MUTATED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import es.uvigo.ei.sing.pandrugs.persistence.entity.DriverLevel;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugs.persistence.entity.OncodriveRole;
import es.uvigo.ei.sing.pandrugs.persistence.entity.TumorPortalMutationLevel;

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
		boolean ccle,
		Double oncoscapeScore,
		OncodriveRole oncodriveRole,
		double gScore
	) {
		this.gene = newGene(
			geneSymbol, tumorPortalMutationLevel, cgc, driverLevel,
			geneEssentialityScore, ccle, oncodriveRole, oncoscapeScore
		);
		this.gScore = gScore;
	}
	
	@Parameters(name = "{0}")
	public static Object[][] parameters() {
		return new Object[][] {
			{ "NoScore", null, false, null, null, false, 0d, OncodriveRole.NONE, 0d },
			{ "TPM_NS", NEAR_SIGNIFICANCE, false, null, null, false, 0d, OncodriveRole.NONE, NEAR_SIGNIFICANCE.getWeight() },
			{ "TPM_SM", SIGNIFICANTLY_MUTATED, false, null, null, false, 0d, OncodriveRole.NONE, SIGNIFICANTLY_MUTATED.getWeight() },
			{ "TPM_HSM", HIGHLY_SIGNIFICANTLY_MUTATED, false, null, null, false, 0d, OncodriveRole.NONE, HIGHLY_SIGNIFICANTLY_MUTATED.getWeight() },
			{ "CGC", null, true, null, null, false, 0d, OncodriveRole.NONE, 0.1d },
			{ "DL_CD", null, false, CANDIDATE_DRIVER, null, false, 0d, OncodriveRole.NONE, CANDIDATE_DRIVER.getWeight() },
			{ "DL_HCD", null, false, HIGH_CONFIDENCE_DRIVER, null, false, 0d, OncodriveRole.NONE, HIGH_CONFIDENCE_DRIVER.getWeight() },
			{ "GES_0.1", null, false, null, 0.1d, false, 0d, OncodriveRole.NONE, 0.04d },
			{ "GES_0.5", null, false, null, 0.5d, false, 0d, OncodriveRole.NONE, 0.2d },
			{ "GES_1.0", null, false, null, 1.0d, false, 0d, OncodriveRole.NONE, 0.4d },
			{ "OS_1", null, false, null, 0d, false, 1d, OncodriveRole.NONE, 0.075d },
			{ "OS_2", null, false, null, 0d, false, 2d, OncodriveRole.NONE, 0.15d },
			{ "OS_3", null, false, null, 0d, false, 4d, OncodriveRole.NONE, 0.3d },
			{ "LOW", NEAR_SIGNIFICANCE, false, CANDIDATE_DRIVER, 0.125d, false, 0d, OncodriveRole.NONE, 0.125d },
			{ "MEDIUM", SIGNIFICANTLY_MUTATED, true, CANDIDATE_DRIVER, 0.5d, false, 2d, OncodriveRole.LOSS_OF_FUNCTION, 0.55d },
			{ "HIGHEST", HIGHLY_SIGNIFICANTLY_MUTATED, true, HIGH_CONFIDENCE_DRIVER, 1d, true, 4d, OncodriveRole.ACTIVATING, 1d }
		};
	}
	
	@Test
	public void testSingle() {
		assertThat(this.gene.getGScore(), is(closeTo(gScore, ALLOWED_ERROR)));
	}
}
