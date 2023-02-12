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

package es.uvigo.ei.sing.pandrugs.controller;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToDrug.containsDrugs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.*;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.inactiveDrugNames;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.listDrugs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.listGeneSymbols;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugNames;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupGeneDependency;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupMixedOnlyDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupMixedOnlyGeneDependency;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupMixedOnlyIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupMixedOnlyPathwayMember;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupPathwayMember;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDrugGroupsByDrugs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsGeneDependency;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsPathwayMember;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.rankingFor;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugGroupByDrug;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugGroupDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugGroupGeneDependency;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugGroupPathwayMember;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolGeneDependency;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolPathwayMember;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

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

import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class
})
@DirtiesContext
@DatabaseSetup("file:src/test/resources/META-INF/dataset.genedrug.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.genedrug.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
//TODO: compare dscore and gscore
public class DefaultGeneDrugControllerIntegrationTest {
	@Inject
	@Named("defaultGeneDrugController")
	private GeneDrugController controller;
	
	@Test
	public void testListGeneSymbols() {
		final String[] geneSymbols = this.controller.listGeneSymbols("D", 10);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("D", 10))));
	}
	
	@Test
	public void testListGeneSymbolsNoMatch() {
		final String[] geneSymbols = this.controller.listGeneSymbols("X", 10);
		
		assertThat(geneSymbols, is(emptyArray()));
	}
	
	@Test
	public void testListGeneSymbolsWithLimit() {
		final String[] geneSymbols = this.controller.listGeneSymbols("D", 1);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("D", 1))));
	}
	
	@Test
	public void testListGeneSymbolsEmptyFilter() {
		final String[] geneSymbols = this.controller.listGeneSymbols("", 10);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("", 10))));
	}
	
	@Test
	public void testListGeneSymbolsNegativeMaxResults() {
		final String[] geneSymbols = this.controller.listGeneSymbols("G", -1);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("G", -1))));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListGeneSymbolsNullFilter() {
		this.controller.listGeneSymbols(null, 10);
	}
	
	@Test
	public void testlistDrugs() {
		final Drug[] drugs = this.controller.listDrugs("D", 10);
		
		assertThat(asList(drugs), containsDrugs(listDrugs("D", 10)));
	}
	
	@Test
	public void testlistDrugsNoMatch() {
		final Drug[] drugs = this.controller.listDrugs("X", 10);
		
		assertThat(drugs, is(emptyArray()));
	}
	
	@Test
	public void testlistDrugsWithLimit() {
		final Drug[] drugs = this.controller.listDrugs("D", 1);
		
		assertThat(asList(drugs), containsDrugs(listDrugs("D", 1)));
	}
	
	@Test
	public void testListDrugsEmptyFilter() {
		final Drug[] drugs = this.controller.listDrugs("", 10);
		
		assertThat(asList(drugs), containsDrugs(listDrugs("", 10)));
	}
	
	@Test
	public void testListDrugsNegativeMaxResults() {
		final Drug[] drugs = this.controller.listDrugs("D", -1);
		
		assertThat(asList(drugs), containsDrugs(listDrugs("D", -1)));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListDrugsNullFilter() {
		this.controller.listDrugs(null, 10);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchByDrugsNullQueryParameters() {
		this.controller.searchByDrugs(null, new String[] { "Drug" });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchByDrugsEmptyGenes() {
		this.controller.searchByDrugs(new GeneDrugQueryParameters(), new String[0]);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchByDrugsNullGenes() {
		this.controller.searchByDrugs(new GeneDrugQueryParameters(), (String[]) null);
	}
	
	@Test
	public void testSearchByDrugAbsent() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String drugNames = absentDrugName();
		
		assertThat(this.controller.searchByDrugs(queryParameters, drugNames), is(empty()));
	}
	
	@Test
	public void testSearchByDrugInactive() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] drugNames = inactiveDrugNames();
		
		assertThat(this.controller.searchByDrugs(queryParameters, drugNames), is(empty()));
	}
	
	@Test
	public void testSearchByDrugSingle() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String drugNames = singleDrugName();
		
		final List<GeneDrugGroup> groups = this.controller.searchByDrugs(queryParameters, drugNames);
		
		assertThat(groups, containsInAnyOrder(singleGeneDrugGroupByDrug()));
	}
	
	@Test
	public void testSearchByDrugMultiple() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] drugNames = multipleDrugNames();
		
		final List<GeneDrugGroup> groups = this.controller.searchByDrugs(queryParameters, drugNames);
		
		assertThat(groups, containsInAnyOrder(multipleGeneDrugGroupsByDrugs()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchByGenesNullQueryParameters() {
		this.controller.searchByGenes(null, new String[] { "IG" });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchByGenesEmptyGenes() {
		this.controller.searchByGenes(new GeneDrugQueryParameters(), new String[0]);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchByGenesNullGenes() {
		this.controller.searchByGenes(new GeneDrugQueryParameters(), (String[]) null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRankedSearchEmptyGenes() {
		this.controller.searchByRanking(new GeneDrugQueryParameters(), new GeneRanking(emptyMap()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testRankedSearchNullGeneRanking() {
		this.controller.searchByRanking(new GeneDrugQueryParameters(), (GeneRanking) null);
	}
	
	@Test
	public void testSearchByGenesNoResult() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), absentGeneSymbol());
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testRankedSearchNoResult() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(absentGeneSymbol())
		);
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testSearchByGenesSingleDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), singleGeneSymbolDirect());
		
		assertThat(result, containsInAnyOrder(singleGeneDrugGroupDirect()));
	}
	
	@Test
	public void testRankedSearchSingleDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(singleGeneSymbolDirect())
		);
		
		assertThat(result, containsInAnyOrder(singleGeneDrugGroupDirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), 
			multipleGeneSymbolsDirect()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyDirect()));
	}
	
	@Test
	public void testRankedSearchMultipleDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(multipleGeneSymbolsDirect())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyDirect()));
	}
	
	@Test
	public void testSearchByGenesSinglePathwayMember() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(),  singleGeneSymbolPathwayMember()
		);
		
		assertThat(result, containsInAnyOrder(singleGeneDrugGroupPathwayMember()));
	}
	
	@Test
	public void testRankedSearchSinglePathwayMember() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(singleGeneSymbolPathwayMember())
		);
		
		assertThat(result, containsInAnyOrder(singleGeneDrugGroupPathwayMember()));
	}
	
	@Test
	public void testSearchByGenesMultipleOnlyPathwayMember() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), multipleGeneSymbolsOnlyPathwayMember()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupOnlyPathwayMember()));
	}
	
	@Test
	public void testRankedSearchMultipleOnlyPathwayMember() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(multipleGeneSymbolsOnlyPathwayMember())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupOnlyPathwayMember()));
	}
	
	@Test
	public void testSearchByGenesMultiplePathwayMember() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), multipleGeneSymbolsPathwayMember()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupPathwayMember()));
	}
	
	@Test
	public void testRankedSearchMultiplePathwayMember() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(multipleGeneSymbolsPathwayMember())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupPathwayMember()));
	}
	
	@Test
	public void testSearchByGenesSingleGeneDependency() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(),  singleGeneSymbolGeneDependency()
		);
		
		assertThat(result, containsInAnyOrder(singleGeneDrugGroupGeneDependency()));
	}
	
	@Test
	public void testRankedSearchSingleGeneDependency() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(singleGeneSymbolGeneDependency())
		);
		
		assertThat(result, containsInAnyOrder(singleGeneDrugGroupGeneDependency()));
	}
	
	@Test
	public void testSearchByGenesMultipleOnlyGeneDependency() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), multipleGeneSymbolsOnlyGeneDependency()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupOnlyGeneDependency()));
	}
	
	@Test
	public void testRankedSearchMultipleOnlyGeneDependency() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(multipleGeneSymbolsOnlyGeneDependency())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupOnlyGeneDependency()));
	}
	
	@Test
	public void testSearchByGenesMultipleGeneDependency() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), multipleGeneSymbolsGeneDependency()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupGeneDependency()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneDependency() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(multipleGeneSymbolsGeneDependency())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupGeneDependency()));
	}
	
	@Test
	public void testSearchByGenesMultipleIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), multipleGeneSymbolsIndirect()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyIndirect()));
	}
	
	@Test
	public void testRankedSearchMultipleIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(multipleGeneSymbolsIndirect())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyIndirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleMixed() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), 
			multipleGeneSymbolsMixed()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixed()));
	}
	
	@Test
	public void testRankedSearchMultipleMixed() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), 
			rankingFor(multipleGeneSymbolsMixed())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixed()));
	}
	
	@Test
	public void testSearchByGenesMultipleMixedOnlyDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(
				GeneDrugQueryParameters.DEFAULT_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_NON_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_CANCER_TYPES,
				true,
				true,
				false,
				false
			), 
			multipleGeneSymbolsMixed()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyDirect()));
	}
	
	@Test
	public void testRankedSearchByGenesMultipleMixedOnlyDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(
				GeneDrugQueryParameters.DEFAULT_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_NON_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_CANCER_TYPES,
				true,
				true,
				false,
				false
			), 
			rankingFor(multipleGeneSymbolsMixed())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyDirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleMixedOnlyIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(
				GeneDrugQueryParameters.DEFAULT_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_NON_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_CANCER_TYPES,
				false,
				false,
				true,
				true
			), 
			multipleGeneSymbolsMixed()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyIndirect()));
	}
	
	@Test
	public void testRankedSearchByGenesMultipleMixedOnlyIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(
				GeneDrugQueryParameters.DEFAULT_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_NON_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_CANCER_TYPES,
				false,
				false,
				true,
				true
			), 
			rankingFor(multipleGeneSymbolsMixed())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyIndirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleMixedOnlyPathwayMember() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(
				GeneDrugQueryParameters.DEFAULT_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_NON_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_CANCER_TYPES,
				false,
				false,
				true,
				false
			), 
			multipleGeneSymbolsMixed()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyPathwayMember()));
	}
	
	@Test
	public void testSearchByGenesMultipleMixedOnlyGeneDependency() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(
				GeneDrugQueryParameters.DEFAULT_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_NON_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_CANCER_TYPES,
				false,
				false,
				false,
				true
			), 
			multipleGeneSymbolsMixed()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDrugGroupMixedOnlyGeneDependency()));
	}
}
