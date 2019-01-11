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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugs.persistence.entity.User;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreUserComputation;

@Repository
@Transactional
public class DefaultVariantsScoreUserComputationDAO
implements VariantsScoreUserComputationDAO {
	@PersistenceContext
	private EntityManager em;

	private DAOHelper<String, VariantsScoreUserComputation> dh;

	DefaultVariantsScoreUserComputationDAO() {}
	
	public DefaultVariantsScoreUserComputationDAO(EntityManager em) {
		this.em = em;
		createDAOHelper();
	}
	
	@PostConstruct
	private void createDAOHelper() {
		this.dh = DAOHelper.of(String.class, VariantsScoreUserComputation.class, em);
	}

	@Override
	public void storeComputation(VariantsScoreUserComputation computation) {
		dh.persist(computation);
	}

	@Override
	public List<VariantsScoreUserComputation> retrieveComputationsBy(User user) {
		return dh.listBy("user", user);
	}

	@Override
	public VariantsScoreUserComputation update(VariantsScoreUserComputation entity) {
		return dh.update(entity);
	}

	@Override
	public VariantsScoreUserComputation get(String id) {
		return dh.get(id);
	}

	@Override
	public List<VariantsScoreUserComputation> list() {
		return dh.list();
	}

	@Override
	public void remove(VariantsScoreUserComputation computation) {
		dh.remove(computation);
	}

}
