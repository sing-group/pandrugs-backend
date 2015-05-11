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

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneDrugDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;

@Controller
@Transactional
@Lazy
public class DefaultGeneDrugController implements GeneDrugController {
	@Inject
	private GeneDrugDAO dao;

	@Override
	public List<GeneDrugGroup> searchForGeneDrugs(String ... geneNames) {
		final List<GeneDrug> geneDrugs = this.dao.searchWithIndirects(geneNames).stream()
			.filter(gd -> gd.getStatus().isActive())
		.collect(toList());

		final List<Set<GeneDrug>> groups = 
			groupBy(geneDrugs, GeneDrug::getStandardDrugName)
		.collect(toList());
		
		return groups.stream()
			.map(gdg -> new GeneDrugGroup(
				filterGenesInGeneDrugs(geneNames, gdg), new ArrayList<>(gdg)
			))
		.collect(toList());
	}
	
	private final static String[] filterGenesInGeneDrugs(
		String[] geneNames, Collection<GeneDrug> geneDrugs
	) {
		final Set<String> geneDrugNames = geneDrugs.stream()
			.map(GeneDrug::getDirectAndIndirectGenes)
			.flatMap(List::stream)
		.collect(Collectors.toSet());
		
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
