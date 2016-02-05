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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.IsEqualToGeneDrugGroupInfos.equalsToGeneDrugGroupInfos;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupInfosMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.multipleGeneGroupMixed;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupIndirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupInfosDirect;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.singleGeneGroupInfosIndirect;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.controller.GeneDrugController;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;

@RunWith(EasyMockRunner.class)
//TODO: compare dscore and gscore
public class DefaultGeneDrugServiceUnitTest {
	@TestSubject
	private DefaultGeneDrugService service = new DefaultGeneDrugService();

	@Mock
	private GeneDrugController controller;
	
	@After
	public void verifyController() {
		EasyMock.verify(controller);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithNullGenes() {
		replay(controller);
	
		service.list(null, null, null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenes() {
		replay(controller);
		
		service.list(emptySet(), null, null, null, null);
	}
	
	@Test
	public void testSearchNoResult() {
		final String query = "Absent gene";
		
		expect(controller.searchForGeneDrugs(anyObject(), eq(query)))
			.andReturn(emptyList());
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.list(
			singleton(query), null, null, null, null);
		
		assertThat(result.getGeneDrugs(), is(empty()));
	}
	
	@Test
	public void testSearchSingleGeneDirect() {
		final String query = "Direct Gene 1";
		
		expect(controller.searchForGeneDrugs(anyObject(), eq(query)))
			.andReturn(asList(singleGeneGroupDirect()));
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.list(
			singleton(query), null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(singleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testSearchMultipleGeneDirect() {
		final String[] query = new String[] {"Direct Gene 1", "Direct Gene 2"};
		
		expect(controller.searchForGeneDrugs(anyObject(), eq("Direct Gene 1"), eq("Direct Gene 2")))
			.andReturn(asList(multipleGeneGroupDirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.list(
			new HashSet<>(asList(query)), null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testSearchSingleGeneIndirect() {
		final String query = "IG1";
		
		expect(controller.searchForGeneDrugs(anyObject(), eq(query)))
			.andReturn(asList(singleGeneGroupIndirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.list(
			singleton(query), null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(singleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneIndirect() {
		final String[] query = new String[] {"IG1", "IG2"};
		
		expect(controller.searchForGeneDrugs(anyObject(), eq("IG1"), eq("IG2")))
			.andReturn(asList(multipleGeneGroupIndirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.list(
			new HashSet<>(asList(query)), null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testSearchMultipleGeneMixed() {
		final String[] query = new String[] {
			"Direct Gene 1", "Direct Gene 2", "IG1", "IG2"
		};
		
		expect(controller.searchForGeneDrugs(
			anyObject(), eq("Direct Gene 1"), eq("Direct Gene 2"), eq("IG1"), eq("IG2")
		)).andReturn(asList(multipleGeneGroupMixed()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.list(
			new LinkedHashSet<>(asList(query)), null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosMixed()));
	}
	
	@Test
	public void testSearchMultipleGeneMixedRepeatedGenes() {
		final String[] query = new String[] {
			"Direct Gene 1", "Direct Gene 1", "Direct Gene 2", "IG1", "Direct Gene 1", "IG2", "IG1", "IG2"
		};
		
		expect(controller.searchForGeneDrugs(
			anyObject(), eq("Direct Gene 1"), eq("Direct Gene 2"), eq("IG1"), eq("IG2")
		)).andReturn(asList(multipleGeneGroupMixed()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.list(
			new LinkedHashSet<>(asList(query)), null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosMixed()));
	}
	
	@Test(expected = BadRequestException.class)
	public void testRankedSearchWithNullGenes() {
		replay(controller);
	
		service.listRanked((GeneRanking) null, null, null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testRankedSearchWithEmptyGenes() {
		replay(controller);
		
		service.listRanked(new GeneRanking(emptyMap()), null, null, null, null);
	}
	
	@Test
	public void testRankedSearchNoResult() {
		final GeneRanking ranking = new GeneRanking(singletonMap("Absent gene", 1d));
		
		expect(controller.searchForGeneDrugs(anyObject(), eq(ranking)))
			.andReturn(emptyList());
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result.getGeneDrugs(), is(empty()));
	}
	
	@Test
	public void testRankedSearchSingleGeneDirect() {
		final GeneRanking ranking = new GeneRanking(singletonMap("Direct Gene 1", 1d));
		
		expect(controller.searchForGeneDrugs(anyObject(), eq(ranking)))
			.andReturn(asList(singleGeneGroupDirect()));
		
		replay(controller);
		
		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(singleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneDirect() {
		final Map<String, Double> rank = new LinkedHashMap<>();
		rank.put("Direct Gene 1", 1d);
		rank.put("Direct Gene 2", 2d);
		final GeneRanking ranking = new GeneRanking(rank);
		
		expect(controller.searchForGeneDrugs(anyObject(), eq(ranking)))
			.andReturn(asList(multipleGeneGroupDirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosDirect()));
	}
	
	@Test
	public void testRankedSearchSingleGeneIndirect() {
		final GeneRanking ranking = new GeneRanking(singletonMap("IG1", 1d));
		
		expect(controller.searchForGeneDrugs(anyObject(), eq(ranking)))
			.andReturn(asList(singleGeneGroupIndirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(singleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneIndirect() {
		final Map<String, Double> rank = new LinkedHashMap<>();
		rank.put("IG1", 1d);
		rank.put("IG2", 2d);
		final GeneRanking ranking = new GeneRanking(rank);
		
		expect(controller.searchForGeneDrugs(anyObject(), eq(ranking)))
			.andReturn(asList(multipleGeneGroupIndirect()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosIndirect()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneMixed() {
		final Map<String, Double> rank = new LinkedHashMap<>();
		rank.put("Direct Gene 1", 1d);
		rank.put("Direct Gene 2", 2d);
		rank.put("IG1", 3d);
		rank.put("IG2", 4d);
		final GeneRanking ranking = new GeneRanking(rank);
		
		expect(controller.searchForGeneDrugs(anyObject(), eq(ranking)))
			.andReturn(asList(multipleGeneGroupMixed()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosMixed()));
	}
	
	@Test
	public void testRankedSearchMultipleGeneMixedRepeatedGenes() {
		final Map<String, Double> rank = new LinkedHashMap<>();
		rank.put("Direct Gene 1", 1d);
		rank.put("Direct Gene 1", 1d);
		rank.put("Direct Gene 2", 2d);
		rank.put("IG1", 3d);
		rank.put("Direct Gene 1", 1d);
		rank.put("IG2", 4d);
		rank.put("IG1", 3d);
		rank.put("IG2", 4d);
		final GeneRanking ranking = new GeneRanking(rank);
		
		expect(controller.searchForGeneDrugs(
			anyObject(), eq(ranking)
		)).andReturn(asList(multipleGeneGroupMixed()));
		
		replay(controller);

		final GeneDrugGroupInfos result = this.service.listRanked(
			ranking, null, null, null, null);

		assertThat(result, equalsToGeneDrugGroupInfos(multipleGeneGroupInfosMixed()));
	}
}
