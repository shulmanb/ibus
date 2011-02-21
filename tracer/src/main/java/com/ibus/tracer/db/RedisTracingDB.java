package com.ibus.tracer.db;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import redis.clients.jedis.Jedis;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ibus.connectivity.AbstractRedisStorage;
import com.ibus.map.Point;
import com.ibus.map.TimedPoint;

public class RedisTracingDB extends AbstractRedisStorage implements ITracingDB {
	private ObjectMapper mapper = new ObjectMapper();

	private boolean closePointRecored(Map<String, String> map) {
		// TODO: add implementation, check if any point within a minute range
		// for the same bus exist
		return false;
	}

	private boolean sameBus(Point entryP, long entryTs, Point point, long ts) {
		// distance between points in meters
		double distance = point.distnaceFrom(entryP);
		// time difference in hours
		double time = ((double)(ts - entryTs)) / 1000 / 60 / 60;
		// check if it make sence for a bus to make that distance in a given
		// time
		// assume velocity 120 km/h
		if (distance < time * 120) {
			return true;
		}

		return false;
	}

	private boolean expired(Long oldTime, Long newTime) {
		if ((newTime - oldTime) / 1000 > 5 * 60) {
			// if a point older than 5 minutes
			return true;
		}
		return false;
	}

	public RedisTracingDB(Jedis jedis) {
		super(jedis);
	}

	@Inject
	public RedisTracingDB(@Named("REDIS HOST") String redisHost,
			@Named("REDIS PORT") Integer redisPort) {
		super(redisHost, redisPort);
	}

	@Override
	public void storeBusLocation(String lineId, long ts, Point point) {
		Jedis jedis = getJedis();
		try {
			// retrieve all the entries
			Map<String, String> map = jedis.hgetAll(lineId);
			// add current point
			// point exists within 1 minute range do nothing
			if (closePointRecored(map)) {
				return;
			}
	
			jedis.hset(lineId, Long.toString(ts),
					mapper.writeValueAsString(point));
	
			// clean obsolete values
			// iterate over the map, expire outdated values,
			// remove the previous value of current bus and put the current
			// values
			// the same structure can be changed in parallel by concurrent
			// sessions
			// in that case we will have multiple value for a bus will would be
			// consolidated on next write
			if (map != null) {
				for (Map.Entry<String, String> entry : map.entrySet()) {
					long entryTs = Long.valueOf(entry.getKey());
					if (expired(entryTs, ts)) {
						jedis.hdel(lineId, entry.getKey());
						continue;
					}
					try {
						Point entryP = mapper.readValue(entry.getValue(),
								Point.class);
						if (sameBus(entryP, entryTs, point, ts)) {
							jedis.hdel(lineId, entry.getKey());
						}
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	
			}
	
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
	
	}

	@Override
	public TimedPoint[] getLineBuses(String lineId) {
		Jedis jedis = getJedis();
		List<TimedPoint> lst = new LinkedList<TimedPoint>();
		try {
			// retrieve all the entries
			Map<String, String> map = jedis.hgetAll(lineId);
			long ts = System.currentTimeMillis();
			if (map != null) {
				for (Map.Entry<String, String> entry : map.entrySet()) {
					long entryTs = Long.valueOf(entry.getKey());
					if (expired(entryTs, ts)) {
						jedis.hdel(lineId, entry.getKey());
						continue;
					}
					try {
						Point entryP = mapper.readValue(entry.getValue(),
								Point.class);
						lst.add(new TimedPoint(entryP.getLongitude(),entryP.getLatitude(),ts));
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	
			}
			return lst.toArray(new TimedPoint[0]);
		} finally {
			returnJedis(jedis);
		}
	}

}
