package com.ibus.navigation.dijkstra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ibus.map.Node;

/**
 * This map stores routes in a matrix, a nxn array. It is most useful when there
 * are lots of routes, otherwise using a sparse representation is recommended.
 * 
 */

class DenseRoutesMap implements RoutesMap {
	private int[][] weights;
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
	@Override
	public int join(RoutesMap otherMap, Map<Integer, Integer> mutuals) {
		DenseRoutesMap newMap = (DenseRoutesMap)otherMap;
		int[][] newWeights = newMap.weights;
		int[][] temp = weights;
		int length = temp.length;
		//mutual nodes shouldn't be added twice, 
		int newSize = (mutuals == null)?length+newWeights.length:length+newWeights.length-mutuals.size();
		weights = new int[newSize][newSize];
		for(int[] arr:weights){
			Arrays.fill(arr, -1);
		}
		
		for(int i =0; i < length;i++){
			for(int j = 0;j<length;j++){
				weights[i][j] = temp[i][j];
			}
		}
		int newIndxI = length;
		for(int i =0; i < newWeights.length;i++){
			int indxI = (mutuals != null && mutuals.containsKey(i))?mutuals.get(i) :newIndxI++; 
			int newIndxJ = length;
			for(int j = 0;j<newWeights.length;j++){
				int indxJ = (mutuals != null && mutuals.containsKey(j))?mutuals.get(j) :newIndxJ++;
				weights[indxI][indxJ] = newWeights[i][j];
			}
		}
		int newId = length;
		for(Entry<Integer, Node> entry: newMap.nodes.entrySet()){
			int currId = entry.getKey();
			if(mutuals != null && mutuals.containsKey(currId)){
				//skip the mutual nodes 
				continue;
			}
			Node n = entry.getValue();
			n.setId(newId);
			nodes.put(newId, n);
			newId++;
		}
		return nodes.size();
	}
}
