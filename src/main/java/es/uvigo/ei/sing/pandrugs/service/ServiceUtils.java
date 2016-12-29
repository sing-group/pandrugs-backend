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

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import es.uvigo.ei.sing.pandrugs.service.entity.ErrorMessage;

public final class ServiceUtils {
	private ServiceUtils() {}

	public static NotAuthorizedException createUnauthorizedException(String message) {
		return createErrorException(
			NotAuthorizedException::new, UNAUTHORIZED, message);
	}

	public static NotFoundException createNotFoundException(String formattableMessage, Object ... params) {
		return createErrorException(NotFoundException::new, NOT_FOUND, String.format(formattableMessage, params));
	}

	public static NotFoundException createNotFoundException(String message) {
		return createErrorException(NotFoundException::new, NOT_FOUND, message);
	}
	
	public static NotFoundException createNotFoundException(Exception exception) {
		return createErrorException(NotFoundException::new, NOT_FOUND, exception);
	}

	public static BadRequestException createBadRequestException(String message) {
		return createErrorException(BadRequestException::new, BAD_REQUEST, message);
	}

	public static BadRequestException createBadRequestException(Exception exception) {
		return createErrorException(BadRequestException::new, BAD_REQUEST, exception);
	}

	public static InternalServerErrorException createInternalServerErrorException(String message) {
		return createErrorException(
			InternalServerErrorException::new, INTERNAL_SERVER_ERROR, message);
	}
	
	public static InternalServerErrorException createInternalServerErrorException(Exception exception) {
		return createErrorException(
			InternalServerErrorException::new, INTERNAL_SERVER_ERROR, exception);
	}

	public static ForbiddenException createForbiddentException(String message) {
		return createErrorException(ForbiddenException::new, FORBIDDEN, message);
	}

	public static ForbiddenException createForbiddentException(String formattableMessage, Object ... params) {
		return createErrorException(ForbiddenException::new, FORBIDDEN, String.format(formattableMessage, params));
	}
	
	public static <T extends WebApplicationException> T createErrorException(
		BiFunction<Response, Throwable, T> exceptionBuilder,
		Response.Status status,
		Throwable exception
	) {
		final Response response = Response.status(status).entity(new ErrorMessage(
			status.getStatusCode(),
			Optional.ofNullable(exception.getMessage()).orElse("Unexpected exception thrown")
		)).build();
		
		return exceptionBuilder.apply(response, exception);
	}

	public static <T extends WebApplicationException> T createErrorException(
		Function<Response, T> exceptionBuilder,
		Response.Status status,
		String message
	) {
		return exceptionBuilder.apply(
			Response.status(status).entity(new ErrorMessage(
				status.getStatusCode(), message
			)).build()
		);
	}
}
