package es.uvigo.ei.sing.pandrugsdb.controller.entity;

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.DriverLevel.CANDIDATE_DRIVER;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.DriverLevel.HIGH_CONFIDENCE_DRIVER;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.HIGHLY_SIGNIFICANTLY_MUTATED;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.NEAR_SIGNIFICANCE;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.SIGNIFICANTLY_MUTATED;
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DriverLevel;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformation;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel;

@RunWith(Parameterized.class)
public class GeneDrugGroupGScoreTest extends EasyMockSupport {
	private final static double ALLOWED_ERROR = 0.00001d;
	
	private final GeneInformation geneInfo;
	private final double gScore;

	public GeneDrugGroupGScoreTest(
		String geneSymbol,
		TumorPortalMutationLevel tumorPortalMutationLevel,
		boolean cgc,
		DriverLevel driverLevel,
		Double geneEssentialityScore,
		double gScore
	) {
		this.geneInfo = new GeneInformation(
			geneSymbol, tumorPortalMutationLevel, cgc, driverLevel,
			geneEssentialityScore
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
		final GeneDrug gd = createNiceMock(GeneDrug.class);
		expect(gd.getGeneSymbol())
			.andReturn(this.geneInfo.getGeneSymbol())
		.atLeastOnce();
		expect(gd.getGeneInformation())
			.andReturn(this.geneInfo)
			.atLeastOnce();
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(
			new String[] {this.geneInfo.getGeneSymbol()}, asList(gd)
		);
		
		assertThat(group.getGScore(gd), is(closeTo(gScore, ALLOWED_ERROR)));
	}
}
