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

import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;

public interface GeneDrugService {
	/**
	 * Returns a list of genes-drug interactions with the provided gene
	 * symbol/s.
	 * 
	 * @param genes a list of gene symbol names to search in the database.
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
	 * @param target a target field filter. Valid values are: TARGET, MARKER
	 * and BOTH. Default value is BOTH.
	 * @param direct a filter for direct/indirect genes. Valid values are: 
	 * DIRECT, INDIRECT and BOTH. Default value is BOTH.
	 * @return a list of gene drugs that match the provided genes symbol.
	 * @throws BadRequestException if not gene symbol is provided.
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract GeneDrugGroupInfos list(
		Set<String> genes,
		Set<String> cancerDrugStatus,
		Set<String> nonCancerDrugStatus,
		String target,
		String direct
	) throws BadRequestException, InternalServerErrorException;

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
	 * @param target a target field filter. Valid values are: TARGET, MARKER
	 * and BOTH. Default value is BOTH.
	 * @param direct a filter for direct/indirect genes. Valid values are:
	 * DIRECT, INDIRECT and BOTH. Default value is BOTH.
	 * @return a list of gene drugs that match the provided genes symbol.
	 * @throws BadRequestException if not gene symbol is provided.
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract GeneDrugGroupInfos listFromComputationId(
			Integer computationId,
			Set<String> cancerDrugStatus,
			Set<String> nonCancerDrugStatus,
			String target,
			String direct
	) throws BadRequestException, InternalServerErrorException;

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
	 * @param target a target field filter. Valid values are: TARGET, MARKER
	 * and BOTH. Default value is BOTH.
	 * @param direct a filter for direct/indirect genes. Valid values are: 
	 * DIRECT, INDIRECT and BOTH. Default value is BOTH.
	 * @return a list of gene drugs that match the provided genes symbol.
	 * @throws BadRequestException if not gene symbol is provided.
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract GeneDrugGroupInfos listRanked(
		GeneRanking genesRanking,
		Set<String> cancerDrugStatus,
		Set<String> nonCancerDrugStatus,
		String target,
		String direct
	) throws BadRequestException, InternalServerErrorException;
}