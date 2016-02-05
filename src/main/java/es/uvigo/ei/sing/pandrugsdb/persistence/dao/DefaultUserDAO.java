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

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

@Repository
@Transactional
public class DefaultUserDAO extends DAO<String, User> implements UserDAO {
	@Override
	public List<User> list() {
		return super.list();
	}
	
	@Override
	public User get(String login) {
		return super.get(login);
	}
	
	@Override
	public void removeByLogin(String login) {
		super.removeByKey(login);
	}
	
	@Override
	public User getByEmail(String email) {
		return getBy("email", email);
	}
	
	@Override
	public User update(User user) {
		final User entity = this.get(user.getLogin());
		
		if (entity == null) {
			throw new IllegalArgumentException("user doesn't exist");
		} else if (entity.getRole() != user.getRole()) {
			throw new IllegalArgumentException("user role can't be changed with update. Please, use the change role methods.");
		} else {
			return super.update(user);
		}
	}
	
	@Override
	public User changeRole(User user, RoleType newRole) {
		final User entity = this.get(user.getLogin());
		
		if (entity == null) {
			throw new IllegalArgumentException("user doesn't exist");
		} else if (user.getRole() == newRole) {
			throw new IllegalArgumentException("user already has the role: " + newRole);
		} else {
			final User userNewRole = new User(
				user.getLogin(),
				user.getEmail(),
				user.getPassword(),
				newRole
			);
			
			return super.update(userNewRole);
		}
	}
	
	@Override
	public User registerNormalUser(
		String login, String email, String password
	) {
		return registerUser(login, email, password, RoleType.USER);
	}
	
	@Override
	public User registerAdminUser(
		String login, String email, String password
	) {
		return registerUser(login, email, password, RoleType.ADMIN);
	}
	
	private final User registerUser(
		String login, String email, String password, RoleType role
	) {
		return persist(new User(login, email, password, role));
	}
}
