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

import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonNullArray;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class DAO<K, T> {
	@PersistenceContext
	protected EntityManager em;
	
	@SuppressWarnings("unchecked")
	protected Class<T> getEntityType() {
		return (Class<T>) ((ParameterizedType) getClass()
			.getGenericSuperclass())
	        .getActualTypeArguments()[1];
	}
	
	protected T get(K key) {
		return this.em.find(this.getEntityType(), requireNonNull(key));
	}
	
	protected T persist(T entity) {
		try {
			this.em.persist(entity);
			this.em.flush();
			
			return entity;
		} catch (PersistenceException pe) {
			throw new IllegalArgumentException("entity already exists", pe);
		}
	}
	
	protected T update(T entity) {
		final T mergedEntity = this.em.merge(entity);
		this.em.flush();
		
		return mergedEntity;
	}
	
	protected List<T> list() {
		final CriteriaQuery<T> query = createCBQuery();
		
		return em.createQuery(
			query.select(query.from(getEntityType()))
		).getResultList();
	}
	
	protected void removeByKey(K key) {
		this.em.remove(get(key));
		this.em.flush();
	}
	
	protected void remove(T entity) {
		this.em.remove(entity);
		this.em.flush();
	}

	@SafeVarargs
	protected final <F> List<T> listBy(String fieldName, F ... value) {
		return createFieldQuery(fieldName, empty(), empty(), value)
			.getResultList();
	}

	@SafeVarargs
	protected final <F> List<T> listBy(String fieldName, int startPosition, int maxResults, F ... value) {
		return createFieldQuery(fieldName, Optional.of(startPosition), Optional.of(maxResults), value)
			.getResultList();
	}

	@SafeVarargs
	protected final <F> List<T> listBy(String fieldName, Optional<Integer> startPosition, Optional<Integer> maxResults, F ... value) {
		return createFieldQuery(fieldName, startPosition, maxResults, value)
			.getResultList();
	}
	
	protected <F> T getBy(String fieldName, F value) {
		try {
			return createFieldQuery(fieldName, empty(), empty(), value)
				.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	@SafeVarargs
	protected final <F> TypedQuery<T> createFieldQuery(
		String fieldName,
		Optional<Integer> startPosition,
		Optional<Integer> maxResults,
		F ... values
	) {
		requireNonNull(fieldName, "fieldname can't be null");
		requireNonNullArray(values);
		
		final CriteriaQuery<T> query = createCBQuery();
		final Root<T> root = query.from(getEntityType());
		
		final Function<F, Predicate> fieldEqualsTo = value -> 
			cb().equal(root.get(fieldName), value);
			
		final Predicate predicate = values.length == 1 ?
			fieldEqualsTo.apply(values[0]) :
			cb().or(Stream.of(values)
				.map(fieldEqualsTo)
			.toArray(Predicate[]::new));
		
		return em.createQuery(
			query.select(root).where(predicate)
		)
		.setFirstResult(startPosition.orElse(0))
		.setMaxResults(maxResults.orElse(Integer.MAX_VALUE));
	}
	
	protected CriteriaQuery<T> createCBQuery() {
		return cb().createQuery(this.getEntityType());
	}
	
	protected CriteriaBuilder cb() {
		return em.getCriteriaBuilder();
	}
}
