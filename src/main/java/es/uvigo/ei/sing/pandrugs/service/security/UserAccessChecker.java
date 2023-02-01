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

package es.uvigo.ei.sing.pandrugs.service.security;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.WebApplicationException;

import es.uvigo.ei.sing.pandrugs.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugs.service.ServiceUtils;
import es.uvigo.ei.sing.pandrugs.util.ThrowingSupplier;
import es.uvigo.ei.sing.pandrugs.util.WrapperRuntimeException;

public interface UserAccessChecker {
	public abstract boolean isUserInRole(RoleType role);
	
	public abstract String getUserName();
	
	public default <R, T extends WebApplicationException, AE extends WebApplicationException>
	R doIfPrivileged(
		String user,
		ThrowingSupplier<R> actionOk,
		Function<Exception, WebApplicationException> actionError,
		Supplier<AE> accessError
	) throws WebApplicationException {
		try {
			if (isUserInRole(RoleType.ADMIN) ||
				user.equals(getUserName())
			) {
				return actionOk.get();
			} else {
				throw accessError.get();
			}
		} catch (WebApplicationException wae) {
			throw wae;
		} catch (WrapperRuntimeException re) {
			if (re.unwrap() instanceof WebApplicationException) {
				throw (WebApplicationException) re.unwrap();
			} else {
				throw actionError.apply(re.unwrap());
			}
		} catch (Exception e) {
			throw actionError.apply(e);
		}
	}

	public default <R, AE extends WebApplicationException> R doIfPrivileged(
		String user,
		ThrowingSupplier<R> actionOk,
		Supplier<AE> accessError
	) throws WebApplicationException {
		return doIfPrivileged(
			user,
			actionOk,
			ServiceUtils::createInternalServerErrorException,
			accessError
		);
	}
}
