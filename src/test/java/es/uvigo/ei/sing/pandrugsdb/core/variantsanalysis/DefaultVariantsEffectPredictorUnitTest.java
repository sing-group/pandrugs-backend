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

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsEffectPredictionResults;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	DefaultVariantsEffectPredictor.class,
		})
public class DefaultVariantsEffectPredictorUnitTest {

	@Rule	
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock
	private FileSystemConfiguration fileSystemConfiguration;
	
	
	@TestSubject
	private DefaultVariantsEffectPredictor vepPredictor = 
		new DefaultVariantsEffectPredictor();

	private String aVCFResourcePath = "sampleVCF_31variants.vcf";
	
	private String inputVCFName = "input.vcf";
	
	private File userStorageDirectory = new File(System.getProperty("java.io.tmpdir"));
	private File expectedResultsFile;
	private File computationDir;
	private File vcfFile;
	
	private String computationBasePath = UUID.randomUUID().toString();
	
	@Before
	public void createUserDir() throws IOException {
		System.out.println("creating user dir: "+computationBasePath);
		
		this.computationDir = new File(userStorageDirectory.getAbsolutePath()+File.separator+this.computationBasePath);
		this.expectedResultsFile = new File(computationDir.getAbsolutePath()+File.separator+DefaultVariantsEffectPredictor.VEP_FILE_NAME);
		this.vcfFile = new File(this.computationDir+File.separator+inputVCFName);
		
		computationDir.mkdir();
		copyVCF();
	}
	
	private void copyVCF() throws IOException {
		
		copyInputStreamToFile(getClass().getResourceAsStream(aVCFResourcePath), vcfFile);
	}

	@After
	public void removeUserDir() throws IOException {
		System.out.println("removing user dir: "+computationBasePath);
		this.expectedResultsFile.delete();
		this.vcfFile.delete();
		this.computationDir.delete();
	}
	
	@Test
	public void testResultsFileAreCreatedAndReferenced() {
		
		expect(fileSystemConfiguration.getUserDataBaseDirectory()).andReturn(userStorageDirectory);
		replay(fileSystemConfiguration);
		
		VariantsEffectPredictionResults results = vepPredictor.predictEffect(Paths.get(inputVCFName), Paths.get(computationBasePath));

		assertEquals(DefaultVariantsEffectPredictor.VEP_FILE_NAME, results.getFilePath().toString());
		assertTrue(expectedResultsFile.exists());
		
	}
}
