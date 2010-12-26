package com.ibus.navigation.map.db;

import com.ibus.map.LineSegment;
import com.ibus.map.Node;
import com.ibus.map.Stop;

public interface IMapDBLoader {
	public static class NodesGraph{
		public Edge[] edges;
		public LineSegment[] segments;
		public int nodesNumber;
	}
	public static class Edge{
		public Node start;
		public Node end;
		public int weight;
		
		public Edge(Node start, Node end, int weight) {
			this.start = start;
			this.end = end;
			this.weight = weight;
		}
	}
	
	NodesGraph getRoutesMap(String submap);

	Stop[] getStops(String submap);
}
