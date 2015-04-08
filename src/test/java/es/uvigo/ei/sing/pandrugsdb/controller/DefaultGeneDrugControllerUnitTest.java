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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasTheSameItemsAsMatcher.hasExactlyTheItems;
import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasTheSameItemsAsMatcher.hasTheSameItemsAs;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.geneDrugs;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.geneDrugsIds;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugDataset.presentGeneDrug;
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneDrugDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;

@RunWith(EasyMockRunner.class)
public class DefaultGeneDrugControllerUnitTest {
	@TestSubject
	private DefaultGeneDrugController controller =
		new DefaultGeneDrugController();
	
	@Mock
	private GeneDrugDAO dao;
	
	@Test
	public void testSearchSingleWithNullStartPositionAndMaxResutls() {
		final GeneDrug expected = presentGeneDrug();
		final String[] geneNames = { expected.getGeneSymbol() };
		
		expect(dao.search(geneNames, null, null))
			.andReturn(asList(expected));
		replay(dao);
		
		final List<GeneDrug> result = controller.searchForGeneDrugs(
			geneNames, null, null);
		
		assertThat(result, hasExactlyTheItems(expected));
	}
	
	@Test
	public void testSearchMultipleWithNullStartPositionAndMaxResutls() {
		final List<GeneDrug> expected = geneDrugs().subList(2, 6);
		final String[] geneNames = expected.stream()
			.map(GeneDrug::getGeneSymbol)
		.toArray(String[]::new);
		
		expect(dao.search(geneNames, null, null)).andReturn(expected);
		replay(dao);
		
		final List<GeneDrug> result = controller.searchForGeneDrugs(
			geneNames, null, null);
		
		assertThat(result, hasTheSameItemsAs(expected));
	}
	
	@Test
	public void testSearchStartAt3() {
		final List<GeneDrug> geneDrugs = geneDrugs();
		final List<GeneDrug> expected = geneDrugs.subList(3, geneDrugs.size());
		final String[] geneNames = geneDrugsIds();
		
		expect(dao.search(geneNames, 3, null)).andReturn(expected);
		replay(dao);
		
		final List<GeneDrug> result = controller.searchForGeneDrugs(
				geneNames, 3, null);
		
		assertThat(result, hasTheSameItemsAs(expected));
	}
	
	
	@Test
	public void testSearchWithMaxResults5() {
		final List<GeneDrug> geneDrugs = geneDrugs();
		final String[] geneNames = geneDrugsIds();
		final List<GeneDrug> expected = geneDrugs.subList(0, 5);
		
		expect(dao.search(geneNames, null, 5)).andReturn(expected);
		replay(dao);
		
		final List<GeneDrug> result = controller.searchForGeneDrugs(
			geneNames, null, 5);
		
		assertThat(result, hasTheSameItemsAs(expected));
	}
	
	@Test
	public void testSearchStartAt1WithMaxResults5() {
		final List<GeneDrug> geneDrugs = geneDrugs();
		final String[] geneNames = geneDrugsIds();
		
		final List<GeneDrug> expected = geneDrugs.subList(5, 10);
		
		expect(dao.search(geneNames, 5, 5)).andReturn(expected);
		replay(dao);
		
		final List<GeneDrug> result = controller.searchForGeneDrugs(
			geneNames, 5, 5);
		
		assertThat(result, hasTheSameItemsAs(expected));
	}
}
