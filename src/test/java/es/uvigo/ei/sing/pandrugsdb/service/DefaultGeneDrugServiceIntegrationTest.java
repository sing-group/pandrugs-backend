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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupInfosIndirect;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@Transactional
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	DbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.genedrug.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.genedrug.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT
)
public class DefaultGeneDrugServiceIntegrationTest {
	@Inject
	@Named("defaultGeneDrugService")
	private GeneDrugService service;
	
	@Test(expected = BadRequestException.class)
	public void testListWithNullGenes() {
		service.list(null, null, null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenes() {
		service.list(emptyList(), null, null, null, null);
	}
	
	@Test
	public void testSearchNoResult() {
		final GeneDrugGroupInfos result = this.service.list(
			asList("Absent Gene"), null, null, null, null
		);
		
		assertThat(result.getGeneDrugs(), is(empty()));
	}
	
	@Test
	public void testSearchSingleGeneDirect() {
		final GeneDrugGroupInfos result = this.service.list(
			asList("Direct Gene 1"), null, null, null, null
		);
		
		assertThat(result, is(singleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testSearchMultipleGeneDirect() {
		final GeneDrugGroupInfos result = this.service.list(
			asList("Direct Gene 1", "Direct Gene 2"), null, null, null, null
		);
		
		assertThat(result, is(multipleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testSearchSingleGeneIndirect() {
		final GeneDrugGroupInfos result = this.service.list(
			asList("IG1"), null, null, null, null);
		
		assertThat(result, is(singleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneIndirect() {
		final GeneDrugGroupInfos result = this.service.list(
			asList("IG1", "IG2"), null, null, null, null
		);
		
		assertThat(result, is(multipleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneMixed() {
		final GeneDrugGroupInfos result = this.service.list(
			asList("Direct Gene 1", "Direct Gene 2", "IG1", "IG2"),
			null, null, null, null
		);
		
		assertThat(result, is(multipleGeneGroupInfosMixed()));
	}
}
