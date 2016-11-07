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
package es.uvigo.ei.sing.pandrugsdb.controller;

import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonEmpty;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneDrugDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.query.DirectIndirectStatus;
import es.uvigo.ei.sing.pandrugsdb.query.GeneQueryParameters;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugsdb.service.genescore.DefaultGeneScoreCalculator;
import es.uvigo.ei.sing.pandrugsdb.service.genescore.GeneScoreCalculator;
import es.uvigo.ei.sing.pandrugsdb.service.genescore.StaticGeneScoreCalculator;

@Controller
@Transactional
@Lazy
public class DefaultGeneDrugController implements GeneDrugController {
	@Inject
	private GeneDrugDAO dao;
	
	@Override
	public List<GeneDrugGroup> searchForGeneDrugs(
		GeneQueryParameters queryParameters, String ... geneNames
	) {
		requireNonNull(queryParameters);
		requireNonEmpty(geneNames);
		
		return searchForGeneDrugs(
			queryParameters,
			new LinkedHashSet<>(asList(geneNames)),
			new DefaultGeneScoreCalculator()
		);
	}

	@Override
	public List<GeneDrugGroup> searchForGeneDrugs(
		GeneQueryParameters queryParameters,
		GeneRanking geneRanking
	) {
		requireNonNull(queryParameters);
		requireNonNull(geneRanking);
		
		final Map<String, Double> geneRank = requireNonEmpty(geneRanking.asMap());
		
		return searchForGeneDrugs(
			queryParameters,
			new LinkedHashSet<>(geneRank.keySet()),
			new StaticGeneScoreCalculator(normalizeGeneRank(geneRank))
		);
	}
	
	private List<GeneDrugGroup> searchForGeneDrugs(
		GeneQueryParameters queryParameters,
		Set<String> geneNames,
		GeneScoreCalculator gScoreCalculator
	) {
		final String[] upperGeneNames = geneNames.stream()
			.map(String::toUpperCase)
		.toArray(String[]::new);
		
		final List<GeneDrug> geneDrugs = this.dao.searchByGene(
			queryParameters, upperGeneNames
		);
		
		final Collection<Set<GeneDrug>> groups = geneDrugs.stream()
			.collect(groupingBy(GeneDrug::getStandardDrugName, toSet()))
		.values();
		
		return groups.stream()
			.map(gdg -> new GeneDrugGroup(
				filterGenesInGeneDrugs(upperGeneNames, gdg, queryParameters.getDirectIndirect()),
				gdg,
				gScoreCalculator
			))
		.collect(toList());
	}
	
	private final static Map<String, Double> normalizeGeneRank(
		Map<String, Double> unnormalizedGeneRank
	) {
		final double min = unnormalizedGeneRank.values().stream()
			.reduce(Double.MAX_VALUE, Math::min)
		.doubleValue();
		final double max = unnormalizedGeneRank.values().stream()
			.reduce(Double.MIN_VALUE, Math::max)
		.doubleValue();
		final double diff = max - min;
		
		return unnormalizedGeneRank.entrySet().stream()
			.collect(toMap(
				Entry::getKey,
				diff == 0d
					? e -> 1d
					: e -> (e.getValue() - min) / diff
			));
	}
	
	private final static String[] filterGenesInGeneDrugs(
		String[] geneNames,
		Collection<GeneDrug> geneDrugs,
		DirectIndirectStatus directIndirectStatus
	) {
		final Function<GeneDrug, List<String>> getGenes;
		
		switch(directIndirectStatus) {
		case DIRECT:
			getGenes = gd -> asList(gd.getGeneSymbol());
			break;
		case INDIRECT:
			getGenes = GeneDrug::getIndirectGeneSymbols;
			break;
		default:
			getGenes = gd -> gd.isTarget() ?
				gd.getDirectAndIndirectGenes() :
				asList(gd.getGeneSymbol());
			break;
		}
		
		final Set<String> geneDrugNames = geneDrugs.stream()
			.map(getGenes)
			.flatMap(List::stream)
		.collect(toSet());
		
		return Stream.of(geneNames)
			.filter(geneDrugNames::contains)
		.toArray(String[]::new);
	}
}
