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

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.expectNew;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.DefaultVariantsScoreComputer.DefaultVariantsScoreComputation;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsEffectPredictionResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationResults;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
		DefaultVariantsScoreComputation.class,
		DefaultVariantsScoreComputer.class
		})
public class DefaultVariantsScoreComputerUnitTest {

	@Rule	
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock
	private VariantsEffectPredictor effectPredictor;
	
	@Mock
	private VEPtoVariantsScoreCalculator vEPtoVariantsScoreCalculator; 
	
	@Mock
	private DefaultVariantsScoreComputation	computation;
	
	@Mock
	private VariantsScoreComputationStatus status;
	
	@Mock	
	private VariantsScoreComputationResults results;
	

	private DefaultVariantsScoreComputer computer;

	private Path aVCF = Paths.get("input.vcf");
	
	@Before
	public void createComputer() {
		computer = new DefaultVariantsScoreComputer(effectPredictor, vEPtoVariantsScoreCalculator, Executors.newFixedThreadPool(1));
	}
	
	@Test
	public void testCreateComputation() throws Exception {
		
		Path aBasePath = Paths.get("/tmp");
		// VEP results mock
		VariantsEffectPredictionResults VEPRs = 
				EasyMock.createMock(VariantsEffectPredictionResults.class);
		
		// effectPredictor mock
		expect(effectPredictor.predictEffect(aVCF, aBasePath)).andReturn(VEPRs);
		replay(effectPredictor);

		
		// variants score calculator mock
		expect(vEPtoVariantsScoreCalculator.calculateVariantsScore(VEPRs, aBasePath))
			.andReturn(results);
		replay(vEPtoVariantsScoreCalculator);
		
		
		expectNew(VariantsScoreComputationStatus.class).andReturn(status);

		// computation
		computation.wrapFuture(EasyMock.anyObject());		
		expectLastCall().times(1);
		
		expect(computation.getStatus()).andReturn(status);
		expectLastCall().anyTimes();
		
		status.setTaskName(anyString());
		expectLastCall().times(3);
		status.setTaskProgress(EasyMock.anyDouble());
		expectLastCall().times(3);
		status.setOverallProgress(EasyMock.anyDouble());
		expectLastCall().times(2);		
		status.setOverallProgress(1.0f);
		expectLastCall().times(1);
		replay(computation);
		replay(status);
		
		// mock configuration finished		
		PowerMock.replay(
				VariantsScoreComputationStatus.class,
				VariantsScoreComputationResults.class 
				);
		
		final VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setVcfFile(aVCF);
		parameters.setResultsBasePath(aBasePath);
		
		final VariantsScoreComputation computation = 
				computer.createComputation(parameters);
		
		assertThat(results, CoreMatchers.is(computation.get()));
		
		PowerMock.verifyAll();
	}
	
}
