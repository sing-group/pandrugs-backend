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
import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonEmpty;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import es.uvigo.ei.sing.pandrugsdb.controller.GeneDrugController;
import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.query.GeneQueryParameters;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;

/**
 * Service to query the gene drugs lists.
 * 
 * @author Miguel Reboiro-Jato
 */
@Path("genedrug")
@Service
@Transactional
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class DefaultGeneDrugService implements GeneDrugService {
	@Inject
	private GeneDrugController controller;

	@GET
	@Consumes(MediaType.WILDCARD)
	@Override
	public GeneDrugGroupInfos list(
		@QueryParam("gene") List<String> genes,
		@QueryParam("cancerDrugStatus") List<String> cancerDrugStatus,
		@QueryParam("nonCancerDrugStatus") List<String> nonCancerDrugStatus,
		@QueryParam("target") String target,
		@QueryParam("direct") String direct
	) throws BadRequestException, InternalServerErrorException {
		try {
			requireNonEmpty(genes, "At least one gene must be provided");
			
			final List<GeneDrugGroup> geneDrugs = controller.searchForGeneDrugs(
				new GeneQueryParameters(
					cancerDrugStatus, nonCancerDrugStatus, target, direct
				),
				genes.toArray(new String[genes.size()])
			);
			
			return new GeneDrugGroupInfos(geneDrugs);
		} catch (IllegalArgumentException | NullPointerException iae) {
			throw createBadRequestException(iae.getMessage());
		}
	}
}
