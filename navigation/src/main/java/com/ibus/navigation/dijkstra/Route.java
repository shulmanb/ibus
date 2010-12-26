package com.ibus.navigation.dijkstra;

import java.util.ArrayList;

import com.ibus.map.Node;

/**
 * This class models a route. A route has the following properties:
 * <ul>
 * <li>a list of cities, maybe empty
 * <li>a length, its number of stops
 * <li>a distance, the total distance of all its segments
 * </ul>
 * Route instances are created by the {@link RouteBuilder}.
 * 
 */

public final class Route implements Cloneable {
	// we need its concrete type to successfully clone this field
	private ArrayList<Node> nodes = new ArrayList<Node>();

	private int weight = 0;

	/**
	 * Instances of this class are created by the {@link RouteBuilder}.
	 */
	Route() {
	}

	public Route clone() {
		Route newInstance = null;

		try {
			newInstance = (Route) super.clone();
		} catch (CloneNotSupportedException cnfe) {
			// we are Cloneable: this should never happen
			assert false : cnfe;
		}

		newInstance.nodes = (ArrayList<Node>) nodes.clone();

		return newInstance;
	}

	/**
	 * Add a new node to this route with the given distance. If this is the
	 * first node (i.e. the starting point), the <code>weight</code> argument
	 * is meaningless.
	 * 
	 * @param node
	 *            the next node on this route.
	 * @param weight
	 *            the weight between the previous node and this one.
	 */
	void addNode(Node node, int weight) {
		if (!nodes.isEmpty()) {
			this.weight += weight;
		}

		nodes.add(node);
	}

	/**
	 * @return the total distance of this route.
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @return the number of stops on this route. The starting city is not
	 *         considered a stop and thus is not counted.
	 */
	public int getLength() {
		return (nodes.isEmpty()) ? 0 : nodes.size() - 1;
	}

	/**
	 * @return the last stop on this route. The last stop may be the starting
	 *         point if there are no other stops, or NULL is this route has no
	 *         stops.
	 */
	public Node getLastNode() {
		if (nodes.isEmpty()) {
			return null;
		} else {
			return nodes.get(nodes.size() - 1);
		}
	}

	/**
	 * @return whether this route goes through the given node.
	 */
	public boolean hasNode(Node node) {
		return nodes.contains(node);
	}

	public String toString() {
		StringBuffer temp = new StringBuffer();

		temp.append("l=").append(getLength()).append(" d=")
				.append(getWeight()).append(" ").append(nodes);

		return temp.toString();
	}
}
