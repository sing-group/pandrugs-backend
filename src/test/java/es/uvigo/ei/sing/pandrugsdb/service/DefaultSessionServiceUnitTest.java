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
import static javax.ws.rs.core.Response.Status.OK;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertThat;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.controller.UserController;
import es.uvigo.ei.sing.pandrugsdb.service.entity.Login;

@RunWith(EasyMockRunner.class)
public class DefaultSessionServiceUnitTest {
	@TestSubject
	private DefaultSessionService service = new DefaultSessionService();

	@Mock
	private UserController controller;
	
	@Test
	public void testLogin() {
		final String username = "pepe";
		final String password = "pepe-pass";
		
		expect(controller.checkLogin(username, password))
			.andReturn(true);
		
		replay(controller);
		
		final Response response = service.login(new Login(username, password));
		
		assertThat(response, hasHTTPStatus(OK));
	}
	
	@Test(expected = NotAuthorizedException.class)
	public void testLoginInvalid() {
		final String username = "pepe";
		final String password = "pepe-pass";
		
		expect(controller.checkLogin(username, password))
			.andReturn(false);
		
		replay(controller);
		
		service.login(new Login(username, password));
	}
	
	@Test(expected = RuntimeException.class)
	public void testLoginUnexpectedException() {
		final String username = "pepe";
		final String password = "pepe-pass";
		
		expect(controller.checkLogin(username, password))
			.andThrow(new RuntimeException("Unexpected exception"));
		
		replay(controller);
		
		service.login(new Login(username, password));
	}
}
