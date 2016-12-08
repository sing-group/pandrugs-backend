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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.IsEqualToGeneDrugGroupInfos.equalsToGeneDrugGroupInfos;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.absentDrugName;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleDrugGeneDrugGroups;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleDrugGeneDrugGroupsMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleDrugNames;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneSymbolsDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneSymbolsIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneSymbolsMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.rankingFor;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleDrugGeneDrugGroups;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleDrugGeneDrugGroupsInfos;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleDrugName;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneSymbolDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneSymbolIndirect;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.ws.rs.BadRequestException;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.controller.GeneDrugController;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;

@RunWith(EasyMockRunner.class)
//TODO: compare dscore and gscore
public class DefaultGeneDrugServiceUnitTest {
	@TestSubject
	private DefaultGeneDrugService service = new DefaultGeneDrugService();

	@Mock
	private GeneDrugController controller;
	
	@After
	public void verifyController() {
		verify(controller);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithNullGenes() {
		replay(controller);
	
		service.list(null, null, null, null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenes() {
		replay(controller);
		
		service.list(emptySet(), null, null, null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyDrugs() {
		replay(controller);
		
		service.list(null, emptySet(), null, null, null, null);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenesAndDrugs() {
		replay(controller);
		
		service.list(emptySet(), emptySet(), null, null, null, null);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithGenesAndDrugs() {
		replay(controller);
		
		service.list(singleton(singleGeneSymbolDirect()), singleton(singleDrugName()), null, null, null, null);
	}
	
	@Test
	public void testListByGenesNoResult() {
		final String query = absentGeneSymbol();
		
		expect(controller.searchByGenes(anyObject(), eq(query)))
			.andReturn(emptyList());
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.list(
			singleton(query), null, null, null, null, null);
		
		assertThat(result.getGeneDrugs(), is(empty()));
	}
	
	@Test
	public void testListByGenesSingleGeneDirect() {
		final String query = singleGeneSymbolDirect();
		
		expect(controller.searchByGenes(anyObject(), eq(query)))
			.andReturn(asList(singleGeneGroupDirect()));
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.list(
			singleton(query), null, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(singleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testListByGenesMultipleGeneDirect() {
		final String[] query = multipleGeneSymbolsDirect();
		
		expect(controller.searchByGenes(anyObject(), arrayToEasyMockMatchers(query)))
			.andReturn(asList(multipleGeneGroupDirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.list(
			new HashSet<>(asList(query)), null, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testListByGenesSingleGeneIndirect() {
		final String query = singleGeneSymbolIndirect();
		
		expect(controller.searchByGenes(anyObject(), eq(query)))
			.andReturn(asList(singleGeneGroupIndirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.list(
			singleton(query), null, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(singleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testListByGenesMultipleGeneIndirect() {
		final String[] query = multipleGeneSymbolsIndirect();
		
		expect(controller.searchByGenes(anyObject(), arrayToEasyMockMatchers(query)))
			.andReturn(asList(multipleGeneGroupIndirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.list(
			new HashSet<>(asList(query)), null, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testListByGenesMultipleGeneMixed() {
		final String[] query = multipleGeneSymbolsMixed();
		
		expect(controller.searchByGenes(
			anyObject(), arrayToEasyMockMatchers(query)
		)).andReturn(asList(multipleGeneGroupMixed()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.list(
			new LinkedHashSet<>(asList(query)), null, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosMixed()));
	}
	
	@Test
	public void testListByDrugsNoResult() {
		final String query = absentDrugName();
		
		expect(controller.searchByDrugs(anyObject(), eq(query)))
			.andReturn(emptyList());
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.list(
			null, singleton(absentDrugName()), null, null, null, null
		);
		
		assertThat(result.getGeneDrugs(), is(empty()));
	}
	
	@Test
	public void testSearchByDrugSingle() {
		final String query = singleDrugName();
		
		expect(controller.searchByDrugs(anyObject(), eq(query)))
			.andReturn(asList(singleDrugGeneDrugGroups()));
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.list(
			null, singleton(query), null, null, null, null
		);
		
		assertThat(result, is(equalsToGeneDrugGroupInfos(singleDrugGeneDrugGroupsInfos())));
	}
	
	@Test
	public void testSearchByDrugMultiple() {
		final String[] query = multipleDrugNames();
		
		expect(controller.searchByDrugs(anyObject(), arrayToEasyMockMatchers(query)))
			.andReturn(asList(multipleDrugGeneDrugGroups()));
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.list(
			null, stream(query).collect(toSet()), null, null, null, null
		);
		
		assertThat(result, is(equalsToGeneDrugGroupInfos(multipleDrugGeneDrugGroupsMixed())));
	}
	
	@Test(expected = BadRequestException.class)
	public void testListRankedWithNullGenes() {
		replay(controller);
	
		service.listRanked((GeneRanking) null, null, null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testListRankedWithEmptyGenes() {
		replay(controller);
		
		service.listRanked(new GeneRanking(emptyMap()), null, null, null, null);
	}
	
	@Test
	public void testListRankedNoResult() {
		final GeneRanking ranking = rankingFor(absentGeneSymbol());
		
		expect(controller.searchByRanking(anyObject(), eq(ranking)))
			.andReturn(emptyList());
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result.getGeneDrugs(), is(empty()));
	}
	
	@Test
	public void testListRankedSingleGeneDirect() {
		final GeneRanking ranking = rankingFor(singleGeneSymbolDirect());
		
		expect(controller.searchByRanking(anyObject(), eq(ranking)))
			.andReturn(asList(singleGeneGroupDirect()));
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(singleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testListRankedMultipleGeneDirect() {
		final GeneRanking ranking = rankingFor(multipleGeneSymbolsDirect());
		
		expect(controller.searchByRanking(anyObject(), eq(ranking)))
			.andReturn(asList(multipleGeneGroupDirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testListRankedSingleGeneIndirect() {
		final GeneRanking ranking = rankingFor(singleGeneSymbolIndirect());
		
		expect(controller.searchByRanking(anyObject(), eq(ranking)))
			.andReturn(asList(singleGeneGroupIndirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(singleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testListRankedMultipleGeneIndirect() {
		final GeneRanking ranking = rankingFor(multipleGeneSymbolsIndirect());
		
		expect(controller.searchByRanking(anyObject(), eq(ranking)))
			.andReturn(asList(multipleGeneGroupIndirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testListRankedMultipleGeneMixed() {
		final GeneRanking ranking = rankingFor(multipleGeneSymbolsMixed());
		
		expect(controller.searchByRanking(anyObject(), eq(ranking)))
			.andReturn(asList(multipleGeneGroupMixed()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosMixed()));
	}
	
	private static String[] arrayToEasyMockMatchers(final String[] query) {
		return stream(query)
			.map(EasyMock::eq)
		.toArray(String[]::new);
	}
}
