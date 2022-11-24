/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2022 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.absentRegistration;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.presentRegistration;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.registrations;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
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

import es.uvigo.ei.sing.pandrugs.persistence.entity.Registration;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.registration.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.registration.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultRegistrationDAOIntegrationTest {
	@Inject
	@Named("defaultRegistrationDAO")
	private RegistrationDAO dao;
	
	@Test
	public void testGet() {
		final Registration expected = presentRegistration();
		final Registration result = dao.get(expected.getUuid());
		
		assertThat(result, is(equalTo(expected)));
	}
	
	@Test
	public void testGetAbsent() {
		final Registration result = dao.get(absentRegistration().getUuid());
		
		assertThat(result, is(nullValue()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetNull() {
		dao.get(null);
	}

	@Test
	public void testGetByEmail() {
		final Registration expected = presentRegistration();
		final Registration result = dao.getByEmail(expected.getEmail());
		
		assertThat(result, is(equalTo(expected)));
	}
	
	@Test
	public void testGetByEmailAbsent() {
		final Registration result = dao.getByEmail(absentRegistration().getEmail());
		
		assertThat(result, is(nullValue()));
	}

	@Test(expected = NullPointerException.class)
	public void testGetByEmailNull() {
		dao.getByEmail(null);
	}
	
	@Test
	public void testList() {
		assertThat(dao.list(), containsInAnyOrder(registrations()));
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.registration.remove.xml",
		table = "registration",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRemove() {
		dao.remove(presentRegistration());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAbsent() {
		dao.remove(absentRegistration());
	}
	
	@Test(expected = NullPointerException.class)
	public void testRemoveNull() {
		dao.remove(null);
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.registration.remove.xml",
		table = "registration",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRemoveByUuid() {
		dao.removeByUuid(presentRegistration().getUuid());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveByUuidAbsent() {
		dao.removeByUuid(absentRegistration().getUuid());
	}
	
	@Test(expected = NullPointerException.class)
	public void testRemoveByEmailNull() {
		dao.removeByUuid(null);
	}
}
