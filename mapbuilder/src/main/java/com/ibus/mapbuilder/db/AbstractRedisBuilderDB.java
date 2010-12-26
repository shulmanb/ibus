package com.ibus.mapbuilder.db;

import java.io.IOException;
import java.util.List;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;
import com.ibus.connectivity.IReconnectable;
import com.ibus.connectivity.Retrieable;
import com.ibus.map.Point;

/**
 * Abstract class that implements the redis storage, but does not implement the persistent storage
 * @author Home
 *
 */
public abstract class AbstractRedisBuilderDB implements IBuilderDB, IReconnectable {

	static class PointContainer{
		double lat;
		double lon;
		long ts;
		boolean isStation;
		String desc;
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


	protected PointContainer[] getPoints(String sessionId){
		List<String> points = jedis.lrange("list:"+sessionId, 1, -1);
		PointContainer[] ret = new PointContainer[points.size()];
		int i = 0;
		for(String p:points){
			ret[i] = gson.fromJson(p, PointContainer.class);
			i++;
		}
		return ret;
	}
	
	protected String getLineDetails(String sessionId){
		return jedis.get(sessionId);
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
	public void createRecordingSession(String sessionID, String lane, String submap) {
		jedis.setex(sessionID,60*60*12, submap+":"+lane);
		jedis.rpush("list:"+sessionID, "");
		//add expiration after redis 2.1.3
		jedis.expire("list:"+sessionID, 60*60*24);
	
	}

	@Override @Retrieable
	public void addPoint(String sessionID, Point point, long ts) {
		String json = gson.toJson(new PointContainer().setTs(ts).setLat(point.getLatitude()).setLon(point.getLongitude()).setStation(false));
		int ret = jedis.rpush("list:"+sessionID, json);
	}
	

}
