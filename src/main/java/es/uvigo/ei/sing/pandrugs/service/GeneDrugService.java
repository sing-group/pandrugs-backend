/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2019 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;

/**
 * Service to query the gene drugs lists.
 * 
 * @author Miguel Reboiro-Jato
 */
public interface GeneDrugService {
	/**
	 * Returns a list of genes-drug interactions with the provided gene
	 * symbol/s or standard drug name/s.
	 * 
	 * @param genes a list of gene symbol names to search in the database.
	 * The use of this and the {@code drugs} parameter is mutually exclusive,
	 * but one of both parameters is required (you can query by genes or by
	 * drugs, but not by both at the same time).
	 * @param drugs a list of standard drug names to search in the database.
	 * The use of this and the {@code genes} parameter is mutually exclusive,
	 * but one of both parameters is required (you can query by genes or by
	 * drugs, but not by both at the same time).
	 * @param cancerDrugStatus a list for filtering the drug status of the 
	 * cancer genes. Multiple values allowed. Valid values are CLINICAL,
	 * APPROVED, EXPERIMENTAL, WITHDRAWN and UNDEFINED. <br>
	 * In addition, NONE value can be used for no cancer results but, when
	 * used, it must be the unique value for this parameter. NONE status 
	 * can't be used, at the same time, for cancerDrugStatus and 
	 * nonCancerDrugStatus parameters.<br>
	 * Default value is: CLINICAL and APPROVED.
	 * @param nonCancerDrugStatus a list for filtering the drug status of the 
	 * non cancer genes. Multiple values allowed. Valid values are CLINICAL,
	 * APPROVED, EXPERIMENTAL, WITHDRAWN and UNDEFINED.<br>
	 * In addition, NONE value can be used for only cancer results but, when 
	 * used, it must be the unique value for this parameter. NONE status 
	 * can't be used, at the same time for, cancerDrugStatus and
	 * nonCancerDrugStatus parameters.<br>
	 * Default value is: CLINICAL, APPROVED and EXPERIMENTAL.
	 * @param cancerTypes list of cancer types of interest. Results will be
	 * filtered using this cancer types, including only drugs related to these
	 * cancer types. If this parameter is not provided, then all cancer types
	 * will be included.
	 * @param directTarget whether gene-drugs where the gene is a direct gene
	 * and the target of the drug should be returned.
	 * @param biomarker whether gene-drugs where the gene is a direct gene
	 * and a marker of the drug should be returned.
	 * @param pathwayMember whether gene-drugs where the gene is an indirect
	 * gene and the target of the drug should be returned.
	 * @return a list of gene drugs that match the provided genes symbol.
	 * @throws BadRequestException if not gene symbol is provided.
	 */
	public abstract Response list(
		Set<String> genes,
		Set<String> drugs,
		Set<String> cancerDrugStatus,
		Set<String> nonCancerDrugStatus,
		Set<String> cancerTypes,
		boolean directTarget,
		boolean biomarker,
		boolean pathwayMember
	) throws BadRequestException;

	/**
	 * Returns a list of genes-drug interactions with the affected genes
	 * previously computed from a VCF.
	 *
	 * @param computationId a finished computation id where the affected genes
	 *                      can be extracted.
	 * @param cancerDrugStatus a list for filtering the drug status of the
	 * cancer genes. Multiple values allowed. Valid values are CLINICAL,
	 * APPROVED, EXPERIMENTAL, WITHDRAWN and UNDEFINED. <br>
	 * In addition, NONE value can be used for no cancer results but, when
	 * used, it must be the unique value for this parameter. NONE status
	 * can't be used, at the same time, for cancerDrugStatus and
	 * nonCancerDrugStatus parameters.<br>
	 * Default value is: CLINICAL and APPROVED.
	 * @param nonCancerDrugStatus a list for filtering the drug status of the
	 * non cancer genes. Multiple values allowed. Valid values are CLINICAL,
	 * APPROVED, EXPERIMENTAL, WITHDRAWN and UNDEFINED.<br>
	 * In addition, NONE value can be used for only cancer results but, when
	 * used, it must be the unique value for this parameter. NONE status
	 * can't be used, at the same time for, cancerDrugStatus and
	 * nonCancerDrugStatus parameters.<br>
	 * Default value is: CLINICAL, APPROVED and EXPERIMENTAL.
	 * @param cancerTypes list of cancer types of interest. Results will be
	 * filtered using this cancer types, including only drugs related to these
	 * cancer types. If this parameter is not provided, then all cancer types
	 * will be included.
	 * @param directTarget whether gene-drugs where the gene is a direct gene
	 * and the target of the drug should be returned.
	 * @param biomarker whether gene-drugs where the gene is a direct gene
	 * and a marker of the drug should be returned.
	 * @param pathwayMember whether gene-drugs where the gene is an indirect
	 * gene and the target of the drug should be returned.
	 * @return a list of gene drugs that match the provided genes symbol.
	 * @throws BadRequestException if not gene symbol is provided.
	 */
	public abstract Response listFromComputationId(
		String computationId,
		Set<String> cancerDrugStatus,
		Set<String> nonCancerDrugStatus,
		Set<String> cancerTypes,
		boolean directTarget,
		boolean biomarker,
		boolean pathwayMember
	) throws BadRequestException;

