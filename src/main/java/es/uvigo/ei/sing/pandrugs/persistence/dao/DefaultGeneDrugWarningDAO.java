/*
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2017 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public Set<GeneDrugWarning> findForGeneDrugs(Collection<GeneDrug> geneDrugs) {
		requireNonEmpty(geneDrugs, "At least one GeneDrug should be provided");
		
		final CriteriaBuilder cb = dh.cb();
		
		final CriteriaQuery<GeneDrugWarning> query = dh.createCBQuery();
		final Root<GeneDrugWarning> root = query.from(GeneDrugWarning.class);
		
		final Path<Object> geneSymbolField = root.get("geneSymbol");
		final Path<Object> drugField = root.get("standardDrugName");
		
		final Predicate[] predicates = geneDrugs.stream()
			.map(gd -> cb.and(
				cb.equal(geneSymbolField, gd.getGeneSymbol()),
				cb.equal(drugField, gd.getStandardDrugName())
			))
		.toArray(Predicate[]::new);

		final List<GeneDrugWarning> results = em.createQuery(query.select(root).where(cb.or(predicates)))
			.getResultList();
		
		
		return new HashSet<>(results);
	}
}
