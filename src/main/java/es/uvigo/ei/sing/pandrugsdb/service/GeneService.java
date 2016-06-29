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

import java.util.List;

import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneInteraction;

public interface GeneService {
	/**
	 * Returns the complete list of gene symbols stored in the database ordered
	 * in ascending alphabetical order.
	 * 
	 * @param query a string with which the gene symbol must start. Use empty
	 * value or ignore it for no filtering.
	 * @param maxResults the maximum number of results to return. Use negative
	 * numbers or ignore it for no limit.
	 * @return the complete list of gene symbols stored in the database ordered
	 * in ascending alphabetical order.
	 */
	public abstract String[] listGeneSymbols(String query, int maxResults);
	
	/**
	 * Returns a list of genes that interact with a provided gene with a
	 * certain degree of distance.
	 * 
	 * @param geneSymbol the gene symbol of the reference gene.
	 * @param degree the maximum degree of distance.
	 * @return a list of gene interactions including the provided gene.
	 */
	public abstract GeneInteraction[] getGeneInteractions(String geneSymbol, int degree);

	/**
	 * Returns a list of genes that interact with a provided list of genes
	 * with a certain degree of distance.
	 * 
	 * @param geneSymbol the gene symbols of the reference genes.
	 * @param degree the maximum degree of distance.
	 * @return a list of gene interactions including the provided genes.
	 */
	public abstract GeneInteraction[] getGenesInteractions(List<String> geneSymbol, int degree);
}
