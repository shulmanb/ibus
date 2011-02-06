package com.ibus.navigation.map.db;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopDetails;

//@Ignore
public class SimpleDbQuery {
	SimpleDBMapQuery mapQuery = new SimpleDBMapQuery("AKIAIJPN5YYDFNRZHUSQ", "0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB");
	
	@Test @Ignore
	public void testGetStationsForLane(){
 		Stop[] stops = mapQuery.getStationsForLine("e5310bc6-e58e-4bf7-830c-fa0fbf679133");
		assertEquals(8, stops.length);
	}

	@Test @Ignore
	public void testGetLanePoints(){
		Point[] points = mapQuery.getLinePoints("e5310bc6-e58e-4bf7-830c-fa0fbf679133");
		assertEquals(366, points.length);
	}
	
	@Test @Ignore
	public void testGetStopDetails(){
		StopDetails stop = mapQuery.getStopDetails("31.804035_34.77663");
		assertEquals(35.08914, stop.getLongitude(),0.00001);
	}
	
	@Test @Ignore
	public void testGetAllStationsInSubmap(){
		StopDetails[] stops = mapQuery.getAllStationsInSubmap("il_yoqneam");
		assertEquals(13, stops.length);
	}
	
	@Test @Ignore
	public void testGetStationsInArea(){
		Point left = new Point((float)35.066067,(float)32.664127);
		Point right = new Point((float)35.10428,(float)32.633503);
		StopDetails[] stops = mapQuery.getStationsInArea("il_yoqneam", left, right);
		assertEquals(8, stops.length);
		
	}
	
	@Test @Ignore
	public void testDeleteLine(){
		mapQuery.deleteLineById("a415a92b-7c20-4e5a-a796-c574873141ef");
	}
	@Test
	public void testGetLinesInSubmap(){
		mapQuery.getLinesInSubmap("il");
	}
	
}
