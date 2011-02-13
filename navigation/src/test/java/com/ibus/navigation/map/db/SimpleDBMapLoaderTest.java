package com.ibus.navigation.map.db;

import org.junit.Ignore;
import org.junit.Test;

import com.ibus.map.LineSegment;
import com.ibus.map.Stop;
import com.ibus.navigation.map.db.IMapDBLoader.NodesGraph;


public class SimpleDBMapLoaderTest {
	
	SimpleDBMapLoader ml = new SimpleDBMapLoader("AKIAIJPN5YYDFNRZHUSQ", "0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB");
	
	@Test @Ignore
	public void testGetStops() {
		Stop[] stops = ml.getStops("il_yoqneam");
	}

	@Test @Ignore
	public void testGetLineSegments() {
		LineSegment[] segments = ml.getLineSegments("il_yoqneam");
	}
	
	@Test @Ignore
	public void testGetRoutesMap() {
		NodesGraph grph = ml.getRoutesMap("il_yoqneam");
		Stop[] stops = ml.getStops("il_yoqneam");
		
		
	}

	

}
