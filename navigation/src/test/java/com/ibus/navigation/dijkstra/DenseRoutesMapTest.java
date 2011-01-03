package com.ibus.navigation.dijkstra;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public void testJoinMapNoMutualNodes(){
		DenseRoutesMap map1 = new DenseRoutesMap(3);
		Node NodeA1 = new Node("A1",0);
		Node NodeB1 = new Node("B1",1);
		Node NodeC1 = new Node("C1",2);
		
		map1.addVertex(NodeA1, NodeB1, 5);
		map1.addVertex(NodeA1, NodeC1, 3);
		int size = map.join(map1,null);
		assertEquals(8, size);
		assertDistanceEquals(NodeA, NodeB, 5);
		assertDistanceEquals(NodeA, NodeD, 2);
		assertDistanceEquals(NodeE, NodeC, 1);
		List<Node> dest = map.getDestinations(NodeA);
		assertEquals(3, dest.size());
		//the new ID of A1 is 5
		List<Node> dest1 = map.getDestinations(new Node("A1",5));
		assertEquals(2, dest1.size());
	}

	public void testJoinMapWithMutualNodes(){
		DenseRoutesMap map1 = new DenseRoutesMap(3);
		Node NodeE1 = new Node("E1",2);
		Node NodeB1 = new Node("B1",1);
		Node NodeC1 = new Node("C1",0);
		
		map1.addVertex(NodeE1, NodeB1, 5);
		map1.addVertex(NodeE1, NodeC1, 3);
		Map<Integer, Integer> mutual = new HashMap<Integer, Integer>();
		mutual.put(2, 4);
		int size = map.join(map1, mutual);
		assertEquals(7, size);
		assertDistanceEquals(NodeA, NodeB, 5);
		assertDistanceEquals(NodeA, NodeD, 2);
		assertDistanceEquals(NodeE, NodeC, 1);
		List<Node> dest = map.getDestinations(NodeE);
		assertEquals(3, dest.size());
	}

}
