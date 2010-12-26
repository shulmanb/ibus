package com.ibus.navigation.dijkstra;

import java.util.List;

import com.ibus.map.Node;
import com.ibus.navigation.dijkstra.DenseRoutesMap;
import com.ibus.navigation.dijkstra.RoutesMap;

import junit.framework.TestCase;

public class DenseRoutesMapTest extends TestCase {
	private RoutesMap map;
	private final static Node NodeA = new Node("A",0);
	private final static Node NodeB = new Node("B",1);
	private final static Node NodeC = new Node("C",2);
	private final static Node NodeD = new Node("D",3);
	private final static Node NodeE = new Node("E",4);
	private final static Node NodeF = new Node("F",5);

	public DenseRoutesMapTest(String name) {
		super(name);
	}

	protected void setUp() {
		map = new DenseRoutesMap(5);
		map.addVertex(NodeA, NodeB, 5);
		map.addVertex(NodeA, NodeC, 3);
		map.addVertex(NodeA, NodeD, 2);
		map.addVertex(NodeE, NodeC, 1);
	}

	public void testDistance() {
		assertDistanceEquals(NodeA, NodeB, 5);
		assertDistanceEquals(NodeA, NodeD, 2);
		assertDistanceEquals(NodeE, NodeC, 1);
	}

	public void testDefautDistance() {
		assertDistanceEquals(NodeA, NodeA, Integer.MAX_VALUE);
		assertDistanceEquals(NodeD, NodeE, Integer.MAX_VALUE);
	}

	private void assertDistanceEquals(Node start, Node stop,
			int expectedDistance) {
		assertEquals("wrong distance", expectedDistance,
				map.getWeight(start, stop));
	}

	public void testDestinations() {
		List<Node> l = map.getDestinations(NodeA);

		assertEquals("incorrect number of destinations", 3, l.size());
		assertSame(NodeB, l.get(0));
		assertSame(NodeC, l.get(1));
		assertSame(NodeD, l.get(2));
	}

	public void testPredecessors() {
		List<Node> l = map.getPredecessors(NodeA);
		assertEquals("incorrect number of predecessors", 0, l.size());

		l = map.getPredecessors(NodeC);
		assertEquals("incorrect number of predecessors", 2, l.size());
		assertSame(NodeA, l.get(0));
		assertSame(NodeE, l.get(1));
	}

	public void testInverse() {
		map = map.getInverse();

		assertDistanceEquals(NodeB, NodeA, 5);
		assertDistanceEquals(NodeD, NodeA, 2);
		assertDistanceEquals(NodeC, NodeE, 1);

		assertDistanceEquals(NodeA, NodeB, Integer.MAX_VALUE);
		assertDistanceEquals(NodeA, NodeD, Integer.MAX_VALUE);
		assertDistanceEquals(NodeE, NodeC, Integer.MAX_VALUE);
	}
}
