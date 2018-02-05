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

import static es.uvigo.ei.sing.pandrugs.persistence.entity.RoleType.ADMIN;
import static es.uvigo.ei.sing.pandrugs.util.EmptyInputStream.emptyInputStream;
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
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

import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.FileSystemConfiguration;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.VariantsScoreComputation;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.VariantsScoreComputer;
import es.uvigo.ei.sing.pandrugs.mail.Mailer;
import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDAO;
import es.uvigo.ei.sing.pandrugs.persistence.dao.UserDAO;
import es.uvigo.ei.sing.pandrugs.persistence.dao.VariantsScoreUserComputationDAO;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;
import es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsEffectPredictionResults;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationDetails;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationResults;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationStatus;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreUserComputation;
import es.uvigo.ei.sing.pandrugs.service.entity.ComputationMetadata;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugs.service.entity.UserLogin;

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

	@Mock
	private Mailer mailer;

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
				new UserLogin(aUser.getLogin()), new ByteArrayInputStream(anVCFFileContent().getBytes()), UUID
						.randomUUID()
						.toString());

		assertEquals(aUser, capturedArgument.getValue().getUser());
		assertEquals(capturedParameters.getValue(), capturedArgument.getValue().getComputationDetails().getParameters());
		assertEquals((Integer)4, capturedParameters.getValue().getNumberOfInputVariants());
	}
	
	@Test
	public void testExperimentResultsAreStoredWhenFinish() throws InterruptedException, ExecutionException, IOException {

		Capture<VariantsScoreUserComputation> capturedVariantsScoreUserComputation =
				newCapture();
		expect(userDAO.get(aUser.getLogin())).andReturn(aUser);

		variantsScoreUserComputationDAO.storeComputation(capture(capturedVariantsScoreUserComputation));
		expect(variantsScoreUserComputationDAO.update(capture(capturedVariantsScoreUserComputation))).andStubAnswer(() ->	capturedVariantsScoreUserComputation.getValue());

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

		mailer.sendComputationFinished(anyObject(VariantsScoreUserComputation.class));
		expectLastCall();

		super.replayAll();

		//controller.startVariantsScoreComputation(aUser, parameters);
		controller.startVariantsScopeUserComputation(
				new UserLogin(aUser.getLogin()), emptyInputStream(), UUID.randomUUID().toString());

		aStatus.setOverallProgress(1.0f);
		assertEquals(expectedResults, capturedVariantsScoreUserComputation.getValue().getComputationDetails().getResults());
	}

	@Test
	public void testGetComputationStatus() {
		VariantsScoreComputationDetails details = new VariantsScoreComputationDetails();
		details.getStatus().setTaskName("a task");
		VariantsScoreUserComputation aComputation = new VariantsScoreUserComputation("1", null, details);

		String anId = "1";

		expect(variantsScoreUserComputationDAO.get(anId)).andReturn(aComputation);

		super.replayAll();

		ComputationMetadata metadata = controller.getComputationStatus(anId);

		assertThat(metadata.getTaskName(), is("a task"));
	}

	@Test
	public void testComputerCallAndResults() throws InterruptedException, ExecutionException, IOException {


		final VariantsScoreComputationResults expectedResults = mockControl
				.createMock(VariantsScoreComputationResults.class);
		expect(expectedResults.getAffectedGenesPath()).andReturn(Paths.get("affected_genes.txt")).times(4);

		final VariantsScoreComputation expectedComputation = mockControl.createMock(VariantsScoreComputation.class);

		final VariantsScoreComputationStatus aStatus = new VariantsScoreComputationStatus();
		
		Capture<VariantsScoreUserComputation> capturedVariantsScoreUserComputation = newCapture();


		expect(userDAO.get(aUser.getLogin())).andReturn(aUser);

		variantsScoreUserComputationDAO.storeComputation(capture(capturedVariantsScoreUserComputation));
		expect(variantsScoreUserComputationDAO.update(capture(capturedVariantsScoreUserComputation)))
				.andStubAnswer(() -> capturedVariantsScoreUserComputation.getValue());
		expect(variantsScoreUserComputationDAO.get(anyString())).andStubAnswer(() -> capturedVariantsScoreUserComputation.getValue());

		expect(expectedComputation.get()).andReturn(expectedResults).anyTimes();
		expect(expectedComputation.getStatus()).andReturn(aStatus).anyTimes();
		expect(computer.createComputation(anyObject())).andReturn(expectedComputation);

		Capture<VariantsScoreUserComputation> capturedVariantsScoreUserComputationForMail = newCapture();
		mailer.sendComputationFinished(capture(capturedVariantsScoreUserComputationForMail));
		expectLastCall();

		super.replayAll();
		//controller.startVariantsScoreComputation(aUser, parameters);
		String id = controller.startVariantsScopeUserComputation(
				new UserLogin(aUser.getLogin()), emptyInputStream(), UUID.randomUUID().toString());

		// provoke finish of computation, listeners will be called and computation
		// and the results should be setted in usercomputation.details
		createFileWithContents("affected_genes.txt", capturedVariantsScoreUserComputation.getValue().getComputationDetails()
				.getParameters().getResultsBasePath().toString(), anAffectedGenesFileContent());
		aStatus.setOverallProgress(1.0);

		assertThat(controller.getComputationStatus(id).isFinished(), is(true));
		assertThat(controller.getComputationStatus(id).getAffectedGenesInfo().get("KCNH5").get("branch"), is
				("UNCLASSIFIED"));
		assertThat(capturedVariantsScoreUserComputation.getValue(),
				is(capturedVariantsScoreUserComputationForMail.getValue()));
	}

	@Test
	public void testGetRankingForComputation() throws IOException {
		testGeneRankings(
			anAffectedGenesFileContent(),
			new double[] { 0.2620, 0.3145, 0.3322, 0.3743, 0.3750, 0.4161 }
		);
	}

	@Test
	public void testInputVariantsForComputation() throws IOException {
		testInputVariants(anVCFFileContent());
	}

	@Test
	public void testDeleteComputation() throws IOException {
		VariantsScoreUserComputation aComputation = prepareFinishedComputation(anAffectedGenesFileContent(), anVCFFileContent());
		String anyId = "1";

		expect(this.variantsScoreUserComputationDAO.get(anyId)).andReturn(aComputation);
		this.variantsScoreUserComputationDAO.remove(aComputation);
		expectLastCall().once();

		super.replayAll();

		controller.deleteComputation(anyId);
	}

	@Test(expected=IllegalStateException.class)
	public void testDeleteNonFinishedComputation() throws IOException {
		VariantsScoreUserComputation aComputation = prepareFinishedComputation(anAffectedGenesFileContent(), anVCFFileContent());
		String anyId = "1";

		aComputation.getComputationDetails().getStatus().setOverallProgress(0.5);

		expect(this.variantsScoreUserComputationDAO.get(anyId)).andReturn(aComputation);

		super.replayAll();

		controller.deleteComputation(anyId);
	}

	@Test
	public void testComputationsForUser() throws IOException {
		User aUser = UserDataset.anyUser();

		VariantsScoreUserComputation aComputation = prepareFinishedComputation(anAffectedGenesFileContent(), anVCFFileContent());

		expect(this.userDAO.get(aUser.getLogin())).andReturn(aUser);
		expect(this.variantsScoreUserComputationDAO.retrieveComputationsBy(aUser)).andReturn(asList(aComputation));

		this.replayAll();

		Map<String, ComputationMetadata> computations =  controller.getComputationsForUser(new UserLogin(aUser.getLogin
				()));

		assertThat(computations.values().iterator().next().getVariantsInInput(), is(4));
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

	private String anVCFFileContent() {
		StringBuilder sb = new StringBuilder();

		sb.append("##fileformat=VCFv4.1\n");
		sb.append("##INFO=<ID=AC,Number=A,Type=Integer,Description=\"Allele count in genotypes, for each ALT allele, in the same order as listed\">\n");
		sb.append("##INFO=<ID=AF,Number=A,Type=Float,Description=\"Allele Frequency, for each ALT allele, in the same order as listed\">\n");
		sb.append("##INFO=<ID=AN,Number=1,Type=Integer,Description=\"Total number of alleles in called genotypes\">\n");
		sb.append("##INFO=<ID=DB,Number=0,Type=Flag,Description=\"dbSNP Membership\">\n");
		sb.append("##INFO=<ID=DP,Number=1,Type=Integer,Description=\"Approximate read depth; some reads may have been filtered\">\n");
		sb.append("##INFO=<ID=MQ0,Number=1,Type=Integer,Description=\"Total Mapping Quality Zero Reads\">\n");
		sb.append("#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO\n");
		sb.append("chr1	109479864	.	GG	TT	255.0	PASS	AC=1;AF=0.500;AN=2;DB;DP=220;MQ0=0\n");
		sb.append("chr1	152277111	.	C	T	255.0	PASS	AC=1;AF=0.500;AN=2;DB;DP=220;MQ0=0\n");
		sb.append("chr1	152283563	.	C	T	255.0	PASS	AC=1;AF=0.500;AN=2;DB;DP=220;MQ0=0\n");
		sb.append("chr1	152944374	.	C	T	255.0	PASS	AC=1;AF=0.500;AN=2;DB;DP=220;MQ0=0\n");

		return sb.toString();

	}
	private void testGeneRankings(String geneRankingContents, double[] expectedRankings) throws
			IOException {
		VariantsScoreUserComputation aComputation = prepareFinishedComputation(geneRankingContents, "");

		String anyId = "1";

		expect(this.variantsScoreUserComputationDAO.get(anyId)).andReturn(aComputation);

		super.replayAll();

		GeneRanking ranking = controller.getGeneRankingForComputation(anyId);


		assertThat(ranking.getGeneRank().size(), is(expectedRankings.length));

		for (int i = 0; i < expectedRankings.length; i++) {
				assertThat(ranking.getGeneRank().get(i).getRank(), is(expectedRankings[i]));
		}
	}

	private void testInputVariants(String vcfContents) throws
			IOException {
		VariantsScoreUserComputation aComputation = prepareFinishedComputation("", vcfContents);

		String anyId = "1";

		expect(this.variantsScoreUserComputationDAO.get(anyId)).andReturn(aComputation);

		super.replayAll();

		ComputationMetadata status = controller.getComputationStatus(anyId);

		assertThat(status.getVariantsInInput(), is(4));

	}
	private VariantsScoreUserComputation prepareFinishedComputation(String geneRankingContents, String vcfContent)
			throws
			IOException {
		String affectedGenesFileName = "affected_genes.txt";
		String basePath = "results";
		createFileWithContents(affectedGenesFileName, basePath, geneRankingContents);

		String vcfFileName = "input.vcf";
		createFileWithContents(vcfFileName, basePath, vcfContent);

		VariantsEffectPredictionResults aVEPResults = new VariantsEffectPredictionResults(Paths.get("vep_results.txt"));
		VariantsScoreUserComputation aComputation = new VariantsScoreUserComputation(UUID.randomUUID().toString());
		aComputation.getComputationDetails().getStatus().setOverallProgress(1.0);

		aComputation.getComputationDetails().getParameters().setResultsBasePath(Paths.get(basePath));

		aComputation.getComputationDetails().getParameters().setVcfFile(Paths.get(vcfFileName));

		aComputation.getComputationDetails().getParameters().setNumberOfInputVariants(4);

		VariantsScoreComputationResults results = new VariantsScoreComputationResults(aVEPResults,
			Paths.get("vscore.txt"),
			Paths.get(affectedGenesFileName)
		);

		aComputation.getComputationDetails().setResults(results);
		return aComputation;
	}

	private void createFileWithContents(String fileName, String basePath, String content)
			throws IOException {

		File outFile = new File(
			aBaseDirectory + File.separator + basePath + File.separator + fileName);

		outFile.deleteOnExit();

		FileUtils.write(outFile, content);
	}
}
