/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.persistence.dao;

import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.absentUser;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.presentAdmin;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.presentUser;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugs.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.user.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.user.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultUserDAOIntegrationTest {
	@Inject
	@Named("defaultUserDAO")
	private UserDAO dao;

	@Test
	public void testGet() {
		final User expected = presentUser();
		final User result = dao.get(expected.getLogin());
		
		assertThat(result, is(equalTo(expected)));
	}
	
	@Test
	public void testGetAbsent() {
		final User result = dao.get(absentUser().getLogin());
		
		assertThat(result, is(nullValue()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetNull() {
		dao.get(null);
	}

	@Test
	public void testGetByEmail() {
		final User expected = presentUser();
		final User result = dao.getByEmail(expected.getEmail());
		
		assertThat(result, is(equalTo(expected)));
	}
	
	@Test
	public void testGetByEmailAbsent() {
		final User result = dao.getByEmail(absentUser().getEmail());
		
		assertThat(result, is(nullValue()));
	}

	@Test(expected = NullPointerException.class)
	public void testGetByEmailNull() {
		dao.getByEmail(null);
	}
	
	@Test
	public void testList() {
		assertThat(dao.list(), containsInAnyOrder(users()));
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.user.register.admin.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRegisterAdminUser() {
		dao.registerAdminUser("admin2", "admin2@email.com", "c84258e9c39059a89ab77d846ddab909");
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.user.register.user.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRegisterNormalUser() {
		dao.registerNormalUser("iria", "iria@email.com", "b24878685a28ca723ac807ac94664fcf");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterAlreadyExistingAdminUser() {
		dao.registerAdminUser("admin1", "admin1@email.com", "e00cf25ad42683b3df678c61f42c6bda");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterAlreadyExistingNormalUser() {
		dao.registerNormalUser("pepe", "pepe@email.com", "926e27eecdbc7a18858b3798ba99bddd");
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
		
		dao.update(user);
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
		
		dao.update(user);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateRole() {
		final User user = presentUser();
		final User userChangedRole = new User(
			user.getLogin(), user.getEmail(),
			user.getPassword(), RoleType.ADMIN
		);
		
		dao.update(userChangedRole);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateAbsentUser() {
		dao.update(absentUser());
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.user.changerole.user.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testChangeRoleUserToAdmin() {
		dao.changeRole(presentUser(), RoleType.ADMIN);
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.user.changerole.admin.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testChangeRoleAdminToUser() {
		dao.changeRole(presentAdmin(), RoleType.USER);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testChangeRoleSameRole() {
		dao.changeRole(presentUser(), RoleType.USER);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testChangeRoleAbsentUser() {
		dao.changeRole(absentUser(), RoleType.ADMIN);
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.user.remove.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRemove() {
		dao.removeByLogin(presentUser().getLogin());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAbsent() {
		dao.removeByLogin(absentUser().getLogin());
	}
	
	@Test(expected = NullPointerException.class)
	public void testRemoveNull() {
		dao.removeByLogin(null);
	}
}
