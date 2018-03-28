/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
import static es.uvigo.ei.sing.pandrugs.util.Checks.UUID_PATTERN;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireEmail;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireStringSize;
import static es.uvigo.ei.sing.pandrugs.util.Checks.requireUUID;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity(name = "registration")
public class Registration implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(length = 36)
	@NotNull
	@Size(min = 36, max = 36, message = "Invalid registration UUID")
	@Pattern(regexp = UUID_PATTERN)
	private String uuid;
	
	@Column(unique = true, updatable = false, length = 50)
	@NotNull
	@Size(min = 1, max = 50, message = "Login must have between 1 and 50 characters")
	private String login;
	
	@Column(unique = true, length = 100)
	@NotNull
	@Pattern(regexp = EMAIL_PATTERN, message = "Invalid email address")
	@Size(min = 5, max = 100, message = "Invalid email address")
	private String email;
	
	@Column(length = 32)
	@NotNull
	private String password;
	
	Registration() {
	}
	
	Registration(String login, String email, String password, String uuid) {
		this.setLogin(login);
		this.setEmail(email);
		this.setPassword(password);
		this.setUuid(uuid);
	}

	public Registration(String login, String email, String password) {
		this(login, email, password, UUID.randomUUID().toString());
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = requireStringSize(login, 1, 50, "Login must have between 1 and 50 characters");
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = requireEmail(email, "Invalid email address");
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = requireUUID(uuid, "Invalid registration UUID");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		Registration other = (Registration) obj;
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
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
