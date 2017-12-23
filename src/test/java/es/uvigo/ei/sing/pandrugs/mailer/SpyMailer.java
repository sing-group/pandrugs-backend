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
package es.uvigo.ei.sing.pandrugs.mailer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import es.uvigo.ei.sing.pandrugs.mail.Mailer;
import es.uvigo.ei.sing.pandrugs.mail.MailerException;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreUserComputation;

public class SpyMailer implements Mailer {
	private final Map<String, AtomicInteger> counts;
	
	private SpyMailer() {
		this.counts = new HashMap<>();
	}
	
	@Override
	public void sendConfirmSingUp(String to, String username, String url)
	throws MailerException {
		this.counts.putIfAbsent("sendConfirmSingUp", new AtomicInteger(0));
		this.counts.get("sendConfirmSingUp").incrementAndGet();
	}

	@Override
	public void sendConfirmSingUp(String to, String username, String uuid, String urlTemplate) throws MailerException {
		this.counts.putIfAbsent("sendConfirmSingUp", new AtomicInteger(0));
		this.counts.get("sendConfirmSingUp").incrementAndGet();
	}

	@Override
	public void sendComputationFinished(VariantsScoreUserComputation userComputation) throws MailerException {
		this.counts.putIfAbsent("sendComputationFinished", new AtomicInteger(0));
		this.counts.get("sendComputationFinished").incrementAndGet();
	}
}
