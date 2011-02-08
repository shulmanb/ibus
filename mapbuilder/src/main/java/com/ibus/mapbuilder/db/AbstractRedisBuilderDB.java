package com.ibus.mapbuilder.db;

import java.util.List;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.gson.Gson;
import com.ibus.connectivity.AbstractRedisStorage;
import com.ibus.connectivity.IReconnectable;
import com.ibus.connectivity.Retrieable;
import com.ibus.map.Point;

/**
 * Abstract class that implements the redis storage, but does not implement the persistent storage
 * @author Home
 *
 */
public abstract class AbstractRedisBuilderDB extends AbstractRedisStorage implements IBuilderDB {

	public static class PointContainer{
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
	protected Gson gson = new Gson();
	public AbstractRedisBuilderDB(Jedis jedis){
		super(jedis);
	}
	
	public AbstractRedisBuilderDB(String redisHost, int redisPort) {
		super(redisHost, redisPort);
	}


	protected PointContainer[] getPoints(String sessionId){
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
		String ret = jedis.get(sessionId);
		returnJedis(jedis);
		return ret;
	}
	
	protected void clearSession(String sessionId){
		Jedis jedis = getJedis();
		jedis.del(sessionId,"list:"+sessionId);
		returnJedis(jedis);
	}

	
	
	@Override @Retrieable
	public void addStation(String sessionID, Point point, long ts) {
		Jedis jedis = getJedis();
		String json = gson.toJson(new PointContainer().setTs(ts).setLat(point.getLatitude()).setLon(point.getLongitude()).setStation(true));
		long ret = jedis.rpush("list:"+sessionID, json);
		returnJedis(jedis);
	}

	@Override @Retrieable
	public void createRecordingSession(String sessionID, String lane, String submap) {
		Jedis jedis = getJedis();
		jedis.setex(sessionID,60*60*12, submap+":"+lane);
		jedis.rpush("list:"+sessionID, "");
		//add expiration after redis 2.1.3
		jedis.expire("list:"+sessionID, 60*60*24);
		returnJedis(jedis);
	}

	@Override @Retrieable
	public void addPoint(String sessionID, Point point, long ts) {
		Jedis jedis = getJedis();
		String json = gson.toJson(new PointContainer().setTs(ts).setLat(point.getLatitude()).setLon(point.getLongitude()).setStation(false));
		long ret = jedis.rpush("list:"+sessionID, json);
		returnJedis(jedis);
	}
	

}
