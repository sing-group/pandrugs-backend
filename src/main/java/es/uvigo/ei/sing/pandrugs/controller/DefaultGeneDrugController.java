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

import static es.uvigo.ei.sing.pandrugs.util.Checks.requireNonEmpty;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireNonNullArray;
import static es.uvigo.ei.sing.pandrugs.util.StringFormatter.toUpperCase;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugs.controller.entity.CalculatedGeneAnnotations;
import es.uvigo.ei.sing.pandrugs.controller.entity.CalculatedGeneAnnotations.CalculatedGeneAnnotationType;
import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.controller.entity.GeneExpression;
import es.uvigo.ei.sing.pandrugs.controller.entity.MultiOmicsAnalysisQueryData;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.GermLineAnnotation;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.PharmCatAnnotation;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.PharmCatJsonReportParser;
import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDAO;
import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDrugDAO;
import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDrugWarningDAO;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugWarning;
import es.uvigo.ei.sing.pandrugs.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugs.service.drugscore.ByGeneDrugDrugScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.drugscore.ByGroupDrugScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.drugscore.DrugScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.entity.CnvData;
import es.uvigo.ei.sing.pandrugs.service.entity.ComputationMetadata;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugs.service.genescore.DefaultGeneScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.genescore.GeneScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.genescore.StaticGeneScoreCalculator;

@Controller
@Transactional
@Lazy
public class DefaultGeneDrugController implements GeneDrugController {
	@Inject
	private GeneDrugDAO dao;

	@Inject
	private GeneDrugWarningDAO drugWarningDao;
	
	@Inject
	private VariantsAnalysisController variantsAnalysisController;

	@Inject
	private GeneDAO geneDao;
	
	@Override
	public Map<String, Boolean> checkGenePresence(String ... geneSymbols) {
		requireNonEmpty(geneSymbols);
		requireNonNullArray(geneSymbols);
		
		geneSymbols = toUpperCase(geneSymbols);
		
		return this.dao.checkGenePresence(geneSymbols);
	}

	@Override
	public String[] listGeneSymbols(String query, int maxResults) {
		requireNonNull(query, "query can't be null");
		
		return dao.listGeneSymbols(query, maxResults);
	}

	@Override
	public Drug[] listDrugs(String query, int maxResults) {
		requireNonNull(query, "query can't be null");
		
		return dao.listDrugs(query, maxResults);
	}

	@Override
	public List<GeneDrugGroup> searchByGenes(
		GeneDrugQueryParameters queryParameters, String ... geneNames
	) {
		requireNonNull(queryParameters);
		requireNonEmpty(geneNames);
		
		return searchForGeneDrugsWithGenes(
			queryParameters,
			stream(geneNames).collect(toSet()),
			Collections.emptySet(),
			new DefaultGeneScoreCalculator(),
			new CalculatedGeneAnnotations()
		);
	}

	@Override
	public List<GeneDrugGroup> searchByDrugs(
		GeneDrugQueryParameters queryParameters, String... standardDrugNames
	) {
		requireNonNull(queryParameters);
		requireNonEmpty(standardDrugNames);
		
		return searchForGeneDrugsWithDrugs(
			queryParameters,
			stream(standardDrugNames).collect(toSet()),
			new DefaultGeneScoreCalculator()
		);
	}

	@Override
	public List<GeneDrugGroup> searchByRanking(
		GeneDrugQueryParameters queryParameters,
		GeneRanking geneRanking
	) {
		requireNonNull(queryParameters);
		requireNonNull(geneRanking);
		
		final Map<String, Double> geneRank = requireNonEmpty(geneRanking.asMap());
		
		return searchForGeneDrugsWithGenes(
			queryParameters,
			new HashSet<>(geneRank.keySet()),
			Collections.emptySet(),
			new StaticGeneScoreCalculator(normalizeGeneRank(geneRank)),
			new CalculatedGeneAnnotations()
		);
	}

