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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasTheSameUserDataMatcher.hasTheSameUserDataAs;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.anyUser;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.users;
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.security.Principal;
import java.util.stream.Stream;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.SecurityContext;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.controller.UserController;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.service.entity.Message;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadatas;

@RunWith(EasyMockRunner.class)
public class DefaultUserServiceUnitTest {
	@TestSubject
	private DefaultUserService service = new DefaultUserService();

	@Mock
	private UserController controller;
	
	@Mock
	private SecurityContext security;
	
	@Mock
	private Principal principal;
	
	@Test
	public void testGetUserOwnData() {
		testGetOwnData(false);
	}
	
	@Test(expected = NotAuthorizedException.class)
	public void testUserGetOthersData() {
		final String login = "not-john";

		configureSecurityContext("john", false);
		
		replay(security, principal);
		
		service.get(new UserLogin(login), security);
	}
	
	@Test(expected = NotAuthorizedException.class)
	public void testUserUpdateOthersData() {
		final User user = anyUser();

		configureSecurityContext("not-" + user.getLogin(), false);
		
		replay(security, principal);
		
		service.update(new UserMetadata(user), security);
	}
	
	@Test(expected = InternalServerErrorException.class)
	public void testGetUserUnexpectedException() {
		final String login = "john";

		configureSecurityContext(login, false);
		
		expect(controller.get(login))
			.andThrow(new RuntimeException("Unexpected exception"));
		
		replay(controller, security, principal);
		
		service.get(new UserLogin(login), security);
	}
	
	@Test
	public void testGetAdminOwnData() {
		testGetOwnData(true);
	}
	
	@Test
	public void testGetAdminOthersData() {
		final User user = anyUser();
		
		configureSecurityContext("not-" + user.getLogin(), true);
		
		expect(controller.get(user.getLogin()))
			.andReturn(user);
		
		replay(controller, security, principal);
		
		final UserMetadata metadata = service.get(new UserLogin(user.getLogin()), security);
		
		assertThat(metadata, hasTheSameUserDataAs(user));
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetAdminAbsentData() {
		final String login = "not-john";

		configureSecurityContext("john", true);
		
		expect(controller.get(login))
			.andReturn(null);
		
		replay(controller, security, principal);
		
		service.get(new UserLogin(login), security);
	}
	
	@Test(expected = InternalServerErrorException.class)
	public void testGetAdminUnexpectedException() {
		final String login = "john";

		configureSecurityContext(login, true);
		
		expect(controller.get(login))
			.andThrow(new RuntimeException("Unexpected exception"));
		
		replay(controller, security, principal);
		
		service.get(new UserLogin(login), security);
	}

	@Test
	public void testUpdateUserOwnData() {
		testUpdateOwnData(false);
	}

	@Test
	public void testUpdateAdminOwnData() {
		testUpdateOwnData(true);
	}
	
	@Test
	public void testUpdateAdminOthersData() {
		final User user = anyUser();
		final String login = user.getLogin();
		
		final User updatedUser = anyUser();
		updatedUser.setEmail("new@email.com");
		updatedUser.setPassword("22af645d1859cb5ca6da0c484f1f37ea");
		
		configureSecurityContext("not-" + user.getLogin(), true);
		
		expect(controller.get(login))
			.andReturn(user);
		
		expect(controller.update(updatedUser))
			.andReturn(updatedUser);
		
		replay(controller, security, principal);
		
		final UserMetadata metadata = service.update(new UserMetadata(updatedUser), security);
		
		assertThat(metadata, hasTheSameUserDataAs(updatedUser));
	}
	
	@Test(expected = NotFoundException.class)
	public void testUpdateAdminAbsentData() {
		final User user = anyUser();
		
		configureSecurityContext("not-" + user.getLogin(), true);
		
		expect(controller.get(user.getLogin()))
			.andReturn(null);
		
		replay(controller, security, principal);
		
		service.update(new UserMetadata(user), security);
	}
	
	@Test(expected = InternalServerErrorException.class)
	public void testUpdateAdminUnexpectedException() {
		final User user = anyUser();
		
		configureSecurityContext("not-" + user.getLogin(), true);
		
		expect(controller.get(user.getLogin()))
			.andThrow(new RuntimeException("Unexpected exception"));
		
		replay(controller, security, principal);
		
		service.update(new UserMetadata(user), security);
	}

	@Test
	public void testDelete() {
		final String login = anyUser().getLogin();
		
		controller.remove(login);
		expectLastCall();
		
		replay(controller, security, principal);
		
		final Message message = service.delete(new UserLogin(login));
		
		assertThat(message, is(instanceOf(Message.class)));
	}

	@Test(expected = NotFoundException.class)
	public void testDeleteAbsent() {
		final String login = anyUser().getLogin();
		
		controller.remove(login);
		expectLastCall()
			.andThrow(new IllegalArgumentException("Not found"));
		
		replay(controller, security, principal);
		
		service.delete(new UserLogin(login));
	}

	@Test(expected = RuntimeException.class)
	public void testDeleteUnexpectedException() {
		final String login = anyUser().getLogin();
		
		controller.remove(login);
		expectLastCall()
			.andThrow(new RuntimeException("Unexpected exception"));
		
		replay(controller, security, principal);
		
		service.delete(new UserLogin(login));
	}

	@Test
	public void testList() {
		final User[] users = users();
		final UserMetadata[] metadatas  = Stream.of(users)
			.map(UserMetadata::new)
		.toArray(UserMetadata[]::new);
		
		expect(controller.list())
			.andReturn(asList(users));
		
		replay(controller, security, principal);
		
		final UserMetadatas userMetadatas = service.list();
		
		assertThat(userMetadatas.getUsers(), containsInAnyOrder(metadatas));
	}

	@Test(expected = RuntimeException.class)
	public void testListUnexpectedException() {
		expect(controller.list())
			.andThrow(new RuntimeException("Unexpected exception"));
		
		replay(controller, security, principal);
		
		service.list();
	}

	private void configureSecurityContext(String login, boolean isAdmin) {
		expect(security.isUserInRole(RoleType.ADMIN.name()))
			.andReturn(isAdmin)
			.anyTimes();
		expect(security.getUserPrincipal())
			.andReturn(principal)
			.anyTimes();
		expect(principal.getName())
			.andReturn(login)
			.anyTimes();
	}
	
	private void testGetOwnData(boolean isAdmin) {
		final User user = anyUser();
		final String login = user.getLogin();
		
		configureSecurityContext(login, isAdmin);
		
		expect(controller.get(login))
			.andReturn(user);
		
		replay(controller, security, principal);
		
		final UserMetadata metadata = service.get(new UserLogin(login), security);
		
		assertThat(metadata, hasTheSameUserDataAs(user));
	}
	
	private void testUpdateOwnData(boolean isAdmin) {
		final User user = anyUser();
		final String login = user.getLogin();
		
		final User updatedUser = anyUser();
		updatedUser.setEmail("new@email.com");
		updatedUser.setPassword("22af645d1859cb5ca6da0c484f1f37ea");
		
		configureSecurityContext(login, isAdmin);
		
		expect(controller.get(login))
			.andReturn(user);
		
		expect(controller.update(updatedUser))
			.andReturn(updatedUser);
		
		replay(controller, security, principal);
		
		final UserMetadata metadata = service.update(new UserMetadata(updatedUser), security);
		
		assertThat(metadata, hasTheSameUserDataAs(updatedUser));
	}
}
