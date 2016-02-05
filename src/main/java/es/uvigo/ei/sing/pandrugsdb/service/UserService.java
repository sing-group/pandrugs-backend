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

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.SecurityContext;

import es.uvigo.ei.sing.pandrugsdb.service.entity.Message;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadatas;

public interface UserService {
	/**
	 * Provides information about an user.
	 * <p>
	 * Administrator users can access the information of any user. Normal users
	 * can only access to their own information.
	 * </p>
	 * 
	 * @param login login of the user.
	 * @param security the security context of the application.
	 * @param security security context object. Should be provided by the
	 * container.
	 * @return the information of the user with a 200 OK HTTP status if the 
	 * client has permission to access that user information.
	 * @throws NotAuthorizedException if the client doesn't have permission to 
	 * access the user information (even if the requested user doesn't exists).
	 * @throws NotFoundException if the requested user doesn't exists (this 
	 * will only happen for administrator users).
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract UserMetadata get(UserLogin login, SecurityContext security)
	throws NotAuthorizedException, NotFoundException, InternalServerErrorException;

	/**
	 * Updates the user profile data. Only the email and password of the users
	 * can be updated.
	 * <p>
	 * Administrator users can access the information of any user. Normal users
	 * can only access to their own information.
	 * </p>
	 * 
	 * @param userMetadata new profile data of the user.
	 * @param security the security context of the application.
	 * @return the information of the user updated with a 200 OK HTTP status if
	 * the client has permission to access that user information.
	 * @throws NotAuthorizedException if the client doesn't have permission to 
	 * access the user information (even if the requested user doesn't exists).
	 * @throws NotFoundException if the requested user doesn't exists (this 
	 * will only happen for administrator users).
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract UserMetadata update(UserMetadata userMetadata, SecurityContext security)
	throws NotAuthorizedException, NotFoundException, InternalServerErrorException;

	/**
	 * Deletes an user from the system.
	 * 
	 * <p>This method can be invoked only by admin users.</p>
	 * 
	 * @param login login of the user to be deleted.
	 * @return a confirmation message with a 200 OK HTTP status if deletion was
	 * successful.
	 * @throws NotFoundException if the user does not exists.
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract Message delete(UserLogin login)
	throws NotFoundException, InternalServerErrorException;

	/**
	 * Returns the list of users registered in the system.
	 * 
	 * <p>This method can be invoked only by admin users.</p>
	 * 
	 * @return the list of users in the system with a 200 OK HTTP status if
	 * the client has permission to access that user information.
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract UserMetadatas list()
	throws InternalServerErrorException;
}