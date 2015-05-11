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

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;

@Repository
@Transactional
public class DefaultGeneDrugDAO
extends DAO<Integer, GeneDrug>
implements GeneDrugDAO {
	@Override
	public List<GeneDrug> searchWithIndirects(String ... geneNames) {
		requireNonEmpty(geneNames, "At least one gene name must be provided");
		
		final CriteriaQuery<GeneDrug> query = createCBQuery();
		final Root<GeneDrug> root = query.from(getEntityType());
		
		final Path<Object> geneSymbolField = root.get("geneSymbol");
		final Expression<Collection<String>> indirectGenesField =
			root.get("indirectGenes");
		
		final Predicate[] directPredicates = Stream.of(geneNames)
			.map(gn -> cb().equal(geneSymbolField, gn))
		.toArray(Predicate[]::new);
		
		final Predicate[] indirectPredicates = Stream.of(geneNames)
			.map(gn -> cb().isMember(gn, indirectGenesField))
		.toArray(Predicate[]::new);
		
		final Predicate predicate = cb().or(
			cb().or(directPredicates),
			cb().or(indirectPredicates)
		);
		return em.createQuery(
			query.select(root).where(predicate)
		).getResultList();
	}
}
