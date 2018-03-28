/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.HasTheSameUserDataMatcher.hasTheSameUserDataAs;
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsAnUUIDMatcher.anUUID;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.absentRegistration;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.absentUser;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.plainPassword;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.presentRegistration;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.presentUser;
import static org.hamcrest.CoreMatchers.is;
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

import es.uvigo.ei.sing.pandrugs.persistence.entity.Registration;
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
@DatabaseSetup("file:src/test/resources/META-INF/dataset.registration.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.registration.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultRegistrationControllerIntegrationTest {
	@Inject
	@Named("defaultRegistrationController")
	private RegistrationController controller;
	
	@Test
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration-create.no-uuids.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRegisterAbsentUser() {
		final User user = absentUser();
		final String login = user.getLogin();
		final String email = user.getEmail();
		final String password = plainPassword(user);
		
		final Registration registration =
			controller.register(login, email, password);

		assertThat(registration, hasTheSameUserDataAs(user));
		assertThat(registration.getUuid(), is(anUUID()));
	}
	
	@Test
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration.no-uuids.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRegisterPresentRegistration() {
		final Registration presentRegistration = presentRegistration();
		final String login = presentRegistration.getLogin();
		final String email = presentRegistration.getEmail();
		final String password = plainPassword(presentRegistration);
		
		final Registration registration =
			controller.register(login, email, password);

		assertThat(registration, hasTheSameUserDataAs(presentRegistration));
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
	public void testRegisterPresentRegistrationLogin() {
		final Registration absentRegistration = absentRegistration();
		final String login = presentRegistration().getLogin();
		final String email = absentRegistration.getEmail();
		final String password = absentRegistration.getPassword();
		
		controller.register(login, email, password);
	}

	@Test
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration-change.no-uuids.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRegisterPresentRegistrationEmail() {
		final Registration absentRegistration = absentRegistration();
		final String login = absentRegistration.getLogin();
		final String email = presentRegistration().getEmail();
		final String password = plainPassword(absentRegistration);
		
		controller.register(login, email, password);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterPresentUserLogin() {
		final User user = presentUser();
		final Registration absentRegistration = absentRegistration();
		final String login = user.getLogin();
		final String email = absentRegistration.getEmail();
		final String password = absentRegistration.getPassword();
		
		controller.register(login, email, password);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterPresentUserEmail() {
		final User user = presentUser();
		final Registration absentRegistration = absentRegistration();
		final String login = absentRegistration.getLogin();
		final String email = user.getEmail();
		final String password = absentRegistration.getPassword();
		
		controller.register(login, email, password);
	}
	
	@Test
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration-confirm.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testConfirm() {
		controller.confirm(presentRegistration().getUuid());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConfirmAbsent() {
		controller.confirm(absentRegistration().getUuid());
	}
}
