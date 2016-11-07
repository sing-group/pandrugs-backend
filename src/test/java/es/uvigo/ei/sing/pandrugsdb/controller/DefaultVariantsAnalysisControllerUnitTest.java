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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.RoleType.ADMIN;
import static es.uvigo.ei.sing.pandrugsdb.util.EmptyInputStream.emptyInputStream;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.easymock.Capture;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.IMocksControl;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.FileSystemConfiguration;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsScoreComputation;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsScoreComputer;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneInformationDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.UserDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.VariantsScoreUserComputationDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsEffectPredictionResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationDetails;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreUserComputation;
import es.uvigo.ei.sing.pandrugsdb.service.entity.ComputationStatusMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;

@RunWith(EasyMockRunner.class)
public class DefaultVariantsAnalysisControllerUnitTest extends EasyMockSupport {

	@Mock
	private VariantsScoreUserComputationDAO variantsScoreUserComputationDAO;

	@Mock
	private GeneInformationDAO geneInformationDAO;

	@Mock
	private UserDAO userDAO;

	@Mock
	private VariantsScoreComputer computer;
	
	@Mock
	private FileSystemConfiguration fileSystemConfiguration;
	
	private File aBaseDirectory = new File(System.getProperty("java.io.tmpdir"));
	
	private IMocksControl mockControl = createControl();
	
	@TestSubject
	private VariantsAnalysisController controller = 
		new DefaultVariantsAnalysisController();

	private final User aUser = new User("login", "login@domain.com", "926e27eecdbc7a18858b3798ba99bddd", ADMIN);
	

	@Before
	public void configureBasePathMock() {
		expect(fileSystemConfiguration.getUserDataBaseDirectory()).andReturn(aBaseDirectory).anyTimes();
	}

	@After
	public void verifyAll() {
		super.verifyAll();
	}

	@Test
	public void testExperimentIsStoredWhenStart() throws Exception {
		Capture<VariantsScoreUserComputation> capturedArgument = newCapture();
		Capture<VariantsScoreComputationParameters> capturedParameters = newCapture();

		expect(userDAO.get(aUser.getLogin())).andReturn(aUser);

		variantsScoreUserComputationDAO.storeComputation(capture(capturedArgument));
		expect(variantsScoreUserComputationDAO.update(capture(capturedArgument))).andStubAnswer(() ->	capturedArgument.getValue());

		final VariantsScoreComputation expectedComputation = mockControl.createMock(VariantsScoreComputation.class);
		final VariantsScoreComputationStatus aStatus = new VariantsScoreComputationStatus();
		
		expect(computer.createComputation(capture(capturedParameters))).andReturn(expectedComputation);
		expect(expectedComputation.getStatus()).andReturn(aStatus).anyTimes();

		super.replayAll();

		controller.startVariantsScopeUserComputation(new UserLogin(aUser.getLogin()), emptyInputStream());

		assertEquals(aUser, capturedArgument.getValue().getUser());
		assertEquals(capturedParameters.getValue(), capturedArgument.getValue().getComputationDetails().getParameters());
	}
	
	@Test
	public void testExperimentResultsAreStoredWhenFinish() throws InterruptedException, ExecutionException, IOException {

		Capture<VariantsScoreUserComputation> capturedArgument = 
				newCapture();

		expect(userDAO.get(aUser.getLogin())).andReturn(aUser);

		variantsScoreUserComputationDAO.storeComputation(capture(capturedArgument));
		expect(variantsScoreUserComputationDAO.update(capture(capturedArgument))).andStubAnswer(() ->	capturedArgument.getValue());

		final VariantsScoreComputation expectedComputation =
				mockControl.createMock(
						VariantsScoreComputation.class);
		final VariantsScoreComputationStatus aStatus = new VariantsScoreComputationStatus();
		
		final VariantsScoreComputationResults expectedResults = mockControl.createMock(
				VariantsScoreComputationResults.class);
		
		//expect(parameters.getVcfFile()).andReturn(aVCF);
		//replay(parameters);
		
		expect(computer.createComputation(anyObject())).andReturn(expectedComputation);
		expect(expectedComputation.getStatus()).andReturn(aStatus).anyTimes();
		expect(expectedComputation.get()).andReturn(expectedResults).anyTimes();

		super.replayAll();

		//controller.startVariantsScoreComputation(aUser, parameters);
		controller.startVariantsScopeUserComputation(new UserLogin(aUser.getLogin()), emptyInputStream());

		aStatus.setOverallProgress(1.0f);
		assertEquals(expectedResults, capturedArgument.getValue().getComputationDetails().getResults());
	}

