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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.geneDrugs;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.geneDrugsIds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.List;

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

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugBasicInfo;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugBasicInfos;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@Transactional
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	DbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.genedrug.xml")
@ExpectedDatabase(value = "file:src/test/resources/META-INF/dataset.genedrug.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
public class DefaultGeneDrugServiceIntegrationTest {
	@Inject
	@Named("defaultGeneDrugService")
	private GeneDrugService service;
	
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
//TODO: Check bounds
//	@Test(expected = BadRequestException.class)
//	public void testListStartPositionOutOfBounds() {
//		service.list(asList(geneDrugsIds()), 1000, null);
//	}

	@Test(expected = BadRequestException.class)
	public void testListWithNullGenes() {
		service.list(null, null, null);
	}

	@Test(expected = BadRequestException.class)
	public void testListWithEmptyGenes() {
		service.list(emptyList(), null, null);
	}
	
	private void testList(Integer startPosition, Integer maxResults) {
		final String[] genes = geneDrugsIds();
		final List<GeneDrug> geneDrugs = geneDrugs();
		
		final int fromIndex = startPosition == null ? 0 : startPosition;
		final int toIndex = maxResults == null ? geneDrugs.size()
			: Math.min(geneDrugs.size(), fromIndex + maxResults);
		
		final List<GeneDrug> geneDrugList = geneDrugs.subList(fromIndex, toIndex);
		final List<GeneDrugBasicInfo> expectedInfos = geneDrugList.stream()
			.map(GeneDrugBasicInfo::new).collect(toList());
		
		final GeneDrugBasicInfos infos = service.list(asList(genes), startPosition, maxResults);

		assertEquals(expectedInfos, infos.getGeneDrugs());
	}
}
