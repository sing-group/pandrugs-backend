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
package es.uvigo.ei.sing.pandrugsdb.persistence.dao;

import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.query.GeneQueryParameters;
import es.uvigo.ei.sing.pandrugsdb.query.TargetMarkerStatus;

@Repository
@Transactional
public class DefaultGeneDrugDAO
extends DAO<Integer, GeneDrug>
implements GeneDrugDAO {
	@Override
	public List<GeneDrug> searchByGene(GeneQueryParameters queryParameters, String ... geneNames) {
		requireNonNull(queryParameters, "Query parameters can't be null");
		requireNonEmpty(geneNames, "At least one gene name must be provided");
		
		final CriteriaQuery<GeneDrug> query = createCBQuery();
		final Root<GeneDrug> root = query.from(getEntityType());
		
		final Predicate predicate = cb().and(
			Stream.of(
				createDirectIndirectPredicate(root, queryParameters, geneNames),
				createDrugStatusPredicate(root, queryParameters),
				createTargetMarkerPredicate(root, queryParameters)
			)
				.filter(Objects::nonNull)
			.toArray(Predicate[]::new)	
		);
		
		return em.createQuery(
			query.select(root).where(predicate)
		).getResultList();
	}
	
	private Predicate createTargetMarkerPredicate(
		Root<GeneDrug> root,
		GeneQueryParameters queryParameters
	) {
		if (queryParameters.getTargetMarker() == TargetMarkerStatus.BOTH) {
			return null;
		} else {
			final Expression<Boolean> targetField = root.get("target");
			
			if (queryParameters.getTargetMarker() == TargetMarkerStatus.TARGET) {
				return cb().isTrue(targetField);
			} else {
				return cb().isFalse(targetField);
			}
		}
	}
	
	private Predicate createDrugStatusPredicate(
		Root<GeneDrug> root,
		GeneQueryParameters queryParameters
	) {
		final CriteriaBuilder cb = cb();
		
		final Expression<String> cancerField = root.get("cancer");
		final Expression<DrugStatus> statusField = root.get("status");

		final List<Predicate> predicates = new LinkedList<>();
		
		if (!queryParameters.isAnyCancerDrugStatus()) {
			predicates.add(cb.and(
				cb.isNotNull(cancerField),
				cb.or(Stream.of(queryParameters.getCancerDrugStatus())
					.map(status -> cb.equal(statusField, status))
				.toArray(Predicate[]::new))
			));
		}
		
		if (!queryParameters.isAnyNonCancerDrugStatus()) {
			predicates.add(cb.and(
				cb.isNull(cancerField),
				cb.or(Stream.of(queryParameters.getNonCancerDrugStatus())
					.map(status -> cb.equal(statusField, status))
				.toArray(Predicate[]::new))
			));
		}

		return or(predicates);
	}

	private Predicate createDirectIndirectPredicate(
		Root<GeneDrug> root,
		GeneQueryParameters queryParameters,
		String... geneNames
	) {
		final CriteriaBuilder cb = cb();
		
		final Expression<String> geneSymbolField = root.get("geneSymbol");
		final Expression<Collection<String>> indirectGenesField =
			root.get("indirectGenes");
		
		final List<Predicate> predicates = new LinkedList<>();
		
		if (queryParameters.areDirectIncluded()) {
			Stream.of(geneNames)
				.map(gn -> cb.equal(geneSymbolField, gn))
			.forEach(predicates::add);
		}
		
		if (queryParameters.areIndirectIncluded()) {
			Stream.of(geneNames)
				.map(gn -> cb.isMember(gn, indirectGenesField))
			.forEach(predicates::add);
		}
		
		return or(predicates);
	}
	
	private Predicate or(List<Predicate> predicates) {
		if (predicates == null) {
			return null;
		} else if (predicates.size() == 1) {
			return predicates.get(0);
		} else {
			return cb().or(predicates.toArray(new Predicate[predicates.size()]));
		}
	}
}
