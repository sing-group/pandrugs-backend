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

package es.uvigo.ei.sing.pandrugs.matcher.hamcrest;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugs.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;
import es.uvigo.ei.sing.pandrugs.service.entity.UserInfo;

public class HasTheSameUserDataMatcher extends TypeSafeMatcher<Object> {
	private final String login;
	private final String email;
	private final String password;
	private final RoleType role;

	public HasTheSameUserDataMatcher(
		String login, String email,
		String password, RoleType role
	) {
		this.login = login;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(String.format("User data does not match: %s - %s - %s - %s",
			this.login, this.email, this.password, this.role == null ? "null" : this.role.toString()));
	}

	@Override
	protected boolean matchesSafely(Object entity) {
		return Checker.check(entity, login, email, password, role);
	}
	
	@Factory
	public static HasTheSameUserDataMatcher hasTheSameUserDataAs(User expected) {
		return new HasTheSameUserDataMatcher(
			expected.getLogin(),
			expected.getEmail(),
			expected.getPassword(),
			expected.getRole()
		);
	}
	
	@Factory
	public static HasTheSameUserDataMatcher hasTheSameUserDataAs(UserInfo expected) {
		return new HasTheSameUserDataMatcher(
			expected.getLogin(),
			expected.getEmail(),
			expected.getPassword(),
			expected.getRole()
		);
	}
	
	@Factory
	public static HasTheSameUserDataMatcher hasTheSameUserDataAs(Registration expected) {
		return new HasTheSameUserDataMatcher(
			expected.getLogin(),
			expected.getEmail(),
			expected.getPassword(),
			null
		);
	}
	
	@Factory
	public static HasTheSameUserDataMatcher hasTheSameUserDataAs(
		String login, String email,
		String password, RoleType role
	) {
		return new HasTheSameUserDataMatcher(
			login, email, password, role
		);
	}
	
	private static enum Checker {
		USER(User.class,
			(u, l) -> ((User) u).getLogin().equals(l),
			(u, e) -> ((User) u).getEmail().equals(e),
			(u, p) -> ((User) u).getPassword().equals(p),
			(u, r) -> ((User) u).getRole().equals(r)
		),
		USER_METADATA(UserInfo.class,
			(u, l) -> ((UserInfo) u).getLogin().equals(l),
			(u, e) -> ((UserInfo) u).getEmail().equals(e),
			(u, p) -> ((UserInfo) u).getPassword().equals(p),
			(u, r) -> ((UserInfo) u).getRole().equals(r)
		),
		REGISTRATION(Registration.class,
			(u, l) -> ((Registration) u).getLogin().equals(l),
			(u, e) -> ((Registration) u).getEmail().equals(e),
			(u, p) -> ((Registration) u).getPassword().equals(p),
			(u, r) -> true
		);
		
		private final Class<?> clazz;
		private final BiFunction<Object, String, Boolean> checkLogin;
		private final BiFunction<Object, String, Boolean> checkEmail;
		private final BiFunction<Object, String, Boolean> checkPassword;
		private final BiFunction<Object, RoleType, Boolean> checkRole;
		
		private Checker(
			Class<?> clazz,
			BiFunction<Object, String, Boolean> getLogin,
			BiFunction<Object, String, Boolean> getEmail,
			BiFunction<Object, String, Boolean> getPassword,
			BiFunction<Object, RoleType, Boolean> getRole
		) {
			this.clazz = clazz;
			this.checkLogin = getLogin;
			this.checkEmail = getEmail;
			this.checkPassword = getPassword;
			this.checkRole = getRole;
		}
		
		public static boolean check(Object entity, String login, String email, String password, RoleType role) {
			final Checker checker = forEntity(entity);
			
			return checker != null &&
				checker.checkLogin.apply(entity, login) &&
				checker.checkEmail.apply(entity, email) &&
				checker.checkPassword.apply(entity, password) &&
				(role == null || checker.checkRole.apply(entity, role));
		}
		
		private static Checker forEntity(Object entity) {
			return Stream.of(values())
				.filter(checker -> checker.clazz.isAssignableFrom(entity.getClass()))
				.findFirst()
			.orElse(null);
		}
	}
}
