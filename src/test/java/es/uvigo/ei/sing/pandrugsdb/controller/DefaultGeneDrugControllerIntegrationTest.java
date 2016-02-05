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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupIndirect;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.query.GeneQueryParameters;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;

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
//TODO: compare dscore and gscore
public class DefaultGeneDrugControllerIntegrationTest {
	@Inject
	@Named("defaultGeneDrugController")
	private GeneDrugController controller;
	
	@Test(expected = NullPointerException.class)
	public void testSearchNullQueryParameters() {
		this.controller.searchForGeneDrugs(null, new String[] { "IG" });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchEmptyGenes() {
		this.controller.searchForGeneDrugs(new GeneQueryParameters(), new String[0]);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchNullGenes() {
		this.controller.searchForGeneDrugs(new GeneQueryParameters(), (String[]) null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRankedSearchEmptyGenes() {
		this.controller.searchForGeneDrugs(new GeneQueryParameters(), new GeneRanking(emptyMap()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testRankedSearchNullGeneRanking() {
		this.controller.searchForGeneDrugs(new GeneQueryParameters(), (GeneRanking) null);
	}
	
	@Test
	public void testSearchNoResult() {
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), "Absent Gene");
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testRankedSearchNoResult() {
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), new GeneRanking(singletonMap("Absent Gene", 1d))
		);
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testSearchSingleGeneDirect() {
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), "Direct Gene 1");
		
		assertThat(result, containsInAnyOrder(singleGeneGroupDirect()));
	}
	
	@Test
	public void testRankedSearchSingleGeneDirect() {
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), new GeneRanking(singletonMap("Direct Gene 1", 1d))
		);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchMultipleGeneDirect() {
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), 
			"Direct Gene 1", "Direct Gene 2"
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneDirect() {
		final Map<String, Double> ranking = new HashMap<>();
		ranking.put("Direct Gene 1", 1d);
		ranking.put("Direct Gene 2", 2d);
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), new GeneRanking(ranking)
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchSingleGeneIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), "IG1"
		);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupIndirect()));
	}
	
	@Test
	public void testRankedSearchSingleGeneIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), new GeneRanking(singletonMap("IG1", 1d))
		);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneIndirect() {
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), "IG1", "IG2"
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneIndirect() {
		final Map<String, Double> ranking = new HashMap<>();
		ranking.put("IG1", 1d);
		ranking.put("IG2", 2d);
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), new GeneRanking(ranking)
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneMixed() {
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), 
			"Direct Gene 1", "Direct Gene 2", "IG1", "IG2"
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupMixed()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneMixed() {
		final Map<String, Double> ranking = new HashMap<>();
		ranking.put("Direct Gene 1", 1d);
		ranking.put("Direct Gene 2", 2d);
		ranking.put("IG1", 3d);
		ranking.put("IG2", 4d);
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(
			new GeneQueryParameters(), new GeneRanking(ranking)
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupMixed()));
	}
}
