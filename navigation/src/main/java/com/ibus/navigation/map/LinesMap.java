package com.ibus.navigation.map;

import gnu.trove.TIntProcedure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.ibus.map.LineSegment;
import com.ibus.map.Node;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopsRoute;
import com.ibus.map.utils.MapUtils;
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

	private static int WALKING_DISTANCE_IN_METERS = 300;

	private static int CLOSE_STATIONS_DISTANCE_IN_METERS = 150;

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
	 * finds best route between two stops, checking routes for all available
	 * outgoing and incoming lanes
	 * 
	 * @param origin
	 *            the origin stop
	 * @param destination
	 *            the destination stop
	 * @return found route
	 */
	private NodesRoute findBestRouteBetweenStops(Stop origin, Stop destination) {
		Collection<Node> origins = origin.getNodes();
		Collection<Node> destinations = destination.getNodes();
		DijkstraEngine routingEngine = new DijkstraEngine(map);
		Collection<Node> shortestPath = null;
		int weight = Integer.MAX_VALUE;
		Node bestOrg = null;
		Node bestDst = null;
		for (Node org : origins) {
			for (Node dest : destinations) {
				routingEngine.execute(org, dest);
				int curr;
				if ((curr = routingEngine.getShortestDistance(dest)) < weight) {
					shortestPath = routingEngine.getPath(org, dest);
					weight = curr;
					bestOrg = org;
					bestDst = dest;
				}
			}
		}
		if (weight == Integer.MAX_VALUE) {
			return null;
		}
		return new NodesRoute(stopsByNodeIds.get(bestOrg.getId())
				.getStopsPoint(), stopsByNodeIds.get(bestDst.getId())
				.getStopsPoint(), shortestPath, weight);
	}

	/**
	 * creates a rectangle arrounf given point, by given geometry
	 * @param p the point
	 * @param xLength the length of x
	 * @param yLength the length of y
	 * @return the rectangle
	 */
	private Rectangle getRectangleArrountPoint(Point p, int xLength, int yLength) {
		return new Rectangle((float) (p.getLongitude() - LONG_DEGREES_IN_METER
				* xLength), (float) (p.getLatitude() - LAT_DEGREES_IN_METER
				* yLength), (float) (p.getLongitude() + LONG_DEGREES_IN_METER
				* xLength), (float) (p.getLatitude() + LAT_DEGREES_IN_METER
				* yLength));
	}

	/**
	 * Creates stops route
	 * @param route
	 * @param dest
	 * @return
	 */
	private StopsRoute createStopsRoute(NodesRoute route, Point dest) {
		if (route == null) {
			return null;
		}
		List<LineSegment> segments = new LinkedList<LineSegment>();
		StopsRoute ret = new StopsRoute(UUID.randomUUID().toString(),
				route.weight, segments, dest);
		Node org = null;
		Node dst = null;
		int segmentIndx = 0;
		for (Node node : route.path) {
			org = dst;
			dst = node;
			if (org != null) {
				Edge edge = new Edge(stopsByNodeIds.get(org.getId()),
						stopsByNodeIds.get(dst.getId()), org.getLine());
				if(!edge.end.equals(edge.start)){
					//skip changing stops on station
					LineSegment ls = this.segments.get(edge);
					if(ls == null){
						//create an empty line segment, with null line
						ls = new LineSegment();
						ls.setEnd(edge.end);
						ls.setStart(edge.start);
					}else{
						ls = new LineSegment(ls);
					}
					ls.setSegmentIndx(segmentIndx);
					segments.add(ls);
					segmentIndx++;
				}
			}
		}
		return ret;
	}

	/**
	 * connects all the nodes from those stops
	 * 
	 * @param st
	 * @param stop
	 */
	private void connectStopsByWalkingEdge(Stop st, Stop stop) {
		// calculate distance
		if (st == stop) {
			return;
		}
		double kmDist = st.getStopsPoint().distnaceFrom(stop.getStopsPoint());
		double time = kmDist / 3L;// in Hours
		//TODO: add wait time for a bus, for now add 15 minutes
		time = time * 60 * 60 * 1000+15*60*1000;// in milis - the weight
		for (Node n : st.getNodes()) {
			for (Node n1 : stop.getNodes()) {
				map.addVertex(n, n1, (int) time);
				map.addVertex(n1, n, (int) time);
			}
		}
	}

	/**
	 * Finds clos stops to the given point
	 * @param p
	 * @return
	 */
	private LinkedList<Integer> findCloseStops(Point p) {
		// check if there are stops that ina walking distance
		final LinkedList<Integer> nearStops = new LinkedList<Integer>();
		TIntProcedure proc = new TIntProcedure() {
			public boolean execute(int value) {
				nearStops.add(value);
				return true;
			};
		};
		stopsIndex.nearestN(
				new com.infomatiq.jsi.Point((float) p.getLongitude(), (float) p
						.getLatitude()),
				proc,
				3,
				adoptDistanceToRTree(CLOSE_STATIONS_DISTANCE_IN_METERS,
						(int) p.getLatitude()));
		return nearStops;
	}

	/**
	 * The RTRee implementation assumes euclidic space - flat (x,y), i.e. the
	 * distance between points is calculated by sqrt((X-X1)^2 +(Y-Y1)^2) as the
	 * coordinates do not represent distnace from 0 but degrees, the distance is
	 * calculated in degrees so they need to be converted to degrees. We convert
	 * aproximatly by multiplying by average degrees in a latitude and longitude
	 * (the conversion is only aproximate and is more aqurate at )
	 * 
	 * @param dist
	 *            metric
	 * @return degrees
	 */
	private float adoptDistanceToRTree(int dist, int lon) {
		return MapUtils.metricDistanceInDegrees(dist, lon);
	}

	/**
	 * protected constructor
	 */
	LinesMap(RoutesMap map, String id) {
		this.map = map;
		this.id = id;
		stopsIndex = new RTree();
		stopsIndex.init(new Properties());
	}

	/**
	 * Adds stop to map
	 * 
	 * @param stop
	 */
	void addStopToMap(Stop stop) {
		// TODO: add check that the stop's nodes exist in the map
		stops.add(stop);
		Point p = stop.getStopsPoint();
		Rectangle rect = getRectangleArrountPoint(p, 25, 25);
	
		LinkedList<Integer> closeStops = findCloseStops(p);
		stopsIndex.add(rect, stops.size() - 1);
		for (Node n : stop.getNodes()) {
			stopsByNodeIds.put(n.getId(), stop);
		}
		//connect nodes within a stop
		connectNodesWithinStop(stop.getNodes());
		//connect stops within a walking distance
		for (int indx : closeStops) {
			Stop st = stops.get(indx);
			if (!st.connectedTo(stop)) {
				connectStopsByWalkingEdge(st, stop);
			}
		}
	
	}

	private void connectNodesWithinStop(Collection<Node> nodes) {
		//TODO: calculate average wait time, for now assume 15 minutes
		//connect all the nodes with each other
		Node[] arr = nodes.toArray(new Node[0]);
		for(int i = 0; i < arr.length;i++){
			for(int j = 0;j < arr.length;j++){
				if(j != i){
					map.addVertex(arr[i], arr[j], 15*60*1000);
				}
			}
		}
	}

	/**
	 * Connects too stops on the map for a named line, This is needed in order
	 * to create a full route to be drown on the client
	 * 
	 * @param stratPoint
	 *            origin point
	 * @param endPoint
	 *            destination point
	 * @param laneName
	 *            the lane's name
	 */
	void addLineSegmentToMap(LineSegment segment) {
		segments.put(
				new Edge(segment.getStart(), segment.getEnd(), segment
						.getLineId()), segment);
	}

	/**
	 * @param origin
	 *            the origin point
	 * @return array of stops sorted by distance to the origin
	 */
	public Stop[] findStopsNearby(Point p, int maxNum) {
		final List<Integer> ids = new LinkedList<Integer>();
		TIntProcedure proc = new TIntProcedure() {
	
			public boolean execute(int value) {
				ids.add(value);
				return true;
			};
		};
		stopsIndex.nearestN(
				new com.infomatiq.jsi.Point((float) p.getLongitude(), (float) p
						.getLatitude()),
				proc,
				3,
				adoptDistanceToRTree(WALKING_DISTANCE_IN_METERS,
						(int) p.getLatitude()));
	
		Stop[] ret = new Stop[ids.size() > maxNum ? maxNum : ids.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = stops.get(ids.get(i));
		}
		return ret;
	}

	public String getId() {
		return id;
	}

	/**
	 * finds best route between two point on the map
	 * 
	 * @param origin
	 * @param destination
	 * @return
	 */
	public StopsRoute findRoute(Point origin, Point destination) {
		Stop[] origins = findStopsNearby(origin, 3);
		Stop[] destinations = findStopsNearby(destination, 3);
		NodesRoute best = null;
		// The stops are order from the closest to the origin/destination
		for (Stop org : origins) {
			if (best != null && best.contains(org)) {
				// prefer closer stop on the same route
				continue;
			}
			for (Stop dst : destinations) {
				if (best != null && best.contains(dst) && best.contains(org)) {
					// prefer closer stop on the same route
					continue;
				}
				NodesRoute route = findBestRouteBetweenStops(org, dst);
				if (route != null && route.isBetter(best, origin, destination)) {
					best = route;
				}
			}
		}

		return createStopsRoute(best, destination);
	}

	public StopsRoute findBestStopsRoute(Stop origin, Stop destination) {
		NodesRoute route = findBestRouteBetweenStops(origin, destination);
		return createStopsRoute(route, destination);
	}

	public Stop[] getStopsInArear(Point center, int xLength, int yLength) {
		Rectangle rec = getRectangleArrountPoint(center, xLength, yLength);
		final List<Integer> ids = new LinkedList<Integer>();
		TIntProcedure proc = new TIntProcedure() {
			public boolean execute(int value) {
				ids.add(value);
				return true;
			};
		};
		stopsIndex.contains(rec, proc);

		Stop[] ret = new Stop[ids.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = stops.get(ids.get(i));
		}

		return ret;
	}

	/**
	 * Joins two maps
	 * @param other the other map
	 */
	public void joinMaps(LinesMap other){
		Map<Integer, Integer> mutual = new HashMap<Integer, Integer>();
		LinkedList<Stop> stopsToAdd = new LinkedList<Stop>();
		for(Stop st:other.stops){
			int indx = stops.indexOf(st);
			if(indx > -1){
				Stop s1 = stops.get(indx);
				Node[] otherNodes = st.getNodes().toArray(new Node[0]);
				Node[] nodes = s1.getNodes().toArray(new Node[0]);
				
				assert nodes.length != otherNodes.length;
				for(int i = 0; i < nodes.length;i++){
					mutual.put(otherNodes[i].getId(), nodes[i].getId());
				}
			}else{
				stopsToAdd.add(st);
			}
		}
		map.join(other.map, mutual);
		for(Stop st:stopsToAdd){
			addStopToMap(st);
		}
		segments.putAll(other.segments);
	}
}
