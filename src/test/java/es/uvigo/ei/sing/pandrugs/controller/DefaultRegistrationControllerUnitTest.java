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

package es.uvigo.ei.sing.pandrugs.controller;

import static es.uvigo.ei.sing.pandrugs.matcher.easymock.UuidEasyMockMatcher.anyUUID;
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.HasTheSameUserDataMatcher.hasTheSameUserDataAs;
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsAnUUIDMatcher.anUUID;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.anyRegistration;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.anyUser;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.RegistrationDataset.plainPassword;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugs.mail.Mailer;
import es.uvigo.ei.sing.pandrugs.persistence.dao.RegistrationDAO;
import es.uvigo.ei.sing.pandrugs.persistence.dao.UserDAO;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugs.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;
import es.uvigo.ei.sing.pandrugs.util.DigestUtils;

@RunWith(EasyMockRunner.class)
public class DefaultRegistrationControllerUnitTest {
	@TestSubject
	private DefaultRegistrationController controller =
		new DefaultRegistrationController();
	
	@Mock
	private RegistrationDAO registrationDAO;
	
	@Mock
	private UserDAO userDAO;
	
	@Mock
	private Mailer mailer;
	
	@Test
	public void testRegister() {
		final User user = anyUser();
		final String login = user.getLogin();
		final String email = user.getEmail();
		final String password = plainPassword(user);
		
		expect(userDAO.get(login)).andReturn(null);
		expect(userDAO.getByEmail(email)).andReturn(null);
		
		expect(registrationDAO.persist(login, email, DigestUtils.md5Digest(password)))
			.andReturn(new Registration(login, email, DigestUtils.md5Digest(password)));
		expect(registrationDAO.get(login)).andReturn(null);
		expect(registrationDAO.getByEmail(email)).andReturn(null);
		
		mailer.sendConfirmSingUp(eq(email), eq(login), anyUUID());
		expectLastCall();
		
		replay(registrationDAO, userDAO, mailer);
		
		final Registration registration =
			controller.register(login, email, password);

		assertThat(registration, hasTheSameUserDataAs(user));
		assertThat(registration.getUuid(), is(anUUID()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegisterPresentLoginRegistration() {
		final User user = anyUser();
		final String login = user.getLogin();
		final String email = user.getEmail();
		final String password = user.getPassword();
		
		expect(userDAO.get(login)).andReturn(null);
		expect(userDAO.getByEmail(email)).andReturn(null);
		expect(registrationDAO.get(login)).andReturn(anyRegistration());
		
		replay(registrationDAO, userDAO);
		
		controller.register(login, email, password);
	}
	
	@Test
	public void testRegisterPresentEmailRegistration() {
		final User user = anyUser();
		final String login = user.getLogin();
		final String email = user.getEmail();
		final String password = plainPassword(user);
		
		expect(userDAO.get(login)).andReturn(null);
		expect(userDAO.getByEmail(email)).andReturn(null);
		expect(registrationDAO.get(login)).andReturn(null);
		
		final Registration newRegistration = new Registration(login, email, DigestUtils.md5Digest(password));
		expect(registrationDAO.persist(login, email, DigestUtils.md5Digest(password)))
			.andReturn(newRegistration);
		
		final Registration previousRegistration = new Registration(login, email, password);
		expect(registrationDAO.getByEmail(email)).andReturn(previousRegistration);
		registrationDAO.remove(previousRegistration);
		expectLastCall();
		
		mailer.sendConfirmSingUp(eq(email), eq(login), anyUUID());
		expectLastCall();
		
		replay(registrationDAO, userDAO, mailer);
		
		final Registration registration =
			controller.register(login, email, password);

		assertThat(registration, hasTheSameUserDataAs(user));
		assertThat(registration.getUuid(), is(anUUID()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterPresentUserLogin() {
		final User user = anyUser();
		final String login = user.getLogin();
		final String email = user.getEmail();
		final String password = user.getPassword();
		
		expect(userDAO.get(login)).andReturn(user);
		
		replay(userDAO);
		
		controller.register(login, email, password);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterPresentUserEmail() {
		final User user = anyUser();
		final String login = user.getLogin();
		final String email = user.getEmail();
		final String password = user.getPassword();
		
		expect(userDAO.get(login)).andReturn(null);
		expect(userDAO.getByEmail(email)).andReturn(user);
		
		replay(userDAO);
		
		controller.register(login, email, password);
	}
	
	@Test
	public void testConfirm() {
		final Registration registration = anyRegistration();
		final String login = registration.getLogin();
		final String email = registration.getEmail();
		final String password = registration.getPassword();
		final User expectedUser = new User(login, email, password, RoleType.USER);
		
		expect(registrationDAO.get(registration.getUuid()))
			.andReturn(registration);
		expect(userDAO.registerNormalUser(login, email, password))
			.andReturn(expectedUser);
		registrationDAO.remove(registration);
		expectLastCall();
		
		replay(registrationDAO, userDAO);
		
		final User user = controller.confirm(registration.getUuid());
		
		assertThat(user, is(equalTo(expectedUser)));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConfirmAbsent() {
		expect(registrationDAO.get(anyUUID()))
			.andReturn(null);
		
		replay(registrationDAO);
		
		controller.confirm(anyRegistration().getUuid());
	}
}
