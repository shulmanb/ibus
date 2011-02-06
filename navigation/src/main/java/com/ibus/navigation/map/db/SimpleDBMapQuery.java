package com.ibus.navigation.map.db;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ibus.map.Line;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopDetails;

import static com.ibus.map.utils.SimpleDBNames.*
;
public class SimpleDBMapQuery implements IMapQueryDB {
	private AmazonSimpleDB sdb;
	private Gson gson = new Gson();
	
	@Inject
	public SimpleDBMapQuery(@Named("AWS USER KEY")String userKey, @Named("AWS SECRET KEY")String secretKey) {
		sdb = new AmazonSimpleDBClient(new BasicAWSCredentials(userKey,
				secretKey));
	}

	@Override
	public Stop[] getStationsForLine(String lineId) {
		GetAttributesRequest getAttributesRequest = new GetAttributesRequest(
				LINE_STATIONS,lineId);
		GetAttributesResult res = sdb.getAttributes(getAttributesRequest);
		Stop[] lineStops = new Stop[res.getAttributes().size()];
		for (Attribute attr : res.getAttributes()) {
			lineStops[Integer.valueOf(attr.getName())] = gson.fromJson(
					attr.getValue(), Stop.class);
		}
		return lineStops;
	}

	@Override
	public Point[] getLinePoints(String lineId) {
		String query = 	"select * from "+SEGMENT_POINTS+" where itemName() like '"
			+ lineId + "%' order by itemName() Asc";
		SelectRequest selectRequest = new SelectRequest(query);
		SelectResult res = sdb.select(selectRequest);
		ArrayList<Point> linePoints = new ArrayList<Point>();
		for (Item itm : res.getItems()) {
			Point[] tmp = new Point[itm.getAttributes().size()];
			for (Attribute attr : itm.getAttributes()) {
				int indx = Integer.valueOf(attr.getName());
				tmp[indx] = gson.fromJson(attr.getValue(),
						Point.class);
			}
			linePoints.addAll(Arrays.asList(tmp));
		}
		return linePoints.toArray(new Point[0]);
	}

	private StopDetails toStopDetails(List<Attribute> list) {
		StopDetails stop = new StopDetails();
		for (Attribute attr : list) {
			if (attr.getName().equalsIgnoreCase(LON_ATTR)) {
				stop.setLongitude(Double.valueOf(attr.getValue()));
			} else if (attr.getName().equalsIgnoreCase(LAT_ATTR)) {
				stop.setLatitude(Double.valueOf(attr.getValue()));
			} else if (attr.getName().equalsIgnoreCase(LINES_ATTR)) {
				stop.setLines(gson.fromJson(attr.getValue(), String[].class));
			} else if (attr.getName().equalsIgnoreCase(LINES_NAMES_ATTR)) {
				stop.setLinesNames(gson.fromJson(attr.getValue(), String[].class));
			} else if (attr.getName().equalsIgnoreCase(SUBMAP_ATTR)) {
				stop.setSubmap(attr.getValue());
			}else if (attr.getName().equalsIgnoreCase(DESC_ATTR)) {
				stop.setDesc(attr.getValue());
			}
		}
		return stop;
	}

	@Override
	public StopDetails getStopDetails(String stationId) {
		GetAttributesRequest getAttributesRequest = new GetAttributesRequest(
				STATIONS_DETAILS, stationId);
		GetAttributesResult res = sdb.getAttributes(getAttributesRequest);
		StopDetails stop = toStopDetails(res.getAttributes());
		return stop;
	}


	@Override
	public StopDetails[] getAllStationsInSubmap(String submap) {
		SelectRequest selectRequest = new SelectRequest("select * from "+STATIONS_DETAILS+" where "+SUBMAP_ATTR+" = '"+submap+"'");
		SelectResult res = sdb.select(selectRequest);
		StopDetails[] stations = new StopDetails[res.getItems().size()];
		int i = 0;
		for(Item itm:res.getItems()){
			stations[i] = toStopDetails(itm.getAttributes());
			i++;
		}
		return stations;
	}

	@Override
	public StopDetails[] getStationsInArea(String submap, Point left, Point right) {
		StringBuilder query = new StringBuilder("select * from "+STATIONS_DETAILS+" where "+SUBMAP_ATTR+" = '"+submap+"'");
		//add high latitude query
		query.append(" intersection  "+LAT_ATTR+" <= '"+left.getLatitude()+"'");
		//add low latitude
		query.append(" intersection  "+LAT_ATTR+" >= '"+right.getLatitude()+"'");
		//add lower longitude
		query.append(" intersection  "+LON_ATTR+" >= '"+left.getLongitude()+"'");
		//add higher longitude
		query.append(" intersection  "+LON_ATTR+" <= '"+right.getLongitude()+"'");
		SelectRequest selectRequest = new SelectRequest(query.toString());
		SelectResult res = sdb.select(selectRequest);
		StopDetails[] stations = new StopDetails[res.getItems().size()];
		int i = 0;
		for(Item itm:res.getItems()){
			stations[i] = toStopDetails(itm.getAttributes());
			i++;
		}
		return stations;
	}

