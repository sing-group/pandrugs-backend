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

import static es.uvigo.ei.sing.pandrugs.service.ServiceUtils.createNotFoundException;
import static es.uvigo.ei.sing.pandrugs.service.ServiceUtils.createUnauthorizedException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qmino.miredot.annotations.ReturnType;

import es.uvigo.ei.sing.pandrugs.controller.UserController;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;
import es.uvigo.ei.sing.pandrugs.service.entity.Message;
import es.uvigo.ei.sing.pandrugs.service.entity.UserInfo;
import es.uvigo.ei.sing.pandrugs.service.entity.UserInfos;
import es.uvigo.ei.sing.pandrugs.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugs.service.security.SecurityContextUserAccessChecker;

@Path("user")
@Service
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class DefaultUserService implements UserService {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultUserService.class);
	
	@Inject
	private UserController controller;
	
	@GET
	@Path("/{login}")
	@RolesAllowed({ "ADMIN", "USER" })
	@ReturnType(clazz = UserInfo.class)
	@Override
	public Response get(
		@PathParam("login") UserLogin login,
		@Context SecurityContext security
	) throws NotAuthorizedException, NotFoundException {
		final String userLogin = login.getLogin();
		final SecurityContextUserAccessChecker checker = 
			new SecurityContextUserAccessChecker(security);
		
		return checker.doIfPrivileged(
			userLogin,
			() -> {
				final User user = controller.get(userLogin);
				
				if (user == null) {
					throw createNotFoundException("No user found with login: %s", userLogin);
				} else {
					return Response.ok(new UserInfo(user)).build();
				}
			},
			() -> {
				LOG.error(String.format(
					"Illegal access attempt of user %s to data of user %s",
					checker.getUserName(), userLogin
				));
				
				throw createUnauthorizedException("Unauthorized access.");
			}
		);
	}
	
	@PUT
	@RolesAllowed({ "ADMIN", "USER" })
	@ReturnType(clazz = UserInfo.class)
	@Override
	public Response update(
		UserInfo userMetadata,
		@Context SecurityContext security
	) throws NotAuthorizedException, NotFoundException {
		final String userLogin = userMetadata.getLogin();
		final SecurityContextUserAccessChecker checker = 
			new SecurityContextUserAccessChecker(security);
		
		return checker.doIfPrivileged(
			userLogin, 
			() -> {
				final User user = controller.get(userLogin);
				
				if (user == null) {
					throw createNotFoundException("No user found with login: %s", userLogin);
				} else {
					user.setEmail(userMetadata.getEmail());
					user.setPassword(userMetadata.getPassword());

					return Response.ok(new UserInfo(controller.update(user))).build();
				}
			},
			() -> {
				LOG.error(String.format(
					"Illegal update attempt of user %s to data of user %s",
					checker.getUserName(), userLogin
				));
				
				throw createUnauthorizedException("Unauthorized access.");
			}
		);
	}
	
	@DELETE
	@Path("/{login}")
	@RolesAllowed("ADMIN")
	@ReturnType(clazz = Message.class)
	@Override
	public Response delete(@PathParam("login") UserLogin login)
	throws NotFoundException {
		try {
			this.controller.remove(login.getLogin());
	
			return Response.ok(Message.withFormat("User %s deleted", login)).build();
		} catch (IllegalArgumentException iae) {
			throw createNotFoundException("User %s not found", login);
		}
	}

	@GET
	@RolesAllowed("ADMIN")
	@ReturnType(clazz = UserInfos.class)
	@Override
	public Response list() {
		return Response.ok(new UserInfos(controller.list())).build();
	}
}
