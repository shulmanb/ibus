package com.ibus.map;


/**
 * A Node is identified by its name, a single uppercase character. Conversions
 * to/from characters are handled by {@link #getLine()} and
 * {@link #valueOf(char)}, respectively.
 * <p>
 * Package members are also given access to an identity relationship between
 * cities and numbers: they can converts between <code>City</code> instances and
 * numbers using {@link #valueOf(int)} and {@link #getId()}. This special
 * relationship is used by the {@link com.ibus.navigation.dijkstra.DenseRoutesMap
 * DensesRoutesMap} to store cities in an array.
 * 
 */

public final class Node implements Comparable<Node> {
	
	/**
	 * The nodes name - lane id
	 */
	private final String line;
	/**
	 * The node's unique id in this map, a monotonous number from 0 till the number of nodes in this graph
	 */
	private transient int id;
	
	
	public Node(String line, int id) {
		this.line = line;
		this.id = id;
	}

	public Node(String line) {
		this.line = line;
		this.id = -1;
	}

	public String getLine() {
		return line;
	}


	public int getId() {
		return id;
	}

	
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return line+" "+id;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * Compare two nodes by name.
	 * 
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Node c) {
		return this.id - c.id;
	}
	
	
}
