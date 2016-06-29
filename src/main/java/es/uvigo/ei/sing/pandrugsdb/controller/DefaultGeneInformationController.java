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
package es.uvigo.ei.sing.pandrugsdb.controller;

import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonEmpty;
import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonNegative;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.persistence.dao.GeneInformationDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformation;

@Controller
@Transactional
@Lazy
public class DefaultGeneInformationController implements GeneInformationController {
	@Inject
	private GeneInformationDAO dao;

	@Override
	public Set<GeneInformation> interactions(int degree, String ... geneSymbol) {
		requireNonEmpty(geneSymbol);
		requireNonNegative(degree);
		
		final Set<GeneInformation> genes = Arrays.stream(geneSymbol)
			.map(dao::get)
		.collect(toSet());
		
		requireNonEmpty(genes);
		
		for (int i = 0; i < degree; i++) {
			genes.addAll(genes.stream()
				.map(GeneInformation::getInteractingGenes)
				.flatMap(Set::stream)
			.collect(toSet()));
		}
		
		return genes;
	}
	
}
