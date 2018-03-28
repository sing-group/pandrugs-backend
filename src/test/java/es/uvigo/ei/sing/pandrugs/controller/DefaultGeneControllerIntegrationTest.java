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

package es.uvigo.ei.sing.pandrugs.controller;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToGene.containsGenes;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.absentGeneSymbols;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.geneSymbolsWithInteractionDegreeUpTo;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.geneSymbolsWithMaxInteractionDegree;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.interactions;
import static org.junit.Assert.assertThat;

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

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
})
@DirtiesContext
@DatabaseSetup("file:src/test/resources/META-INF/dataset.gene.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.gene.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultGeneControllerIntegrationTest {
	@Inject
	@Named("defaultGeneController")
	private GeneController controller;
	
	@Test
	public void testInteractionsWithMaxDegree() {
		for (int degree = 0; degree <= 3; degree++) {
			final String[] queryGenes = geneSymbolsWithMaxInteractionDegree(degree);

			assertThat(controller.interactions(degree, queryGenes), containsGenes(interactions(degree, queryGenes)));
		}
	}

	@Test
	public void testInteractionsWithDegreeUpTo() {
		for (int degree = 0; degree <= 5; degree++) {
			final String[] queryGenes = geneSymbolsWithInteractionDegreeUpTo(degree);
			
			assertThat(controller.interactions(degree, queryGenes), containsGenes(interactions(degree, queryGenes)));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInteractionsNegativeDegree() {
		controller.interactions(-1, "GATA2");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInteractionsEmptyGeneSymbols() {
		controller.interactions(10);
	}
	
	@Test(expected = NullPointerException.class)
	public void testInteractionsNullGeneSymbols() {
		controller.interactions(10, "GATA2", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInteractionsEmptyGenes() {
		final String[] geneSymbols = absentGeneSymbols();
		
		controller.interactions(10, geneSymbols);
	}
}
