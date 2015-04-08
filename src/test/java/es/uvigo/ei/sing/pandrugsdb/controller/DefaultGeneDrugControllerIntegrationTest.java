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
public class DefaultGeneDrugControllerIntegrationTest {
	@Inject
	@Named("defaultGeneDrugController")
	private GeneDrugController controller;
	
	@Test
	public void testSearchSingleWithNullStartPositionAndMaxResutls() {
		final GeneDrug expected = presentGeneDrug();
		final List<GeneDrug> result = controller.searchForGeneDrugs(
			new String[] { expected.getGeneSymbol() }, null, null
		);
		
		assertThat(result, hasExactlyTheItems(expected));
	}
	
	@Test
	public void testSearchMultipleWithNullStartPositionAndMaxResutls() {
		final List<GeneDrug> expected = geneDrugs().subList(2, 6);
		final List<GeneDrug> result = controller.searchForGeneDrugs(
			expected.stream().map(GeneDrug::getGeneSymbol).toArray(String[]::new), null, null
		);
		
		assertThat(result, hasTheSameItemsAs(expected));
	}
	
	@Test
	public void testSearchStartAt3() {
		final List<GeneDrug> geneDrugs = geneDrugs();
		
		final List<GeneDrug> expected = geneDrugs.subList(3, geneDrugs.size());
		final List<GeneDrug> result = controller.searchForGeneDrugs(geneDrugsIds(), 3, null);
		
		assertThat(result, hasTheSameItemsAs(expected));
	}
	
	@Test
	public void testSearchWithMaxResults5() {
		final List<GeneDrug> geneDrugs = geneDrugs();
		
		final List<GeneDrug> expected = geneDrugs.subList(0, 5);
		final List<GeneDrug> result = controller.searchForGeneDrugs(geneDrugsIds(), null, 5);
		
		assertThat(result, hasTheSameItemsAs(expected));
	}
	
	@Test
	public void testSearchStartAt1WithMaxResults5() {
		final List<GeneDrug> geneDrugs = geneDrugs();
		
		final List<GeneDrug> expected = geneDrugs.subList(5, 10);
		final List<GeneDrug> result = controller.searchForGeneDrugs(geneDrugsIds(), 5, 5);
		
		assertThat(result, hasTheSameItemsAs(expected));
	}
}
