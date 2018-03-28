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

package es.uvigo.ei.sing.pandrugs.util;

import static es.uvigo.ei.sing.pandrugs.util.StringFormatter.newStringFormatter;

import java.util.Arrays;

public final class StringJoiner {
	private String[] strings;
	private String separator;
	private String lastSeparator;
	private StringFormatter formatter;
	
	private StringJoiner(String[] strings) {
		this.strings = strings;
		this.formatter = newStringFormatter().build();
		this.separator = "";
		this.lastSeparator = "";
	}
	
	public static StringJoiner join(String ... strings) {
		return new StringJoiner(strings);
	}
	
	public StringJoiner withSeparator(String separator) {
		this.separator = separator;
		
		return this;
	}
	
	public StringJoiner withLastSeparator(String lastSeparator) {
		this.lastSeparator = lastSeparator;
		
		return this;
	}
	
	public StringJoiner withFormatter(StringFormatter formatter) {
		this.formatter = formatter;
		
		return this;
	}
	
	public String andGet() {
		final String[] strings = Arrays.stream(this.strings)
			.map(this.formatter::format)
		.toArray(String[]::new);
		
		if (strings.length > 1 && !lastSeparator.equals(separator)) {
			final String[] allButLast = new String[strings.length - 1];
			System.arraycopy(strings, 0, allButLast, 0, allButLast.length);

			return String.join(separator, allButLast)
				+ lastSeparator + strings[strings.length - 1];
		} else {
			return String.join(separator, strings);
		}
	}
}
