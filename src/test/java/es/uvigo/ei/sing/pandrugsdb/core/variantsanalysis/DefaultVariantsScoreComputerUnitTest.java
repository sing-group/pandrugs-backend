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

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.expectNew;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.easymock.*;
import org.easymock.internal.RuntimeExceptionWrapper;
import org.hamcrest.CoreMatchers;
import org.junit.*;
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
public class DefaultVariantsScoreComputerUnitTest extends EasyMockSupport {

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

	private Path aBasePath = Paths.get("/tmp");
	private Path aVCF = Paths.get("input.vcf");
	
	@Before
	public void createComputer() {
		computer = new DefaultVariantsScoreComputer(effectPredictor, vEPtoVariantsScoreCalculator, Executors.newFixedThreadPool(1));
	}

	@After
	public void verifyAll() {
		super.verifyAll(); //verify easy mocks
		PowerMock.verifyAll();
	}

	@Test
	public void testCreateComputation() throws Exception {

		// VEP results mock
		VariantsEffectPredictionResults VEPRs = 
				EasyMock.createMock(VariantsEffectPredictionResults.class);
		
		// effectPredictor mock
		expect(effectPredictor.predictEffect(aVCF, aBasePath)).andAnswer(() -> {
			Thread.sleep(200);
			return VEPRs;
		});

		
		// variants score calculator mock
		expect(vEPtoVariantsScoreCalculator.calculateVariantsScore(VEPRs, aBasePath))
			.andReturn(results);


		// computation
		expectNew(DefaultVariantsScoreComputation.class).andReturn(computation);
		Capture<Future> capturedTasks = EasyMock.newCapture();
		computation.wrapFuture(capture(capturedTasks));
		expectLastCall().times(1);
		expect(computation.get()).andReturn(results).anyTimes();


		expect(computation.getStatus()).andReturn(status);
		expectLastCall().anyTimes();
		
		status.setTaskName(anyString());
		expectLastCall().times(4);
		status.setTaskProgress(EasyMock.anyDouble());
		expectLastCall().times(3);
		status.setOverallProgress(EasyMock.anyDouble());
		expectLastCall().times(2);
		status.setOverallProgress(1.0f);
		expectLastCall().times(1);

		// easy mock replay
		super.replayAll();

		// power mock replay
		PowerMock.replay(
			DefaultVariantsScoreComputation.class,
			VariantsScoreComputationStatus.class,
			VariantsScoreComputationResults.class
		);
		
		final VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setVcfFile(aVCF);
		parameters.setResultsBasePath(aBasePath);
		
		final VariantsScoreComputation computation = 
				computer.createComputation(parameters);
		
		assertThat(results, is(computation.get()));

		// wait in get()
		assertThat(capturedTasks.getValue().get(), is(results));
	}

	@Test
	public void testVEPError() throws ExecutionException, InterruptedException {
		// effectPredictor mock
		expect(effectPredictor.predictEffect(anyObject(), anyObject()))
				.andAnswer(() -> {
					Thread.sleep(200);
					throw new RuntimeException(new RuntimeException("exception in VEP"));
				});

		super.replayAll();

		final VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setVcfFile(aVCF);
		parameters.setResultsBasePath(aBasePath);

		VariantsScoreComputation computation = computer.createComputation(parameters);

		try	{
			// wait
			computation.get();
			fail("We expect the computation to throw an exception in get");

		} catch(ExecutionException e) {
			//we expect to be here
			assertThat(e.getCause().getCause().getMessage(), is("exception in VEP"));
			assertThat(computation.getStatus().getTaskName(), is("Finished-Error"));
			assertThat(computation.getStatus().isFinished(), is(true));
		}
	}
}
