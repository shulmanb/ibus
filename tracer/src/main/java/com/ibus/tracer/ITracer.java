package com.ibus.tracer;

import com.ibus.map.Point;
import com.ibus.map.StopsRoute;
import com.ibus.map.TimedPoint;

public interface ITracer {

	/**
	 * Retrieves the check in status
	 * @param sessionId
	 * @return null if not checked in, otherwise line name and submap
	 */
	public String getCheckInStatus(String sessionId) throws InvalidSessionException;

	/**
	 * Retrieves the last reported location
	 * @param sessionId
	 * @return the location if exists, null otherwise
	 */
	public TimedPoint getLocation(String sessionId)throws InvalidSessionException;

	/**
	 * stores location on route
	 * @param session
	 * @param point
	 * @return the status of the user on route, including time remains to switch and to dest
	 * @throws UserOffRouteException 
	 */
	public Status storeLocationOnRoute(String session, TimedPoint point)throws InvalidSessionException, UserOffRouteException;

	/**
	 * Check in the user to route
	 * @param session
	 * @param routeId
	 * @throws InvalidRouteException 
	 */
	public void checkInToRoute(String session, String routeId)throws InvalidSessionException, InvalidRouteException;

	/**
	 * Stores location in the session
	 * @param sessionId
	 * @param point
	 */
	public void storeLocation(String sessionId, TimedPoint point)throws InvalidSessionException;
	
	/**
	 * Stores the route
	 * @param route
	 * @throws InvalidSessionException
	 */
	public void storeTemporaryRoute(StopsRoute route)throws InvalidSessionException;


}
