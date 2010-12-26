package com.ibus.navigation.map;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibus.map.Point;
import com.ibus.map.StopsRoute;
import com.ibus.navigation.map.db.SimpleDBMapLoader;

public class SimpleDBMapFactoryTester {

	LinesMapFactory factory = new LinesMapFactory();
	@Before
	public void setUp() throws Exception {
		SimpleDBMapLoader ml = new SimpleDBMapLoader("AKIAIJPN5YYDFNRZHUSQ", "0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB");
		factory.setMapLoader(ml);
	}

	@SuppressWarnings("unused")
	@Test
	public void testLoadMap() {
		LinesMap map = factory.loadMap("il_yoqneam");
		StopsRoute sr = map.findRoute(new Point(35.1042,32.6641), new Point(35.0891,32.6477));
	}

}
