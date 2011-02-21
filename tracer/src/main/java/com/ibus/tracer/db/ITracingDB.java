package com.ibus.tracer.db;

import com.ibus.map.Point;
import com.ibus.map.TimedPoint;

public interface ITracingDB {
	public void storeBusLocation(String lineId,long ts, Point point);

	public TimedPoint[] getLineBuses(String lineId);
}
