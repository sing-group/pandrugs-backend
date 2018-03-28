/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import es.uvigo.ei.sing.pandrugs.service.entity.UserInfo;
import es.uvigo.ei.sing.pandrugs.service.entity.UserLogin;

/**
 * Service to manage the user data.
 * 
 * @author Miguel Reboiro-Jato
 */
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
	 * @servicetag ADMIN
	 * @servicetag USER
	 */
	public Response get(UserLogin login, SecurityContext security)
	throws NotAuthorizedException, NotFoundException;

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
	 * @servicetag ADMIN
	 * @servicetag USER
	 */
	public Response update(UserInfo userMetadata, SecurityContext security)
	throws NotAuthorizedException, NotFoundException;

	/**
	 * Deletes an user from the system.
	 * 
	 * <p>This method can be invoked only by admin users.</p>
	 * 
	 * @param login login of the user to be deleted.
	 * @return a confirmation message with a 200 OK HTTP status if deletion was
	 * successful.
	 * @throws NotFoundException if the user does not exists.
	 * @throws NotAuthorizedException if the client doesn't have permission to 
	 * access the user information (even if the requested user doesn't exists).
	 * @servicetag ADMIN
	 */
	public Response delete(UserLogin login)
	throws NotAuthorizedException, NotFoundException;

	/**
	 * Returns the list of users registered in the system.
	 * 
	 * <p>This method can be invoked only by admin users.</p>
	 * 
	 * @return the list of users in the system with a 200 OK HTTP status if
	 * the client has permission to access that user information.
	 * @throws NotAuthorizedException if the client doesn't have permission to 
	 * access the user information (even if the requested user doesn't exists).
	 * @servicetag ADMIN
	 */
	public Response list() throws NotAuthorizedException;
}
