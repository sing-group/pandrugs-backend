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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasTheSameItemsAsMatcher.hasTheSameItemsAs;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.RoleType.ADMIN;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.easymock.Capture;
import org.easymock.EasyMockRunner;
import org.easymock.IMocksControl;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.CandidateTherapiesComputationResults;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.ComputationsStore;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsCandidateTherapiesComputation;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsCandidateTherapiesComputer;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

@RunWith(EasyMockRunner.class)
public class DefaultVariantsAnalysisControllerUnitTest {

	@Mock
	private ComputationsStore store;

	@Mock
	private VariantsCandidateTherapiesComputer computer;
	
	private IMocksControl mockControl = createControl();
	
	@TestSubject
	private VariantsAnalysisController controller = 
		new DefaultVariantCallingAnalysisController();

	private final User aUser = new User("login", "login@domain.com",
			"926e27eecdbc7a18858b3798ba99bddd", ADMIN);
	
	private final URL aVCF = getClass().getResource("sampleVCF_31variants.vcf");

	@Test
	public void testExperimentIsStoredWhenStart() {

		Capture<VariantsCandidateTherapiesComputation> capturedArgument = 
				newCapture();
		store.storeComputation(capture(capturedArgument), eq(aUser));
		replay(store);

		final VariantsCandidateTherapiesComputation computation = 
				controller.startCandidateTherapiesComputation(aUser, aVCF);

		assertEquals(computation, capturedArgument.getValue());
	}

	@Test
	public void testGetComputations() {

		final List<VariantsCandidateTherapiesComputation> expected = 
				new ArrayList<>();
		expected.add(
				mockControl.createMock(
						VariantsCandidateTherapiesComputation.class));
		expected.add(
				mockControl.createMock(
						VariantsCandidateTherapiesComputation.class));
				
		expect(store.retrieveComputations(aUser)).andReturn(expected);
		replay(store);

		assertThat(controller.getComputations(aUser),
				hasTheSameItemsAs(expected));
	}

	@Test
	public void testComputerCallAndResults() throws InterruptedException, ExecutionException {

		final CandidateTherapiesComputationResults expectedDrugs = 
				mockControl.createMock(
						CandidateTherapiesComputationResults.class);
		
		final VariantsCandidateTherapiesComputation expectedComputation = 
				mockControl.createMock(
						VariantsCandidateTherapiesComputation.class);
		
		expect(expectedComputation.get()).andReturn(expectedDrugs);
		replay(expectedComputation);
		
		expect(computer.createComputation(aVCF)).andReturn(expectedComputation);
		replay(computer);
		
		final VariantsCandidateTherapiesComputation computation = controller
				.startCandidateTherapiesComputation(aUser, aVCF);
		
		assertEquals(expectedDrugs, computation.get());
		
	}
}
