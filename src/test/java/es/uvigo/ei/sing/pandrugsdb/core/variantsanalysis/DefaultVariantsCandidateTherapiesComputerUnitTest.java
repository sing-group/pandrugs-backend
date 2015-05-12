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

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.expectNew;

import java.net.URL;

import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.DefaultVariantsCandidateTherapiesComputer.AbstractVariantCallingCandidateTherapiesComputation;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
		AbstractVariantCallingCandidateTherapiesComputation.class,
		DefaultVariantsCandidateTherapiesComputer.class
		})
public class DefaultVariantsCandidateTherapiesComputerUnitTest {

	@Rule	
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock
	private VariantEffectPredictor effectPredictor;
	
	@Mock
	private CandidateTherapiesCalculator candidateTherapyCalculator; 
	
	@Mock
	AbstractVariantCallingCandidateTherapiesComputation	computation;
	
	@Mock	
	CandidateTherapiesComputationResults results;
	
	@TestSubject
	private DefaultVariantsCandidateTherapiesComputer computer = 
		new DefaultVariantsCandidateTherapiesComputer();

	
	private final URL aVCF = getClass().getResource("sampleVCF_31variants.vcf");
	
	@Test
	public void testCreateComputation() throws Exception {
		
		// VEP results mock
		VariantsEffectPredictionResults VEPRs = 
				EasyMock.createMock(VariantsEffectPredictionResults.class);
		
		// effectPredictor mock
		expect(effectPredictor.predictEffect(aVCF)).andReturn(VEPRs);
		replay(effectPredictor);

		// CandidateTherapies results mock
		final CandidateTherapy[] expectedResults = new CandidateTherapy[] {
			createMock(CandidateTherapy.class),
			createMock(CandidateTherapy.class)
		};
		
		// candidateTherapyCalculator mock
		expect(candidateTherapyCalculator.calculateTherapies(VEPRs))
			.andReturn(asList(expectedResults));
		replay(candidateTherapyCalculator);
		
		// computation mock
		Class<AbstractVariantCallingCandidateTherapiesComputation> 
			candidateTherapiesCompClass =
				AbstractVariantCallingCandidateTherapiesComputation.class;
		expectNew(candidateTherapiesCompClass).andReturn(computation);
		computation.wrapFuture(EasyMock.anyObject());
		expectLastCall().times(1);
		computation.setCurrentTaskName(anyString());
		expectLastCall().times(3);
		computation.setOverallProgress(EasyMock.anyDouble());
		expectLastCall().times(3);
		computation.setCurrentTaskProgress(EasyMock.anyDouble());
		expectLastCall().times(3);
		expect(computation.get()).andReturn(results).times(1);
		replay(computation);
		
		//results mock
		expectNew(CandidateTherapiesComputationResults.class, 
				asList(expectedResults), VEPRs)
		.andReturn(results);
		expect(results.getCandidateTherapies()).andReturn(asList(expectedResults));
		replay(results);
		
		// mock configuration finished		
		PowerMock.replay(
				candidateTherapiesCompClass, 
				CandidateTherapiesComputationResults.class 
				);
		
		final VariantsCandidateTherapiesComputation computation = 
				computer.createComputation(aVCF);
		
		assertThat(computation.get().getCandidateTherapies(), containsInAnyOrder(expectedResults));
		
		PowerMock.verifyAll();
	}
	
}
