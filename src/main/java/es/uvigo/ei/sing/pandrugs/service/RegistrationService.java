/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugs.service.entity.UUID;

/**
 * Service to perform the user registration and confirmation.
 * 
 * @author Miguel Reboiro-Jato
 */
public interface RegistrationService {
	/**
	 * Creates a new normal user registration request. An UUID will be
	 * generated that user must use to access the application.
	 * <p>
	 * The confirmation path is:
	 * {@code http://<server>/public/registration/<uuid>}
	 * </p>
	 *
	 * @param registration data for user registration.
	 * @param confirmationUrlTemplate a template for the confirmation URL that
	 *                                goes inside the confirmation email. Use %s
	 *                                as placeholder for the confirmation token
	 * @return a confirmation message with a 200 OK HTTP status if registration
	 * was successful.
	 * @throws BadRequestException if the registration information is not valid.
	 */
	public Response register(Registration registration, String confirmationUrlTemplate)
	throws BadRequestException;

	/**
	 * Confirms an user registration. If the UUID exists, the associated user 
	 * will be effectively created as a regular user.
	 * 
	 * @param uuid UUID generated during registration.
	 * @return the new user created after confirmation with a 200 OK HTTP 
	 * status.
	 * @throws NotFoundException if the uuid is not valid. 
	 */
	public Response confirm(UUID uuid)
	throws NotFoundException;
}