	/**
	 * Returns a list of genes-drug interactions with the provided gene
	 * symbol/s. This operation takes as input a ranked list of genes, that is
	 * used to calculate the GScore of each gene. Instead of using the
	 * precalculated GScore, the rank values are normalized and used as the
	 * GScore of each gene.
	 * 
	 * @param genesRanking a gene ranking with a ranked list of genes. The rank
	 * value can be a decimal value.
	 * @param cancerDrugStatus a list for filtering the drug status of the 
	 * cancer genes. Multiple values allowed. Valid values are CLINICAL,
	 * APPROVED, EXPERIMENTAL, WITHDRAWN and UNDEFINED. <br>
	 * In addition, NONE value can be used for no cancer results but, when
	 * used, it must be the unique value for this parameter. NONE status 
	 * can't be used, at the same time, for cancerDrugStatus and 
	 * nonCancerDrugStatus parameters.<br>
	 * Default value is: CLINICAL and APPROVED.
	 * @param nonCancerDrugStatus a list for filtering the drug status of the 
	 * non cancer genes. Multiple values allowed. Valid values are CLINICAL,
	 * APPROVED, EXPERIMENTAL, WITHDRAWN and UNDEFINED.<br>
	 * In addition, NONE value can be used for only cancer results but, when 
	 * used, it must be the unique value for this parameter. NONE status 
	 * can't be used, at the same time for, cancerDrugStatus and
	 * nonCancerDrugStatus parameters.<br>
	 * Default value is: CLINICAL, APPROVED and EXPERIMENTAL.
	 * @param cancerTypes list of cancer types of interest. Results will be
	 * filtered using this cancer types, including only drugs related to these
	 * cancer types. If this parameter is not provided, then all cancer types
	 * will be included.
	 * @param directTarget whether gene-drugs where the gene is a direct gene
	 * and the target of the drug should be returned.
	 * @param biomarker whether gene-drugs where the gene is a direct gene
	 * and a marker of the drug should be returned.
	 * @param pathwayMember whether gene-drugs where the gene is an indirect
	 * gene and the target of the drug should be returned.
	 * @return a list of gene drugs that match the provided genes symbol.
	 * @throws BadRequestException if not gene symbol is provided.
	 */
	public abstract Response listRanked(
		GeneRanking genesRanking,
		Set<String> cancerDrugStatus,
		Set<String> nonCancerDrugStatus,
		Set<String> cancerTypes,
		boolean directTarget,
		boolean biomarker,
		boolean pathwayMember
	) throws BadRequestException;
	
	/**
	 * Returns a list of drug names associated with a drug ordered by ascending
	 * alphabetical order of the show drug name. The drugs included are those
	 * with a CLINICAL, APPROVED or EXPERIMENTAL status.
	 * 
	 * The drug name includes the standard drug name, the show drug name and
	 * the drug name for each data source.
	 * 
	 * @param query a string with which any of the drug names must start. Use
	 * empty value or ignore it for no filtering.
	 * @param maxResults the maximum number of results to return. Use negative
	 * numbers or ignore it for no limit.
	 * @return a list of drug names associated with a drug ordered by ascending
	 * alphabetical order.
	 */
	public abstract Response listDrugNames(String query, int maxResults);

	/**
	 * Returns a list of gene symbols associated with a drug ordered by ascending
	 * alphabetical order. Only genes related to drugs with an CLINICAL,
	 * APPROVED or EXPERIMENTAL status. 
	 * 
	 * @param query a string with which the gene symbol must start. Use empty
	 * value or ignore it for no filtering.
	 * @param maxResults the maximum number of results to return. Use negative
	 * numbers or ignore it for no limit.
	 * @return a list of gene symbols associated with a drug ordered by ascending
	 * alphabetical order.
	 */
	public abstract Response listGeneSymbols(String query, int maxResults);
	
	/**
	 * Checks if a list of gene symbols are present or absent in the gene drug
	 * information stored in the application.
	 * 
	 * The response returned includes a list of present genes and a list of
	 * absent genes.
	 * 
	 * @param geneSymbols the gene symbols of the genes which presence will be
	 * checked.
	 * @return a list of present genes and a list of absent genes.
	 */
	public abstract Response checkPresence(Set<String> geneSymbols);
}
