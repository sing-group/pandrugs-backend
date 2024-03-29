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

package es.uvigo.ei.sing.pandrugs.service;

import static es.uvigo.ei.sing.pandrugs.service.ServiceUtils.createBadRequestException;
import static es.uvigo.ei.sing.pandrugs.service.ServiceUtils.createNotFoundException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qmino.miredot.annotations.ReturnType;

import es.uvigo.ei.sing.pandrugs.controller.RegistrationController;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugs.service.entity.Message;
import es.uvigo.ei.sing.pandrugs.service.entity.UUID;
import es.uvigo.ei.sing.pandrugs.service.entity.UserInfo;

@Path("registration")
@Service
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class DefaultRegistrationService implements RegistrationService {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultRegistrationService.class);
	
	@Inject
	private RegistrationController controller;

	@PersistenceContext
	private EntityManager em;
	
	@POST
	@ReturnType(clazz = Message.class)
	@Override
	public Response register(
		Registration registration,
		@QueryParam("confirmurltemplate") String confirmationUrlTemplate
	) throws BadRequestException {
		try {
			if (confirmationUrlTemplate == null) {
				controller.register(
					registration.getLogin(),
					registration.getEmail(),
					registration.getPassword()
				);
			} else {
				controller.register(
					registration.getLogin(),
					registration.getEmail(),
					registration.getPassword(),
					confirmationUrlTemplate
				);
			}

			return Response.ok(new Message("User registered")).build();
		} catch (IllegalArgumentException iae) {
			LOG.warn("Error registering user");
			throw createBadRequestException(iae.getMessage());
		}
	}

	@GET
	@Path("/{uuid}")
	@Consumes(MediaType.WILDCARD)
	@ReturnType(clazz = UserInfo.class)
	@Override
	public Response confirm(@PathParam("uuid") UUID uuid)
	throws NotFoundException {
		try {
			return Response
				.ok(new UserInfo(controller.confirm(uuid.getUuid())))
			.build();
		} catch (IllegalArgumentException iae) {
			LOG.warn("Error confirming user");
			throw createNotFoundException(iae);
		}
	}
}
