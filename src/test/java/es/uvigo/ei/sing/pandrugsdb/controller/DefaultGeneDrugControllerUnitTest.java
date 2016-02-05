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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneDrugDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneIndirect;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneDrugDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.query.GeneQueryParameters;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;

//TODO: compare dscore and gscore
@RunWith(EasyMockRunner.class)
public class DefaultGeneDrugControllerUnitTest {
	@TestSubject
	private DefaultGeneDrugController controller =
		new DefaultGeneDrugController();
	
	@Mock
	private GeneDrugDAO dao;
	
	@After
	public void verifyDao() {
		verify(dao);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchEmptyGenes() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		
		replay(dao);
		
		this.controller.searchForGeneDrugs(queryParameters, new String[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRankedSearchEmptyGenes() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();

		replay(dao);
		
		this.controller.searchForGeneDrugs(queryParameters, new GeneRanking(emptyMap()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchQueryNullQuery() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final String[] query = null;
		
		replay(dao);
		
		this.controller.searchForGeneDrugs(queryParameters, query);
	}

	@Test(expected = NullPointerException.class)
	public void testRankedSearchNullRank() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final Map<String, Double> geneRank = null;
	
		replay(dao);
		
		this.controller.searchForGeneDrugs(queryParameters, new GeneRanking(geneRank));
	}

	@Test(expected = NullPointerException.class)
	public void testSearchQueryNullQueryParameters() {
		final GeneQueryParameters queryParameters = null;
		final String[] query = new String[] {"Direct Gene 1", "Direct Gene 2"};
		
		replay(dao);
		
		this.controller.searchForGeneDrugs(queryParameters, query);
	}

	@Test(expected = NullPointerException.class)
	public void testRankedSearchNullQueryParameters() {
		final GeneQueryParameters queryParameters = null;
		final GeneRanking geneRank = new GeneRanking(singletonMap("gene", 1d));

		replay(dao);
		
		this.controller.searchForGeneDrugs(queryParameters, geneRank);
	}
	
	@Test
	public void testSearchNoResult() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final String query = "Absent gene";
		
		prepareDao(queryParameters, query, emptyList());
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			queryParameters, query);
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testRankedSearchNoResult() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final GeneRanking geneRank = new GeneRanking(singletonMap("Absent gene", 1d));
		
		prepareDao(queryParameters, "Absent gene", emptyList());
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			queryParameters, geneRank
		);
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testSearchSingleGeneDirect() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final String query = "Direct Gene 1";
		
		prepareDao(queryParameters, query, asList(singleGeneDrugDirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupDirect()));
	}
	
	@Test
	public void testRankedSearchSingleGeneDirect() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final GeneRanking geneRank = new GeneRanking(singletonMap("Direct Gene 1", 1d));
		
		prepareDao(queryParameters, "Direct Gene 1", asList(singleGeneDrugDirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchMultipleGeneDirect() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final String[] query = new String[] { "Direct Gene 1", "Direct Gene 2" };
		
		prepareDao(queryParameters, query, asList(multipleGeneDirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneDirect() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final Map<String, Double> rank = new HashMap<>();
		rank.put("Direct Gene 1", 1d);
		rank.put("Direct Gene 2", 2d);
		final GeneRanking geneRank = new GeneRanking(rank);
		
		prepareDao(queryParameters, new String[] { "Direct Gene 1", "Direct Gene 2" }, asList(multipleGeneDirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchSingleGeneIndirect() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final String query = "IG1";

		prepareDao(queryParameters, query, asList(singleGeneIndirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupIndirect()));
	}
	
	@Test
	public void testRankedSearchSingleGeneIndirect() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final GeneRanking geneRank = new GeneRanking(singletonMap("IG1", 1d));
		
		prepareDao(queryParameters, "IG1", asList(singleGeneIndirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneIndirect() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final String[] query = new String[] { "IG1", "IG2" };
		
		prepareDao(queryParameters, query, asList(multipleGeneIndirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneIndirect() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final Map<String, Double> rank = new HashMap<>();
		rank.put("IG1", 1d);
		rank.put("IG2", 2d);
		final GeneRanking geneRank = new GeneRanking(rank);
		
		prepareDao(queryParameters, new String[] { "IG1", "IG2" }, asList(multipleGeneIndirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneMixed() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final String[] query = new String[] {
			"Direct Gene 1", "Direct Gene 2", "IG1", "IG2"
		};

		prepareDao(queryParameters, query, asList(multipleGeneMixed()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupMixed()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneMixed() {
		final GeneQueryParameters queryParameters = new GeneQueryParameters();
		final Map<String, Double> rank = new LinkedHashMap<>();
		rank.put("Direct Gene 1", 1d);
		rank.put("Direct Gene 2", 2d);
		rank.put("IG1", 3d);
		rank.put("IG2", 4d);
		final GeneRanking geneRank = new GeneRanking(rank);
		final String[] query = new String[] {
			"Direct Gene 1", "Direct Gene 2", "IG1", "IG2"
		};

		prepareDao(queryParameters, query, asList(multipleGeneMixed()));
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupMixed()));
	}
	
	private void prepareDao(
		GeneQueryParameters queryParameters,
		String geneName,
		List<GeneDrug> response
	) {
		prepareDao(queryParameters, new String[] { geneName }, response);
	}
	
	private void prepareDao(
		GeneQueryParameters queryParameters,
		String[] geneNames,
		List<GeneDrug> response
	) {
		geneNames = Arrays.stream(geneNames)
			.map(String::toUpperCase)
		.toArray(String[]::new);
		
		expect(dao.searchByGene(queryParameters, geneNames))
			.andReturn(response);
		
		replay(dao);
	}
}
