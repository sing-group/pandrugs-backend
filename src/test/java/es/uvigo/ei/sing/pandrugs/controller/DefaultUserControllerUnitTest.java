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

import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.anyUser;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.UserDataset.users;
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugs.persistence.dao.UserDAO;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;

@RunWith(EasyMockRunner.class)
public class DefaultUserControllerUnitTest {
	@TestSubject
	private DefaultUserController controller = new DefaultUserController();
	
	@Mock
	private UserDAO userDAO;

	@Test
	public void testGet() {
		final User expected = anyUser();
		final String login = expected.getLogin();
		
		expect(userDAO.get(login))
			.andReturn(expected);
		
		replay(userDAO);
		
		final User result = controller.get(login);
		assertThat(result, is(equalTo(expected)));
	}
	
	@Test
	public void testGetAbsent() {
		expect(userDAO.get(anyString()))
			.andReturn(null);
		
		replay(userDAO);
		
		final User result = controller.get(anyUser().getLogin());
		
		assertThat(result, is(nullValue()));
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetNull() {
		expect(userDAO.get(null))
			.andThrow(new NullPointerException());
		
		replay(userDAO);
		
		controller.get(null);
	}
	
	@Test
	public void testList() {
		expect(userDAO.list()).andReturn(asList(users()));
		
		replay(userDAO);
		
		assertThat(controller.list(), containsInAnyOrder(users()));
	}
	
	@Test
	public void testUpdateEmail() {
		final User user = anyUser();
		user.setEmail("francisco@email.com");
		
		expect(userDAO.update(user))
			.andReturn(user);
		
		replay(userDAO);
		
		controller.update(user);
	}
	
	@Test
	public void testUpdatePassword() {
		final User user = anyUser();
		user.setPassword("117735823fadae51db091c7d63e60eb0");
		
		expect(userDAO.update(user))
			.andReturn(user);
		
		replay(userDAO);
		
		controller.update(user);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateAbsentUser() {
		expect(userDAO.update(anyObject()))
			.andThrow(new IllegalArgumentException());
		
		replay(userDAO);
		
		controller.update(anyUser());
	}
	
	@Test
	public void testRemove() {
		userDAO.removeByLogin(anyString());
		expectLastCall();
		
		replay(userDAO);
		
		controller.remove(anyUser().getLogin());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAbsent() {
		userDAO.removeByLogin(anyString());
		expectLastCall()
			.andThrow(new IllegalArgumentException());
		
		replay(userDAO);
		
		controller.remove(anyUser().getLogin());
	}
	
	@Test(expected = NullPointerException.class)
	public void testRemoveNull() {
		userDAO.removeByLogin(null);
		expectLastCall()
			.andThrow(new NullPointerException());
		
		replay(userDAO);
		
		controller.remove(null);
	}
	
	@Test
	public void testCheckLoginPresent() {
		final User user = anyUser();
		
		expect(userDAO.get(user.getLogin()))
			.andReturn(user);
		
		replay(userDAO);
		
		final boolean check = controller.checkLogin(user.getLogin(), "fake");
		
		assertThat(check, is(true));
	}
	
	@Test
	public void testCheckLoginAbsent() {
		final User user = anyUser();
		
		expect(userDAO.get(user.getLogin()))
			.andReturn(null);
		
		replay(userDAO);
		
		final boolean check = controller.checkLogin(user.getLogin(), "fake");
		
		assertThat(check, is(false));
	}
	
	@Test
	public void testCheckLoginInvalidPassword() {
		final User user = anyUser();
		
		expect(userDAO.get(user.getLogin()))
			.andReturn(user);
		
		replay(userDAO);
		
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
