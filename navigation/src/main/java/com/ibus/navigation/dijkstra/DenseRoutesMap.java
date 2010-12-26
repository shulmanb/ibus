package com.ibus.navigation.dijkstra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ibus.map.Node;

/**
 * This map stores routes in a matrix, a nxn array. It is most useful when there
 * are lots of routes, otherwise using a sparse representation is recommended.
 * 
 */

class DenseRoutesMap implements RoutesMap {
	private final int[][] weights;
	private HashMap<Integer,Node> nodes = new HashMap<Integer,Node>();
	private DynamicWeightCalculator weightCalculator = new DynamicWeightCalculator();
	/**
	 * Calculates the vertex's weight acording to the static weight in the matrix and dnamic data
	 * this method can result in updating the static data 
	 * @param start
	 * @param end
	 * @return calculated weight
	 */
	private int claculateWeight(Node start, Node end){
		int weight = weights[start.getId()][end.getId()];
		if(weight == -1){
			return Integer.MAX_VALUE;
		}
		//calculate dynamic weight
		int calculated = weightCalculator.calculate(start, end);
		if(calculated == 0){
			return weight;
		}
		return calculated; 
	}
	DenseRoutesMap(int numNodes) {
		weights = new int[numNodes][numNodes];
		for(int i = 0;i < numNodes;i++){
			Arrays.fill(weights[i], -1);
		}
	}

	/**
	 * Link two nodes by a direct route with the given distance.
	 */
	public void addVertex(Node start, Node end, int defaultWeight) {
		weights[start.getId()][end.getId()] = defaultWeight;
		nodes.put(start.getId(), start);
		nodes.put(end.getId(), end);
	}

	/**
	 * @return the distance between the two nodes, or 0 if no path exists.
	 */
	public int getWeight(Node start, Node end) {
		return claculateWeight(start, end);
	}

	/**
	 * @return the list of all valid destinations from the given city.
	 */
	public List<Node> getDestinations(Node node) {
		List<Node> list = new ArrayList<Node>();

		for (int i = 0; i < weights.length; i++) {
			if (weights[node.getId()][i] > -1) {
				list.add(nodes.get(i));
			}
		}

		return list;
	}

	/**
	 * @return the list of all cities leading to the given city.
	 */
	public List<Node> getPredecessors(Node node) {
		List<Node> list = new ArrayList<Node>();

		for (int i = 0; i < weights.length; i++) {
			if (weights[i][node.getId()] > -1) {
				list.add(nodes.get(i));
			}
		}

		return list;
	}

	/**
	 * @return the transposed graph of this graph, as a new RoutesMap instance.
	 */
	public RoutesMap getInverse() {
		DenseRoutesMap transposed = new DenseRoutesMap(weights.length);

		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights.length; j++) {
				transposed.weights[i][j] = weights[j][i];
			}
		}

		return transposed;
	}
}
