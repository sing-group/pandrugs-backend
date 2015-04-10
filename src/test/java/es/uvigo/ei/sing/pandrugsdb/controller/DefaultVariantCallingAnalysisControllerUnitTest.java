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
import static org.easymock.EasyMock.createMock;
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
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis.ComputationsStore;
import es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis.VariantCallingCandidateDrugComputation;
import es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis.VariantCallingCandidateDrugComputer;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.service.entity.CandidateDrug;

@RunWith(EasyMockRunner.class)
public class DefaultVariantCallingAnalysisControllerUnitTest {

	@Mock
	private ComputationsStore store;

	@Mock
	private VariantCallingCandidateDrugComputer computer;

	
	@TestSubject
	private VariantCallingAnalysisController controller = new DefaultVariantCallingAnalysisController();

	private final User aUser = new User("login", "login@domain.com",
			"926e27eecdbc7a18858b3798ba99bddd", ADMIN);
	final URL aVCF = getClass().getResource("sampleVCF_31variants.vcf");

	@Test
	public void testExperimentIsStoredWhenStart() {

		Capture<VariantCallingCandidateDrugComputation> capturedArgument = newCapture();
		store.storeComputation(capture(capturedArgument), eq(aUser));
		replay(store);

		final VariantCallingCandidateDrugComputation computation = controller
				.startCandidateDrugsComputation(aUser, aVCF);

		assertEquals(computation, capturedArgument.getValue());
	}

	@Test
	public void testGetComputations() {

		final List<VariantCallingCandidateDrugComputation> expected = new ArrayList<>();
		expected.add(createMock(VariantCallingCandidateDrugComputation.class));
		expected.add(createMock(VariantCallingCandidateDrugComputation.class));

		expect(store.retrieveComputations(aUser)).andReturn(expected);
		replay(store);

		assertThat(controller.getComputations(aUser),
				hasTheSameItemsAs(expected));
	}

	@Test
	public void testComputerCallAndResults() throws InterruptedException, ExecutionException {

		final List<CandidateDrug> expectedDrugs = new ArrayList<>();
			expectedDrugs.add(createMock(CandidateDrug.class));
			expectedDrugs.add(createMock(CandidateDrug.class));
		
		final VariantCallingCandidateDrugComputation expectedComputation = 
				createMock(VariantCallingCandidateDrugComputation.class);
		
		expect(expectedComputation.get()).andReturn(expectedDrugs);
		replay(expectedComputation);
		
		expect(computer.createComputation(aVCF)).andReturn(expectedComputation);
		replay(computer);
		
		final VariantCallingCandidateDrugComputation computation = controller
				.startCandidateDrugsComputation(aUser, aVCF);
		assertThat(expectedDrugs, hasTheSameItemsAs(computation.get()));
		
	}
}
