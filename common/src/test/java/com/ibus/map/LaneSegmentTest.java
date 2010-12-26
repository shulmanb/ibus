package com.ibus.map;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibus.map.LineSegment;
import com.ibus.map.Point;
import com.ibus.map.Stop;

public class LaneSegmentTest {

	@Test
	public void testAddTracePoint() {
		LineSegment ls = new LineSegment("1", new Stop("a", new Point(0,0)), new Stop("b", new Point(1,1)));
		ls.addPoint(new TimedPoint(0,0,0));
		ls.addPoint(new TimedPoint(1,1,1), 0);
		assertEquals(2,ls.getPoints().size());
		assertEquals(new TimedPoint(0,0,0),ls.getPoints().get(1));
	}

}
