package com.ibus.tracer.db;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import redis.clients.jedis.Jedis;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ibus.connectivity.IReconnectable;
import com.ibus.connectivity.Retrieable;
import com.ibus.map.LineSegment;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopsRoute;
import com.ibus.map.TimedPoint;
import com.ibus.tracer.InvalidRouteException;
import com.ibus.tracer.Status;

public class RedisSessionDB implements ISessionDB, IReconnectable{
	
	private Jedis jedis;
	private ObjectMapper mapper = new ObjectMapper();
	
	public static final int SESSION_TTL = 60*60*12;
	public static final int ROUTE_TTL = 60*5;
	private String host;
	private int port;
	
	@Inject
	public RedisSessionDB(@Named("REDIS HOST")String redisHost,@Named("REDIS PORT") Integer redisPort) {
		this.jedis = new Jedis(redisHost, redisPort);
		this.host = redisHost;
		this.port = redisPort;
	}
	
	public void reconnect() throws IOException{
		this.jedis = new Jedis(host, port);
		jedis.connect();
	}

	
	public RedisSessionDB(Jedis jedis){
		this.jedis = jedis;
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
		String ses = jedis.get(clientid);
		if(ses != null && !ses.isEmpty() && pingSession(ses)){
				return ses;
		}
		jedis.hset(sessionId, "client", clientid);
		jedis.set(clientid, sessionId);
		jedis.expire(sessionId,SESSION_TTL);
		jedis.expire(clientid,SESSION_TTL);
		return sessionId;

	}

	@Override @Retrieable
	public boolean pingSession(String sessionId) {
		if(1 == jedis.exists(sessionId)){
			String client = jedis.hget(sessionId, "client");
			jedis.expire(sessionId,SESSION_TTL);
			jedis.expire(client,SESSION_TTL);
			return true;
		}
		return false;
	}

	@Override @Retrieable
	public void storeLocation(String sessionId, TimedPoint point) {
		try {
			jedis.hset(sessionId, "location", mapper.writeValueAsString(point));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Override @Retrieable
	public TimedPoint getLastLocation(String sessionId) {
		String ret = jedis.hget(sessionId, "location");
		return (TimedPoint) fromJsonIfNotEmpty(ret, TimedPoint.class);
	}

	@Override @Retrieable
	public String getCheckInStatus(String sessionId) {
		String ret = jedis.hget(sessionId, "checkIn");
		return (String) fromJsonIfNotEmpty(ret, null);
	}



	@Override @Retrieable
	public void storeRoute(StopsRoute route){
		try {
			jedis.setex(route.getRouteId(), ROUTE_TTL, mapper.writeValueAsString(route));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@Override @Retrieable
	public StopsRoute getRoute(String routeId)throws InvalidRouteException{
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
		}
		return null; 
	}

	@Override @Retrieable
	public void setNextSegmentStop(String sessionId, Stop start) {
		try {
			jedis.hset(sessionId+"status", "nextstop", mapper.writeValueAsString(start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override @Retrieable
	public void addSegment(String sessionId, LineSegment ls) {
		try {
			jedis.rpush(sessionId+"route", mapper.writeValueAsString(ls));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Override @Retrieable
	public Stop getNextSegmentStop(String sessionId) {
		return (Stop) fromJsonIfNotEmpty(jedis.hget(sessionId+"status", "nextstop"), Stop.class);
	}

	@Override @Retrieable
	public LineSegment popNextSegment(String sessionId) {
		LineSegment ls = (LineSegment) fromJsonIfNotEmpty(jedis.lpop(sessionId+"route"), LineSegment.class);
		return ls;
	}

	@Override @Retrieable
	public void addPointForNextSegment(String sessionId, TimedPoint p) {
		try {
			jedis.rpush(sessionId+"segment", mapper.writeValueAsString(p));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Override @Retrieable
	public LineSegment getCurrentSegment(String sessionId) {
		LineSegment ls = (LineSegment) fromJsonIfNotEmpty(jedis.hget(sessionId+"status","currls"), LineSegment.class);
		return ls;
	}

	@Override @Retrieable
	public void setCurrentSegment(String sessionId, LineSegment ls) {
		try {
			jedis.hset(sessionId+"status", "currls", mapper.writeValueAsString(ls));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override @Retrieable
	public TimedPoint[] getNextPointsInSegment(String sessionId, int count) {
		List<String> rets = jedis.lrange(sessionId+"segment", 0, count -1);
		int i = 0;
		TimedPoint[] res = new TimedPoint[rets.size()];
		for(String ret:rets){
			res[i] = (TimedPoint) fromJsonIfNotEmpty(ret, TimedPoint.class);
			i++;
		}
		return res;
	}

	@Override @Retrieable
	public void removePointsFromSegment(String sessionId, int index) {
		jedis.ltrim(sessionId+"segment", index, Integer.MAX_VALUE);
		
	}

	@Override @Retrieable
	public Status getStatusOnRoute(String sessionId) {
		String ret = jedis.hget(sessionId, "status");
		return (Status) fromJsonIfNotEmpty(ret, Status.class);
	}

	@Override @Retrieable
	public void updateStatus(String sessionId, Status status) {
		try {
			jedis.hset(sessionId, "status", mapper.writeValueAsString(status));
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}

	@Override @Retrieable
	public void chcekInToRoute(String sessionId, String routeId) {
		jedis.hset(sessionId, "checkIn", routeId);
	}

	@Override @Retrieable
	public void storeDestinationCoordinates(String sessionId, Point dest) {
		try{
			jedis.hset(sessionId, "destination", mapper.writeValueAsString(dest));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override @Retrieable
	public Point getDestinationCoordinates(String sessionId) {
		String ret = jedis.hget(sessionId, "destination");
		return (Point) fromJsonIfNotEmpty(ret, Point.class);
	}
}
