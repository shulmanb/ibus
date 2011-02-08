package com.ibus.tracer.db;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import redis.clients.jedis.Jedis;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ibus.connectivity.AbstractRedisStorage;
import com.ibus.connectivity.IReconnectable;
import com.ibus.connectivity.Retrieable;
import com.ibus.map.LineSegment;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopsRoute;
import com.ibus.map.TimedPoint;
import com.ibus.tracer.InvalidRouteException;
import com.ibus.tracer.Status;

public class RedisSessionDB extends AbstractRedisStorage implements ISessionDB{
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public static final int SESSION_TTL = 60*60*12;
	public static final int ROUTE_TTL = 60*5;
	
	@Inject
	public RedisSessionDB(@Named("REDIS HOST")String redisHost,@Named("REDIS PORT") Integer redisPort) {
		super(redisHost, redisPort);
	}
	

	
	public RedisSessionDB(Jedis jedis){
		super(jedis);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object fromJsonIfNotEmpty(String ret, Class clazz) {
		if(ret == null || ret.equalsIgnoreCase("nil")){
			return null;
		}
		if(clazz == null){
			return ret;
		}
		try {
			return mapper.readValue(ret, clazz);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clazz; 
	}

	@Override @Retrieable
	public String initiateSession(String clientid, String sessionId) {
		Jedis jedis = getJedis();
		try {
			String ses = jedis.get(clientid);
			if (ses != null && !ses.isEmpty() && pingSession(ses)) {
				return ses;
			}
			jedis.hset(sessionId, "client", clientid);
			jedis.set(clientid, sessionId);
			jedis.expire(sessionId, SESSION_TTL);
			jedis.expire(clientid, SESSION_TTL);
			return sessionId;
		} finally {
			returnJedis(jedis);
		}

	}

	@Override @Retrieable
	public boolean pingSession(String sessionId) {
		Jedis jedis = getJedis();
		try {
			if (jedis.exists(sessionId)) {
				String client = jedis.hget(sessionId, "client");
				jedis.expire(sessionId, SESSION_TTL);
				jedis.expire(client, SESSION_TTL);
				return true;
			}
			return false;
		} finally {
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public void storeLocation(String sessionId, TimedPoint point) {
		Jedis jedis = getJedis();
		try {
			jedis.hset(sessionId, "location", mapper.writeValueAsString(point));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public TimedPoint getLastLocation(String sessionId) {
		Jedis jedis = getJedis();
		try {
			String ret = jedis.hget(sessionId, "location");
			return (TimedPoint) fromJsonIfNotEmpty(ret, TimedPoint.class);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public String getCheckInStatus(String sessionId) {
		Jedis jedis = getJedis();
		try {
			String ret = jedis.hget(sessionId, "checkIn");
			return (String) fromJsonIfNotEmpty(ret, null);
		} finally {
			returnJedis(jedis);
		}
	}



	@Override @Retrieable
	public void storeRoute(StopsRoute route){
		Jedis jedis = getJedis();
		try {
			jedis.setex(route.getRouteId(), ROUTE_TTL, mapper.writeValueAsString(route));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			returnJedis(jedis);
		} 
	}
	
	@Override @Retrieable
	public StopsRoute getRoute(String routeId)throws InvalidRouteException{
		Jedis jedis = getJedis();
		String route = jedis.get(routeId);
		if(route == null){
			throw new InvalidRouteException(routeId);
		}
		jedis.del(routeId);
		try {
			return mapper.readValue(route, StopsRoute.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			returnJedis(jedis);
		}
		return null; 
	}

	@Override @Retrieable
	public void setNextSegmentStop(String sessionId, Stop start) {
		Jedis jedis = getJedis();
		try {
			jedis.hset(sessionId+"status", "nextstop", mapper.writeValueAsString(start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public void addSegment(String sessionId, LineSegment ls) {
		Jedis jedis = getJedis();
		try {
			jedis.rpush(sessionId+"route", mapper.writeValueAsString(ls));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public Stop getNextSegmentStop(String sessionId) {
		Jedis jedis = getJedis();
		try {
			return (Stop) fromJsonIfNotEmpty(
					jedis.hget(sessionId + "status", "nextstop"), Stop.class);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public LineSegment popNextSegment(String sessionId) {
		Jedis jedis = getJedis();
		try {
			LineSegment ls = (LineSegment) fromJsonIfNotEmpty(
					jedis.lpop(sessionId + "route"), LineSegment.class);
			return ls;
		} finally {
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public void addPointForNextSegment(String sessionId, TimedPoint p) {
		Jedis jedis = getJedis();
		try {
			jedis.rpush(sessionId+"segment", mapper.writeValueAsString(p));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public LineSegment getCurrentSegment(String sessionId) {
		Jedis jedis = getJedis();
		try {
			LineSegment ls = (LineSegment) fromJsonIfNotEmpty(
					jedis.hget(sessionId + "status", "currls"),
					LineSegment.class);
			return ls;
		} finally {
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public void setCurrentSegment(String sessionId, LineSegment ls) {
		Jedis jedis = getJedis();
		try {
			jedis.hset(sessionId+"status", "currls", mapper.writeValueAsString(ls));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			returnJedis(jedis);
		}
		
	}
	
	@Override @Retrieable
	public TimedPoint[] getNextPointsInSegment(String sessionId, int count) {
		Jedis jedis = getJedis();
		try {
			List<String> rets = jedis.lrange(sessionId + "segment", 0,
					count - 1);
			int i = 0;
			TimedPoint[] res = new TimedPoint[rets.size()];
			for (String ret : rets) {
				res[i] = (TimedPoint) fromJsonIfNotEmpty(ret, TimedPoint.class);
				i++;
			}
			return res;
		} finally {
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public void removePointsFromSegment(String sessionId, int index) {
		Jedis jedis = getJedis();
		try {
			jedis.ltrim(sessionId + "segment", index, Integer.MAX_VALUE);
		} finally {
			returnJedis(jedis);
		}
		
	}

	@Override @Retrieable
	public Status getStatusOnRoute(String sessionId) {
		Jedis jedis = getJedis();
		try {
			String ret = jedis.hget(sessionId, "status");
			return (Status) fromJsonIfNotEmpty(ret, Status.class);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public void updateStatus(String sessionId, Status status) {
		Jedis jedis = getJedis();
		try {
			jedis.hset(sessionId, "status", mapper.writeValueAsString(status));
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}finally{
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public void chcekInToRoute(String sessionId, String routeId) {
		Jedis jedis = getJedis();
		try {
			jedis.hset(sessionId, "checkIn", routeId);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public void storeDestinationCoordinates(String sessionId, Point dest) {
		Jedis jedis = getJedis();
		try{
			jedis.hset(sessionId, "destination", mapper.writeValueAsString(dest));
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			returnJedis(jedis);
		}
	}

	@Override @Retrieable
	public Point getDestinationCoordinates(String sessionId) {
		Jedis jedis = getJedis();
		try {
			String ret = jedis.hget(sessionId, "destination");
			return (Point) fromJsonIfNotEmpty(ret, Point.class);
		} finally {
			returnJedis(jedis);
		}
	}
}
