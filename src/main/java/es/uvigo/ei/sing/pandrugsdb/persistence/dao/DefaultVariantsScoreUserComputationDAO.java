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

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreUserComputation;

@Repository
@Transactional
public class DefaultVariantsScoreUserComputationDAO
extends DAO<Integer, VariantsScoreUserComputation>
implements VariantsScoreUserComputationDAO {

	@Override
	public void storeComputation(VariantsScoreUserComputation computation) {
		super.persist(computation);
		
	}

	@Override
	public List<VariantsScoreUserComputation> retrieveComputationsBy(User user) {
		return super.listBy("user", user);
	}

	
	@Override
	public VariantsScoreUserComputation update(VariantsScoreUserComputation entity) {
		return super.update(entity);
	}

	@Override
	public VariantsScoreUserComputation get(int id) {
		return super.get(id);
	}

	@Override
	public List<VariantsScoreUserComputation> list() {
		return super.list();
	}
	
}
