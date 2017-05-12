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
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugs.persistence.entity.IndirectResistance;
import es.uvigo.ei.sing.pandrugs.persistence.entity.IndirectResistanceId;

@Repository
@Transactional
public class DefaultIndirectResistanceDAO implements IndirectResistanceDAO {
	@PersistenceContext
	private EntityManager em;
	
	private DAOHelper<IndirectResistanceId, IndirectResistance> dh;
	
	DefaultIndirectResistanceDAO() {}
	
	public DefaultIndirectResistanceDAO(EntityManager em) {
		this.em = em;
		createDAOHelper();
	}
	
	@PostConstruct
	private void createDAOHelper() {
		this.dh = DAOHelper.of(IndirectResistanceId.class, IndirectResistance.class, em);
	}

	@Override
	public Map<String, Set<String>> getIndirectResistancesFor(Collection<String> queryGenes) {
		requireNonEmpty(queryGenes, "At least one queryGene should be provided");
		
		final CriteriaQuery<IndirectResistance> query = dh.createCBQuery();
		final Root<IndirectResistance> root = query.from(IndirectResistance.class);
		
		final List<IndirectResistance> resistances = em.createQuery(
			query.select(root)
				.where(root.get("queryGene").in(queryGenes))
		).getResultList();
		
		final Map<String, Set<String>> result = resistances.stream()
			.collect(groupingBy(
				IndirectResistance::getQueryGene,
				mapping(IndirectResistance::getAffectedGene, toSet())
			));

		
		return invertMap(result);
	}

	private static Map<String, Set<String>> invertMap(Map<String, Set<String>> indirectResistances) {
		final Map<String, Set<String>> resistanceCausedBy = new HashMap<>();
		
		for (Entry<String, Set<String>> entry : indirectResistances.entrySet()) {
			for (String affectedGene : entry.getValue()) {
				resistanceCausedBy.putIfAbsent(affectedGene, new HashSet<>());
				resistanceCausedBy.get(affectedGene).add(entry.getKey());
			}
		}
		return resistanceCausedBy;
	}

}
