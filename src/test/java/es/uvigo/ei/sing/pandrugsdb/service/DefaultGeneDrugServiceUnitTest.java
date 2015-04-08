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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasAHTTPStatusMatcher.hasHTTPStatus;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.geneDrugs;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.geneDrugsIds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.controller.GeneDrugController;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugBasicInfo;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugBasicInfos;

@RunWith(EasyMockRunner.class)
public class DefaultGeneDrugServiceUnitTest {
	@TestSubject
	private DefaultGeneDrugService service = new DefaultGeneDrugService();

	@Mock
	private GeneDrugController controller;
	
	@Test
	public void testList() {
		testList(null, null);
	}
	
	@Test
	public void testListWithStartPosition() {
		testList(5, null);
	}
	
	@Test
	public void testListWithMaxResults() {
		testList(null, 5);
	}
	
	@Test
	public void testListWithStartPositionAndMaxResults() {
		testList(5, 5);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithNullGenes() {
		service.list(null, null, null);
	}
	
	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenes() {
		service.list(emptyList(), null, null);
	}
	
	@Test(expected = InternalServerErrorException.class)
	public void testListUnexpectedException() {
		expect(controller.searchForGeneDrugs(anyObject(), anyObject(), anyObject()))
			.andThrow(new RuntimeException("Unexpected exception"));
		
		replay(controller);
		
		service.list(asList(geneDrugsIds()), null, null);
	}
	
	private void testList(Integer startPosition, Integer maxResults) {
		final String[] genes = geneDrugsIds();
		final List<GeneDrug> geneDrugList = geneDrugs();
		final List<GeneDrugBasicInfo> expectedInfos = geneDrugList.stream()
			.map(GeneDrugBasicInfo::new).collect(toList());
		
		expect(controller.searchForGeneDrugs(aryEq(genes), eq(startPosition), eq(maxResults)))
			.andReturn(geneDrugList);
		
		replay(controller);
		
		final Response response = service.list(asList(genes), startPosition, maxResults);
		
		assertThat(response, hasHTTPStatus(Status.OK));
		assertThat(response.getEntity(), is(instanceOf(GeneDrugBasicInfos.class)));
		
		final GeneDrugBasicInfos infos = (GeneDrugBasicInfos) response.getEntity();
		assertEquals(expectedInfos, infos.getGeneDrugs());
	}
}
