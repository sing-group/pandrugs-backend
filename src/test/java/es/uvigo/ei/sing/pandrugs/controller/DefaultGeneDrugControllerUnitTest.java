/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToDrug.containsDrugs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.absentDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.geneDrugsWithDrug;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.listDrugs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.listGeneSymbols;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugGeneDrugGroups;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugNames;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.rankingFor;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugGeneDrugGroups;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolIndirect;
import static es.uvigo.ei.sing.pandrugs.util.ExpectWithVarargs.expectWithUnorderedVarargs;
import static es.uvigo.ei.sing.pandrugs.util.StringFormatter.toUpperCase;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDrugDAO;
import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDrugWarningDAO;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;

//TODO: compare dscore and gscore
@RunWith(EasyMockRunner.class)
public class DefaultGeneDrugControllerUnitTest {
	@TestSubject
	private DefaultGeneDrugController controller = new DefaultGeneDrugController();
	
	@Mock
	private GeneDrugDAO dao;
	
	@Mock
	private GeneDrugWarningDAO drugWarningDao;
	
	@Before
	public void setUp() {
		expect(this.drugWarningDao.findForGeneDrugs(anyObject()))
			.andReturn(emptyMap())
		.anyTimes();
	}
	
	@After
	public void verifyDao() {
		verify(dao);
		verify(drugWarningDao);
	}
	
	private void replayAll() {
		replay(dao);
		replay(drugWarningDao);
	}
	
	@Test
	public void testListGeneSymbols() {
		final String query = "D";
		final int maxResults = 10;
		final String[] expected = listGeneSymbols(query, maxResults);
		
		prepareListGeneSymbols(query, maxResults, expected);
		
		final String[] geneSymbols = this.controller.listGeneSymbols(query, maxResults);
		
		assertThat(geneSymbols, is(arrayContaining(expected)));
	}
	
	@Test
	public void testListGeneSymbolsNoMatch() {
		final String query = "X";
		final int maxResults = 10;
		final String[] expected = new String[0];
		
		prepareListGeneSymbols(query, maxResults, expected);
		
		final String[] geneSymbols = this.controller.listGeneSymbols(query, maxResults);

		assertThat(geneSymbols, is(emptyArray()));
	}
	