	@Override
	public List<GeneDrugGroup> searchByCnv(
		GeneDrugQueryParameters queryParameters,
		CnvData cnvData
	) {
		requireNonNull(queryParameters);
		requireNonNull(cnvData);

		final Map<String, String> cnvMap = requireNonEmpty(cnvData.getDataMap());

		CalculatedGeneAnnotations calculatedGeneAnnotations = new CalculatedGeneAnnotations();
		calculatedGeneAnnotations.addAnnotation(CalculatedGeneAnnotationType.CNV, cnvMap);
		
		return searchForGeneDrugsWithGenes(
			queryParameters,
			new HashSet<>(cnvMap.keySet()),
			Collections.emptySet(),
			new DefaultGeneScoreCalculator(),
			calculatedGeneAnnotations
		);
	}

	@Override
	public List<GeneDrugGroup> searchByCnvWithExpression(
		GeneDrugQueryParameters queryParameters, CnvData cnvData, GeneExpression geneExpression
	) {
		requireNonNull(queryParameters);
		requireNonNull(cnvData);
		requireNonNull(geneExpression);

		MultiOmicsAnalysisQueryData multiOmicsQueryData = 
			new MultiOmicsAnalysisQueryData(geneDao, cnvData, geneExpression);

		return searchForGeneDrugsWithGenes(
			queryParameters,
			multiOmicsQueryData.getQueryGenes(),
			Collections.emptySet(),
			new DefaultGeneScoreCalculator(),
			multiOmicsQueryData.getCalculatedGeneAnnotations()
		);
	}

	@Override
	public List<GeneDrugGroup> searchFromComputationId(
		GeneDrugQueryParameters queryParameters, String computationId
	) {
		return this.searchFromComputationIdWithCnvAndExpression(queryParameters, computationId, null, null);
	}

	@Override
	public List<GeneDrugGroup> searchFromComputationIdWithCnv(
		GeneDrugQueryParameters queryParameters, String computationId, CnvData cnvData
	) {
		return this.searchFromComputationIdWithCnvAndExpression(queryParameters, computationId, cnvData, null);
	}

	@Override
	public List<GeneDrugGroup> searchFromComputationIdWithExpression(
		GeneDrugQueryParameters queryParameters, String computationId, GeneExpression geneExpression
	) {
		return this.searchFromComputationIdWithCnvAndExpression(queryParameters, computationId, null, geneExpression);
	}
	
	@Override
	public List<GeneDrugGroup> searchFromComputationIdWithCnvAndExpression(
		GeneDrugQueryParameters queryParameters, String computationId, CnvData cnvData, GeneExpression geneExpression
	) {
		requireNonNull(queryParameters);

		final Map<String, Double> geneRank = requireNonEmpty(
			variantsAnalysisController.getGeneRankingForComputation(computationId).asMap()
		);
		
		ComputationMetadata metadata = this.variantsAnalysisController.getComputationStatus(computationId);

		final Map<String, PharmCatAnnotation> pharmCatAnnotations = new HashMap<>();
		if (metadata.isPharmcat()) {
			pharmCatAnnotations.putAll(this.variantsAnalysisController.getPharmCatAnnotations(computationId));
		}

		MultiOmicsAnalysisQueryData multiOmicsQueryData = 
			new MultiOmicsAnalysisQueryData(geneDao, cnvData, geneExpression, geneRank);

		return searchForGeneDrugsWithGenes(
			queryParameters,
			multiOmicsQueryData.getQueryGenes(),
			Collections.emptySet(),
			new StaticGeneScoreCalculator(geneRank, true),
			pharmCatAnnotations,
			multiOmicsQueryData.getCalculatedGeneAnnotations()
		);
	}

	private List<GeneDrugGroup> searchForGeneDrugsWithGenes(
		GeneDrugQueryParameters queryParameters,
		Set<String> geneNames,
		Set<String> geneNamesExcludedAsIndirect,
		GeneScoreCalculator gScoreCalculator,
		CalculatedGeneAnnotations calculatedGeneAnnotations
	) {
		return searchForGeneDrugsWithGenes(
			queryParameters, 
			geneNames,
			geneNamesExcludedAsIndirect,
			gScoreCalculator, 
			Collections.emptyMap(),
			calculatedGeneAnnotations
		);
	}

