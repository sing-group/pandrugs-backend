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
package es.uvigo.ei.sing.pandrugsdb.persistence.dao;

import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonEmpty;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.SomaticMutationInCancer;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.SomaticMutationInCancerId;

public class DefaultSomaticMutationInCancerDAO
extends DAO<SomaticMutationInCancerId, SomaticMutationInCancer>
implements SomaticMutationInCancerDAO {

	@Override
	public SomaticMutationInCancer get(SomaticMutationInCancerId id) {
		return super.get(id);
	}

	@Override
	public List<SomaticMutationInCancer> listByGeneAndMutationAA(String geneSymbol, String mutationAA) {
		requireNonEmpty(geneSymbol, "geneSymbol can't be empty or null");
		requireNonEmpty(mutationAA, "mutationAA can't be empty or null");

		final CriteriaQuery<SomaticMutationInCancer> query = createCBQuery();
		final Root<SomaticMutationInCancer> root = query.from(getEntityType());
		final CriteriaBuilder cb = cb();

		final Path<String> geneSymbolField = root.get("geneSymbol");
		final Path<Integer> mutationAAField = root.get("mutationAA");
		
		return em.createQuery(
			query.select(root)
				.where(cb.and(
					cb.equal(geneSymbolField, geneSymbol),
					cb.equal(mutationAAField, mutationAA)
				))
		).getResultList();
	}

}
