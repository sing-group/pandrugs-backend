/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2019 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancer;
import es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancerId;

public class DefaultSomaticMutationInCancerDAO
implements SomaticMutationInCancerDAO {
	@PersistenceContext
	private EntityManager em;
	
	private DAOHelper<SomaticMutationInCancerId, SomaticMutationInCancer> dh;
	
	DefaultSomaticMutationInCancerDAO() {}
	
	public DefaultSomaticMutationInCancerDAO(EntityManager em) {
		this.em = em;
		createDAOHelper();
	}

	@PostConstruct
	private void createDAOHelper() {
		this.dh = DAOHelper.of(SomaticMutationInCancerId.class, SomaticMutationInCancer.class, this.em);
	}
	
	@Override
	public SomaticMutationInCancer get(SomaticMutationInCancerId id) {
		return dh.get(id);
	}

	@Override
	public List<SomaticMutationInCancer> listByGeneAndMutationAA(String geneSymbol, String mutationAA) {
		requireNonEmpty(geneSymbol, "geneSymbol can't be empty or null");
		requireNonEmpty(mutationAA, "mutationAA can't be empty or null");

		final CriteriaQuery<SomaticMutationInCancer> query = dh.createCBQuery();
		final Root<SomaticMutationInCancer> root = query.from(dh.getEntityType());
		final CriteriaBuilder cb = dh.cb();

		final Path<String> geneSymbolField = root.get("geneSymbol");
		final Path<Integer> mutationAAField = root.get("mutationAA");
		
		return dh.em().createQuery(
			query.select(root)
				.where(cb.and(
					cb.equal(geneSymbolField, geneSymbol),
					cb.equal(mutationAAField, mutationAA)
				))
		).getResultList();
	}

}
