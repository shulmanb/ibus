package com.ibus.navigation.map;

import gnu.trove.TIntProcedure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.ibus.map.LineSegment;
import com.ibus.map.Node;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopsRoute;
import com.ibus.navigation.dijkstra.DijkstraEngine;
import com.ibus.navigation.dijkstra.RoutesMap;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

/**
 * This class represents a single map , city or country/state
 * 
 * @author shulmanb
 *
 */
public class LinesMap {
	/**
	 * assume average, Y
	 */
	private static double LAT_DEGREES_IN_METER = 0.000009;
	/**
	 * assume 60 N latitude, X
	 */
	private static double LONG_DEGREES_IN_METER = 0.000018;
	
	/**
	 * The map id , for example city
	 */
	private String id;
	/**
	 * The routes map, that is used for running dijkstra
	 */
	private RoutesMap map;
	
	/**
	 * Stops list each stop may contain several nodes, one per line
	 */
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	
	private HashMap<Integer, Stop> stopsByNodeIds = new HashMap<Integer, Stop>();
	/**
	 * The Spatial Index of stops 
	 */
	private RTree stopsIndex;

	/**
	 * The map, represented by all the lane segments
	 */
	private HashMap<Edge, LineSegment> segments = new HashMap<Edge, LineSegment>(); 
	/**
	 * finds best route between two stops, 
	 * checking routes for all available outgoing and incoming lanes
	 * @param origin the origin stop
	 * @param destination the destination stop
	 * @return found route
	 */
	private NodesRoute findBestRouteBetweenStops(Stop origin, Stop destination){
		Collection<Node> origins = origin.getNodes();
		Collection<Node> destinations = destination.getNodes();
		DijkstraEngine routingEngine = new DijkstraEngine(map);
		Collection<Node> shortestPath = null;
		int weight = Integer.MAX_VALUE;
		Node bestOrg = null;
		Node bestDst = null;
		for(Node org:origins){
			for(Node dest:destinations){
				routingEngine.execute(org, dest);
				int curr;
				if((curr=routingEngine.getShortestDistance(dest)) < weight){
					shortestPath = routingEngine.getPath(org, dest);
					weight = curr;
					bestOrg = org;
					bestDst = dest;
				}
			}
		}
		if(weight == Integer.MAX_VALUE){
			return null;
		}
		return new NodesRoute(stopsByNodeIds.get(bestOrg.getId()).getStopsPoint(), 
				              stopsByNodeIds.get(bestDst.getId()).getStopsPoint(), 
				              shortestPath, weight) ;
	}
	/**
	 * @param origin the origin point
	 * @return array of stops sorted by distance to the origin
	 */
	public Stop[] findStopsNearby(Point p, int maxNum) {
		final List<Integer> ids = new LinkedList<Integer>();
		TIntProcedure proc = new  TIntProcedure() {
		   
			public boolean execute(int value) {
		      ids.add(value);
		      return true;
		    };
		};
		stopsIndex.nearestN(new com.infomatiq.jsi.Point((float)p.getLongitude(),(float)p.getLatitude()), proc, 3,(float)LONG_DEGREES_IN_METER*500);
		
		Stop[] ret = new Stop[ids.size()>maxNum?maxNum:ids.size()];
		for(int i = 0;i < ret.length;i++){
			ret[i] = stops.get(ids.get(i));
		}
		return ret;
	}

	private Rectangle getRectangleArrountPoint(Point p, int xLength, int yLength){
		return new Rectangle((float)(p.getLongitude()-LONG_DEGREES_IN_METER*xLength), 
				 (float)(p.getLatitude()-LAT_DEGREES_IN_METER*yLength),
				 (float)(p.getLongitude()+LONG_DEGREES_IN_METER*xLength), 
				 (float)(p.getLatitude()+LAT_DEGREES_IN_METER*yLength));
	}
	private StopsRoute createStopsRoute(NodesRoute route, Point dest) {
		if(route == null){
			return null;
		}
		List<LineSegment> segments = new LinkedList<LineSegment>();
		StopsRoute ret = new StopsRoute(UUID.randomUUID().toString(), route.weight, segments, dest);
		Node org = null;
		Node dst = null;
		for(Node node:route.path){
			org = dst;
			dst = node;
			if(org != null){
				Edge edge = new Edge(stopsByNodeIds.get(org.getId()),stopsByNodeIds.get(dst.getId()),org.getLine());
				segments.add(this.segments.get(edge));
			}
		}
		return ret;
	}
	/**
	 * protected constructor 
	 */
	LinesMap(RoutesMap map, String id){
		this.map = map;
		this.id = id;
		stopsIndex = new RTree();
		stopsIndex.init(new Properties());
	}
	
	/**
	 * Adds stop to map
	 * @param stop
	 */
	void addStopToMap(Stop stop){
		//TODO: add check that the stop's nodes exist in the map
		stops.add(stop);
		Point p = stop.getStopsPoint();
		Rectangle rect = getRectangleArrountPoint(p, 25, 25);
		stopsIndex.add(rect,stops.size()-1);
		for(Node n:stop.getNodes()){
			stopsByNodeIds.put(n.getId(), stop);
		}
	}
	
	/**
	 * Connects too stops on the map for a named line,
	 * This is needed in order to create a full route to be dran on the client
	 * @param stratPoint origin point
	 * @param endPoint destination point
	 * @param laneName the lane's name
	 */
	void addLineSegmentToMap(LineSegment segment){
		segments.put(new Edge(segment.getStart(), segment.getEnd(), segment.getLineId()), segment);
	}
	
	public String getId() {
		return id;
	}
	/**
	 * finds best route between two point on the map
	 * @param origin
	 * @param destination
	 * @return
	 */
	public StopsRoute findRoute(Point origin, Point destination){
		Stop[] origins = findStopsNearby(origin,3);
		Stop[] destinations = findStopsNearby(destination,3);
		NodesRoute best = null;
		//The stops are order from the closest to the origin/destination
		for(Stop org:origins){
			if(best != null && best.contains(org)){
				//prefer closer stop on the same route
				continue;
			}
			for(Stop dst:destinations){
				if(best != null && best.contains(dst) && best.contains(org)){
					//prefer closer stop on the same route
					continue;
				}
				NodesRoute route = findBestRouteBetweenStops(org, dst);
				if(route != null && route.isBetter(best, origin, destination)){
					best = route;
				}
			}
		}
		
		return createStopsRoute(best, destination);
	}
	
	public StopsRoute findBestStopsRoute(Stop origin, Stop destination){
		NodesRoute route = findBestRouteBetweenStops(origin, destination);
		return createStopsRoute(route, destination);
	}
	
	public Stop[] getStopsInArear(Point center, int xLength, int yLength){
		Rectangle rec = getRectangleArrountPoint(center, xLength, yLength);
		final List<Integer> ids = new LinkedList<Integer>();
		TIntProcedure proc = new  TIntProcedure() {
			public boolean execute(int value) {
		      ids.add(value);
		      return true;
		    };
		};
		stopsIndex.contains(rec, proc);

		Stop[] ret = new Stop[ids.size()];
		for(int i = 0;i < ret.length;i++){
			ret[i] = stops.get(ids.get(i));
		}

		return ret;
	}

}
