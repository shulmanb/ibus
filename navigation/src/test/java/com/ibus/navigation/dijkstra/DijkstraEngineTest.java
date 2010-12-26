package com.ibus.navigation.dijkstra;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ibus.map.Node;
import com.ibus.navigation.dijkstra.DijkstraEngine;
import com.ibus.navigation.dijkstra.RoutesMap;

import junit.framework.TestCase;

/**
 * DijkstraEngineTest to validate the DijkstraEngine class.
 * 
 */
public class DijkstraEngineTest extends TestCase {

	private final static Node NodeA = new Node("A",0);
	private final static Node NodeB = new Node("B",1);
	private final static Node NodeC = new Node("C",2);
	private final static Node NodeD = new Node("D",3);
	private final static Node NodeE = new Node("E",4);
	private final static Node NodeF = new Node("F",5);

	private static class MockMap4 implements RoutesMap {
		public void addVertex(Node start, Node end, int distance) {
			throw new UnsupportedOperationException();
		}

		public int getWeight(Node start, Node end) {
			if (start == NodeA) {
				if (end == NodeB)
					return 5;
				if (end == NodeC)
					return 20;
			} else if (start == NodeB) {
				if (end == NodeD)
					return 1;
			} else if (start == NodeC) {
				if (end == NodeD)
					return 3;
				if (end == NodeE)
					return 1;
			} else if (start == NodeD) {
				if (end == NodeE)
					return 100;
			}

			return DijkstraEngine.INFINITE_DISTANCE;
		}

		public List<Node> getDestinations(Node node) {
			if (node == NodeA) {
				return Arrays.asList(new Node[] { NodeB, NodeC});
			} else if (node == NodeB) {
				return Collections.singletonList(NodeD);
			} else if (node == NodeC) {
				return Arrays.asList(new Node[] { NodeD, NodeE});
			} else if (node == NodeD) {
				return Collections.singletonList(NodeE);
			} else {
				return Collections.emptyList();
			}
		}

		public List<Node> getPredecessors(Node node) {
			throw new UnsupportedOperationException();
		}

		public RoutesMap getInverse() {
			throw new UnsupportedOperationException();
		}
	};

	
	
	
	
	
	/**
	 * This test was used to test the issue brought forth by Marc Barry. This
	 * graph is used in my Dijkstra article.
	 */
	private static class MockMap3 implements RoutesMap {
		private final boolean withNodeE;
		public MockMap3(boolean withNodeE) {
			this.withNodeE = withNodeE;
		}

		public void addVertex(Node start, Node end, int distance) {
			throw new UnsupportedOperationException();
		}

		public int getWeight(Node start, Node end) {
			if (start == NodeA) {
				if (end == NodeB)
					return 4;
				if (end == NodeC)
					return 2;
				if (withNodeE && end == NodeE)
					return 1;
			} else if (start == NodeB) {
				if (end == NodeD)
					return 1;
				if (end == NodeC)
					return 3;
			} else if (start == NodeC) {
				if (end == NodeA)
					return 2;
				if (end == NodeB)
					return 1;
				if (end == NodeD)
					return 5;
			} else if (withNodeE && start == NodeE) {
				if (end == NodeD)
					return 2;
			}

			return DijkstraEngine.INFINITE_DISTANCE;
		}

		public List<Node> getDestinations(Node node) {
			if (node == NodeA) {
				return Arrays.asList((withNodeE) ? new Node[] { NodeB, NodeC,
						NodeE } : new Node[] { NodeB, NodeC });
			} else if (node == NodeB) {
				return Arrays.asList(new Node[] { NodeD, NodeC });
			} else if (node == NodeC) {
				return Arrays.asList(new Node[] { NodeA, NodeB, NodeD });
			} else if (withNodeE && node == NodeE) {
				return Collections.singletonList(NodeD);
			}

			return Collections.emptyList();
		}

		public List<Node> getPredecessors(Node city) {
			throw new UnsupportedOperationException();
		}

