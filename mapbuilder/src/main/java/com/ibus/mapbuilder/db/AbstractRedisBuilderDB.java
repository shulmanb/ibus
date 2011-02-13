package com.ibus.mapbuilder.db;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.omg.CosNaming.IstringHelper;

import redis.clients.jedis.Jedis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.ibus.connectivity.IReconnectable;
import com.ibus.connectivity.Retrieable;
import com.ibus.map.Point;
import com.ibus.map.SubmapIdGenerator;

/**
 * Abstract class that implements the redis storage, but does not implement the persistent storage
 * @author Home
 *
 */
public abstract class AbstractRedisBuilderDB implements IBuilderDB, IReconnectable {

	public static class PointContainer{
		double lat;
		double lon;
		long ts;
		boolean isStation;
		String desc;
		boolean foreign = false;
		
		public PointContainer cloneAsForeign(){
			PointContainer nPc = new PointContainer();
			nPc.desc = desc;
			nPc.isStation = isStation;
			nPc.lat = lat;
			nPc.lon = lon;
			nPc.ts = ts;
			nPc.foreign = true;
			return nPc;
		}
		public boolean isForeign() {
			return foreign;
		}
		public void setForeign(boolean foreign) {
			this.foreign = foreign;
		}
		public double getLat() {
			return lat;
		}
		public double getLon() {
			return lon;
		}
		public long getTs() {
			return ts;
		}
		public boolean isStation() {
			return isStation;
		}
		public String getDesc() {
			return desc;
		}
		public PointContainer setLat(double lat){
			this.lat = lat;
			return this;
		}
		public PointContainer setLon(double lon){
			this.lon = lon;
			return this;
		}
		public PointContainer setTs(long ts){
			this.ts = ts;
			return this;
		}
		public PointContainer setStation(boolean st){
			this.isStation = st;
			return this;
		}
		public PointContainer setDesc(String desc){
			this.desc = desc;
			return this;
		}
	}
	private Jedis jedis;
	protected Gson gson = new Gson();
	private String host;
	private int port;
	
	public AbstractRedisBuilderDB(Jedis jedis){
		this.jedis = jedis;
	}
	
	public AbstractRedisBuilderDB(String redisHost, int redisPort) {
		this.jedis = new Jedis(redisHost, redisPort);
		this.host = redisHost;
		this.port = redisPort;
	}


	@Override
	public void reconnect() throws IOException {
		this.jedis = new Jedis(host, port);
		jedis.connect();
	}


	protected Multimap<String,List<PointContainer>> getPoints(String sessionId){
		List<String> points = jedis.lrange("list:"+sessionId, 1, -1);
		RegionDetails rdetails = getSessionRegion(sessionId);
		SubmapIdGenerator idGen = new SubmapIdGenerator(rdetails.getNorthwest(), rdetails.getSoutheast(), rdetails.getLength(), rdetails.getRegionId());
		String currSubmap = null;
		List<PointContainer> currList = null;
		Multimap<String,List<PointContainer>> retList = HashMultimap.create();
		for(String p:points){
			PointContainer pc = gson.fromJson(p, PointContainer.class); 
			if(pc.isStation()){
				String id = idGen.getSubmapIdForPoint(new Point(pc.getLon(),pc.getLat()));
				if(!id.equals(currSubmap)){
					if(currList != null){
						currList.add(pc.cloneAsForeign());
					}
					currSubmap = id;
					currList = new LinkedList<PointContainer>();
					retList.put(currSubmap, currList);
				}
			}
			currList.add(pc);
		}
		return retList;
	}
	
	protected String getLineDetails(String sessionId){
		return jedis.hget(sessionId,"line");
	}
	
	protected RegionDetails getSessionRegion(String sessionId){
		String str = jedis.hget(sessionId,"region");
		return gson.fromJson(str, RegionDetails.class);
	}
	
	
	protected void clearSession(String sessionId){
		jedis.del(sessionId,"list:"+sessionId);
	}

	
	
	@Override @Retrieable
	public void addStation(String sessionID, Point point, long ts) {
		String json = gson.toJson(new PointContainer().setTs(ts).setLat(point.getLatitude()).setLon(point.getLongitude()).setStation(true));
		int ret = jedis.rpush("list:"+sessionID, json);
	}

	@Override @Retrieable
	public void createRecordingSession(String sessionID, String line, String submap) {
		RegionDetails region = getRegionDetails(submap);
		jedis.hset(sessionID,"line",line);
		jedis.hset(sessionID,"region",this.gson.toJson(region));
		jedis.rpush("list:"+sessionID, "");
		//add expiration after redis 2.1.3
		jedis.expire(sessionID, 60*60*12);
		jedis.expire("list:"+sessionID, 60*60*12);
	}

	protected abstract RegionDetails getRegionDetails(String submap);

	@Override @Retrieable
	public void addPoint(String sessionID, Point point, long ts) {
		String json = gson.toJson(new PointContainer().setTs(ts).setLat(point.getLatitude()).setLon(point.getLongitude()).setStation(false));
		int ret = jedis.rpush("list:"+sessionID, json);
	}
	

}
