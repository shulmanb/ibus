package com.ibus.mapbuilder.db;

import static com.ibus.map.utils.SimpleDBNames.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import redis.clients.jedis.Jedis;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ibus.map.*;
import com.ibus.mapbuilder.db.AbstractRedisBuilderDB.PointContainer;

public class SimpleDBRedisBuilderDB extends AbstractRedisBuilderDB {
	private AmazonSimpleDB sdb;

	
	/**
	 * This nconstructor should be used for unit tests
	 * @param jedis
	 * @param sdb
	 */
	public SimpleDBRedisBuilderDB(Jedis jedis, AmazonSimpleDBClient sdb){
		super(jedis);
		this.sdb = sdb;
	}
	
	@Inject
	public SimpleDBRedisBuilderDB(
			@Named("REDIS HOST")String redisHost, 
			@Named("REDIS PORT")int redisPort,
			@Named("AWS USER KEY")String userKey, 
			@Named("AWS SECRET KEY")String secretKey) {
		super(redisHost, redisPort);
		sdb = new AmazonSimpleDBClient(new BasicAWSCredentials(userKey,
				secretKey));
	}

	@Override
	public void flushRoute(String sessionID) {
		String details = getLineDetails(sessionID);
		StringTokenizer tkn = new StringTokenizer(details, ":");
		String submap = tkn.nextToken();
		String lineName = tkn.nextToken();
		PointContainer[] points = getPoints(sessionID);
		List<LineSegment> segments = new ArrayList<LineSegment>();
		ArrayList<Stop> stations = new ArrayList<Stop>();
		ArrayList<Point> linePoints = new ArrayList<Point>();
		
		createDataStructures(lineName,sessionID, submap, points, segments, stations,linePoints);
		
		store(lineName, sessionID,submap, segments, stations, linePoints);
		
		clearSession(sessionID);
	}

	private void store(String lineName,String lineId, String submap, List<LineSegment> segments, ArrayList<Stop> stations, ArrayList<Point> linePoints) {
		//store line segments
		storeLineSegmentsAndEdges(submap,lineId, lineName,segments);

		storeStations(submap,lineId,lineName, stations);
	}

	private void storeStations(String submap, String lineId,String lineName,ArrayList<Stop> stations) {
		//store stops details
		//retrive all stops for the submap
		List<String> stationsIdsToAlter = new LinkedList<String>();
		List<Stop> stationsToAlter = new LinkedList<Stop>();
		
		GetAttributesResult result = sdb.getAttributes(new GetAttributesRequest(STATIONS, submap));
		List<Attribute> existingStations = result.getAttributes();
		for(Attribute attr:existingStations){
			Point p = gson.fromJson(attr.getValue(),Point.class);
			Stop s = new Stop(p);
			int indx = stations.indexOf(s);
			if(indx > -1){
				stationsIdsToAlter.add(attr.getValue());
				stationsToAlter.add(stations.get(indx));
				stations.remove(indx);
			}
		}
		//add new stations
		addNewStationsToSubmap(submap, stations, existingStations);

		BatchPutAttributesRequest bpar = new BatchPutAttributesRequest();
		bpar.setDomainName(STATIONS_DETAILS);
		Collection<ReplaceableItem> items = new LinkedList<ReplaceableItem>();

		//retrieve existing stations and add line to them
		addExistingStationsToBatch(lineId,lineName, items, stationsToAlter);

		addNewStationsToBatch(submap, lineId,lineName, stations, items);
		//store in batch all
		bpar.setItems(items);
		sdb.batchPutAttributes(bpar);
		//store list of stations for line
		storeStationsList(stations, submap, lineId);
	}

	private void storeStationsList(ArrayList<Stop> stations, String submap,
			String lineId) {
		if(!stations.isEmpty()){
			List<ReplaceableAttribute> newStations = new LinkedList<ReplaceableAttribute>();
			int i = 0;
			for(Stop st:stations){
				ReplaceableAttribute ra = new ReplaceableAttribute(String.valueOf(i), gson.toJson(st.getStopsPoint()), false);
				newStations.add(ra);
				i++;
			}
			PutAttributesRequest putAttributesRequest = new PutAttributesRequest(LINE_STATIONS,lineId, newStations);
			sdb.putAttributes(putAttributesRequest);
		}
	}

	private void addNewStationsToBatch(String submap, String line,String lineName,
			ArrayList<Stop> stations, Collection<ReplaceableItem> items) {
		HashSet<String> addedIds = new HashSet<String>();
		for(Stop st:stations){
			if(addedIds.contains(st.getId())){
				continue;
			}
			ReplaceableItem item = new ReplaceableItem(st.getId());
			addedIds.add(st.getId());
			Collection<ReplaceableAttribute> col = new LinkedList<ReplaceableAttribute>();
			col.add(new ReplaceableAttribute(SUBMAP_ATTR,submap,false));
			col.add(new ReplaceableAttribute(LAT_ATTR,String.valueOf(st.getStopsPoint().getLatitude()),false));
			col.add(new ReplaceableAttribute(LON_ATTR,String.valueOf(st.getStopsPoint().getLongitude()),false));
			col.add(new ReplaceableAttribute(LINES_ATTR,gson.toJson(new String[]{line}),false));
			col.add(new ReplaceableAttribute(LINES_NAMES_ATTR,gson.toJson(new String[]{lineName}),false));
			if(st.getDesc()!=null && !st.getDesc().isEmpty()){
				col.add(new ReplaceableAttribute(DESC_ATTR,st.getDesc(),false));
			}
			item.withAttributes(col);
			items.add(item);
		}
	}

