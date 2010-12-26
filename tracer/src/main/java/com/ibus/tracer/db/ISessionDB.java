package com.ibus.tracer.db;

import com.ibus.map.LineSegment;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopsRoute;
import com.ibus.map.TimedPoint;
import com.ibus.tracer.InvalidRouteException;
import com.ibus.tracer.InvalidSessionException;
import com.ibus.tracer.Status;
import com.ibus.tracer.Status.StatusOnRoute;

/**
 * 
 * Defines interface for a session in memory database
 * @author Home
 *
 */
public interface ISessionDB {

	/**
	 * Stores new session in the db
	 * @param clientid
	 * @param sessionId
	 */
	public void initiateSession(String clientid, String sessionId);

	/**
	 * Checks if the session is still valid and increases its ttl
	 * @param sessionId
	 * @return <code>true</code> if the session is valid, <code>false</code> otherwise
	 */
	public boolean pingSession(String sessionId);

	/**
	 * stores user's current location
	 * @param sessionId
	 * @param point
	 * @return
	 */
	public void storeLocation(String sessionId, TimedPoint point)throws InvalidSessionException;

	/**
	 * Retrieves last known location
	 * @param sessionId
	 * @return the location
	 */
	public TimedPoint getLastLocation(String sessionId)throws InvalidSessionException;

	/**
	 * Retrieve the checkIn status 
	 * @param sessionId
	 * @return <code>null</code> if not checked in, otherwise the line name and the submap
	 */
	public String getCheckInStatus(String sessionId)throws InvalidSessionException;


	/**
	 * Stores route in db as JSON object
	 * @param route
	 * @throws InvalidSessionException
	 */
	public void storeRoute(StopsRoute route)throws InvalidSessionException;

	/**
	 * Retrieves route from a db, and deletes it
	 * @param routeId
	 * @return
	 * @throws InvalidRouteException
	 */
	public StopsRoute getRoute(String routeId) throws InvalidRouteException;

	/**
	 * Stores stop that starts next segment
	 * @param sessionId
	 * @param start
	 */
	public void setNextSegmentStop(String sessionId, Stop start);

	/**
	 * Add next segment for navigation, store for the segment 
	 *  1. number of stops to switch transport
	 *  2. number of stops to destination
	 *  3.estimated time to switch transport
	 *  4.estimated time to destination
	 * @param sessionId
	 * @param ls
	 */
	public void addSegment(String sessionId, LineSegment ls);

	/**
	 * Retrieves the next segment starting station
	 * @param sessionId
	 * @return
	 */
	public Point getNextSegmentStop(String sessionId);

	/**
	 * retrieves next segment, removing it from the list. 
	 * @param sessionId
	 * @return the next segment
	 */
	public LineSegment popNextSegment(String sessionId);


	/**
	 * Retrieves the next segment
	 * @param sessionId
	 * @return the segment
	 */
	public LineSegment getCurrentSegment(String sessionId);

	/**
	 * @param sessionId
	 * @param ls
	 */
	public void setCurrentSegment(String sessionId, LineSegment ls);
	
	/**
	 * @param sessionId
	 * @param p
	 */
	public void addPointForNextSegment(String sessionId, TimedPoint p);

	/**
	 * Retrieves next points in segment
	 * 
	 * @param sessionId
	 * @param count number of points
	 * @return
	 */
	public TimedPoint[] getNextPointsInSegment(String sessionId, int count);

	public void removePointsFromSegment(String sessionId, int index);

	public Status getStatusOnRoute(String sessionId);

	public void updateStatus(String sessionId, Status status);

	public void chcekInToRoute(String session, String routeId);

	public void storeDestinationCoordinates(String sessionId, Point dest);

	public Point getDestinationCoordinates(String sessionId);
}
