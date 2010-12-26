package com.ibus.navigation;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibus.map.Node;

public class NodeTest {

	@Test
	public void testEqualsObject() {
		Node a = new Node("t",1);
		assertTrue(a.equals(a));
		Node b = new Node("r",1);
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		Node c = new Node("t",2);
		assertTrue(!a.equals(c));
	}

	@Test
	public void testCompareTo() {
		Node a = new Node("t",2);
		Node b = new Node("t",1);
		assertEquals(1,a.compareTo(b));
	}

}
