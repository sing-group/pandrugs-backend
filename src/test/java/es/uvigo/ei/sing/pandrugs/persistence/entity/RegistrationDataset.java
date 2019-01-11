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

package es.uvigo.ei.sing.pandrugs.persistence.entity;


public final class RegistrationDataset {
	private RegistrationDataset() {}

	public final static Registration[] registrations() {
		return new Registration[] {
			new Registration("pepe", "pepe@email.com",
				"926e27eecdbc7a18858b3798ba99bddd",
				"87654321-abcd-1234-cdef-0123456789ab"),
			new Registration("paco", "paco@email.com",
				"311020666a5776c57d265ace682dc46d",
				"11223344-abcd-1234-cdef-0123456789ab"),
			new Registration("juan", "juan@email.com",
				"a94652aa97c7211ba8954dd15a3cf838",
				"12345678-abcd-1234-cdef-0123456789ab")
		};
	}
	
	public final static User userFor(Registration registration) {
		return new User(
			registration.getLogin(),
			registration.getEmail(),
			registration.getPassword(),
			RoleType.USER
		);
	}
	
	public final static User presentUser() {
		return new User("maría", "maria@email.com",
			"8a2ac0c5b70c320c517fea7adb2e4d00", RoleType.USER);
	}
	
	public final static User absentUser() {
		return new User("fake", "fake@email.com",
			"144c9defac04969c7bfad8efaa8ea194", RoleType.USER);
	}
	
	public final static User anyUser() {
		return absentUser();
	}
	
	public final static Registration presentRegistration() {
		return registrations()[1];
	}
	
	public final static Registration absentRegistration() {
		return new Registration("fake", "fake@email.com",
			"144c9defac04969c7bfad8efaa8ea194",
			"00000000-1111-2222-3333-444444444444");
	}

	public final static Registration anyRegistration() {
		return absentRegistration();
	}

	public final static String plainPassword(User user) {
		return user.getLogin();
	}

	public final static String plainPassword(Registration registration) {
		return registration.getLogin();
	}

	public static String anyUrl() {
		return "http://localhost/%s";
	}
}
