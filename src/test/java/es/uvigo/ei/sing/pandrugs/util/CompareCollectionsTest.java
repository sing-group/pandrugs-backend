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

import static es.uvigo.ei.sing.pandrugs.util.CompareCollections.equalsIgnoreOrder;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class CompareCollectionsTest {
	@Test
	public void testEqualsIgnoreOrderArray() {
		final String[] a1 = new String[] {"One", "Two", "Three"};
		final String[] a2 = new String[] {"Two", "One", "Three"};
		
		assertTrue(equalsIgnoreOrder(a1, a2));
	}
	
	@Test
	public void testEqualsIgnoreOrderList() {
		final List<String> l1 = asList("One", "Two", "Three");
		final List<String> l2 = asList("Two", "One", "Three");
		
		assertTrue(equalsIgnoreOrder(l1, l2));
	}
	
	@Test
	public void testEqualsIgnoreOrderSet() {
		final Set<String> s1 = new HashSet<>(asList("One", "Two", "Three"));
		final Set<String> s2 = new HashSet<>(asList("Two", "One", "Three"));
		
		assertTrue(equalsIgnoreOrder(s1, s2));
	}
	
	@Test
	public void testEqualsIgnoreOrderMixed() {
		final List<String> l1 = asList("One", "Two", "Three");
		final Set<String> s2 = new HashSet<>(asList("Two", "One", "Three"));
		
		assertTrue(equalsIgnoreOrder(l1, s2));
	}
	
	@Test
	public void testEqualsIgnoreOrderArraySameOrder() {
		final String[] a1 = new String[] {"One", "Two", "Three"};
		final String[] a2 = new String[] {"One", "Two", "Three"};
		
		assertTrue(equalsIgnoreOrder(a1, a2));
	}
	
	@Test
	public void testEqualsIgnoreOrderListSameOrder() {
		final List<String> l1 = asList("One", "Two", "Three");
		final List<String> l2 = asList("One", "Two", "Three");
		
		assertTrue(equalsIgnoreOrder(l1, l2));
	}
	
	@Test
	public void testEqualsIgnoreOrderArrayEmpty() {
		final String[] a1 = new String[0];
		final String[] a2 = new String[0];
		
		assertTrue(equalsIgnoreOrder(a1, a2));
	}
	
	@Test
	public void testEqualsIgnoreOrderListEmpty() {
		final List<String> l1 = new ArrayList<>();
		final List<String> l2 = new ArrayList<>();
		
		assertTrue(equalsIgnoreOrder(l1, l2));
	}
	
	@Test
	public void testEqualsIgnoreOrderSetEmpty() {
		final Set<String> s1 = new HashSet<>();
		final Set<String> s2 = new HashSet<>();
		
		assertTrue(equalsIgnoreOrder(s1, s2));
	}
	
	@Test
	public void testEqualsIgnoreOrderMixedEmpty() {
		final Set<String> s1 = new HashSet<>();
		final List<String> l2 = new ArrayList<>();
		
		assertTrue(equalsIgnoreOrder(s1, l2));
	}
	
	@Test
	public void testEqualsIgnoreOrderArrayDifferent() {
		final String[] a1 = new String[] {"One", "Two", "Three"};
		final String[] a2 = new String[] {"Two", "One"};
		
		assertFalse(equalsIgnoreOrder(a1, a2));
	}
	
	@Test
	public void testEqualsIgnoreOrderListDifferent() {
		final List<String> l1 = asList("One", "Two", "Three");
		final List<String> l2 = asList("Two", "One");
		
		assertFalse(equalsIgnoreOrder(l1, l2));
	}
	
	@Test
	public void testEqualsIgnoreOrderSetDifferent() {
		final Set<String> l1 = new HashSet<>(asList("One", "Two", "Three"));
		final Set<String> l2 = new HashSet<>(asList("Two", "One"));
		
		assertFalse(equalsIgnoreOrder(l1, l2));
	}
	
	@Test
	public void testEqualsIgnoreOrderMixedDifferent() {
		final List<String> l1 = asList("One", "Two", "Three");
		final Set<String> l2 = new HashSet<>(asList("Two", "One"));
		
		assertFalse(equalsIgnoreOrder(l1, l2));
	}
	
	@Test
	public void testEqualsIgnoreOrderNullIterable1() {
		assertFalse(equalsIgnoreOrder(null, asList("One", "Two")));
	}
	
	@Test
	public void testEqualsIgnoreOrderNullIterable2() {
		assertFalse(equalsIgnoreOrder(asList("One", "Two"), null));
	}
	
	@Test
	public void testEqualsIgnoreOrderNullArray1() {
		assertFalse(equalsIgnoreOrder(null, new String[] {"One", "Two"}));
	}
	
	@Test
	public void testEqualsIgnoreOrderNullArray() {
		assertFalse(equalsIgnoreOrder(new String[] {"One", "Two"}, null));
	}
}