	@Test
	public void testListGeneSymbolsEmptyFilterAndNegativeMaxResults() {
		final String query = "";
		final int maxResults = -1;
		final String[] expected = listGeneSymbols(query, maxResults);
		
		prepareListGeneSymbols(query, maxResults, expected);
		
		final String[] geneSymbols = this.controller.listGeneSymbols(query, maxResults);
		
		assertThat(geneSymbols, is(arrayContaining(expected)));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListGeneSymbolsNullFilter() {
		replayAll();
		
		this.controller.listGeneSymbols(null, 10);
	}
	
	@Test
	public void testlistDrugs() {
		final String query = "D";
		final int maxResults = 10;
		final Drug[] expected = listDrugs(query, maxResults);
		
		preparelistDrugs(query, maxResults, expected);

		final Drug[] drugs = this.controller.listDrugs(query, maxResults);
		
		assertThat(asList(drugs), containsDrugs(expected));
	}
	
	@Test
	public void testlistDrugsNoMatch() {
		final String query = "X";
		final int maxResults = 10;
		final Drug[] expected = new Drug[0];
		
		preparelistDrugs(query, maxResults, expected);
		
		final Drug[] drugs = this.controller.listDrugs(query, maxResults);
		
		assertThat(drugs, is(emptyArray()));
	}
	
	@Test
	public void testlistDrugsEmptyQueryAndNegativeMaxResults() {
		final String query = "";
		final int maxResults = -1;
		final Drug[] expected = listDrugs(query, maxResults);
		
		preparelistDrugs(query, maxResults, expected);
		
		final Drug[] drugs = this.controller.listDrugs(query, maxResults);
		
		assertThat(asList(drugs), containsDrugs(expected));
	}
	
	@Test(expected = NullPointerException.class)
	public void testlistDrugsNullFilter() {
		replayAll();
		
		this.controller.listDrugs(null, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchByGenesEmptyGenes() {
		replayAll();
		
		this.controller.searchByGenes(new GeneDrugQueryParameters(), new String[0]);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchByGenesNullGenes() {
		replayAll();
		
		this.controller.searchByGenes(new GeneDrugQueryParameters(), (String[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRankedSearchEmptyGenes() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();

		replayAll();
		
		this.controller.searchByRanking(queryParameters, new GeneRanking(emptyMap()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchByGenesQueryNullQuery() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] query = null;
		
		replayAll();
		
		this.controller.searchByGenes(queryParameters, query);
	}

	@Test(expected = NullPointerException.class)
	public void testRankedSearchNullRank() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final Map<String, Double> geneRank = null;
	
		replayAll();
		
		this.controller.searchByRanking(queryParameters, new GeneRanking(geneRank));
	}

	@Test(expected = NullPointerException.class)
	public void testSearchByGenesQueryNullQueryParameters() {
		final GeneDrugQueryParameters queryParameters = null;
		final String[] query = multipleGeneSymbolsDirect();
		
		replayAll();
		
		this.controller.searchByGenes(queryParameters, query);
	}

	@Test(expected = NullPointerException.class)
	public void testRankedSearchNullQueryParameters() {
		final GeneDrugQueryParameters queryParameters = null;
		final GeneRanking geneRank = rankingFor("GENE");

		replayAll();
		
		this.controller.searchByRanking(queryParameters, geneRank);
	}
	
	@Test
	public void testSearchByGenesNoResult() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String query = absentGeneSymbol();
		
		prepareSearchByGene(queryParameters, query, emptyList());
		
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			queryParameters, query);
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testRankedSearchNoResult() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String geneSymbol = absentGeneSymbol();
		final GeneRanking geneRank = rankingFor(geneSymbol);
		
		prepareSearchByGene(queryParameters, geneSymbol, emptyList());
		
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			queryParameters, geneRank
		);
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testSearchByGenesSingleGeneDirect() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String query = singleGeneSymbolDirect();
		
		prepareSearchByGene(queryParameters, query, asList(singleGeneDrugDirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchByGenes(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupDirect()));
	}
	
	@Test
	public void testRankedSearchSingleGeneDirect() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String geneSymbol = singleGeneSymbolDirect();
		final GeneRanking geneRank = rankingFor(geneSymbol);
		
		prepareSearchByGene(queryParameters, geneSymbol, asList(singleGeneDrugDirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchByRanking(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleGeneDirect() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] query = multipleGeneSymbolsDirect();
		
		prepareSearchByGene(queryParameters, query, asList(multipleGeneDirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchByGenes(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneDirect() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] query = multipleGeneSymbolsDirect();
		final GeneRanking geneRank = rankingFor(query);
		
		prepareSearchByGene(queryParameters, query, asList(multipleGeneDirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchByRanking(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchByGenesSingleGeneIndirect() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String query = singleGeneSymbolIndirect();

		prepareSearchByGene(queryParameters, query, asList(singleGeneIndirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchByGenes(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupIndirect()));
	}
	
	@Test
	public void testRankedSearchSingleGeneIndirect() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String query = singleGeneSymbolIndirect();
		final GeneRanking geneRank = rankingFor(query);
		
		prepareSearchByGene(queryParameters, query, asList(singleGeneIndirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchByRanking(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleGeneIndirect() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] query = multipleGeneSymbolsIndirect();
		
		prepareSearchByGene(queryParameters, query, asList(multipleGeneIndirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchByGenes(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneIndirect() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] query = multipleGeneSymbolsIndirect();
		final GeneRanking geneRank = rankingFor(query);
		
		prepareSearchByGene(queryParameters, query, asList(multipleGeneIndirect()));
		
		final List<GeneDrugGroup> result = this.controller.searchByRanking(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleGeneMixed() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] query = {"DIRECT GENE 1", "DIRECT GENE 2", "IG1", "IG2"};//multipleGeneSymbolsMixed();

		prepareSearchByGene(queryParameters, query, asList(multipleGeneMixed()));
		
		final List<GeneDrugGroup> result = this.controller.searchByGenes(queryParameters, query);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupMixed()));
	}
	
	@Test
	public void testSearchByDrugAbsent() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String drugNames = absentDrugName();
		final List<GeneDrug> geneDrugs = emptyList();
			
		prepareSearchByDrug(queryParameters, drugNames, geneDrugs);
		
		assertThat(this.controller.searchByDrugs(queryParameters, drugNames), is(empty()));
	}
	
	@Test
	public void testSearchByDrugSingle() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String drugNames = singleDrugName();
		final List<GeneDrug> geneDrugs = asList(geneDrugsWithDrug(drugNames));
			
		prepareSearchByDrug(queryParameters, drugNames, geneDrugs);
		
		final List<GeneDrugGroup> groups = this.controller.searchByDrugs(queryParameters, drugNames);
		
		assertThat(groups, containsInAnyOrder(singleDrugGeneDrugGroups()));
	}
	
	@Test
	public void testSearchByDrugMultiple() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] drugNames = multipleDrugNames();
		final List<GeneDrug> geneDrugs = asList(geneDrugsWithDrug(drugNames));
			
		prepareSearchByDrug(queryParameters, drugNames, geneDrugs);
		
		final List<GeneDrugGroup> groups = this.controller.searchByDrugs(queryParameters, drugNames);
		
		assertThat(groups, containsInAnyOrder(multipleDrugGeneDrugGroups()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneMixed() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] query = multipleGeneSymbolsMixed();
		final GeneRanking geneRank = rankingFor(query);

		prepareSearchByGene(queryParameters, query, asList(multipleGeneMixed()));
		
		final List<GeneDrugGroup> result = this.controller.searchByRanking(queryParameters, geneRank);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupMixed()));
	}
	
	private void prepareSearchByGene(
		GeneDrugQueryParameters queryParameters,
		String geneName,
		List<GeneDrug> response
	) {
		expect(dao.searchByGene(queryParameters, geneName.toUpperCase()))
			.andReturn(response);
	
		replayAll();
	}
	
	private void prepareSearchByGene(
		GeneDrugQueryParameters queryParameters,
		String[] geneNames,
		List<GeneDrug> response
	) {
		geneNames = toUpperCase(geneNames);
		
		expectWithUnorderedVarargs(dao, "searchByGene", geneNames, eq(queryParameters))
			.andReturn(response);
		
		replayAll();
	}
	
	private void prepareSearchByDrug(
		GeneDrugQueryParameters queryParameters,
		String drugNames,
		List<GeneDrug> response
	) {
		prepareSearchByDrug(queryParameters, new String[] { drugNames }, response);
	}
	
	private void prepareSearchByDrug(
		GeneDrugQueryParameters queryParameters,
		String[] drugNames,
		List<GeneDrug> response
	) {
		drugNames = toUpperCase(drugNames);
		
		expectWithUnorderedVarargs(dao, "searchByDrug", drugNames, eq(queryParameters))
			.andReturn(response);

		replayAll();
	}
	
	private void prepareListGeneSymbols(String query, int maxResults, String[] expected) {
		expect(dao.listGeneSymbols(query, maxResults)).andReturn(expected);
		
		replayAll();
	}
	
	private void preparelistDrugs(String query, int maxResults, Drug[] expected) {
		expect(dao.listDrugs(query, maxResults)).andReturn(expected);
		
		replayAll();
	}
}
