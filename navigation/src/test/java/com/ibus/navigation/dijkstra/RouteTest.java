package com.ibus.navigation.dijkstra;

import com.ibus.map.Node;
import com.ibus.navigation.dijkstra.Route;

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
}
