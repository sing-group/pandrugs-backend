/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2023 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

package es.uvigo.ei.sing.pandrugs.controller;

import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.absentUser;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.absentUserPassword;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.presentUser;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.presentUserPassword;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.users;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugs.persistence.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class
})
@DirtiesContext
@DatabaseSetup("file:src/test/resources/META-INF/dataset.user.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.user.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultUserControllerIntegrationTest {
	@Inject
	@Named("defaultUserController")
	private UserController controller;

	@Test
	public void testGet() {
		final User expected = presentUser();
		final User result = controller.get(expected.getLogin());
		
		assertThat(result, is(equalTo(expected)));
	}
	
	@Test
	public void testGetAbsent() {
		final User result = controller.get(absentUser().getLogin());
		
		assertThat(result, is(nullValue()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetNull() {
		controller.get(null);
	}
	
	@Test
	public void testList() {
		assertThat(controller.list(), containsInAnyOrder(users()));
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.user.update.email.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testUpdateEmail() {
		final User user = presentUser();
		user.setEmail("francisco@email.com");
		
		controller.update(user);
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.user.update.password.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testUpdatePassword() {
		final User user = presentUser();
		user.setPassword("117735823fadae51db091c7d63e60eb0");
		
		controller.update(user);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateAbsentUser() {
		controller.update(absentUser());
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.user.remove.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRemove() {
		controller.remove(presentUser().getLogin());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAbsent() {
		controller.remove(absentUser().getLogin());
	}
	
	@Test(expected = NullPointerException.class)
	public void testRemoveNull() {
		controller.remove(null);
	}
	
	@Test
	public void testCheckLoginPresent() {
		final User user = presentUser();
		
		final boolean check = controller.checkLogin(user.getLogin(), presentUserPassword());
		
		assertThat(check, is(true));
	}
	
	@Test
	public void testCheckLoginAbsent() {
		final User user = absentUser();
		
		final boolean check = controller.checkLogin(user.getLogin(), absentUserPassword());
		
		assertThat(check, is(false));
	}
	
	@Test
	public void testCheckLoginInvalidPassword() {
		final User user = presentUser();
		
		final boolean check = controller.checkLogin(user.getLogin(), "wrong");
		
		assertThat(check, is(false));
	}
	
	@Test(expected = NullPointerException.class)
	public void testCheckLoginNullUser() {
		controller.checkLogin(null, "fake");
	}
	
	@Test(expected = NullPointerException.class)
	public void testCheckLoginNullPassword() {
		controller.checkLogin("fake", null);
	}
}
