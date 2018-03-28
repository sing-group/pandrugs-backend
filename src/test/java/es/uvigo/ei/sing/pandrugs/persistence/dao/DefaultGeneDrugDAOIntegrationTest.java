/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

package es.uvigo.ei.sing.pandrugs.persistence.dao;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToDrug.containsDrugs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.absentDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.geneDrugsWithDrug;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.listDrugs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.listGeneSymbols;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleDrugNames;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.multipleGeneSymbolsMixed;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleDrugName;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneDrugDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneIndirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolDirect;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugDataset.singleGeneSymbolIndirect;
import static java.util.Arrays.asList;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.query.GeneDrugQueryParameters;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.genedrug.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.genedrug.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultGeneDrugDAOIntegrationTest {
	@Inject
	@Named("defaultGeneDrugDAO")
	private GeneDrugDAO dao;
	
	@Test
	public void testListGeneSymbols() {
		final String[] geneSymbols = this.dao.listGeneSymbols("D", 10);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("D", 10))));
	}
	
	@Test
	public void testListGeneSymbolsNoMatch() {
		final String[] geneSymbols = this.dao.listGeneSymbols("X", 10);
		
		assertThat(geneSymbols, is(emptyArray()));
	}
	
	@Test
	public void testListGeneSymbolsWithLimit() {
		final String[] geneSymbols = this.dao.listGeneSymbols("D", 1);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("D", 1))));
	}
	
	@Test
	public void testListGeneSymbolsEmptyFilter() {
		final String[] geneSymbols = this.dao.listGeneSymbols("", 10);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("", 10))));
	}
	
	@Test
	public void testListGeneSymbolsNegativeMaxResults() {
		final String[] geneSymbols = this.dao.listGeneSymbols("G", -1);
		
		assertThat(geneSymbols, is(arrayContaining(listGeneSymbols("G", -1))));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListGeneSymbolsNullFilter() {
		this.dao.listGeneSymbols(null, 10);
	}
	
	@Test
	public void testlistDrugs() {
		final Drug[] drugs = this.dao.listDrugs("D", 10);
		
		assertThat(asList(drugs), containsDrugs(listDrugs("D", 10)));
	}
	
	@Test
	public void testlistDrugsNoMatch() {
		final Drug[] drugs = this.dao.listDrugs("X", 10);
		
		assertThat(drugs, is(emptyArray()));
	}
	
	@Test
	public void testlistDrugsWithLimit() {
		final Drug[] drugs = this.dao.listDrugs("D", 1);
		
		assertThat(asList(drugs), containsDrugs(listDrugs("D", 1)));
	}
	
	@Test
	public void testlistDrugsEmptyFilter() {
		final Drug[] drugs = this.dao.listDrugs("", 10);
		
		assertThat(asList(drugs), containsDrugs(listDrugs("", 10)));
	}
	
	@Test
	public void testlistDrugsNegativeMaxResults() {
		final Drug[] drugs = this.dao.listDrugs("D", -1);
		
		assertThat(asList(drugs), containsDrugs(listDrugs("D", -1)));
	}
	
	@Test(expected = NullPointerException.class)
	public void testlistDrugsNullFilter() {
		this.dao.listDrugs(null, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchByGeneEmptyGenes() {
		this.dao.searchByGene(new GeneDrugQueryParameters(), new String[0]);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchByGeneNullGenes() {
		this.dao.searchByGene(new GeneDrugQueryParameters(), (String[]) null);
	}
	
	@Test
	public void testSearchByGeneNoResult() {
		final List<GeneDrug> result = this.dao.searchByGene(
			new GeneDrugQueryParameters(), absentGeneSymbol()
		);
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testSearchByGeneSingleGeneDirect() {
		final List<GeneDrug> result = this.dao.searchByGene(
			new GeneDrugQueryParameters(), singleGeneSymbolDirect()
		);

		assertThat(result, containsInAnyOrder(singleGeneDrugDirect()));
	}
	
	@Test
	public void testSearchByGeneMultipleGeneDirect() {
		final List<GeneDrug> result = this.dao.searchByGene(
			new GeneDrugQueryParameters(), multipleGeneSymbolsDirect()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDirect()));
	}
	
	@Test
	public void testSearchByGeneSingleGeneIndirect() {
		final List<GeneDrug> result = this.dao.searchByGene(
			new GeneDrugQueryParameters(), singleGeneSymbolIndirect()
		);
		
		assertThat(result, containsInAnyOrder(singleGeneIndirect()));
	}
	
	@Test
	public void testSearchByGeneMultipleGeneIndirect() {
		final List<GeneDrug> result = this.dao.searchByGene(
			new GeneDrugQueryParameters(), multipleGeneSymbolsIndirect()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneIndirect()));
	}
	
	@Test
	public void testSearchByGeneMultipleGeneMixed() {
		final List<GeneDrug> result = this.dao.searchByGene(
			new GeneDrugQueryParameters(), multipleGeneSymbolsMixed()
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneMixed()));
	}
	
	@Test
	public void testSearchByDrugAbsent() {
		testSearchByDrug(absentDrugName());
	}
	
	@Test
	public void testSearchByDrugSingle() {
		testSearchByDrug(singleDrugName());
	}
	
	@Test
	public void testSearchByDrugMultiple() {
		testSearchByDrug(multipleDrugNames());
	}
	
	private void testSearchByDrug(String ... drugNames) {
		final List<GeneDrug> result = this.dao.searchByDrug(
			new GeneDrugQueryParameters(), drugNames
		);

		assertThat(result, containsInAnyOrder(geneDrugsWithDrug(drugNames)));
	}
}
