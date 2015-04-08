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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasTheSameUserDataMatcher.hasTheSameUserDataAs;
import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.IsAnUUIDMatcher.anUUID;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.RegistrationDataset.absentRegistration;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.RegistrationDataset.absentUser;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.RegistrationDataset.anyRegistration;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.RegistrationDataset.presentRegistration;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.RegistrationDataset.presentUser;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.registration.xml")
public class DefaultRegistrationControllerIntegrationTest {
	@Inject
	@Named("defaultRegistrationController")
	private RegistrationController controller;
	
	@Test
	public void testRegister() {
		final User user = absentUser();
		final String login = user.getLogin();
		final String email = user.getEmail();
		final String password = user.getPassword();
		
		final Registration registration =
			controller.register(login, email, password);

		assertThat(registration, hasTheSameUserDataAs(user));
		assertThat(registration.getUuid(), is(anUUID()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegisterPresentLoginRegistration() {
		final Registration registration = presentRegistration();
		final String login = registration.getLogin();
		final String email = anyRegistration().getEmail();
		final String password = registration.getPassword();
		
		controller.register(login, email, password);
	}
	
	@Test
	public void testRegisterPresentEmailRegistration() {
		final Registration user = presentRegistration();
		final String login = user.getLogin();
		final String email = user.getEmail();
		final String password = user.getPassword();
		
		final Registration registration =
			controller.register(login, email, password);

		assertThat(registration, hasTheSameUserDataAs(user));
		assertThat(registration.getUuid(), is(anUUID()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterPresentUser() {
		final User user = presentUser();
		final String login = user.getLogin();
		final String email = user.getEmail();
		final String password = user.getPassword();
		
		controller.register(login, email, password);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterPresentUserLogin() {
		final User user = presentUser();
		final User absentUser = absentUser();
		final String login = user.getLogin();
		final String email = absentUser.getEmail();
		final String password = absentUser.getPassword();
		
		controller.register(login, email, password);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterPresentUserEmail() {
		final User user = presentUser();
		final User absentUser = absentUser();
		final String login = absentUser.getLogin();
		final String email = user.getEmail();
		final String password = absentUser.getPassword();
		
		controller.register(login, email, password);
	}
	
	@Test
	@ExpectedDatabase(
		value = "dataset.registration.register.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
		override = false
	)
	@ExpectedDatabase(
		value = "dataset.registration.register.xml",
		table = "registration",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
		override = false
	)
	public void testConfirm() {
		controller.confirm(presentRegistration().getUuid());
	}
	
	@Test(expected = IllegalArgumentException.class)
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration.xml",
		table = "user",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
		override = false
	)
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration.xml",
		table = "registration",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
		override = false
	)
	public void testConfirmAbsent() {
		controller.confirm(absentRegistration().getUuid());
	}
}
