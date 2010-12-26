package com.ibus.tracer;

public interface ISessionManager {

	public abstract String createSession(String clientid);

	public abstract String createSession(String username, String password);

	public abstract Boolean validateSession(String sessionId);

}