package com.ibus.navigation.dijkstra;

import com.ibus.map.Node;
import com.ibus.map.Point;
import com.ibus.map.utils.MapUtils;
import com.ibus.navigation.dijkstra.Route;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

import junit.framework.TestCase;

public class RouteTest extends TestCase {
	private final static Node NodeA = new Node("A",0);
	private final static Node NodeB = new Node("B",1);
	private final static Node NodeC = new Node("C",2);
	private final static Node NodeD = new Node("D",3);
	private final static Node NodeE = new Node("E",4);
	private final static Node NodeF = new Node("F",5);

	
	public RouteTest(String name) {
		super(name);
	}

	public void testNoStops() {
		Route route = new Route();
		route.addNode(NodeA, 0);

		assertRoute(route, NodeA, 0, 0);
		assertTrue("route should have start city", route.hasNode(NodeA));
	}

	public void testAddStop() {
		Route route = new Route();
		route.addNode(NodeA, 0);
		route.addNode(NodeB, 3);
		route.addNode(NodeC, 2);
		route.addNode(NodeE, 1);

		assertRoute(route, NodeE, 6, 3);

		assertTrue("city should be present", route.hasNode(NodeA));
		assertTrue("city should be present", route.hasNode(NodeB));
	}

	static void assertRoute(Route route, Node expectedLastStop,
			int expectedDistance, int expectedLength) {
		assertSame("incorrect last stop", expectedLastStop, route.getLastNode());
		assertEquals("incorrect distance", expectedDistance,
				route.getWeight());
		assertEquals("incorrect length", expectedLength, route.getLength());
	}
	
//	public void testUnits(){
//		//x = longitude, y = latitude
//		
//		Point p = new Point(35.01,32.01);
//		System.out.println(p.distnaceFrom(new Point(35.02,32.02))*1000*0.000009);
//		
//		Rectangle rect = new Rectangle();
//		rect.set((float)35.01384, (float)32.775963, (float)35.01404,(float)32.775557);
//		System.out.println(MapUtils.degreesDistanceInMeters(
//				rect.distance(new com.infomatiq.jsi.Point((float)35.01318,(float)32.773807)),32));
//
//		rect.set((float)-74.030868,(float)40.767794, (float)-74.030849, (float)40.767759);
//		System.out.println(MapUtils.degreesDistanceInMeters(
//				rect.distance(new com.infomatiq.jsi.Point((float)-74.030457,(float)40.768385)),40));
//
//	
//	}
}
