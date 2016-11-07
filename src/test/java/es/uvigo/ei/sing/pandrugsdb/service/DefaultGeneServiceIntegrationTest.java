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
package es.uvigo.ei.sing.pandrugsdb.service;

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.IsEqualToGeneInteraction.containsGeneInteraction;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.absentGeneSymbols;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.geneSymbolsForQuery;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.genesWithInteractionDegreeUpTo;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.genesWithMaxInteractionDegree;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.interactions;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.presentGeneSymbol;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.presentGeneSymbols;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

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

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformation;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneInteraction;

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
	public void testListGeneSymbols() {
		final String query = "C";
		final int maxResults = 10;
		final String[] expected = geneSymbolsForQuery(query);
		
		assertThat(service.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsZeroMaxResults() {
		final String query = "C";
		final int maxResults = 0;
		final String[] expected = geneSymbolsForQuery(query);
		
		assertThat(service.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsNegativeMaxResults() {
		final String query = "C";
		final int maxResults = -1;
		final String[] expected = geneSymbolsForQuery(query);
		
		assertThat(service.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsMaxResults() {
		final String query = "C";
		final int maxResults = 1;
		final String[] expected = geneSymbolsForQuery(query, maxResults);
		
		assertThat(service.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsEmptyResults() {
		final String query = "XYZ";
		final int maxResults = 10;
		
		assertThat(service.listGeneSymbols(query, maxResults), is(emptyArray()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetGeneInteractionsNusNullQuery() {
		service.listGeneSymbols(null, 0);
	}
	
	@Test
	public void testGetGeneInteractions() {
		for (String queryGene : presentGeneSymbols()) {
			for (int degree = 0; degree < 5; degree++) {
				final GeneInformation[] expected = interactions(degree, queryGene);
				final GeneInteraction[] interactions = service.getGeneInteractions(queryGene, degree);
				
				assertThat(asList(interactions), containsGeneInteraction(expected));
			}
		}
	}
	
	@Test
	public void testGetGenesInteractionsWithMaxDegree() {
		for (int degree = 0; degree <= 3; degree++) {
			final List<String> queryGenes = asList(genesWithMaxInteractionDegree(degree));

			final GeneInformation[] expected = interactions(degree, queryGenes);
			final GeneInteraction[] interactions = service.getGenesInteractions(queryGenes, degree);
				
			assertThat(asList(interactions), containsGeneInteraction(expected));
		}
	}
	
	@Test
	public void testGetGenesInteractionsWithDegreeUpTo() {
		for (int degree = 0; degree <= 5; degree++) {
			final List<String> queryGenes = asList(genesWithInteractionDegreeUpTo(degree));

			final GeneInformation[] expected = interactions(degree, queryGenes);
			final GeneInteraction[] interactions = service.getGenesInteractions(queryGenes, degree);
			
			assertThat(asList(interactions), containsGeneInteraction(expected));
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGeneInteractionsNegativeDegree() {
		service.getGeneInteractions(presentGeneSymbol(), -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGeneInteractionsEmptyGeneSymbol() {
		service.getGeneInteractions("", 10);
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetGeneInteractionsNullGeneSymbol() {
		service.getGeneInteractions(null, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGeneInteractionsAbsentGeneSymbol() {
		service.getGeneInteractions(absentGeneSymbol(), 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesInteractionsNegativeDegree() {
		service.getGenesInteractions(asList(presentGeneSymbols()), -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesInteractionsEmptyGeneSymbols() {
		service.getGenesInteractions(emptyList(), 10);
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetGenesInteractionsNullGeneSymbols() {
		service.getGenesInteractions(null, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesInteractionsAbsentGeneSymbols() {
		service.getGenesInteractions(asList(absentGeneSymbols()), 10);
	}
}
