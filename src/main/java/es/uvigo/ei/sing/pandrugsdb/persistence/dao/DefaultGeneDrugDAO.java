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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus.activeDrugStatus;
import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonEmpty;
import static es.uvigo.ei.sing.pandrugsdb.util.StringFormatter.toUpperCase;
import static java.util.Objects.requireNonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugSource;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrugId;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.IndirectGene;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.ResistanceType;
import es.uvigo.ei.sing.pandrugsdb.query.GeneDrugQueryParameters;
import es.uvigo.ei.sing.pandrugsdb.query.TargetMarkerStatus;

@Repository
@Transactional
public class DefaultGeneDrugDAO implements GeneDrugDAO {
	@PersistenceContext
	private EntityManager em;
	
	private DAOHelper<GeneDrugId, GeneDrug> dh;
	
	DefaultGeneDrugDAO() {}
	
	public DefaultGeneDrugDAO(EntityManager em) {
		this.em = em;
		createDAOHelper();
	}

	@PostConstruct
	private void createDAOHelper() {
		this.dh = DAOHelper.of(GeneDrugId.class, GeneDrug.class, this.em);
	}

	@Override
	public String[] listGeneSymbols(String queryFilter, int maxResults) {
		return listByField(queryFilter, maxResults, (root, join) -> root.get("geneSymbol"));
	}

	@Override
	public Drug[] listDrugs(String queryFilter, int maxResults) {
		requireNonNull(queryFilter, "Query filter can't be null");
		
		final CriteriaBuilder cb = dh.cb();
		final CriteriaQuery<Drug> cq = cb.createQuery(Drug.class);
		
		final Root<GeneDrug> root = cq.from(dh.getEntityType());
		
		final Join<GeneDrug, Drug> joinDrug = root.join("drug");
		
		final Join<Drug, DrugSource> joinDrugSources = joinDrug.join("drugSources");
		
		final Expression<DrugStatus> fieldStatus = joinDrug.get("status");
		final Expression<String> fieldStandardName = joinDrug.get("standardName");
		final Expression<String> fieldShowName = joinDrug.get("showName");
		final Expression<String> fieldSourceName = joinDrugSources.get("sourceDrugName");
		
		final DrugStatus[] activeDrugStatus = activeDrugStatus();
		
		final TypedQuery<Drug> query = dh.em().createQuery(
			cq.select(joinDrug).distinct(true)
				.where(cb.and(
					fieldStatus.in((Object[]) activeDrugStatus),
					cb.or(
						cb.like(fieldStandardName, queryFilter + "%"),
						cb.like(fieldShowName, queryFilter + "%"),
						cb.like(fieldSourceName, queryFilter + "%")
					)
				))
				.orderBy(cb.asc(fieldShowName))
		);
		
		if (maxResults > 0)
			query.setMaxResults(maxResults);
		
		return query
			.getResultList()
			.stream()
		.toArray(Drug[]::new);
	}
	
	private String[] listByField(
		String queryFilter,
		int maxResults,
		BiFunction<Root<GeneDrug>, Join<GeneDrug, Drug>, Expression<String>> fieldProvider
	) {
		requireNonNull(queryFilter, "Query filter can't be null");
		
		final CriteriaBuilder cb = dh.cb();
		final CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		final Root<GeneDrug> root = cq.from(dh.getEntityType());
		
		final Join<GeneDrug, Drug> join = root.join("drug");
		
		final Expression<String> field = fieldProvider.apply(root, join);
		final Expression<DrugStatus> statusField = join.get("status");
		
		final DrugStatus[] activeDrugStatus = activeDrugStatus();
		
		final TypedQuery<String> query = dh.em().createQuery(
			cq.select(field).distinct(true)
				.where(cb.and(
					cb.like(field, queryFilter + "%"),
					statusField.in((Object[]) activeDrugStatus)
				))
				.orderBy(cb.asc(field))
		);
		
		if (maxResults > 0)
			query.setMaxResults(maxResults);
		
		return query
			.getResultList()
			.stream()
		.toArray(String[]::new);
	}
	
	@Override
	public List<GeneDrug> searchByGene(GeneDrugQueryParameters queryParameters, String ... geneNames) {
		requireNonNull(queryParameters, "Query parameters can't be null");
		requireNonEmpty(geneNames, "At least one gene name must be provided");

		return searchWithQueryParameters(
			queryParameters,
			(root, join, query) -> createDirectIndirectPredicate(root, join, query, queryParameters, toUpperCase(geneNames))
		);
	}