	private List<GeneDrugGroup> searchForGeneDrugsWithGenes(
		GeneDrugQueryParameters queryParameters,
		Set<String> geneNames,
		Set<String> geneNamesExcludedAsIndirect,
		GeneScoreCalculator gScoreCalculator,
		Map<String, PharmCatAnnotation> pharmCatAnnotations,
		CalculatedGeneAnnotations calculatedGeneAnnotations
	) {
		final String[] upperGeneNames = toUpperCase(geneNames);
		
		return groupGeneDrugs(
			queryParameters,
			this.dao.searchByGene(queryParameters, upperGeneNames, geneNamesExcludedAsIndirect.stream().toArray(String[]::new)),
			gdg -> filterGenesInGeneDrugs(upperGeneNames, gdg, queryParameters, geneNamesExcludedAsIndirect).toArray(String[]::new),
			gScoreCalculator,
			new ByGroupDrugScoreCalculator(),
			pharmCatAnnotations, calculatedGeneAnnotations
		);
	}

	private List<GeneDrugGroup> searchForGeneDrugsWithDrugs(
		GeneDrugQueryParameters queryParameters,
		Set<String> drugNames,
		GeneScoreCalculator gScoreCalculator
	) {
		return searchForGeneDrugsWithDrugs(queryParameters, drugNames, gScoreCalculator, Collections.emptyMap());
	}

	private List<GeneDrugGroup> searchForGeneDrugsWithDrugs(
		GeneDrugQueryParameters queryParameters,
		Set<String> drugNames,
		GeneScoreCalculator gScoreCalculator,
		Map<String, PharmCatAnnotation> pharmCatAnnotations
	) {
		final Function<Set<GeneDrug>, String[]> groupToGenes = group -> group.stream()
			.map(GeneDrug::getGeneSymbol)
		.toArray(String[]::new);
		
		return groupGeneDrugs(
			queryParameters,
			this.dao.searchByDrug(queryParameters, toUpperCase(drugNames)),
			gdg -> filterGenesInGeneDrugs(groupToGenes.apply(gdg), gdg, queryParameters).toArray(String[]::new),
			gScoreCalculator,
			new ByGeneDrugDrugScoreCalculator(),
			pharmCatAnnotations, new CalculatedGeneAnnotations()
		);
	}

	private List<GeneDrugGroup> groupGeneDrugs(
		GeneDrugQueryParameters queryParameters,
		Collection<GeneDrug> geneDrugs,
		Function<Set<GeneDrug>, String[]> geneDrugToGenes,
		GeneScoreCalculator gScoreCalculator,
		DrugScoreCalculator drugScoreCalculator,
		Map<String, PharmCatAnnotation> pharmCatAnnotations, CalculatedGeneAnnotations calculatedGeneAnnotations
	) {
		if (geneDrugs.isEmpty()) {
			return emptyList();
		} else {
			final Collection<Set<GeneDrug>> groups = geneDrugs.stream()
				.collect(groupingBy(GeneDrug::getStandardDrugName, toSet()))
			.values();
			
			final Map<GeneDrug, Set<GeneDrugWarning>> warnings = this.drugWarningDao.findForGeneDrugs(geneDrugs);
				
			return groups.stream()
				.map(gdg -> {
					final String[] queryGenes = geneDrugToGenes.apply(gdg);
					final CalculatedGeneAnnotations annotations = calculatedGeneAnnotations.filterByGenes(asList(queryGenes));
					final Map<GeneDrug, Set<GeneDrugWarning>> gdgWarnings = warnings.entrySet().stream()
						.filter(entry -> gdg.contains(entry.getKey()))
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

					return new GeneDrugGroup(
						queryGenes,
						gdg,
						queryParameters.isPathwayMember(),
						queryParameters.isGeneDependency(),
						gdgWarnings,
						gScoreCalculator,
						drugScoreCalculator,
						getPharmCatAnnotation(pharmCatAnnotations, gdg),
						annotations
					);
				})
			.collect(toList());
		}
	}

