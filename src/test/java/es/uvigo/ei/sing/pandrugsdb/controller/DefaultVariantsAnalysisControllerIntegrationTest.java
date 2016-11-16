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
package es.uvigo.ei.sing.pandrugsdb.controller;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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

import es.uvigo.ei.sing.pandrugsdb.TestServletContext;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.DefaultVEPConfiguration;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreUserComputation;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreUserComputationDataset;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;

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

	private static final String aVCFResourcePath = "../core/variantsanalysis/sampleVCF_31variants.vcf";
	private static final String aVEPResourcePath = "../core/variantsanalysis/vep-small.txt";

	@BeforeClass
	public static void initContext() throws IOException {
		TestServletContext.INIT_PARAMETERS.put("user.data.directory", System.getProperty("java.io.tmpdir"));

		// the vep command template is mocked as a simple "cp [test.vep] %2$s"
		File tempVepFile = File.createTempFile("vep-small", ".csv");
		FileUtils.copyInputStreamToFile(
			DefaultVariantsAnalysisControllerIntegrationTest.class.getResourceAsStream(aVEPResourcePath),
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
		for (VariantsScoreUserComputation computation : VariantsScoreUserComputationDataset.computations()) {
			File computationDir = new File(
				systemTmpDir +
				File.separator +
				computation.getComputationDetails().getParameters().getResultsBasePath().toString());

			if (!computationDir.exists()) {
				computationDir.mkdir();
			}

			if (computation.getComputationDetails().getParameters().getVcfFile() != null) {
				copyComputationFile("inputVCF.vcf", computationDir, computation);
			}
			if (computation.getComputationDetails().getResults().getVepResults().getFilePath() != null) {
				copyComputationFile("vep.txt", computationDir, computation);
			}
			if (computation.getComputationDetails().getResults().getAffectedGenesPath() != null) {
				copyComputationFile("genes_affected.csv", computationDir, computation);
			}
			if (computation.getComputationDetails().getResults().getVscorePath() != null) {
				copyComputationFile("vep_data.csv", computationDir, computation);
			}
		}
	}

	@Test
	@ExpectedDatabase(
		value = "dataset.variantanalysis.create.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
		override = false
	)
	public void testStartAndWaitForVariantsScoreComputation() throws InterruptedException, URISyntaxException, IOException {
		final User aUser = UserDataset.users()[0];

		int id = controller.startVariantsScopeUserComputation(new UserLogin(aUser.getLogin()),
				openComputationFileStream(aVCFResourcePath), UUID.randomUUID().toString());

		waitWhileOrFail(() ->!controller.getComputationsStatus(id).isFinished(), 10000);
	}

	@Test
	public void testGetComputationStatus() throws InterruptedException {
		assertFalse(controller.getComputationsStatus(1).isFinished());
		assertTrue(controller.getComputationsStatus(2).isFinished());
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
		waitWhileOrFail(() ->!controller.getComputationsStatus(1).isFinished(), 10000);
	}

	@Test
	public void testGetAffectedGenesRanking() {
		assertThat(controller.getGeneRankingForComputation(2).getGeneRank().get(0).getGene(), is("KCNH5"));
		assertThat(controller.getGeneRankingForComputation(2).getGeneRank().get(1).getGene(), is("TPO"));
		assertThat(controller.getGeneRankingForComputation(2).getGeneRank().get(0).getRank(), is(0.3743));
		assertThat(controller.getGeneRankingForComputation(2).getGeneRank().get(1).getRank(), is(0.4161));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetAffectedGenesRankingOfNonCompletedComputation() {
		controller.getGeneRankingForComputation(1).getGeneRank();
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

	private void copyComputationFile(String fileName, File computationDir, VariantsScoreUserComputation computation)
	throws IOException {
		copyInputStreamToFile(
			openComputationFileStream(
				"/META-INF/dataset.variantanalysis.xml.files/" +
				computation.getComputationDetails().getParameters().getResultsBasePath().toString() + "/" +
				fileName
			),
			new	File(computationDir.getAbsolutePath() + File.separator + fileName)
		);
	}

	private InputStream openComputationFileStream(String name) {
		return getClass().getResourceAsStream(name);
	}
}