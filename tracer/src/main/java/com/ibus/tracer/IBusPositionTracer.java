package com.ibus.tracer;

import com.ibus.map.Point;

/**
 * DEfines Bus position tracing contract
 * @author Home
 *
 */
public interface IBusPositionTracer {
	/**
	 * Records the bus position
	 * @param lineid lineId
	 * @param ts timestamp
	 * @param position the position
	 */
	public void tracPosition(String lineid, Point position);
	
	/**
	 * Retrieves Line Buses
	 * @param lineId
	 * @return
	 */
	public Point[] getLineBuses(String lineId);
}
