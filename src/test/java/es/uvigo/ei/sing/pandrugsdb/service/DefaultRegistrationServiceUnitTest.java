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
package es.uvigo.ei.sing.pandrugsdb.service;

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasAHTTPStatusMatcher.hasHTTPStatus;
import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasTheSameUserDataMatcher.hasTheSameUserDataAs;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.RegistrationDataset.anyRegistration;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.RegistrationDataset.anyUser;
import static javax.ws.rs.core.Response.Status.OK;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.controller.RegistrationController;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.service.entity.Message;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UUID;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadata;

@RunWith(EasyMockRunner.class)
public class DefaultRegistrationServiceUnitTest {
	@TestSubject
	private DefaultRegistrationService service = new DefaultRegistrationService();
	
	@Mock
	private RegistrationController controller;

	@Test
	public void testRegisterAbsentRegistration() {
		final Registration registration = anyRegistration();
		final String login = registration.getLogin();
		final String email = registration.getEmail();
		final String password = registration.getPassword();
		
		expect(controller.register(login, email, password))
			.andReturn(registration);
		
		replay(controller);
		
		final Response response = service.register(registration);
		
		assertThat(response, hasHTTPStatus(OK));
		assertThat(response.getEntity(), is(instanceOf(Message.class)));
	}
	
	@Test(expected = BadRequestException.class)
	public void testRegisterPresentRegistrationLogin() {
		final Registration registration = anyRegistration();
		final String login = registration.getLogin();
		final String email = registration.getEmail();
		final String password = registration.getPassword();
		
		expect(controller.register(login, email, password))
			.andThrow(new IllegalArgumentException("Already present"));
		
		replay(controller);
		
		service.register(registration);
	}

	@Test(expected = BadRequestException.class)
	public void testRegisterPresentUser() {
		final Registration registration = anyRegistration();
		final String login = registration.getLogin();
		final String email = registration.getEmail();
		final String password = registration.getPassword();
		
		expect(controller.register(login, email, password))
			.andThrow(new IllegalArgumentException("Error"));
		
		replay(controller);
		
		service.register(registration);
	}
	
	@Test(expected = InternalServerErrorException.class)
	public void testRegisterUnexpectedException() {
		expect(controller.register(anyString(), anyString(), anyString()))
			.andThrow(new RuntimeException("Unexpected exception"));
		
		replay(controller);
		
		service.register(anyRegistration());
	}

	@Test
	public void testConfirm() throws Exception {
		final String uuid = anyRegistration().getUuid();
		final User user = anyUser();
		
		expect(controller.confirm(uuid))
			.andReturn(user);

		replay(controller);
		
		final Response response = service.confirm(new UUID(uuid));
		
		assertThat(response, hasHTTPStatus(OK));
		assertThat(response.getEntity(), is(instanceOf(UserMetadata.class)));
		assertThat(response.getEntity(), hasTheSameUserDataAs(user));
	}

	@Test(expected = NotFoundException.class)
	public void testConfirmAbsent() {
		final UUID uuid = new UUID(anyRegistration().getUuid());
		
		expect(controller.confirm(uuid.getUuid()))
			.andThrow(new IllegalArgumentException("Error"));
		
		replay(controller);
		
		service.confirm(uuid);
	}
	
	@Test(expected = InternalServerErrorException.class)
	public void testConfirmUnexpectedException() {
		expect(controller.confirm(anyString()))
			.andThrow(new RuntimeException("Unexpected exception"));
		
		replay(controller);
		
		service.confirm(new UUID(anyRegistration().getUuid()));
	}
}
