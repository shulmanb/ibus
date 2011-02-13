package com.ibus.navigation.map.db;

import com.ibus.map.Line;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopDetails;

public interface IMapQueryDB {

	/**
	 * @param laneId a unique id on the submap
	 * @return arrays of stops (point + desc)
	 */
	Stop[] getStationsForLine(String lineId);

	/**
	 * @param laneId a unique id on the submap
	 * @return arrays of points representing the lane
	 */
	Point[] getLinePoints(String lineId);

	/**
	 * @param stationId a unique id in the system
	 * @return
	 */
	StopDetails getStopDetails(String stationId);

	/**
	 * @param submap
	 * @return
	 */
	StopDetails[] getAllStationsInSubmap(String submap);

	/**
	 * @param submap
	 * @param left
	 * @param right
	 * @return
	 */
	StopDetails[] getStationsInArea(String submap, Point left, Point right);

	StopDetails[] getStationsInArea(Point left, Point right);

	void deleteLineById(String lineId);

	Line[] getLinesInSubmap(String submap);


}
