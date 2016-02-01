/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 - 2016 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
package es.uvigo.ei.sing.pandrugsdb.util;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public final class StringFormatter {
	private final List<Function<String, String>> transformations;
	
	private StringFormatter(List<Function<String, String>> transformations) {
		this.transformations = transformations;
	}
	
	public static StringFormatterBuiler newStringFormatter() {
		return new StringFormatterBuiler();
	}
	
	public static class StringFormatterBuiler {
		private final List<Function<String, String>> transformations;
		
		private StringFormatterBuiler() {
			this.transformations = new LinkedList<>();
		}

		public StringFormatterBuiler toTitleCase() {
			this.transformations.add(text -> stream(text.split(" "))
				.map(String::toLowerCase)
				.map(token -> Character.toUpperCase(token.charAt(0)) + token.substring(1))
			.collect(joining(" ")));
			
			return this;
		}
		
		public StringFormatterBuiler toLowerCase() {
			this.transformations.add(String::toLowerCase);
			
			return this;
		}
		
		public StringFormatterBuiler toUpperCase() {
			this.transformations.add(String::toUpperCase);
			
			return this;
		}
		
		public StringFormatterBuiler replaceAll(String regex, String replacement) {
			this.transformations.add(text -> text.replace(regex, replacement));
			
			return this;
		}
		
		public StringFormatter build() {
			return new StringFormatter(this.transformations);
		}
	}
	
	public String format(String text) {
		for (Function<String, String> transformation : this.transformations) {
			text = transformation.apply(text);
		}
		
		return text;
	}
}
