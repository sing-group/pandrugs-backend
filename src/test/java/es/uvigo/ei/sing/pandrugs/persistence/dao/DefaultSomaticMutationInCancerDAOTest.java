/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
 * and Miguel Reboiro-Jato
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

package es.uvigo.ei.sing.pandrugs.persistence.dao;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToSomaticMutationInCancer.containsSomaticMutationInCancers;
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToSomaticMutationInCancer.equalSomaticMutationInCancer;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancerDataset.invalidGeneAndMutationAA;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancerDataset.invalidIds;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancerDataset.validGeneAndMutationAA;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancerDataset.validIds;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancerDataset.withGeneAndMutationAA;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancerDataset.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

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

import es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancerId;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.smic.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.smic.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultSomaticMutationInCancerDAOTest {
	@Inject
	@Named("defaultSomaticMutationInCancerDAO")
	private SomaticMutationInCancerDAO dao;

	@Test
	public void testGetById() {
		for (SomaticMutationInCancerId id : validIds()) {
			assertThat(dao.get(id), is(equalSomaticMutationInCancer(withId(id))));
		}
	}

	@Test
	public void testGetByIdNonExistent() {
		for (SomaticMutationInCancerId id : invalidIds()) {
			assertThat(dao.get(id), is(nullValue()));
		}
	}

	@Test
	public void testGetByIdData() {
		for (SomaticMutationInCancerId id : validIds()) {
			assertThat(
				dao.get(id.getSampleId(), id.getGeneSymbol(), id.getMutationId(), id.getStatus()),
				is(equalSomaticMutationInCancer(withId(id)))
			);
		}
	}

	@Test
	public void testGetByIdNonExistentData() {
		for (SomaticMutationInCancerId id : invalidIds()) {
			assertThat(
				dao.get(id.getSampleId(), id.getGeneSymbol(), id.getMutationId(), id.getStatus()),
				is(nullValue())
			);
		}
	}

	@Test
	public void testListByGeneAndMutationAA() {
		for (String[] geneAndMutationAA : validGeneAndMutationAA()) {
			final String gene = geneAndMutationAA[0];
			final String mutation = geneAndMutationAA[1];
			
			assertThat(dao.listByGeneAndMutationAA(gene, mutation), containsSomaticMutationInCancers(withGeneAndMutationAA(gene, mutation)));
		}
	}

	@Test
	public void testListByGeneAndMutationAANonExistent() {
		for (String[] geneAndMutationAA : invalidGeneAndMutationAA()) {
			final String gene = geneAndMutationAA[0];
			final String mutation = geneAndMutationAA[1];
			
			assertThat(dao.listByGeneAndMutationAA(gene, mutation), is(empty()));
		}
	}
}
