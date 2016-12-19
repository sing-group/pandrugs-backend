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

import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireStringSize;

import java.io.InputStream;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
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

import es.uvigo.ei.sing.pandrugsdb.controller.VariantsAnalysisController;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugsdb.service.security.SecurityContextUserAccessChecker;

/**
 * Service to submit VCF Analysis computations, as well as to follow their
 * status
 *
 * @autor Daniel Glez-Peña
 */
@Path("variantsanalysis")
@Service
@RolesAllowed({
	"ADMIN", "USER"
})
@Produces({
	MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Consumes({
	MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class DefaultVariantsAnalysisService implements VariantsAnalysisService {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultVariantsAnalysisService.class);

	@Inject
	private VariantsAnalysisController controller;

	@POST
	@Override
	@Path("/{login}")
	@Consumes(MediaType.WILDCARD)
	@ReturnType("java.lang.Integer")
	public Response startVariantsScoreUserComputation(
		@PathParam("login") UserLogin login,
		InputStream vcfFile,
		@QueryParam("name") String computationName,
		@Context SecurityContext security,
		@Context UriInfo currentUri
	) throws ForbiddenException,
		NotAuthorizedException {

		final String userLogin = login.getLogin();
		final SecurityContextUserAccessChecker checker = new SecurityContextUserAccessChecker(security);
		return checker.doIfPrivileged(
			userLogin,
			() -> {
				requireStringSize(computationName, 1, Integer.MAX_VALUE, "name must not be empty");
				int computationId = controller.startVariantsScopeUserComputation(login, vcfFile, computationName);
				return Response.created(
					currentUri.getAbsolutePathBuilder()
						.path("/" + computationId).build()
				)
					.build();
			},
			() -> {
				LOG.error(
					String.format(
						"Illegal access to create a computation as user %s on behalf of user %s",
						checker.getUserName(), userLogin
					)
				);

				throw new ForbiddenException("User " + userLogin + " is not you");
			}
		);

	}

	@Override
	@Path("{login}/{computationId}")
	@ReturnType("es.uvigo.ei.sing.pandrugsdb.service.entity.ComputationMetadata")
	@GET
	public Response getComputationStatus(
		@PathParam("login") UserLogin login,
		@PathParam("computationId") Integer computationId,
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
					throw new NotFoundException("computation id " + computationId + " not found");
				}

				if (!controller.getUserOfComputation(computationId).getLogin().equals(userLogin)) {
					throw new ForbiddenException("You have not a computation with id = " + computationId);
				}
				return Response.ok(
					controller.getComputationStatus(computationId)
				).build();
			},
			() -> {
				LOG.error(
					String.format(
						"Illegal access to get gene ranking for a computation as user %s on behalf of user %s",
						checker.getUserName(), userLogin
					)
				);

				throw new ForbiddenException("User " + userLogin + " is not you");
			}
		);
	}

	@Override
	@Path("{login}/{computationId}")
	@DELETE
	public Response deleteComputation(
		@PathParam("login") UserLogin login,
		@PathParam("computationId") Integer computationId,
		@Context SecurityContext security
	)
		throws ForbiddenException, NotAuthorizedException, InternalServerErrorException, NotFoundException {
		final String userLogin = login.getLogin();
		final SecurityContextUserAccessChecker checker = new SecurityContextUserAccessChecker(security);

		return checker.doIfPrivileged(
			userLogin,
			() -> {
				if (!controller.getComputationsForUser(login).containsKey(computationId)) {
					throw new NotFoundException(
						"Computation with id " + computationId + " has not been found"
					);
				}

				if (!controller.getUserOfComputation(computationId).getLogin().equals(userLogin)) {
					throw new ForbiddenException("You have not a computation with id = " + computationId);
				}

				controller.deleteComputation(computationId);

				return Response.noContent().build();
			},
			() -> {
				LOG.error(
					String.format(
						"Illegal access to get remove a computation as user %s on behalf of" +
							" user %s",
						checker.getUserName(), userLogin
					)
				);

				throw new ForbiddenException("User " + userLogin + " is not you");
			}
		);
	}

	@GET
	@Path("/{login}")
	@ReturnType("java.util.Map<Integer, es.uvigo.ei.sing.pandrugsdb.service.entity.ComputationMetadata>")
	@Override
	public Response getComputationsForUser(@PathParam("login") UserLogin login, @Context SecurityContext security)
		throws ForbiddenException, NotAuthorizedException, InternalServerErrorException {
		final String userLogin = login.getLogin();
		final SecurityContextUserAccessChecker checker = new SecurityContextUserAccessChecker(security);

		if (checker.isUserInRole(RoleType.GUEST)) {
			LOG.error(String.format("Your are a guest user, you cannot list computations. Give an id"));

			throw new ForbiddenException("Guest users cannot list computations");
		}

		return checker.doIfPrivileged(
			userLogin,
			() -> {
				return Response.ok(
					controller.getComputationsForUser(login)
				).build();
			},
			() -> {
				LOG.error(
					String.format(
						"Illegal access to get computations as user %s on behalf of user %s",
						checker.getUserName(), userLogin
					)
				);

				throw new ForbiddenException("User " + userLogin + " is not you");
			}
		);
	}
}
