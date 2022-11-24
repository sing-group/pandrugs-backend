/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2022 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireNonEmpty;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireNonNegative;
import static java.util.Arrays.asList;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qmino.miredot.annotations.ReturnType;

import es.uvigo.ei.sing.pandrugs.controller.GeneController;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneInteraction;
import es.uvigo.ei.sing.pandrugs.util.Checks;

@Path("gene")
@Service
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class DefaultGeneService implements GeneService {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultGeneService.class);
	private final static Comparator<Gene> GENE_COMPARATOR =
		(gi1, gi2) -> gi1.getGeneSymbol().compareTo(gi2.getGeneSymbol());
		
	@Inject
	private GeneController controller;
	
	@GET
	@Path("/{gene}/interactions")
	@ReturnType(clazz = GeneInteraction[].class)
	@Override
	public Response getGeneInteractions(
		@PathParam("gene") String geneSymbol,
		@QueryParam("degree") @DefaultValue("0") int degree
	) throws BadRequestException {
		return this.getGenesInteractions(asList(geneSymbol), degree);
	}
	
	@GET
	@Path("/interactions")
	@ReturnType(clazz = GeneInteraction[].class)
	@Override
	public Response getGenesInteractions(
		@QueryParam("gene") List<String> geneSymbols,
		@QueryParam("degree") @DefaultValue("0") int degree
	) throws BadRequestException {
		try {
			requireNonNegative(degree);
			requireNonEmpty(geneSymbols);
			geneSymbols.forEach(Checks::requireNonEmpty);
			
			final GeneInteraction[] interactions =
				controller.interactions(degree, geneSymbols.stream().toArray(String[]::new)).stream()
					.sorted(GENE_COMPARATOR)
					.map(GeneInteraction::new)
				.toArray(GeneInteraction[]::new);
			
			return Response.ok(interactions).build();
		} catch (NullPointerException |  IllegalArgumentException e) {
			LOG.warn("Error retrieving gene interactions", e);
			throw createBadRequestException(e);
		}
	}
}
