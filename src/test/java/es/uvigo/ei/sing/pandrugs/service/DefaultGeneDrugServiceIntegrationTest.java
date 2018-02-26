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
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.listDrugs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.listGeneSymbols;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugGeneDrugGroupsMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugNames;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.rankingFor;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugGeneDrugGroupsInfos;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolIndirect;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

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

import es.uvigo.ei.sing.pandrugs.service.entity.DrugNames;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@Transactional
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DirtiesContext
@DatabaseSetup("file:src/test/resources/META-INF/dataset.genedrug.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.genedrug.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
//TODO: compare dscore and gscore
public class DefaultGeneDrugServiceIntegrationTest {
	@Inject
	@Named("defaultGeneDrugService")
	private GeneDrugService service;
	
	@Test(expected = BadRequestException.class)
	public void testListGeneSymbolsNullFilter() {
		this.service.listGeneSymbols(null, 10);
	}
	
	@Test
	public void testListGeneSymbols() {
		testListGeneSymbols("D", 10);
	}
	
	@Test
	public void testListGeneSymbolsNoMatch() {
		testListGeneSymbols("X", 10);
	}
	
	@Test
	public void testListGeneSymbolsWithLimit() {
		testListGeneSymbols("D", 1);
	}
	
	@Test
	public void testListGeneSymbolsEmptyFilter() {
		testListGeneSymbols("", 10);
	}
	
	@Test
	public void testListGeneSymbolsNegativeMaxResults() {
		testListGeneSymbols("G", -1);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListDrugNamesNullFilter() {
		this.service.listDrugNames(null, 10);
	}
	
	@Test
	public void testListDrugNames() {
		testListDrugNames("D", 10);
	}
	
	@Test
	public void testListDrugNamesNoMatch() {
		testListDrugNames("X", 10);
	}
	
	@Test
	public void testListDrugNamesWithLimit() {
		testListDrugNames("D", 1);
	}
	
	@Test
	public void testListDrugNamesEmptyFilter() {
		testListDrugNames("", 10);
	}
	
	@Test
	public void testListDrugNamesNegativeMaxResults() {
		testListDrugNames("D", -1);
	}

	private void testListGeneSymbols(final String query, final int maxResults) {
		final Response response = this.service.listGeneSymbols(query, maxResults);
		assertThat(response, hasOkStatus());
		
		final String[] expected = listGeneSymbols(query, maxResults);
		
		if (expected.length == 0) {
			assertThat((String[]) response.getEntity(), is(emptyArray()));
		} else {
			assertThat((String[]) response.getEntity(), is(arrayContaining(expected)));
		}
	}

	private void testListDrugNames(final String query, final int maxResults) {
		final Response response = this.service.listDrugNames(query, maxResults);
		
		assertThat(response, hasOkStatus());
		
		final DrugNames[] expected = DrugNames.of(listDrugs(query, maxResults));

		if (expected.length == 0) {
			assertThat((DrugNames[]) response.getEntity(), is(emptyArray()));
		} else {
			assertThat((DrugNames[]) response.getEntity(), is(arrayContaining(expected)));
		}
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithNullGenes() {
		service.list(null, null, null, null, null, true, true, true);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenes() {
		service.list(emptySet(), null, null, null, null, true, true, true);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyDrugs() {
		service.list(null, emptySet(), null, null, null, true, true, true);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenesAndDrugs() {
		service.list(emptySet(), emptySet(), null, null, null, true, true, true);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithGenesAndDrugs() {
		service.list(singleton(singleGeneSymbolDirect()), singleton(singleDrugName()), null, null, null, true, true, true);
	}
	
	@Test
	public void testListByGenesNoResult() {
		testListByGene(absentGeneSymbol(), emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testListByGenesSingleGeneDirect() {
		testListByGene(singleGeneSymbolDirect(), singleGeneGroupInfosDirect());
	}
	
	@Test
	public void testListByGenesMultipleGeneDirect() {
		testListByGene(multipleGeneSymbolsDirect(), multipleGeneGroupInfosDirect());
	}
	
	@Test
	public void testListByGenesSingleGeneIndirect() {
		testListByGene(singleGeneSymbolIndirect(), singleGeneGroupInfosIndirect());
	}
	
	@Test
	public void testListByGenesMultipleGeneIndirect() {
		testListByGene(multipleGeneSymbolsIndirect(), multipleGeneGroupInfosIndirect());
	}
	
	@Test
	public void testListByGenesMultipleGeneMixed() {
		testListByGene(multipleGeneSymbolsMixed(), multipleGeneGroupInfosMixed());
	}
	
	@Test
	public void testListByDrugsNoResult() {
		testSearchByDrug(absentDrugName(), emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testSearchByDrugSingle() {
		testSearchByDrug(singleDrugName(), singleDrugGeneDrugGroupsInfos());
	}
	
	@Test
	public void testSearchByDrugMultiple() {
		testSearchByDrug(multipleDrugNames(), multipleDrugGeneDrugGroupsMixed());
	}
	
	@Test(expected = BadRequestException.class)
	public void testListRankedWithNullGenes() {
		service.listRanked(null, null, null, null, true, true, true);
	}

	@Test(expected = BadRequestException.class)
	public void testListRankedWithEmptyGenes() {
		service.listRanked(new GeneRanking(emptyMap()), null, null, null, true, true, true);
	}
	
	@Test
	public void testListRankedNoResult() {
		testListRanked(rankingFor(absentGeneSymbol()), emptyGeneDrugGroupInfo());
	}
	
	@Test
	public void testListRankedSingleGeneDirect() {
		testListRanked(rankingFor(singleGeneSymbolDirect()), singleGeneGroupInfosDirect());
	}
	
	@Test
	public void testListRankedMultipleGeneDirect() {
		testListRanked(rankingFor(multipleGeneSymbolsDirect()), multipleGeneGroupInfosDirect());
	}
	
	@Test
	public void testListRankedSingleGeneIndirect() {
		testListRanked(rankingFor(singleGeneSymbolIndirect()), singleGeneGroupInfosIndirect());
	}
	
	@Test
	public void testListRankedMultipleGeneIndirect() {
		testListRanked(rankingFor(multipleGeneSymbolsIndirect()), multipleGeneGroupInfosIndirect());
	}
	
	@Test
	public void testListRankedMultipleGeneMixed() {
		testListRanked(rankingFor(multipleGeneSymbolsMixed()), multipleGeneGroupInfosMixed());
	}
	
	private void testListByGene(final String query, final GeneDrugGroupInfos expectedResult) {
		testListByGene(new String[] { query }, expectedResult);
	}

	private void testListByGene(final String[] query, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			service -> service.list(stream(query).collect(toSet()), null, null, null, null, true, true, true),
			expectedResult
		);
	}

	private void testSearchByDrug(final String query, final GeneDrugGroupInfos expectedResult) {
		testSearchByDrug(new String[] { query }, expectedResult);
	}
	
	private void testSearchByDrug(final String[] query, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			service -> service.list(null, stream(query).collect(toSet()), null, null, null, true, true, true),
			expectedResult
		);
	}

	private void testListRanked(final GeneRanking ranking, final GeneDrugGroupInfos expectedResult) {
		checkListResults(
			service -> service.listRanked(ranking, null, null, null, true, true, true),
			expectedResult
		);
	}

	private void checkListResults(
		final Function<GeneDrugService, Response> list,
		final GeneDrugGroupInfos expectedResult
	) {
		final Response response = list.apply(this.service);
		
		assertThat(response, hasOkStatus());
		assertThat((GeneDrugGroupInfos) response.getEntity(), is(equalToGeneDrugGroupInfos(expectedResult)));
	}
}
