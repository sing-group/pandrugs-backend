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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.IsEqualToGene.equalsToGene;
import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.IsEqualToPathway.containsPathways;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.absentGeneSymbol;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.genes;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.genesWithPathway;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDataset.genesWithoutPathway;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

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

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Pathway;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
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
public class DefaultGeneDAOIntegrationTest {
	@Inject
	@Named("defaultGeneDAO")
	private GeneDAO dao;
	
	@Test
	public void testGet() {
		for (Gene gene : genes()) {
			assertThat(dao.get(gene.getGeneSymbol()), is(equalsToGene(gene)));
		}
	}
	
	@Test
	public void testGetAbsent() {
		assertThat(dao.get(absentGeneSymbol()), is(nullValue()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetNull() {
		dao.get(null);
	}
	
	@Test
	public void testGetNoPathways() {
		final Gene[] genes = genesWithoutPathway();
		
		for (Gene expectedGene : genes) {
			final Gene actualGene = dao.get(expectedGene.getGeneSymbol());
			
			assertThat(actualGene, is(equalsToGene(expectedGene)));
			assertThat(actualGene.getPathways(), is(empty()));
		}
	}
	
	@Test
	public void testGetPathways() {
		final Gene[] genes = genesWithPathway();
		
		for (Gene expectedGene : genes) {
			final Gene actualGene = dao.get(expectedGene.getGeneSymbol());
			final Pathway[] expectedPathways = expectedGene.getPathways().stream().toArray(Pathway[]::new);
			
			assertThat(actualGene, is(equalsToGene(expectedGene)));
			assertThat(actualGene.getPathways(), containsPathways(expectedPathways));
		}
	}
}
