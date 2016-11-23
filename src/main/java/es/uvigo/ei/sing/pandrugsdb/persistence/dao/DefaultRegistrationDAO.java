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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Registration;

@Repository
@Transactional
public class DefaultRegistrationDAO implements RegistrationDAO {
	@PersistenceContext
	private EntityManager em;
	
	private DAOHelper<String, Registration> dh;
	
	DefaultRegistrationDAO() {}
	
	public DefaultRegistrationDAO(EntityManager em) {
		this.em = em;
		createDAOHelper();
	}

	@PostConstruct
	private void createDAOHelper() {
		this.dh = DAOHelper.of(String.class, Registration.class, this.em);
	}
	
	@Override
	public Registration get(String key) {
		return dh.get(key);
	}
	
	@Override
	public List<Registration> list() {
		return dh.list();
	}
	
	@Override
	public Registration persist(String login, String email, String password) {
		return dh.persist(new Registration(login, email, password));
	}
	
	@Override
	public Registration getByEmail(String email) {
		return dh.getBy("email", email);
	}

	@Override
	public void remove(Registration entity) {
		this.removeByUuid(entity.getUuid());
	}
	
	@Override
	public void removeByUuid(String uuid) {
		dh.removeByKey(uuid);
	}
}
