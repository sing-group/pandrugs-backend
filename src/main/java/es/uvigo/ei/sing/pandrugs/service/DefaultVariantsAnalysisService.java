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

import static es.uvigo.ei.sing.pandrugs.service.ServiceUtils.createBadRequestException;
import static es.uvigo.ei.sing.pandrugs.service.ServiceUtils.createForbiddentException;
import static es.uvigo.ei.sing.pandrugs.service.ServiceUtils.createNotFoundException;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireStringSize;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qmino.miredot.annotations.ReturnType;

import es.uvigo.ei.sing.pandrugs.controller.VariantsAnalysisController;
import es.uvigo.ei.sing.pandrugs.service.entity.ComputationMetadata;
import es.uvigo.ei.sing.pandrugs.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugs.service.security.SecurityContextUserAccessChecker;

@Path("variantsanalysis")
@Service
@RolesAllowed({ "ADMIN", "USER", "GUEST" })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class DefaultVariantsAnalysisService implements VariantsAnalysisService {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultVariantsAnalysisService.class);

	@Inject
	private VariantsAnalysisController controller;

	@POST
	@Path("/{login}")
	@Consumes(MediaType.WILDCARD)
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	@ReturnType(clazz = Integer.class)
	@Override
	public Response startVariantsScoreUserComputation(
		@PathParam("login") UserLogin login,
		InputStream vcfFile,
		@QueryParam("name") String computationName,
		@QueryParam("resultsurltemplate") String resultsURLTemplate,
		@Context SecurityContext security,
		@Context UriInfo currentUri
	) throws ForbiddenException, NotAuthorizedException {
		final String userLogin = login.getLogin();
		final SecurityContextUserAccessChecker checker = new SecurityContextUserAccessChecker(security);
		
		return checker.doIfPrivileged(
			userLogin,
			() -> {
				requireStringSize(computationName, 1, Integer.MAX_VALUE, "name must not be empty");
				
				final String computationId = controller.startVariantsScopeUserComputation(login, vcfFile,
						computationName, resultsURLTemplate);
				
				return Response.created(
					currentUri.getAbsolutePathBuilder().path("/" + computationId).build()
				) .build();
			},
			() -> {
				LOG.error(String.format(
					"Illegal access to create a computation as user %s on behalf of user %s",
					checker.getUserName(), userLogin
				));

				throw createForbiddentException("User %s is not you", userLogin);
			}
		);
	}

	@GET
	@Path("{login}/{computationId}")
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	@ReturnType(clazz = ComputationMetadata.class)
	@Override
	public Response getComputationStatus(
		@PathParam("login") UserLogin login,
		@PathParam("computationId") String computationId,
		@Context SecurityContext security
	) throws ForbiddenException, NotAuthorizedException {
		final String userLogin = login.getLogin();
		final SecurityContextUserAccessChecker checker = new SecurityContextUserAccessChecker(security);

		return checker.doIfPrivileged(
			userLogin,
			() -> {
				try {
					controller.getUserOfComputation(computationId);
				} catch (IllegalArgumentException e) {
					throw createNotFoundException("Computation id %s not found", computationId);
				}

				if (!controller.getUserOfComputation(computationId).getLogin().equals(userLogin)) {
					throw createForbiddentException("You do not have a computation with id %s", computationId);
				}
				return Response.ok(
					controller.getComputationStatus(computationId)
				).build();
			},
			() -> {
				LOG.error(String.format(
					"Illegal access to get gene ranking for a computation as user %s on behalf of user %s",
					checker.getUserName(), userLogin
				));

				throw createForbiddentException("User %s is not you", userLogin);
			}
		);
	}

	@GET
	@Path("files/{login}/{computationId}/vscorefile")
	@PermitAll
	@ReturnType(clazz = OutputStream.class)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Override
	public Response downloadVariantsScoreComputationDetails(
			@PathParam("login") UserLogin login,
			@PathParam("computationId") String computationId
	) {
		final String userLogin = login.getLogin();

		try {
			controller.getUserOfComputation(computationId);
		} catch (IllegalArgumentException e) {
			throw createNotFoundException("Computation id %s not found", computationId);
		}

		if (!controller.getUserOfComputation(computationId).getLogin().equals(userLogin)) {
			throw createForbiddentException("You do not have a computation with id %s", computationId);
		}

		if (!controller.getComputationStatus(computationId).isFinished()) {
			throw createBadRequestException("The computation has not finished yet");
		}

		return Response.ok(
				controller.getVariantsScoreFile(computationId)
		).header("Content-Disposition", "attachment; filename=\"" + computationId + "-vscore.tsv\"" )
				.build();
	}

	@DELETE
	@Path("{login}/{computationId}")
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	@ReturnType(clazz = Void.class)
	@Override
	public Response deleteComputation(
		@PathParam("login") UserLogin login,
		@PathParam("computationId") String computationId,
		@Context SecurityContext security
	) throws ForbiddenException, NotAuthorizedException, NotFoundException {
		final String userLogin = login.getLogin();
		final SecurityContextUserAccessChecker checker = new SecurityContextUserAccessChecker(security);

		return checker.doIfPrivileged(
			userLogin,
			() -> {
				if (!controller.getComputationsForUser(login).containsKey(computationId)) {
					throw createNotFoundException("Computation id %s not found", computationId);
				}

				if (!controller.getUserOfComputation(computationId).getLogin().equals(userLogin)) {
					throw createForbiddentException("You do not have a computation with id %s", computationId);
				}

				controller.deleteComputation(computationId);

				return Response.noContent().build();
			},
			() -> {
				LOG.error(String.format(
					"Illegal access to get remove a computation as user %s on behalf of user %s",
					checker.getUserName(), userLogin
				));

				throw createForbiddentException("User %s is not you", userLogin);
			}
		);
	}

	@GET
	@Path("/{login}")
	@RolesAllowed({ "ADMIN", "USER" })
	@ReturnType("java.util.Map<java.lang.Integer, es.uvigo.ei.sing.pandrugs.service.entity.ComputationMetadata>")
	@Override
	public Response getComputationsForUser(
		@PathParam("login") UserLogin login,
		@Context SecurityContext security
	) throws ForbiddenException, NotAuthorizedException {
		final String userLogin = login.getLogin();
		final SecurityContextUserAccessChecker checker = new SecurityContextUserAccessChecker(security);

		return checker.doIfPrivileged(
			userLogin,
			() -> {
				return Response.ok(
					controller.getComputationsForUser(login)
				).build();
			},
			() -> {
				LOG.error(String.format(
					"Illegal access to get computations as user %s on behalf of user %s",
					checker.getUserName(), userLogin
				));

				throw createForbiddentException("User %s is not you", userLogin);
			}
		);
	}
}