	private static GermLineAnnotation getPharmCatAnnotation(Map<String, PharmCatAnnotation> pharmCatAnnotations, Set<GeneDrug> gdg) { 
		List<GeneDrug> geneDrugsList = new ArrayList<>(gdg);
		String showDrugName = geneDrugsList.get(0).getDrug().getShowName().toLowerCase();
		String pharmCatDrugName = PharmCatJsonReportParser.toPharmCatDrugName(showDrugName);
		GermLineAnnotation pharmCatAnnotation = GermLineAnnotation.NOT_AVAILABLE;
		if(pharmCatAnnotations.containsKey(pharmCatDrugName)) {
			pharmCatAnnotation = pharmCatAnnotations.get(pharmCatDrugName).getGermLineAnnotation();
		}

		return pharmCatAnnotation;
	}
	
	private final static Map<String, Double> normalizeGeneRank(
		Map<String, Double> unnormalizedGeneRank
	) {
		final double min = unnormalizedGeneRank.values().stream()
			.mapToDouble(Double::valueOf)
		.min().orElse(Double.NaN);
		
		final double max = unnormalizedGeneRank.values().stream()
			.mapToDouble(Double::valueOf)
		.max().orElse(Double.NaN);
		
		final double diff = max - min;
		
		return unnormalizedGeneRank.entrySet().stream()
			.collect(toMap(
				entry -> entry.getKey().toUpperCase(),
				diff == 0d
					? e -> 1d
					: e -> (e.getValue() - min) / diff
			));
	}
	
	private final static Stream<String> filterGenesInGeneDrugs(
		String[] geneNames,
		Collection<GeneDrug> geneDrugs,
		GeneDrugQueryParameters queryParameters
	) {
		return filterGenesInGeneDrugs(geneNames, geneDrugs, queryParameters, null);
	}
	
	private final static Stream<String> filterGenesInGeneDrugs(
		String[] geneNames,
		Collection<GeneDrug> geneDrugs,
		GeneDrugQueryParameters queryParameters,
		Set<String> geneNamesExcludedAsIndirect
	) {
		Predicate<String> relationFilter = createRelationFilter(geneDrugs, queryParameters, geneNamesExcludedAsIndirect);
		
		return stream(geneNames).filter(relationFilter);
	}

	private static Predicate<String> createRelationFilter(
		Collection<GeneDrug> geneDrugs,
		GeneDrugQueryParameters queryParameters,
		Set<String> geneNamesExcludedAsIndirect
	) {
		final Predicate<String> isNotExcludedAsIndirect = geneSymbol ->
			geneNamesExcludedAsIndirect == null || !geneNamesExcludedAsIndirect.contains(geneSymbol);
		
		final Set<String> geneSymbols = new HashSet<>();
		
		if (queryParameters.areDirectIncluded()) {
			geneDrugs.stream()
				.map(GeneDrug::getGeneSymbol)
			.forEach(geneSymbols::add);
		}
		
		if (queryParameters.isPathwayMember()) {
			geneDrugs.stream()
				.filter(GeneDrug::isTarget)
				.map(GeneDrug::getPathwayMemberGeneSymbols)
				.flatMap(List::stream)
				.filter(isNotExcludedAsIndirect)
			.forEach(geneSymbols::add);
		}
		
		if (queryParameters.isGeneDependency()) {
			geneDrugs.stream()
				.filter(GeneDrug::isTarget)
				.map(GeneDrug::getGeneDependenciesGeneSymbols)
				.flatMap(List::stream)
				.filter(isNotExcludedAsIndirect)
			.forEach(geneSymbols::add);
		}
		
		return geneSymbols::contains;
	}
}
