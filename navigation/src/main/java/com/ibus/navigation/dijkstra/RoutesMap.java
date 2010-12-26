package com.ibus.navigation.dijkstra;

import java.util.List;

import com.ibus.map.Node;

/**
 * This interface defines the object storing the graph of all routes
 */

public interface RoutesMap {
	/**
	 * Create a new vertex on the graph
	 * @param start the start node
	 * @param end the end node
	 * @param defaultWeight the default static weight on the vertex
	 */
	public void addVertex(Node start, Node end, int defaultWeight);

	/**
	 * Get the value of a segment.h
	 */
	public int getWeight(Node start, Node end);

	/**
	 * Get the list of nodes that can be reached from the given node.
	 */
	public List<Node> getDestinations(Node node);

	/**
	 * Get the list of nodes that lead to the given node.
	 */
	public List<Node> getPredecessors(Node node);

	/**
	 * @return the transposed graph of this graph, as a new RoutesMap instance.
	 */
	public RoutesMap getInverse();
}
