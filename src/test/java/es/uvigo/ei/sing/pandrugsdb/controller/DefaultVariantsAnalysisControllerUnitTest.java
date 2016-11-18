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
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
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
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.UserDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.VariantsScoreUserComputationDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsEffectPredictionResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationDetails;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreUserComputation;
import es.uvigo.ei.sing.pandrugsdb.service.entity.ComputationMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;

@RunWith(EasyMockRunner.class)
public class DefaultVariantsAnalysisControllerUnitTest extends EasyMockSupport {

	@Mock
	private VariantsScoreUserComputationDAO variantsScoreUserComputationDAO;

	@Mock
	private GeneDAO geneDAO;

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

		controller.startVariantsScopeUserComputation(
				new UserLogin(aUser.getLogin()), emptyInputStream(), UUID.randomUUID().toString());

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
		controller.startVariantsScopeUserComputation(
				new UserLogin(aUser.getLogin()), emptyInputStream(), UUID.randomUUID().toString());

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

		ComputationMetadata metadata = controller.getComputationStatus(anId);

		assertThat(metadata.getTaskName(), is("a task"));

	}

	@Test
	public void testComputerCallAndResults() throws InterruptedException, ExecutionException, IOException {


		final VariantsScoreComputationResults expectedResults = mockControl
				.createMock(VariantsScoreComputationResults.class);
		expect(expectedResults.getAffectedGenesPath()).andReturn(Paths.get("affected_genes.txt"));

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
		int id = controller.startVariantsScopeUserComputation(
				new UserLogin(aUser.getLogin()), emptyInputStream(), UUID.randomUUID().toString());

		// provoke finish of computation, listeners will be called and computation
		// and the results should be setted in usercomputation.details

		createAffectedGenesFile("affected_genes.txt", capturedArgument.getValue().getComputationDetails()
				.getParameters().getResultsBasePath().toString(), anAffectedGenesFileContent());
		aStatus.setOverallProgress(1.0);

		assertThat(controller.getComputationStatus(id).isFinished(), is(true));
	}

	@Test
	public void testGetRankingForComputation() throws IOException {
		testGeneRankings(anAffectedGenesFileContent(), new double[]{0.3743, 0.4161, 0.3322, 0.2620, 0.3750, 0.3145});
	}

	@Test
	public void testDeleteComputation() throws IOException {
		VariantsScoreUserComputation aComputation = prepareFinishedComputation(anAffectedGenesFileContent());
		int anyId = 1;

		expect(this.variantsScoreUserComputationDAO.get(anyId)).andReturn(aComputation);
		this.variantsScoreUserComputationDAO.remove(aComputation);
		expectLastCall().once();

		super.replayAll();

		controller.deleteComputation(anyId);
	}

	@Test(expected=IllegalStateException.class)
	public void testDeleteNonFinishedComputation() throws IOException {
		VariantsScoreUserComputation aComputation = prepareFinishedComputation(anAffectedGenesFileContent());
		int anyId = 1;

		aComputation.getComputationDetails().getStatus().setOverallProgress(0.5);

		expect(this.variantsScoreUserComputationDAO.get(anyId)).andReturn(aComputation);

		super.replayAll();

		controller.deleteComputation(anyId);
	}

	@Test
	public void testComputationsForUser() throws IOException {
		User aUser = UserDataset.anyUser();

		VariantsScoreUserComputation aComputation = prepareFinishedComputation(anAffectedGenesFileContent());

		expect(this.userDAO.get(aUser.getLogin())).andReturn(aUser);
		expect(this.variantsScoreUserComputationDAO.retrieveComputationsBy(aUser)).andReturn(asList(aComputation));

		this.replayAll();

		controller.getComputationsForUser(new UserLogin(aUser.getLogin()));
	}

	private String anAffectedGenesFileContent() {
		StringBuilder sb = new StringBuilder();

		sb.append("gene_hgnc\tmax(vscore)\tbranch\tentrez_id\tpath_desc\tpath_id\n");
		sb.append("KCNH5\t0.3743\tUNCLASSIFIED\tKCNH5\t\t\n");
		sb.append("TPO\t0.4161\tUNCLASSIFIED\tTPO\tTyrosine metabolism|Metabolic pathways|Thyroid hormone " +
						"synthesis|Autoimmune thyroid disease\thsa00350|hsa01100|hsa04918|hsa05320\n");
		sb.append("CWC25\t0.3322\tUNCLASSIFIED\tCWC25\t\t\n");
		sb.append("ZNF891\t0.2620\tUNCLASSIFIED\tZNF891\t\t\n");
		sb.append("TSKS\t0.3750\tTSG\tTSKS\t\t\n");
		sb.append("CARD16\t0.3145\tUNCLASSIFIED\tCARD16\t\t\n");

		return sb.toString();
	}

	private void testGeneRankings(String geneRankingContents, double[] expectedRankings) throws IOException {
		VariantsScoreUserComputation aComputation = prepareFinishedComputation(geneRankingContents);

		int anyId = 1;

		expect(this.variantsScoreUserComputationDAO.get(anyId)).andReturn(aComputation);

		super.replayAll();

		GeneRanking ranking = controller.getGeneRankingForComputation(anyId);


		assertThat(ranking.getGeneRank().size(), is(expectedRankings.length));

		for (int i = 0; i < expectedRankings.length; i++) {
				assertThat(ranking.getGeneRank().get(i).getRank(), is(expectedRankings[i]));
		}
	}

	private VariantsScoreUserComputation prepareFinishedComputation(String geneRankingContents) throws IOException {
		String affectedGenesFileName = "affected_genes.txt";
		String basePath = "results";
		createAffectedGenesFile(affectedGenesFileName, basePath, geneRankingContents);

		VariantsEffectPredictionResults aVEPResults = new VariantsEffectPredictionResults(Paths.get("vep_results.txt"));
		VariantsScoreUserComputation aComputation = new VariantsScoreUserComputation();
		aComputation.getComputationDetails().getStatus().setOverallProgress(1.0);

		aComputation.getComputationDetails().getParameters().setResultsBasePath(Paths.get(basePath));

		VariantsScoreComputationResults results = new VariantsScoreComputationResults(aVEPResults,
			Paths.get("vscore.txt"),
			Paths.get("affected_genes.txt")
		);

		aComputation.getComputationDetails().setResults(results);
		return aComputation;
	}

	private void createAffectedGenesFile(String affectedGenesFileName, String basePath, String geneRankingContents)
			throws IOException {

		File genesAffectedFile = new File(
			aBaseDirectory + File.separator + basePath + File.separator + affectedGenesFileName);

		genesAffectedFile.deleteOnExit();

		FileUtils.write(genesAffectedFile, geneRankingContents);
	}
}
