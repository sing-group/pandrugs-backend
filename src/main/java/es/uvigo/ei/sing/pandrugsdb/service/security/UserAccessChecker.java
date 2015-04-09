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
package es.uvigo.ei.sing.pandrugsdb.service.security;

import static es.uvigo.ei.sing.pandrugsdb.service.ServiceUtils.createInternalServerErrorException;
import static es.uvigo.ei.sing.pandrugsdb.service.ServiceUtils.createUnauthorizedException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugsdb.util.ThrowingSupplier;
import es.uvigo.ei.sing.pandrugsdb.util.TypedThrowingFunction;

public interface UserAccessChecker {
	public abstract boolean isUserInRole(RoleType role);
	
	public abstract String getUserName();
	
	public default Response doIfPrivileged(
		String user,
		ThrowingSupplier<Response> actionOk,
		TypedThrowingFunction<Exception, Response, WebApplicationException> actionError,
		ThrowingSupplier<Response> accessError
	) throws WebApplicationException {
		try {
			if (isUserInRole(RoleType.ADMIN) ||
				user.equals(getUserName())
			) {
				return actionOk.get();
			} else {
				return accessError.get();
			}
		} catch (WebApplicationException wae) {
			throw wae;
		} catch (Exception e) {
			return actionError.apply(e);
		}
	}

	public default Response doIfPrivileged(
		String user,
		ThrowingSupplier<Response> actionOk,
		ThrowingSupplier<Response> accessError
	) throws WebApplicationException {
		return doIfPrivileged(
			user,
			actionOk,
			new TypedThrowingFunction<Exception, Response, WebApplicationException>() {
				@Override
				public Response apply(Exception value)
				throws WebApplicationException {
					throw createInternalServerErrorException(value);
				}
			},
			accessError
		);
	}

	public default Response doIfPrivileged(
		String user,
		ThrowingSupplier<Response> actionOk
	) throws WebApplicationException {
		return doIfPrivileged(
			user,
			actionOk,
			new TypedThrowingFunction<Exception, Response, WebApplicationException>() {
				@Override
				public Response apply(Exception value)
				throws WebApplicationException {
					throw createInternalServerErrorException(value);
				}
			},
			() -> { throw createUnauthorizedException("Unauthorized access"); }
		);
	}
}
