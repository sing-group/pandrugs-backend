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

package es.uvigo.ei.sing.pandrugs.service;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.HasHttpStatus.hasNoContentStatus;
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.HasHttpStatus.hasUnauthorizedStatus;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import es.uvigo.ei.sing.pandrugs.service.entity.Login;

@DatabaseSetup("file:src/test/resources/META-INF/dataset.user.xml")
public class DefaultSessionServiceJerseyIntegrationTest extends ConfiguredJerseyTest {
	@Override
	protected Class<?>[] getServiceClasses() {
		return new Class<?>[] { DefaultSessionService.class };
	}
	
	@Test
	public void testLogin() throws Exception {
		final Response response = target("session")
			.request(APPLICATION_JSON_TYPE)
		.post(entity(new Login("pepe", "pepe"), APPLICATION_JSON_TYPE));
		
		assertThat(response, hasNoContentStatus());
	}
	
	@Test
	public void testLoginInvalidPassword() throws Exception {
		final Response response = target("session")
			.request(APPLICATION_JSON_TYPE)
		.post(entity(new Login("pepe", "badpassword"), APPLICATION_JSON_TYPE));
		
		assertThat(response, hasUnauthorizedStatus());
	}
	
	@Test
	public void testLoginInvalidLogin() throws Exception {
		final Response response = target("session")
			.request(APPLICATION_JSON_TYPE)
		.post(entity(new Login("john", "john"), APPLICATION_JSON_TYPE));
		
		assertThat(response, hasUnauthorizedStatus());
	}
}
