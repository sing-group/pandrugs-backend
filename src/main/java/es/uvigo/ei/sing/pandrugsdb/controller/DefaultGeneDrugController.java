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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneDrugDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.query.DirectIndirectStatus;
import es.uvigo.ei.sing.pandrugsdb.query.GeneQueryParameters;

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
		final String[] upperGeneNames = Stream.of(geneNames)
			.map(String::toUpperCase)
		.toArray(String[]::new);
		
		final List<GeneDrug> geneDrugs = this.dao.searchByGene(
			queryParameters, upperGeneNames
		);

		final List<Set<GeneDrug>> groups = 
			groupBy(geneDrugs, GeneDrug::getStandardDrugName)
		.collect(toList());
		
		return groups.stream()
			.map(gdg -> new GeneDrugGroup(
				filterGenesInGeneDrugs(upperGeneNames, gdg, queryParameters.getDirectIndirect()),
				gdg
			))
		.collect(toList());
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
			getGenes = GeneDrug::getIndirectGenes;
			break;
		default:
			getGenes = GeneDrug::getDirectAndIndirectGenes;
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
	
	private final static <T> Stream<Set<GeneDrug>> groupBy(
		Collection<GeneDrug> geneDrugs, Function<GeneDrug, T> criteria
	) {
		final Map<T, Set<GeneDrug>> groups = new HashMap<>();
		
		for (GeneDrug gd : geneDrugs) {
			final T cValue = criteria.apply(gd);
			
			if (!groups.containsKey(cValue))
				groups.put(cValue, new HashSet<>());
			groups.get(cValue).add(gd);
		}
		
		return groups.values().stream();
	}
}
