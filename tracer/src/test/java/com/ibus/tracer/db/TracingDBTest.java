package com.ibus.tracer.db;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.ibus.map.Point;

public class TracingDBTest {

	private Jedis jedis;
	private RedisTracingDB db;

	@Before
	public void setUpBefore() throws Exception {
		jedis = mock(Jedis.class);
		db = new  RedisTracingDB(jedis);
	}

	@Test
	public void testStoreBusLocation_firstPoint() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "{\"longitude\":1.0,\"latitude\":1.0}");
		when(jedis.hgetAll("1")).
			thenReturn(null).
			thenReturn(map);
		
		db.storeBusLocation("1", 1, new Point(1,1));
		verify(jedis).hgetAll("1");
		verify(jedis).hset("1", "1","{\"longitude\":1.0,\"latitude\":1.0}");
		verifyNoMoreInteractions(jedis);		
	}

	@Test
	public void testStoreBusLocation_firstPoint_secondPoint() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "{\"longitude\":1.0,\"latitude\":1.0}");
		when(jedis.hgetAll("1")).
			thenReturn(null).
			thenReturn(map);
		
		db.storeBusLocation("1", 1, new Point(1,1));
		db.storeBusLocation("1", 10000, new Point(1.0001,1.0001));
		verify(jedis).hset("1", "1","{\"longitude\":1.0,\"latitude\":1.0}");
		verify(jedis,times(2)).hgetAll("1");
		verify(jedis).hset("1", "10000","{\"longitude\":1.0001,\"latitude\":1.0001}");
		verify(jedis).hdel("1", "1");
		verifyNoMoreInteractions(jedis);		
		//		db.storeBusLocation("1", 100000, new Point(0,0));
	}
	@Test
	public void testStoreBusLocation_Expire() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "{\"longitude\":1.0,\"latitude\":1.0}");
		when(jedis.hgetAll("1")).
			thenReturn(null).
			thenReturn(map);
		
		db.storeBusLocation("1", 1, new Point(1,1));
		db.storeBusLocation("1", 300000000, new Point(10.0001,10.0001));
		verify(jedis).hset("1", "1","{\"longitude\":1.0,\"latitude\":1.0}");
		verify(jedis,times(2)).hgetAll("1");
		verify(jedis).hset("1", "300000000","{\"longitude\":10.0001,\"latitude\":10.0001}");
		verify(jedis).hdel("1", "1");
		verifyNoMoreInteractions(jedis);		
	}

	@Test
	public void testStoreBusLocation_TwoBusesOnLine() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "{\"longitude\":1.0,\"latitude\":1.0}");
		when(jedis.hgetAll("1")).
			thenReturn(null).
			thenReturn(map);
		
		db.storeBusLocation("1", 1, new Point(1,1));
		db.storeBusLocation("1", 2, new Point(10.0001,10.0001));
		verify(jedis).hset("1", "1","{\"longitude\":1.0,\"latitude\":1.0}");
		verify(jedis,times(2)).hgetAll("1");
		verify(jedis).hset("1", "2","{\"longitude\":10.0001,\"latitude\":10.0001}");
		verifyNoMoreInteractions(jedis);		
	}

}
