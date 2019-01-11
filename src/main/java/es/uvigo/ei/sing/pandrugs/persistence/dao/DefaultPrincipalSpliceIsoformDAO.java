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

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.uvigo.ei.sing.pandrugs.persistence.entity.PrincipalSpliceIsoform;

public class DefaultPrincipalSpliceIsoformDAO implements PrincipalSpliceIsoformDAO {
	@PersistenceContext
	private EntityManager em;
	
	private DAOHelper<String, PrincipalSpliceIsoform> dh;
	
	DefaultPrincipalSpliceIsoformDAO() {}
	
	public DefaultPrincipalSpliceIsoformDAO(EntityManager em) {
		this.em = em;
		createDAOHelper();
	}

	@PostConstruct
	private void createDAOHelper() {
		this.dh = DAOHelper.of(String.class, PrincipalSpliceIsoform.class, this.em);
	}
	
	@Override
	public PrincipalSpliceIsoform get(String transcriptId) {
		return dh.get(transcriptId);
	}

}
