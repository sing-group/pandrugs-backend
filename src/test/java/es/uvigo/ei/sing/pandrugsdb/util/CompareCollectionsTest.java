package es.uvigo.ei.sing.pandrugsdb.util;

import static es.uvigo.ei.sing.pandrugsdb.util.CompareCollections.equalsIgnoreOrder;
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
