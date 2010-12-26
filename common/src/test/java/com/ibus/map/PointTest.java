package com.ibus.map;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibus.map.Point;

public class PointTest {

	@Test
	public void testEqualsObject() {
		Point a = new Point(1,1);
		Point b = new Point(1,1);
		Point c = new Point(1,2);
		Point d = new Point(2,1);
		
		assertTrue(a.equals(a));
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		assertTrue(!c.equals(d));
		assertTrue(!c.equals(a));
		assertTrue(!d.equals(a));
		assertTrue(!d.equals("jjj"));
	}

	@Test
	public void testDistnaceFrom() {
		Point a = new Point(1,1);
		Point b = new Point(0,0);
		assertEquals(0,a.distnaceFrom(a),0);
		assertEquals(157.426,a.distnaceFrom(b),0.0002);
	}

		
	@Test
	public void testCompareTo() {
		Point a = new Point(1,1);
		Point b = new Point(0,0);
		assertEquals(0,a.compareTo(a));
		assertEquals(157426,a.compareTo(b));
	}

	@Test
	public void testRound(){
		Point p = new Point(35.104279, 32.664129);
		Point p1 = new Point(35.1042, 32.6641);
		assertTrue(p.distnaceFrom(p.round()) < 20);
		assertEquals(0,p.round().distnaceFrom(p1),0);
		assertEquals(p.round(), p1);
	}
	
	@Test
	public void distanceTest_RealPoints(){
		Point p = new Point(35.103945,32.660069);
		Point p1 = new Point(35.103659, 32.659668);
		System.out.println(p.distnaceFrom(p1)*1000);
		System.out.println(p.distnaceFrom(p1.round())*1000);
	}
}
