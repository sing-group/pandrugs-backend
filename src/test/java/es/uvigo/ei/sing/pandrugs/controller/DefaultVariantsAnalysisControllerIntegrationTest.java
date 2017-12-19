/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugs.TestServletContext;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.DefaultVEPConfiguration;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;
import es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreUserComputationDataset;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRank;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugs.service.entity.UserLogin;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DirtiesContext
@DatabaseSetup(value = {
	"file:src/test/resources/META-INF/dataset.user.xml",
	"file:src/test/resources/META-INF/dataset.variantanalysis.xml"
})
@DatabaseTearDown(value = "file:src/test/resources/META-INF/dataset.variantanalysis.xml",
		type = DatabaseOperation.DELETE_ALL)
public class DefaultVariantsAnalysisControllerIntegrationTest {

	@Inject
	private ServletContext context;

	@Inject
	@Named("defaultVariantsAnalysisController")
	private DefaultVariantsAnalysisController controller;

	private static final String A_VCF_RESOURCE_PATH = "../core/variantsanalysis/sampleVCF_31variants.vcf";
	private static final String A_VEP_RESOURCE_PATH = "../core/variantsanalysis/vep-small.txt";

	@BeforeClass
	public static void initContext() throws IOException {
		TestServletContext.INIT_PARAMETERS.put("user.data.directory", System.getProperty("java.io.tmpdir"));

		// the vep command template is mocked as a simple "cp [test.vep] %2$s"
		File tempVepFile = File.createTempFile("vep-small", ".csv");
		FileUtils.copyInputStreamToFile(
			DefaultVariantsAnalysisControllerIntegrationTest.class.getResourceAsStream(A_VEP_RESOURCE_PATH),
			tempVepFile
		);
		TestServletContext.INIT_PARAMETERS.put(DefaultVEPConfiguration.VEP_COMMAND_TEMPLATE_PARAMETER,
			"cp " + tempVepFile.getAbsolutePath() + " %2$s"
		);
	}

	@Before
	public void prepareComputationFilesStorage() throws IOException {
		String systemTmpDir = System.getProperty("java.io.tmpdir");

		// extracts the files available as resources and copies them
		// to a temporary directory
		VariantsScoreUserComputationDataset.copyComputationFilesToDir(systemTmpDir);
	}


	@Test
	@ExpectedDatabase(
		value = "dataset.variantanalysis.create.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
		override = false
	)
	public void testStartAndWaitForVariantsScoreComputation() throws InterruptedException, URISyntaxException, IOException {
		final User aUser = UserDataset.users()[0];

		final String id = controller.startVariantsScopeUserComputation(new UserLogin(aUser.getLogin()),
				openComputationFileStream(A_VCF_RESOURCE_PATH), UUID.randomUUID().toString());

		waitWhileOrFail(() ->!controller.getComputationStatus(id).isFinished(), 10000);
	}

	@Test
	public void testGetComputationStatus() throws InterruptedException {
		assertFalse(controller.getComputationStatus("1").isFinished());
		assertFalse(controller.getComputationStatus("1").isFailed());
		assertThat(controller.getComputationStatus("1").getOverallProgress(), is(0.5));
		assertThat(controller.getComputationStatus("1").getTaskProgress(), is(0.0));
		assertThat(controller.getComputationStatus("1").getTaskName(), is("Computing Variant Scores"));

		assertTrue(controller.getComputationStatus("2").isFinished());
		assertFalse(controller.getComputationStatus("2").isFailed());
		assertThat(controller.getComputationStatus("2").getOverallProgress(), is(1.0));
		assertThat(controller.getComputationStatus("2").getTaskProgress(), is(1.0));
		assertThat(controller.getComputationStatus("2").getTaskName(), is("Annotation Process Finished"));
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.variantanalysis.resume.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
		override = false
	)
	public void testComputationsAreResumedAndFinish() throws InterruptedException, IOException {
		final User aUser = UserDataset.users()[0];
		
		createRemainingTasksInputFiles(aUser);
		
		controller.onApplicationEvent(null); //resume computations

		// wait for computations
		waitWhileOrFail(() ->!controller.getComputationStatus("1").isFinished(), 10000);
	}

	@Test
	public void testGetAffectedGenesRanking() {
		final GeneRanking geneRanking = controller.getGeneRankingForComputation("2");
		final List<GeneRank> geneRank = geneRanking.getGeneRank();
		
		assertThat(geneRank, hasSize(6));
		assertThat(geneRank.get(0).getGene(), is("ZNF891"));
		assertThat(geneRank.get(1).getGene(), is("CARD16"));
		assertThat(geneRank.get(2).getGene(), is("CWC25"));
		assertThat(geneRank.get(3).getGene(), is("KCNH5"));
		assertThat(geneRank.get(4).getGene(), is("TSKS"));
		assertThat(geneRank.get(5).getGene(), is("TPO"));
		assertThat(geneRank.get(0).getRank(), is(0.262));
		assertThat(geneRank.get(1).getRank(), is(0.3145));
		assertThat(geneRank.get(2).getRank(), is(0.3322));
		assertThat(geneRank.get(3).getRank(), is(0.3743));
		assertThat(geneRank.get(4).getRank(), is(0.375));
		assertThat(geneRank.get(5).getRank(), is(0.4161));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetAffectedGenesRankingOfNonCompletedComputation() {
		controller.getGeneRankingForComputation("1").getGeneRank();
	}

	@Test
	public void testGetVariantsScoreFile() throws FileNotFoundException {
		final File variantsScoreFile = controller.getVariantsScoreFile("2");

		assertThat(variantsScoreFile.exists(), is(true));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetVariantsScoreFileOfNonCompletedComputation() throws FileNotFoundException {
		final File variantsScoreFile = controller.getVariantsScoreFile("1");
	}

	private void waitWhileOrFail(BooleanSupplier condition, long timeout) throws InterruptedException {
		waitWhile(condition, 10000);
		
		assertFalse(condition.getAsBoolean());
	}

	private void waitWhile(BooleanSupplier condition, long timeout) throws InterruptedException {
		long startTime = System.currentTimeMillis();
		while (condition.getAsBoolean() &&
			   System.currentTimeMillis() - startTime < timeout) {
			Thread.sleep(100);
		}
	}

	private void createRemainingTasksInputFiles(final User aUser) throws IOException {
		File dir = new File(context.getInitParameter("user.data.directory") + File.separator + "pepe-1");
		dir.mkdir();
		
		File inputFile = new File(dir.getAbsolutePath() + File.separator + "inputVCF.vcf");
		FileUtils.touch(inputFile);
	}

	private InputStream openComputationFileStream(String name) {
		return getClass().getResourceAsStream(name);
	}
}