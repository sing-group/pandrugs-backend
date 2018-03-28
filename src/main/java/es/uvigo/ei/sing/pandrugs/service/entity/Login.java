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

package es.uvigo.ei.sing.pandrugs.service.entity;

import static es.uvigo.ei.sing.pandrugs.util.Checks.requireNonEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "login", namespace = "http://sing.ei.uvigo.es/pandrugs")
@XmlAccessorType(XmlAccessType.FIELD)
public class Login {
	@NotNull(message = "Username can not be null")
	@Size(min = 1, message = "Username can not be empty")
	@XmlElement(nillable = false, required =  true)
	private String username;
	
	@NotNull(message = "Password can not be null")
	@Size(min = 1, message = "Password can not be empty")
	@XmlElement(nillable = false, required =  true)
	private String password;
	
	Login() {}
	
	public Login(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = requireNonEmpty(username, "Username can not be null or empty");
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = requireNonEmpty(password, "Password can not be null or empty");
	}
}
