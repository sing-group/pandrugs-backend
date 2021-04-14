/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;

@Repository
@Transactional
public class DefaultGeneDAO implements GeneDAO {
	@PersistenceContext
	private EntityManager em;
	
	private DAOHelper<String, Gene> dh;
	
	DefaultGeneDAO() {}
	
	public DefaultGeneDAO(EntityManager em) {
		this.em = em;
		createDAOHelper();
	}
	
	@PostConstruct
	private void createDAOHelper() {
		this.dh = DAOHelper.of(String.class, Gene.class, em);
	}
	
	@Override
	public Gene get(String geneSymbol) {
		return dh.get(geneSymbol.toUpperCase());
	}
}
