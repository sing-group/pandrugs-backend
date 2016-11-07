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
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.gene;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.geneSymbolsForQuery;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.geneSymbolsWithInteractionDegreeUpTo;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.geneSymbolsWithMaxInteractionDegree;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.interactions;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneDAO;

@RunWith(EasyMockRunner.class)
public class DefaultGeneControllerUnitTest {
	@TestSubject
	private DefaultGeneController controller = new DefaultGeneController();
	
	@Mock
	private GeneDAO dao;
	
	@After
	public void verifyDao() {
		verify(dao);
	}
	
	@Test
	public void testListGeneSymbols() {
		final String query = "C";
		final int maxResults = 10;
		final String[] expected = geneSymbolsForQuery(query);
		
		prepareDaoList(query, maxResults, expected);
		
		assertThat(controller.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsZeroMaxResults() {
		final String query = "C";
		final int maxResults = 0;
		final String[] expected = geneSymbolsForQuery(query);
		
		prepareDaoList(query, maxResults, expected);
		
		assertThat(controller.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsNegativeMaxResults() {
		final String query = "C";
		final int maxResults = -1;
		final String[] expected = geneSymbolsForQuery(query);
		
		prepareDaoList(query, maxResults, expected);
		
		assertThat(controller.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsMaxResults() {
		final String query = "C";
		final int maxResults = 1;
		final String[] expected = geneSymbolsForQuery(query, maxResults);
		
		prepareDaoList(query, maxResults, expected);
		
		assertThat(controller.listGeneSymbols(query, maxResults), arrayContaining(expected));
	}
	
	@Test
	public void testListGeneSymbolsEmptyResults() {
		final String query = "XYZ";
		final int maxResults = 10;
		final String[] expected = new String[0];
		
		prepareDaoList(query, maxResults, expected);
		
		assertThat(controller.listGeneSymbols(query, maxResults), is(emptyArray()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListGeneSymbolsNullQuery() {
		replay(dao);
		
		controller.listGeneSymbols(null, 0);
	}

	@Test
	public void testInteractionsWithMaxDegree() {
		for (int degree = 0; degree <= 3; degree++) {
			final String[] queryGenes = geneSymbolsWithMaxInteractionDegree(degree);
			
			prepareDaoGet(queryGenes);
			
			assertThat(controller.interactions(degree, queryGenes), containsGenes(interactions(degree, queryGenes)));
		}
	}

	@Test
	public void testInteractionsWithDegreeUpTo() {
		for (int degree = 0; degree <= 5; degree++) {
			final String[] queryGenes = geneSymbolsWithInteractionDegreeUpTo(degree);
			
			prepareDaoGet(queryGenes);
			
			assertThat(controller.interactions(degree, queryGenes), containsGenes(interactions(degree, queryGenes)));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInteractionsNegativeDegree() {
		replay(dao);
		
		controller.interactions(-1, "GATA2");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInteractionsEmptyGeneSymbols() {
		replay(dao);
		
		controller.interactions(10);
	}
	
	@Test(expected = NullPointerException.class)
	public void testInteractionsNullGeneSymbols() {
		replay(dao);
		
		controller.interactions(10, "GATA2", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInteractionsEmptyGenes() {
		final String[] geneSymbols = absentGeneSymbols();
		
		for (String geneSymbol : geneSymbols) {
			expect(dao.get(geneSymbol)).andReturn(null);
		}
		
		replay(dao);
		
		controller.interactions(10, geneSymbols);
	}
	
	private void prepareDaoList(
		String geneSymbol, int maxResults, String[] geneSymbols
	) {
		expect(dao.listGeneSymbols(geneSymbol, maxResults))
			.andReturn(geneSymbols);
		
		replay(dao);
	}
	
	private void prepareDaoGet(String ... geneSymbols) {
		reset(dao);
		
		for (String geneSymbol : geneSymbols) {
			expect(dao.get(geneSymbol)).andReturn(gene(geneSymbol));
		}
		
		replay(dao);
	}
}
