package com.ibus.navigation.dijkstra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.ibus.map.Node;

/**
 * An implementation of Dijkstra's shortest path algorithm. It computes the
 * shortest path (in distance) to all nodes in the map. The output of the
 * algorithm is the shortest distance from the start Node to every other Node,
 * and the shortest path from the start Node to every other.
 * <p>
 * Upon calling {@link #execute(Node, Node)}, the results of the algorithm are
 * made available by calling {@link #getPredecessor(Node)} and
 * {@link #getShortestDistance(Node)}.
 * 
 * 
 * @see #execute(Node, Node)
 * 
 */

public class DijkstraEngine {
	/**
	 * Infinity value for distances.
	 */
	public static final int INFINITE_DISTANCE = Integer.MAX_VALUE;

	/**
	 * Some value to initialize the priority queue with.
	 */
	private static final int INITIAL_CAPANode = 8;

	/**
	 * This comparator orders nodes according to their shortest distances, in
	 * ascending fashion. If two nodes have the same shortest distance, we
	 * compare the nodes themselves.
	 */
	private final Comparator<Node> shortestDistanceComparator = new Comparator<Node>() {
		public int compare(Node left, Node right) {
			// note that this trick doesn't work for huge distances, close to
			// Integer.MAX_VALUE
			int result = getShortestDistance(left) - getShortestDistance(right);

			return (result == 0) ? left.compareTo(right) : result;
		}
	};

	/**
	 * The graph.
	 */
	private final RoutesMap map;

	/**
	 * The working set of nodes, kept ordered by shortest distance.
	 */
	private final PriorityQueue<Node> unsettledNodes = new PriorityQueue<Node>(
			INITIAL_CAPANode, shortestDistanceComparator);

	/**
	 * The set of nodes for which the shortest distance to the source has been
	 * found.
	 */
	private final Set<Node> settledNodes = new HashSet<Node>();

	/**
	 * The currently known shortest distance for all nodes.
	 */
	private final Map<Node, Integer> shortestDistances = new HashMap<Node, Integer>();

	/**
	 * Predecessors list: maps a Node to its predecessor in the spanning tree of
	 * shortest paths.
	 */
	private final Map<Node, Node> predecessors = new HashMap<Node, Node>();

	private void setPredecessor(Node a, Node b) {
		predecessors.put(a, b);
	}

	/**
	 * Set the new shortest distance for the given node, and re-balance the
	 * queue according to new shortest distances.
	 * 
	 * @param node
	 *            the node to set
	 * @param distance
	 *            new shortest distance value
	 */
	private void setShortestDistance(Node node, int distance) {
		/*
		 * This crucial step ensures no duplicates are created in the queue when
		 * an existing unsettled node is updated with a new shortest distance.
		 * 
		 * Note: this operation takes linear time. If performance is a concern,
		 * consider using a TreeSet instead instead of a PriorityQueue.
		 * TreeSet.remove() performs in logarithmic time, but the PriorityQueue
		 * is simpler. (An earlier version of this class used a TreeSet.)
		 */
		unsettledNodes.remove(node);
	
		/*
		 * Update the shortest distance.
		 */
		shortestDistances.put(node, distance);
	
		/*
		 * Re-balance the queue according to the new shortest distance found
		 * (see the comparator the queue was initialized with).
		 */
		unsettledNodes.add(node);
	}

	/**
	 * Test a node.
	 * 
	 * @param v
	 *            the node to consider
	 * 
	 * @return whether the node is settled, ie. its shortest distance has been
	 *         found.
	 */
	private boolean isSettled(Node v) {
		return settledNodes.contains(v);
	}

	/**
	 * Compute new shortest distance for neighboring nodes and update if a
	 * shorter distance is found.
	 * 
	 * @param u
	 *            the node
	 */
	private void relaxNeighbors(Node u) {
		for (Node v : map.getDestinations(u)) {
			// skip node already settled
			if (isSettled(v))
				continue;
	
			int shortDist = getShortestDistance(u) + map.getWeight(u, v);
	
			if (shortDist < getShortestDistance(v)) {
				// assign new shortest distance and mark unsettled
				setShortestDistance(v, shortDist);
	
				// assign predecessor in shortest path
				setPredecessor(v, u);
			}
		}
	}

	/**
	 * Initialize all data structures used by the algorithm.
	 * 
	 * @param start
	 *            the source node
	 */
	private void init(Node start) {
		settledNodes.clear();
		unsettledNodes.clear();
	
		shortestDistances.clear();
		predecessors.clear();
	
		// add source
		setShortestDistance(start, 0);
		unsettledNodes.add(start);
	}

	/**
	 * Constructor.
	 */
	public DijkstraEngine(RoutesMap map) {
		this.map = map;
	}

	/**
	 * Run Dijkstra's shortest path algorithm on the map. The results of the
	 * algorithm are available through {@link #getPredecessor(Node)} and
	 * {@link #getShortestDistance(Node)} upon completion of this method.
	 * 
	 * @param start
	 *            the starting Node
	 * @param destination
	 *            the destination Node. If this argument is <code>null</code>,
	 *            the algorithm is run on the entire graph, instead of being
	 *            stopped as soon as the destination is reached.
	 */
	public void execute(Node start, Node destination) {
		init(start);

		// the current node
		Node u;

		// extract the node with the shortest distance
		while ((u = unsettledNodes.poll()) != null) {
			//assert !isSettled(u);

			// destination reached, stop
			if (u == destination)
				break;

			settledNodes.add(u);

			relaxNeighbors(u);
		}
	}

	/**
	 * @return the shortest distance from the source to the given node, or
	 *         {@link DijkstraEngine#INFINITE_DISTANCE} if there is no route to
	 *         the destination.
	 */
	public int getShortestDistance(Node node) {
		Integer d = shortestDistances.get(node);
		return (d == null) ? INFINITE_DISTANCE : d;
	}

	/**
	 * @return the Node leading to the given Node on the shortest path, or
	 *         <code>null</code> if there is no route to the destination.
	 */
	public Node getPredecessor(Node node) {
		return predecessors.get(node);
	}

	public Collection<Node> getPath(Node source, Node dest) {
		ArrayList<Node> l = new ArrayList<Node>();
		for (Node node = dest; node != null; node = getPredecessor(node)) {
			l.add(node);
		}
		Collections.reverse(l);
		return l;
	}
}
