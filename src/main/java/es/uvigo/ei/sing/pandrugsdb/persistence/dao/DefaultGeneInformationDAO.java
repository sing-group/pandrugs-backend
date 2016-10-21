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

import static java.util.Objects.requireNonNull;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformation;

@Repository
@Transactional
public class DefaultGeneInformationDAO
extends DAO<String, GeneInformation>
implements GeneInformationDAO {
	
	@Override
	public GeneInformation get(String geneSymbol) {
		return super.get(geneSymbol);
	}

	@Override
	public String[] listGeneSymbols(String queryFilter, int maxResults) {
		requireNonNull(queryFilter, "queryFilter can't be null");
		
		final CriteriaQuery<String> cq = cb().createQuery(String.class);
		final Root<GeneInformation> root = cq.from(getEntityType());
		
		final Path<String> geneSymbolField = root.get("geneSymbol");
		
		final TypedQuery<String> query = em.createQuery(
			cq.select(geneSymbolField).distinct(true)
				.where(cb().like(geneSymbolField, queryFilter + "%"))
				.orderBy(cb().asc(root.get("geneSymbol")))
		);
		
		if (maxResults > 0)
			query.setMaxResults(maxResults);
		
		return query
			.getResultList()
			.stream()
		.toArray(String[]::new);
	}
	
}
