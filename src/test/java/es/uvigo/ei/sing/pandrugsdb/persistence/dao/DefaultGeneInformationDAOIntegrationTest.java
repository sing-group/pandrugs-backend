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
package es.uvigo.ei.sing.pandrugsdb.persistence.dao;

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.IsEqualToGeneInformation.equalsToGeneInformation;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.geneInformations;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformationDataset.geneSymbolsForQuery;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.inject.Named;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformation;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@WebAppConfiguration
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.gene.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.gene.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultGeneInformationDAOIntegrationTest {
	@Inject
	@Named("defaultGeneInformationDAO")
	private GeneInformationDAO dao;
	
	@Test
	public void testGet() {
		for (GeneInformation geneInfo : geneInformations()) {
			assertThat(dao.get(geneInfo.getGeneSymbol()), is(equalsToGeneInformation(geneInfo)));
		}
	}
	
	@Test
	public void testGetAbsent() {
		assertThat(dao.get(absentGeneSymbol()), is(CoreMatchers.nullValue()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetNull() {
		dao.get(null);
	}
	
	@Test
	public void testListGeneSymbols() {
		assertThat(dao.listGeneSymbols("C", 10), arrayContaining(geneSymbolsForQuery("C")));
	}
	
	@Test
	public void testListGeneSymbolsZeroMaxResults() {
		assertThat(dao.listGeneSymbols("C", 0), arrayContaining(geneSymbolsForQuery("C")));
	}
	
	@Test
	public void testListGeneSymbolsNegativeMaxResults() {
		assertThat(dao.listGeneSymbols("C", -1), arrayContaining(geneSymbolsForQuery("C")));
	}
	
	@Test
	public void testListGeneSymbolsOneMaxResults() {
		assertThat(dao.listGeneSymbols("C", 1), arrayContaining(geneSymbolsForQuery("C", 1)));
	}
	
	@Test
	public void testListGeneSymbolsLimitedMaxResults() {
		assertThat(dao.listGeneSymbols("P", 2), arrayContaining(geneSymbolsForQuery("P", 2)));
	}
	
	@Test
	public void testListGeneSymbolsEmptyResults() {
		assertThat(dao.listGeneSymbols("XYZ", 10), is(emptyArray()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testListGeneSymbolsNullQuery() {
		dao.listGeneSymbols(null, 0);
	}
}
