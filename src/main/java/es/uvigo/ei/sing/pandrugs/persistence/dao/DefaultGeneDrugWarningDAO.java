/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

package es.uvigo.ei.sing.pandrugs.persistence.dao;

import static es.uvigo.ei.sing.pandrugs.util.Checks.requireNonEmpty;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugWarning;

@Repository
@Transactional
public class DefaultGeneDrugWarningDAO implements GeneDrugWarningDAO {
	@PersistenceContext
	private EntityManager em;
	
	private DAOHelper<Long, GeneDrugWarning> dh;
	
	DefaultGeneDrugWarningDAO() {}
	
	public DefaultGeneDrugWarningDAO(EntityManager em) {
		this.em = em;
		createDAOHelper();
	}
	
	@PostConstruct
	private void createDAOHelper() {
		this.dh = DAOHelper.of(Long.class, GeneDrugWarning.class, em);
	}

	@Override
	public Map<GeneDrug, Set<GeneDrugWarning>> findForGeneDrugs(Collection<GeneDrug> geneDrugs) {
		requireNonEmpty(geneDrugs, "At least one GeneDrug should be provided");
		
		final CriteriaBuilder cb = dh.cb();
		
		final CriteriaQuery<GeneDrugWarning> query = dh.createCBQuery();
		final Root<GeneDrugWarning> root = query.from(GeneDrugWarning.class);
		
		final Path<String> geneSymbolField = root.get("affectedGene");
		final Path<String> drugField = root.get("standardDrugName");
		
		final Predicate[] geneDrugPredicates = geneDrugs.stream()
			.map(gd -> cb.and(
				cb.equal(geneSymbolField, gd.getGeneSymbol()),
				cb.equal(drugField, gd.getStandardDrugName())
			))
		.toArray(Predicate[]::new);

		final List<GeneDrugWarning> results = em.createQuery(
			query.select(root)
				.where(cb.or(geneDrugPredicates))
		).getResultList();
		
		final Function<GeneDrug, Set<GeneDrugWarningMessage>> getGeneDrugWarnings = geneDrug -> results.stream()
			.filter(warning -> isWarningForGeneDrug(warning, geneDrug))
			.map(warning -> new GeneDrugWarningMessage(geneDrug, warning))
			.collect(toSet());
		
		return geneDrugs.stream()
			.map(getGeneDrugWarnings)
			.flatMap(Set::stream)
		.collect(groupingBy(GeneDrugWarningMessage::getGeneDrug, mapping(GeneDrugWarningMessage::getWarning, toSet())));
			
	}
	
	private final static boolean isWarningForGeneDrug(GeneDrugWarning warning, GeneDrug geneDrug) {
		if (warning.getAffectedGene().equals(geneDrug.getGeneSymbol())
			&& warning.getStandardDrugName().equals(geneDrug.getStandardDrugName())
		) {
			switch (warning.getInteractionType()) {
			case DIRECT_TARGET:
				return geneDrug.isTarget();
			case BIOMARKER:
				return !geneDrug.isTarget();
			case PATHWAY_MEMBER:
				return geneDrug.isTarget() && geneDrug.getIndirectGeneSymbols().contains(warning.getIndirectGene());
			default:
				throw new IllegalStateException("GeneDrugWarning must have a valid interaction type");
			}
		} else {
			return false;
		}
	}
	
	private final static class GeneDrugWarningMessage {
		private final GeneDrug geneDrug;
		private final GeneDrugWarning warning;
		
		public GeneDrugWarningMessage(GeneDrug geneDrug, GeneDrugWarning warningMessage) {
			this.geneDrug = requireNonNull(geneDrug);
			this.warning = requireNonNull(warningMessage);
		}
		
		public GeneDrug getGeneDrug() {
			return geneDrug;
		}
		
		public GeneDrugWarning getWarning() {
			return warning;
		}
	}
}
