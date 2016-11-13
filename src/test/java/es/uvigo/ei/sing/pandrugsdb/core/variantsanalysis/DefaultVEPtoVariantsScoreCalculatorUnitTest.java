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
package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsEffectPredictionResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationResults;

public class DefaultVEPtoVariantsScoreCalculatorUnitTest extends EasyMockSupport {
	@Rule	
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock
	private FileSystemConfiguration fileSystemConfiguration;
	
	private File aBaseDirectory = new File(System.getProperty("java.io.tmpdir"));
	
	@TestSubject
	private DefaultVEPtoVariantsScoreCalculator defaultVEPToVariantsScoreCalculator = 
		new DefaultVEPtoVariantsScoreCalculator();

	private File vepFile;
	private File expectedResultsFile;

	private File userDir;
	private String userName;

	@Before
	public void createUserDir() throws IOException {
		this.userName = UUID.randomUUID().toString();
		this.userDir = new File(aBaseDirectory.getAbsolutePath() + File.separator + userName);
		this.expectedResultsFile = new File(userDir.getAbsolutePath() + File.separator
			+ DefaultVEPtoVariantsScoreCalculator.VARIANT_SCORES_FILE_NAME);
		this.vepFile = new File(
			userDir.getAbsolutePath() + File.separator + DefaultVariantsEffectPredictor.VEP_FILE_NAME);
		FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("vep-small.txt"), this.vepFile);
		userDir.mkdir();
	}

	@After
	public void verifyAll() {
		super.verifyAll();
	}

	@After
	public void removeUserDir() throws IOException {
		this.vepFile.delete();
		this.expectedResultsFile.delete();
		this.userDir.delete();
	}
	
	@Test
	public void testResultsFileAreCreatedAndReferenced() {
		expect(fileSystemConfiguration.getUserDataBaseDirectory()).andReturn(aBaseDirectory).anyTimes();

		super.replayAll();

		VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setResultsBasePath(Paths.get(userName));
		VariantsEffectPredictionResults vepResults = new VariantsEffectPredictionResults(
			Paths.get(DefaultVariantsEffectPredictor.VEP_FILE_NAME));

		VariantsScoreComputationResults results = defaultVEPToVariantsScoreCalculator.calculateVariantsScore(parameters,
			vepResults);

		assertEquals(DefaultVEPtoVariantsScoreCalculator.VARIANT_SCORES_FILE_NAME, results.getVscorePath().toString());
		assertTrue(expectedResultsFile.exists());
	}
}
