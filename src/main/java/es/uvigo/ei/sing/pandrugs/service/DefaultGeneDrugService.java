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

package es.uvigo.ei.sing.pandrugs.service;

import static es.uvigo.ei.sing.pandrugs.service.ServiceUtils.createBadRequestException;
import static es.uvigo.ei.sing.pandrugs.util.Checks.isEmpty;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireNonEmpty;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
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

import es.uvigo.ei.sing.pandrugs.controller.GeneDrugController;
import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.controller.entity.GeneExpression;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugs.service.entity.CnvData;
import es.uvigo.ei.sing.pandrugs.service.entity.MultiOmicsAnalysisInputData;
import es.uvigo.ei.sing.pandrugs.service.entity.DrugNames;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneExpressionData;
import es.uvigo.ei.sing.pandrugs.service.entity.GenePresence;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;

/**
 * Service to query the gene drugs lists.
 * 
 * @author Miguel Reboiro-Jato
 */
@Path("genedrug")
@Service
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.MULTIPART_FORM_DATA })
public class DefaultGeneDrugService implements GeneDrugService {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultGeneDrugService.class);
	
	@Inject
	private GeneDrugController controller;

	@GET
	@Consumes(MediaType.WILDCARD)
	@ReturnType(clazz = GeneDrugGroupInfos.class)
	@Override
	public Response listByGeneOrDrugByGet(
		@QueryParam("gene") Set<String> genes,
		@QueryParam("drug") Set<String> drugs,
		@QueryParam("cancerDrugStatus") Set<String> cancerDrugStatus,
		@QueryParam("nonCancerDrugStatus") Set<String> nonCancerDrugStatus,
		@QueryParam("cancer") Set<String> cancerTypes,
		@QueryParam("directTarget") boolean directTarget,
		@QueryParam("biomarker") boolean biomarker,
		@QueryParam("pathwayMember") boolean pathwayMember,
		@QueryParam("geneDependency") boolean geneDependency
	) throws BadRequestException {
		return this.listByGeneOrDrug(
			genes, drugs, cancerDrugStatus, nonCancerDrugStatus, cancerTypes, directTarget, biomarker, pathwayMember, geneDependency
		);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ReturnType(clazz = GeneDrugGroupInfos.class)
	@Override
	public Response listByGeneOrDrugByPost(
		@FormParam("gene") Set<String> genes,
		@FormParam("drug") Set<String> drugs,
		@FormParam("cancerDrugStatus") Set<String> cancerDrugStatus,
		@FormParam("nonCancerDrugStatus") Set<String> nonCancerDrugStatus,
		@FormParam("cancer") Set<String> cancerTypes,
		@FormParam("directTarget") boolean directTarget,
		@FormParam("biomarker") boolean biomarker,
		@FormParam("pathwayMember") boolean pathwayMember,
		@QueryParam("geneDependency") boolean geneDependency
	) throws BadRequestException {
		return this.listByGeneOrDrug(
			genes, drugs, cancerDrugStatus, nonCancerDrugStatus, cancerTypes, directTarget, biomarker, pathwayMember, geneDependency
		);
	}
	
	private Response listByGeneOrDrug(
		Set<String> genes,
		Set<String> drugs,
		Set<String> cancerDrugStatus,
		Set<String> nonCancerDrugStatus,
		Set<String> cancerTypes,
		boolean directTarget,
		boolean biomarker,
		boolean pathwayMember,
		boolean geneDependency
	) throws BadRequestException {
		try {
			if (!isEmpty(genes) && !isEmpty(drugs)) {
				throw new IllegalArgumentException("Genes and dugs can't be provided at the same time");
			} else if (isEmpty(genes) && isEmpty(drugs)) {
				throw new IllegalArgumentException("At least one gene or one drug must be provided");
			} else if (isEmpty(drugs)) {
				final List<GeneDrugGroup> geneDrugs = controller.searchByGenes(
					new GeneDrugQueryParameters(
						cancerDrugStatus, nonCancerDrugStatus, cancerTypes, directTarget, biomarker, pathwayMember, geneDependency
					),
					genes.stream().toArray(String[]::new)
				);

				return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
			} else {
				final List<GeneDrugGroup> geneDrugs = controller.searchByDrugs(
					new GeneDrugQueryParameters(
						cancerDrugStatus, nonCancerDrugStatus, cancerTypes, directTarget, biomarker, pathwayMember, geneDependency
					),
					drugs.stream().toArray(String[]::new)
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
		@QueryParam("cancer") Set<String> cancerTypes,
		@QueryParam("directTarget") boolean directTarget,
		@QueryParam("biomarker") boolean biomarker,
		@QueryParam("pathwayMember") boolean pathwayMember,
		@QueryParam("geneDependency") boolean geneDependency
	) throws BadRequestException {
		try {
			requireNonNull(geneRanking, "geneRanking can't be null");
			requireNonEmpty(geneRanking.getGeneRank(), "At least one gene must be provided");
			
			final List<GeneDrugGroup> geneDrugs = controller.searchByRanking(
				new GeneDrugQueryParameters(
					cancerDrugStatus, nonCancerDrugStatus, cancerTypes, directTarget, biomarker, pathwayMember, geneDependency
				),
				geneRanking
			);

			return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
		} catch (IllegalArgumentException | NullPointerException e) {
			LOG.warn("Error listing gene-drugs from rank", e);
			throw createBadRequestException(e);
		}
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("cnv")
	@ReturnType(clazz = GeneDrugGroupInfos.class)
	@Override
	public Response listCnv(
		CnvData cnvData,
		@QueryParam("cancerDrugStatus") Set<String> cancerDrugStatus,
		@QueryParam("nonCancerDrugStatus") Set<String> nonCancerDrugStatus,
		@QueryParam("cancer") Set<String> cancerTypes,
		@QueryParam("directTarget") boolean directTarget,
		@QueryParam("biomarker") boolean biomarker,
		@QueryParam("pathwayMember") boolean pathwayMember,
		@QueryParam("geneDependency") boolean geneDependency
	) throws BadRequestException {
		try {
			requireNonNull(cnvData, "cnv can't be null");
			requireNonEmpty(cnvData.getDataMap().keySet(), "At least one gene must be provided");
			
			final List<GeneDrugGroup> geneDrugs = controller.searchByCnv(
				new GeneDrugQueryParameters(
					cancerDrugStatus, nonCancerDrugStatus, cancerTypes, directTarget, biomarker, pathwayMember, geneDependency
				),
				cnvData
			);

			return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
		} catch (IllegalArgumentException | NullPointerException e) {
			LOG.warn("Error listing gene-drugs from CNV", e);
			throw createBadRequestException(e);
		}
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("multiomics")
	@ReturnType(clazz = GeneDrugGroupInfos.class)
	@Override
	public Response listMultiOmicsCnvAndExpression(
		MultiOmicsAnalysisInputData multiOmicsAnalysisInputData,
		@QueryParam("cancerDrugStatus") Set<String> cancerDrugStatus,
		@QueryParam("nonCancerDrugStatus") Set<String> nonCancerDrugStatus,
		@QueryParam("cancer") Set<String> cancerTypes,
		@QueryParam("directTarget") boolean directTarget,
		@QueryParam("biomarker") boolean biomarker,
		@QueryParam("pathwayMember") boolean pathwayMember,
		@QueryParam("geneDependency") boolean geneDependency
	) throws BadRequestException {
		try {
			requireNonNull(multiOmicsAnalysisInputData.getCnvData(), "cnv can't be null");
			CnvData cnvData = multiOmicsAnalysisInputData.getCnvData();
			requireNonEmpty(cnvData.getDataMap().keySet(), "At least one gene must be provided");

			requireNonNull(multiOmicsAnalysisInputData.getGeneExpressionData(), "expression data can't be null");
			GeneExpressionData geneExpressionData = multiOmicsAnalysisInputData.getGeneExpressionData();
			requireNonEmpty(geneExpressionData.getGeneExpression().keySet(), "At least one gene must be provided");

			final List<GeneDrugGroup> geneDrugs = controller.searchByCnvWithExpression(
				new GeneDrugQueryParameters(
					cancerDrugStatus, nonCancerDrugStatus, cancerTypes, directTarget, biomarker, pathwayMember, geneDependency
				),
				cnvData, new GeneExpression(geneExpressionData.getGeneExpression())
			);

			return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
		} catch (IllegalArgumentException | NullPointerException e) {
			LOG.warn("Error listing gene-drugs from multi-omics analysis (CNV and expression data)", e);
			throw createBadRequestException(e);
		}
	}

	@GET
	@Consumes(MediaType.WILDCARD)
	@Path("fromComputationId")
	@ReturnType(clazz = GeneDrugGroupInfos.class)
	@Override
	public Response listFromComputationId(
		@QueryParam("computationId") String computationId,
		@QueryParam("cancerDrugStatus") Set<String> cancerDrugStatus,
		@QueryParam("nonCancerDrugStatus") Set<String> nonCancerDrugStatus,
		@QueryParam("cancer") Set<String> cancerTypes,
		@QueryParam("directTarget") boolean directTarget,
		@QueryParam("biomarker") boolean biomarker,
		@QueryParam("pathwayMember") boolean pathwayMember,
		@QueryParam("geneDependency") boolean geneDependency
	) throws BadRequestException {
		try {
			requireNonNull(computationId, "A computation Id must be provided");

			final List<GeneDrugGroup> geneDrugs = controller.searchFromComputationId(
				new GeneDrugQueryParameters(
					cancerDrugStatus, nonCancerDrugStatus, cancerTypes, directTarget, biomarker, pathwayMember, geneDependency
				),
				computationId
			);

			return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
		} catch (IllegalArgumentException | NullPointerException e) {
			LOG.warn("Error listing gene-drugs from computation id", e);
			throw createBadRequestException(e);
		}
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("fromComputationId/multiomics")
	@ReturnType(clazz = GeneDrugGroupInfos.class)
	@Override
	public Response listFromComputationIdWithMultiOmicsAnalysisFiles(
		@QueryParam("computationId") String computationId,
		MultiOmicsAnalysisInputData multiOmicsAnalysisInputData,
		@QueryParam("cancerDrugStatus") Set<String> cancerDrugStatus,
		@QueryParam("nonCancerDrugStatus") Set<String> nonCancerDrugStatus,
		@QueryParam("cancer") Set<String> cancerTypes,
		@QueryParam("directTarget") boolean directTarget,
		@QueryParam("biomarker") boolean biomarker,
		@QueryParam("pathwayMember") boolean pathwayMember,
		@QueryParam("geneDependency") boolean geneDependency
	) throws BadRequestException {
		try {
			requireNonNull(computationId, "A computation Id must be provided");

			CnvData cnvData = null;
			if (multiOmicsAnalysisInputData.getCnvData() != null) {
				cnvData = multiOmicsAnalysisInputData.getCnvData();
				requireNonEmpty(cnvData.getDataMap().keySet(), "At least one gene must be provided");
			}

			GeneExpressionData geneExpressionData = null;
			if(multiOmicsAnalysisInputData.getGeneExpressionData() != null) {
				geneExpressionData = multiOmicsAnalysisInputData.getGeneExpressionData();
				requireNonEmpty(geneExpressionData.getGeneExpression().keySet(), "At least one gene must be provided");
			}

			if(cnvData ==  null && geneExpressionData == null) {
				createBadRequestException("Both CNV and expression data can't be empty at the same time, at least one is required");
			}

			GeneDrugQueryParameters queryParameters = new GeneDrugQueryParameters(
				cancerDrugStatus, nonCancerDrugStatus, cancerTypes, directTarget, biomarker, pathwayMember, geneDependency);

			final List<GeneDrugGroup> geneDrugs;
			if (cnvData == null && geneExpressionData != null) {
				geneDrugs = controller.searchFromComputationIdWithExpression(queryParameters, computationId,
					new GeneExpression(geneExpressionData.getGeneExpression()));
			} else if (cnvData != null && geneExpressionData == null) {
				geneDrugs = controller.searchFromComputationIdWithCnv(queryParameters, computationId, cnvData);
			} else {
				geneDrugs = controller.searchFromComputationIdWithCnvAndExpression(queryParameters, computationId,
					cnvData, new GeneExpression(geneExpressionData.getGeneExpression()));
			}

			return Response.ok(new GeneDrugGroupInfos(geneDrugs)).build();
		} catch (IllegalArgumentException | NullPointerException e) {
			LOG.warn("Error listing gene-drugs from computation id with multi-omics analysis files", e);
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
	@ReturnType(clazz = DrugNames[].class)
	@Override
	public Response listDrugNames(
		@QueryParam("query") @DefaultValue("") String query,
		@QueryParam("maxResults") @DefaultValue("-1") int maxResults
	) {
		try {
			requireNonNull(query, "query can't be null");
	
			final Drug[] drugs = controller.listDrugs(query, maxResults);
			
			return Response.ok(DrugNames.of(drugs)).build();
		} catch (NullPointerException e) {
			LOG.warn("Error retrieving standard drug names", e);
			throw createBadRequestException(e);
		}
	}

	@GET
	@Path("/gene/presence")
	@ReturnType(clazz = GenePresence.class)
	@Override
	public Response checkPresenceByGet(@QueryParam("gene") Set<String> geneSymbols) {
		return this.checkPresence(geneSymbols);
	}

	@POST
	@Path("/gene/presence")
	@ReturnType(clazz = GenePresence.class)
	@Consumes(APPLICATION_FORM_URLENCODED)
	@Override
	public Response checkPresenceByPost(@FormParam("gene") Set<String> geneSymbols) {
		return this.checkPresence(geneSymbols);
	}

	private Response checkPresence(Set<String> geneSymbols) {
		try {
			requireNonEmpty(geneSymbols, "At least one gene must be provided");
			
			final String[] gsArray = geneSymbols.stream().toArray(String[]::new);
			final Map<String, Boolean> presence = this.controller.checkGenePresence(gsArray);
			
			return Response.ok(new GenePresence(presence)).build();
		} catch (NullPointerException | IllegalArgumentException e) {
			LOG.warn("Error checking gene presence", e);
			throw createBadRequestException(e);
		}
	}
}
