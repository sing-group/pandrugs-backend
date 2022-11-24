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

package es.uvigo.ei.sing.pandrugs.controller;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToGene.containsGenes;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.absentGeneSymbols;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.gene;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.geneSymbolsWithInteractionDegreeUpTo;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.geneSymbolsWithMaxInteractionDegree;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.interactions;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDAO;

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
	
	private void prepareDaoGet(String ... geneSymbols) {
		reset(dao);
		
		for (String geneSymbol : geneSymbols) {
			expect(dao.get(geneSymbol)).andReturn(gene(geneSymbol));
		}
		
		replay(dao);
	}
}
