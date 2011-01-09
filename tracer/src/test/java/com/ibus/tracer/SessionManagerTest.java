package com.ibus.tracer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.ibus.tracer.db.ISessionDB;
import com.ibus.tracer.db.RedisSessionDB;

public class SessionManagerTest {

	private Jedis jedis;
	private SessionManager sm = new SessionManager();

	@Before
	public void setUpBefore() throws Exception {
		jedis = mock(Jedis.class);
		ISessionDB sesDb = new  RedisSessionDB(jedis);
		sm.setSesDb(sesDb);
	}

	@Test
	public void testCreateSessionAnonimous() {
		when(jedis.get("client")).thenReturn(null);
		String ses = sm.createSession("client");
		verify(jedis).get("client");
		verify(jedis).hset(ses, "client", "client");
		verify(jedis).set("client", ses);
		verify(jedis).expire(ses,RedisSessionDB.SESSION_TTL);
		verify(jedis).expire("client",RedisSessionDB.SESSION_TTL);
		verifyNoMoreInteractions(jedis);
	}

	@Test
	public void testCreateSessionClientExists() {
		when(jedis.get("client")).thenReturn("ses");
		when(jedis.exists("ses")).thenReturn(1);
		when(jedis.hget("ses","client")).thenReturn("client");
		String ses = sm.createSession("client");
		assertEquals("ses", ses);
		verify(jedis).get("client");
		verify(jedis).hget("ses", "client");
		verify(jedis).expire("ses",RedisSessionDB.SESSION_TTL);
		verify(jedis).expire("client",RedisSessionDB.SESSION_TTL);
		verify(jedis).exists("ses");
		verifyNoMoreInteractions(jedis);
	}

	
	@Test
	public void testCreateSession() {
		when(jedis.get("client")).thenReturn(null);
		String ses = sm.createSession("client", "password");
		verify(jedis).get("client");
		verify(jedis).hset(ses, "client", "client");
		verify(jedis).set("client", ses);
		verify(jedis).expire(ses,RedisSessionDB.SESSION_TTL);
		verify(jedis).expire("client",RedisSessionDB.SESSION_TTL);
		verifyNoMoreInteractions(jedis);
	}

	@Test
	public void testValidateSessionNotExists() {
		when(jedis.exists("ses")).thenReturn(0);
		Boolean res = sm.validateSession("ses");
		assertFalse(res);
		verify(jedis).exists("ses");
		verifyNoMoreInteractions(jedis);
	}

	@Test
	public void testValidateSessionExists() {
		when(jedis.exists("ses")).thenReturn(1);
		when(jedis.hget("ses","client")).thenReturn("client");
		Boolean res = sm.validateSession("ses");
		assertTrue(res);
		verify(jedis).exists("ses");
		verify(jedis).hget("ses", "client");
		verify(jedis).expire("ses",RedisSessionDB.SESSION_TTL);
		verify(jedis).expire("client",RedisSessionDB.SESSION_TTL);
		verifyNoMoreInteractions(jedis);
	}

}
