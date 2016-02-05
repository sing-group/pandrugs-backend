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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.IndirectGene;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.ResistanceType;
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
				createDirectIndirectPredicate(root, query, queryParameters, geneNames),
				createDrugStatusPredicate(root, query, queryParameters),
				createTargetMarkerPredicate(root, queryParameters)
			)
				.filter(Objects::nonNull)
			.toArray(Predicate[]::new)	
		);
		
		return em.createQuery(
			query.select(root).distinct(true)
				.where(predicate)
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
		CriteriaQuery<GeneDrug> query,
		GeneQueryParameters queryParameters
	) {
		if (queryParameters.areAllDrugStatusIncluded()) {
			return null;
		} else {
			final CriteriaBuilder cb = cb();

			final Subquery<Drug> subqueryDrug = query.subquery(Drug.class);
			final Root<Drug> rootDrug = subqueryDrug.from(Drug.class);
			
			final Expression<List<CancerType>> cancersField = rootDrug.get("cancers");
			final Expression<DrugStatus> statusField = rootDrug.get("status");

			final List<Predicate> predicates = new LinkedList<>();
			
			if (queryParameters.areCancerDrugStatusIncluded()) {
				final Predicate isCancer = cb.gt(cb.size(cancersField), 0);
				
				if (queryParameters.isAnyCancerDrugStatus()) {
					predicates.add(isCancer);
				} else {
					final DrugStatus[] cancerDrugStatus =
						queryParameters.getCancerDrugStatus();
					
					predicates.add(cb.and(
						isCancer,
						statusField.in((Object[]) cancerDrugStatus)
					));
				}
			}
			
			if (queryParameters.areNonCancerDrugStatusIncluded()) {
				final Predicate isNotCancer = cb.equal(cb.size(cancersField), 0);
				
				if (queryParameters.isAnyNonCancerDrugStatus()) {
					predicates.add(isNotCancer);
				} else {
					final DrugStatus[] nonCancerDrugStatus =
						queryParameters.getNonCancerDrugStatus();
					
					predicates.add(cb.and(
						isNotCancer,
						statusField.in((Object[]) nonCancerDrugStatus)
					));
				}
			}
			
			return cb.exists(subqueryDrug.select(rootDrug)
				.where(cb.and(
					cb.equal(rootDrug.get("id"), root.get("drugId")),
					or(predicates)
				))
			);
		}
	}

	private Predicate createDirectIndirectPredicate(
		Root<GeneDrug> root,
		CriteriaQuery<GeneDrug> query,
		GeneQueryParameters queryParameters,
		String ... geneNames
	) {
		final CriteriaBuilder cb = cb();
		
		final Expression<String> geneSymbolField = root.get("geneSymbol");
		
		final List<Predicate> predicates = new LinkedList<>();
		
		final Function<Expression<String>, Predicate> isInGenes = geneNames.length == 1 ?
			e -> cb().equal(e, geneNames[0]) :
			e -> e.in((Object[]) geneNames);
			
		
		if (queryParameters.areDirectIncluded()) {
			predicates.add(isInGenes.apply(geneSymbolField));
		}
		
		if (queryParameters.areIndirectIncluded()) {
			final Subquery<IndirectGene> subqueryIndirectGenes =
				query.subquery(IndirectGene.class);
			final Root<IndirectGene> rootIndirectGene =
				subqueryIndirectGenes.from(IndirectGene.class);
			
			predicates.add(
				cb.and(
					cb.notEqual(root.get("resistance"), ResistanceType.RESISTANCE),
					cb.notEqual(root.get("target"), false),
					cb.exists(
						subqueryIndirectGenes.select(rootIndirectGene.get("indirectGeneSymbol"))
						.where(cb.and(
							isInGenes.apply(rootIndirectGene.get("indirectGeneSymbol")),
							cb.equal(rootIndirectGene.get("directGeneSymbol"), root.get("geneSymbol")),
							cb.equal(rootIndirectGene.get("drugId"), root.get("drugId")),
							cb.equal(rootIndirectGene.get("target"), root.get("target"))
						))
					)
				)
			);
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