		public RoutesMap getInverse() {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * This map was used to test and validate the fix for the issue brought
	 * forth by Carl Schwarcz: the <code>Comparator</code> object is used by the
	 * SortedSet for object ordering AND identity. My implementation incorrectly
	 * reported some nodes as equal.
	 */
	private static class MockMap2 implements RoutesMap {
		public void addVertex(Node start, Node end, int distance) {
			throw new UnsupportedOperationException();
		}

		public int getWeight(Node start, Node end) {
			if (start == NodeA) {
				if (end == NodeB)
					return 2;
				if (end == NodeC)
					return 7;
				if (end == NodeD)
					return 8;
				if (end == NodeE)
					return 9;
			} else if (start == NodeB) {
				return 3;
			} else if (start == NodeE) {
				return 1;
			}

			return DijkstraEngine.INFINITE_DISTANCE;
		}

		public List<Node> getDestinations(Node node) {
			if (node == NodeA) {
				return Arrays.asList(new Node[] { NodeB, NodeC, NodeD,
						NodeE });
			} else if (node == NodeB) {
				return Collections.singletonList(NodeE);
			} else if (node == NodeE) {
				return Collections.singletonList(NodeF);
			} else {
				return Collections.emptyList();
			}
		}

		public List<Node> getPredecessors(Node city) {
			throw new UnsupportedOperationException();
		}

		public RoutesMap getInverse() {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * Test map.
	 * 
	 * <pre>
	 *     4
	 * A -----> B 
	 * \        \ 
	 *  \ 4      \ 2
	 *   \        \
	 *    \/       \/
	 *     C -----> D 
	 *         1
	 * </pre>
	 */
	private static class MockMap1 implements RoutesMap {
		public void addVertex(Node start, Node end, int distance) {
			throw new UnsupportedOperationException();
		}

		public int getWeight(Node start, Node end) {
			if (start == NodeA) {
				return 4;
			} else if (start == NodeB) {
				return 2;
			} else if (start == NodeC) {
				return 1;
			} else {
				return DijkstraEngine.INFINITE_DISTANCE;
			}
		}

		public List<Node> getDestinations(Node city) {
			if (city == NodeA) {
				return Arrays.asList(new Node[] { NodeB, NodeC });
			} else if (city == NodeB || city == NodeC) {
				return Collections.singletonList(NodeD);
			} else {
				return Collections.emptyList();
			}
		}

		public List<Node> getPredecessors(Node city) {
			throw new UnsupportedOperationException();
		}

		public RoutesMap getInverse() {
			throw new UnsupportedOperationException();
		}
	};

	public DijkstraEngineTest(String name) {
		super(name);
	}

	private DijkstraEngine engine;

	/**
	 * Test the <code>MockMap1</code>.
	 */
	public void testEngine1() {
		engine = new DijkstraEngine(new MockMap1());
		engine.execute(NodeA, null);

		// sanity checks
		assertPredecessorAndShortestDistance(NodeA, null, 0);
		assertPredecessorAndShortestDistance(NodeE, null,
				DijkstraEngine.INFINITE_DISTANCE);

		// shortest path from A to D
		assertPredecessorAndShortestDistance(NodeD, NodeC, 5);
		assertPredecessorAndShortestDistance(NodeC, NodeA, 4);
	}

	private void assertPredecessorAndShortestDistance(Node c, Node pred, int sd) {
		assertEquals("incorrect shortest path", pred, engine.getPredecessor(c));
		assertEquals("incorrect shortest distance", sd,
				engine.getShortestDistance(c));
	}

	/**
	 * Test the <code>MockMap2</code> (Carl Schwarcz fix).
	 */
	public void testEngine2() {
		engine = new DijkstraEngine(new MockMap2());
		engine.execute(NodeA, null);

		// sanity check
		assertPredecessorAndShortestDistance(NodeA, null, 0);

		// shortest path from A to F
		assertPredecessorAndShortestDistance(NodeF, NodeE, 6);
		assertPredecessorAndShortestDistance(NodeE, NodeB, 5);
		assertPredecessorAndShortestDistance(NodeB, NodeA, 2);
	}

	/**
	 * Test the <code>MockMap3</code> without a <code>E</code> Node
	 */
	public void testEngine3a() {
		engine = new DijkstraEngine(new MockMap3(false));
		engine.execute(NodeA, null);

		// sanity checks
		assertPredecessorAndShortestDistance(NodeA, null, 0);
		assertPredecessorAndShortestDistance(NodeE, null,
				DijkstraEngine.INFINITE_DISTANCE);

		// shortest path from A to D
		assertPredecessorAndShortestDistance(NodeD, NodeB, 4);
		assertPredecessorAndShortestDistance(NodeB, NodeC, 3);
		assertPredecessorAndShortestDistance(NodeC, NodeA, 2);
	}

	/**
	 * Test the <code>MockMap3</code> with a <code>E</code> Node
	 */
	public void testEngine3b() {
		engine = new DijkstraEngine(new MockMap3(true));
		engine.execute(NodeA, null);

		// sanity check
		assertPredecessorAndShortestDistance(NodeA, null, 0);

		// shortest path from A to E
		assertPredecessorAndShortestDistance(NodeD, NodeE, 3);
		assertPredecessorAndShortestDistance(NodeE, NodeA, 1);
	}
	
	public void testEngine4(){
		engine = new DijkstraEngine(new MockMap4());
		engine.execute(NodeA, NodeE);

		// shortest path from A to E
		assertPredecessorAndShortestDistance(NodeE, NodeC, 21);
		assertPredecessorAndShortestDistance(NodeC, NodeA, 20);

	}

}
