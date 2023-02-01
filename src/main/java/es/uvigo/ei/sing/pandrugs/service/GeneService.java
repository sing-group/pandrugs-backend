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

import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

/**
 * Service to get the gene to gene interactions.
 * 
 * @author Miguel Reboiro-Jato
 */
public interface GeneService {
	/**
	 * Returns a list of genes that interact with a provided gene with a
	 * certain degree of distance.
	 * 
	 * @param geneSymbol the gene symbol of the reference gene. This parameter
	 * is required and can't be empty.
	 * @param degree the maximum degree of distance. Default values is 0.
	 * @return a list of gene interactions including the provided gene.
	 * @throws BadRequestException if the {@code geneSymbol} parameter is
	 * missing or empty, or if the {@code degree} parameter is negative.
	 */
	public Response getGeneInteractions(String geneSymbol, int degree)
	throws BadRequestException;

	/**
	 * Returns a list of genes that interact with a provided list of genes
	 * with a certain degree of distance.
	 * 
	 * @param geneSymbol the gene symbols of the reference genes. At least, one
	 * gene symbol must be provided and no empty values are allowed.
	 * @param degree the maximum degree of distance. Default values is 0.
	 * @return a list of gene interactions including the provided genes.
	 * @throws BadRequestException if no gene symbols are provided or if any of
	 * the provided gene symbols are empty. Also, if the {@code degree} 
	 * parameter is negative.
	 */
	public Response getGenesInteractions(List<String> geneSymbol, int degree)
	throws BadRequestException;
}
