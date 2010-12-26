package com.ibus.mapbuilder.db;

import com.ibus.map.Point;

/**
 * Defines the persistency layer interface for the map builder
 * @author Home
 *
 */
public interface IBuilderDB {

	/**
	 * Adds station to the session/redis based storage, creates serializable format of the station
	 * and stores it
	 * @param sessionID the session id
	 * @param point the point
	 * @param ts the ts
	 */
	public void addStation(String sessionID, Point point, long ts);

	/**
	 * Creates recording session, assocoates session id and the lane id
	 * @param sessionID
	 * @param lane
	 * @param submap
	 */
	public void createRecordingSession(String sessionID, String lane, String submap);

	/**
	 * Stores point info in the recording session (redis)
	 * @param sessionID
	 * @param point
	 * @param ts
	 */
	public void addPoint(String sessionID, Point point, long ts);

	/**
	 * flushes data to a persistent storage (simple db/cassandra)
	 * @param sessionID
	 */
	public void flushRoute(String sessionID);

}
