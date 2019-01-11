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

import es.uvigo.ei.sing.pandrugs.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;

public interface UserDAO {
	public abstract User get(String login);

	public abstract User getByEmail(String email);
	
	public abstract List<User> list();

	public abstract User registerNormalUser(
		String login, String email, String password);

	public abstract User registerAdminUser(
		String login, String email, String password);

	public abstract User update(User user);
	
	public abstract User changeRole(User user, RoleType newRole);
	
	public abstract void removeByLogin(String login);
}
