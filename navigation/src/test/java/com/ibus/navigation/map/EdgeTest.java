package com.ibus.navigation.map;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibus.map.Point;
import com.ibus.map.Stop;

public class EdgeTest {

	@Test
	public void testEqualsObject() {
		Stop s1 = new Stop(new Point(0,0));
		Stop s2 = new Stop(new Point((float)0.5,(float)0.5));
		Stop s3 = new Stop(new Point(1,1));
		Stop s4 = new Stop(new Point((float)1.5,(float)1.5));
		Edge e1 = new Edge(s1,s2,"l1");
		Edge e12 = new Edge(s1,s2,"l1");
		Edge e11 = new Edge(s1,s2,"l11");
		Edge e2 = new Edge(s3,s4,"l2");
		assertTrue(e1.equals(e1));
		assertTrue(!e1.equals(null));
		assertTrue(!e1.equals(e2));
		assertTrue(!e1.equals(e11));
		assertTrue(e1.equals(e12));
	}

}
