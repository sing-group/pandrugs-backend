/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.persistence.entity;

import static es.uvigo.ei.sing.pandrugs.util.Checks.EMAIL_PATTERN;
import static es.uvigo.ei.sing.pandrugs.util.Checks.MD5_PATTERN;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireEmail;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireMD5;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireStringSize;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity(name = "user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 50)
	@NotNull(message = "Login can not be null")
	@Size(min = 1, max = 50, message = "Login must have between 1 and 50 characters")
	private String login;
	
	@Column(unique = true, length = 100)
	@NotNull
	@Pattern(regexp = EMAIL_PATTERN, message = "Invalid email address")
	@Size(min = 5, max = 100, message = "Invalid email address")
	private String email;
	
	@Column(length = 32)
	@NotNull(message = "Password can not be null")
	@Size(min = 32, max = 32, message = "Password must be MD5 digested")
	@Pattern(regexp = MD5_PATTERN, message = "Password must be MD5 digested")
	private String password;
	
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Role can not be null")
	private RoleType role;
	
	User() {
	}
	
	public User(String login, String email, String password, RoleType role) {
		this.login = requireStringSize(login, 1, 50, "Login must have between 1 and 50 characters");
		this.setEmail(email);
		this.setPassword(password);
		this.role = requireNonNull(role);
	}

	public String getLogin() {
		return login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = requireEmail(email, "Invalid email address: " + email);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = requireMD5(password, "Invalid password");
	}
	
	public RoleType getRole() {
		return role;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (role != other.role)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.login + " - " + this.password;
	}
}
