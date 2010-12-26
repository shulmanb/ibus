package com.ibus.navigation.map;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ibus.map.Node;
import com.ibus.map.Point;
import com.ibus.map.Stop;

/**
 * Route in nodes
 * 
 * @author Home
 *
 */
class NodesRoute{
	private static final int WALKING_FACTOR = 15;
	private Point start;
	private Point end;

	public NodesRoute(Point start, Point end, Collection<Node> path, int weight) {
		this.weight = weight;
		this.path = new LinkedList<Node>(path);
		this.start = start;
		this.end = end;
	}
	/**
	 * the path weight represents time in minutes;
	 */
	int weight;
	/**
	 * Ordered collection of nodes, according to the order on the path from origin to destination
	 */
	List<Node> path;
	
	public boolean isBetter(NodesRoute other, Point origin, Point destination) {
		if(other == null){
			return true;
		}
		//assume walking speed 15min for km
		double walking = start.distnaceFrom(origin)+end.distnaceFrom(destination);
		double otherWalking = other.start.distnaceFrom(origin)+other.end.distnaceFrom(destination);
		if(weight + WALKING_FACTOR*walking < other.weight + WALKING_FACTOR*otherWalking){
			return true;
		}
		//TODO: decide according total weight and distance from origin point and destination point
		//also factors like number of lane changes should be taken into account 
		return false;
	}

	public boolean contains(Stop stop) {
		for(Node n:stop.getNodes()){
			if(path.contains(n)){
				return true;
			}
		}
		return false;
	}
}