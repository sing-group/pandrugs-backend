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
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.absentUser;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.absentUserPassword;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.presentUser;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.presentUserPassword;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import es.uvigo.ei.sing.pandrugsdb.service.entity.Login;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@Transactional
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	DbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.user.xml")
public class DefaultSessionServiceIntegrationTest {
	@Inject
	@Named("defaultSessionService")
	private SessionService service;
	
	@Test
	public void testLogin() throws Exception {
		final String login = presentUser().getLogin();
		final String password = presentUserPassword();
		
		final Response response = service.login(new Login(login, password));
		
		assertThat(response, hasHTTPStatus(OK));
	}
	
	@Test(expected = NotAuthorizedException.class)
	public void testLoginInvalidPassword() throws Exception {
		final String login = presentUser().getLogin();
		final String password = "invalid" + presentUserPassword();
		
		service.login(new Login(login, password));
	}

	@Test(expected = NotAuthorizedException.class)
	public void testLoginInvalidLogin() throws Exception {
		final String login = absentUser().getLogin();
		final String password = absentUserPassword();
		
		service.login(new Login(login, password));
	}
}
