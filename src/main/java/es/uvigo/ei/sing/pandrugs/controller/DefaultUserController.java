/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2023 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

package es.uvigo.ei.sing.pandrugs.controller;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugs.persistence.dao.UserDAO;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;
import es.uvigo.ei.sing.pandrugs.util.DigestUtils;

@Controller
@Transactional
@Lazy
public class DefaultUserController implements UserController {
	@Inject
	private UserDAO userDAO;
	
	@Override
	public User get(String login) {
		return this.userDAO.get(login);
	}
	
	@Override
	public void remove(String login) {
		this.userDAO.removeByLogin(login);
	}

	@Override
	public User update(User user) {
		return this.userDAO.update(user);
	}

	@Override
	public List<User> list() {
		return this.userDAO.list();
	}

	@Override
	public boolean checkLogin(String username, String password) {
		requireNonNull(username);
		requireNonNull(password);
		
		final String md5Pass = DigestUtils.md5Digest(password);
		
		return Optional.ofNullable(userDAO.get(username))
			.map(u -> u.getPassword().equalsIgnoreCase(md5Pass))
		.orElse(Boolean.FALSE);
	}
}
