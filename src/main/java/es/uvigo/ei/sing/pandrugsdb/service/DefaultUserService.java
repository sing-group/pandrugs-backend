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

import static es.uvigo.ei.sing.pandrugsdb.service.ServiceUtils.createNotFoundException;
import static es.uvigo.ei.sing.pandrugsdb.service.ServiceUtils.createUnauthorizedException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
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

import es.uvigo.ei.sing.pandrugsdb.controller.UserController;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.service.entity.Message;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadatas;
import es.uvigo.ei.sing.pandrugsdb.service.security.SecurityContextUserAccessChecker;

@Path("user")
@Service
@RolesAllowed("ADMIN")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class DefaultUserService implements UserService {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultUserService.class);
	
	@Inject
	private UserController controller;
	
	@GET
	@Path("/{login}")
	@RolesAllowed({ "ADMIN", "USER" })
	@Override
	public Response get(
		@PathParam("login") UserLogin login,
		@Context SecurityContext security
	) throws NotAuthorizedException, NotFoundException, InternalServerErrorException {
		final String userLogin = login.getLogin();
		final SecurityContextUserAccessChecker checker = 
			new SecurityContextUserAccessChecker(security);
		
		return checker.doIfPrivileged(
			userLogin,
			() -> {
				final User user = controller.get(userLogin);
				
				if (user == null) {
					throw createNotFoundException(
						"No user found with login: " + userLogin
					);
				} else {
					return Response.ok(
						new UserMetadata(user)
					).build();
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
	@Override
	public Response update(
		UserMetadata userMetadata,
		@Context SecurityContext security
	) throws NotAuthorizedException, NotFoundException, InternalServerErrorException {
		final String userLogin = userMetadata.getLogin();
		final SecurityContextUserAccessChecker checker = 
			new SecurityContextUserAccessChecker(security);
		
		return checker.doIfPrivileged(
			userLogin, 
			() -> {
				final User user = controller.get(userLogin);
				
				if (user == null) {
					throw createNotFoundException(
						"No user found with login: " + userLogin
					);
				} else {
					user.setEmail(userMetadata.getEmail());
					user.setPassword(userMetadata.getPassword());

					return Response.ok(
						new UserMetadata(controller.update(user))
					).build();
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
	
	@Override
	@DELETE
	public Response delete(@PathParam("login") UserLogin login)
	throws NotFoundException, InternalServerErrorException {
		try {
			this.controller.remove(login.getLogin());
	
			return Response.ok(
				new Message("User " + login + " deleted")
			).build();
		} catch (IllegalArgumentException iae) {
			throw createNotFoundException(
				"User " + login + " not found"
			);
		}
	}

	@GET
	@Override
	public Response list() 
	throws InternalServerErrorException {
		return Response.ok(
			UserMetadatas.buildFor(controller.list())
		).build();
	}
}
