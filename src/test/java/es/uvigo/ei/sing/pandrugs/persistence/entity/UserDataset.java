/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2022 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

package es.uvigo.ei.sing.pandrugs.persistence.entity;

import java.util.Arrays;

public class UserDataset {
	private UserDataset() {}
	
	public final static User[] users() {
		return new User[] {
			new User("pepe", "pepe@email.com", "926e27eecdbc7a18858b3798ba99bddd", RoleType.USER),
			new User("paco", "paco@email.com", "311020666a5776c57d265ace682dc46d", RoleType.USER),
			new User("ana", "ana@email.com", "276b6c4692e78d4799c12ada515bc3e4", RoleType.USER),
			new User("juan", "juan@email.com", "a94652aa97c7211ba8954dd15a3cf838", RoleType.USER),
			new User("maría", "maria@email.com", "8a2ac0c5b70c320c517fea7adb2e4d00", RoleType.USER),
			new User("admin1", "admin1@email.com", "e00cf25ad42683b3df678c61f42c6bda", RoleType.ADMIN),
			new User("guest", "guest@guest.com", "084e0343a0486ff05530df6c705c8bb4", RoleType.GUEST)
		};
	}
	
	public final static User user(String login) {
		return Arrays.stream(users())
			.filter(user -> user.getLogin().equals(login))
		.findAny().orElseThrow(() -> new IllegalArgumentException("Unknown user: " + login));
	}
	
	public final static User presentUser() {
		return users()[1];
	}
	
	public final static User presentUser2() {
		return users()[2];
	}
	
	public final static String presentUserPassword() {
		return presentUser().getLogin();
	}
	
	public final static User presentAdmin() {
		final User[] users = users();
		return users[users.length - 2];
	}
	
	public final static String presentAdminPassword() {
		return presentAdmin().getLogin();
	}
	
	public final static User absentUser() {
		return new User("fake", "fake@email.com", "144c9defac04969c7bfad8efaa8ea194", RoleType.USER);
	}
	
	public final static String absentUserPassword() {
		return absentUser().getLogin();
	}
	
	public final static User anyUser() {
		return absentUser();
	}

	public final static User guestUser() {
		return users()[users().length - 1];
	}

	public final static String anyUserPassword() {
		return anyUser().getLogin();
	}
}
