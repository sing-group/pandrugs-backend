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
package es.uvigo.ei.sing.pandrugsdb.mail;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import es.uvigo.ei.sing.pandrugsdb.Configuration;

@Singleton
public class DefaultMailer implements Mailer {
	@Inject
	@Named("mailSession")
	private Session session;
	
	@Inject
	@Named("configuration")
	private Configuration configuration;
	
	protected void send(String from, String to, String subject, String content, String contentType)
	throws MailerException {
		try {
			final MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setContent(content, contentType);
			
			Transport.send(message);
		} catch (MessagingException e) {
			throw new MailerException(e);
		}
	}
	
	@Override
	public void sendConfirmSingUp(String to, String username, String uuid)
	throws MailerException {
		final String url =  configuration.getServerURL() + "/public/registration/" + uuid;
		
		this.send(this.configuration.getEmailFrom(), 
			to, "PanDrugsDB registration confirmation",
			String.format("Hi %s," +
				"<p>In order to confirm your PanDrugsDB registration, please, click on the following link: " +
				"<a href=\"%s\">%s</a>.<p/>" +
				"<p>Thanks for your registration,<br/><br/>" +
				"The PanDrugsDB Team</p>",
				username, url, url
			), 
			"text/html"
		);
	}
}
