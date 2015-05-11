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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneIndirect;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneDrugDAO;

@RunWith(EasyMockRunner.class)
public class DefaultGeneDrugControllerUnitTest {
	@TestSubject
	private DefaultGeneDrugController controller =
		new DefaultGeneDrugController();
	
	@Mock
	private GeneDrugDAO dao;
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchEmptyGenes() {
		final String[] query = new String[0];
		
		expect(dao.searchWithIndirects(query))
			.andThrow(new IllegalArgumentException());
		
		replay(dao);
		
		this.controller.searchForGeneDrugs(query);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSearchNullPointerException() {
		final String[] query = null;
		
		expect(dao.searchWithIndirects(query))
			.andThrow(new NullPointerException());
		
		replay(dao);
		
		this.controller.searchForGeneDrugs(query);
	}
	
	@Test
	public void testSearchNoResult() {
		final String query = "Absent gene";
		
		expect(dao.searchWithIndirects(query))
			.andReturn(emptyList());
		
		replay(dao);
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(query);
		
		assertThat(result, is(empty()));
	}
	
	@Test
	public void testSearchSingleGeneDirect() {
		final String query = "Direct Gene 1";
		
		expect(dao.searchWithIndirects(query))
			.andReturn(asList(singleGeneDirect()));
		
		replay(dao);
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(query);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchMultipleGeneDirect() {
		final String[] query = new String[] {"Direct Gene 1", "Direct Gene 2"};
		
		expect(dao.searchWithIndirects(query))
			.andReturn(asList(multipleGeneDirect()));
		
		replay(dao);
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(query);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupDirect()));
	}
	
	@Test
	public void testSearchSingleGeneIndirect() {
		final String query = "IG1";
		
		expect(dao.searchWithIndirects(query))
			.andReturn(asList(singleGeneIndirect()));
		
		replay(dao);
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(query);
		
		assertThat(result, containsInAnyOrder(singleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneIndirect() {
		final String[] query = new String[] {"IG1", "IG2"};
		
		expect(dao.searchWithIndirects(query))
			.andReturn(asList(multipleGeneIndirect()));
		
		replay(dao);
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(query);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneMixed() {
		final String[] query = new String[] {
			"Direct Gene 1", "Direct Gene 2", "IG1", "IG2"
		};
		
		expect(dao.searchWithIndirects(query))
			.andReturn(asList(multipleGeneMixed()));
		
		replay(dao);
		
		final List<GeneDrugGroup> result = this.controller.searchForGeneDrugs(query);
		
		assertThat(result, containsInAnyOrder(multipleGeneGroupMixed()));
	}
}
