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

package es.uvigo.ei.sing.pandrugs.service.security;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.SecurityContext;

import es.uvigo.ei.sing.pandrugs.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;

public class SecurityContextStub implements SecurityContext {
	private final Map<String, RoleType> userToRole;
	
	private Principal principal;
	
	public SecurityContextStub(User[] users) {
		this(asList(users));
	}

	public SecurityContextStub(User[] users, String user) {
		this(asList(users), user);
	}
	
	public SecurityContextStub(List<User> users) {
		this.userToRole = users.stream().collect(toMap(User::getLogin, User::getRole));
	}

	public SecurityContextStub(List<User> users, String user) {
		this(users);
		
		this.setCurrentUser(user);
	}
	
	public void setCurrentUser(final String user) {
		this.principal = () -> user;
	}
	
	public String getCurrentUser() {
		return this.principal.getName();
	}
	
	@Override
	public Principal getUserPrincipal() {
		return this.principal;
	}

	@Override
	public boolean isUserInRole(String role) {
		final String user = this.getCurrentUser();
		
		if (this.userToRole.containsKey(user)) {
			return this.userToRole.get(user).name().equals(role);
		} else {
			return false;
		}
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		return SecurityContext.BASIC_AUTH;
	}
}
