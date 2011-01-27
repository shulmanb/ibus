package com.ibus.mapbuilder;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibus.map.Point;

public class SubmapIdGeneratorTest {
	SubmapIdGenerator idg = new SubmapIdGenerator(new Point(29,1), new Point(30,0), 5, "ID");

	@Test
	public void testGetSubmapIdForPoint_LastSquare() {
		String id = idg.getSubmapIdForPoint(new Point(30,0));
		assertEquals("ID_"+22*22, id);
		//lat long distances at 0 in meters: 110570,111320
	}
	@Test
	public void testGetSubmapIdForPoint_FirstSquare() {
		String id = idg.getSubmapIdForPoint(new Point(29,1));
		assertEquals("ID_"+1, id);
		//lat long distances at 0 in meters: 110570,111320
	}
	@Test
	public void testGetSubmapIdForPoint_FirstLineMiddleSquare() {
		String id = idg.getSubmapIdForPoint(new Point(29.5,1));
		assertEquals("ID_"+12, id);
		//lat long distances at 0 in meters: 110570,111320
	}

}
