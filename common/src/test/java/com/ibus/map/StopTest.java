package com.ibus.map;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibus.map.Node;
import com.ibus.map.Point;
import com.ibus.map.Stop;

public class StopTest {

	@Test
	public void testGetNodes() {
		//test that received collection is immutable
		Stop s = new Stop("d", new Point(0,0));
		Exception e = null;
		try{
			s.getNodes().add(new Node("hhh",1));
		}catch (Exception e1) {
			e = e1;
		}
		assertNotNull(e);
		assertEquals(0, s.getNodes().size());
	}

	@Test
	public void testAddNode() {
		Stop s = new Stop("d", new Point(0,0));
		s.addNode(new Node("",1));
		assertEquals(1, s.getNodes().size());
	}

	@Test
	public void testRemoveNode() {
		Stop s = new Stop("d", new Point(0,0));
		s.addNode(new Node("a",1));
		s.addNode(new Node("b",1));
		s.removeNode("a");
		assertEquals(1, s.getNodes().size());
	}

	@Test
	public void testEqualsObject() {
		Stop s = new Stop("d", new Point(0,0));
		Stop s1 = new Stop("c", new Point(0,0));
		Stop s2 = new Stop("d", new Point(1,1));
		assertTrue(s.equals(s));
		assertTrue(!s.equals(null));
		assertTrue(!s.equals("kkkk"));
		assertTrue(s.equals(s1));
		assertTrue(!s.equals(s2));
	}

}