	private void addExistingStationsToBatch(String line,String lineName,
			Collection<ReplaceableItem> items, List<Stop> stationsToAlter) {
		if(!stationsToAlter.isEmpty()){
			StringBuilder sb = new StringBuilder("select * from "+STATIONS_DETAILS+" where ");
			boolean first = true;
			for(Stop st:stationsToAlter){
				if(!first){
					sb.append(" or ");
				}
				sb.append("itemName=\""+st.getId()+"\"");
				first = false;
			}
			SelectRequest sr = new SelectRequest(sb.toString());
			SelectResult res = sdb.select(sr);
			List<Item> existing = res.getItems();
			for(Item item:existing){
				ReplaceableItem ri = new ReplaceableItem(item.getName()); 
				for(Attribute attr:item.getAttributes()){
					if(attr.getName().equalsIgnoreCase(LINES_ATTR)){
						Type collectionType = new TypeToken<LinkedList<String>>(){}.getType();
						List<String> lst = gson.fromJson(attr.getValue(), collectionType);
						lst.add(line);
						ReplaceableAttribute ra = new ReplaceableAttribute(attr.getName(),gson.toJson(lst,collectionType),true);
						ri.withAttributes(ra);
						items.add(ri);
					}else if(attr.getName().equalsIgnoreCase(LINES_NAMES_ATTR)){
						Type collectionType = new TypeToken<LinkedList<String>>(){}.getType();
						List<String> lst = gson.fromJson(attr.getValue(), collectionType);
						lst.add(lineName);
						ReplaceableAttribute ra = new ReplaceableAttribute(attr.getName(),gson.toJson(lst,collectionType),true);
						ri.withAttributes(ra);
						items.add(ri);
					}
				}
			}
		}
	}

	private void addNewStationsToSubmap(String submap,
			ArrayList<Stop> stations, List<Attribute> existingStations) {
		if(!stations.isEmpty()){
			List<ReplaceableAttribute> newStations = new LinkedList<ReplaceableAttribute>();
			int i = existingStations.size();
			for(Stop st:stations){
				ReplaceableAttribute ra = new ReplaceableAttribute(String.valueOf(i), gson.toJson(st.getStopsPoint()), false);
				newStations.add(ra);
				i++;
			}
			PutAttributesRequest putAttributesRequest = new PutAttributesRequest(STATIONS,submap, newStations);
			sdb.putAttributes(putAttributesRequest);
		}
	}

	private void storeLineSegmentsAndEdges(String submap,String lineId, String lineName, List<LineSegment> segments) {
		PutAttributesRequest putAttributesRequest = new PutAttributesRequest();
		putAttributesRequest.setDomainName(LINE_SEGMENTS);
		putAttributesRequest.setItemName(lineId);
		Collection<ReplaceableAttribute> attributes = new LinkedList<ReplaceableAttribute>();
		attributes.add(new ReplaceableAttribute(LINE_NAME_ATTR, lineName, true));
		attributes.add(new ReplaceableAttribute(SUBMAP_ATTR, submap, true));
		int indx = 0;
		BatchPutAttributesRequest batch = new BatchPutAttributesRequest();
		batch.setDomainName(SEGMENT_POINTS);
		List<ReplaceableItem> segmentsPoints = new LinkedList<ReplaceableItem>();
		for(LineSegment segment:segments){
			List<ReplaceableAttribute> points = new LinkedList<ReplaceableAttribute>();
			ArrayList<TimedPoint> route = segment.getPoints();
			int i = 0;
			for(TimedPoint p:route){
				points.add(new ReplaceableAttribute(String.valueOf(i), gson.toJson(p,TimedPoint.class), true));
				i++;
			}
			segmentsPoints.add(new ReplaceableItem(lineId+"_"+indx, points));
			//not serializing the points
			segment.setPoints(null);
			ReplaceableAttribute attr = new ReplaceableAttribute(String.valueOf(indx),gson.toJson(segment),true);
			attributes.add(attr);
			segment.setPoints(route);
			indx++;
		}
		batch.setItems(segmentsPoints);
		putAttributesRequest.setAttributes(attributes);
		sdb.putAttributes(putAttributesRequest);
		sdb.batchPutAttributes(batch);
	}

	/**
	 * @param lineName  the line id
	 * @param submap the submap id
	 * @param points the points from the cache
	 * @param segments data structure for created segments
	 * @param stations data structure for created stations
	 * @param linePoints data structure for created line points
	 */
	private void createDataStructures(String lineName,String lineId, String submap, PointContainer[] points, 
									  List<LineSegment> segments,ArrayList<Stop> stations,ArrayList<Point> linePoints) {
		// iterate over points, create line segments
		// each segment is bounded by two stations and contains all the points
		// create and store stations (with their points)
		// create edges for the nodes graph(lat_long---->lat_long) use the time
		// for the default weight....
		ArrayList<TimedPoint> segmentPoints  = new ArrayList<TimedPoint>(); 
		Stop startStop = null;
		int stopid = 0;
		long basets = points[0].ts;  
		for (PointContainer container : points) {
			TimedPoint tp = new TimedPoint(container.lon, container.lat,container.ts-basets);
			linePoints.add(tp.toPoint());
			segmentPoints.add(tp);
			if (container.isStation) {
				stopid++;
				Stop st = new Stop(new Point(container.lon, container.lat));
				st.setDesc(container.desc);
				stations.add(st);
				if (startStop != null) {
					// create a segment;
					LineSegment ls = new LineSegment(lineId,lineName, startStop, st,segmentPoints);
					segments.add(ls);
					segmentPoints = new ArrayList<TimedPoint>();
					basets = container.ts;
					tp = new TimedPoint(tp.getLongitude(), tp.getLatitude(), 0);
					segmentPoints.add(tp);
				}
				startStop = st;
			}
		}
	}

}
