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
package es.uvigo.ei.sing.pandrugsdb;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.pandrugsdb.service.entity.ErrorMessage;

@Provider
public class UnexpectedExceptionMapper implements ExceptionMapper<Throwable> {
	private final Logger LOG = LoggerFactory.getLogger(UnexpectedExceptionMapper.class);
	
	@Override
	public Response toResponse(Throwable exception) {
		if (exception instanceof WebApplicationException) {
			LOG.error("Web application exception", exception);
			
			return ((WebApplicationException) exception).getResponse();
		} else {
			LOG.error("Unexpected exception", exception);
			
			return Response.status(INTERNAL_SERVER_ERROR)
				.entity(new ErrorMessage(INTERNAL_SERVER_ERROR.getStatusCode(), exception.getMessage()))
			.build();
		}
	}
}
