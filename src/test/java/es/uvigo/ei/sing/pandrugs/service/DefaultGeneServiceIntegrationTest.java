/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2022 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

package es.uvigo.ei.sing.pandrugs.service;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.HasHttpStatus.hasOkStatus;
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToGeneInteraction.containsGeneInteraction;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.absentGeneSymbols;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.geneSymbolsWithInteractionDegreeUpTo;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.geneSymbolsWithMaxInteractionDegree;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.interactions;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.presentGeneSymbol;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.presentGeneSymbols;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugs.service.entity.GeneInteraction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@Transactional
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DirtiesContext
@DatabaseSetup("file:src/test/resources/META-INF/dataset.gene.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.gene.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultGeneServiceIntegrationTest {
	@Inject
	@Named("defaultGeneService")
	private GeneService service;
	
	@Test
	public void testGetGeneInteractions() {
		for (String queryGene : presentGeneSymbols()) {
			for (int degree = 0; degree < 5; degree++) {
				testGetInteractions(degree, queryGene);
			}
		}
	}
	
	@Test
	public void testGetGenesInteractionsWithMaxDegree() {
		for (int degree = 0; degree <= 3; degree++) {
			testGetInteractions(degree, geneSymbolsWithMaxInteractionDegree(degree));
		}
	}
	
	@Test
	public void testGetGenesInteractionsWithDegreeUpTo() {
		for (int degree = 0; degree <= 5; degree++) {
			testGetInteractions(degree, geneSymbolsWithInteractionDegreeUpTo(degree));
		}
	}
	
	@Test(expected = BadRequestException.class)
	public void testGetGeneInteractionsNegativeDegree() {
		service.getGeneInteractions(presentGeneSymbol(), -1);
	}
	
	@Test(expected = BadRequestException.class)
	public void testGetGeneInteractionsEmptyGeneSymbol() {
		service.getGeneInteractions("", 10);
	}
	
	@Test(expected = BadRequestException.class)
	public void testGetGeneInteractionsNullGeneSymbol() {
		service.getGeneInteractions(null, 10);
	}
	
	@Test(expected = BadRequestException.class)
	public void testGetGeneInteractionsAbsentGeneSymbol() {
		service.getGeneInteractions(absentGeneSymbol(), 10);
	}
	
	@Test(expected = BadRequestException.class)
	public void testGetGenesInteractionsNegativeDegree() {
		service.getGenesInteractions(asList(presentGeneSymbols()), -1);
	}
	
	@Test(expected = BadRequestException.class)
	public void testGetGenesInteractionsEmptyGeneSymbols() {
		service.getGenesInteractions(emptyList(), 10);
	}
	
	@Test(expected = BadRequestException.class)
	public void testGetGenesInteractionsNullGeneSymbols() {
		service.getGenesInteractions(null, 10);
	}
	
	@Test(expected = BadRequestException.class)
	public void testGetGenesInteractionsAbsentGeneSymbols() {
		service.getGenesInteractions(asList(absentGeneSymbols()), 10);
	}

	private void testGetInteractions(int degree, String ... queryGene) {
		final Response response;
		
		if (queryGene.length == 1) {
			response = service.getGeneInteractions(queryGene[0], degree);
		} else {
			response = service.getGenesInteractions(asList(queryGene), degree);
		}
		
		assertThat(response, hasOkStatus());
		assertThat(
			asList((GeneInteraction[]) response.getEntity()),
			containsGeneInteraction(interactions(degree, queryGene))
		);
	}
}
