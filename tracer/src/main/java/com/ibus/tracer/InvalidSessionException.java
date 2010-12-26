package com.ibus.tracer;

public class InvalidSessionException extends Exception {
	
	public InvalidSessionException(String routeId) {
		super(routeId);
	}

}