	@Test
	public void testGetComputationStatus() {
		VariantsScoreComputationDetails details = new VariantsScoreComputationDetails();
		details.getStatus().setTaskName("a task");
		VariantsScoreUserComputation aComputation = new VariantsScoreUserComputation(1, null, details);

		int anId = 1;

		expect(variantsScoreUserComputationDAO.get(anId)).andReturn(aComputation);

		super.replayAll();

		ComputationStatusMetadata metadata = controller.getComputationsStatus(anId);

		assertThat(metadata.getTaskName(), is("a task"));

	}

	@Test
	public void testComputerCallAndResults() throws InterruptedException, ExecutionException, IOException {
		final VariantsScoreComputationResults expectedResults = mockControl
				.createMock(VariantsScoreComputationResults.class);
		
		final VariantsScoreComputation expectedComputation = mockControl.createMock(VariantsScoreComputation.class);
		

		final VariantsScoreComputationStatus aStatus = new VariantsScoreComputationStatus();
		
		Capture<VariantsScoreUserComputation> capturedArgument = newCapture();

		expect(userDAO.get(aUser.getLogin())).andReturn(aUser);

		variantsScoreUserComputationDAO.storeComputation(capture(capturedArgument));
		expect(variantsScoreUserComputationDAO.update(capture(capturedArgument)))
				.andStubAnswer(() -> capturedArgument.getValue());
		expect(variantsScoreUserComputationDAO.get(anyInt())).andStubAnswer(() -> capturedArgument.getValue());

		expect(expectedComputation.get()).andReturn(expectedResults).anyTimes();
		expect(expectedComputation.getStatus()).andReturn(aStatus).anyTimes();
		expect(computer.createComputation(anyObject())).andReturn(expectedComputation);

		super.replayAll();

		//controller.startVariantsScoreComputation(aUser, parameters);
		int id = controller.startVariantsScopeUserComputation(new UserLogin(aUser.getLogin()), emptyInputStream());

		// provoke finish of computation, listeners will be called and computation
		// and the results should be setted in usercomputation.details
		aStatus.setOverallProgress(1.0);

		assertThat(controller.getComputationsStatus(id).isFinished(), is(true));
	}

	@Test
	public void testGetRankingForComputation() throws IOException {
		int anyId = 1;
		String affectedGenesFileName = "affected_genes.txt";
		File genesAffectedFile = new File(
			aBaseDirectory + File.separator + "results" + File.separator + affectedGenesFileName);

		genesAffectedFile.deleteOnExit();

		FileUtils.write(genesAffectedFile, "GENE1\t1.0\nGENE2\t2.4");

		VariantsEffectPredictionResults aVEPResults = new VariantsEffectPredictionResults(Paths.get("vep_results.txt"));
		VariantsScoreUserComputation aComputation = new VariantsScoreUserComputation();
		aComputation.getComputationDetails().getStatus().setOverallProgress(1.0);

		aComputation.getComputationDetails().getParameters().setResultsBasePath(Paths.get("results"));

		VariantsScoreComputationResults results = new VariantsScoreComputationResults(aVEPResults,
			Paths.get("vscore.txt"),
			Paths.get("affected_genes.txt")
		);

		aComputation.getComputationDetails().setResults(results);

		expect(this.variantsScoreUserComputationDAO.get(anyId)).andReturn(aComputation);

		super.replayAll();

		GeneRanking ranking = controller.getGeneRankingForComputation(anyId);

		assertThat(ranking.getGeneRank().size(), is(2));
		assertThat(ranking.getGeneRank().get(0).getRank(), is(1.0d));
		assertThat(ranking.getGeneRank().get(1).getRank(), is(2.4d));
	}
}
