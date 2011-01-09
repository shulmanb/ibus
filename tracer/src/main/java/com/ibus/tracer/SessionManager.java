package com.ibus.tracer;

import java.util.UUID;

import com.google.inject.Inject;
import com.ibus.tracer.db.ISessionDB;
import com.ibus.tracer.db.RedisSessionDB;

/**
 * Manages use sessions sessions 
 * 
 * @author Home
 *
 */
public class SessionManager implements ISessionManager {
	
	ISessionDB sesDb = null;
	
	public SessionManager(){}
	
	public SessionManager(String host, int port){
		this.sesDb = new RedisSessionDB(host, port);
	}
	
	@Inject
	public void setSesDb(ISessionDB sesDb) {
		this.sesDb = sesDb;
	}

	/* (non-Javadoc)
	 * @see com.ibus.tracer.ISessionManager#createSession(java.lang.String)
	 */
	@Override
	public String createSession(String clientid) {
		String sessionId = UUID.randomUUID().toString();
		return sesDb.initiateSession(clientid, sessionId);
	}

	/* (non-Javadoc)
	 * @see com.ibus.tracer.ISessionManager#createSession(java.lang.String, java.lang.String)
	 */
	@Override
	public String createSession(String username, String password) {
		//TODO: check authentication
		return createSession(username);
	}

	/* (non-Javadoc)
	 * @see com.ibus.tracer.ISessionManager#validateSession(java.lang.String)
	 */
	@Override
	public Boolean validateSession(String sessionId){
		boolean isValid = sesDb.pingSession(sessionId);
		return isValid;
	}

}
