package com.ibus.tracer;

import com.google.inject.Inject;
import com.ibus.map.Point;
import com.ibus.map.TimedPoint;
import com.ibus.tracer.db.ITracingDB;

public class BusPositionTracer implements IBusPositionTracer{
	private ITracingDB traceDB;
	
	@Inject
	public void setTracingDB(ITracingDB db){
		this.traceDB = db;
	}
	@Override
	public void tracPosition(String lineid, Point position) {
		traceDB.storeBusLocation(lineid, System.currentTimeMillis(), position);
	}
	@Override
	public Point[] getLineBuses(String lineId) {
		TimedPoint[] locations = traceDB.getLineBuses(lineId);
		//TODO: iterate over points and filter close points (similar bus) returning the latest point
		return locations;
	}

}
