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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneIndirect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.genedrug.xml")
public class DefaultGeneDrugDAOIntegrationTest {
	@Inject
	@Named("defaultGeneDrugDAO")
	private GeneDrugDAO dao;
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchEmptyGenes() {
		this.dao.searchWithIndirects(new String[0]);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchNullGenes() {
		this.dao.searchWithIndirects((String[]) null);
	}
	
	@Test
	public void testSearchNoResult() {
		final List<GeneDrug> result = this.dao.searchWithIndirects("ABSENT GENE");
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testSearchSingleGeneDirect() {
		final List<GeneDrug> result = this.dao.searchWithIndirects("DIRECT GENE 1");
		
		assertThat(result, containsInAnyOrder(singleGeneDirect()));
	}
	
	@Test
	public void testSearchMultipleGeneDirect() {
		final List<GeneDrug> result = this.dao.searchWithIndirects(
			"DIRECT GENE 1", "DIRECT GENE 2"
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneDirect()));
	}
	
	@Test
	public void testSearchSingleGeneIndirect() {
		final List<GeneDrug> result = this.dao.searchWithIndirects(
			"IG1"
		);
		
		assertThat(result, containsInAnyOrder(singleGeneIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneIndirect() {
		final List<GeneDrug> result = this.dao.searchWithIndirects(
			"IG1", "IG2"
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneMixed() {
		final List<GeneDrug> result = this.dao.searchWithIndirects(
			"DIRECT GENE 1", "DIRECT GENE 2", "IG1", "IG2"
		);
		
		assertThat(result, containsInAnyOrder(multipleGeneMixed()));
	}
}
