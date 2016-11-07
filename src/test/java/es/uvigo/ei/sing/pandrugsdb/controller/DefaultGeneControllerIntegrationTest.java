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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.IsEqualToGene.containsGenes;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.absentGeneSymbols;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.geneSymbolsForQuery;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.geneSymbolsWithInteractionDegreeUpTo;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.geneSymbolsWithMaxInteractionDegree;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.interactions;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.Assert.assertThat;

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

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
})
@DirtiesContext
@DatabaseSetup("file:src/test/resources/META-INF/dataset.gene.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.gene.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultGeneControllerIntegrationTest {
	@Inject
	@Named("defaultGeneController")
	private GeneController controller;
	
	@Test
	public void testListGeneSymbols() {
		final String query = "C";
		final int maxResults = 10;
		final String[] expected = geneSymbolsForQuery(query);
		
		assertThat(controller.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsZeroMaxResults() {
		final String query = "C";
		final int maxResults = 0;
		final String[] expected = geneSymbolsForQuery(query);
		
		assertThat(controller.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsNegativeMaxResults() {
		final String query = "C";
		final int maxResults = -1;
		final String[] expected = geneSymbolsForQuery(query);
		
		assertThat(controller.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsMaxResults() {
		final String query = "C";
		final int maxResults = 1;
		final String[] expected = geneSymbolsForQuery(query, maxResults);
		
		assertThat(controller.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsEmptyResults() {
		final String query = "XYZ";
		final int maxResults = 10;
		
		assertThat(controller.listGeneSymbols(query, maxResults), is(emptyArray()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListGeneSymbolsNullQuery() {
		controller.listGeneSymbols(null, 0);
	}

	@Test
	public void testInteractionsWithMaxDegree() {
		for (int degree = 0; degree <= 3; degree++) {
			final String[] queryGenes = geneSymbolsWithMaxInteractionDegree(degree);

			assertThat(controller.interactions(degree, queryGenes), containsGenes(interactions(degree, queryGenes)));
		}
	}

	@Test
	public void testInteractionsWithDegreeUpTo() {
		for (int degree = 0; degree <= 5; degree++) {
			final String[] queryGenes = geneSymbolsWithInteractionDegreeUpTo(degree);
			
			assertThat(controller.interactions(degree, queryGenes), containsGenes(interactions(degree, queryGenes)));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInteractionsNegativeDegree() {
		controller.interactions(-1, "GATA2");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInteractionsEmptyGeneSymbols() {
		controller.interactions(10);
	}
	
	@Test(expected = NullPointerException.class)
	public void testInteractionsNullGeneSymbols() {
		controller.interactions(10, "GATA2", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInteractionsEmptyGenes() {
		final String[] geneSymbols = absentGeneSymbols();
		
		controller.interactions(10, geneSymbols);
	}
}
