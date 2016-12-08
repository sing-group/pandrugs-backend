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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.absentDrugName;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.listGeneSymbols;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.listStandardDrugNames;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleDrugGeneDrugGroups;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleDrugNames;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneSymbolsDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneSymbolsIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneSymbolsMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.rankingFor;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleDrugGeneDrugGroups;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleDrugName;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneSymbolDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneSymbolIndirect;
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

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.query.DirectIndirectStatus;
import es.uvigo.ei.sing.pandrugsdb.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;

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
	public void testListStandardDrugNames() {
		final String[] drugNames = this.controller.listStandardDrugNames("D", 10);
		
		assertThat(drugNames, is(arrayContaining(listStandardDrugNames("D", 10))));
	}
	
	@Test
	public void testListStandardDrugNamesNoMatch() {
		final String[] drugNames = this.controller.listStandardDrugNames("X", 10);
		
		assertThat(drugNames, is(emptyArray()));
	}
	
	@Test
	public void testListStandardDrugNamesWithLimit() {
		final String[] drugNames = this.controller.listStandardDrugNames("D", 1);
		
		assertThat(drugNames, is(arrayContaining(listStandardDrugNames("D", 1))));
	}
	
	@Test
	public void testListStandardDrugNamesEmptyFilter() {
		final String[] drugNames = this.controller.listStandardDrugNames("", 10);
		
		assertThat(drugNames, is(arrayContaining(listStandardDrugNames("", 10))));
	}
	
	@Test
	public void testListStandardDrugNamesNegativeMaxResults() {
		final String[] drugNames = this.controller.listStandardDrugNames("D", -1);
		
		assertThat(drugNames, is(arrayContaining(listStandardDrugNames("D", -1))));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListStandardDrugNamesNullFilter() {
		this.controller.listStandardDrugNames(null, 10);
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
	public void testSearchByDrugSingle() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String drugNames = singleDrugName();
		
		final List<GeneDrugGroup> groups = this.controller.searchByDrugs(queryParameters, drugNames);
		
		assertThat(groups, containsInAnyOrder(singleDrugGeneDrugGroups()));
	}
	
	@Test
	public void testSearchByDrugMultiple() {
		final GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters();
		final String[] drugNames = multipleDrugNames();
		
		final List<GeneDrugGroup> groups = this.controller.searchByDrugs(queryParameters, drugNames);
		
		assertThat(groups, containsInAnyOrder(multipleDrugGeneDrugGroups()));
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
	public void testSearchByGenesSingleGeneDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), singleGeneSymbolDirect());
		
		assertThat(result, containsInAnyOrder(singleGeneGroupDirect()));
	}
	
	@Test
	public void testRankedSearchSingleGeneDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(singleGeneSymbolDirect())
		);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleGeneDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), 
			multipleGeneSymbolsDirect()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(multipleGeneSymbolsDirect())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchByGenesSingleGeneIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(),  singleGeneSymbolIndirect()
		);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupIndirect()));
	}
	
	@Test
	public void testRankedSearchSingleGeneIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(singleGeneSymbolIndirect())
		);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleGeneIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), multipleGeneSymbolsIndirect()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(multipleGeneSymbolsIndirect())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleGeneMixed() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(), 
			multipleGeneSymbolsMixed()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupMixed()));
	}
	
	@Test
	public void testSearchByGenesMultipleGeneMixedOnlyDirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(
				GeneDrugQueryParameters.DEFAULT_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_NON_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_TARGET_MARKER,
				DirectIndirectStatus.DIRECT
			), 
			multipleGeneSymbolsMixed()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchByGenesMultipleGeneMixedOnlyIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchByGenes(
			new GeneDrugQueryParameters(
				GeneDrugQueryParameters.DEFAULT_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_NON_CANCER_DRUG_STATUS,
				GeneDrugQueryParameters.DEFAULT_TARGET_MARKER,
				DirectIndirectStatus.INDIRECT
			), 
			multipleGeneSymbolsMixed()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneMixed() {
		final List<GeneDrugGroup> result = this.controller.searchByRanking(
			new GeneDrugQueryParameters(), rankingFor(multipleGeneSymbolsMixed())
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupMixed()));
	}
}
