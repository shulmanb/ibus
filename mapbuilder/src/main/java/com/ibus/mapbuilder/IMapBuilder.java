package com.ibus.mapbuilder;

import com.ibus.map.*;
public interface IMapBuilder {
	public String initiateRouteRecording(long ts,Point point, String lane, String submap );
	public void addPoint(String sessionId,long ts, Point point);
	public void addStation(String sessionId,long ts, Point point);
	public void finishRouteRecording(String sessionId, long ts, Point point);
}
