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

import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonNegative;
import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.ClinicalGenomeVariation;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.ClinicalGenomeVariationId;

@Repository
@Transactional
public class DefaultClinicalGenomeVariationDAO
extends DAO<ClinicalGenomeVariationId, ClinicalGenomeVariation>
implements ClinicalGenomeVariationDAO {
	@Override
	public ClinicalGenomeVariation get(ClinicalGenomeVariationId key) {
		return super.get(key);
	}
	
	@Override
	public List<ClinicalGenomeVariation> listByDbSnp(String dbSnp) {
		return super.listBy("dbSnp", dbSnp);
	}

	@Override
	public List<ClinicalGenomeVariation> listByChromosomePosition(String chromosome, int start, int end) {
		requireNonNull(chromosome, "Chromosome can't be null");
		requireNonNegative(start, "Start can't be negative");
		requireNonNegative(end, "End can't be negative");
		
		final CriteriaQuery<ClinicalGenomeVariation> query = createCBQuery();
		final Root<ClinicalGenomeVariation> root = query.from(getEntityType());
		final CriteriaBuilder cb = cb();

		final Path<String> chromosomeField = root.get("chromosome");
		final Path<Integer> startField = root.get("start");
		final Path<Integer> endField = root.get("end");
		
		return em.createQuery(
			query.select(root)
				.where(cb.and(
					cb.equal(chromosomeField, chromosome),
					cb.equal(startField, start),
					cb.equal(endField, end)
				))
		).getResultList();
	}
}
