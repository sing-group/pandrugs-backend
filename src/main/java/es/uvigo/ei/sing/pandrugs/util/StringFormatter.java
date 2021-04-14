/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

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
				.map(StringFormatter::capitalize)
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
	
	public static String[] toUpperCase(String ... strings) {
		return toUpperCase(stream(strings));
	}
	
	public static String[] toUpperCase(Collection<String> strings) {
		return toUpperCase(strings.stream());
	}
	
	public static String[] toUpperCase(Stream<String> strings) {
		return strings.map(String::toUpperCase).toArray(String[]::new);
	}
	
	public static String capitalize(String text) {
		if (text.isEmpty()) return text;
		else if (text.length() == 1) return text.toUpperCase();
		else return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
	}
	
	public String format(String text) {
		for (Function<String, String> transformation : this.transformations) {
			text = transformation.apply(text);
		}
		
		return text;
	}
}
