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
package es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

public class HasTheSameItemsAsMatcher<T> extends TypeSafeMatcher<Collection<T>> {
	private final Collection<T> expected;

	public HasTheSameItemsAsMatcher(Collection<T> expected) {
		this.expected = expected;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(this.expected);
	}

	@Override
	protected boolean matchesSafely(Collection<T> items) {
		final Set<T> expectedItemsSet = new HashSet<>(expected);
		final Set<T> itemsSet = new HashSet<>(items);
		
		return expectedItemsSet.equals(itemsSet);
	}
	
	@Factory
	public static <T> HasTheSameItemsAsMatcher<T> hasTheSameItemsAs(Collection<T> expected) {
		return new HasTheSameItemsAsMatcher<T>(expected);
	}
	
	@Factory
	@SafeVarargs
	public static <T> HasTheSameItemsAsMatcher<T> hasExactlyTheItems(T ... expected) {
		return new HasTheSameItemsAsMatcher<T>(asList(expected));
	}
}