	@Override
	public List<GeneDrug> searchByDrug(final GeneDrugQueryParameters queryParameters, final String... drugNames) {
		requireNonNull(queryParameters, "Query parameters can't be null");
		requireNonEmpty(drugNames, "At least one drug name must be provided");
		
		return searchWithQueryParameters(
			queryParameters,
			(root, join, query) -> createDrugPredicate(root, join, query, queryParameters, toUpperCase(drugNames))
		);
	}
	
	private interface SpecificPredicateBuilder {
		public Predicate buildPredicate(Root<GeneDrug> root, Join<GeneDrug, Drug> join, CriteriaQuery<GeneDrug> cb);
	}
	
	private List<GeneDrug> searchWithQueryParameters(
		GeneDrugQueryParameters queryParameters,
		SpecificPredicateBuilder specificPredicateBuilder
	) {
		requireNonNull(queryParameters, "Query parameters can't be null");
		
		final CriteriaQuery<GeneDrug> query = dh.createCBQuery();
		final Root<GeneDrug> root = query.from(dh.getEntityType());
		final Join<GeneDrug, Drug> join = root.join("drug");
		
		final Predicate predicate = dh.cb().and(
			Stream.of(
				specificPredicateBuilder.buildPredicate(root, join, query),
				createDrugStatusPredicate(root, join, query, queryParameters),
				createTargetMarkerPredicate(root, queryParameters)
			)
				.filter(Objects::nonNull)
			.toArray(Predicate[]::new)	
		);
		
		return dh.em().createQuery(
			query.select(root).distinct(true)
				.where(predicate)
		).getResultList();
	}
	
	private Predicate createTargetMarkerPredicate(
		Root<GeneDrug> root,
		GeneDrugQueryParameters queryParameters
	) {
		if (queryParameters.getTargetMarker() == TargetMarkerStatus.BOTH) {
			return null;
		} else {
			final Expression<Boolean> targetField = root.get("target");
			
			if (queryParameters.getTargetMarker() == TargetMarkerStatus.TARGET) {
				return dh.cb().isTrue(targetField);
			} else {
				return dh.cb().isFalse(targetField);
			}
		}
	}
	
	private Predicate createDrugStatusPredicate(
		Root<GeneDrug> root,
		Join<GeneDrug, Drug> join,
		CriteriaQuery<GeneDrug> query,
		GeneDrugQueryParameters queryParameters
	) {
		if (queryParameters.areAllDrugStatusIncluded()) {
			return null;
		} else {
			final CriteriaBuilder cb = dh.cb();
			
			final Expression<DrugStatus> statusField = join.get("status");
			
			final Join<Drug, CancerType> joinCancers = join.join("cancers", JoinType.LEFT);
			
			final List<Predicate> predicates = new LinkedList<>();
			
			if (queryParameters.areCancerDrugStatusIncluded()) {
				final Predicate isCancer = joinCancers.isNotNull();
				
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
				final Predicate isNotCancer = joinCancers.isNull();
				
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
			
			return or(predicates);
		}
	}

	private Predicate createDrugPredicate(
		Root<GeneDrug> root,
		Join<GeneDrug, Drug> join,
		CriteriaQuery<GeneDrug> query,
		GeneDrugQueryParameters queryParameters,
		String ... drugNames
	) {
		final Expression<String> standardNameDrugField = join.get("standardName");
		
		final CriteriaBuilder cb = dh.cb();
		final Function<Expression<String>, Predicate> isInDrugs = drugNames.length == 1 ?
			e -> cb.equal(e, drugNames[0]) :
			e -> e.in((Object[]) drugNames);
		
		return isInDrugs.apply(standardNameDrugField);
	}

	private Predicate createDirectIndirectPredicate(
		Root<GeneDrug> root,
		Join<GeneDrug, Drug> join,
		CriteriaQuery<GeneDrug> query,
		GeneDrugQueryParameters queryParameters,
		String ... geneNames
	) {
		final CriteriaBuilder cb = dh.cb();
		
		final Expression<String> geneSymbolField = root.get("geneSymbol");
		
		final List<Predicate> predicates = new LinkedList<>();
		
		final Function<Expression<String>, Predicate> isInGenes = geneNames.length == 1 ?
			e -> dh.cb().equal(e, geneNames[0]) :
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
			return dh.cb().or(predicates.toArray(new Predicate[predicates.size()]));
		}
	}
}
