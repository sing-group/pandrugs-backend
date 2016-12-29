/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.persistence.dao;

import static es.uvigo.ei.sing.pandrugs.util.Checks.requireNonNegative;
import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugs.persistence.entity.ClinicalGenomeVariation;
import es.uvigo.ei.sing.pandrugs.persistence.entity.ClinicalGenomeVariationId;

@Repository
@Transactional
public class DefaultClinicalGenomeVariationDAO
implements ClinicalGenomeVariationDAO {
	@PersistenceContext
	private EntityManager em;
	
	private DAOHelper<ClinicalGenomeVariationId, ClinicalGenomeVariation> dh;
	
	DefaultClinicalGenomeVariationDAO() {}
	
	public DefaultClinicalGenomeVariationDAO(EntityManager em) {
		this.em = em;
		createDAOHelper();
	}

	@PostConstruct
	private void createDAOHelper() {
		this.dh = DAOHelper.of(ClinicalGenomeVariationId.class, ClinicalGenomeVariation.class, this.em);
	}
	
	@Override
	public ClinicalGenomeVariation get(ClinicalGenomeVariationId key) {
		return dh.get(key);
	}
	
	@Override
	public List<ClinicalGenomeVariation> listByDbSnp(String dbSnp) {
		return dh.listBy("dbSnp", dbSnp);
	}

	@Override
	public List<ClinicalGenomeVariation> listByChromosomePosition(String chromosome, int start, int end) {
		requireNonNull(chromosome, "Chromosome can't be null");
		requireNonNegative(start, "Start can't be negative");
		requireNonNegative(end, "End can't be negative");
		
		final CriteriaQuery<ClinicalGenomeVariation> query = dh.createCBQuery();
		final Root<ClinicalGenomeVariation> root = query.from(dh.getEntityType());
		final CriteriaBuilder cb = dh.cb();

		final Path<String> chromosomeField = root.get("chromosome");
		final Path<Integer> startField = root.get("start");
		final Path<Integer> endField = root.get("end");
		
		return dh.em().createQuery(
			query.select(root)
				.where(cb.and(
					cb.equal(chromosomeField, chromosome),
					cb.equal(startField, start),
					cb.equal(endField, end)
				))
		).getResultList();
	}
}
