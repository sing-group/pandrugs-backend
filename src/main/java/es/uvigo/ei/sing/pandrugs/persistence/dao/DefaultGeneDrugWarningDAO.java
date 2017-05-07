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

import static java.util.Objects.requireNonNull;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
	public GeneDrugWarning findForGeneDrug(String geneSymbol, String standardDrugName) {
		requireNonNull(geneSymbol, "geneSymbol can't be null");
		requireNonNull(standardDrugName, "standardDrugName can't be null");
		
		final CriteriaBuilder cb = dh.cb();
		
		final CriteriaQuery<GeneDrugWarning> query = dh.createCBQuery();
		final Root<GeneDrugWarning> root = query.from(GeneDrugWarning.class);
		
		try {
			return em.createQuery(
				query.select(root)
					.where(cb.and(
						cb.equal(root.get("geneSymbol"), geneSymbol),
						cb.equal(root.get("standardDrugName"), standardDrugName)
					))
			)
			.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

}
