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

import static es.uvigo.ei.sing.pandrugsdb.service.ServiceUtils.createBadRequestException;
import static es.uvigo.ei.sing.pandrugsdb.service.ServiceUtils.createNotFoundException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import es.uvigo.ei.sing.pandrugsdb.controller.RegistrationController;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.service.entity.Message;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UUID;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadata;

@Path("registration")
@Service
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class DefaultRegistrationService implements RegistrationService {
	@Inject
	private RegistrationController controller;

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Message register(Registration registration)
	throws BadRequestException, InternalServerErrorException {
		try {
			controller.register(
				registration.getLogin(),
				registration.getEmail(),
				registration.getPassword()
			);

			return new Message("User registered");
		} catch (IllegalArgumentException iae) {
			throw createBadRequestException(iae.getMessage());
		}
	}

	@POST
	@Override
	public Message register(Registration registration, @QueryParam("confirmurltemplate") String
			confirmationUrlTemplate) throws
			BadRequestException, InternalServerErrorException {
		try {
			if (confirmationUrlTemplate == null) {
				return register(registration);
			}
			else {
				controller.register(
						registration.getLogin(),
						registration.getEmail(),
						registration.getPassword(),
						confirmationUrlTemplate
				);
			}

			return new Message("User registered");
		} catch (IllegalArgumentException iae) {
			throw createBadRequestException(iae.getMessage());
		}
	}

	@GET
	@Path("/{uuid}")
	@Consumes(MediaType.WILDCARD)
	@Override
	public UserMetadata confirm(@PathParam("uuid") UUID uuid)
	throws NotFoundException, InternalServerErrorException {
		try {
			final User user = controller.confirm(uuid.getUuid());
			return new UserMetadata(user);
		} catch (IllegalArgumentException iae) {
			throw createNotFoundException(iae);
		}
	}
}
