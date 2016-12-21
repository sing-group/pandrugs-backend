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

import static es.uvigo.ei.sing.pandrugsdb.service.ServiceUtils.createBadRequestException;
import static es.uvigo.ei.sing.pandrugsdb.util.Checks.isEmpty;
import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qmino.miredot.annotations.ReturnType;

import es.uvigo.ei.sing.pandrugsdb.controller.GeneDrugController;
import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;

/**
 * Service to query the gene drugs lists.
 * 
 * @author Miguel Reboiro-Jato
 */
@Path("genedrug")
@Service
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class DefaultGeneDrugService implements GeneDrugService {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultGeneDrugService.class);
	
	@Inject
	private GeneDrugController controller;

	@GET
	@Consumes(MediaType.WILDCARD)
	@ReturnType(clazz = GeneDrugGroupInfos.class)
	@Override
	public Response list(
		@QueryParam("gene") Set<String> genes,
		@QueryParam("drug") Set<String> drugs,
		@QueryParam("cancerDrugStatus") Set<String> cancerDrugStatus,
		@QueryParam("nonCancerDrugStatus") Set<String> nonCancerDrugStatus,
		@QueryParam("target") String target,
		@QueryParam("direct") String direct
	) throws BadRequestException {
		try {
			if (!isEmpty(genes) && !isEmpty(drugs)) {
				throw new IllegalArgumentException("Genes and dugs can't be provided at the same time");
			} else if (isEmpty(genes) && isEmpty(drugs)) {
				throw new IllegalArgumentException("At least one gene or one drug must be provided");
			} else if (isEmpty(drugs)) {
				final List<GeneDrugGroup> geneDrugs = controller.searchByGenes(
					new GeneDrugQueryParameters(
						cancerDrugStatus, nonCancerDrugStatus, target, direct
					),
					genes.stream().sorted().toArray(String[]::new)
				);
				
				return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
			} else {
				final List<GeneDrugGroup> geneDrugs = controller.searchByDrugs(
					new GeneDrugQueryParameters(
						cancerDrugStatus, nonCancerDrugStatus, target, direct
					),
					drugs.stream().sorted().toArray(String[]::new)
				);
				
				return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
			}
		} catch (IllegalArgumentException | NullPointerException e) {
			LOG.warn("Error listing gene-drugs", e);
			throw createBadRequestException(e);
		}
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ReturnType(clazz = GeneDrugGroupInfos.class)
	@Override
	public Response listRanked(
		GeneRanking geneRanking,
		@QueryParam("cancerDrugStatus") Set<String> cancerDrugStatus,
		@QueryParam("nonCancerDrugStatus") Set<String> nonCancerDrugStatus,
		@QueryParam("target") String target,
		@QueryParam("direct") String direct
	) throws BadRequestException {
		try {
			requireNonNull(geneRanking, "geneRanking can't be null");
			requireNonEmpty(geneRanking.getGeneRank(), "At least one gene must be provided");
			
			final List<GeneDrugGroup> geneDrugs = controller.searchByRanking(
				new GeneDrugQueryParameters(
					cancerDrugStatus, nonCancerDrugStatus, target, direct
				),
				geneRanking
			);

			return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
		} catch (IllegalArgumentException | NullPointerException e) {
			LOG.warn("Error listing gene-drugs from rank", e);
			throw createBadRequestException(e);
		}
	}

	@GET
	@Consumes(MediaType.WILDCARD)
	@Path("fromComputationId")
	@ReturnType(clazz = GeneDrugGroupInfos.class)
	@Override
	public Response listFromComputationId(
		@QueryParam("computationId") Integer computationId,
		@QueryParam("cancerDrugStatus") Set<String> cancerDrugStatus,
		@QueryParam("nonCancerDrugStatus") Set<String> nonCancerDrugStatus,
		@QueryParam("target") String target,
		@QueryParam("direct") String direct
	) throws BadRequestException {
		try {
			requireNonNull(computationId, "A computation Id must be provided");

			final List<GeneDrugGroup> geneDrugs = controller.searchFromComputationId(
				new GeneDrugQueryParameters(
					cancerDrugStatus, nonCancerDrugStatus, target, direct
				),
				computationId
			);

			return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
		} catch (IllegalArgumentException | NullPointerException e) {
			LOG.warn("Error listing gene-drugs from computation id", e);
			throw createBadRequestException(e);
		}
	}

	@GET
	@Path("/gene")
	@ReturnType(clazz = String[].class)
	@Override
	public Response listGeneSymbols(
		@QueryParam("query") @DefaultValue("") String query,
		@QueryParam("maxResults") @DefaultValue("-1") int maxResults
	) {
		try {
			requireNonNull(query, "query can't be null");
			
			return Response.ok(controller.listGeneSymbols(query, maxResults)).build();
		} catch (NullPointerException e) {
			LOG.warn("Error retrieving gene symbols", e);
			throw createBadRequestException(e);
		}
	}

	@GET
	@Path("/drug")
	@ReturnType(clazz = String[].class)
	@Override
	public Response listStandardDrugNames(
		@QueryParam("query") @DefaultValue("") String query,
		@QueryParam("maxResults") @DefaultValue("-1") int maxResults
	) {
		try {
			requireNonNull(query, "query can't be null");
	
			return Response.ok(controller.listStandardDrugNames(query, maxResults)).build();
		} catch (NullPointerException e) {
			LOG.warn("Error retrieving standard drug names", e);
			throw createBadRequestException(e);
		}
	}
}
