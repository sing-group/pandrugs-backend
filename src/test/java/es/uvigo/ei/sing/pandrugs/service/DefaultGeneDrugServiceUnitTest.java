/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.service;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.HasHttpStatus.hasOkStatus;
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToGeneDrugGroupInfos.equalToGeneDrugGroupInfos;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.absentDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.emptyGeneDrugGroupInfo;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugGeneDrugGroups;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugGeneDrugGroupsMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugNames;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.rankingFor;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugGeneDrugGroups;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugGeneDrugGroupsInfos;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolIndirect;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertThat;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugs.controller.GeneDrugController;
import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;

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
		testListByGene(absentGeneSymbol(), new GeneDrugGroup[0], emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testListByGenesSingleGeneDirect() {
		testListByGene(singleGeneSymbolDirect(), singleGeneGroupDirect(), singleGeneGroupInfosDirect());
	}
	
	@Test
	public void testListByGenesMultipleGeneDirect() {
		testListByGene(multipleGeneSymbolsDirect(), multipleGeneGroupDirect(), multipleGeneGroupInfosDirect());
	}
	
	@Test
	public void testListByGenesSingleGeneIndirect() {
		testListByGene(singleGeneSymbolIndirect(), singleGeneGroupIndirect(), singleGeneGroupInfosIndirect());
	}
	
	@Test
	public void testListByGenesMultipleGeneIndirect() {
		testListByGene(multipleGeneSymbolsIndirect(), multipleGeneGroupIndirect(), multipleGeneGroupInfosIndirect());
	}
	
	@Test
	public void testListByGenesMultipleGeneMixed() {
		testListByGene(multipleGeneSymbolsMixed(), multipleGeneGroupMixed(), multipleGeneGroupInfosMixed());
	}
	
	@Test
	public void testListByDrugsNoResult() {
		testSearchByDrug(absentDrugName(), new GeneDrugGroup[0], emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testSearchByDrugSingle() {
		testSearchByDrug(singleDrugName(), singleDrugGeneDrugGroups(), singleDrugGeneDrugGroupsInfos());
	}
	
	@Test
	public void testSearchByDrugMultiple() {
		testSearchByDrug(multipleDrugNames(), multipleDrugGeneDrugGroups(), multipleDrugGeneDrugGroupsMixed());
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
		testListRanked(rankingFor(absentGeneSymbol()), new GeneDrugGroup[0], emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testListRankedSingleGeneDirect() {
		testListRanked(rankingFor(singleGeneSymbolDirect()), singleGeneGroupDirect(), singleGeneGroupInfosDirect());
	}
	
	@Test
	public void testListRankedMultipleGeneDirect() {
		testListRanked(rankingFor(multipleGeneSymbolsDirect()), multipleGeneGroupDirect(), multipleGeneGroupInfosDirect());
	}
	
	@Test
	public void testListRankedSingleGeneIndirect() {
		testListRanked(rankingFor(singleGeneSymbolIndirect()), singleGeneGroupIndirect(), singleGeneGroupInfosIndirect());
	}
	
	@Test
	public void testListRankedMultipleGeneIndirect() {
		testListRanked(rankingFor(multipleGeneSymbolsIndirect()), multipleGeneGroupIndirect(), multipleGeneGroupInfosIndirect());
	}
	
	@Test
	public void testListRankedMultipleGeneMixed() {
		testListRanked(rankingFor(multipleGeneSymbolsMixed()), multipleGeneGroupMixed(), multipleGeneGroupInfosMixed());
	}
	
	private static String[] arrayToEasyMockMatchers(final String[] query) {
		return stream(query)
			.map(EasyMock::eq)
		.toArray(String[]::new);
	}
	
	private void testListByGene(final String query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		testListByGene(new String[] { query }, controllerResult, expectedResult);
	}

	private void testListByGene(final String[] query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			controller -> expect(controller.searchByGenes(anyObject(), arrayToEasyMockMatchers(query)))
				.andReturn(asList(controllerResult)),
			service -> service.list(stream(query).collect(toSet()), null, null, null, null, null),
			expectedResult
		);
	}

	private void testSearchByDrug(final String query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		testSearchByDrug(new String[] { query }, controllerResult, expectedResult);
	}
	
	private void testSearchByDrug(final String[] query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			controller -> expect(controller.searchByDrugs(anyObject(), arrayToEasyMockMatchers(query)))
				.andReturn(asList(controllerResult)),
			service -> service.list(null, stream(query).collect(toSet()), null, null, null, null),
			expectedResult
		);
	}

	private void testListRanked(final GeneRanking ranking, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			controller -> expect(controller.searchByRanking(anyObject(), eq(ranking)))
				.andReturn(asList(controllerResult)),
			service -> service.listRanked(ranking, null, null, null, null),
			expectedResult
		);
	}

	private void checkListResults(
		final Consumer<GeneDrugController> prepareController,
		final Function<GeneDrugService, Response> list,
		final GeneDrugGroupInfos expectedResult
	) {
		prepareController.accept(this.controller);
		
		replay(this.controller);
		
		final Response response = list.apply(this.service);
		
		assertThat(response, hasOkStatus());
		assertThat((GeneDrugGroupInfos) response.getEntity(), equalToGeneDrugGroupInfos(expectedResult));
	}
}
