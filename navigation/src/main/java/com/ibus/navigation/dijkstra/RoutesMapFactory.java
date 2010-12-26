package com.ibus.navigation.dijkstra;

public class RoutesMapFactory {
	public RoutesMap createMap(int numOfNodes){
		return new DenseRoutesMap(numOfNodes);
	}
}
