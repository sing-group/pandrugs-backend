/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import javax.ws.rs.NotAuthorizedException;

import es.uvigo.ei.sing.pandrugs.service.entity.Login;

/**
 * Service to check the user credentials.
 * 
 * @author Miguel Reboiro-Jato
 */
public interface SessionService {
	/**
	 * Checks the login credentials received, returning a HTTP status code 200
	 * OK if the credentials are valid or a 401 Unauthorized otherwise.
	 * 
	 * @param login the login credentials to check.
	 * @throws NotAuthorizedException if the credentials are not valid.
	 */
	public void login(Login login) throws NotAuthorizedException;
}
