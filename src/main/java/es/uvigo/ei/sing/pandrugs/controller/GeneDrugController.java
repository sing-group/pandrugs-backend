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

package es.uvigo.ei.sing.pandrugs.controller;

import java.util.List;
import java.util.Map;

import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.controller.entity.GeneExpression;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugs.service.entity.CnvData;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;

public interface GeneDrugController {
	public abstract Map<String, Boolean> checkGenePresence(String ... geneSymbols);
	
	public abstract String[] listGeneSymbols(String query, int maxResults);

	public abstract Drug[] listDrugs(String query, int maxResults);
	
	public abstract List<GeneDrugGroup> searchByGenes(
		GeneDrugQueryParameters queryParameters, String ... geneNames
	);
	
	public abstract List<GeneDrugGroup> searchByDrugs(
		GeneDrugQueryParameters queryParameters, String ... standardDrugNames
	);
	
	public abstract List<GeneDrugGroup> searchByRanking(
		GeneDrugQueryParameters queryParameters, GeneRanking geneRanking
	);

	public abstract List<GeneDrugGroup> searchByCnv(
		GeneDrugQueryParameters queryParameters, CnvData cnvData
	);

	public abstract List<GeneDrugGroup> searchByCnvWithExpression(
		GeneDrugQueryParameters queryParameters, CnvData cnvData, GeneExpression geneExpression
	);

	public abstract List<GeneDrugGroup> searchFromComputationId(
		GeneDrugQueryParameters queryParameters, String computationId
	);

	public abstract List<GeneDrugGroup> searchFromComputationIdWithCnv(
		GeneDrugQueryParameters queryParameters, String computationId, CnvData cnvData
	);

	public abstract List<GeneDrugGroup> searchFromComputationIdWithExpression(
		GeneDrugQueryParameters queryParameters, String computationId, GeneExpression geneExpression
	);

	public abstract List<GeneDrugGroup> searchFromComputationIdWithCnvAndExpression(
		GeneDrugQueryParameters queryParameters, String computationId, CnvData cnvData, GeneExpression geneExpression
	);
}
