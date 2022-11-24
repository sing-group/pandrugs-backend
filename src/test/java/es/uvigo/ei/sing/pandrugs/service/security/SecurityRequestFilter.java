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

package es.uvigo.ei.sing.pandrugs.service.security;

import java.io.IOException;
import java.util.Base64;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugs.controller.UserController;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class SecurityRequestFilter implements ContainerRequestFilter {
	@Inject
	private UserController controller;
	
	@Override
	@Transactional
	public void filter(ContainerRequestContext requestContext)
	throws IOException {
		final String basic = requestContext.getHeaders().getFirst("Authorization");
		
		final String user = checkCredentials(basic);
		
		requestContext.setSecurityContext(new SecurityContextStub(controller.list(), user));
	}

	private String checkCredentials(String basic) {
		if (basic != null && basic.startsWith("Basic ")) {
			final String base64 = basic.substring("Basic ".length());
			final String loginAndPassword = new String(Base64.getDecoder().decode(base64));
			final String[] tokens = loginAndPassword.split(":");
			
			if (tokens.length == 2) {
				if (controller.checkLogin(tokens[0], tokens[1])) {
					return tokens[0];
				}
			}
		}
		
		return null;
	}
}
