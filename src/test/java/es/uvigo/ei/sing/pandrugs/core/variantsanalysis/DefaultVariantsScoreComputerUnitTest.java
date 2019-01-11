/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2019 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
 * and Miguel Reboiro-Jato
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

package es.uvigo.ei.sing.pandrugs.core.variantsanalysis;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.expectNew;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.DefaultVariantsScoreComputer.DefaultVariantsScoreComputation;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsEffectPredictionResults;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationResults;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationStatus;

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
	

	@Mock
	private ExecutorService thisThreadExecutorService;
	private DefaultVariantsScoreComputer computer;

	private Path aBasePath = Paths.get("/tmp");
	private Path aVCF = Paths.get("input.vcf");
	
	@Before
	public void createComputer() {
		Capture<Runnable> taskCapture = EasyMock.newCapture();
		thisThreadExecutorService.execute(EasyMock.capture(taskCapture));
		EasyMock.expectLastCall().andAnswer(
			() -> {
				taskCapture.getValue().run();
				return null;
			}
		);

		computer = new DefaultVariantsScoreComputer(effectPredictor, vEPtoVariantsScoreCalculator,thisThreadExecutorService);
	}

	@After
	public void verifyAll() {
		super.verifyAll(); //verify easy mocks
		PowerMock.verifyAll();
	}

	@Test
	public void testCreateComputation() throws Exception {
		// VEP results mock
		VariantsEffectPredictionResults VEPRs = createMock(VariantsEffectPredictionResults.class);
		
		// effectPredictor mock
		expect(effectPredictor.predictEffect(aVCF, aBasePath)).andAnswer(() -> {
			Thread.sleep(200);
			return VEPRs;
		});

		final VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setVcfFile(aVCF);
		parameters.setResultsBasePath(aBasePath);

		// variants score calculator mock
		expect(vEPtoVariantsScoreCalculator.calculateVariantsScore(parameters, VEPRs))
			.andReturn(results);


		// computation
		expectNew(DefaultVariantsScoreComputation.class).andReturn(computation);
		Capture<Future<VariantsScoreComputationResults>> capturedTasks = newCapture();
		computation.wrapFuture(capture(capturedTasks));
		expectLastCall().times(1);
		expect(computation.get()).andReturn(results).anyTimes();


		expect(computation.getStatus()).andReturn(status);
		expectLastCall().anyTimes();

		status.setStatus("Submitted", 0.0, 0.0);
		expectLastCall().once();
		status.setTaskName("Computing VEP");
		expectLastCall().once();
		status.setStatus("Computing Variant Scores", 0.0, 0.5);
		expectLastCall().once();
		status.setStatus("Annotation Process Finished", 0.0, 1.0);
		expectLastCall().once();

		// easy mock replay
		super.replayAll();

		// power mock replay
		PowerMock.replay(
			DefaultVariantsScoreComputation.class,
			VariantsScoreComputationStatus.class,
			VariantsScoreComputationResults.class
		);

		final VariantsScoreComputation computation = computer.createComputation(parameters);
		
		assertThat(results, is(computation.get()));

		// wait in get()
		assertThat(capturedTasks.getValue().get(), is(results));
	}

	@Test(timeout = 5000)
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

		// listen for status change
		final AtomicBoolean expectedStatusArrived = new AtomicBoolean(false);
		computation.getStatus().onChange((status) -> {
			if (status.getTaskName().equals("Error")) {
				synchronized (DefaultVariantsScoreComputerUnitTest.this) {
					expectedStatusArrived.set(true);
					DefaultVariantsScoreComputerUnitTest.this.notify();
				}
			}
		});

		try	{
			// wait
			computation.get();
			fail("We expect the computation to throw an exception in get");
		} catch(ExecutionException e) {
			//we expect to be here
			assertThat(e.getCause().getCause().getMessage(), is("exception in VEP"));

			if (! computation.getStatus().getTaskName().equals("Error")) {
				synchronized (this) {
					if (!expectedStatusArrived.get()) {
						this.wait();
					}
				}
			}
			assertThat(computation.getStatus().getTaskName(), is("Error"));
			assertThat(computation.getStatus().isFinished(), is(true));
			assertThat(computation.getStatus().hasErrors(), is(true));
		}
	}

	@Test(timeout = 5000)
	public void testVEPtoVariantsError() throws ExecutionException, InterruptedException {
		// VEP results mock
		VariantsEffectPredictionResults VEPRs = createMock(VariantsEffectPredictionResults.class);

		// effectPredictor mock
		expect(effectPredictor.predictEffect(aVCF, aBasePath)).andAnswer(() -> {
			Thread.sleep(200);
			return VEPRs;
		});

		// variants score calculator mock
		expect(vEPtoVariantsScoreCalculator.calculateVariantsScore(anyObject(), eq(VEPRs)))
				.andThrow(new RuntimeException(new RuntimeException("Error in VScore computation")));

		super.replayAll();

		final VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setVcfFile(aVCF);
		parameters.setResultsBasePath(aBasePath);

		VariantsScoreComputation computation = computer.createComputation(parameters);

		// listen for status change
		final AtomicBoolean expectedStatusArrived = new AtomicBoolean(false);
		computation.getStatus().onChange((status) -> {
			if (status.getTaskName().equals("Error")) {
				synchronized (DefaultVariantsScoreComputerUnitTest.this) {
					expectedStatusArrived.set(true);
					DefaultVariantsScoreComputerUnitTest.this.notify();
				}
			}
		});

		try	{
			// wait
			computation.get();
			fail("We expect the computation to throw an exception in get");
		} catch(ExecutionException e) {
			//we expect to be here
			assertThat(e.getCause().getCause().getMessage(), is("Error in VScore computation"));

			if (! computation.getStatus().getTaskName().equals("Error")) {
				synchronized (this) {
					if (!expectedStatusArrived.get()) {
						this.wait();
					}
				}
			}
			assertThat(computation.getStatus().getTaskName(), is("Error"));
			assertThat(computation.getStatus().isFinished(), is(true));
			assertThat(computation.getStatus().hasErrors(), is(true));
		}
	}

	@Test(timeout = 5000)
	public void testVEPInterrupted() throws ExecutionException, InterruptedException {
		// effectPredictor mock
		expect(effectPredictor.predictEffect(anyObject(), anyObject()))
			.andAnswer(() -> {
				Thread.sleep(200);
				throw new RuntimeException(new InterruptedException("exception in VEP"));
			});

		super.replayAll();

		final VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setVcfFile(aVCF);
		parameters.setResultsBasePath(aBasePath);

		VariantsScoreComputation computation = computer.createComputation(parameters);

		// listen for status change
		final AtomicBoolean expectedStatusArrived = new AtomicBoolean(false);
		computation.getStatus().onChange((status) -> {
			if (status.getTaskName().equals("Interrupted")) {
				synchronized (DefaultVariantsScoreComputerUnitTest.this) {
					expectedStatusArrived.set(true);
					DefaultVariantsScoreComputerUnitTest.this.notify();
				}
			}
		});

		try	{
			// wait
			computation.get();
			fail("We expect the computation to throw an exception in get");
		} catch(ExecutionException e) {
			//we expect to be here
			assertThat(e.getCause().getCause().getMessage(), is("exception in VEP"));

			if (! computation.getStatus().getTaskName().equals("Interrupted")) {
				synchronized (this) {
					if (!expectedStatusArrived.get()) {
						this.wait();
					}
				}
			}
			assertThat(computation.getStatus().getTaskName(), is("Interrupted"));
			assertThat(computation.getStatus().isFinished(), is(false));
			assertThat(computation.getStatus().hasErrors(), is(false));
		}
	}

	@Test(timeout = 5000)
	public void testVEPtoVariantsInterrupted() throws ExecutionException, InterruptedException {
		// VEP results mock
		VariantsEffectPredictionResults VEPRs = createMock(VariantsEffectPredictionResults.class);

		// effectPredictor mock
		expect(effectPredictor.predictEffect(aVCF, aBasePath)).andAnswer(() -> {
			Thread.sleep(200);
			return VEPRs;
		});

		// variants score calculator mock
		expect(vEPtoVariantsScoreCalculator.calculateVariantsScore(anyObject(), eq(VEPRs)))
				.andThrow(new RuntimeException(new InterruptedException("Error in VScore computation")));

		super.replayAll();

		final VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setVcfFile(aVCF);
		parameters.setResultsBasePath(aBasePath);

		VariantsScoreComputation computation = computer.createComputation(parameters);

		// listen for status change
		final AtomicBoolean expectedStatusArrived = new AtomicBoolean(false);
		computation.getStatus().onChange((status) -> {
			if (status.getTaskName().equals("Interrupted")) {
				synchronized (DefaultVariantsScoreComputerUnitTest.this) {
					expectedStatusArrived.set(true);
					DefaultVariantsScoreComputerUnitTest.this.notify();
				}
			}
		});

		try	{
			// wait
			computation.get();
			fail("We expect the computation to throw an exception in get");
		} catch(ExecutionException e) {
			//we expect to be here
			assertThat(e.getCause().getCause().getMessage(), is("Error in VScore computation"));

			if (! computation.getStatus().getTaskName().equals("Interrupted")) {
				synchronized (this) {
					if (!expectedStatusArrived.get()) {
						this.wait();
					}
				}
			}

			assertThat(computation.getStatus().getTaskName(), is("Interrupted"));
			assertThat(computation.getStatus().isFinished(), is(false));
			assertThat(computation.getStatus().hasErrors(), is(false));
		}
	}
}
