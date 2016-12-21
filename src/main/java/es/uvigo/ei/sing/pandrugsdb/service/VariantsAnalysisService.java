/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 - 2016 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

import java.io.InputStream;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;

/**
 * Service to submit VCF Analysis computations, as well as to follow their
 * status
 *
 * @author Daniel Glez-Peña
 */
public interface VariantsAnalysisService {
	/**
	 * Submits a new variant score computation for an existing user.
	 *
	 * @param login login of the user.
	 * @param vcfFile variants in VCF format to analyze.
	 * @param computationName the name for the computation.
	 * @param security security context object. Should be provided by the container.
	 * @param currentUri the current URI information
	 * @return the computation id as an integer value
	 * @throws ForbiddenException if the authenticated user does not have permissions to create the computation.
	 * @throws NotAuthorizedException if provided login has not been correctly authenticated.
	 */
	public Response startVariantsScoreUserComputation(
		UserLogin login,
		InputStream vcfFile,
		String computationName,
		SecurityContext security,
		UriInfo currentUri
	) throws ForbiddenException, NotAuthorizedException;

	/**
	 * Obtains the current status of a previously submitted computation.
	 *
	 * @see VariantsAnalysisService#startVariantsScoreUserComputation
	 *
	 * @param login login of the user.
	 * @param computationId the computation id to query.
	 * @param security security context object. Should be provided by the container.
	 * @return the status of the computation.
	 * @throws ForbiddenException if the authenticated user does not have permissions to see this computation status.
	 * @throws NotAuthorizedException if provided login has not been correctly authenticated
	 * or does not own the computation he/she is asking for.
	 */
	public Response getComputationStatus(
		UserLogin login,
		Integer computationId,
		SecurityContext security
	) throws ForbiddenException, NotAuthorizedException;

	/**
	 * Deletes a computation.
	 *
	 * @see VariantsAnalysisService#startVariantsScoreUserComputation
	 *
	 * @param login login of the user.
	 * @param computationId the computation id to delete.
	 * @param security security context object. Should be provided by the container.
	 * @return no content.
	 * @throws ForbiddenException if the authenticated user does not have permissions to delete this computation.
	 * @throws NotAuthorizedException if provided login has not been correctly authenticated
	 * or does not own the computation he/she is asking for.
	 * @throws NotFoundException if the computation does not exist.
	 */
	public Response deleteComputation(
		UserLogin login,
		Integer computationId,
		SecurityContext security
	) throws ForbiddenException, NotAuthorizedException, NotFoundException;

	/**
	 * Obtains the current status of all submitted computations.
	 *
	 * @see VariantsAnalysisService#startVariantsScoreUserComputation
	 *
	 * @param login login of the user.
	 * @param security security context object. Should be provided by the container.
	 * @return all computations and their status.
	 * @throws ForbiddenException if the authenticated user does not have permissions to see this computation status.
	 * @throws NotAuthorizedException if provided login has not been correctly authenticated
	 * or does not own the computation he/she is asking for.
	 */
	public Response getComputationsForUser(
		UserLogin login,
		SecurityContext security
	) throws ForbiddenException, NotAuthorizedException;
}
