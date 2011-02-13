package com.ibus.tracer;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.ibus.tracer.db.ISessionDB;
import com.ibus.tracer.db.RedisSessionDB;

public class SessionManagerIntegTest {
	static Jedis jedis;
	static SessionManager sm;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sm = new SessionManager();
		jedis = new Jedis("localhost", 6379);
		jedis.select(1);
		ISessionDB sesDb = new RedisSessionDB(jedis);
		sm.setSesDb(sesDb);
	}

	@Before
	public void setUpTest() throws Exception {
		jedis.flushDB();
	}
	
	@Test
	public void testCreateSession_Anonimous(){
		String ses = sm.createSession("test");
		String ret = jedis.hget(ses, "client");
		assertEquals(true, jedis.exists(ses));
		assertEquals(true, jedis.hexists(ses,"client"));
		assertEquals("test", ret);
	}

	@Test
	public void testCreateSession(){
		String ses = sm.createSession("test","");
		String ret = jedis.hget(ses, "client");
		assertEquals(true, jedis.exists(ses));
		assertEquals(true, jedis.hexists(ses,"client"));
		assertEquals("test", ret);
	}
	
	@Test
	public void testPingSession(){
		String ses = sm.createSession("test","");
		assertTrue(sm.validateSession(ses));
		assertTrue(sm.validateSession(ses));
		assertFalse(sm.validateSession("11"));
	}
}
