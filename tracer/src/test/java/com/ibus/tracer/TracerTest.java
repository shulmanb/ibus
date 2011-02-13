package com.ibus.tracer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.ibus.tracer.db.ISessionDB;
import com.ibus.tracer.db.RedisSessionDB;
public class TracerTest {

	private Jedis jedis;
	private Tracer tr = new Tracer();

	@Before
	public void setUpBefore() throws Exception {
		jedis = mock(Jedis.class);
		ISessionDB sesDb = new  RedisSessionDB(jedis);
		tr.setSesDb(sesDb);
	}

	@Test @Ignore
	public void testStoreLocation() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testCheckInToRoute() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testStoreLocationOnRoute() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testGetLocation() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testGetCheckInStatus() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testStoreTemporaryRoute() {
		fail("Not yet implemented");
	}

}
