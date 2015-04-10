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

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugsdb.service.entity.Message;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UUID;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadata;

public interface RegistrationService {
	/**
	 * Creates a new normal user registration request. An UUID will be 
	 * generated that user must use to access the application.
	 * <p>
	 * The confirmation path is:
	 * {@code http://&lt;server&gt;/public/registration/&lt;uuid&gt;}
	 * </p>
	 * 
	 * @param registration data for user registration.
	 * @return a confirmation message with a 200 OK HTTP status if registration
	 * was successful.
	 * @throws BadRequestException if the registration information is not valid.
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract Message register(Registration registration)
	throws BadRequestException, InternalServerErrorException;

	/**
	 * Confirms an user registration. If the UUID exists, the associated user 
	 * will be effectively created as a regular user.
	 * 
	 * @param uuid UUID generated during registration.
	 * @return the new user created after confirmation with a 200 OK HTTP 
	 * status.
	 * @throws NotFoundException if the uuid is not valid. 
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract UserMetadata confirm(UUID uuid)
	throws NotFoundException, InternalServerErrorException;
}