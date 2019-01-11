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

import java.util.Collection;
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

import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDrugDAO;
import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDrugWarningDAO;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugWarning;
import es.uvigo.ei.sing.pandrugs.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugs.service.drugscore.ByGeneDrugDrugScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.drugscore.ByGroupDrugScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.drugscore.DrugScoreCalculator;
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
			new DefaultGeneScoreCalculator()
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
			new StaticGeneScoreCalculator(normalizeGeneRank(geneRank))
		);
	}

	@Override
	public List<GeneDrugGroup> searchFromComputationId(
			GeneDrugQueryParameters queryParameters, String computationId
	) {
		requireNonNull(queryParameters);

		final Map<String, Double> geneRank = requireNonEmpty(
			variantsAnalysisController.getGeneRankingForComputation(computationId).asMap()
		);

		return searchForGeneDrugsWithGenes(
			queryParameters,
			new HashSet<>(geneRank.keySet()),
			new StaticGeneScoreCalculator(geneRank)
		);
	}

	private List<GeneDrugGroup> searchForGeneDrugsWithGenes(
		GeneDrugQueryParameters queryParameters,
		Set<String> geneNames,
		GeneScoreCalculator gScoreCalculator
	) {
		final String[] upperGeneNames = toUpperCase(geneNames);
		
		return searchForGeneDrugs(
			this.dao.searchByGene(queryParameters, upperGeneNames),
			gdg -> filterGenesInGeneDrugs(upperGeneNames, gdg, queryParameters).toArray(String[]::new),
			gScoreCalculator,
			new ByGroupDrugScoreCalculator()
		);
	}

	private List<GeneDrugGroup> searchForGeneDrugsWithDrugs(
		GeneDrugQueryParameters queryParameters,
		Set<String> drugNames,
		GeneScoreCalculator gScoreCalculator
	) {
		final Function<Set<GeneDrug>, String[]> groupToGenes = group -> group.stream()
			.map(GeneDrug::getGeneSymbol)
		.toArray(String[]::new);
		
		return searchForGeneDrugs(
			this.dao.searchByDrug(queryParameters, toUpperCase(drugNames)),
			gdg -> filterGenesInGeneDrugs(groupToGenes.apply(gdg), gdg, queryParameters).toArray(String[]::new),
			gScoreCalculator,
			new ByGeneDrugDrugScoreCalculator()
		);
	}

	private List<GeneDrugGroup> searchForGeneDrugs(
		Collection<GeneDrug> geneDrugs,
		Function<Set<GeneDrug>, String[]> geneDrugToGenes,
		GeneScoreCalculator gScoreCalculator,
		DrugScoreCalculator drugScoreCalculator
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
					
					final Map<GeneDrug, Set<GeneDrugWarning>> gdgWarnings = warnings.entrySet().stream()
						.filter(entry -> gdg.contains(entry.getKey()))
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
					
					return new GeneDrugGroup(
						queryGenes,
						gdg,
						gdgWarnings,
						gScoreCalculator,
						drugScoreCalculator
					);
				})
			.collect(toList());
		}
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
		final Predicate<String> relationFilter = createRelationFilter(geneDrugs, queryParameters);
		
		return stream(geneNames).filter(relationFilter);
	}

	private static Predicate<String> createRelationFilter(
		Collection<GeneDrug> geneDrugs,
		GeneDrugQueryParameters queryParameters
	) {
		final Function<GeneDrug, List<String>> getGenes;
		
		final boolean isDirect = queryParameters.areDirectIncluded();
		final boolean isIndirect = queryParameters.areIndirectIncluded();
		
		if (isDirect != isIndirect) {
			if (isDirect) {
				getGenes = gd -> asList(gd.getGeneSymbol());
			} else {
				getGenes = GeneDrug::getIndirectGeneSymbols;
			}
		} else {
			getGenes = gd -> gd.isTarget() ?
				gd.getDirectAndIndirectGeneSymbols() :
				asList(gd.getGeneSymbol());
		}
		
		final Set<String> geneDrugNames = geneDrugs.stream()
			.map(getGenes)
			.flatMap(List::stream)
		.collect(toSet());
		
		return geneDrugNames::contains;
	}
}
