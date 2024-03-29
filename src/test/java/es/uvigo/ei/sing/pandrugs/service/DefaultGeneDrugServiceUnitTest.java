/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2023 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToGeneDrugGroupInfos.equalToGeneDrugGroupInfos;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.absentDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.emptyGeneDrugGroupInfo;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupsByDrugs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugGeneDrugGroupsInfosMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugNames;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupMixedOnlyDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupPathwayMember;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupInfosPathwayMember;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupInfosMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsPathwayMember;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.rankingFor;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugGroupByDrug;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugGeneDrugGroupsInfos;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugGroupDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugGroupPathwayMember;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugGroupInfosPathwayMember;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolPathwayMember;
import static es.uvigo.ei.sing.pandrugs.util.ExpectWithVarargs.expectWithUnorderedVarargs;
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

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugs.controller.GeneDrugController;
import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;

@RunWith(EasyMockRunner.class)
@Ignore("Deprecated. Will be removed soon.")
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
	public void testListWithNullGenesByGet() {
		replay(controller);
	
		service.listByGeneOrDrugByGet(null, null, null, null, null, true, true, true, true);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenesByGet() {
		replay(controller);
		
		service.listByGeneOrDrugByGet(emptySet(), null, null, null, null, true, true, true, true);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyDrugsByGet() {
		replay(controller);
		
		service.listByGeneOrDrugByGet(null, emptySet(), null, null, null, true, true, true, true);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenesAndDrugsByGet() {
		replay(controller);
		
		service.listByGeneOrDrugByGet(emptySet(), emptySet(), null, null, null, true, true, true, true);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithGenesAndDrugsByGet() {
		replay(controller);
		
		service.listByGeneOrDrugByGet(singleton(singleGeneSymbolDirect()), singleton(singleDrugName()), null, null, null, true, true, true, true);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithNullGenesByPost() {
		replay(controller);
	
		service.listByGeneOrDrugByPost(null, null, null, null, null, true, true, true, true);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenesByPost() {
		replay(controller);
		
		service.listByGeneOrDrugByPost(emptySet(), null, null, null, null, true, true, true, true);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyDrugsByPost() {
		replay(controller);
		
		service.listByGeneOrDrugByPost(null, emptySet(), null, null, null, true, true, true, true);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenesAndDrugsByPost() {
		replay(controller);
		
		service.listByGeneOrDrugByPost(emptySet(), emptySet(), null, null, null, true, true, true, true);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithGenesAndDrugsByPost() {
		replay(controller);
		
		service.listByGeneOrDrugByPost(singleton(singleGeneSymbolDirect()), singleton(singleDrugName()), null, null, null, true, true, true, true);
	}
	
	@Test
	public void testListByGenesNoResultByGet() {
		testListByGeneByGet(absentGeneSymbol(), new GeneDrugGroup[0], emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testListByGenesSingleGeneDirectByGet() {
		testListByGeneByGet(singleGeneSymbolDirect(), singleGeneDrugGroupDirect(), singleGeneDrugGroupInfosDirect());
	}
	
	@Test
	public void testListByGenesMultipleGeneDirectByGet() {
		testListByGeneByGet(multipleGeneSymbolsDirect(), multipleGeneDrugGroupMixedOnlyDirect(), multipleGeneDrugGroupInfosDirect());
	}
	
	@Test
	public void testListByGenesSingleGeneIndirectByGet() {
		testListByGeneByGet(singleGeneSymbolPathwayMember(), singleGeneDrugGroupPathwayMember(), singleGeneDrugGroupInfosPathwayMember());
	}
	
	@Test
	public void testListByGenesMultipleGeneIndirectByGet() {
		testListByGeneByGet(multipleGeneSymbolsPathwayMember(), multipleGeneDrugGroupPathwayMember(), multipleGeneDrugGroupInfosPathwayMember());
	}
	
	@Test
	public void testListByGenesMultipleGeneMixedByGet() {
		testListByGeneByGet(multipleGeneSymbolsMixed(), multipleGeneDrugGroupMixed(), multipleGeneDrugGroupInfosMixed());
	}
	
	@Test
	public void testListByDrugsNoResultByGet() {
		testSearchByDrugByGet(absentDrugName(), new GeneDrugGroup[0], emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testSearchByDrugSingleByGet() {
		testSearchByDrugByGet(singleDrugName(), singleGeneDrugGroupByDrug(), singleDrugGeneDrugGroupsInfos());
	}
	
	@Test
	public void testSearchByDrugMultipleByGet() {
		testSearchByDrugByGet(multipleDrugNames(), multipleGeneDrugGroupsByDrugs(), multipleDrugGeneDrugGroupsInfosMixed());
	}
	
	@Test
	public void testListByGenesNoResultByPost() {
		testListByGeneByPost(absentGeneSymbol(), new GeneDrugGroup[0], emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testListByGenesSingleGeneDirectByPost() {
		testListByGeneByPost(singleGeneSymbolDirect(), singleGeneDrugGroupDirect(), singleGeneDrugGroupInfosDirect());
	}
	
	@Test
	public void testListByGenesMultipleGeneDirectByPost() {
		testListByGeneByPost(multipleGeneSymbolsDirect(), multipleGeneDrugGroupMixedOnlyDirect(), multipleGeneDrugGroupInfosDirect());
	}
	
	@Test
	public void testListByGenesSingleGeneIndirectByPost() {
		testListByGeneByPost(singleGeneSymbolPathwayMember(), singleGeneDrugGroupPathwayMember(), singleGeneDrugGroupInfosPathwayMember());
	}
	
	@Test
	public void testListByGenesMultipleGeneIndirectByPost() {
		testListByGeneByPost(multipleGeneSymbolsPathwayMember(), multipleGeneDrugGroupPathwayMember(), multipleGeneDrugGroupInfosPathwayMember());
	}
	
	@Test
	public void testListByGenesMultipleGeneMixedByPost() {
		testListByGeneByPost(multipleGeneSymbolsMixed(), multipleGeneDrugGroupMixed(), multipleGeneDrugGroupInfosMixed());
	}
	
	@Test
	public void testListByDrugsNoResultByPost() {
		testSearchByDrugByPost(absentDrugName(), new GeneDrugGroup[0], emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testSearchByDrugSingleByPost() {
		testSearchByDrugByPost(singleDrugName(), singleGeneDrugGroupByDrug(), singleDrugGeneDrugGroupsInfos());
	}
	
	@Test
	public void testSearchByDrugMultipleByPost() {
		testSearchByDrugByPost(multipleDrugNames(), multipleGeneDrugGroupsByDrugs(), multipleDrugGeneDrugGroupsInfosMixed());
	}
	
	@Test(expected = BadRequestException.class)
	public void testListRankedWithNullGenes() {
		replay(controller);
	
		service.listRanked((GeneRanking) null, null, null, null, true, true, true, true);
	}

	@Test(expected = BadRequestException.class)
	public void testListRankedWithEmptyGenes() {
		replay(controller);
		
		service.listRanked(new GeneRanking(emptyMap()), null, null, null, true, true, true, true);
	}
	
	@Test
	public void testListRankedNoResult() {
		testListRanked(rankingFor(absentGeneSymbol()), new GeneDrugGroup[0], emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testListRankedSingleGeneDirect() {
		testListRanked(rankingFor(singleGeneSymbolDirect()), singleGeneDrugGroupDirect(), singleGeneDrugGroupInfosDirect());
	}
	
	@Test
	public void testListRankedMultipleGeneDirect() {
		testListRanked(rankingFor(multipleGeneSymbolsDirect()), multipleGeneDrugGroupMixedOnlyDirect(), multipleGeneDrugGroupInfosDirect());
	}
	
	@Test
	public void testListRankedSingleGeneIndirect() {
		testListRanked(rankingFor(singleGeneSymbolPathwayMember()), singleGeneDrugGroupPathwayMember(), singleGeneDrugGroupInfosPathwayMember());
	}
	
	@Test
	public void testListRankedMultipleGeneIndirect() {
		testListRanked(rankingFor(multipleGeneSymbolsPathwayMember()), multipleGeneDrugGroupPathwayMember(), multipleGeneDrugGroupInfosPathwayMember());
	}
	
	@Test
	public void testListRankedMultipleGeneMixed() {
		testListRanked(rankingFor(multipleGeneSymbolsMixed()), multipleGeneDrugGroupMixed(), multipleGeneDrugGroupInfosMixed());
	}
	
	private void testListByGeneByGet(final String query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		testListByGeneByGet(new String[] { query }, controllerResult, expectedResult);
	}

	private void testListByGeneByGet(final String[] query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			controller -> expectWithUnorderedVarargs(controller, "searchByGenes", query, GeneDrugQueryParameters.class)
				.andReturn(asList(controllerResult)),
			service -> service.listByGeneOrDrugByGet(stream(query).collect(toSet()), null, null, null, null, true, true, true, true),
			expectedResult
		);
	}

	private void testSearchByDrugByGet(final String query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		testSearchByDrugByGet(new String[] { query }, controllerResult, expectedResult);
	}
	
	private void testSearchByDrugByGet(final String[] query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			controller -> expectWithUnorderedVarargs(controller, "searchByDrugs", query, GeneDrugQueryParameters.class)
				.andReturn(asList(controllerResult)),
			service -> service.listByGeneOrDrugByGet(null, stream(query).collect(toSet()), null, null, null, true, true, true, true),
			expectedResult
		);
	}
	
	private void testListByGeneByPost(final String query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		testListByGeneByPost(new String[] { query }, controllerResult, expectedResult);
	}

	private void testListByGeneByPost(final String[] query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			controller -> expectWithUnorderedVarargs(controller, "searchByGenes", query, GeneDrugQueryParameters.class)
				.andReturn(asList(controllerResult)),
			service -> service.listByGeneOrDrugByPost(stream(query).collect(toSet()), null, null, null, null, true, true, true, true),
			expectedResult
		);
	}

	private void testSearchByDrugByPost(final String query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		testSearchByDrugByPost(new String[] { query }, controllerResult, expectedResult);
	}
	
	private void testSearchByDrugByPost(final String[] query, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			controller -> expectWithUnorderedVarargs(controller, "searchByDrugs", query, GeneDrugQueryParameters.class)
				.andReturn(asList(controllerResult)),
			service -> service.listByGeneOrDrugByPost(null, stream(query).collect(toSet()), null, null, null, true, true, true, true),
			expectedResult
		);
	}

	private void testListRanked(final GeneRanking ranking, final GeneDrugGroup[] controllerResult, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			controller -> expect(controller.searchByRanking(anyObject(), eq(ranking)))
				.andReturn(asList(controllerResult)),
			service -> service.listRanked(ranking, null, null, null, true, true, true, true),
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
