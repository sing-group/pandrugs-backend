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

package es.uvigo.ei.sing.pandrugs.service;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.HasHttpStatus.hasOkStatus;
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.HasTheSameUserDataMatcher.hasTheSameUserDataAs;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.absentRegistration;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.plainPassword;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.presentRegistration;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.presentUser;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.userFor;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;
import es.uvigo.ei.sing.pandrugs.service.entity.Message;
import es.uvigo.ei.sing.pandrugs.service.entity.UUID;
import es.uvigo.ei.sing.pandrugs.service.entity.UserInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DirtiesContext
@DatabaseSetup("file:src/test/resources/META-INF/dataset.registration.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.registration.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
//TODO: Check email sending
public class DefaultRegistrationServiceIntegrationTest {
	@Inject
	@Named("defaultRegistrationService")
	private RegistrationService service;
	
	@Test
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration-create.no-uuids.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRegisterAbsentUser() {
		final Registration registration = absentRegistration();
		registration.setPassword(plainPassword(registration));

		final Response response = service.register(registration, null);
		
		assertThat(response, hasOkStatus());
		assertThat(response.getEntity(), is(instanceOf(Message.class)));
	}
	
	@Test
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration.no-uuids.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRegisterPresentRegistration() {
		final Registration registration = presentRegistration();
		registration.setPassword(plainPassword(registration));

		assertNotNull(service.register(registration, null));
	}

	@Test(expected = BadRequestException.class)
	public void testRegisterPresentUser() {
		final User user = presentUser();
		final Registration registration = new Registration(
			user.getLogin(), user.getEmail(), user.getPassword());
		
		service.register(registration, null);
	}
	
	@Test(expected = BadRequestException.class)
	public void testRegisterPresentRegistrationLogin() {
		final Registration registration = absentRegistration();
		registration.setLogin(presentRegistration().getLogin());
		registration.setPassword(plainPassword(registration));

		service.register(registration, null);
	}
	
	@Test
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration-change.no-uuids.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testRegisterPresentRegistrationEmail() {
		final Registration registration = absentRegistration();
		registration.setPassword(plainPassword(registration));

		registration.setEmail(presentRegistration().getEmail());
		
		assertNotNull(service.register(registration, null));
	}
	
	@Test(expected = BadRequestException.class)
	public void testRegisterPresentUserLogin() {
		final User user = presentUser();
		final Registration registration = absentRegistration();
		registration.setLogin(user.getLogin());
		registration.setPassword(plainPassword(registration));
		
		assertNotNull(service.register(registration, null));
	}
	
	@Test(expected = BadRequestException.class)
	public void testRegisterPresentUserEmail() {
		final User user = presentUser();
		final Registration registration = absentRegistration();
		registration.setEmail(user.getEmail());
		registration.setPassword(plainPassword(registration));

		assertNotNull(service.register(registration, null));
	}

	@Test
	@ExpectedDatabase(
		value = "file:src/test/resources/META-INF/dataset.registration-confirm.xml",
		assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
	)
	public void testConfirm() throws Exception {
		final Registration registration = presentRegistration();
		final String uuid = registration.getUuid();
		final Response response = service.confirm(new UUID(uuid));
		
		assertThat(response, hasOkStatus());
		assertThat((UserInfo) response.getEntity(), hasTheSameUserDataAs(userFor(registration)));
	}

	@Test(expected = NotFoundException.class)
	public void testConfirmAbsent() {
		service.confirm(new UUID(absentRegistration().getUuid()));
	}
}
