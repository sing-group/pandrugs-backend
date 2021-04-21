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

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;
import java.util.stream.Stream;


public final class CompareCollections {
	private CompareCollections() {
	}

	public static boolean equalsIgnoreOrder(Iterable<?> i1, Iterable<?> i2) {
		if (i1 == null && i2 == null) {
			return true;
		} else if (i1 == null || i2 == null) {
			return false;
		} else {
			final List<?> l1 = stream(i1.spliterator(), false)
				.collect(toList());
			
			return stream(i2.spliterator(), false).allMatch(l1::remove) && l1.isEmpty();
		}
	}

	public static boolean equalsIgnoreOrder(Object[] i1, Object[] i2) {
		if (i1 == null && i2 == null) {
			return true;
		} else if (i1 == null || i2 == null) {
			return false;
		} else {
			final List<?> l1 = Stream.of(i1)
				.collect(toList());
			
			return Stream.of(i2).allMatch(l1::remove) && l1.isEmpty();
		}
	}
}
