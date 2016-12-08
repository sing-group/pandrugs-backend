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
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.absentGeneSymbols;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.geneSymbolsWithInteractionDegreeUpTo;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.geneSymbolsWithMaxInteractionDegree;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.interactions;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.presentGeneSymbol;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.presentGeneSymbols;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.controller.GeneController;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneInteraction;

@RunWith(EasyMockRunner.class)
public class DefaultGeneServiceUnitTest {
	@TestSubject
	private DefaultGeneService service = new DefaultGeneService();

	@Mock
	private GeneController controller;
	
	private boolean verify = true;
	
	@After
	public void tearDown() {
		if (verify)
			verify(controller);
	}
	
	@Test
	public void testGetGeneInteractions() {
		this.verify = false;
		
		for (String gene : presentGeneSymbols()) {
			for (int degree = 0; degree < 5; degree++) {
				final Gene[] expected = interactions(degree, gene);
				
				final Set<Gene> expectedSet = new HashSet<>(asList(expected));
				expect(controller.interactions(degree, gene)).andReturn(expectedSet);
				replay(controller);
				
				final GeneInteraction[] interactions = service.getGeneInteractions(gene, degree);
				
				assertThat(asList(interactions), containsGeneInteraction(expected));

				verify(controller);
				reset(controller);
			}
		}
	}
	
	@Test
	public void testGetGenesInteractionsWithMaxDegree() {
		this.verify = false;
		
		for (int degree = 0; degree <= 3; degree++) {
			final String[] queryGenes = geneSymbolsWithMaxInteractionDegree(degree);
			final Gene[] expected = interactions(degree, queryGenes);
			
			final Set<Gene> expectedSet = new HashSet<>(asList(expected));
			expect(controller.interactions(degree, queryGenes)).andReturn(expectedSet);
			replay(controller);
				
			final GeneInteraction[] interactions = service.getGenesInteractions(asList(queryGenes), degree);
			
			assertThat(asList(interactions), containsGeneInteraction(expected));

			verify(controller);
			reset(controller);
		}
	}
	
	@Test
	public void testGetGenesInteractionsWithDegreeUpTo() {
		this.verify = false;
		
		for (int degree = 0; degree <= 5; degree++) {
			final String[] queryGenes = geneSymbolsWithInteractionDegreeUpTo(degree);
			final Gene[] expected = interactions(degree, queryGenes);
			
			final Set<Gene> expectedSet = new HashSet<>(asList(expected));
			expect(controller.interactions(degree, queryGenes)).andReturn(expectedSet);
			replay(controller);
			
			final GeneInteraction[] interactions = service.getGenesInteractions(asList(queryGenes), degree);
			
			assertThat(asList(interactions), containsGeneInteraction(expected));
			
			verify(controller);
			reset(controller);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGeneInteractionsNegativeDegree() {
		replay(controller);
		
		service.getGeneInteractions(presentGeneSymbol(), -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGeneInteractionsEmptyGeneSymbol() {
		replay(controller);
		
		service.getGeneInteractions("", 10);
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetGeneInteractionsNullGeneSymbol() {
		replay(controller);
		
		service.getGeneInteractions(null, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGeneInteractionsAbsentGeneSymbol() {
		final String query = absentGeneSymbol();
		final int degree = 10;
		
		expect(controller.interactions(degree, query)).andThrow(new IllegalArgumentException());
		replay(controller);
		
		service.getGeneInteractions(query, degree);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesInteractionsNegativeDegree() {
		replay(controller);
		
		service.getGenesInteractions(asList(presentGeneSymbols()), -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesInteractionsEmptyGeneSymbols() {
		replay(controller);
		
		service.getGenesInteractions(emptyList(), 10);
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetGenesInteractionsNullGeneSymbols() {
		replay(controller);
		
		service.getGenesInteractions(null, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesInteractionsAbsentGeneSymbols() {
		final String[] query = absentGeneSymbols();
		final int degree = 10;
		
		expect(controller.interactions(degree, query)).andThrow(new IllegalArgumentException());
		replay(controller);
		
		service.getGenesInteractions(asList(query), degree);
	}
}