	@Override
	public StopDetails[] getStationsInArea(Point left, Point right) {
		StringBuilder query = new StringBuilder("select * from "+STATIONS_DETAILS+" where");
		//add high latitude query
		query.append(" "+LAT_ATTR+" <= '"+left.getLatitude()+"'");
		//add low latitude
		query.append(" intersection  "+LAT_ATTR+" >= '"+right.getLatitude()+"'");
		//add lower longitude
		query.append(" intersection  "+LON_ATTR+" >= '"+left.getLongitude()+"'");
		//add higher longitude
		query.append(" intersection  "+LON_ATTR+" <= '"+right.getLongitude()+"'");
		SelectRequest selectRequest = new SelectRequest(query.toString());
		SelectResult res = sdb.select(selectRequest);
		StopDetails[] stations = new StopDetails[res.getItems().size()];
		int i = 0;
		for(Item itm:res.getItems()){
			stations[i] = toStopDetails(itm.getAttributes());
			i++;
		}
		return stations;
	}

	@Override
	public void deleteLineById(String lineId) {
		GetAttributesResult lSegments = sdb.getAttributes(new GetAttributesRequest(LINE_SEGMENTS, lineId));
		List<String> segmentIds = new LinkedList<String>();
		List<String> segments = new LinkedList<String>();
		String submap = "";
		for(Attribute attr:lSegments.getAttributes()){
			try{
				Integer.parseInt(attr.getName());
				segmentIds.add(lineId+"_"+attr.getName());
				segments.add(attr.getValue());
			}catch(NumberFormatException e){
				if(attr.getName().equals(SUBMAP_ATTR)){
					submap = attr.getValue();
				}
			}
		}

		GetAttributesResult segmentStations = sdb.getAttributes(new GetAttributesRequest(LINE_STATIONS, lineId));
		List<String> stationIds = new LinkedList<String>();
		for(Attribute attr:segmentStations.getAttributes()){
			stationIds.add(gson.fromJson(attr.getValue(), Stop.class).getId());
		}

		//retrieve stations fromibus_stations
		StringBuilder sb = new StringBuilder("select * from "+STATIONS_DETAILS+" where itemName() in(");
		boolean first = true;
		for(String stId:stationIds){
			if(!first){
				sb.append(" , ");
			}
			first = false;
			sb.append("'"+stId+"'");
		}
		sb.append(")");
		String query = 	sb.toString();
		SelectRequest selectRequest = new SelectRequest(query);
		SelectResult res = sdb.select(selectRequest);

		List<String> stationsToDelete = new LinkedList<String>();
		for(Item itm:res.getItems()){
			for(Attribute attr:itm.getAttributes()){
				if(attr.getName().equals(LINES_ATTR)){
					Type collectionType = new TypeToken<LinkedList<String>>(){}.getType();
					List<String> lst = gson.fromJson(attr.getValue(), collectionType);
					if(lst.size() == 1){
						stationsToDelete.add(itm.getName());
					}
				}
			}
		}
		
		
		//delete needed from ibus_stations
		Collection<Attribute> attrs = new LinkedList<Attribute>();
		for(String station:stationsToDelete){
			Attribute attr = new Attribute();
			attr.setName(station);
			attrs.add(attr);
		}
		DeleteAttributesRequest dar = new DeleteAttributesRequest(STATIONS, submap);
		dar.setAttributes(attrs);
		sdb.deleteAttributes(dar);
		
		
		//delete needed from ibus_stations_details
		for(String station:stationsToDelete){
			sdb.deleteAttributes(new DeleteAttributesRequest(STATIONS_DETAILS, station));
		}

		
		
		//delete all segment points
		for(String segment:segmentIds){
			sdb.deleteAttributes(new DeleteAttributesRequest(SEGMENT_POINTS, segment));
		}
		//delete line stations entry
		sdb.deleteAttributes(new DeleteAttributesRequest(LINE_STATIONS, lineId));
		//delete line segments entry
		sdb.deleteAttributes(new DeleteAttributesRequest(LINE_SEGMENTS, lineId));
		
	}

	@Override
	public Line[] getLinesInSubmap(String submap) {
		String query = 	"select lineName from "+LINE_SEGMENTS+" where submap =  '"+submap+"'";
		SelectRequest selectRequest = new SelectRequest(query);
		SelectResult res = sdb.select(selectRequest);
		List<Line> ret = new LinkedList<Line>();
		for(Item ln : res.getItems()){
			Line line = new Line();
			line.setId(ln.getName());
			line.setName(ln.getAttributes().get(0).getValue());
			ret.add(line);
		}
		return ret.toArray(new Line[0]);
	}
}
