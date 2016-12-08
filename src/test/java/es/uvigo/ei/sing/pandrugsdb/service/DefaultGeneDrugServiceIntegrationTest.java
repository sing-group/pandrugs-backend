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
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.listGeneSymbols;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.listStandardDrugNames;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleDrugGeneDrugGroupsMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleDrugNames;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneSymbolsDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneSymbolsIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneSymbolsMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.rankingFor;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleDrugGeneDrugGroupsInfos;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleDrugName;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneSymbolDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneSymbolIndirect;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;

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

import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;

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
	
	@Test
	public void testListGeneSymbols() {
		final String[] geneSymbols = this.service.listGeneSymbols("D", 10);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("D", 10))));
	}
	
	@Test
	public void testListGeneSymbolsNoMatch() {
		final String[] geneSymbols = this.service.listGeneSymbols("X", 10);
		
		assertThat(geneSymbols, is(emptyArray()));
	}
	
	@Test
	public void testListGeneSymbolsWithLimit() {
		final String[] geneSymbols = this.service.listGeneSymbols("D", 1);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("D", 1))));
	}
	
	@Test
	public void testListGeneSymbolsEmptyFilter() {
		final String[] geneSymbols = this.service.listGeneSymbols("", 10);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("", 10))));
	}
	
	@Test
	public void testListGeneSymbolsNegativeMaxResults() {
		final String[] geneSymbols = this.service.listGeneSymbols("G", -1);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("G", -1))));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListGeneSymbolsNullFilter() {
		this.service.listGeneSymbols(null, 10);
	}
	
	@Test
	public void testListStandardDrugNames() {
		final String[] drugNames = this.service.listStandardDrugNames("D", 10);
		
		assertThat(drugNames, is(arrayContaining(listStandardDrugNames("D", 10))));
	}
	
	@Test
	public void testListStandardDrugNamesNoMatch() {
		final String[] drugNames = this.service.listStandardDrugNames("X", 10);
		
		assertThat(drugNames, is(emptyArray()));
	}
	
	@Test
	public void testListStandardDrugNamesWithLimit() {
		final String[] drugNames = this.service.listStandardDrugNames("D", 1);
		
		assertThat(drugNames, is(arrayContaining(listStandardDrugNames("D", 1))));
	}
	
	@Test
	public void testListStandardDrugNamesEmptyFilter() {
		final String[] drugNames = this.service.listStandardDrugNames("", 10);
		
		assertThat(drugNames, is(arrayContaining(listStandardDrugNames("", 10))));
	}
	
	@Test
	public void testListStandardDrugNamesNegativeMaxResults() {
		final String[] drugNames = this.service.listStandardDrugNames("D", -1);
		
		assertThat(drugNames, is(arrayContaining(listStandardDrugNames("D", -1))));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListStandardDrugNamesNullFilter() {
		this.service.listStandardDrugNames(null, 10);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithNullGenes() {
		service.list(null, null, null, null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenes() {
		service.list(emptySet(), null, null, null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyDrugs() {
		service.list(null, emptySet(), null, null, null, null);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenesAndDrugs() {
		service.list(emptySet(), emptySet(), null, null, null, null);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithGenesAndDrugs() {
		service.list(singleton(singleGeneSymbolDirect()), singleton(singleDrugName()), null, null, null, null);
	}
	
	@Test
	public void testListByGenesNoResult() {
		final GeneDrugGroupInfos result = this.service.list(
			singleton(absentGeneSymbol()), null, null, null, null, null
		);
		
		assertThat(result.getGeneDrugs(), is(empty()));
	}
	
	@Test
	public void testListByGenesSingleGeneDirect() {
		final GeneDrugGroupInfos result = this.service.list(
			singleton(singleGeneSymbolDirect()), null, null, null, null, null
		);

		assertThat(result, is(equalsToGeneDrugGroupInfos(singleGeneGroupInfosDirect())));
	}
	
	@Test
	public void testListByGenesMultipleGeneDirect() {
		final GeneDrugGroupInfos result = this.service.list(
			stream(multipleGeneSymbolsDirect()).collect(toSet()),
			null, null, null, null, null
		);

		assertThat(result, is(equalsToGeneDrugGroupInfos(multipleGeneGroupInfosDirect())));
	}
	
	@Test
	public void testListByGenesSingleGeneIndirect() {
		final GeneDrugGroupInfos result = this.service.list(
			singleton(singleGeneSymbolIndirect()), null, null, null, null, null
		);

		assertThat(result, is(equalsToGeneDrugGroupInfos(singleGeneGroupInfosIndirect())));
	}
	
	@Test
	public void testListByGenesMultipleGeneIndirect() {
		final GeneDrugGroupInfos result = this.service.list(
			stream(multipleGeneSymbolsIndirect()).collect(toSet()),
			null, null, null, null, null
		);

		assertThat(result, is(equalsToGeneDrugGroupInfos(multipleGeneGroupInfosIndirect())));
	}
	
	@Test
	public void testListByGenesMultipleGeneMixed() {
		final GeneDrugGroupInfos result = this.service.list(
			stream(multipleGeneSymbolsMixed()).collect(toSet()),
			null, null, null, null, null
		);

		assertThat(result, is(equalsToGeneDrugGroupInfos(multipleGeneGroupInfosMixed())));
	}
	
	@Test
	public void testListByDrugsNoResult() {
		final GeneDrugGroupInfos result = this.service.list(
			null, singleton(absentDrugName()), null, null, null, null
		);
		
		assertThat(result.getGeneDrugs(), is(empty()));
	}
	
	@Test
	public void testSearchByDrugSingle() {
		final GeneDrugGroupInfos result = this.service.list(
			null, singleton(singleDrugName()), null, null, null, null
		);
		
		assertThat(result, is(equalsToGeneDrugGroupInfos(singleDrugGeneDrugGroupsInfos())));
	}
	
	@Test
	public void testSearchByDrugMultiple() {
		final GeneDrugGroupInfos result = this.service.list(
			null, stream(multipleDrugNames()).collect(toSet()), null, null, null, null
		);
		
		assertThat(result, is(equalsToGeneDrugGroupInfos(multipleDrugGeneDrugGroupsMixed())));
	}
	
	@Test(expected = BadRequestException.class)
	public void testListRankedWithNullGenes() {
		service.listRanked(null, null, null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testListRankedWithEmptyGenes() {
		service.listRanked(new GeneRanking(emptyMap()), null, null, null, null);
	}
	
	@Test
	public void testListRankedNoResult() {
		final GeneDrugGroupInfos result = this.service.listRanked(
			rankingFor(absentGeneSymbol()), null, null, null, null
		);
		
		assertThat(result.getGeneDrugs(), is(empty()));
	}
	
	@Test
	public void testListRankedSingleGeneDirect() {
		final GeneRanking ranking = rankingFor(singleGeneSymbolDirect());
		
		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null
		);
		
		assertThat(result, is(equalsToGeneDrugGroupInfos(singleGeneGroupInfosDirect())));
	}
	
	@Test
	public void testListRankedMultipleGeneDirect() {
		final GeneRanking ranking = rankingFor(multipleGeneSymbolsDirect());
		
		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null
		);
		
		assertThat(result, is(equalsToGeneDrugGroupInfos(multipleGeneGroupInfosDirect())));
	}
	
	@Test
	public void testListRankedSingleGeneIndirect() {
		final GeneRanking ranking = rankingFor(singleGeneSymbolIndirect());
		
		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null
		);
		
		assertThat(result, is(equalsToGeneDrugGroupInfos(singleGeneGroupInfosIndirect())));
	}
	
	@Test
	public void testListRankedMultipleGeneIndirect() {
		final GeneRanking ranking = rankingFor(multipleGeneSymbolsIndirect());
		
		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null
		);

		assertThat(result, is(equalsToGeneDrugGroupInfos(multipleGeneGroupInfosIndirect())));
	}
	
	@Test
	public void testListRankedMultipleGeneMixed() {
		final GeneRanking ranking = rankingFor(multipleGeneSymbolsMixed());
		
		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null
		);

		assertThat(result, is(equalsToGeneDrugGroupInfos(multipleGeneGroupInfosMixed())));
	}
}
