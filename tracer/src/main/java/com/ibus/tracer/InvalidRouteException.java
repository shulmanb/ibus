package com.ibus.tracer;

public class InvalidRouteException extends Exception {
	
	public InvalidRouteException(String sessionId) {
		super(sessionId);
	}

}
