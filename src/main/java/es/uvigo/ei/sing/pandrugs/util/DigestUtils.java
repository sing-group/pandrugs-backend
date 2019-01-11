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

package es.uvigo.ei.sing.pandrugs.util;

import static java.util.Objects.requireNonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class DigestUtils {
	private DigestUtils() {}
	
	public static String md5Digest(String text) {
		requireNonNull(text);
		
		try {
			final MessageDigest digester = MessageDigest.getInstance("MD5");
			
			final byte[] digested = digester.digest(text.getBytes());
			
			final StringBuilder sb = new StringBuilder();
			for (byte db : digested) {
				sb.append(String.format("%02x", db));
			}
			
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
