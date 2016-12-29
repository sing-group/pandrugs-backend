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
package es.uvigo.ei.sing.pandrugsdb.controller;

import javax.inject.Inject;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.ei.sing.pandrugsdb.mail.Mailer;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.RegistrationDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.UserDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Registration;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.util.DigestUtils;

@Controller
@Transactional
@Lazy
public class DefaultRegistrationController
implements RegistrationController {
	@Inject
	private RegistrationDAO registrationDAO;
	
	@Inject
	private UserDAO userDAO;
	
	@Inject
	private Mailer mailer;
	
	@Override
	public Registration register(String login, String email, String password) {
		final Registration registration = doRegistration(login, email, password);

		mailer.sendConfirmSingUp(email, login, registration.getUuid());
		
		return registration;
	}

	@Override
	public Registration register(String login, String email, String password, String urlTemplate) {
		final Registration registration = doRegistration(login, email, password);

		mailer.sendConfirmSingUp(email, login, registration.getUuid(), urlTemplate);

		return registration;
	}

	private Registration doRegistration(String login, String email, String password) {
		checkIfUserAlreadyExists(login, email);

		checkIfRegistrationAlreadyExists(login);

		deleteRegistrationWithEmail(email);

		return registrationDAO.persist(login, email, DigestUtils.md5Digest(password));
	}



	@Override
	public User confirm(String uuid) {
		final Registration registration = registrationDAO.get(uuid);
		
		if (registration == null) {
			throw new IllegalArgumentException("Invalid UUID");
		} else {
			registrationDAO.remove(registration);
			
			return userDAO.registerNormalUser(registration.getLogin(), registration.getEmail(), registration.getPassword());
		}
	}

	protected void checkIfUserAlreadyExists(String login, String email) {
		if (userDAO.get(login) != null)
			throw new IllegalArgumentException("Login already exists");
		if (userDAO.getByEmail(email) != null)
			throw new IllegalArgumentException("Email already exists");
	}
	
	protected void checkIfRegistrationAlreadyExists(String login) {
		if (registrationDAO.get(login) != null)
			throw new IllegalArgumentException("A registration with the login already exists");
	}
	
	protected void deleteRegistrationWithEmail(String email) {
		final Registration registration = this.registrationDAO.getByEmail(email);
		
		if (registration != null) {
			registrationDAO.remove(registration);
		}
	}
}
