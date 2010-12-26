package com.ibus.navigation.map;

import com.google.inject.Inject;
import com.ibus.map.LineSegment;
import com.ibus.map.Stop;
import com.ibus.navigation.dijkstra.RoutesMap;
import com.ibus.navigation.dijkstra.RoutesMapFactory;
import com.ibus.navigation.map.db.IMapDBLoader;
import com.ibus.navigation.map.db.IMapDBLoader.NodesGraph;

public class LinesMapFactory {
	
	private IMapDBLoader mapLoader;
	private RoutesMapFactory factory = new RoutesMapFactory();
	/**
	 * @param mapLoader
	 */
	@Inject
	public void setMapLoader(IMapDBLoader mapLoader) {
		this.mapLoader = mapLoader;
	}
	
	public LinesMap loadMap(String id){
		
		//retrieve all nodes edges
		NodesGraph graph = mapLoader.getRoutesMap(id);
		RoutesMap rMap = factory.createMap(graph.nodesNumber);  
		for(com.ibus.navigation.map.db.IMapDBLoader.Edge edge:graph.edges){
			rMap.addVertex(edge.start, edge.end, edge.weight);
		}
		LinesMap map = new LinesMap(rMap, id);
		//retrieve all stops
		Stop[] stops = mapLoader.getStops(id);
		for(Stop stop:stops){
			map.addStopToMap(stop);
		}
		
		//retrieve all lane segments
		LineSegment[] segments = graph.segments;
		for(LineSegment segment:segments){
			map.addLineSegmentToMap(segment);
		}
		return map;
	}
}
